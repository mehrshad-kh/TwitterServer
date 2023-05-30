package com.mkh;

import com.google.type.DateTime;
import com.mkh.twitter.*;
import io.grpc.stub.StreamObserver;

import java.sql.*;
import java.time.Instant;
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
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;


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
        LocalDateTime localDateTime  = LocalDateTime.now();
        int id;
        String query = "INSERT INTO Users (" +
                "first_name, " +
                "last_name, " +
                "username, " +
                "password, " +
                "email, " +
                "phone_number, " +
                "country_id, " +
                "birthdate, " +
                "date_created, " +
                "date_last_modified) " +
                "VAlUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        // Incomplete exception handling.
        // Handle taken username, email, etc.
        // Close statement.

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getUsername());
            statement.setString(4, user.getPassword());
            statement.setString(5, user.getEmail());
            statement.setString(6, user.getPhoneNumber());
            statement.setInt(7, user.getCountryId());
            // Possible error: check Timestamp.toString()
            statement.setDate(8, new  java.sql.Date((new java.util.Date(user.getBirthdate()).getTime())));
            Instant now = Instant.now();
            statement.setTimestamp(9,Timestamp.valueOf(localDateTime));
            statement.setTimestamp(10,Timestamp.valueOf(localDateTime));
            statement.executeUpdate();
            String selectQuery = "SELECT id FROM users " +
                                "where username = ? ";
            statement = connection.prepareStatement(selectQuery);
            statement.setString(1, user.getUsername());
            ResultSet resultSet =  statement.executeQuery();
            resultSet.next();
            id = resultSet.getInt("id");

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        User registeredUser = user.toBuilder()
                .setId(id)
                .build();

        responseObserver.onNext(registeredUser);
        responseObserver.onCompleted();
    }
}
