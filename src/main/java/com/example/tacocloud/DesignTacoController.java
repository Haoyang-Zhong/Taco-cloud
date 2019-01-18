package com.example.tacocloud;


import com.example.tacocloud.data.TacoRepository;
import com.sun.org.apache.xpath.internal.operations.Or;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import com.example.tacocloud.Ingredient;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignTacoController {
	//private static final Logger log = LoggerFactory.getLogger(DesignTacoController.class);
	private final IngredientRepository ingredientRepository;

	private TacoRepository designRepo;

	@Autowired
	public DesignTacoController(IngredientRepository ingredientRepository, TacoRepository designRepo){
		this.ingredientRepository = ingredientRepository;
		this.designRepo = designRepo;
	}

	@GetMapping
	public String showDesignForm(Model model) {
		List<Ingredient> ingredients = new ArrayList<>();
		ingredientRepository.findAll().forEach(i -> ingredients.add(i));
		Ingredient.Type[] types = Ingredient.Type.values();
		for (Ingredient.Type type : types){
			model.addAttribute(type.toString().toLowerCase(), filterByType(ingredients, type));
		}

		return "design";
	}

	@ModelAttribute(name = "order")
	public Order order(){
		return new Order();
	}

	@ModelAttribute(name = "taco")
	public Taco taco(){
		return new Taco();
	}



	@PostMapping
	public String processingDesign(@Valid Taco design, Errors errors, @ModelAttribute Order order){
		if (errors.hasErrors()) {
			return "design";
		}
		Taco saved = designRepo.save(design);
		order.addDesign(saved);

		return "redirect:/orders/current";
	}

	public List<Ingredient> filterByType(List<Ingredient> list, Ingredient.Type type){
		return list.stream().filter(item -> item.getType()== type).collect(Collectors.toList());
	}


}
