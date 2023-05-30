package com.dopamines.backend.review.service;

import com.dopamines.backend.account.entity.Account;
import com.dopamines.backend.account.service.UserService;
import com.dopamines.backend.plan.entity.Participant;
import com.dopamines.backend.plan.entity.Plan;
import com.dopamines.backend.plan.repository.ParticipantRepository;
import com.dopamines.backend.plan.repository.PlanRepository;
import com.dopamines.backend.plan.service.PlanService;
import com.dopamines.backend.review.dto.CommentDto;
import com.dopamines.backend.review.entity.Comment;
import com.dopamines.backend.review.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final ParticipantRepository participantRepository;

    private final PlanRepository planRepository;

    private final PlanService planService;

    private final UserService userService;


    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글 정보가 없습니다."));
    }


    // 해당 날짜의 약속 리스트
    @Override
    public Map<LocalDate, List<CommentDto>> getCommentList(Long planId) {
        Plan plan = planService.getPlanById(planId);
        List<Comment> comments = commentRepository.findByPlan(plan);

        // 순서가 보장되는 Map
        Map<LocalDate, List<CommentDto>> commentMap = new LinkedHashMap<>();

        for (Comment comment : comments) {
            LocalDate date = comment.getUpdateTime().toLocalDate();
            CommentDto commentDto = new CommentDto(
                    comment.getCommentId(),
                    comment.getParticipant().getAccount().getNickname(),
                    comment.getParticipant().getAccount().getProfile(),
                    comment.getContent(),
                    comment.getUpdateTime()
            );

            if (commentMap.containsKey(date)) {
                commentMap.get(date).add(commentDto);
            } else {
                List<CommentDto> commentList = new ArrayList<>();
                commentList.add(commentDto);
                commentMap.put(date, commentList);
            }
        }

        return commentMap;
    }

    @Override
    public Long createComment(String userEmail, Long planId, String content) {

        Account account = userService.findByEmail(userEmail);

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("해당 약속 정보가 없습니다."));

        Participant participant = participantRepository.findByPlanAndAccount(plan,account)
                .orElseThrow(() -> new IllegalArgumentException("해당 약속에 참가자의 정보가 없습니다."));

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        Comment comment = Comment.builder()
                .participant(participant)
                .plan(plan)
                .content(content)
                .registerTime(now)
                .updateTime(now)
                .build();
        commentRepository.save(comment);
        log.info(account.getEmail() + " 님이 댓글이 등록되었습니다");
        return comment.getCommentId();
    }

    @Override
    public void updateComment(String userEmail, Long commentId, String content) {

        Comment comment = getCommentById(commentId);

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        comment.setContent(content);
        comment.setUpdateTime(now);
        commentRepository.save(comment);
        log.info(userEmail + " 님이 댓글이 수정되었습니다");
    }

    // 약속 삭제
    @Override
    public void deleteComment(Long commentId) {
        Comment comment = getCommentById(commentId);
        commentRepository.delete(comment);
    }

    // 내 댓글이니? (권한 확인)
    @Override
    public Boolean isMyComment(String userEmail, Long commentId) {
        Comment comment = getCommentById(commentId);
        return comment.getParticipant().getAccount().getEmail().equals(userEmail);
    }
}
