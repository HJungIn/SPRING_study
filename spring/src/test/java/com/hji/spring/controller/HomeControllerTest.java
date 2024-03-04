
package com.hji.spring.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

// 스프링 MVC 애플리케이션의 형태로 테스트가 실행되도록 한다 => HomeController가 스프링 MVC에 등록되므로 우리가 스프링 MVC에 웹 요청을 보낼 수 있다.
@WebMvcTest(HomeController.class)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc; // 실제서버를 시작하는 대신 스프링 MVC의 모의 테스트를 하기 위한 역할

    @Test
    public void testHomePage() throws Exception {
        mockMvc.perform(get("/")) // 루트 경로인 HTTP GET 요청
                .andExpect(status().isOk()) // 응답은 HTTP 200 상태가 되어야 함
                .andExpect(view().name("home")) // 뷰의 이름은 home이여야 함
                .andExpect(content().string(containsString("Welcome to ..."))); // 브라우저에 보이는 뷰는 'Welcome to ...'라는 텍스트가 포함되어야 함
        // 세 가지 중 하나라도 충족하지 않으면 테스트는 실패함.
    }
}
