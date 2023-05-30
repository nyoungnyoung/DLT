package com.dopamines.backend.review.service;

import com.dopamines.backend.account.entity.Account;
import com.dopamines.backend.account.service.UserService;
import com.dopamines.backend.plan.entity.Participant;
import com.dopamines.backend.plan.entity.Plan;
import com.dopamines.backend.plan.repository.ParticipantRepository;
import com.dopamines.backend.plan.repository.PlanRepository;
import com.dopamines.backend.plan.service.PlanService;
import com.dopamines.backend.review.dto.PhotoDateDto;
import com.dopamines.backend.review.dto.PhotoDetailDto;
import com.dopamines.backend.review.dto.PhotoMonthDto;
import com.dopamines.backend.review.entity.Photo;
import com.dopamines.backend.review.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

    private final PhotoRepository photoRepository;

    private final PlanRepository planRepository;

    private final UserService userService;

    private final ParticipantRepository participantRepository;

    // 인증을 사진을 등록하는 함수
    @Override
    public Long savePicture(Long planId, String file) {
//        Plan plan = planService.getPlanById(planId);
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 planId 입니다."));

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        Photo photo = Photo.builder()
                .plan(plan)
                .photoUrl(file)
                .registerTime(now)
                .build();
        photoRepository.save(photo);

        return photo.getPhotoId();

    }

    @Override
    public PhotoDetailDto getPhoto(Long planId){
//        Plan plan = planService.getPlanById(planId);
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 planId 입니다."));

        Optional<Photo> photo = photoRepository.findByPlan(plan);
        PhotoDetailDto photoDto = new PhotoDetailDto();
        photoDto.setPlanId(planId);
        if (photo.isPresent()){
            photoDto.setPhotoId(photo.get().getPhotoId());
            photoDto.setPhotoUrl(photo.get().getPhotoUrl());
            photoDto.setRegisterTime(photo.get().getRegisterTime());
        } else {
            log.info("해당 planId {}의 사진 정보가 없습니다.",planId);
        }

        return photoDto;
    }

    // 사진 있는지 확인
    @Override
    public boolean isPhotoRegistered(Long planId) {
//        Plan plan = planService.getPlanById(planId);
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 planId 입니다."));

        Optional<Photo> photo = photoRepository.findByPlan(plan);
        return photo.isPresent();
    }


    // 월별 사진 리스트를 가져오는 함수
    public List<PhotoMonthDto> getPhotosByMonthAndUser(String userEmail, LocalDate selectedDate) {

        Account account = userService.findByEmail(userEmail);

        // 선택한 달의 시작일과 종료일을 계산합니다.
        LocalDate startDate = selectedDate.withDayOfMonth(1);
        LocalDate endDate = selectedDate.withDayOfMonth(selectedDate.lengthOfMonth());

        // 현재 사용자가 참여한 해당 월의 모든 약속 리스트를 가져온다.
        List<Participant> myParticipants = participantRepository.findByAccountAndPlanPlanDateBetween(account, startDate, endDate);

        List<PhotoMonthDto> photoDtoList = new ArrayList<>();
        for (Participant participant : myParticipants) {

            // 참여한 약속의 사진 리스트 가져오기
            List<Photo> photos = photoRepository.findAllByPlan(participant.getPlan());

            if (!photos.isEmpty()) { // 값이 있는 경우

                // 각 사진을 PhotoDto로 변환하여 리스트에 추가
                for (Photo photo : photos) {
                    PhotoMonthDto photoDto = new PhotoMonthDto();

                    photoDto.setPhotoId(photo.getPhotoId());
                    photoDto.setPlanId(participant.getPlan().getPlanId());
                    photoDto.setPhotoUrl(photo.getPhotoUrl());
                    photoDto.setPlanDate(participant.getPlan().getPlanDate());
                    photoDto.setPlanTime(participant.getPlan().getPlanTime());
                    photoDtoList.add(photoDto);
                }
            } else { // 빈 객체인 경우
                // Photo가 없는 경우, PhotoDto에 NULL값을 넣어줌
                log.info("해당 planId {}의 사진 정보가 없습니다.",participant.getPlan().getPlanId());

                PhotoMonthDto photoDto = new PhotoMonthDto();
                photoDto.setPhotoId(null);
                photoDto.setPlanId(participant.getPlan().getPlanId());
                photoDto.setPhotoUrl(null);
                photoDto.setPlanDate(participant.getPlan().getPlanDate());
                photoDto.setPlanTime(participant.getPlan().getPlanTime());
                photoDtoList.add(photoDto);
            }
        }

        return photoDtoList;
    }


    // 월별 사진 리스트를 일별로 매핑하여 가져오는 함수
    public Map<LocalDate, List<PhotoDateDto>> getPhotosByDateMap(String userEmail, LocalDate selectedDate) {
        Account account = userService.findByEmail(userEmail);
        LocalDate startDate = selectedDate.withDayOfMonth(1);
        LocalDate endDate = selectedDate.withDayOfMonth(selectedDate.lengthOfMonth());

        // 현재 사용자가 참여한 해당 월의 모든 약속 리스트를 가져온다.
        List<Participant> myParticipants = participantRepository.findByAccountAndPlanPlanDateBetween(account, startDate, endDate);

        // 일짜별로 매핑
        Map<LocalDate, List<PhotoDateDto>> photoDtoMap = new HashMap<>();
        for (Participant participant : myParticipants) {
            List<PhotoDateDto> photoDtos = new ArrayList<>();

            // 참여한 약속의 사진 리스트 가져오기
            List<Photo> photos = photoRepository.findAllByPlan(participant.getPlan());

            if (!photos.isEmpty()) {
                for (Photo photo : photos) {
                    PhotoDateDto photoDto = new PhotoDateDto();
                    photoDto.setPhotoId(photo.getPhotoId());
                    photoDto.setPlanId(participant.getPlan().getPlanId());
                    photoDto.setPhotoUrl(photo.getPhotoUrl());
                    photoDto.setPlanTime(participant.getPlan().getPlanTime());
                    photoDtos.add(photoDto);
                }
            } else {
                log.info("해당 planId {}의 사진 정보가 없습니다.",participant.getPlan().getPlanId());
                PhotoDateDto photoDto = new PhotoDateDto();
                photoDto.setPhotoId(null);
                photoDto.setPlanId(participant.getPlan().getPlanId());
                photoDto.setPhotoUrl(null);
                photoDto.setPlanTime(participant.getPlan().getPlanTime());
                photoDtos.add(photoDto);
            }

            LocalDate planDate = participant.getPlan().getPlanDate();
            if (photoDtoMap.containsKey(planDate)) {
                photoDtoMap.get(planDate).addAll(photoDtos);
            } else {
                photoDtoMap.put(planDate, photoDtos);
            }
        }

        return photoDtoMap;
    }
}
