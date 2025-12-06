package org.example.service;

import org.example.config.SeedProperties;
import org.example.database.entity.DocEntity;
import org.example.database.entity.UserEntity;
import org.example.database.repository.DocsRepository;
import org.example.database.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Database seeder for generating random test data at application startup.
 * Only runs if seeding is enabled and database is empty enough.
 * Intended for student/test environments with fresh databases.
 */
@Component
public class DatabaseSeeder {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final SeedProperties seedProperties;
    private final UserRepository userRepository;
    private final DocsRepository docsRepository;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random();

    // Realistic first names and last names for generating usernames
    private static final String[] FIRST_NAMES = {
        "alex", "maria", "john", "anna", "david", "sarah", "michael", "emily",
        "james", "lisa", "robert", "jennifer", "william", "jessica", "richard", "susan"
    };

    private static final String[] LAST_NAMES = {
        "smith", "johnson", "williams", "brown", "jones", "garcia", "miller", "davis",
        "rodriguez", "martinez", "hernandez", "lopez", "wilson", "anderson", "thomas", "taylor"
    };

    private static final String[] DOC_NAMES = {
        "contract", "invoice", "report", "proposal", "agreement", "memo", "letter",
        "presentation", "manual", "guide", "specification", "analysis", "summary"
    };

    private static final String[] DOC_EXTENSIONS = {
        ".pdf", ".docx", ".xlsx", ".txt", ".csv"
    };

    @Autowired
    public DatabaseSeeder(SeedProperties seedProperties, UserRepository userRepository,
                         DocsRepository docsRepository, PasswordEncoder passwordEncoder) {
        this.seedProperties = seedProperties;
        this.userRepository = userRepository;
        this.docsRepository = docsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Seed database after application is ready (Flyway migrations completed).
     */
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seedDatabase() {
        if (!seedProperties.isEnabled()) {
            logger.debug("Database seeding is disabled");
            return;
        }

        logger.info("Checking if database seeding is needed...");

        // Check if database is empty enough
        long userCount = userRepository.count();
        long docCount = docsRepository.count();

        // Only seed if database is relatively empty (less than 5 users and 50 documents)
        if (userCount >= 5 || docCount >= 50) {
            logger.info("Database already contains data (users: {}, documents: {}). Skipping seeding.", 
                        userCount, docCount);
            return;
        }

        logger.info("Database appears empty. Starting seeding process...");
        logger.info("Seeding configuration: users={}, docsPerUser={}, maxVersion={}, daysRange={}",
                   seedProperties.getUsers(), seedProperties.getDocsPerUser(),
                   seedProperties.getMaxDocVersion(), seedProperties.getDaysRange());

        List<UserEntity> createdUsers = seedUsers();
        seedDocuments(createdUsers);

        logger.info("Database seeding completed successfully. Created {} users with documents.",
                   createdUsers.size());
    }

    /**
     * Generate random users.
     */
    private List<UserEntity> seedUsers() {
        List<UserEntity> users = new ArrayList<>();
        int userCount = seedProperties.getUsers();

        for (int i = 0; i < userCount; i++) {
            String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
            String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            String username = firstName + "_" + lastName + "_" + (i + 1);
            String email = username + "@student.test";
            // Password format: student{N}pass (e.g., student1pass, student2pass)
            // This makes it easy for students to test with seeded users
            String password = "student" + (i + 1) + "pass";

            // Check if username already exists (shouldn't happen in fresh DB, but be safe)
            if (userRepository.existsByUsername(username)) {
                username = username + "_" + System.currentTimeMillis();
                email = username + "@student.test";
            }

            UserEntity user = new UserEntity();
            user.setUsername(username);
            user.setPasswordHash(passwordEncoder.encode(password));
            user.setEmail(email);
            user.setRole(random.nextDouble() < 0.1 ? "ROLE_ADMIN" : "ROLE_USER"); // 10% chance of admin
            user.setCreatedAt(generateRandomTimestamp());

            UserEntity saved = userRepository.save(user);
            users.add(saved);
        }

        logger.info("Created {} users", users.size());
        return users;
    }

    /**
     * Generate random documents for users.
     */
    private void seedDocuments(List<UserEntity> users) {
        int docsPerUser = seedProperties.getDocsPerUser();
        int maxVersion = seedProperties.getMaxDocVersion();
        int totalDocs = 0;

        for (UserEntity user : users) {
            int userDocCount = docsPerUser + random.nextInt(20) - 10; // ±10 variation
            if (userDocCount < 0) userDocCount = 0;

            for (int i = 0; i < userDocCount; i++) {
                DocEntity doc = new DocEntity();
                
                // Generate random document name
                String docName = DOC_NAMES[random.nextInt(DOC_NAMES.length)] + "_" +
                               random.nextInt(1000) + 
                               DOC_EXTENSIONS[random.nextInt(DOC_EXTENSIONS.length)];
                doc.setName(docName);

                // Generate random document content
                byte[] content = ("Random content " + UUID.randomUUID())
                        .getBytes(StandardCharsets.UTF_8);
                doc.setDocument(content);

                // Random status (70% UPLOADED, 30% SIGNED)
                String status = random.nextDouble() < 0.7 ? "UPLOADED" : "SIGNED";
                doc.setStatus(status);

                // Random version (1 to maxDocVersion)
                doc.setVersion(1 + random.nextInt(maxVersion));

                // Set uploadedBy to username
                doc.setUploadedBy(user.getUsername());

                // Random createdAt within daysRange
                doc.setCreatedAt(generateRandomTimestamp());

                docsRepository.save(doc);
                totalDocs++;
            }
        }

        logger.info("Created {} documents for {} users", totalDocs, users.size());
    }

    /**
     * Generate a random timestamp within the configured days range.
     */
    private Instant generateRandomTimestamp() {
        int daysRange = seedProperties.getDaysRange();
        Instant now = Instant.now();
        Instant past = now.minus(daysRange, ChronoUnit.DAYS);
        
        long secondsBetween = ChronoUnit.SECONDS.between(past, now);
        long randomSeconds = random.nextLong(secondsBetween);
        
        return past.plusSeconds(randomSeconds);
    }
}

