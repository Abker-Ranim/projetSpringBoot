package tn.ucar.enicar.info.projetspring;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import tn.ucar.enicar.info.projetspring.auth.AuthenticationRequest;
import tn.ucar.enicar.info.projetspring.auth.AuthenticationService;
import tn.ucar.enicar.info.projetspring.auth.RegisterRequest;
import tn.ucar.enicar.info.projetspring.entities.Role;
import tn.ucar.enicar.info.projetspring.repositories.userRepo;


@SpringBootApplication
public class ProjetspringApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjetspringApplication.class, args);
    }


    @Bean
    public CommandLineRunner commandLineRunner(
            AuthenticationService service,
            userRepo userRepository
    ) {
        return args -> {
            String adminEmail = "admin@mail.com";
            String adminPassword = "password"; // Store securely in production!

            // Check if admin user exists
            if (!userRepository.findByEmail(adminEmail).isPresent()) {
                // Create new admin user
                var admin = RegisterRequest.builder()
                        .firstname("Admin")
                        .lastname("Admin")
                        .email(adminEmail)
                        .password(adminPassword)
                        .role(Role.ADMIN)
                        .build();
                System.out.println("New admin created. Admin token: " + service.register(admin).getAccessToken());
            } else {
                // Authenticate existing admin user
                var authRequest = AuthenticationRequest.builder()
                        .email(adminEmail)
                        .password(adminPassword)
                        .build();
                try {
                    System.out.println("Admin already exists. New admin token: " + service.authenticate(authRequest).getAccessToken());
                } catch (Exception e) {
                    System.out.println("Failed to authenticate admin user: " + e.getMessage());
                }
            }
        };
    }

}