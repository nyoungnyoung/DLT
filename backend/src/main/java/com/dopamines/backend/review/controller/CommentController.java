package com.dopamines.backend.review.controller;

import com.dopamines.backend.review.dto.CommentDto;
import com.dopamines.backend.review.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Api(value = "comment", description = "약속 후기 댓글을 관리하는 컨트롤러입니다.")
public class CommentController {

    private final CommentService commentService;


    @PostMapping("/register")
    @Operation(summary = "댓글 등록 api 입니다.", description = "planId와 content를 입력하여 댓글을 등록합니다. 댓글이 생성되면 commentId를 Long 형태로 반환합니다.")
    public ResponseEntity<Long> createComment(
            HttpServletRequest request,
            @RequestParam("planId") Long planId,
            @RequestParam("content") String content
    ){
        // 헤더에서 유저 이메일 가져옴
        String userEmail = request.getRemoteUser();
        Long commentId = commentService.createComment(userEmail, planId, content);
        return ResponseEntity.ok(commentId);
    }

    @PutMapping("/update")
    @Operation(summary = "댓글 수정 api 입니다.", description = "commentId와 content를 입력하여 댓글을 수정합니다.")
    public ResponseEntity<Void> updateComment(
            HttpServletRequest request,
            @RequestParam("commentId") Long commentId,
            @RequestParam("content") String content
    ){

        try{
            // 헤더에서 유저 이메일 가져옴
            String userEmail = request.getRemoteUser();

            if (!commentService.isMyComment(userEmail,commentId)){
                log.warn("내 댓글이 아닙니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            commentService.updateComment(userEmail, commentId, content);

            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            log.error("수정 실패: commentId {}는 존재하지 않는 commentId 입니다.", commentId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    @DeleteMapping("/delete")
    @Operation(summary = "댓글 수정 api 입니다.", description = "commentId를 입력하여 댓글을 삭제합니다.")
    public ResponseEntity<Void> deleteComment(
            HttpServletRequest request,
            @RequestParam("commentId") Long commentId
    ){
        try {
            String userEmail = request.getRemoteUser();

            if (!commentService.isMyComment(userEmail, commentId)) {
                log.warn("내 댓글이 아닙니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            commentService.deleteComment(commentId);
            log.info("commentId {} 삭제되었습니다.", commentId);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            // PlanId가 잘못된 경우, HttpStatus.BAD_REQUEST 반환
            log.error("삭제 실패: commentId {}는 존재하지 않는 commentId 입니다.", commentId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/list")
    @Operation(summary = "댓글 리스트 api 입니다.", description = "planId를 입력하여 해당 약속의 댓글을 모두 불러옵니다.")
    public ResponseEntity<Map<LocalDate, List<CommentDto>>>  getCommentList(
            HttpServletRequest request,
            @RequestParam("planId") Long planId
    ){

        Map<LocalDate, List<CommentDto>> commentMap = commentService.getCommentList(planId);
        return ResponseEntity.ok(commentMap);
    }
}