package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.services.UserService;
import com.nnk.springboot.validator.UserValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Controller
public class UserController {

	private static final Logger logger = LogManager.getLogger("UserController");

	@Autowired
	private UserService userService;

	@Autowired
	private UserValidator validator;

	@RolesAllowed("ADMIN")
	@GetMapping("/user/list")
	public String home(@NotNull Model model) {
		model.addAttribute("users", userService.findAll());
		logger.info("Show list users");
		return "user/list";
	}

	@GetMapping("/user/add")
	public String addUser(User user) {
		return "user/add";
	}

	@PostMapping("/user/validate")
	public String validate(@Valid User user, BindingResult result, Model model) {

		// Validation
		validator.validate(user, result);
		if(!result.hasErrors())
		{
			validator.validate(user, result);
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			user.setPassword(encoder.encode(user.getPassword()));
			userService.save(user);
			model.addAttribute("users", userService.findAll());
			return "redirect:/user/list";
		}
		return "user/add";

	}

	@GetMapping("/user/update/{id}")
	public String showUpdateForm(@PathVariable("id") Integer id, Model model) {

		User user = userService.findById(id);
		user.setPassword("");
		model.addAttribute("user", user);
		return "user/update";

	}

	@PostMapping("/user/update/{id}")
	public String updateUser(@PathVariable("id") Integer id, @Valid User user,
	                         BindingResult result, Model model) {

		// Validation
		validator.validate(user, result);

		if(result.hasErrors())
		{
			return "user/update";
		}

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		user.setPassword(encoder.encode(user.getPassword()));
		user.setId(id);
		userService.save(user);
		model.addAttribute("users", userService.findAll());
		return "redirect:/user/list";

	}

	@GetMapping("/user/delete/{id}")
	public String deleteUser(@PathVariable("id") Integer id, Model model) {

		User user = userService.findById(id);
		userService.delete(user);
		model.addAttribute("users", userService.findAll());
		return "redirect:/user/list";
	}

}
