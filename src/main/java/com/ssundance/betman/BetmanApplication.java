package com.ssundance.betman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class BetmanApplication {

	public static void main(String[] args) {
		SpringApplication.run(BetmanApplication.class, args);
	}

}

//Add the controller.
@RestController
class HelloWorldController {
@GetMapping("/aaa")
public String hello() {
 return "hello world!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!";
}
}