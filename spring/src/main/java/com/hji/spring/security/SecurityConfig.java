package com.hji.spring.security;

import java.io.PrintWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.ErrorResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hji.spring.service.UserRepositoryUserDetailsService;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final UserRepositoryUserDetailsService myUserDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    PasswordEncoder passwordEncoder() { // 비밀번호가 암호화되어 DB에 저장될 수 있도록 비밀번호 인코더를 전달
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(Customizer.withDefaults())
            .formLogin(Customizer.withDefaults())
            .authorizeHttpRequests(authorizeRequest ->
                    authorizeRequest
                            .requestMatchers(AntPathRequestMatcher.antMatcher("/design")).hasAnyRole("USER")
                            .requestMatchers(AntPathRequestMatcher.antMatcher("/orders")).hasAnyRole("USER")
                            .requestMatchers(
                                    AntPathRequestMatcher.antMatcher("/h2-console/**")
                            ).permitAll()
                            .requestMatchers(AntPathRequestMatcher.antMatcher("/**")).permitAll()
            )
            .headers(
                    headersConfigurer ->
                            headersConfigurer
                                    .frameOptions(
                                            HeadersConfigurer.FrameOptionsConfig::sameOrigin
                                    )
            )
            .formLogin((formLogin) -> formLogin
                    .loginPage("/login")
                    .loginProcessingUrl("/authentication")
                    .defaultSuccessUrl("/design", true)
                    .failureUrl("/member/signin?error")
            )
            .logout(logoutConfig -> logoutConfig.logoutSuccessUrl("/"))

            .addFilterBefore(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
   
    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter() throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager());
        // "/authentication" 엔드포인트로 들어오는 요청을 CustomAuthenticationFilter에서 처리하도록 지정한다.
        customAuthenticationFilter.setFilterProcessesUrl("/authentication");

        /**
         *  spring security 6.x 버전으로 업한뒤 문제점 : 로그인 후 SecurityContextHolder.getContext().getAuthentication()의 유지가 안됨.
         *      => 로그인 후 페이지 전환 시 SecurityContextHolder.getContext().getAuthentication() = null 로 나오는 현상
         *  
         *  해결방법 : setSecurityContextRepository 설정
         * */
        customAuthenticationFilter.setSecurityContextRepository(new DelegatingSecurityContextRepository(
                new HttpSessionSecurityContextRepository(),
                new RequestAttributeSecurityContextRepository()
        ));
        customAuthenticationFilter.afterPropertiesSet();
        return customAuthenticationFilter;
    }

   /**
     * authenticate 의 인증 메서드를 제공하는 매니져로'Provider'의 인터페이스를 의미한다.
     * 이 메서드는 인증 매니저를 생성한다. 인증 매니저는 인증 과정을 처리하는 역할을 한다.
     */
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(myUserDetailsService);
    }

/*
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrfConfig) -> csrfConfig.disable())
                .headers(
                        (headerConfig) -> headerConfig.frameOptions(frameOptionsConfig -> frameOptionsConfig.disable()))
                .authorizeHttpRequests((authorizeRequests) -> authorizeRequests
                        .requestMatchers(PathRequest.toH2Console()).permitAll())
                .exceptionHandling((exceptionConfig) -> exceptionConfig //  // 401 403 관련 예외처리
                        .authenticationEntryPoint(unauthorizedEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .formLogin((formLogin) -> formLogin
                        .loginPage("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .loginProcessingUrl("/login/login-proc")
                        .defaultSuccessUrl("/", true))
                .logout(logoutConfig -> logoutConfig.logoutSuccessUrl("/"))
                .userDetailsService(myUserDetailsService);

        return http.build();
    }

    private final AuthenticationEntryPoint unauthorizedEntryPoint = (request, response, authException) -> {
        ErrorResponse fail = new ErrorResponse(HttpStatus.UNAUTHORIZED, "Spring security unauthorized...");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        String json = new ObjectMapper().writeValueAsString(fail);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.flush();
    };

    private final AccessDeniedHandler accessDeniedHandler = (request, response, accessDeniedException) -> {
        ErrorResponse fail = new ErrorResponse(HttpStatus.FORBIDDEN, "Spring security forbidden...");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        String json = new ObjectMapper().writeValueAsString(fail);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.flush();
    };
    

    @Getter
    @RequiredArgsConstructor
    public class ErrorResponse {

        private final HttpStatus status;
        private final String message;
    }
*/
}
