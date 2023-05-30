package com.dopamines.backend.webSocket.service;

import com.dopamines.backend.account.entity.Account;
import com.dopamines.backend.account.repository.AccountRepository;
import com.dopamines.backend.plan.entity.Participant;
import com.dopamines.backend.plan.entity.Plan;
import com.dopamines.backend.plan.repository.ParticipantRepository;
import com.dopamines.backend.plan.repository.PlanRepository;
import com.dopamines.backend.plan.service.PlanService;
import com.dopamines.backend.webSocket.dto.CreateRoomDto;
import com.dopamines.backend.webSocket.dto.MessageDto;
import com.dopamines.backend.webSocket.dto.PlanRoomDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PositionService {

    private final PlanService planService;

    private final ParticipantRepository participantRepository;

    private final PlanRepository planRepository;

    private final AccountRepository accountRepository;

    private final ObjectMapper objectMapper;
    private Map<String, PlanRoomDto> planRooms;


    @PostConstruct
    private void init() {
        planRooms = new LinkedHashMap<>();
    }


    public List<PlanRoomDto> findAllRoom() {
        return new ArrayList<>(planRooms.values());
    }


    public PlanRoomDto findRoomById(String roomId) {
        return planRooms.get(roomId);
    }


    public CreateRoomDto createRoom(String planId) {

        if (planRooms.containsKey(planId)) {
            // 이미 생성된 방이면
            log.info("planId {} 는 이미 존재하는 방입니다.", planId);
            CreateRoomDto createRoomDto = new CreateRoomDto();
            createRoomDto.setRoomId(planId);
            createRoomDto.setState("exist");
            return createRoomDto;
        }

        PlanRoomDto planRoom = PlanRoomDto.builder()
                .roomId(planId)
                .build();
        planRooms.put(planId, planRoom);

        CreateRoomDto createRoomDto = new CreateRoomDto();
        createRoomDto.setRoomId(planId);
        createRoomDto.setState("create");

        return createRoomDto;
    }


    // 메시지 보내기
    public <T> void sendMessage(WebSocketSession session, T payload) {
        if (session.isOpen()) {
            try {
                String jsonPayload = objectMapper.writeValueAsString(payload);
                session.sendMessage(new TextMessage(jsonPayload));
            } catch (IOException e) {
                log.error("Error sending message: {}", e.getMessage(), e);
            } catch (Exception e) {
                log.error("Unexpected error while sending message: {}", e.getMessage(), e);
            }
        } else {
            log.warn("Cannot send message to a closed WebSocket session");
        }
    }
//    public <T> void sendMessage(WebSocketSession session, T message) {
//        try {
//            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
//        } catch (IOException e) {
//            log.error("Error sending message: {}", e.getMessage(), e);
//        } catch (Exception e) {
//            log.error("Unexpected error while sending message: {}", e.getMessage(), e);
//        }
//    }


    // 도착하면 도착상태, 도착시간, 지각시간 업데이트
    // 입장하면 도착상태 false로 변환
    @Transactional
    public void updateIsArrived(Long planId, Long accountId, Boolean isArrived) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 약속 입니다. : Invalid Plan ID"));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다. : User not found"));

        Participant participant = participantRepository.findByPlanAndAccount(plan, account)
                .orElseThrow(() -> new IllegalArgumentException("약속 참가자가 아닙니다. : Participant not found"));

        ////////////// 도착상태 변화 ////////////////
        participant.setIsArrived(isArrived);

        // 도착했으면 시간저장
        if (isArrived) {
            LocalTime arrivalTime = LocalTime.now(ZoneId.of("Asia/Seoul"));
            ////////////// 도착 시간 저장 /////////////
            participant.setArrivalTime(arrivalTime);
            // 지각 시간 계산
//            Duration duration = Duration.between(plan.getPlanTime(),arrivalTime);
//            long minutesBetween = duration.toMinutes();
            long minutesBetween = planService.getTimeMinutesDifference(plan.getPlanDate(),plan.getPlanTime());
            ///////////// 지각 시간 저장 ///////////////////////////
            participant.setLateTime(minutesBetween);
            //////////// 누적 지각 시간 저장 ////////////////////////
            account.setAccumulatedTime(account.getAccumulatedTime() + (int) minutesBetween);
        }
        // 변경사항 저장
        participantRepository.save(participant);
    }

    // 방 삭제
    public void closeRoom(String roomId) {
        PlanRoomDto room = planRooms.get(roomId);
        if (room != null) {
            for (WebSocketSession session : room.getSessions()) {
                try {
                    session.close();
                } catch (IOException e) {
                    log.error("Error closing WebSocket session: {}", e.getMessage());
                }
            }
            room.getSessions().clear();
            planRooms.remove(roomId);
            log.info("Room {} closed successfully", roomId);
        } else {
            log.warn("Room {} does not exist", roomId);
        }
    }


    // 모든 참가자가 도착했는지 확인하고 모두 도착했으면 세션을 종료하고 방을 제거
    public void arrivedAllParticipant(PlanRoomDto room, MessageDto message) {
//    public void arrivedAllParticipant(PlanRoomDto room, String planId) {

        if (planService.isAllMemberArrived(Long.parseLong(message.getRoomId()))) {
            // 모든 참가자가 도착한 경우
            log.info("All members arrived for planId : {}", message.getRoomId());
            /////////////////////////// 도착했으면 ///////////////////////////////////

            // 모든 참가자에게 도착 완료 메시지를 전송합니다.
            message.setType(MessageDto.MessageType.ARRIVE_COMPLETE);

            message.setMessage("모든 참가자가 도착했습니다.");
            message.setSender("system");
            sendMessageToAll(room, message);
            // 모든 세션을 종료합니다.
            for (WebSocketSession session : room.getSessions()) {
                try {
                    session.close();
                } catch (IOException e) {
                    log.error("Error while closing session", e);
                }
            }
            // 방을 제거합니다.
            planRooms.remove(message.getRoomId());
//            planRooms.remove(planId);
        }
    }


//    private void sendMessageToAll(PlanRoomDto room, MessageDto message) {
//        room.getSessions().parallelStream().forEach(session -> sendMessage(session, message));
//    }
    private void sendMessageToAll(PlanRoomDto room, MessageDto message) {
        room.getSessions().stream().forEach(session -> sendMessage(session, message));
    }

//    private void sendMessageToAll(PlanRoomDto room, MessageDto message) {
//        for (WebSocketSession session : room.getSessions()) {
//            try {
//                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
//            } catch (IOException e) {
//                log.error("Error while sending message to session", e);
//            }
//        }
//    }
}
