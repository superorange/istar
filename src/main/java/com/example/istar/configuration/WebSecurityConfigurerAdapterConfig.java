package com.example.istar.configuration;

import cn.hutool.crypto.digest.MD5;
import com.example.istar.common.PermitUrl;
import com.example.istar.filter.GatewayLogFilter;
import com.example.istar.filter.AuthenticationFilter;
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
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;


/**
 * @author tian
 * springboot  security配置
 * TODO 开启权限认证
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfigurerAdapterConfig extends WebSecurityConfigurerAdapter {
    @Resource
    private AuthenticationFilter authenticationFilter;
    @Resource
    private GatewayLogFilter gatewayLogFilter;
    @Resource
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Resource
    private AccessDeniedHandler accessDeniedHandler;

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence charSequence) {
                return charSequence.toString();
            }

            @Override
            public boolean matches(CharSequence charSequence, String s) {
                return s.equals(MD5.create().digestHex(charSequence.toString()));
            }
        };
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //禁止创建session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
//                .anyRequest().access("@myServiceImpl.hasPurchase(request)");
                .antMatchers(PermitUrl.PERMIT_URL).permitAll()
//                .antMatchers("/user/getUserList").permitAll()
                //放行swagger
//                .antMatchers("/doc.html", "/swagger-ui.html", "/swagger-resources/**", "/webjars/**", "/v2/**", "/api/**", "/v3/**").permitAll()
                .anyRequest().permitAll();
        http.formLogin().disable();
        http.csrf().disable();
        ///TODO 开启自定义的过滤器
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(gatewayLogFilter, ChannelProcessingFilter.class);
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
