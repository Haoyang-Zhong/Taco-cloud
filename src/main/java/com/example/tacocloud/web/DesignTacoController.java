package com.example.tacocloud.web;


import com.example.tacocloud.Ingredient;
import com.example.tacocloud.Order;
import com.example.tacocloud.Taco;
import com.example.tacocloud.User;
import com.example.tacocloud.data.IngredientRepository;
import com.example.tacocloud.data.TacoRepository;
import com.example.tacocloud.data.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignTacoController {
	//private static final Logger log = LoggerFactory.getLogger(DesignTacoController.class);
	private final IngredientRepository ingredientRepository;

	private TacoRepository tacoRepo;

	private UserRepository userRepo;

	@Autowired
	public DesignTacoController(IngredientRepository ingredientRepository, TacoRepository tacoRepo, UserRepository userRepo){
		this.ingredientRepository = ingredientRepository;
		this.tacoRepo = tacoRepo;
		this.userRepo = userRepo;
	}

	@ModelAttribute(name = "order")
	public Order order() {
		return new Order();
	}

	@ModelAttribute(name = "design")
	public Taco design() {
		return new Taco();
	}

	@GetMapping
	public String showDesignForm(Model model, Principal principal) {
		log.info("     ------ Design taco");
		List<Ingredient> ingredients = new ArrayList<>();
		ingredientRepository.findAll().forEach(i -> ingredients.add(i));
		Ingredient.Type[] types = Ingredient.Type.values();
		for (Ingredient.Type type : types){
			model.addAttribute(type.toString().toLowerCase(), filterByType(ingredients, type));
		}

		String username = principal.getName();
		User user = userRepo.findByUsername(username);
		model.addAttribute("user", user);

		return "design";
	}



	@PostMapping
	public String processDesign(
			@Valid Taco taco, Errors errors,
			@ModelAttribute Order order) {

		log.info("   --- Saving taco");

		if (errors.hasErrors()) {
			return "design";
		}

		Taco saved = tacoRepo.save(taco);
		order.addDesign(saved);

		return "redirect:/orders/current";
	}

	private List<Ingredient> filterByType(
			List<Ingredient> ingredients, Ingredient.Type type) {
		return ingredients
				.stream()
				.filter(x -> x.getType().equals(type))
				.collect(Collectors.toList());
	}


}
