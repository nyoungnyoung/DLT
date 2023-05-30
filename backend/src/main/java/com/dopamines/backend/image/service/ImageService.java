package com.dopamines.backend.image.service;

import com.dopamines.backend.account.entity.Account;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface ImageService {
    String saveFile(MultipartFile file,String folderName) throws IOException;
    Optional<Account> editProfile(String email, MultipartFile multipartFile) throws IOException;
}
