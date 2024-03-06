package com.hji.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{ // 뷰 컨트롤러의 역할을 수행하는 구성 클래스이다. WebMvcConfigurer 인터페이스는 스프링 MVC를 구성하는 메서드를 정의한다.
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry){ // ViewControllerRegistry : 하나 이상의 뷰 컨트롤러를 등록하기 위해 사용할 수 있다.
        registry.addViewController("/").setViewName("home"); // "/"경로의 요청이 전달되어야 하는 뷰로 "home"을 지정한다.
    }
}
