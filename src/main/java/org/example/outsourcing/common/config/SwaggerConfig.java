package org.example.outsourcing.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {

	private final ObjectMapper objectMapper;

	@PostConstruct
	public void init() {
		ModelConverters.getInstance().addConverter(new ModelResolver(objectMapper));
	}

	@Bean
	public OpenAPI openApi() {
		return new OpenAPI()
			.addSecurityItem(new SecurityRequirement().addList("bearer-key").addList("Refresh-Token"))
			.components(new Components()
				.addSecuritySchemes("bearer-key", new SecurityScheme()
					.type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT"))
				.addSecuritySchemes("Refresh-Token", new SecurityScheme()
					.type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.HEADER)
					.name("Refresh-Token")
					.description("Bearer 를 붙여서 넣어주어야 합니다. ex) Bearer xxxxxx"))
			)
			.info(new Info()
				.title("out-sourcing")
				.version("1.0")
				.description(
					"과제의 민족 팀 프로젝트 과제  \n" +
						"[▶ Google OAuth2 로그인](http://localhost:8080/api/auth/oauth2/signin/google)"
				)
			)
			.externalDocs(new ExternalDocumentation()
				.description("Google Sign-in 바로가기")
				.url("http://localhost:8080/api/auth/oauth2/signin/google")
			);
	}
}


