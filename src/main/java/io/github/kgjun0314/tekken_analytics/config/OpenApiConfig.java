package io.github.kgjun0314.tekken_analytics.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;


@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Tekken Analytics API",
                version = "v1",
                description = """
                        철권8 리플레이 데이터를 수집하고 분석하는 REST API입니다.
                        
                        주요 기능
                        - 플레이어 전적 조회
                        - 캐릭터 승률 조회
                        - 캐릭터 상성 조회
                        """,
                contact = @Contact(
                        name = "Gyeong-Jun Kim",
                        url = "https://github.com/kgjun0314"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Local Server"
                )
        }
)
public class OpenApiConfig {
}