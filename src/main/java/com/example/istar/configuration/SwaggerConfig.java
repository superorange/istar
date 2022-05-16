package com.example.istar.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author tian
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean(value = "api")
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(new ApiInfoBuilder()
                        .title("智慧I-Study")
                        .description("测试swagger2接口文档生成工具")
                        .termsOfServiceUrl("https://www.xx.com/")
//                        .contact("xx@qq.com")

                        .version("3.0")
                        .build())
                //分组名称
                .groupName("istar接口文档")
                .select()
                //这里指定Controller扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.example.istar.controller"))
                .paths(PathSelectors.any())
                .build();

    }


}
