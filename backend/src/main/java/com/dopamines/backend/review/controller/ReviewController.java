package com.dopamines.backend.review.controller;

import com.dopamines.backend.plan.service.PlanService;
import com.dopamines.backend.review.dto.PhotoDetailDto;
import com.dopamines.backend.review.dto.ReviewDto;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Api(value = "review", description = "약속 후기를 관리하는 컨트롤러입니다.")
public class ReviewController {

//    private final PlanService planService;


//    @GetMapping("/detail")
//    @Operation(summary = "약속 사진과 댓글을 모두 가져오는 api입니다.", description = "planId를 활용하여 해당 약속의 후기 정보를 모두 가져옵니다.")
//    public ResponseEntity<ReviewDto> getReview(
//            HttpServletRequest request,
//            @RequestParam("planId") Long planId
//    ){
//        String userEmail = request.getRemoteUser();
//
//        // 참가자 인지 확인
//        if (!planService.isMyPlan(userEmail, planId)) {
//            log.info("해당 약속의 참가자가 아닙니다. userEmail: {}, planId: {}", userEmail, planId);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        }
//
//        ReviewDto reviewDto = reviewService.getReview(planId);
//
//    }

}
