package com.example.cloudservicebackend;

import com.example.cloudservicebackend.config.container.PostgresContainer;
import com.example.cloudservicebackend.config.container.ServiceContainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ContainersBaseTest {
    private static final Network NETWORK = Network.newNetwork();
    @Container
    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER = PostgresContainer.getInstance(NETWORK);
    @Container
    private static final GenericContainer<?> SERVICE_CONTAINER = ServiceContainer.getInstance(POSTGRES_CONTAINER, NETWORK);

    public static PostgreSQLContainer<?> getPostgresContainer() {
        return POSTGRES_CONTAINER;
    }

    public static GenericContainer<?> getServiceContainer() {
        return SERVICE_CONTAINER;
    }
}
