package com.hji.spring.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.hji.spring.model.Ingredient;
import com.hji.spring.repository.IngredientRepository;

@Component
public class IngredientByIdConverter
		implements Converter<String, Ingredient> {
	private IngredientRepository ingredientRepo;
	
	@Autowired
	public IngredientByIdConverter(IngredientRepository ingredientRepo) {
		this.ingredientRepo = ingredientRepo;
	}
	
	@Override
	public Ingredient convert(String id) { //String 타입의 ID를 사용해서 DB에 저장된 특정 데이터를 읽은 후 Ingredient 객체로 변환하기 위해 사용
		Optional<Ingredient> optionalIngredient = ingredientRepo.findById(id);
		return optionalIngredient.isPresent() ?
							optionalIngredient.get() : null;
	}
}
