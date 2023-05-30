package com.dopamines.backend.plan.controller;

import com.dopamines.backend.account.entity.Account;
import com.dopamines.backend.account.service.UserService;
import com.dopamines.backend.plan.dto.EndPlanDto;
import com.dopamines.backend.plan.dto.OngoingPlanDto;
import com.dopamines.backend.plan.dto.PlanListDto;
import com.dopamines.backend.plan.dto.GameMoneyDto;
import com.dopamines.backend.plan.entity.Plan;
import com.dopamines.backend.plan.service.ParticipantService;
import com.dopamines.backend.plan.service.PlanService;
import com.dopamines.backend.wallet.dto.SettlementResultDto;
import com.dopamines.backend.wallet.service.WalletService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/plan")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Api(value = "plan", description = "약속을 관리하는 컨트롤러입니다.")
public class PlanController {

    private final PlanService planService;

    private final UserService userService;

    private final ParticipantService participantService;

    private final WalletService walletService;


    @PostMapping("/create")
    @Operation(summary = "약속 생성 api 입니다.", description = "약속 정보를 입력하여 약속을 생성합니다. 약속이 생성되면 PlanId을 Long 타입으로 반환합니다.<br>"  +
            "participantIds는 유저id를 문자열(예시 : 1,2,3,4,5)로 입력합니다. planDate는 'yyyy-MM-dd', planTime는 'HH:mm:ss' 형태의 문자열로 입력합니다.<br>" +
            "location은 주소를 입력합니다. latitude는 위도, longitude는 경도를 나타냅니다. 실수로 입력해주세요.")
    public ResponseEntity<Long> createPlan(
            HttpServletRequest request,
            @RequestParam("title") String title,
            @RequestParam("planDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate planDate,
            @RequestParam("planTime") @DateTimeFormat(pattern = "HH:mm:ss") LocalTime planTime,
            @RequestParam("location") String location,
            @RequestParam("address") String address,
            @RequestParam("latitude") Double latitude, // 위도
            @RequestParam("longitude") Double longitude, // 경도
            @RequestParam("cost") Integer cost,
            @RequestParam(value = "participantIds", required = false) String participantIdsStr // 입력값: 1,2,3,4
    ) {
        // 헤더에서 유저 이메일 가져옴
        String userEmail = request.getRemoteUser();

        if (planService.getTimeMinutesDifference(planDate, planTime) >= 0) {
            log.warn("생성 실패: 약속 시간은 현재 시간 이후 시간으로 생성할 수 있습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Long planId = planService.createPlan(userEmail, title, planDate, planTime, location, address, latitude, longitude, cost, participantIdsStr);
        log.info("약속이 생성되었습니다.");
        return ResponseEntity.ok(planId);
    }


    @PutMapping("/update")
    @Operation(summary = "약속 수정 api 입니다.", description = "PlanId를 입력하여 약속 정보를 불러와 약속 정보을 수정합니다. 방장만 수정 가능합니다.<br>" +
            "participantIds는 유저id를 문자열(예시 : 1,2,3,4,5)로 입력합니다. planDate는 'yyyy-MM-dd', planTime는 'HH:mm:ss' 형태의 문자열로 입력합니다.<br>" +
            "location은 주소를 입력합니다. latitude는 위도, longitude는 경도를 나타냅니다. 실수로 입력해주세요.")
    public ResponseEntity<Void> updatePlan(
            HttpServletRequest request,
            @RequestParam("planId") Long planId,
            @RequestParam("title") String title,
            @RequestParam("planDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate planDate,
            @RequestParam("planTime") @DateTimeFormat(pattern = "HH:mm:ss") LocalTime planTime,
            @RequestParam("location") String location,
            @RequestParam("address") String address,
            @RequestParam("latitude") Double latitude, // 위도
            @RequestParam("longitude") Double longitude, // 경도
            @RequestParam("cost") Integer cost,
            @RequestParam(value = "participantIds", required = false) String participantIdsStr // 입력값: 1,2,3,4
    ) {

        try {

            Plan plan = planService.getPlanById(planId);

            String userEmail = request.getRemoteUser();

            Account account = userService.findByEmail(userEmail);

            // 약속 상태 변경
            planService.updatePlanStatus(plan);

            // 방장 여부 확인
            if (!participantService.findIsHostByPlanAndUser(plan, account)) {
                log.warn("방장이 아닙니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            // 약속 상태 확인
            if (plan.getState() > 1) {
                log.warn("약속이 진행 중 이므로 수정할 수 없습니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            if (planService.getTimeMinutesDifference(planDate, planTime) >= 0) {
                log.warn("수정 실패: 약속 시간은 현재 시간 이후 시간으로 변경할 수 있습니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            planService.updatePlanAndParticipant(plan, title, planDate, planTime, location, address, latitude, longitude, cost, participantIdsStr);
            log.info("planId {}이고 title '{}'인 약속이 방장 {}에 의해 수정되었습니다.", planId, title, account.getNickname());

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("수정 실패: planId {}는 존재하지 않는 planId 입니다.", planId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            // 기타 예외 발생 시, HttpStatus.INTERNAL_SERVER_ERROR 반환
            log.error("수정 실패: planId {}", planId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }


    @DeleteMapping("/delete")
    @Operation(summary = "약속 삭제 api 입니다.", description = "PlanId를 입력하여 약속 정보를 삭제합니다. 방장만 삭제 가능합니다.")
    public ResponseEntity<Void> deletePlan(
            HttpServletRequest request,
            @RequestParam("planId") Long planId
    ){

        try {

            Plan plan = planService.getPlanById(planId);

            String userEmail = request.getRemoteUser();
            Account account = userService.findByEmail(userEmail);

            // 약속 상태 변경
            planService.updatePlanStatus(plan);

            // 방장 여부 확인
            if (!participantService.findIsHostByPlanAndUser(plan, account)) {
                log.warn("방장이 아닙니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            // 약속 상태 확인
            if (plan.getState() > 1) {
                log.warn("약속이 진행 중 이므로 삭제할 수 없습니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            planService.deletePlan(plan);

            log.info("PlanId {}인 약속이 방장 {}에 의해 삭제되었습니다.", planId, account.getNickname());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            // PlanId가 잘못된 경우, HttpStatus.BAD_REQUEST 반환
            log.error("삭제 실패: planId {}는 존재하지 않는 planId 입니다.", planId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            // 기타 예외 발생 시, HttpStatus.INTERNAL_SERVER_ERROR 반환
            log.error("삭제 실패: planId {}", planId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/detail")
    @Operation(summary = "진행 중인 약속 상세 정보를 불러오는 api 입니다.", description = "PlanId를 입력하여 약속 상세 정보를 불러옵니다.<br>" +
            "designation은 칭호이며 0 보통, 1 일찍, 2 지각을 나타냅니다. status는 0 기본, 1 위치공유(30분 전~약속시간), 2 게임 활성화(약속시간~1시간 후), 3 약속 종료(1시간 이후)을 나타냅니다.<br/>" +
            "diffDay가 음수이면 아직 시작하지 않은 약속, 양수이면 지난 약속 입니다.")
    public ResponseEntity<OngoingPlanDto> planDetail(@RequestParam("planId") Long planId) {

        OngoingPlanDto planDto = planService.getPlanDetail(planId);
        return new ResponseEntity<>(planDto, HttpStatus.OK);
    }


    @GetMapping("/list")
    @Operation(summary = "약속 리스트를 불러오는 api 입니다.", description = "userId와 planDate를 입력하여 유저의 해당 날짜 약속 리스트를 불러옵니다.<br>" +
            "status는 0 기본, 1 위치공유(30분 전~약속시간), 2 게임 활성화(약속시간~1시간 후), 3 약속 종료(1시간 이후)을 나타냅니다.<br>" +
            "diff시간이 음수이면 아직 시작하지 않은 약속, 양수이면 지난 약속 입니다.")
    public ResponseEntity<List<PlanListDto>> planList(
            HttpServletRequest request,
            @RequestParam("planDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate planDate) {

        String userEmail = request.getRemoteUser();
        List<PlanListDto> planListDto = planService.getPlanList(userEmail, planDate);
        return new ResponseEntity<>(planListDto, HttpStatus.OK);
    }


    @GetMapping("/endDetail")
    @Operation(summary = "완료된 약속 상세 정보를 불러오는 api 입니다.", description = "PlanId를 입력하여 약속 상세 정보를 불러옵니다.<br>" +
            "designation은 칭호이며 0 보통, 1 일찍, 2 지각을 나타냅니다. status는 0 기본, 1 위치공유(30분 전~약속시간), 2 게임 활성화(약속시간~1시간 후), 3 약속 종료(1시간 이후)을 나타냅니다.")
    public ResponseEntity<EndPlanDto> endPlanDetail(
            HttpServletRequest request,
            @RequestParam("planId") Long planId
    ) {
        String userEmail = request.getRemoteUser();
        EndPlanDto endPlanDto = planService.getEndPlanDetail(planId, userEmail);
        return new ResponseEntity<>(endPlanDto, HttpStatus.OK);
    }


    @GetMapping("/gameMoney")
    @Operation(summary = "약속 지각자들의 총 지각비 정보를 불러오는 api 입니다.", description = "PlanId를 입력하여 약속 지각비 총 금액을 불러옵니다.<br>" +
            "도착한 약속시간을 기준으로 지각여부를 판단합니다. 약속 시간 이후에 요청해주세요.")
    public ResponseEntity<GameMoneyDto> getGameMoney(
            HttpServletRequest request,
            @RequestParam("planId") Long planId
    ) {

        try {
            GameMoneyDto gameMoneyDto = planService.getGameMoney(planId);
            return ResponseEntity.ok(gameMoneyDto);
        } catch (IllegalArgumentException e) {
            log.error("API 호출 중 예외 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("API 호출 중 예외 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @PostMapping("/settle")
    @Operation(summary = "약속 참가자들의 지각비를 정산하는 api 입니다.", description = "PlanId를 입력하여 해당 약속의 정산을 수행합니다.<br>" +
            "참가자 지갑에 지각비가 부족할 경우 정산에 실패하고 잔액이 부족한 참가자 목록을 보여줍니다. 정산에 성공할 경우 전체 참가자 목록을 보여줍니다.")
    public ResponseEntity<SettlementResultDto> settleMoney(
            HttpServletRequest request,
            @RequestParam("planId") Long planId
    ) {

        try {
            String userEmail = request.getRemoteUser();
            SettlementResultDto settlementResultDto = walletService.settleMoney(userEmail, planId);
            return ResponseEntity.ok(settlementResultDto);
        } catch (IllegalArgumentException e) {
            log.error("API 호출 중 예외 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("API 호출 중 예외 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

//    @GetMapping("/planIdList")
//    @Operation(summary = "참가한 약속 리스트를 불러오는 api 입니다.", description = "리스트로 반환합니다.")
//    public ResponseEntity<List<Long>> getPlanIdList(HttpServletRequest request){
//
//        try {
//            String userEmail = request.getRemoteUser();
//            List<Long> myPlanIds = planService.getMyPlanIds(userEmail);
//            return ResponseEntity.ok(myPlanIds);
//        } catch (IllegalArgumentException e) {
//            log.error("API 호출 중 예외 발생: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        } catch (Exception e) {
//            log.error("API 호출 중 예외 발생: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//
//    }

}