package ru.avselectro.webapi1c;

import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ru.avselectro.webapi1c.domain.WebAPIRole;
import ru.avselectro.webapi1c.domain.WebAPIUser;
import ru.avselectro.webapi1c.service.UserService;

@SpringBootApplication
public class WebApi1CApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebApi1CApplication.class, args);
	}
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}	
	@Bean
	CommandLineRunner run(UserService userService) {
		return args -> {
			userService.saveRole(new WebAPIRole(null, "ROLE_USER"));
			userService.saveRole(new WebAPIRole(null, "ROLE_MANAGER"));
			userService.saveRole(new WebAPIRole(null, "ROLE_ADMIN"));
			
			userService.saveUser(new WebAPIUser(null, "Kochetov Evgenii", "kochetov", "1234", new ArrayList<>()));
			userService.saveUser(new WebAPIUser(null, "Administrator", "administrator", "we1234w", new ArrayList<>()));
			
			userService.addRoleToUser("kochetov", "ROLE_USER");
			userService.addRoleToUser("administrator", "ROLE_ADMIN");
			
		};
	}

	//private final UserDetailsService userDetailsService;
	//private final BCryptPasswordEncoder bCryptPasswordEncoder;


}
