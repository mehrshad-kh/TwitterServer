package com.mkh;

import com.mkh.twitter.*;
import io.grpc.stub.StreamObserver;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
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
    public void isTakenUsername(MKString username, StreamObserver<MKBoolean> responseObserver) {
        boolean result = false;
        String query = "SELECT COUNT(*) " +
                "FROM users " +
                "WHERE username = ?;";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username.getValue());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);

                if (count == 0) {
                    result = false;
                }
                else {
                    result = true;
                }
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        MKBoolean response = MKBoolean.newBuilder().setValue(result).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void isTakenEmail(MKString email, StreamObserver<MKBoolean> responseObserver){
        boolean result = false;
        String query = "SELECT COUNT(*)" +
                "FROM users " +
                "Where email = ? ;";

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email.getValue());
            ResultSet resultSet = statement.executeQuery();

               if (resultSet.next()){
                    int count = resultSet.getInt(1);
                    if (count == 0) {
                        result = false;
                    } else {
                        result = true;
                    }
               }

               statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        MKBoolean response = MKBoolean.newBuilder().setValue(result).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void isTakenPhoneNumber(MKString phoneNumber,StreamObserver<MKBoolean> responseObserver  ){
        boolean result = false;
        String query = "SELECT COUNT(*) " +
                "FROM users "+
                "WHERE phone_number = ?;";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, phoneNumber.getValue());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);

                if (count == 0) {
                    result = false;
                } else {
                    result = true;
                }
            }

           statement.close();
        } catch (SQLException e){
            e.printStackTrace();
            return;
        }

        MKBoolean response = MKBoolean.newBuilder().setValue(result).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void signUp(User user, StreamObserver<User> responseObserver) {
        LocalDateTime now = LocalDateTime.now();
        int id;

        String query = "INSERT INTO users (" +
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

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getUsername());
            statement.setString(4, user.getPassword());
            statement.setString(5, user.getEmail());
            statement.setString(6, user.getPhoneNumber());
            statement.setInt(7, user.getCountryId());
            statement.setDate(8, java.sql.Date.valueOf(user.getBirthdate()));
            statement.setTimestamp(9, Timestamp.valueOf(now));
            statement.setTimestamp(10, Timestamp.valueOf(now));
            statement.execute();
            String selectQuery = "SELECT id " +
                    "FROM users " +
                    "WHERE username = ?;";
            statement = connection.prepareStatement(selectQuery);
            statement.setString(1, user.getUsername());
            ResultSet resultSet =  statement.executeQuery();

            resultSet.next();
            id = resultSet.getInt("id");

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        User signedUpUser = user.toBuilder()
                .setId(id)
                .build();
        responseObserver.onNext(signedUpUser);
        responseObserver.onCompleted();
    }

    @Override
    public void submitProfilePhoto(ProfilePhoto profilePhoto, StreamObserver<MKBoolean> responseObserver) {
        boolean result;
        byte[] bytes = profilePhoto.getPhoto().getBytes().toByteArray();

        Path destinationPath
                = Paths.get("profile-photos/" +
                String.format("%s.%s",
                RandomStringUtils.randomAlphabetic(16),
                profilePhoto.getPhoto().getExtension()));

        try {
            Files.write(destinationPath, bytes);
            result = true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "*** IO error occurred");
            result = false;
        }

        responseObserver.onNext(MKBoolean.newBuilder().setValue(result).build());
        responseObserver.onCompleted();
    }

    @Override
    public void signIn(User user, StreamObserver<User> responseObserver) {
        String query = "SELECT * " +
                "FROM users " +
                "WHERE username = ? AND password = ?;";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                User signedInUser = User.newBuilder()
                        .setId(resultSet.getInt("id"))
                        .setFirstName(resultSet.getString("first_name"))
                        .setLastName(resultSet.getString("last_name"))
                        .setUsername(resultSet.getString("username"))
                        .setPassword(resultSet.getString("password"))
                        .setEmail(resultSet.getString("email"))
                        .setPhoneNumber(resultSet.getString("phone_number"))
                        .setCountryId(resultSet.getInt("country_id"))
                        .setBirthdate(resultSet.getDate("birthdate").toString())
                        .setDateCreated(resultSet.getTimestamp("date_created").toString())
                        .setDateLastModified(resultSet.getTimestamp("date_last_modified").toString())
                        .build();
                responseObserver.onNext(signedInUser);
            } else {
                responseObserver.onNext(null);
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        responseObserver.onCompleted();
    }

    @Override
    public void retrieveCountries(MKEmpty empty, StreamObserver<Country> responseObserver) {
        String query = "SELECT id, name " +
                "FROM countries;";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Country country = Country.newBuilder()
                        .setId(resultSet.getInt("id"))
                        .setName(resultSet.getString("name"))
                        .build();
                responseObserver.onNext(country);
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        responseObserver.onCompleted();
    }

    @Override
    public void retrieveLikeCount(Tweet tweet, StreamObserver<MKInteger> responseObserver) {
        int count;
        String query = "SELECT COUNT(user_id) " +
                "FROM likes " +
                "WHERE tweet_id = ? " +
                "AND date_deleted IS NOT NULL;";

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, tweet.getId());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt(1);
            } else {
                count = 0;
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        MKInteger response = MKInteger.newBuilder()
                .setValue(count)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void retrieveReplyCount(Tweet tweet, StreamObserver<MKInteger> responseObserver) {
        int count;
        String query = "SELECT COUNT(user_id) " +
                "FROM replies " +
                "WHERE tweet_id = ?;";

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, tweet.getId());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt(1);
            } else {
                count = 0;
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        MKInteger response = MKInteger.newBuilder()
                .setValue(count)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
