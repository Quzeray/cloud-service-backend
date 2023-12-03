package com.example.cloudservicebackend.config.container;

import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresContainer extends PostgreSQLContainer<PostgresContainer> {
    private static final int POSTGRES_PORT = 5432;
    private static final String POSTGRES_IMAGE = "postgres:latest";
    private static final String POSTGRES_CONTAINER_NAME = "database";
    private static final String POSTGRES_DATABASE_NAME = "cloud";
    private static final String POSTGRES_USERNAME = "postgres";
    private static final String POSTGRES_PASSWORD = "postgres";
    private static PostgresContainer postgresContainer;

    private PostgresContainer(String image) {
        super(image);
    }

    public static PostgresContainer getInstance(Network network) {
        if (postgresContainer == null) {
            postgresContainer = new PostgresContainer(POSTGRES_IMAGE)
                    .withDatabaseName(POSTGRES_DATABASE_NAME)
                    .withUsername(POSTGRES_USERNAME)
                    .withPassword(POSTGRES_PASSWORD)
                    .withCreateContainerCmdModifier(cmd -> cmd.withName(POSTGRES_CONTAINER_NAME))
                    .withNetwork(network)
                    .withExposedPorts(POSTGRES_PORT);
        }
        postgresContainer.start();
        return postgresContainer;
    }

}