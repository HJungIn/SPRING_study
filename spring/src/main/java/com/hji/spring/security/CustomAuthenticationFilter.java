package com.hji.spring.security;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 사용자가 로그인 폼을 통해 제출한 사용자 이름과 비밀번호를 가지고 인증을 시도한다.
 * 인증이 성공하면, 인증된 사용자의 정보와 권한을 담은 Authentication 객체를 생성하여 SecurityContext에 저장한다.
 * 이 필터는 /user/login 엔드포인트로 들어오는 요청을 처리한다.
 * */
@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager){
        super(authenticationManager);
    }


    /**
     * 이 메서드는 사용자가 로그인을 시도할 때 호출된다. (사용자의 로그인 요청을 처리하고 인증을 시도할 때 사용됨)
     * HTTP 요청에서 사용자 이름과 비밀번호를 추출하여 UsernamePasswordAuthenticationToken 객체를 생성하고, 이를 AuthenticationManager에 전달하여 인증을 시도한다.
     * 인증이 성공하면 인증된 사용자의 정보와 권한을 담은 Authentication 객체를 반환하고, 인증이 실패하면 AuthenticationException을 던진다.
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authRequest = null;

        try {
            authRequest = getAuthRequestSimple(request); // simple 한 버전 : getAuthRequestSimple(request);
            setDetails(request, authRequest);
        } catch (Exception e) {
//            throw new ProfileApplicationException(ErrorCode.BUSINESS_EXCEPTION_ERROR);
        }

        System.out.println(authRequest);
        // Authentication 객체를 반환한다.
        return this.getAuthenticationManager().authenticate(authRequest);
    }


    /**
     * 심플한 버전
     */
    private UsernamePasswordAuthenticationToken getAuthRequestSimple(HttpServletRequest request) throws Exception {
        System.out.println("request.getParameter(\"username\") = " + request.getParameter("username"));
        System.out.println("request.getParameter(\"password\") = " + request.getParameter("password"));
        return  new UsernamePasswordAuthenticationToken(request.getParameter("username"), request.getParameter("password"));
    }

}
