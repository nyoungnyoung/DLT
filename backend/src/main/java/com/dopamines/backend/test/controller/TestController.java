package com.dopamines.backend.test.controller;


import com.dopamines.backend.test.dto.ObjectDto;
import com.dopamines.backend.test.dto.TestDto;
import com.dopamines.backend.test.service.TestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Api(value = "test", description = "테스트 컨트롤러입니다.")
public class TestController {

    private Logger log = LoggerFactory.getLogger(TestController.class);

    private final TestService teatservice;

    //db와 상호작용 필요없는 일반 api
    @GetMapping("/hello")
    @Operation(summary = "hello world 출력", description = "hello world 반환") //notes는 안적어도 상관없음!
    public String Hello(){
        return "hello world!";
    }

    
    //jpa가 기본적으로 제공하는 함수를 이용해 db와 상호작용 (post)
    @PostMapping("/testPost")
    @Operation(summary = "Jpa 기본 동작 확인", description = "데이터베이스에 데이터 삽입")
    public ResponseEntity<Void> postData(@RequestParam("name") String name) {
        teatservice.saveData(name);
        return ResponseEntity.ok().build();
    }


    //jpa가 기본적으로 제공하는 함수를 이용해 db와 상호작용 (get)
    @GetMapping("/testDefault")
    @Operation(summary = "jpa 기본 동작 확인", description = "테스트 테이블의 칼럼 수 반환")
    public long getCount(){
        return teatservice.getCount();
    }


    //jpa가 기본적으로 제공하지 않지만, dto 이용
    @GetMapping("/testCustom")
    @Operation(summary = "jpa dto 동작 확인", description = "이름에 '안녕'을 포함하는 칼럼 리스트 반환")
    public List<TestDto> getTest(){
        return teatservice.getCustom("안녕");
    }


    @GetMapping("/get")
    public ObjectDto getObjectTest(){
        ObjectDto objectDto = new ObjectDto(11L, "getObjectTest");

        return objectDto;
    }


    @PostMapping("/post")
    public Map<String, List<ObjectDto>> postObjectTest(String name){
        if(name == null){
            log.info("name이 null임");
        } else {
            log.info("name: " + name);
        }

        Map<String, List<ObjectDto>> res = new HashMap<>();
        res.put("res", new ArrayList<ObjectDto>());
        for(long i = 0; i < 10L; i++) {
            res.get("res").add(new ObjectDto(i, name + i));
        }
        return res;
    }


}
