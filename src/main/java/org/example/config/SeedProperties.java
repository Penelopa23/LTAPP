package org.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for database seeding.
 * Controlled via environment variables for student/test environments.
 */
@Component
@ConfigurationProperties(prefix = "ltapp.seed")
public class SeedProperties {
    private boolean enabled = false;
    private int users = 10;
    private int docsPerUser = 50;
    private int maxDocVersion = 5;
    private int daysRange = 30;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getUsers() {
        return users;
    }

    public void setUsers(int users) {
        this.users = users;
    }

    public int getDocsPerUser() {
        return docsPerUser;
    }

    public void setDocsPerUser(int docsPerUser) {
        this.docsPerUser = docsPerUser;
    }

    public int getMaxDocVersion() {
        return maxDocVersion;
    }

    public void setMaxDocVersion(int maxDocVersion) {
        this.maxDocVersion = maxDocVersion;
    }

    public int getDaysRange() {
        return daysRange;
    }

    public void setDaysRange(int daysRange) {
        this.daysRange = daysRange;
    }
}

