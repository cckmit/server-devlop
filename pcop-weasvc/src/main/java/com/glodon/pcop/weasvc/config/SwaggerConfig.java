package com.glodon.pcop.weasvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * @author Jimmy.Liu(liuzm@glodon.com), Oct/24/2018.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.glodon.pcop.weasvc"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Glodon PCOP Weather Data Services")
                .description("The Weather Data services of Glodon Plan Construction Operation Platform")
                .termsOfServiceUrl("http://pcop.glodon.com/")
                .contact(new Contact("Jimmy", "http://www.glodon.com", "liuzm@glodon.com"))
                .version("0.1")
                .build();
    }
}
