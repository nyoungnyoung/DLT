package com.dopamines.backend.plan.service;

import com.dopamines.backend.account.entity.Account;
import com.dopamines.backend.account.repository.AccountRepository;
import com.dopamines.backend.game.GameManager;
import com.dopamines.backend.plan.dto.GameResultMoneyDto;
import com.dopamines.backend.plan.entity.Participant;
import com.dopamines.backend.plan.entity.Plan;
import com.dopamines.backend.plan.repository.ParticipantRepository;
import com.dopamines.backend.plan.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {

    private final ParticipantRepository participantRepository;

    private final AccountRepository accountRepository;

    private final PlanRepository planRepository;

    private final GameManager gameManager;


    // 참가자 생성
    @Override
    public void createParticipant(Account account, Plan plan, boolean host) {
        Participant participant = Participant.builder()
                .account(account)
                .plan(plan)
                .isArrived(false)
                .isHost(host)
                .build();
        participantRepository.save(participant);
        log.info(account.getEmail() + " 님이 참가되었습니다");
    }


    // 참가자 수정
    @Override
    public void updateParticipant(Plan plan, String newParticipantIdsStr) {

        // 이전 참가자 목록 조회
        List<Participant> oldParticipants  = participantRepository.findByPlan(plan);

        // 이전 참가자들의 ID 집합
        Set<Long> oldParticipantIds = oldParticipants.stream()
                .map(participant -> participant.getAccount().getAccountId())
                .collect(Collectors.toSet());

        // 새로운 참가자 ID 목록 파싱
        List<Long> newParticipantIds = new ArrayList<>();
        if (newParticipantIdsStr != null && !newParticipantIdsStr.isEmpty()) {
            newParticipantIds = Arrays.stream(newParticipantIdsStr.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
        }

        // 새로 추가해야 할 참가자 리스트 생성
        List<Participant> participantsToAdd = new ArrayList<>();
        for (Long newParticipantId : newParticipantIds) {
            // 기존 참가자가 아니라면 추가
            if (!oldParticipantIds.contains(newParticipantId)) {
                // 새로운 참가자 객체 생성
                Account newUser = accountRepository.findById(newParticipantId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다.: " + newParticipantId));
                Participant newParticipant = new Participant();
                newParticipant.setPlan(plan);
                newParticipant.setAccount(newUser);
                newParticipant.setIsArrived(false);
                newParticipant.setIsHost(false);
                participantsToAdd.add(newParticipant);
                log.info("참가자 추가: " + newUser.getEmail());
            }
        }

        // 제거해야 할 참가자 리스트 생성
        List<Participant> participantsToRemove = new ArrayList<>();
        for (Participant oldParticipant : oldParticipants) {
            // 새로운 참가자 목록에 없는 참가자이고 방장이 아닐 경우 삭제 대상에 추가
            if (!newParticipantIds.contains(oldParticipant.getAccount().getAccountId())
                    && !oldParticipant.getIsHost()) {
                participantsToRemove.add(oldParticipant);
                log.info("참가자 삭제: " + oldParticipant.getAccount().getEmail());
            }

        }

        // 제거 대상 삭제, 추가 대상 추가
        participantRepository.deleteAll(participantsToRemove);
        participantRepository.saveAll(participantsToAdd);
    }


    // 게임으로 획득한 돈을 참가자의 거래금액에 등록하고 회원계정의 누적금액으로 업데이트합니다.
    @Override
    public GameResultMoneyDto registerGetMoney(String userEmail, Long planId, Integer getGameMoney) {
        int balance = gameManager.getGameMoney(planId);
        // 약속 정보 가져오기
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("해당 약속의 약속 정보가 없습니다."));

        // 회원 정보 가져오기
        Account account = accountRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원의 정보가 없습니다."));

        // 약속에 대한 참가자 정보 가져오기
        Participant participant = participantRepository.findByPlanAndAccount(plan,account)
                .orElseThrow(() -> new IllegalArgumentException("해당 약속에 참가자의 정보가 없습니다."));

        List<Participant> participantList = participantRepository.findAllByPlan(plan);
        participantList.sort((o1, o2) -> o2.getTransactionMoney() - o1.getTransactionMoney());
        int rank = participantList.indexOf(participant) + 1;
        // 참가자 수
//        int countParticipant = participantRepository.countByPlan(plan);
        int countParticipant = participantList.size();

        int quotient = 0; // 몫
        int remainder = 0; // 나머지

        // 잔액과 참가자 수가 존재할 때 n빵 계산
        if (balance > 0 && countParticipant > 0) {
            quotient = balance / countParticipant; // n빵한 금액
            remainder = balance % countParticipant; // 나머지는 방장에게 줌
        }

        // 최종 거래 금액 계산
        // 일찍 온 사람이면 획득한 금액과 n빵한 금액을 모두 얻는다.
        int finalAmount = getGameMoney + quotient;
        // 나머지가 있으면 방장에게 준다.
        if (participant.getIsHost()){
            finalAmount += remainder;
        }

        // 지각자면 최종 획득한 금액에서 지각비 뺀다. (음수가 된다.)
        if (participant.getLateTime() == null || participant.getLateTime() > 0) {
            finalAmount -=  plan.getCost();
        }

        ///////////////////////////////////////////////
        // 최종 거래 금액 저장
        participant.setTransactionMoney(finalAmount);
        participantRepository.save(participant);

        /////////////////////////////////////////////////////////
        // 최종 거래 금액이 음수이면 account에 totalOut에 저장
        // 최종 거래 금액이 양수이면 account에 totalIn에 저장
        if (finalAmount < 0) {
            account.setTotalOut(account.getTotalOut() + finalAmount);
        } else {
            account.setTotalIn(account.getTotalIn() + finalAmount);
        }

        // 약속 끝 처리(state를 3으로)
        if(plan.getState() == 2) {
            plan.setState(3);
            planRepository.save(plan);
        }


        // 결과 dto 생성
        GameResultMoneyDto gameResultMoneyDto = new GameResultMoneyDto();
        gameResultMoneyDto.setPlanId(planId);
        gameResultMoneyDto.setAccountId(account.getAccountId());
        gameResultMoneyDto.setPlanCost(plan.getCost());
        gameResultMoneyDto.setIsLate(participant.getLateTime() == null || participant.getLateTime()>0);
        gameResultMoneyDto.setGetGameMoney(getGameMoney);
        gameResultMoneyDto.setFinalAmount(finalAmount);
        gameResultMoneyDto.setParticipantCount(countParticipant);
        gameResultMoneyDto.setRank(rank);
        gameResultMoneyDto.setThyme(participant.getThyme());

        if (participant.getIsHost()){
            gameResultMoneyDto.setGetBalance(quotient+remainder);
        } else {
            gameResultMoneyDto.setGetBalance(quotient);
        }

        return gameResultMoneyDto;
    }


    //////////////////////////// 중복 ////////////////////////////

    // 방장 인지 확인
    @Override
    public boolean findIsHostByPlanAndUser(Plan plan, Account account) {
        Participant participant = participantRepository.findByPlanAndAccount(plan, account)
                .orElseThrow(() -> new IllegalArgumentException("참가자가 아닙니다."));

        return participant.getIsHost();
    }

}
