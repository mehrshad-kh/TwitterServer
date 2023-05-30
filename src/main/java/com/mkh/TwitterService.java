package com.mkh;

import com.mkh.twitter.*;
import io.grpc.stub.StreamObserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Our implementation of Twitter service.
 */
public final class TwitterService extends TwitterGrpc.TwitterImplBase {
    private static final Logger logger = Logger.getLogger(TwitterService.class.getName());

    // Possible error: does it have to be final?
    private final Connection connection;

    public TwitterService(Connection connection) {
        this.connection = connection;
    }

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
