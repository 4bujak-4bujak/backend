package com.example.sabujak.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class SwaggerConfig {

    private static final String BEARER = "bearer";
    private static final String JWT = "JWT";

    @Bean
    public OpenAPI openAPI() {

        Server localServer = new Server().description("로컬 서버").url("http://localhost:8080");
        Server deployedServer = new Server().description("배포된 서버").url("https://www.4bujak.site");

        Info info = new Info()
                .title("Offispace API 명세서")
                .version("1.0.0")
                .description("4부작4부작 팀의 Offispace API 명세서입니다.")
                .contact(new Contact() // 연락처
                        .name("4bujak-4bujak")
                        .email("jjwm0128@naver.com")
                        .url("https://github.com/4bujak-4bujak"));

        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme(BEARER)
                .bearerFormat(JWT)
                .in(SecurityScheme.In.HEADER)
                .name(HttpHeaders.AUTHORIZATION);

        // Security 요청 설정
        SecurityRequirement addSecurityItem = new SecurityRequirement();
        addSecurityItem.addList(JWT);

        return new OpenAPI()
                // Security 인증 컴포넌트 설정
                .components(new Components().addSecuritySchemes(JWT, bearerAuth))
                .addServersItem(deployedServer)
                .addServersItem(localServer)
                // API 마다 Security 인증 컴포넌트 설정
                .addSecurityItem(addSecurityItem)
                .info(info);
    }
}
