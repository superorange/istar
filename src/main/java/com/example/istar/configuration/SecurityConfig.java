package com.example.istar.configuration;

import com.example.istar.service.impl.MyServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;

/**
 * @author tian
 * springboot  security配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence charSequence) {
                System.out.println("加密字符串:" + charSequence);
                return charSequence.toString();
            }

            @Override
            public boolean matches(CharSequence charSequence, String s) {
                System.out.println("比较字符串:" + charSequence + "：" + s);
                return s.equals(charSequence.toString());
            }
        };
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().access("@myServiceImpl.hasPurchase(http)");
        http.csrf().disable();
    }
}
