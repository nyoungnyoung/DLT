package com.dopamines.backend.image.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.dopamines.backend.account.entity.Account;
import com.dopamines.backend.account.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    @Value("${naver.cloud.endpoint}")
    private String endPoint;
    @Value("${naver.cloud.region.name}")
    private String regionName;
    @Value("${naver.cloud.bucket.name}")
    private String bucketName;
    @Value("${naver.cloud.access.key}")
    private String accessKey;
    @Value("${naver.cloud.secret.key}")
    private String secretKey;
    private AmazonS3 s3;

    @Autowired
    private AccountRepository accountRepository;


    @PostConstruct
    public void initialize(){
        s3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();
    }


    public String saveFile(MultipartFile file,String folderName) throws IOException {
        // 원래 파일 이름 추출
        String origin_name = file.getOriginalFilename();
        // 파일 이름으로 쓸 uuid 생성
        String uuid = UUID.randomUUID().toString();
        // uuid와 확장자 결합
        String saved_name = uuid + origin_name;

        String bucketPath=bucketName+"/"+folderName;
        File uploadFile = convert(file)        // 파일 생성
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File convert fail"));

        upload(bucketPath,saved_name,uploadFile);
        // 파일 업로드 & 링크 받아오기

        return endPoint +"/"+ bucketPath +"/"+ saved_name;

    }

    private void upload(String bucketName, String fileName, File uploadFile) {
        s3.putObject(new PutObjectRequest(bucketName, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        uploadFile.delete();
    }
    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(file.getOriginalFilename());
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Account> editProfile(String email, MultipartFile multipartFile) throws IOException {

        String url=saveFile(multipartFile, "profile");

        Optional<Account> optional = accountRepository.findByEmail(email);
        Account account = null;
        if(optional.isEmpty()) {
            account = new Account();
            log.info("ImageController의 editProfile에서 optional.isEmpty");
            return null;
        }else {
            account = optional.get();
            account.setProfile(url);

            accountRepository.save(account);
            return Optional.of(account);
        }
    }
}
