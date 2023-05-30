package com.dopamines.backend.fcm.service;

import com.dopamines.backend.account.entity.Account;
import com.dopamines.backend.account.repository.AccountRepository;
import com.dopamines.backend.fcm.dto.FirebaseCloudMessageToken;
import com.dopamines.backend.fcm.dto.FirebaseCloudMessageTopic;
import com.dopamines.backend.fcm.dto.GroupTokenListDto;
import com.dopamines.backend.fcm.entity.FCM;
import com.dopamines.backend.fcm.repository.FirebaseCloudMessageRepository;
import com.dopamines.backend.plan.entity.Participant;
import com.dopamines.backend.plan.entity.Plan;
import com.dopamines.backend.plan.repository.ParticipantRepository;
import com.dopamines.backend.plan.repository.PlanRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.net.HttpHeaders;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.TopicManagementResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FirebaseCloudMessageServiceImpl implements FirebaseCloudMessageService {

    private final String API_URL = "https://fcm.googleapis.com/v1/projects/d209-dopamines/messages:send";

    private final ObjectMapper objectMapper;

    private final AccountRepository accountRepository;

    private final FirebaseCloudMessageRepository fcmRepository;

    private final PlanRepository planRepository;

    private final ParticipantRepository participantRepository;


    @Override
    public void registerToken(String userEmail, String deviceToken) {
        Account account = accountRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원 정보가 없습니다."));

        Optional<FCM> existingFcm = fcmRepository.findByAccount(account);
        if (existingFcm.isPresent()) {
            throw new IllegalArgumentException("이미 해당 유저의 FCM deviceToken이 존재합니다.");
        }

        FCM fcm = FCM.builder()
                .account(account)
                .deviceToken(deviceToken)
                .build();
        fcmRepository.save(fcm);
        log.info("{} 님의 deviceToken : {} 이 저장되었습니다.",  account.getEmail(), deviceToken);
    }


    @Override
    public void updateToken(String userEmail, String deviceToken) {
        Account account = accountRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원 정보가 없습니다."));

        FCM fcm = fcmRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("해당 토큰 정보가 없습니다."));

        fcm.setDeviceToken(deviceToken);
        fcmRepository.save(fcm);
        log.info("{} 님의 deviceToken : {} 이 갱신되었습니다.",  account.getEmail(), deviceToken);
    }


    @Override
    public void deleteToken(String userEmail) {

        Account account = accountRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원 정보가 없습니다."));

        FCM fcm = fcmRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("해당 토큰 정보가 없습니다."));

        fcmRepository.delete(fcm);
    }


    @Override
    public GroupTokenListDto getGroupToken(Long planId) {

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("해당 약속 정보가 없습니다."));

        List<Participant> participants = participantRepository.findByPlan(plan);

        List<String> tokenList = new ArrayList<>();
        for (Participant participant : participants) {
            Optional<FCM> fcm = fcmRepository.findByAccount(participant.getAccount());
            if (fcm.isPresent()) {
                tokenList.add(fcm.get().getDeviceToken());
            }
        }
        GroupTokenListDto groupTokenListDto = new GroupTokenListDto(tokenList);
        return groupTokenListDto;
    }


    @Override
    public List<String> getGroupTokens(Long planId) {

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("해당 약속 정보가 없습니다."));

        List<Participant> participants = participantRepository.findByPlan(plan);

        List<String> tokenList = new ArrayList<>();
        for (Participant participant : participants) {
            Optional<FCM> fcm = fcmRepository.findByAccount(participant.getAccount());
            if (fcm.isPresent()) {
                tokenList.add(fcm.get().getDeviceToken());
            }
        }
        return tokenList;
    }


    // fcm 주제를 구독 시킵니다.
    public void SubscribeTopic(List<Long> accountIds, String topic) throws FirebaseMessagingException {

        List<String> registrationTokens = new ArrayList<>();

        // These registration tokens come from the client FCM SDKs.
        for (Long accountId : accountIds) {
            Optional<FCM> fcm = fcmRepository.findByAccount_AccountId(accountId);
            if (fcm.isPresent()) {
                registrationTokens.add(fcm.get().getDeviceToken());
            }
        }

        TopicManagementResponse response = FirebaseMessaging.getInstance().subscribeToTopic(registrationTokens, topic);
        // See the TopicManagementResponse reference documentation
        // for the contents of response.
        log.info(response.getSuccessCount() + " tokens were subscribed successfully");

    }


    @Override
    public void sendTopicMessageTo(String topic, String title, String body, String planId, String type) throws IOException {
        String message = makeTopicMessage(topic, title, body, planId, type);

        try {
            // OkHttp3 를 이용해, Http Post Request를 생성
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(requestBody)
                    // header에 AccessToken을 추가
                    .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                    .build();

            Response response = client.newCall(request)
                    .execute();

            log.info("Successfully sent message: " + response);
            log.info("성공하면 각 전송 메서드가 메시지 ID를 반환: " + response.body().string());
//            log.info(response.body().string());
        } catch (IOException e) {
            log.error("Error in sending message to FCM server " + e);
        }
    }


    // FcmMessage를 만들고, 이를 ObjectMapper을 이용해 String으로 변환하여 반환
    private String makeTopicMessage(String topic, String title, String body, String planId, String type) throws JsonProcessingException {

        FirebaseCloudMessageTopic fcmMessage = FirebaseCloudMessageTopic.builder()
                .message(FirebaseCloudMessageTopic.Message.builder()
                        .topic(topic)
                        .notification(FirebaseCloudMessageTopic.Notification.builder()
                                .title(title)
                                .body(body)
                                .build()
                        )
                        .data(FirebaseCloudMessageTopic.Data.builder()
                                .planId(planId)
                                .type(type)
                                .build()
                        )
                        .build()
                )
                .validate_only(false)
                .build();

        return objectMapper.writeValueAsString(fcmMessage);
    }


    // targetToken에 해당하는 device로 FCM 푸시알림을 전송 요청 (특정 기기에 메시지 전송)
    @Override
    public void sendTokenMessageTo(String targetToken, String title, String body, String planId, String type) throws IOException {
        String message = makeTokenMessage(targetToken, title, body, planId, type);

        try {
            // OkHttp3 를 이용해, Http Post Request를 생성
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(requestBody)
                    // header에 AccessToken을 추가
                    .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                    .build();

            Response response = client.newCall(request)
                    .execute();

            log.info("Successfully sent message: " + response);
            log.info("성공하면 각 전송 메서드가 메시지 ID를 반환");
            log.info(response.body().string());
        } catch (IOException e) {
            log.error("Error in sending message to FCM server " + e);
        }
    }


    // FcmMessage를 만들고, 이를 ObjectMapper을 이용해 String으로 변환하여 반환
    private String makeTokenMessage(String targetToken, String title, String body, String planId, String type) throws JsonProcessingException {
        FirebaseCloudMessageToken fcmMessage = FirebaseCloudMessageToken.builder()
                .message(FirebaseCloudMessageToken.Message.builder()
                        .token(targetToken)
                        .notification(FirebaseCloudMessageToken.Notification.builder()
                                .title(title)
                                .body(body)
                                .build()
                        )
                        .data(FirebaseCloudMessageToken.Data.builder()
                                .planId(planId)
                                .type(type)
                                .build()
                        )
                        .build()
                )
                .validateOnly(false)
                .build();

        return objectMapper.writeValueAsString(fcmMessage);
    }


    // FCM을 이용할수 있는 권한이 부여된 Oauth2의 AccessToken을 받음
    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "/firebase/d209-dopamines-firebase-adminsdk-pz1bc-a666dc4e60.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        //  AccessToken은 RestAPI를 이용해 FCM에 Push 요청을 보낼때, Header에 설정하여, 인증을 위해 사용
        return googleCredentials.getAccessToken().getTokenValue();
    }
}
