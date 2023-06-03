package com.mkh;

import com.google.protobuf.ByteString;
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
import java.util.concurrent.BlockingQueue;
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
    // it's address that we use to store the photo's address must be changed
    @Override
    public void submitProfilePhoto(ProfilePhoto profilePhoto, StreamObserver<MKBoolean> responseObserver) {
        boolean result;
        byte[] bytes = profilePhoto.getPhoto().getBytes().toByteArray();
        String query = "INSERT INTO profile_photos (filename) " +
                       "VALUES  (?);";
        String query2 = "INSERT INTO user_profile_photos (user_id, profile_photo_id) " +
                        "VALUES (?, ?);";
        String query3 = "SELECT id " +
                        "FROM profile_photos " +
                        "WHERE filename = ?;";
        String randomPath =  RandomStringUtils.randomAlphabetic(16);
        Path destinationPath
                = Paths.get("D:\\" +
                String.format("%s.%s",
                randomPath,
                profilePhoto.getPhoto().getExtension()));

        try {
            Files.write(destinationPath, bytes);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, randomPath +"." + profilePhoto.getPhoto().getExtension());
            statement.execute();
            statement.close();
            PreparedStatement statement3 = connection.prepareStatement(query3);
            statement3.setString(1, randomPath + "." + profilePhoto.getPhoto().getExtension());
            ResultSet resultSet = statement3.executeQuery();
            resultSet.next();
            int profilePhotoId = resultSet.getInt("id");
            statement3.close();
            PreparedStatement statement2 = connection.prepareStatement(query2);
            statement2.setInt(1, profilePhoto.getUserId());
            statement2.setInt(2, profilePhotoId);
            statement2.execute();
            statement2.close();
            result = true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "*** IO error occurred");
            e.printStackTrace();
            result = false;
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "*** SQL error occurred");
            e.printStackTrace();
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
    //it only retrieves the first photo of the user , do not forget to change it if it is needed
    @Override
    public void retrieveProfilePhoto(User user, StreamObserver<ProfilePhoto> responseObserver ){
        String query = "SELECT profile_photos.filename " +
                "FROM profile_photos " +
                "INNER JOIN user_profile_photos " +
                "ON profile_photos.id = user_profile_photos.profile_photo_id " +
                "WHERE user_profile_photos.user_id = ?;";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, user.getId());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                System.out.println(resultSet.getString("filename"));
                byte[] bytes = Files.readAllBytes(Paths.get("D:\\" + resultSet.getString("filename")));
                MKFile file = MKFile.newBuilder()
                        .setBytes(ByteString.copyFrom(bytes))
                        .setExtension(resultSet.getString("filename").substring(resultSet.getString("filename").lastIndexOf(".")))
                        .build();
                ProfilePhoto profilePhoto = ProfilePhoto.newBuilder()
                        .setUserId(user.getId())
                        .setPhoto(file)
                        .build();
                responseObserver.onNext(profilePhoto);
            } else {
                responseObserver.onNext(null);
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
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
}
