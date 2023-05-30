package com.dopamines.backend.friend.controller;

import com.dopamines.backend.friend.dto.FriendResponseDto;
import com.dopamines.backend.friend.entity.Friend;
import com.dopamines.backend.friend.service.FriendService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/friend")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*", allowedHeaders = "*")
@Api(value = "friend", description = "친구 신청 및 수락을 합니다.")
public class FriendController {
    private final FriendService friendService;

    @GetMapping("/get")
    public ResponseEntity<Map<String, List<FriendResponseDto>>> getFriendList(HttpServletRequest request){
        String email = request.getRemoteUser();
        return ResponseEntity.ok(friendService.getFriendList(email));
    }

    @PostMapping("/add")
    public ResponseEntity<FriendResponseDto> addFriend(HttpServletRequest request, Long friendId) {
        String email = request.getRemoteUser();
        try{
            return ResponseEntity.ok(friendService.addFriend(email, friendId));

        } catch(IllegalArgumentException e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        }catch (RuntimeException e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        }
    }


    @PostMapping("/accept")
    public ResponseEntity<FriendResponseDto> acceptFriend(HttpServletRequest request, Long friendId) {
        String email = request.getRemoteUser();
        try {
            return ResponseEntity.ok(friendService.acceptFriend(email, friendId));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    @DeleteMapping("/deny")
    public ResponseEntity<FriendResponseDto> denyFriend(HttpServletRequest request, Long friendId) {
        String email = request.getRemoteUser();
        try {
            return ResponseEntity.ok(friendService.denyFriend(email, friendId));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/delete") //친구삭제
    public ResponseEntity<FriendResponseDto> deleteFriend (HttpServletRequest request,  Long friendId) {
        String email = request.getRemoteUser();
        try {
            return ResponseEntity.ok(friendService.deleteFriend(email, friendId));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}
