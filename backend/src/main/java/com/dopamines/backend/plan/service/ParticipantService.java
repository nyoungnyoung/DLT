package com.dopamines.backend.plan.service;

import com.dopamines.backend.account.entity.Account;
import com.dopamines.backend.plan.dto.GameResultMoneyDto;
import com.dopamines.backend.plan.entity.Plan;

public interface ParticipantService {

    // 참가자 생성
    void createParticipant(Account account, Plan plan, boolean host);

    // 참가자 수정
    void updateParticipant(Plan plan, String newParticipantIdsStr);

    // 방장 인지 확인
    boolean findIsHostByPlanAndUser(Plan plan, Account account);

    // 게임 결과에 따른 거래 금액을 저장합니다.
    GameResultMoneyDto registerGetMoney(String userEmail, Long planId, Integer getGameMoney);

}
