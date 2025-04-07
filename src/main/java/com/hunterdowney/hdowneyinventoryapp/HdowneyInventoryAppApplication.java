package com.hunterdowney.hdowneyinventoryapp;

import com.hunterdowney.hdowneyinventoryapp.domain.User;
import com.hunterdowney.hdowneyinventoryapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class HdowneyInventoryAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(HdowneyInventoryAppApplication.class, args);
	}

	@Bean
	public CommandLineRunner initUsers(UserRepository userRepo, PasswordEncoder encoder) {
		return args -> {
			if (userRepo.findByUsername("user").isEmpty()) {
				userRepo.save(new User("user", encoder.encode("password"), "User One", "USER"));
				userRepo.save(new User("assoc", encoder.encode("password"), "Associate One", "ASSOC"));
				userRepo.save(new User("mngr", encoder.encode("password"), "Manager One", "MNGR"));
				userRepo.save(new User("admin", encoder.encode("password"), "Admin One", "ADMIN"));
			}
		};
	}

}
