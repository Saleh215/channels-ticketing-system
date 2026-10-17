package com.channels.ticketing.user;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SeedUsers {
    @Bean
    CommandLineRunner seed(UserRepository users, PasswordEncoder encoder) {
        return args -> {
            add(users, encoder, "customer", "Customer123!", Role.CUSTOMER);
            add(users, encoder, "agent", "Agent123!", Role.AGENT);
            add(users, encoder, "monitor", "Monitor123!", Role.MONITOR);
        };
    }

    private void add(UserRepository repo, PasswordEncoder encoder, String name, String password, Role role) {
        if (repo.findByUsername(name).isEmpty()) repo.save(new AppUser(name, encoder.encode(password), role));
    }
}
