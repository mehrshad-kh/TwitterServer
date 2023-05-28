package com.mkh;

import com.mkh.twitter.*;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.checkerframework.checker.units.qual.A;

import java.io.IOException;
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
        public void signUp(User user, StreamObserver<User> responseObserver){

        }
    }
}
