package ru.avselectro.webapi1c;

import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ru.avselectro.webapi1c.domain.WebAPIRequest;
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
			userService.saveRole(new WebAPIRole(null, "ROLE_ADMIN"));
			
			userService.saveUser(new WebAPIUser(null, "admin_avs", "admin_avs", "asDfgK001hg", new ArrayList<>()));
			//userService.saveUser(new WebAPIUser(null, "user_test", "user_test", "ds23xcvuy", new ArrayList<>()));
			
			userService.addRoleToUser("admin_avs", "ROLE_ADMIN");			
			
			
		};
	}

	//private final UserDetailsService userDetailsService;
	//private final BCryptPasswordEncoder bCryptPasswordEncoder;


}
