package com.dopamines.backend.review.service;

import com.dopamines.backend.review.dto.CommentDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CommentService {

    // 댓글 생성
    Long createComment(String userEmail, Long planId, String content);

    // 댓글 수정
    void updateComment(String userEmail, Long planId, String content);

    // 댓글 권한
    Boolean isMyComment(String userEmail, Long commentId);

    // 댓글 삭제
    void deleteComment(Long commentId);

    // 댓글 리스트
    Map<LocalDate, List<CommentDto>> getCommentList(Long planId);

}
