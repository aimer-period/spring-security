package com.demo.springsecurity.config;

import com.demo.springsecurity.filter.JwtAuthenticationTokenFilter;
import com.demo.springsecurity.service.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
//开启权限注解控制
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Lazy
    @Autowired
    UserDetailServiceImpl userDetailService;


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 基础配置
        http.csrf().disable()                                                               // 关闭csrf
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)     // 指定session的创建策略，不使用session
                .and()                                                                          // 再次获取到HttpSecurity对象
                .addFilterBefore(new JwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()                                                            // 进行认证请求的配置
                .antMatchers("/login").anonymous()                                         // 对于登录接口，允许匿名访问
                .anyRequest().authenticated();
        return http.build();
    }



    /**
     * 白名单
     * @return
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/ignore1", "/ignore2");
    }

//    @Bean
//    UserDetailsService userDetailsService() {
//        InMemoryUserDetailsManager users = new InMemoryUserDetailsManager();
//        users.createUser(User.withUsername("root").password("{noop}123").roles("admin")
//                .authorities("user:query").
//                build());
//        users.createUser(User.withUsername("江南一点雨").password("{noop}123").roles("admin").build());
//        return users;
//    }

    /**
     *构造一个AuthenticationManager，使用自定义的userDetailsService和passwordEncoder
     */
    @Bean
    AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
        return authenticationManager;
    }

    /**
     * 加密
     * @return 加密对象
     * 如需使用自定义密码凭证匹配器 返回自定义加密对象
     * 例如: return new MD5PasswordEncoder();
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        //Spring Security 自带
        return new BCryptPasswordEncoder();
    }

}
