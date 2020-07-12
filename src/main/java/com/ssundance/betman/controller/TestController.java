package com.ssundance.betman.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController {
	@RequestMapping("/welcome.do")
	public String welcome(Model model) throws Exception {
	    model.addAttribute("greeting", "Hello Thymeleaf!");
	    return "thymeleaf/welcome";
	}

	@RequestMapping("/sample")
	public String sample(Model model) throws Exception {
	    model.addAttribute("greeting", "Hello Thymeleaf!");
	    return "test";

	}
}

