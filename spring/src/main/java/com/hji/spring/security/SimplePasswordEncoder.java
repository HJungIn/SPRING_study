package com.hji.spring.security;

import org.springframework.security.crypto.password.PasswordEncoder;

public class SimplePasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) { // encode : 해당 암호화 방식으로 암호화한 문자열을 리턴
        return rawPassword.toString();
    }

    /*
     * rawPassword : 사용자가 입력한 비밀번호
     * 
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encodedPassword.equals(encode(rawPassword));
    }
}  