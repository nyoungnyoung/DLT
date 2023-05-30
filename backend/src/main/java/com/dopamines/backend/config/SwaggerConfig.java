package com.dopamines.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Server;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;


@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {
        Server serverLocal = new Server("local", "http://localhost:8081", "for local usages", Collections.emptyList(), Collections.emptyList());
        Server server = new Server("test", "https://k8d209.p.ssafy.io", "for testing", Collections.emptyList(), Collections.emptyList());        return new Docket(DocumentationType.OAS_30)
                .servers(serverLocal, server)
                .useDefaultResponseMessages(false)
                .groupName("dopamines")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors
                        .basePackage("com.dopamines.backend"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("언제와: Don't Late Together")
                .description("SSAFY 8기 구미 D209 DOPAMINES팀의 『언제와: Don't Late Together』 입니다.♡")
                .version("1.0")
                .build();
    }


}

