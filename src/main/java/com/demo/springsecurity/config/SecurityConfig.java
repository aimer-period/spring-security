package com.demo.springsecurity.config;

import com.demo.springsecurity.filter.JwtAuthenticationTokenFilter;
import com.demo.springsecurity.filter.TokenAuthenticationFilter;
import com.demo.springsecurity.filter.TokenLoginFilter;
import com.demo.springsecurity.security.DefaultPasswordEncoder;
import com.demo.springsecurity.security.TokenLogoutHandler;
import com.demo.springsecurity.security.TokenManager;
import com.demo.springsecurity.security.UnauthorizedEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
//开启权限注解控制
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private UserDetailsService userDetailsService;
    private TokenManager tokenManager;
    private DefaultPasswordEncoder defaultPasswordEncoder;
    private RedisTemplate redisTemplate;


    @Autowired
    public SecurityConfig(@Lazy UserDetailsService userDetailsService, DefaultPasswordEncoder defaultPasswordEncoder,
                          TokenManager tokenManager, RedisTemplate redisTemplate) {
        this.userDetailsService = userDetailsService;
        this.defaultPasswordEncoder = defaultPasswordEncoder;
        this.tokenManager = tokenManager;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 配置设置
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(jwtAuthenticationTokenFilter(),
                UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling()
                //认证失败处理器
                .authenticationEntryPoint(new UnauthorizedEntryPoint())
                // 授权失败处理类
                //.accessDeniedHandler(new CustomerAccessDeniedHandler())
                // CSRF禁用，因为不使用session
                .and().csrf().disable()
                // 基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                // 过滤请求
                .authorizeRequests()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated()
                // 退出登录请求
                .and().logout().logoutUrl("/admin/acl/index/logout")
                // 退出登录处理器
                .addLogoutHandler(new TokenLogoutHandler(tokenManager, redisTemplate)).and()
                // 登录拦截器
                .addFilter(new TokenLoginFilter(authenticationManager(), tokenManager, redisTemplate))
                // 访问拦截器
                .addFilter(new TokenAuthenticationFilter(authenticationManager(), tokenManager, redisTemplate)).httpBasic();
    }

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter();
    }

    /**
     * 密码处理
     * @param auth
     * @throws Exception
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(defaultPasswordEncoder);
    }

    /**
     * 白名单：配置哪些请求不拦截
     * @return
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/api/**", "/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**",
                "/doc.html/**");
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
