package tn.ucar.enicar.info.projetspring.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tn.ucar.enicar.info.projetspring.sevices.UserService;

@Component
@RequiredArgsConstructor
public class ApplicationStartupRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartupRunner.class);
    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Application started, triggering score calculation for all VOLUNTARY users");
        userService.calculateAllVolunteerScores();
        logger.info("Score calculation completed on startup");
    }
}