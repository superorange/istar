package com.example.istar.configuration;

import com.example.istar.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;


/**
 * @author tian
 * springboot  security配置
 */
@Configuration
@EnableWebSecurity
///TODO 开启权限认证
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfigurerAdapterConfig extends WebSecurityConfigurerAdapter {
    @Resource
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Resource
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Resource
    private AccessDeniedHandler accessDeniedHandler;

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
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().antMatchers("/user/login").anonymous()
                .antMatchers("/user/register").anonymous()
//                .anyRequest().access("@myServiceImpl.hasPurchase(request)");
                .antMatchers("/doc.html").permitAll()
//                .antMatchers("/user/getUserList").permitAll()
                //放行swagger
                .antMatchers("/swagger-ui.html", "/swagger-resources/**", "/webjars/**", "/v2/**", "/api/**", "/v3/**").permitAll()
                .anyRequest().authenticated();
        http.csrf().disable();
        ///TODO 开启自定义的过滤器
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        ///TODO 开启自定义的登录失败处理
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).accessDeniedHandler(accessDeniedHandler);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
