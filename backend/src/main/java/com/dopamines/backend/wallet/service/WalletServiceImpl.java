package com.dopamines.backend.wallet.service;

import com.dopamines.backend.account.entity.Account;
import com.dopamines.backend.account.repository.AccountRepository;
import com.dopamines.backend.fcm.entity.FCM;
import com.dopamines.backend.fcm.repository.FirebaseCloudMessageRepository;
import com.dopamines.backend.plan.entity.Participant;
import com.dopamines.backend.plan.entity.Plan;
import com.dopamines.backend.plan.repository.ParticipantRepository;
import com.dopamines.backend.plan.repository.PlanRepository;
import com.dopamines.backend.wallet.dto.SettlementDto;
import com.dopamines.backend.wallet.dto.SettlementResultDto;
import com.dopamines.backend.wallet.dto.WalletDetailDto;
import com.dopamines.backend.wallet.dto.WalletDto;
import com.dopamines.backend.wallet.entity.Wallet;
import com.dopamines.backend.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final AccountRepository accountRepository;

    private final PlanRepository planRepository;

    private final ParticipantRepository participantRepository;

    private final WalletRepository walletRepository;

    private final FirebaseCloudMessageRepository fcmRepository;


    // 정산 가능한지 확인 (각 참가자의 지갑 금액이 정산해야하는 금액보다 많은 지 확인) 적은 사람이 있으면 dto로 보내줌
    private List<SettlementDto> isSettle(List<Participant> participants) {
        List<SettlementDto> settlementFailure = new ArrayList<>();

        for (Participant participant : participants) {

            // transactionMoney가 null인 경우 에러 반환
//            if (participant.getTransactionMoney() == null) {
//                throw new IllegalArgumentException(participant.getAccount().getEmail() + " 님의 게임결과 transactionMoney가 null입니다. 게임 결과를 먼저 입력해주세요.");
//            }

            if (participant.getTransactionMoney() < 0) {
                if (participant.getAccount().getTotalWallet() < Math.abs(participant.getTransactionMoney())) {
                    SettlementDto settlementDto = new SettlementDto();
                    settlementDto.setAccountId(participant.getAccount().getAccountId());
                    settlementDto.setNickName(participant.getAccount().getNickname());
                    settlementDto.setPaymentAmount(participant.getTransactionMoney());
                    settlementFailure.add(settlementDto);
                }
            }
        }
        return settlementFailure;
    }


    // 정산하기
    @Override
    public SettlementResultDto settleMoney(String userEmail, Long planId) {

        Account account = accountRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원 정보가 없습니다."));

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("해당 약속 정보가 없습니다."));


        // 약속 참가자들의 wallet 금액이 정산해야하는 금액 이상으로 가지고 있는지 체크
        List<Participant> participants = participantRepository.findByPlan(plan);
        List<SettlementDto> poorParticipants = isSettle(participants);


        if (poorParticipants.isEmpty()) {
            log.info("planId : {}, title : {} 약속의 정산이 시작되었습니다.", planId, plan.getTitle());
            // 정산 성공
            List<SettlementDto> settlementSuccess = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

            // 정산 성공으로 변경
            plan.setIsSettle(true);
            planRepository.save(plan);

            for (Participant participant : participants) {

                Account user = participant.getAccount();

                // 지갑 목록 생성
                Wallet wallet = new Wallet();
                wallet.setAccount(user);
                wallet.setPlan(plan);
                wallet.setMoney(participant.getTransactionMoney());
                wallet.setTotalMoney(user.getTotalWallet() + participant.getTransactionMoney());
                wallet.setTransactionDate(now.toLocalDate());
                wallet.setTransactionTime(now.toLocalTime());

                if (participant.getTransactionMoney() < 0) {

                    // 잃은 지각비
                    wallet.setType(3);

                    // account 누적 지출 지각비
                    user.setTotalOut(user.getTotalOut() + participant.getTransactionMoney());

                } else {
                    // 얻은 지각비
                    wallet.setType(2);

                    // account 누적 획득 지각비
                    user.setTotalIn(user.getTotalIn() + participant.getTransactionMoney());

                }
                walletRepository.save(wallet);
                // 지갑 전체 금액
                user.setTotalWallet(user.getTotalWallet() + participant.getTransactionMoney());
                accountRepository.save(user);

                Optional<FCM> fcm =  fcmRepository.findByAccount(participant.getAccount());

                SettlementDto settlementDto = new SettlementDto();
                settlementDto.setAccountId(user.getAccountId());
                settlementDto.setNickName(user.getNickname());
                settlementDto.setPaymentAmount(participant.getTransactionMoney());

                if (fcm.isPresent()) {
                    settlementDto.setDeviceToken(fcm.get().getDeviceToken());
                }

                settlementSuccess.add(settlementDto);

            }

            log.info("planId : {}, title : {} 약속의 정산이 완료되었습니다.", planId, plan.getTitle());
            return new SettlementResultDto(true, settlementSuccess);
        } else {
            // 정산 실패
            log.info("planId : {}, title : {} 약속 정산 실패: 일부 참가자의 지갑 금액이 부족합니다.", planId, plan.getTitle());
            return new SettlementResultDto(false, poorParticipants);
        }
    }

    @Override
    public WalletDto getWalletDetails(String email) {

        List<Wallet> wallets = walletRepository.findAllByAccount_Email(email);

        // 일자별로 매핑
        Map<LocalDate, List<WalletDetailDto>> res = new HashMap<LocalDate, List<WalletDetailDto>>();

        for (Wallet wallet: wallets) {
            List<WalletDetailDto> walletDetailDtos = new ArrayList<WalletDetailDto>();

            WalletDetailDto walletDetailDto = new WalletDetailDto();
            walletDetailDto.setMoney(wallet.getMoney());
            walletDetailDto.setType(wallet.getType());
            walletDetailDto.setTransactionTime(wallet.getTransactionTime());
            walletDetailDto.setTotal(wallet.getTotalMoney());
            walletDetailDto.setReceipt(wallet.getReceipt());

            if (wallet.getPlan() == null){
                if(wallet.getType() == 0){
                    walletDetailDto.setTitle(wallet.getMethod());
                }else if (wallet.getType() == 1) {
                    walletDetailDto.setTitle("출금");
                }else {
                    walletDetailDto.setTitle(null);
                }
            } else{
                walletDetailDto.setTitle(wallet.getPlan().getTitle());
            }

            walletDetailDtos.add(walletDetailDto);
            LocalDate transactionDate = wallet.getTransactionDate();

            if(res.containsKey(transactionDate)) {
                res.get(transactionDate).addAll(walletDetailDtos);
            } else {

                res.put(transactionDate, walletDetailDtos);
            }


        }
        WalletDto walletDto;
        Optional<Account> account = accountRepository.findByEmail(email);

        if (account.isEmpty()){
            throw new RuntimeException("계정 정보가 없습니다.");
        } else {
            return new WalletDto(account.get().getTotalWallet(), res);
        }
    }

    @Override
    public void chargeWallet(String email, int money, String method, LocalDate trasactionDate, LocalTime transactionTime, String receipt) {
        Optional<Account> account = accountRepository.findByEmail(email);
        if(account.isPresent()) {
            // Wallet 기록 남기기
            Wallet wallet = new Wallet();

            wallet.setAccount(account.get());
            wallet.setPlan(null);
            wallet.setMoney(money);
            wallet.setTransactionDate(trasactionDate);
            wallet.setTransactionTime(transactionTime);
            wallet.setType(0);
            wallet.setTotalMoney(account.get().getTotalWallet() + money);
            wallet.setMethod(method);
            wallet.setReceipt(receipt);
            walletRepository.save(wallet);

            // Account의 totalWallet 업뎃
            int totalWallet = account.get().getTotalWallet();
            account.get().setTotalWallet(totalWallet + money);
            accountRepository.save(account.get());
        } else {
            throw new RuntimeException("충전에 실패했습니다.");
        }
    }

    @Override
    public void withdrawWallet(String email, int money){
        Optional<Account> account = accountRepository.findByEmail(email);
        if(account.isEmpty()){
            throw new RuntimeException("해당 계정을 찾을 수 없습니다.");
        }

        int totalWallet = account.get().getTotalWallet();

        // money가 잔액보다 클 때
        if(totalWallet < money) {
            throw new RuntimeException("잔액이 부족하여 출금할 수 없습니다.");
        }
        // Account 정보 업뎃
        account.get().setTotalWallet(totalWallet-money);
        accountRepository.save(account.get());
        // Wallet 생성
        Wallet wallet = new Wallet();
        wallet.setType(1);
        wallet.setTransactionTime(LocalTime.now(ZoneId.of("Asia/Seoul")));
        wallet.setTransactionDate(LocalDate.now(ZoneId.of("Asia/Seoul")));
        wallet.setAccount(account.get());
        wallet.setMoney(-money);
        wallet.setTotalMoney(account.get().getTotalWallet());

        walletRepository.save(wallet);

    };
}
