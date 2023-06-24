package com.mkh;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import  java.sql.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TwitterServer {
    private static final Logger logger = Logger.getLogger(TwitterServer.class.getName());

    private final int port;
    private final Server server;
    private final Connection databaseConnection;

    public TwitterServer(int port) throws SQLException {
        this(Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create()), port);
    }

    public TwitterServer(ServerBuilder<?> serverBuilder, int port) throws SQLException {
        this.port = port;
        // Preferred: DataSource instead of DriverManager.
        databaseConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/twitterdb", "shared_user", "1234");
        server = serverBuilder.addService(new TwitterService(databaseConnection)).build();
    }

    /** Start serving requests. */
    public void start() throws IOException {
        server.start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    TwitterServer.this.stop();
                } catch (InterruptedException | SQLException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    /** Stop serving requests and shutdown resources. */
    public void stop() throws InterruptedException, SQLException {
        if (server != null) {
            // Throws SQLException.
            databaseConnection.close();
            // Throws InterruptedException.
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Main method. This comment makes the linter happy.
     */
    public static void main(String[] args) throws Exception {
        TwitterServer server = new TwitterServer(8080);
        server.start();
        server.blockUntilShutdown();
    }
}
