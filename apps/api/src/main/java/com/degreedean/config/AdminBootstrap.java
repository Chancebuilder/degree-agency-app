package com.degreedean.config;

import com.degreedean.identity.UserAccount;
import com.degreedean.identity.UserAccountRepository;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class AdminBootstrap implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);
    private final UserAccountRepository users;
    private final PasswordEncoder encoder;

    public AdminBootstrap(UserAccountRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        users.findByEmailIgnoreCase("dean@thedegreeagency.com").ifPresentOrElse(
                existing -> {},
                () -> {
                    Instant now = Instant.now();
                    UserAccount admin = new UserAccount();
                    admin.setId(UUID.randomUUID());
                    admin.setEmail("dean@thedegreeagency.com");
                    admin.setPasswordHash(encoder.encode("ChangeMeNow!"));
                    admin.setRole("ADMIN");
                    admin.setTier("PRO");
                    admin.setCreatedAt(now);
                    admin.setUpdatedAt(now);
                    users.save(admin);
                    log.info("Seeded local admin dean@thedegreeagency.com — change the password before any shared environment");
                }
        );
    }
}
