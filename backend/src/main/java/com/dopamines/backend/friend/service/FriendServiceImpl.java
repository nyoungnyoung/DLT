package com.dopamines.backend.friend.service;

import com.dopamines.backend.account.entity.Account;
import com.dopamines.backend.account.repository.AccountRepository;
import com.dopamines.backend.friend.dto.FriendResponseDto;
import com.dopamines.backend.friend.entity.Friend;
import com.dopamines.backend.friend.entity.WaitingFriend;
import com.dopamines.backend.friend.repository.FriendRepository;
import com.dopamines.backend.friend.repository.WaitingFriendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class FriendServiceImpl implements FriendService{
    private final AccountRepository accountRepository;
    private final FriendRepository friendRepository;
    private final WaitingFriendRepository waitingFriendRepository;
    private FriendService friendService;


    @Override
    public Map<String, List<FriendResponseDto>> getFriendList(String email){
        Account account = getAccountByEmail(email);
        Map<String, List<FriendResponseDto>> res = new HashMap<String, List<FriendResponseDto>>();

        // 받은 요청
        List<FriendResponseDto> waitingValue = new ArrayList<FriendResponseDto>();
        List<WaitingFriend> waitings = waitingFriendRepository.findAllByfriendId(account.getAccountId());
        for(WaitingFriend waitingFriend : waitings) {
            Account friendAccount = getAccountById(waitingFriend.getFriendId());
            FriendResponseDto temp = toFriendResponseDto(2, friendAccount);
            waitingValue.add(temp);
        }
        res.put("waitings", waitingValue);

        // 친구
        List<FriendResponseDto> friendValue = new ArrayList<FriendResponseDto>();
        List<Friend> friends = friendRepository.findAllByAccount_AccountId(account.getAccountId());
        for(Friend friend : friends) {
            Account friendAccount = getAccountById(friend.getFriendId());
            FriendResponseDto temp = toFriendResponseDto(3, friendAccount);
            friendValue.add(temp);
        }
        res.put("friends", friendValue);

        return res;
    }


    @Override
    public FriendResponseDto addFriend(String email, Long friendId) {
        Account myAccount = getAccountByEmail(email);
        Account friendAccount = getAccountById(friendId);

        if(myAccount == null || friendAccount == null){
            throw new IllegalArgumentException("해당 email이나 firendId가 없습니다. ;(");
        }


        validateNotSelf(myAccount, friendAccount);
        validateNotAlreadyFriends(myAccount, friendAccount);


        log.info("addFriend에서 찍는 myAccount: " + myAccount.getEmail());
        log.info("addFriend에서 찍는 friendAccount: " + friendAccount.getEmail());

        // 이미 친구 신청한 상태인지 확인
        Optional<WaitingFriend> waitingFriendOpt= waitingFriendRepository.findByFriendIdAndAccount_AccountId(friendId, myAccount.getAccountId());
        if(!waitingFriendOpt.isEmpty()){
            // 했으면 에러
            throw new RuntimeException("이미 신청했습니다.");
        }

        try {

        // 하지 않았으면 친구 신청
        WaitingFriend waitingFriend = WaitingFriend.toBuild(myAccount, friendAccount);

        waitingFriendRepository.save(waitingFriend);

        // FriendResponseDto 반환
            return toFriendResponseDto(1, friendAccount);

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        throw new RuntimeException("FriendResponseDto를 생성할 수 없습니다.");
    }

    @Override
    public FriendResponseDto acceptFriend(String email, Long friendId){
        Account myAccount = getAccountByEmail(email);
        Account friendAccount = getAccountById(friendId);

        if(myAccount == null || friendAccount == null){
            throw new IllegalArgumentException("해당 email이나 firendId가 없습니다. ;(");
        }

        validateNotSelf(myAccount, friendAccount);
        validateNotAlreadyFriends(myAccount, friendAccount);

        // 친구 신청이 왔는지 확인
        validateFriendRequest(myAccount, friendAccount);

        // 왔으면
        // 양쪽에 friend entity 생성
        Friend friend1 = Friend.toBuild(myAccount, friendAccount);
        friendRepository.save(friend1);
        Friend friend2 = Friend.toBuild(friendAccount, myAccount);
        friendRepository.save(friend2);

        // waiting에서 삭제
        Optional<WaitingFriend> waitingFriend = waitingFriendRepository.findByFriendIdAndAccount_AccountId(myAccount.getAccountId(), friendId);
        waitingFriendRepository.delete(waitingFriend.get());

        // dto 반환
        return toFriendResponseDto(3, friendAccount);
    }


    @Override
    public FriendResponseDto denyFriend(String email, Long friendId){
        Account myAccount = getAccountByEmail(email);
        Account friendAccount = getAccountById(friendId);

        if(myAccount == null || friendAccount == null){
            throw new IllegalArgumentException("해당 email이나 firendId가 없습니다. ;(");
        }

        validateNotSelf(myAccount, friendAccount);

        // 요청이 있으면 삭제
        waitingFriendRepository.delete(validateFriendRequest(myAccount, friendAccount));


        // dto 반환
        return toFriendResponseDto(4, friendAccount);
    };

    @Override
    public FriendResponseDto deleteFriend(String email, Long friendId){
        Account myAccount = getAccountByEmail(email);
        Account friendAccount = getAccountById(friendId);

        if(myAccount == null || friendAccount == null){
            throw new IllegalArgumentException("해당 email이나 firendId가 없습니다. ;(");
        }

        validateNotSelf(myAccount, friendAccount);

        // 서로 친구인지 확인
        Optional<Friend> friendOpt1 = friendRepository.findByFriendIdAndAccount_AccountId(friendId, myAccount.getAccountId());
        Optional<Friend> friendOpt2 = friendRepository.findByFriendIdAndAccount_AccountId(myAccount.getAccountId(), friendId);
        // 친구가 아니면
        if(friendOpt1.isEmpty() || friendOpt2.isEmpty()) {
            throw new RuntimeException("친구가 아닙니다.");
        }
        // 친구면 삭제
        friendRepository.delete(friendOpt1.get());
        friendRepository.delete(friendOpt2.get());


        // dto 반환
        return toFriendResponseDto(4, friendAccount);
    };

    private Account getAccountByEmail(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 email이 없습니다. ;("));
    }

    private Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 id가 없습니다. ;("));
    }
    private FriendResponseDto toFriendResponseDto (int status, Account friendAccount){
        FriendResponseDto friendResponseDto = new FriendResponseDto();

        friendResponseDto.setStatus(status);
        friendResponseDto.setProfile(friendAccount.getProfile());
        friendResponseDto.setNickname(friendAccount.getNickname());
        friendResponseDto.setProfileMessage(friendAccount.getProfileMessage());
        friendResponseDto.setFriendId(friendAccount.getAccountId());

        return friendResponseDto;
    }

    private void validateNotSelf(Account myAccount, Account friendAccount) {
        if (myAccount.getAccountId().equals(friendAccount.getAccountId())) {
            throw new RuntimeException("나는 세상에서 제일 소중한 친구입니다:)");
        }
    }

    private void validateNotAlreadyFriends(Account myAccount, Account friendAccount) {
        List<Friend> myFriends = friendRepository.findAllByFriendIdAndAccount_AccountId(friendAccount.getAccountId(), myAccount.getAccountId());
        for(Friend myFriend : myFriends) {
            if(myFriend.getFriendId().equals(friendAccount.getAccountId()))
                throw new RuntimeException("이미 친구입니다.");
        }
    }

    private WaitingFriend validateFriendRequest(Account myAccount, Account friendAccount){
        // 요청이 있는지 확인
        Optional<WaitingFriend> waitingFriendOpt = waitingFriendRepository.findByFriendIdAndAccount_AccountId(myAccount.getAccountId(), friendAccount.getAccountId());
        // 요청이 없으면
        if(waitingFriendOpt.isEmpty()) {
            throw new RuntimeException("해당 친구 요청이 없습니다.");
        } else {
            return waitingFriendOpt.get();
        }
    }
}
