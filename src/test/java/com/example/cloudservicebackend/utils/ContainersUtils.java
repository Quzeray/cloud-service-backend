package com.example.cloudservicebackend.utils;

import org.springframework.beans.factory.annotation.Value;
import org.testcontainers.containers.PostgreSQLContainer;

public class ContainersUtils {
    @Value("${container.postgres.database}")
    private static String DATABASE_NAME;
    @Value("${container.postgres.username}")
    private static String USERNAME;
    @Value("${container.postgres.password}")
    private static String PASSWORD;

    public static void configurePostgresContainer(PostgreSQLContainer<?> postgresContainer) {
        postgresContainer.withDatabaseName(DATABASE_NAME);
        postgresContainer.withUsername(USERNAME);
        postgresContainer.withPassword(PASSWORD);
    }
}
