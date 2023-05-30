package com.dopamines.backend.plan.service;

import com.dopamines.backend.account.entity.Account;
import com.dopamines.backend.account.repository.AccountRepository;
import com.dopamines.backend.account.service.UserService;
import com.dopamines.backend.fcm.entity.FCM;
import com.dopamines.backend.fcm.repository.FirebaseCloudMessageRepository;
import com.dopamines.backend.game.GameManager;
import com.dopamines.backend.plan.dto.*;
import com.dopamines.backend.plan.entity.Participant;
import com.dopamines.backend.plan.entity.Plan;
import com.dopamines.backend.plan.repository.ParticipantRepository;
import com.dopamines.backend.plan.repository.PlanRepository;
import com.dopamines.backend.review.entity.Photo;
import com.dopamines.backend.review.repository.PhotoRepository;
import com.dopamines.backend.review.service.PhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final UserService userService;

    private final PlanRepository planRepository;

    private final ParticipantRepository participantRepository;

    private final ParticipantService participantService;

    private final AccountRepository accountRepository;

    private final GameManager gameManager;

    private final FirebaseCloudMessageRepository fcmRepository;

    private final PhotoRepository photoRepository;


    // thyme 지급
    private void giveThyme(String  email, int thyme, Plan plan) {
        Optional<Account> account = accountRepository.findByEmail(email);
        Optional<Participant> participant = participantRepository.findByPlanAndAccount(plan, account.get());

        if (participant.isEmpty()) {
            throw new IllegalArgumentException("해당 계정이 없습니다.");
        } else {
            log.info("thyme 지급 전 thyme 잔액: " + participant.get().getThyme());

            participant.get().setThyme(participant.get().getThyme() + thyme);

            log.info("thyme 지급 성공! thyme 잔액:" + participant.get().getThyme());

            participantRepository.save(participant.get());
        }
    }


    // 약속 생성
    @Override
    public Long createPlan(String userEmail, String title, LocalDate planDate, LocalTime planTime, String location, String address, Double latitude, Double longitude, Integer cost, String participantIdsStr) {

        Account account = userService.findByEmail(userEmail);

        Plan plan = planRepository.save(
                Plan.builder()
                        .title(title)
                        .planDate(planDate)
                        .planTime(planTime)
                        .location(location)
                        .address(address)
                        .latitude(latitude)
                        .longitude(longitude)
                        .cost(cost)
                        .isSettle(false) /////////////////
                        .build()
        );

        // 방장 참가자로 추가
        participantService.createParticipant(account, plan, true);

        // 방장한테 20 thyme 지급
        giveThyme(userEmail, 20, plan);

        // 참가자 추가
        if (participantIdsStr != null && !participantIdsStr.isEmpty()) {
            String[] participantIds = participantIdsStr.split(",");
            for (String participantId : participantIds) {
                if (Long.valueOf(participantId).equals(account.getAccountId())) {
                    continue;
                }
                Account participant = userService.findByAccountId(Long.valueOf(participantId));
                participantService.createParticipant(participant, plan, false);

                // 참가자한테 10 thyme 지급
                giveThyme(participant.getEmail(), 10, plan);
            }
        }

        return plan.getPlanId();
    }


    // 약속 수정
    @Override
    public void updatePlanAndParticipant(Plan plan, String title, LocalDate planDate, LocalTime planTime, String location, String address, Double latitude, Double longitude, Integer cost, String newParticipantIdsStr) {

        // 참가자 수정
        participantService.updateParticipant(plan, newParticipantIdsStr);

        // 약속 정보를 업데이트
        plan.setTitle(title);
        plan.setPlanDate(planDate);
        plan.setPlanTime(planTime);
        plan.setLocation(location);
        plan.setAddress(address);
        plan.setLatitude(latitude); // 위도
        plan.setLongitude(longitude); // 경도
        plan.setCost(cost);

        planRepository.save(plan);
    }


    // 약속 삭제
    @Override
    public void deletePlan(Plan plan) {
        planRepository.delete(plan);
    }


    // 진행 중인 약속 상세 정보
    @Override
    public OngoingPlanDto getPlanDetail(Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("해당 약속의 약속 정보가 없습니다."));

        // 시간에 따른 약속 상태 변경
        updatePlanStatus(plan);

        boolean isPhoto = photoRepository.findByPlan(plan).isPresent();

        OngoingPlanDto planDto = new OngoingPlanDto();
        planDto.setPlanId(planId);
        planDto.setTitle(plan.getTitle());
        planDto.setPlanDate(plan.getPlanDate());
        planDto.setPlanTime(plan.getPlanTime());
        planDto.setCost(plan.getCost());
        planDto.setLocation(plan.getLocation());
        planDto.setAddress(plan.getAddress());
        planDto.setLatitude(plan.getLatitude());
        planDto.setLongitude(plan.getLongitude());
        planDto.setState(plan.getState());
        planDto.setIsPhoto(isPhoto);

        // D-day 계산
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        long diffDay = ChronoUnit.DAYS.between(plan.getPlanDate(), today);

        // D-day
        planDto.setDiffDay(diffDay);

        // 참가자 리스트 정보
        List<Participant> participants = participantRepository.findByPlan(plan);

        // 참가자 수
        planDto.setParticipantCount(participants.size());

        // 참가자 정보 dto 추가
        List<OngoingParticipantDto> participantDtoList = new ArrayList<>();
        for (Participant participant : participants) {

            // 칭호 : 1 = 일찍 오는 사람, 2 = 늦게 오는 사람, 0: 정시에 오는사람 (누적시간)
            int designation = checkDesignation(participant.getAccount().getAccumulatedTime());

            OngoingParticipantDto participantDto = new OngoingParticipantDto();
            participantDto.setAccountId(participant.getAccount().getAccountId());
            participantDto.setNickname(participant.getAccount().getNickname());
            participantDto.setProfile(participant.getAccount().getProfile());
            participantDto.setIsHost(participant.getIsHost());
            participantDto.setIsArrived(participant.getIsArrived());
            participantDto.setDesignation(designation);
            participantDtoList.add(participantDto);
        }
        planDto.setParticipantList(participantDtoList);

        return planDto;
    }


    // 완료된 중인 약속 상세 정보
    @Override
    public EndPlanDto getEndPlanDetail(Long planId, String userEmail) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("해당 약속의 약속 정보가 없습니다."));

        // 시간에 따른 약속 상태 변경
        updatePlanStatus(plan);

        EndPlanDto endPlanDto = new EndPlanDto();
        endPlanDto.setPlanId(planId);
        endPlanDto.setTitle(plan.getTitle());
        endPlanDto.setPlanDate(plan.getPlanDate());
        endPlanDto.setPlanTime(plan.getPlanTime());
        endPlanDto.setLocation(plan.getLocation());
        endPlanDto.setAddress(plan.getAddress());
        endPlanDto.setLatitude(plan.getLatitude());
        endPlanDto.setLongitude(plan.getLongitude());
        endPlanDto.setCost(plan.getCost());
        endPlanDto.setState(plan.getState());
        endPlanDto.setIsSettle(plan.getIsSettle());

        // 참가자 리스트 정보
        List<Participant> endParticipants = participantRepository.findByPlan(plan);

        // 참가자 정보 dto 추가
        List<EndPlanParticipantDto> endParticipantList = new ArrayList<>();
        for (Participant endParticipant : endParticipants) {

            Account user = endParticipant.getAccount();

            // 칭호 : 1 = 일찍 오는 사람, 2 = 늦게 오는 사람, 0: 정시에 오는사람 (누적시간)
            int designation = checkDesignation(user.getAccumulatedTime());

            // 로그인한 유저 정보는 따로 관리
            if (user.getEmail().equals(userEmail)) {
                // 로그인 된 유저 정보
                MyEndPlanDto myDetail = new MyEndPlanDto();
                myDetail.setAccountId(user.getAccountId());
                myDetail.setNickname(user.getNickname());
                myDetail.setProfile(user.getProfile());
                myDetail.setDesignation(designation);
                myDetail.setArrivalTime(endParticipant.getArrivalTime());
                // 지각한 시각 <= 0: 지각안함, 지각한 시각 > 0 : 지각
                myDetail.setLateTime(endParticipant.getLateTime());
                // 게임에서 획득한 돈 > 0 , 지출된 돈 < 0
                myDetail.setGetMoney(endParticipant.getTransactionMoney());
                endPlanDto.setMyDetail(myDetail);

                continue;
            }

            EndPlanParticipantDto endPlanParticipantDto = new EndPlanParticipantDto();
            endPlanParticipantDto.setAccountId(user.getAccountId());
            endPlanParticipantDto.setNickname(user.getNickname());
            endPlanParticipantDto.setProfile(user.getProfile());
            endPlanParticipantDto.setDesignation(designation);
            // 지각 시간
            endPlanParticipantDto.setLateTime(endParticipant.getLateTime());

            // 상세페이지 fcm 추가 로직
            // 토큰
            Optional<FCM> fcm =  fcmRepository.findByAccount(user);
            if (fcm.isPresent()) {
                endPlanParticipantDto.setDeviceToken(fcm.get().getDeviceToken());
            }
            // 지갑에 돈 충분함?
            endPlanParticipantDto.setPaymentAvailability(endParticipant.getTransactionMoney() >= 0 || user.getTotalWallet() >= Math.abs(endParticipant.getTransactionMoney()));

//            endPlanParticipantDto.setPaymentAvailability(true);
//            if (endParticipant.getTransactionMoney() < 0) {
//                if (user.getTotalWallet() < Math.abs(endParticipant.getTransactionMoney())) {
//                    endPlanParticipantDto.setPaymentAvailability(false);
//                }
//            }

            // 리스트에 저장
            endParticipantList.add(endPlanParticipantDto);
        }
        endPlanDto.setEndPlanParticipantDto(endParticipantList);

        return endPlanDto;
    }


    // 약속 리스트
    @Override
    public List<PlanListDto> getPlanList(String userEmail, LocalDate planDate) {

        Account account = userService.findByEmail(userEmail);

        // 해당 account가 참여하고 있는 participant 목록 가져오기
        List<Participant> participants = participantRepository.findByAccount(account);

        List<PlanListDto> stateOneTwoList = new ArrayList<>();
        List<PlanListDto> otherList = new ArrayList<>();

//        List<PlanListDto> planHomeListDto = new ArrayList<>();
        for (Participant participant : participants) {
            // 참여한 약속
            Plan plan = participant.getPlan();

            // 입력한 날짜와 같은 약속이면
            if (plan.getPlanDate().equals(planDate)) {

                // 시간에 따른 약속 상태 변경
                updatePlanStatus(plan);

                // 해당 약속 정보 저장
                PlanListDto planListDto = new PlanListDto();
                planListDto.setPlanId(plan.getPlanId());
                planListDto.setTitle(plan.getTitle());
                planListDto.setPlanDate(planDate);
                planListDto.setPlanTime(plan.getPlanTime());
                planListDto.setLocation(plan.getLocation());
                planListDto.setAddress(plan.getAddress());
                planListDto.setLatitude(plan.getLatitude());
                planListDto.setLongitude(plan.getLongitude());
                planListDto.setState(plan.getState());
                // 남은 시간 (-1이면 약속 시간 1시간 전)
                planListDto.setDiffHours(getTimeHoursDifference(plan.getPlanDate(),plan.getPlanTime()));
                // 남은 분 (-40이면 약속 시간 40분 전)
                planListDto.setDiffMinutes(getTimeMinutesDifference(plan.getPlanDate(),plan.getPlanTime()));

                // 해당 약속의 참가자 리스트 정보
                List<Participant> participantList = participantRepository.findByPlan(plan);
                // 해당 약속의 참가자 수 저장
                planListDto.setParticipantCount(participantList.size());

                // 참가자 정보 dto 추가
                List<PlanListParticipantDto> planHomeListParticipantDto = new ArrayList<>();
                for (Participant user : participantList) {
                    PlanListParticipantDto planListParticipantDto = new PlanListParticipantDto();
                    planListParticipantDto.setAccountId(user.getAccount().getAccountId());
                    planListParticipantDto.setProfile(user.getAccount().getProfile());
                    planHomeListParticipantDto.add(planListParticipantDto);
                }
                // 해당 약속의 참가자 저장
                planListDto.setParticipantList(planHomeListParticipantDto);

                // 해당 날짜의 약속 리스트에 현재 약속 정보 저장
//                planHomeListDto.add(planListDto);
                if (plan.getState() == 1 || plan.getState() == 2) {
                    stateOneTwoList.add(planListDto);
                } else {
                    otherList.add(planListDto);
                }
            }
        }
        // 약속 시간을 기준으로 정렬
//        planHomeListDto.sort(Comparator.comparing(PlanListDto::getPlanTime));

        // 상태가 1 또는 2인 리스트를 시간 순으로 정렬
        stateOneTwoList.sort(Comparator.comparing(PlanListDto::getPlanTime));
        // 나머지 리스트를 시간 순으로 정렬
        otherList.sort(Comparator.comparing(PlanListDto::getPlanTime));

        // 상태가 1 또는 2인 리스트를 앞에 추가한 뒤, 나머지 리스트를 추가하여 최종 리스트 생성
        List<PlanListDto> sortedList = new ArrayList<>();
        sortedList.addAll(stateOneTwoList);
        sortedList.addAll(otherList);

        return sortedList;
//        return planHomeListDto;
    }

    @Override
    public List<Long> getMyPlanIds(String userEmail){
        Account account = accountRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저 정보가 없습니다."));
        List<Participant> participants = participantRepository.findByAccount(account);

        List<Long> myPlanIds = new ArrayList<>();
        for (Participant user : participants) {
            myPlanIds.add(user.getPlan().getPlanId());
        }
        return myPlanIds;
    }


    // 해당 약속의 지각비 총 금액 계산
    public GameMoneyDto getGameMoney(Long planId){

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("해당 약속의 약속 정보가 없습니다."));

        LocalDateTime currentTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime appointmentTime = LocalDateTime.of(plan.getPlanDate(), plan.getPlanTime());

        if (currentTime.isBefore(appointmentTime)) {
            throw new IllegalArgumentException("약속 시간 이후에 API를 요청해주세요.");
        }

        // 해당 약속의 participant 목록 가져오기
        List<Participant> participants = participantRepository.findByPlan(plan);
        int laterCount = 0;
        for (Participant user : participants) {
            // 지각시간이 null 이거나 0보다 크면 지각으로 간주
            if (user.getLateTime() == null || user.getLateTime() > 0) {
                laterCount += 1;
            }
        }
        GameMoneyDto gameMoneyDto = new GameMoneyDto();
        gameMoneyDto.setPlanId(planId);
        gameMoneyDto.setTotalPayment(laterCount*plan.getCost());
        return gameMoneyDto;
    }


    // 정산확인
    @Override
    public boolean checkSettle(Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("해당 약속 정보가 없습니다."));

        return plan.getIsSettle();
    }


    /////////////////////////////// 중복 사용 함수 ////////////////////////////////////////////

    // 약속 시간 유효성 검사
    // 약속 시간 차이
    // 약속 시간 이전 이면 음수, 약속 시간 이후면 양수
    // 시간
    @Override
    public long getTimeHoursDifference(LocalDate planDate, LocalTime planTime) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime planDateTime = LocalDateTime.of(planDate, planTime);

        return ChronoUnit.HOURS.between(planDateTime, now);
    }
    // 분
    @Override
    public long getTimeMinutesDifference(LocalDate planDate, LocalTime planTime) {
        LocalDateTime planDateTime = LocalDateTime.of(planDate, planTime);
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        return ChronoUnit.MINUTES.between(planDateTime, now);
    }

    @Override
    // 자세한 시간 차이를 계산 : 음수면 시간 지남, 양수면 시간 안됌
    public Duration getTimeDifference(LocalDate planDate, LocalTime planTime) {
        LocalDateTime planDateTime = LocalDateTime.of(planDate, planTime);
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        return Duration.between(planDateTime, now);
    }

    // 약속 유효성 검사
    @Override
    public Plan getPlanById(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 planId 입니다."));
    }

    // 약속 상태 변경 함수
    @Override
    public void updatePlanStatus(Plan plan) {
        LocalDateTime planDateTime = LocalDateTime.of(plan.getPlanDate(), plan.getPlanTime());
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        long diffMinutes = ChronoUnit.MINUTES.between(now, planDateTime);

        if (diffMinutes > 30) {
            plan.setState(0); // 기본 상태
        } else if (30 >=diffMinutes && diffMinutes >= 0 && plan.getState() <= 0) {
            plan.setState(1); // 위치공유 (30분 전 ~ 약속시간)

        } else if (0 > diffMinutes && diffMinutes >= -60 && plan.getState() <= 1) {
            gameManager.setGameMoney(plan.getPlanId(), getGameMoney(plan.getPlanId()).getTotalPayment());

            // 지각 하지 않은 참가자에게 50 thyme 지급
            List<Participant> participants = participantRepository.findByPlan(plan);

            for(Participant participant : participants) {

                if(participant.getLateTime() != null && participant.getLateTime() >=0){

                    Optional<Account> account = accountRepository.findByEmail(participant.getAccount().getEmail());

                    if(account.isEmpty()) {
                        log.info("해당 계정 정보가 없습니다.");
                    } else {
                        giveThyme(account.get().getEmail(), 50, plan);
                    }
                }
            }

            plan.setState(2); // 게임 활성화 (약속시간 ~ 1시간 후)
        } else if(diffMinutes < -60 && plan.getState() <= 2){
            plan.setState(3); // 약속 종료 (1시간 이후)

            // participant의 thyme을 account에 업뎃
            List<Participant> participants = participantRepository.findByPlan(plan);

            for(Participant participant : participants) {
                Optional<Account> account = accountRepository.findByEmail(participant.getAccount().getEmail());
                if(account.isEmpty()) {
                    log.info("해당 계정 정보가 없습니다.");
                } else {
                    account.get().setThyme(account.get().getThyme() + participant.getThyme());
                    accountRepository.save(account.get());
                    log.info("participant의 thyme을 account에 업뎃함");
                }

            }
        }

        planRepository.save(plan);
    }

    // 칭호 반환 (designation은 칭호이며 0 보통, 1 일찍, 2 지각을 나타냅니다.)
    public int checkDesignation(int arrivalTime) {
        int designation = 0;

        if (arrivalTime < 0) {
            // 일찍 오는 사람
            designation = 1;
        } else if (arrivalTime > 0) {
            // 늦게 오는 사람
            designation = 2;
        }

        return designation;
    }

    // 내 약속이니?
    @Override
    public boolean isMyPlan(String userEmail, Long planId) {
        Account account = userService.findByEmail(userEmail);
        Plan plan = getPlanById(planId);
        Optional<Participant> participant = participantRepository.findByPlanAndAccount(plan, account);

        return participant.isPresent();
    }

    /////////////////////////////// 소켓 ////////////////////////////////////////

    // 모든 참가자가 도착한 경우 true 반환환
    @Override
    public boolean isAllMemberArrived(Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 약속 정보가 없습니다."));

        List<Participant> participants = participantRepository.findByPlan(plan);
        // 현재 참여중인 멤버들의 도착 여부를 확인합니다.
        // 모두 도착했으면 true, 한사람이라도 도착하지 않았다면 false를 반환합니다.
        for (Participant participant : participants) {
            if (!participant.getIsArrived()) {
                return false;
            }
        }
        return true;
    }

    //////////////////////////////// fcm //////////////////////////////
    // 3시간 전후 약속 알림
    @Override
    public List<Plan> getPlansWithinThreeHours() {
        LocalDateTime currentDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalTime threeHoursBefore = currentDateTime.minusHours(3).toLocalTime();
        LocalTime threeHoursAfter = currentDateTime.plusHours(3).toLocalTime();

//        List<Plan> plans = planRepository.findByPlanDateAndPlanTimeBetween(currentDateTime.toLocalDate(), threeHoursBefore.toLocalTime(), threeHoursAfter.toLocalTime());

        List<Plan> plans = planRepository.findByPlanTimeBetween(threeHoursBefore, threeHoursAfter);

        return plans;
    }

}
