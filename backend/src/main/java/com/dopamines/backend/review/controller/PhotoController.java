package com.dopamines.backend.review.controller;

import com.dopamines.backend.image.service.ImageService;
import com.dopamines.backend.plan.service.PlanService;
import com.dopamines.backend.review.dto.PhotoDateDto;
import com.dopamines.backend.review.dto.PhotoDetailDto;
import com.dopamines.backend.review.dto.PhotoMonthDto;
import com.dopamines.backend.review.service.PhotoService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/photo")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Api(value = "photo", description = "약속 사진을 관리하는 컨트롤러입니다.")
public class PhotoController {

    private final ImageService imageService;

    private final PhotoService photoService;

    private final PlanService planService;


    @PostMapping("/register")
    @Operation(summary = "인증 사진을 등록하는 api입니다.", description = "planId와 photoFile 활용하여 사진을 업로드 합니다. 사진 업로드에 성공하면 photoId를 반환합니다.<br/>" +
            "사진은 photoFile에 MultipartFile의 형태로 받아옵니다.")
    public ResponseEntity<Long> savePhoto(
            HttpServletRequest request,
            @RequestParam("planId") Long planId,
            @RequestParam("photoFile") MultipartFile file
    ) {

        String userEmail = request.getRemoteUser();

        // 참가자 인지 확인
        if (!planService.isMyPlan(userEmail, planId)) {
            log.info("참가자가 아닙니다. userEmail: {}, planId: {}", userEmail, planId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // 사진있는지 확인
        if (photoService.isPhotoRegistered(planId)) {
            log.info("이미 사진이 등록되어 있습니다. planId:{}", planId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // 사진 업로드
        String url = null;
        try {
            url = imageService.saveFile(file, "photo");
        } catch (IOException e) {
            log.error("사진 업로드 실패", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }

        Long photoId = photoService.savePicture(planId, url);

        return ResponseEntity.ok(photoId);
    }

    @GetMapping("/list")
    @Operation(summary = "기록 탭에 월별 사진 내역을 가져오는 api입니다.", description = "date 활용하여 해당 월의 사진 정보를 가져옵니다.<br/>" +
            "date는 'yyyy-MM-dd' 형태의 문지열로 입력합니다. 일자 dd는 영향을 주지않으므로 01로 통일합니다. (예시: 2023-05-01)")
    public ResponseEntity<List<PhotoMonthDto>> getPhotoList(
            HttpServletRequest request,
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate selectedDate
    ) {

        String userEmail = request.getRemoteUser();
        // 선택한 달의 시작일과 종료일 구하기
        List<PhotoMonthDto> photoList = photoService.getPhotosByMonthAndUser(userEmail, selectedDate);

        return ResponseEntity.ok(photoList);
    }


    @GetMapping("/listMap")
    @Operation(summary = "기록 탭에 월별 사진 내역을 가져오는 api입니다.", description = "date 활용하여 해당 월의 사진 정보를 날짜별로 매핑하여 가져옵니다.<br/> " +
            "date는 'yyyy-MM-dd' 형태의 문지열로 입력합니다. 일자 dd는 영향을 주지않으므로 01로 통일합니다. (예시: 2023-05-01)")
    public ResponseEntity<Map<LocalDate, List<PhotoDateDto>>> getPhotoListMap(
            HttpServletRequest request,
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate selectedDate
    ) {

        String userEmail = request.getRemoteUser();
        // 선택한 달의 시작일과 종료일 구하기
        Map<LocalDate, List<PhotoDateDto>> photoList = photoService.getPhotosByDateMap(userEmail, selectedDate);

        return ResponseEntity.ok(photoList);
    }


    @GetMapping("/detail")
    @Operation(summary = "약속 사진을 가져오는 api입니다.", description = "planId를 활용하여 해당 약속의 사진 정보를 가져옵니다.")
    public ResponseEntity<PhotoDetailDto> getPhoto(
            HttpServletRequest request,
            @RequestParam("planId") Long planId
    ) {

        String userEmail = request.getRemoteUser();

        // 참가자 인지 확인
        if (!planService.isMyPlan(userEmail, planId)) {
            log.info("해당 약속의 참가자가 아닙니다. userEmail: {}, planId: {}", userEmail, planId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        PhotoDetailDto photoDto  = photoService.getPhoto(planId);
        return ResponseEntity.ok(photoDto);
    }
}
