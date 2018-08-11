package com.acme.checkout.config;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
                .apis(RequestHandlerSelectors.basePackage("com.acme.checkout.api.controllers"))
                .paths(PathSelectors.regex("/api.*"))
                .build()
				.apiInfo(new ApiInfo(
						"ACME Checkout API",
						null,
						"v1",
						null,
						null,
						null,
						null,
						new ArrayList<>()
				))
				.securitySchemes(newArrayList(apiKey()))
				.securityContexts(Lists.newArrayList(securityContext()))
				.tags(
						new Tag("payments", "Operations to manage the payments")
				);
	}

	private SecurityContext securityContext() {
		return SecurityContext.builder().securityReferences(defaultAuth()).forPaths(regex("/api/.*")).build();
	}

	private List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Lists.newArrayList(new SecurityReference("api_key", authorizationScopes));
	}

    private ApiKey apiKey() {
        return new ApiKey("api_key", "Authorization", "header");
    }

}