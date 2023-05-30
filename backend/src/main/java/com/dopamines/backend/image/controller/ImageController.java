package com.dopamines.backend.image.controller;


import com.dopamines.backend.account.entity.Account;
import com.dopamines.backend.account.repository.AccountRepository;
import com.dopamines.backend.image.service.ImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Api(value = "image", description = "파일을 업로드 합니다.")
public class ImageController {


    private final ImageService imageService;

    @PostMapping("/upload")
    @ApiOperation(value = "파일 업로드", notes = "파일을 업로드하고 img url을 가져옵니다. 폴더명은 NaverCloud 폴더명에 맞게 입력합니다.")
    public ResponseEntity<String> uploadAndGetLink(MultipartFile multipartFile, String folderName) throws Exception  {
        String url = imageService.saveFile(multipartFile,folderName);
        return new ResponseEntity<>(url, HttpStatus.OK);
    }

    @PutMapping("/profile")
    @ApiOperation(value = "프로필 사진 바꾸기", notes = "프로필 사진을 MultipartFile 로 업로드하고 Account entity를 반환합니다.")
    public ResponseEntity<Optional<Account>> editProfile(HttpServletRequest request, @RequestParam MultipartFile multipartFile) throws Exception  {

        String email = request.getRemoteUser();

        return ResponseEntity.ok(imageService.editProfile(email, multipartFile));
    }
}
