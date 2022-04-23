package com.example.istar;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author tian
 */
@SpringBootApplication
@EnableSwagger2
@MapperScan("com.example.istar.mapper")
public class IstarApplication {

    public static void main(String[] args) {
        SpringApplication.run(IstarApplication.class, args);
    }

}
