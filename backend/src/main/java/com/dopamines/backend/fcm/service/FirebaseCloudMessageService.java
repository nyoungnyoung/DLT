package com.dopamines.backend.fcm.service;

import com.dopamines.backend.fcm.dto.GroupTokenListDto;

import java.io.IOException;
import java.util.List;

public interface FirebaseCloudMessageService {

    // 토큰 저장
    void registerToken(String userEmail, String deviceToken);

    // 토큰 수정
    void updateToken(String userEmail, String deviceToken);

    // 토큰 삭제
    void deleteToken(String userEmail);

    // 참가자 리스트 가져오기
    GroupTokenListDto getGroupToken(Long planId);

    // 그룹 토큰 리스트로 가져오기
    List<String> getGroupTokens(Long planId);

    // 특정 기기에 메시지 전송
    void sendTokenMessageTo(String targetToken, String title, String body, String planId, String type) throws IOException;

    // 주제로 메시지 전송
    void sendTopicMessageTo(String topic, String title, String body, String planId, String type) throws IOException;


}
