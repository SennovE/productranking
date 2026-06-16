package ru.sennov.productranking.grpc;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import ru.sennov.productranking.config.GrpcServerProperties;

@Component
public class GrpcServerLifecycle implements SmartLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcServerLifecycle.class);

    private final GrpcServerProperties properties;
    private final List<BindableService> services;

    private Server server;
    private boolean running;

    public GrpcServerLifecycle(GrpcServerProperties properties, List<BindableService> services) {
        this.properties = properties;
        this.services = services;
    }

    @Override
    public void start() {
        if (!properties.isEnabled() || running) {
            return;
        }

        ServerBuilder<?> builder = ServerBuilder.forPort(properties.getPort());
        services.forEach(builder::addService);
        builder.addService(ProtoReflectionService.newInstance());

        try {
            server = builder.build().start();
            running = true;
            LOGGER.info("gRPC server started on port {}", properties.getPort());
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to start gRPC server on port " + properties.getPort(), exception);
        }
    }

    @Override
    public void stop() {
        if (server == null) {
            running = false;
            return;
        }

        server.shutdown();
        try {
            if (!server.awaitTermination(5, TimeUnit.SECONDS)) {
                server.shutdownNow();
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            server.shutdownNow();
        } finally {
            running = false;
            server = null;
            LOGGER.info("gRPC server stopped");
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
