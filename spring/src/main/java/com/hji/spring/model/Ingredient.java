package com.hji.spring.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data // 런타임 시에 getter(), setter() 등의 메소드를 자동으로 생성
@RequiredArgsConstructor // final이 붙은 필드들의 초기화하는 생성자 생성
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true) // JPA에서는 개체가 인자 없는 생성자를 가져야 한다. | AccessLevel.PRIVATE : 클래스 외부에서 사용하지 못하게 함. | force : final 필드가 선언된 경우 컴파일 타임에 기본값을 0 / null / false 로 설정해준다(true의 경우)
@Entity // JPA 개체(entity)로 선언하기 위해 반드시 추가해야함.
public class Ingredient {

    @Id // DB의 개체를 고유하게 식별한다는 것
    private final String id;
    private final String name;
    private final Type type;

    public static enum Type {
        WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
    }
}
