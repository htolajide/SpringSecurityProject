package com.tfkconsult.amigoscode;

import java.util.ArrayList;
import com.tfkconsult.amigoscode.service.UserService;
import com.tfkconsult.amigoscode.domain.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SpringSecurityProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityProjectApplication.class, args);
	}

	@Bean
	PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner run(UserService userService){
		return args -> {
			userService.saveRole(new Role(null, "ROLE_USER"));
			userService.saveRole(new Role(null, "ROLE_MANAGER"));
			userService.saveRole(new Role(null, "ROLE_ADMIN"));
			userService.saveRole(new Role(null, "ROLE_SUPER_ADMIN"));

			userService.saveUser(new AppUser(null, "John Doe", "john", "12345", new ArrayList<>()));
			userService.saveUser(new AppUser(null, "John Kelly", "kelly", "12345", new ArrayList<>()));
			userService.saveUser(new AppUser(null, "Kareem Akeem", "kakeem", "12345", new ArrayList<>()));
			userService.saveUser(new AppUser(null, "Taofeek Hammed", "htolajide", "12345", new ArrayList<>()));

			userService.addRoleUser("john", "ROLE_USER");
			userService.addRoleUser("john", "ROLE_ADMIN");
			userService.addRoleUser("kakeem", "ROLE_SUPER_ADMIN");
			userService.addRoleUser("kakeem", "ROLE_ADMIN");
			userService.addRoleUser("htolajide", "ROLE_ADMIN");
			userService.addRoleUser("htolajide", "ROLE_MANAGER");
		};
	}
}
