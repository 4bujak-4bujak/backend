package com.example.sabujak.common.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Map;

@Configuration
public class SwaggerConfig {

    private static final String BEARER = "bearer";
    private static final String JWT = "JWT";

    @Bean
    public OpenAPI openAPI() {

        Server localServer = new Server().description("로컬 서버").url("http://localhost:8080");
        Server developServer = new Server().description("develop 서버").url("https://joo-api.store");
        Server productionServer = new Server().description("production 서버").url("https://www.4bujak.site");

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
                .addServersItem(productionServer)
                .addServersItem(developServer)
                .addServersItem(localServer)
                // API 마다 Security 인증 컴포넌트 설정
                .addSecurityItem(addSecurityItem)
                .info(info);
    }

    @Bean
    public OpenApiCustomizer customizeLoginApi() {
        return openApi -> {
            openApi.getComponents().addSchemas("LoginRequest", new Schema<>()
                    .type("object")
                    .properties(Map.of(
                            "email", new Schema<>().type("string").description("이메일").example("test@gmail.com").format("email"),
                            "password", new Schema<>().type("string").description("비밀번호").example("!password12")
                    ))
                    .required(List.of("email", "password"))
            );

            Operation loginOperation = new Operation()
                    .summary("로그인 (토큰 X)")
                    .description("로그인 요청을 보냅니다")
                    .tags(List.of("인증"))
                    .requestBody(new RequestBody()
                            .description("로그인 요청")
                            .content(new Content()
                                    .addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                                            new MediaType().schema(new Schema<>().$ref("#/components/schemas/LoginRequest"))))
                            .required(true))
                    .responses(new ApiResponses()
                            .addApiResponse("200", new ApiResponse().description("로그인 성공")));

            Operation reissueOperation = new Operation()
                    .summary("토큰 재발급")
                    .description("Access 토큰과 refreshToken을 사용하여 Access 토큰을 재발급합니다.\n 아래 parameter는 Swagger가 자동으로 해주지만 이런게 들어간다는 용도로 넣었습니다")
                    .tags(List.of("인증"))
                    .addParametersItem(new Parameter()
                            .in("header")
                            .name("Authorization")
                            .description("Bearer [accessToken]")
                            .required(false)
                            .schema(new Schema<>().type("string")))
                    .addParametersItem(new Parameter()
                            .in("cookie")
                            .name("refreshToken")
                            .description("Refresh Token")
                            .required(false)
                            .schema(new Schema<>().type("string")))
                    .responses(new ApiResponses()
                            .addApiResponse("200", new ApiResponse().description("토큰 재발급 성공"))
                            .addApiResponse("401", new ApiResponse().description("인증 실패")));

            Operation logoutOperation = new Operation()
                    .summary("로그아웃")
                    .description("Access 토큰과 refreshToken을 사용하여 로그아웃을 진행합니다.\n 아래 parameter는 Swagger가 자동으로 해주지만 이런게 들어간다는 용도로 넣었습니다")
                    .tags(List.of("인증"))
                    .addParametersItem(new Parameter()
                            .in("header")
                            .name("Authorization")
                            .description("Bearer [accessToken]")
                            .required(false)
                            .schema(new Schema<>().type("string")))
                    .addParametersItem(new Parameter()
                            .in("cookie")
                            .name("refreshToken")
                            .description("Refresh Token")
                            .required(false)
                            .schema(new Schema<>().type("string")))
                    .responses(new ApiResponses()
                            .addApiResponse("200", new ApiResponse().description("로그아웃 성공"))
                            .addApiResponse("401", new ApiResponse().description("로그아웃 실패")));

            PathItem loginPathItem = new PathItem().post(loginOperation);
            PathItem reissuePathItem = new PathItem().post(reissueOperation);
            PathItem logoutPathItem = new PathItem().post(logoutOperation);

            Paths paths = openApi.getPaths();
            if (paths == null) paths = new Paths();
            paths.addPathItem("/login", loginPathItem);
            paths.addPathItem("/reissue", reissuePathItem);
            paths.addPathItem("/logout", logoutPathItem);
            openApi.setPaths(paths);
        };
    }
}
