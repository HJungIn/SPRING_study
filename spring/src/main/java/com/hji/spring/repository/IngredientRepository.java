package com.hji.spring.repository;

import org.springframework.data.repository.CrudRepository;

import com.hji.spring.model.Ingredient;

public interface IngredientRepository extends CrudRepository<Ingredient, String>{ // CrudRepository 인터페이스에는 DB의 CRUD 연산을 위한 많은 메서드가 선언되어 있다.
}
