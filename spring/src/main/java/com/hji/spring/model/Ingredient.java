package com.hji.spring.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data // 런타임 시에 getter(), setter() 등의 메소드를 자동으로 생성
@RequiredArgsConstructor // final이 붙은 필드들의 초기화하는 생성자 생성
public class Ingredient {
    private final String id;
    private final String name;
    private final Type type;

    public static enum Type {
        WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
    }
}
