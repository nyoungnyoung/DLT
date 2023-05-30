package com.dopamines.backend.account.service;


import com.dopamines.backend.account.dto.MyPageDto;
import com.dopamines.backend.account.dto.NicknameProfileDto;
import com.dopamines.backend.account.dto.SearchResponseDto;
import com.dopamines.backend.account.entity.Account;
import com.dopamines.backend.account.repository.AccountRepository;
import com.dopamines.backend.plan.entity.Participant;
import com.dopamines.backend.plan.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final ParticipantRepository participantRepository;

    @Override

    public Account editNickname(String email, String nickname) {
        log.info("AccountServiceImpl의 editNickname에서 찍는 nickname: " + nickname);

        // 닉네임 중복확인
        validateDuplicateNickname(nickname);

        Optional<Account> optional = accountRepository.findByEmail(email);
        Account account = null;

        if (optional.isEmpty()) {
            account = new Account();
            log.info("AccountServiceImpl의 editNickname에서");
        } else {
            account = optional.get();
            account.setNickname(nickname);
            accountRepository.save(account);
        }

        return account;
    }

    private void validateDuplicateNickname(String nickname) {

        if (accountRepository.existsByNickname(nickname)) {
            throw new RuntimeException("이미 존재하는 nickname입니다.");
        }
    }

    //    @Override
    public Account editProfileMessage(String email, String profileMessage) {
        log.info("AccountServiceImpl의 에서 찍는 profileMessage: " + profileMessage);

        Optional<Account> optional = accountRepository.findByEmail(email);
        Account account = null;

        if (optional.isEmpty()) {
            account = new Account();
            log.info("AccountServiceImpl의 profileMessage에서");

        } else {
            account = optional.get();
            account.setProfileMessage(profileMessage);
            accountRepository.save(account);
        }

        return account;
    }

    @Override
    public void deleteAccount(String email) {
        log.info("AccountServiceImpl의 에서 찍는 email: " + email);

        Optional<Account> optional = accountRepository.findByEmail(email);
        Account account = null;

        if (optional.isEmpty()) {
            account = new Account();
            log.info("AccountServiceImpl의 deleteAccount에서");

        } else {
            account = optional.get();
            account.setDeleted(true);
            account.setNickname(null);
            account.setProfile(null);
            account.setRefreshToken(null);
            account.setProfileMessage(null);
            account.setKakaoId(null);
//            account.setEmail(null);

            accountRepository.save(account);
        }
    }

    @Override
    public ArrayList<SearchResponseDto> searchNickname(String keyword) {
        List<Account> accounts = accountRepository.findByNicknameContaining(keyword);

        ArrayList<SearchResponseDto> result = new ArrayList<SearchResponseDto>();

        for (Account account : accounts) {
            log.info("AccountServiceImpl: " + account);
            SearchResponseDto searchResponseDto = new SearchResponseDto();

            searchResponseDto.setAccountId(account.getAccountId());
            searchResponseDto.setNickname(account.getNickname());
            searchResponseDto.setProfile(account.getProfile());
            searchResponseDto.setProfileMessage(account.getProfileMessage());
            result.add(searchResponseDto);

        }

        return result;
    }

    @Override
    public NicknameProfileDto getNicknameProfile(Long accountId) {
        NicknameProfileDto nicknameProfileDto = new NicknameProfileDto();
        Optional<Account> optional = accountRepository.findById(accountId);
        Account account = null;
        if(optional.isEmpty()){
            log.info("optional.isEmpty()");
            return null;
        } else {
            account = optional.get();
            nicknameProfileDto.setNickname(account.getNickname());
            nicknameProfileDto.setProfile(account.getProfile());
            return nicknameProfileDto;
        }
    }


    @Override
    public MyPageDto getMyInfo(String userEmail) {
        Account account = accountRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        List<Participant> participants = participantRepository.findByAccount(account);

        List<Participant> arrivedParticipants = new ArrayList<>();

        for (Participant participant : participants) {
            if (participant.getIsArrived()) {
                arrivedParticipants.add(participant);
            }
        }

        int planCount = arrivedParticipants.size();

        int lateCount = calculateLateCount(arrivedParticipants);
        int latenessRate = calculateLatenessRate(planCount, lateCount);

        MyPageDto myPageDto = new MyPageDto();
        myPageDto.setAccountId(account.getAccountId());
        myPageDto.setNickname(account.getNickname());
        myPageDto.setProfile(account.getProfile());
        myPageDto.setAverageArrivalTime(planCount > 0 ? account.getAccumulatedTime() / planCount : 0);
        myPageDto.setLatenessRate(latenessRate);
        myPageDto.setTotalCost(account.getTotalIn() + account.getTotalOut());

        return myPageDto;

    }

    // 지각자 수
    private int calculateLateCount(List<Participant> participants) {
        int lateCount = 0;
        for (Participant participant : participants) {
            if (participant.getLateTime() > 0) {
                lateCount++;
            }
        }
        return lateCount;
    }

    // 지각 비율
    private int calculateLatenessRate(int planCount, int lateCount) {
        if (lateCount > 0) {
            return lateCount * 100 / planCount;
        } else {
            return 0;
        }
    }

}