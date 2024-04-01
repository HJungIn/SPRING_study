package com.hji.spring.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hji.spring.model.Ingredient;
import com.hji.spring.model.Order;
import com.hji.spring.model.Taco;
import com.hji.spring.model.User;
import com.hji.spring.model.Ingredient.Type;
import com.hji.spring.repository.IngredientRepository;
import com.hji.spring.repository.TacoRepository;
import com.hji.spring.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j // 컴파일 시에 Lombok에 제공됨 => log 사용 가능
@Controller // 컨트롤러로 식별되게 함, 스프링 애플리케이션 컨텍스트의 빈으로 이 클래스의 인스턴스를 자동 생성한다.
@RequestMapping("/design") // 해당 경로의 요청을 처리함.
public class DesignTacoController {

    private final IngredientRepository ingredientRepository;

    private final TacoRepository tacoRepo;

    private final UserRepository userRepo;

    @Autowired
    public DesignTacoController(IngredientRepository ingredientRepository, TacoRepository tacoRepository, UserRepository userRepository){
        this.ingredientRepository = ingredientRepository;
        this.tacoRepo = tacoRepository;
        this.userRepo = userRepository;
    }

    @GetMapping
    public String showDesignForm(Model model, Principal principal) { // Model : 컨트롤러와 뷰 사이에서 데이터를 운반하는 객체 -> Model 객체의 속성에 있는 데이터는 뷰가 알 수 있는
                                                // 서블릿 요청 속성들로 복사된다.

        List<Ingredient> ingredients = new ArrayList<>();
        ingredientRepository.findAll().forEach(i -> ingredients.add(i));

        Type[] types = Ingredient.Type.values();
        for (Type type : types) {
            model.addAttribute(type.toString().toLowerCase(),
                    filterByType(ingredients, type));
        }

        String username = principal.getName();
        User user = userRepo.findByUsername(username);
        model.addAttribute("user", user);
        return "design";

    }

    private List<Ingredient> filterByType(List<Ingredient> ingredients, Type type) {
        return ingredients
                .stream()
                .filter(x -> x.getType().equals(type))
                .collect(Collectors.toList());
    }

    @ModelAttribute(name = "order")
	public Order order() {
		return new Order();
	}
	
	@ModelAttribute(name = "taco")
	public Taco taco() {
		return new Taco();
	}

	@PostMapping
	public String processDesign(@Valid Taco design, Errors errors, @ModelAttribute Order order) {
		if (errors.hasErrors()) {
			return "design";
		}
		
		Taco saved = tacoRepo.save(design);
		order.addDesign(saved);
		
		return "redirect:/orders/current";
	}
}
