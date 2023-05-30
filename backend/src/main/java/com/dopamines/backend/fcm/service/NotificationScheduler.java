package com.dopamines.backend.fcm.service;

import com.dopamines.backend.plan.entity.Plan;
import com.dopamines.backend.plan.repository.PlanRepository;
import com.dopamines.backend.review.repository.PhotoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.*;
import java.util.List;

@Slf4j
@EnableScheduling
@Service
public class NotificationScheduler {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private FirebaseCloudMessageService fcmService;

//    @Scheduled(cron = "0/5 * * * * ?") // 20초 마다 실행
    @Scheduled(cron = "0 * * * * *") // 매 분마다 실행
    public void pushPlanAlarm() throws IOException {
//        System.out.println("스케줄러가 매 분 마다 동작해요!");

        LocalDateTime currentDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDate yesterday = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1);
        LocalDate tomorrow = LocalDate.now(ZoneId.of("Asia/Seoul")).plusDays(1);
        List<Plan> plans = planRepository.findByPlanDateBetween(yesterday, tomorrow);

        // 약속 전체 검색
        // List<Plan> plans = planRepository.findAll();

        for (Plan plan : plans) {

            // System.out.println(plan.getTitle() + " : " + plan.getPlanDate());

            LocalDateTime planDateTime = LocalDateTime.of(plan.getPlanDate(), plan.getPlanTime());
            Duration duration = Duration.between(currentDateTime, planDateTime);
            // 시간 차이 분으로 환산
            long minutesDifference = duration.toMinutes();

            // 알림 조건에 따른 처리 로직 (예시)
            if (minutesDifference == -60) {
                fcmService.sendTopicMessageTo(
                        String.valueOf(plan.getPlanId()),
                        "약속 1시간 전",
                        "[" + plan.getTitle() + "] 1시간 후에 예정되어 있습니다.",
                        String.valueOf(plan.getPlanId()),
                        "toDetailPlanFragment"
                );

                log.info(plan.getTitle() + " : 약속 1 시간 전입니다.");

            } else if (minutesDifference == -30) {
                fcmService.sendTopicMessageTo(
                        String.valueOf(plan.getPlanId()),
                        "약속 30분 전",
                        "[" + plan.getTitle() + "] 참여자들의 위치 공유가 시작되었습니다.",
                        String.valueOf(plan.getPlanId()),
                        "toDetailPlanFragment"
                );

                log.info(plan.getTitle() + " : 약속 30분 전입니다. 위치 공유가 시작됩니다.");

            } else if (minutesDifference == 0) {
                fcmService.sendTopicMessageTo(
                        String.valueOf(plan.getPlanId()),
                        "약속 시작",
                        "[" + plan.getTitle() + "] 게임에 입장하여 지각비를 획득하세요.",
                        String.valueOf(plan.getPlanId()),
                        "toDetailPlanFragment"
                );

                log.info(plan.getTitle() + " : 약속 시간입니다. 게임에 입장할 수 있습니다.");

            } else if (minutesDifference == 60 && photoRepository.findByPlan(plan).isEmpty()) {
                fcmService.sendTopicMessageTo(
                        String.valueOf(plan.getPlanId()),
                        "약속 기록",
                        "[" + plan.getTitle() + "] 사진으로 추억을 남겨보세요.",
                        String.valueOf(plan.getPlanId()),
                        "toDetailPlanFragment"
                );

                log.info(plan.getTitle() + " : 약속 1시간 지났습니다. 약속 사진을 찍어주세요.");

            } else if (minutesDifference == 120 && photoRepository.findByPlan(plan).isEmpty()) {
                fcmService.sendTopicMessageTo(
                        String.valueOf(plan.getPlanId()),
                        "약속 기록",
                        "[" + plan.getTitle() + "] 사진으로 추억을 남겨보세요.",
                        String.valueOf(plan.getPlanId()),
                        "toDetailPlanFragment"
                );

                log.info(plan.getTitle(), " : 약속 2시간 지났습니다. 아직 약속 사진을 남기지 않았다면 사진을 찍어주세요.");
            }
        }
    }
}
