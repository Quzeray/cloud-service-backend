package com.example.cloudservicebackend.config.container;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;

import static java.util.Map.of;

public class ServiceContainer extends GenericContainer<ServiceContainer> {
    private static final int SERVICE_PORT = 8081;
    private static final String SERVICE_IMAGE = "backend:latest";
    private static final String SERVICE_CONTAINER_NAME = "backend";
    private static ServiceContainer serviceContainer;

    private ServiceContainer(String image) {
        super(image);
    }

    public static ServiceContainer getInstance(PostgreSQLContainer<?> postgresContainer, Network network) {
        if (serviceContainer == null) {
            serviceContainer = new ServiceContainer(SERVICE_IMAGE)
                    .withCreateContainerCmdModifier(cmd -> cmd.withName(SERVICE_CONTAINER_NAME))
                    .withNetwork(network)
                    .withExposedPorts(SERVICE_PORT)
                    .withEnv(of(
                            "DB_HOST", postgresContainer.getContainerName().substring(1),
                            "DB_PORT", postgresContainer.getExposedPorts().get(0).toString(),
                            "DB_NAME", postgresContainer.getDatabaseName(),
                            "DB_USERNAME", postgresContainer.getUsername(),
                            "DB_PASSWORD", postgresContainer.getPassword()
                    ))
                    .dependsOn(postgresContainer);

        }
        serviceContainer.start();
        return serviceContainer;
    }
}