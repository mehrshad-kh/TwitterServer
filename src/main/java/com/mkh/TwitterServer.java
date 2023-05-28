package com.mkh;

import com.mkh.twitter.*;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.checkerframework.checker.units.qual.A;
import  java.sql.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TwitterServer {
    private static final Logger logger = Logger.getLogger(TwitterServer.class.getName());

    private final int port;
    private final Server server;


    public TwitterServer(int port) throws IOException {
        this(port, "extra");
    }

    public TwitterServer(int port, String extra) {
        this(Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create()), port);
    }

    public TwitterServer(ServerBuilder<?> serverBuilder, int port) {
        this.port = port;
        server = serverBuilder.addService(new TwitterService()).build();
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
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    /** Stop serving requests and shutdown resources. */
    public void stop() throws InterruptedException {
        if (server != null) {
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

    /**
     * Our implementation of Twitter service.
     */
    private static class TwitterService extends TwitterGrpc.TwitterImplBase {
        @Override
        public void authenticate(User user, StreamObserver<AuthResponse> responseObserver) {
            AuthResponse response;
            if (user.getFirstName().equals("Mehrshad")) {
                response = AuthResponse.newBuilder().setResult(AuthResult.GRANTED).build();
            } else {
                response = AuthResponse.newBuilder().setResult(AuthResult.NOT_FOUND).build();
            }

            System.out.println("Responding back...");

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void getDailyBriefing(User user, StreamObserver<Tweet> responseObserver) {
            ArrayList<Tweet> tweets = new ArrayList<>();

            for (int i = 0; i < 3; i++) {
                tweets.add(Tweet.newBuilder().setText("Hello, folks! This is Tweet number " + (i + 1) + "!").build());
            }

            for (Tweet tweet : tweets) {
                responseObserver.onNext(tweet);
            }

            responseObserver.onCompleted();
        }

        @Override
        public void signUp(User user, StreamObserver<User> responseObserver) {
            String query = "INSERT INTO Users (" +
                    "first_name, last_name, username, password, email, " +
                    "phone_number,country_id, birthday, date_created, date_last_modified) " +
                    "VAlUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            Connection connection;

            // Incomplete exception handling.
            // Handle taken username, email, etc.
         try {
             connection =  DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+"test","postgresql","1234");

             PreparedStatement preparedStatement = connection.prepareStatement(query);
             preparedStatement.setString(1, user.getFirstName());
             preparedStatement.setString(2, user.getLastName());
             preparedStatement.setString(3, user.getUsername());
             preparedStatement.setString(4, user.getPassword());
             preparedStatement.setString(5, user.getEmail());
             preparedStatement.setString(6, user.getPhoneNumber());
             preparedStatement.setInt(7, user.getCountryId());
             // Possible error: check Timestamp.toString().
             preparedStatement.setString(8, user.getBirthdate().toString());
             preparedStatement.setString(9, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));
             preparedStatement.setString(10, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));
         } catch (Exception e) {
             e.printStackTrace();
             return;
         }

         // responseObserver.onNext();
        }
    }
}
