package com.dopamines.backend.plan.service;

import com.dopamines.backend.plan.dto.EndPlanDto;
import com.dopamines.backend.plan.dto.GameMoneyDto;
import com.dopamines.backend.plan.dto.OngoingPlanDto;
import com.dopamines.backend.plan.dto.PlanListDto;
import com.dopamines.backend.plan.entity.Plan;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface PlanService {

    // 약속 생성
    Long createPlan(String userEmail, String title, LocalDate planDate, LocalTime planTime, String location, String address,Double latitude, Double longitude, Integer find, String participantIdsStr);

    // 약속 수정
    void updatePlanAndParticipant(Plan plan, String title, LocalDate planDate, LocalTime planTime, String location, String address, Double latitude, Double longitude, Integer find, String newParticipantIdsStr);

    // 약속 삭제
    void deletePlan(Plan plan);

    // 약속 리스트
    List<Long> getMyPlanIds(String userEmail);

    // 진행 중인 약속 상세 정보
    OngoingPlanDto getPlanDetail(Long planId);

    // 진행 중인 약속 상세 정보
    EndPlanDto getEndPlanDetail(Long planId, String userEmail);

    // 해당 날짜의 약속 리스트
    List<PlanListDto> getPlanList(String userEmail, LocalDate planDate);

    // 게임으로 보낼 총 지각비를 계산합니다.
    GameMoneyDto getGameMoney(Long planId);

    // 정산 여부 확인
    boolean checkSettle(Long planId);

    // 모든 참가자가 도착한 경우 true 반환환
    boolean isAllMemberArrived(Long planId);

    // 내 약속이니?
    boolean isMyPlan(String userEmail, Long planId);
    //////////////// 중복 ///////////////////

    // 약속 상태 변경 함수
    void updatePlanStatus(Plan plan);

    // 약속 유효성 검사
    Plan getPlanById(Long planId);

    // 약속 시간 유효성 검사
    // 시간
    long getTimeHoursDifference(LocalDate planDate, LocalTime planTime);
    // 분
    long getTimeMinutesDifference(LocalDate planDate, LocalTime planTime);

    // 시간 차이 맘대로 가져오기 (자세한 시간 차이를 계산)
    Duration getTimeDifference(LocalDate planDate, LocalTime planTime);

    // 3시간 전후 약속 리스트
    List<Plan> getPlansWithinThreeHours();
}
