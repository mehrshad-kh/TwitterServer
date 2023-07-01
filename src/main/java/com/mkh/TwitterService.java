package com.mkh;

import com.google.protobuf.ByteString;
import com.mkh.twitter.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

// import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;
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
    public void getDailyBriefing(User user, StreamObserver<Tweet> responseObserver) {
        System.out.println("getDailyBriefing() was called by the client.");

        String query = "SELECT id, text, retweet_id, sender_id, date_created " +
                "FROM tweets WHERE id = " + user.getId() + ";";
//        String query = "SELECT id, text, retweet_id, sender_id, date_created " +
//                "FROM tweets " +
//                "WHERE id IN ( " +
//                "    SELECT tweet_id " +
//                "    FROM views " +
//                "    WHERE tweet_id NOT IN ( " +
//                "        SELECT tweet_id " +
//                "        FROM tweets " +
//                "        WHERE user_id IN ( " +
//                "            SELECT blocker_id " +
//                "            FROM blacklist " +
//                "            WHERE blocked_id =  ? " +
//                "        ) " +
//                "    ) " +
//                "    GROUP BY tweet_id " +
//                "    HAVING COUNT(tweet_id) >= 10 " +
//                "    )  " +
//                "OR (sender_id IN ( " +
//                "        SELECT followee_id  " +
//                "        FROM followings " +
//                "        WHERE follower_id =  ? AND date_deleted IS NOT NULL " +
//                "    ) AND retweet_id NOT IN ( " +
//                "        SELECT tweet_id " +
//                "        FROM views " +
//                "        WHERE user_id =  ? " +
//                "    ) " +
//                "); ";

        ArrayList<Tweet> tweets = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, user.getId());
            statement.setInt(2, user.getId());
            statement.setInt(3, user.getId());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String text = resultSet.getString(2);
                int RetweetId = resultSet.getInt(3);
                int SenderId = resultSet.getInt(4);
                String dateCreated = resultSet.getString(5);

                Tweet tweet = Tweet.newBuilder()
                        .setId(id)
                        .setText(text)
                        .setRetweetId(RetweetId)
                        .setSenderId(SenderId)
                        .setDateCreated(dateCreated)
                        .build();
                tweets.add(tweet);
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        
        for (Tweet tweet : tweets) {
            responseObserver.onNext(tweet);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void isTakenUsername(MKString username, StreamObserver<MKBoolean> responseObserver) {
        logger.info("isTakenUsername() was called.");

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

                result = count != 0;
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        MKBoolean response = MKBoolean.newBuilder().setValue(result).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void isTakenEmail(MKString email, StreamObserver<MKBoolean> responseObserver){
        boolean result = false;
        String query = "SELECT COUNT(*) " +
                "FROM users " +
                "WHERE email = ?;";

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email.getValue());
            ResultSet resultSet = statement.executeQuery();

               if (resultSet.next()) {

                   int count = resultSet.getInt(1);
                   result = count != 0;
               }

               statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
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
                result = count != 0;
            }

           statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
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
            Status status = Status.INTERNAL.withDescription("Database error occurred.");
            responseObserver.onError(status.asRuntimeException());
            return;
        }

        User signedUpUser = user.toBuilder()
                .setId(id)
                .build();
        responseObserver.onNext(signedUpUser);
        responseObserver.onCompleted();
    }

    @Override
    public void uploadTweetPhoto(TweetPhoto tweetPhoto, StreamObserver<MKBoolean> responseObserver) {
        int id;
        boolean result = false;
        String query = "INSERT INTO photos (filename) " +
                "VALUES (?);";
        String query2 = "INSERT INTO tweet_photos (tweet_id, photo_id) " +
                "VALUES (?, ?);";
        String query3 = "SELECT id " +
                "FROM photos " +
                "WHERE filename = ?;";

        String randomFilename = UUID.randomUUID().toString();
        Path destinationPath
                = Paths.get("C:\\Users\\amirsalar.abedini\\Desktop\\server\\photos" +
                String.format("%s.%s",
                        randomFilename,
                        tweetPhoto.getPhoto().getExtension()));
        try {
            Files.write(destinationPath, tweetPhoto.getPhoto().getBytes().toByteArray());
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, randomFilename + "." + tweetPhoto.getPhoto().getExtension());
            statement.execute();
            statement.close();
            statement = connection.prepareStatement(query3);
            statement.setString(1, randomFilename + "." + tweetPhoto.getPhoto().getExtension());
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            id = resultSet.getInt("id");
            statement = connection.prepareStatement(query2);
            statement.setInt(1, tweetPhoto.getTweetId());
            statement.setInt(2, id);
            statement.execute();
            statement.close();
            result = true;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        MKBoolean mkBoolean = MKBoolean.newBuilder().setValue(result).build();
        responseObserver.onNext(mkBoolean);
        responseObserver.onCompleted();
    }

    @Override
    public void retrieveTweetPhotos(Tweet tweet, StreamObserver<MKFile> responseObserver) {
        String query = "SELECT filename " +
                "FROM tweet_photos " +
                "WHERE tweet_id = ?;";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, tweet.getId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String filename = resultSet.getString("filename");
                Path path = Paths.get("user-files/photos/" + filename);
                byte[] bytes = Files.readAllBytes(path);
                MKFile photo = MKFile.newBuilder()
                        .setExtension(filename.substring(filename.lastIndexOf(".") + 1))
                        .setBytes(ByteString.copyFrom(bytes))
                        .build();
                responseObserver.onNext(photo);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        responseObserver.onCompleted();
    }

    @Override
    public void sendTweet(Tweet tweet, StreamObserver<Tweet> responseObserver){
        String query = "INSERT INTO tweets (text, sender_id, retweet_id, date_created) " +
                       "VALUES (?, ?, ?, ?) RETURNING id;";

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, tweet.getText());
            statement.setInt(2, tweet.getSenderId());
            statement.setNull(3, Types.INTEGER);
            statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ResultSet resultSet =  statement.executeQuery();
            //the result is tweet id which is auto generated
            resultSet.next();
            tweet = tweet.toBuilder()
                    .setId(resultSet.getInt("id"))
                    .build();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        responseObserver.onNext(Tweet.newBuilder()
                .setId(tweet.getId())
                .setText(tweet.getText())
                .setSenderId(tweet.getSenderId())
                .setRetweetId(tweet.getRetweetId())
                .setDateCreated(tweet.getDateCreated())
                .build());
        responseObserver.onCompleted();
    }

    public void sendRetweet(Tweet tweet, StreamObserver<MKBoolean> responseObserver) {
        String query = "INSERT INTO tweets (text, user_id, retweet_id, date_created) " +
                "VALUES (?, ?, ?, ?);";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setNull(1, Types.VARCHAR);
            statement.setInt(2, tweet.getSenderId());
            statement.setInt(3, tweet.getRetweetId());
            statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        responseObserver.onNext(MKBoolean.newBuilder().setValue(true).build());
        responseObserver.onCompleted();
    }

    @Override
    public void sendQuote(Tweet tweet, StreamObserver<MKBoolean> responseObserver) {
        String query = "INSERT INTO tweets (text, sender_id, retweet_id, date_created) " +
                "VALUES (?, ?, ?, ?);";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, tweet.getText());
            statement.setInt(2, tweet.getSenderId());
            statement.setInt(3, tweet.getRetweetId());
            statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        responseObserver.onNext(MKBoolean.newBuilder().setValue(true).build());
        responseObserver.onCompleted();
    }

    @Override
    public void submitProfilePhoto(ProfilePhoto profilePhoto, StreamObserver<MKEmpty> responseObserver) {
        logger.info("submitProfilePhoto() was called.");

        byte[] bytes = profilePhoto.getPhoto().getBytes().toByteArray();
        String query = "INSERT INTO profile_photos (filename) " +
                       "VALUES  (?);";
        String query2 = "INSERT INTO user_profile_photos (user_id, profile_photo_id) " +
                        "VALUES (?, ?);";
        String query3 = "SELECT id " +
                        "FROM profile_photos " +
                        "WHERE filename = ?;";
        String randomFilename = UUID.randomUUID().toString();
        Path destinationPath
                = Paths.get("user-files/profile-photos/" +
                String.format("%s.%s",
                randomFilename,
                profilePhoto.getPhoto().getExtension()));

        try {
            Files.write(destinationPath, bytes);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, randomFilename + "." + profilePhoto.getPhoto().getExtension());
            statement.execute();
            statement.close();
            PreparedStatement statement3 = connection.prepareStatement(query3);
            statement3.setString(1, randomFilename + "." + profilePhoto.getPhoto().getExtension());
            ResultSet resultSet = statement3.executeQuery();
            resultSet.next();
            int profilePhotoId = resultSet.getInt("id");
            statement3.close();
            PreparedStatement statement2 = connection.prepareStatement(query2);
            statement2.setInt(1, profilePhoto.getUserId());
            statement2.setInt(2, profilePhotoId);
            statement2.execute();
            statement2.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "*** IO error occurred");
            e.printStackTrace();
            Status errorStatus = Status.INTERNAL.withDescription("Could not store file.");
            responseObserver.onError(errorStatus.asRuntimeException());
            return;
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "*** SQL error occurred");
            e.printStackTrace();
            Status errorStatus = Status.INTERNAL.withDescription("Database error occurred.");
            responseObserver.onError(errorStatus.asRuntimeException());
            return;
        }

        responseObserver.onNext(MKEmpty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void submitHeaderPhoto(HeaderPhoto headerPhoto, StreamObserver<MKEmpty> responseObserver) {
        logger.info("submitHeaderPhoto() was called.");

        byte[] bytes = headerPhoto.getPhoto().getBytes().toByteArray();
        String query = "INSERT INTO header_photos (filename) " +
                "VALUES (?);";
        String query2 = "INSERT INTO user_header_photos (user_id, header_photo_id) " +
                "VALUES (?, ?);";
        String query3 = "SELECT id " +
                "FROM header_photos " +
                "WHERE filename = ?;";
        String randomFilename = UUID.randomUUID().toString();
        Path destinationPath
                = Paths.get("user-files/header-photos/" +
                String.format("%s.%s",
                        randomFilename,
                        headerPhoto.getPhoto().getExtension()));

        try {
            Files.write(destinationPath, bytes);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, randomFilename + "." + headerPhoto.getPhoto().getExtension());
            statement.execute();
            statement.close();
            PreparedStatement statement3 = connection.prepareStatement(query3);
            statement3.setString(1, randomFilename + "." + headerPhoto.getPhoto().getExtension());
            ResultSet resultSet = statement3.executeQuery();
            resultSet.next();
            int headerPhotoId = resultSet.getInt("id");
            statement3.close();
            PreparedStatement statement2 = connection.prepareStatement(query2);
            statement2.setInt(1, headerPhoto.getUserId());
            statement2.setInt(2, headerPhotoId);
            statement2.execute();
            statement2.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "*** IO error occurred");
            e.printStackTrace();
            Status errorStatus = Status.INTERNAL.withDescription("Could not store file.");
            responseObserver.onError(errorStatus.asRuntimeException());
            return;
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "*** SQL error occurred");
            e.printStackTrace();
            Status errorStatus = Status.INTERNAL.withDescription("Database error occurred.");
            responseObserver.onError(errorStatus.asRuntimeException());
            return;
        }

        responseObserver.onNext(MKEmpty.newBuilder().build());
        responseObserver.onCompleted();
    }

    // Possible error: only retrieves a handful of user info.
    @Override
    public void signIn(User user, StreamObserver<User> responseObserver) {
        logger.info("signIn() was called.");

        String query = "SELECT id, first_name, last_name, username, email, " +
                "phone_number, country_id, birthdate, bio, location, website " +
                "FROM users " +
                "WHERE username = ? " +
                "AND password = ?;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                User signedInUser = User.newBuilder()
                        .setId(resultSet.getInt("id"))
                        .setFirstName(resultSet.getString("first_name"))
                        .setLastName(resultSet.getString("last_name"))
                        .setUsername(resultSet.getString("username"))
                        .setEmail(resultSet.getString("email"))
                        .setPhoneNumber(resultSet.getString("phone_number") == null ? "" : resultSet.getString("phone_number"))
                        .setCountryId(resultSet.getInt("country_id"))
                        .setBirthdate(resultSet.getDate("birthdate").toString())
                        .setBio(resultSet.getString("bio") == null ? "" : resultSet.getString("bio"))
                        .setLocation(resultSet.getString("location") == null ? "" : resultSet.getString("location"))
                        .setWebsite(resultSet.getString("website") == null ? "" : resultSet.getString("website"))
                        .build();
                responseObserver.onNext(signedInUser);
            } else {
                Status errorStatus = Status.UNAUTHENTICATED.withDescription("Incorrect username or password.");
                responseObserver.onError(errorStatus.asRuntimeException());
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Status errorStatus = Status.INTERNAL.withDescription("Database error occurred.");
            responseObserver.onError(errorStatus.asRuntimeException());
            return;
        }

        responseObserver.onCompleted();
    }

    // Only the first profile photo is retrieved, whereas many may be stored.
    @Override
    public void retrieveProfilePhoto(User user, StreamObserver<ProfilePhoto> responseObserver ) {
        String query = "SELECT profile_photos.filename " +
                "FROM profile_photos " +
                "INNER JOIN user_profile_photos " +
                "ON profile_photos.id = user_profile_photos.profile_photo_id " +
                "WHERE user_profile_photos.user_id = ?;";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, user.getId());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                byte[] bytes = Files.readAllBytes(Paths.get("user-files/profile-photos/" + resultSet.getString("filename")));
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
                // Possible error: may incur problems.
                responseObserver.onNext(null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }

        responseObserver.onCompleted();
    }

    // Only the first header photo is retrieved, whereas many may be stored.
    @Override
    public void retrieveHeaderPhoto(User user, StreamObserver<HeaderPhoto> responseObserver) {
        String query = "SELECT header_photos.filename " +
                "FROM header_photos " +
                "INNER JOIN user_header_photos " +
                "ON header_photos.id = user_header_photos.header_photo_id " +
                "WHERE user_header_photos.user_id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, user.getId());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                byte[] bytes = Files.readAllBytes(
                        Paths.get("user-files/header-photos/" +
                                resultSet.getString("filename")));
                MKFile file = MKFile.newBuilder()
                        .setBytes(ByteString.copyFrom(bytes))
                        .setExtension(resultSet.getString("filename")
                                .substring(resultSet.getString("filename")
                                        .lastIndexOf(".")))
                        .build();
                HeaderPhoto headerPhoto = HeaderPhoto.newBuilder()
                        .setUserId(user.getId())
                        .setPhoto(file)
                        .build();
                responseObserver.onNext(headerPhoto);
            } else {
                // Possible error: may incur problems.
                responseObserver.onNext(null);
            }
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
        logger.info("retrieveCountries() was called.");

        String query = "SELECT id, nice_name " +
                "FROM countries;";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Country country = Country.newBuilder()
                        .setId(resultSet.getInt("id")).setNiceName(resultSet.getString("nice_name"))
                        .build();
                responseObserver.onNext(country);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        responseObserver.onCompleted();
    }

    @Override
    public void updateProfileInfo(User user, StreamObserver<User> responseObserver) {
        String query = "UPDATE users "+
                "SET bio = ?, " +
                "location = ?, " +
                "website = ?, " +
                "date_last_modified = ? " +
                "WHERE id = ?;";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            if (user.getBio().isEmpty()) {
                statement.setNull(1, Types.VARCHAR);
            } else {
                statement.setString(1, user.getBio());
            }

            if (user.getLocation().isEmpty()) {
                statement.setNull(2, Types.VARCHAR);
            } else {
                statement.setString(2, user.getLocation());
            }

            if (user.getWebsite().isEmpty()) {
                statement.setNull(3, Types.VARCHAR);
            } else {
                statement.setString(3, user.getWebsite());
            }
            statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            statement.setInt(5, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        responseObserver.onNext(user);
        responseObserver.onCompleted();
    }

    @Override
    public void retrieveLikeCount(Tweet tweet, StreamObserver<MKInteger> responseObserver) {
        int count;
        String query = "SELECT COUNT(user_id) " +
                "FROM likes " +
                "WHERE tweet_id = ? " +
                "AND date_deleted IS NOT NULL;";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, tweet.getId());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt(1);
            } else {
                count = 0;
            }
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

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, tweet.getId());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt(1);
            } else {
                count = 0;
            }
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
    public void retrieveRetweetCount(Tweet tweet, StreamObserver<MKInteger> responseObserver) {
        int count;
        String query = "SELECT COUNT(id) " +
                "FROM tweets " +
                "WHERE retweet_id = ?;";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, tweet.getId());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt(1);
            } else {
                count = 0;
            }
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
    public void likeTweet(TweetLike tweetLike, StreamObserver<MKBoolean> responseObserver) {
        String query = "INSERT INTO likes (user_id, tweet_id, date_created) " +
                "VALUES (?, ?, ?);";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, tweetLike.getUserId());
            statement.setInt(2, tweetLike.getTweetId());
            statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        responseObserver.onNext(MKBoolean.newBuilder().setValue(true).build());
        responseObserver.onCompleted();
    }

    @Override
    public void unlikeTweet(TweetLike tweetLike, StreamObserver<MKBoolean> responseObserver) {
        String query = "UPDATE likes " +
                "SET date_deleted = ? " +
                "WHERE user_id = ? " +
                "AND tweet_id = ?" +
                "AND date_deleted IS NOT NULL;";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            statement.setInt(2, tweetLike.getUserId());
            statement.setInt(3, tweetLike.getTweetId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        responseObserver.onNext(MKBoolean.newBuilder().setValue(true).build());
        responseObserver.onCompleted();
    }

    @Override
    public void follow(FollowRequest followRequest, StreamObserver<MKBoolean> responseObserver) {
        String query = "INSERT INTO followings (follower_id, followee_id, date_created) " +
                "VALUES (?, ?, ?);";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, followRequest.getFollowerId());
            statement.setInt(2, followRequest.getFolloweeId());
            statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        responseObserver.onNext(MKBoolean.newBuilder().setValue(true).build());
        responseObserver.onCompleted();
    }

    @Override
    public void unfollow(FollowRequest unfollowRequest, StreamObserver<MKBoolean> responseObserver) {
        String query = "UPDATE followings " +
                "SET date_deleted = ? " +
                "WHERE follower_id = ? " +
                "AND followee_id = ? " +
                "AND date_deleted IS NOT NULL;";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            statement.setInt(2, unfollowRequest.getFollowerId());
            statement.setInt(3, unfollowRequest.getFolloweeId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        responseObserver.onNext(MKBoolean.newBuilder().setValue(true).build());
        responseObserver.onCompleted();
    }

    @Override
    public void block(BlockRequest blockRequest, StreamObserver<MKBoolean> responseObserver) {
        String query = "INSERT INTO blacklist (blocker_id, blocked_id, date_created) " +
                "VALUES (?, ?, ?);";
        //and the date_deleted of the followings table most be null
        String query2 = "UPDATE followings " +
                "SET date_deleted = ? " +
                "WHERE follower_id = ? " +
                "AND followee_id = ? " +
                "AND date_deleted IS NULL;";

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, blockRequest.getBlockerId());
            statement.setInt(2, blockRequest.getBlockedId());
            statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            statement.executeUpdate();
            statement.close();

            statement = connection.prepareStatement(query2);
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            statement.setInt(2, blockRequest.getBlockedId());
            statement.setInt(3, blockRequest.getBlockerId());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        responseObserver.onNext(MKBoolean.newBuilder().setValue(true).build());
        responseObserver.onCompleted();
    }

    @Override
    public void unblock(BlockRequest unblockRequest, StreamObserver<MKBoolean> responseObserver) {
        String query = "UPDATE blacklist " +
                "SET date_deleted = ? " +
                "WHERE blocker_id = ? " +
                "AND blocked_id = ?;";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            statement.setInt(2, unblockRequest.getBlockerId());
            statement.setInt(3, unblockRequest.getBlockedId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        responseObserver.onNext(MKBoolean.newBuilder().setValue(true).build());
        responseObserver.onCompleted();
    }

    // Retrieves only a bare minimum of user info.
    @Override
    public void retrieveFollowers(User user, StreamObserver<User> responseObserver) {
        String query = "SELECT id, first_name, last_name, username " +
                "FROM users " +
                "WHERE id IN (" +
                "SELECT follower_id " +
                "FROM followings " +
                "WHERE followee_id = ? " +
                "AND date_deleted IS NULL" +
                ");";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, user.getId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                responseObserver.onNext(User.newBuilder()
                                .setId(resultSet.getInt("id"))
                                .setFirstName(resultSet.getString("first_name"))
                                .setLastName(resultSet.getString("last_name"))
                                .setUsername(resultSet.getString("username"))
                                .build());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        responseObserver.onCompleted();
    }

    // Retrieves only a bare minimum of user info.
    @Override
    public void retrieveFollowees(User user , StreamObserver<User> responseObserver){
        String query = "SELECT id, first_name, last_name, username " +
                "FROM users " +
                "WHERE id IN (" +
                "SELECT followee_id " +
                "FROM followings " +
                "WHERE follower_id = ? " +
                "AND date_deleted IS NULL" +
                ");";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, user.getId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                System.out.println(resultSet.getString("username"));
                responseObserver.onNext(User.newBuilder()
                                .setId(resultSet.getInt("id"))
                                .setFirstName(resultSet.getString("first_name"))
                                .setLastName(resultSet.getString("last_name"))
                                .setUsername(resultSet.getString("username"))
                                .build());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        responseObserver.onCompleted();
    }

    @Override
    public void searchUsers(MKString searchPhrase, StreamObserver<User> responseObserver) {
        String query = "SELECT * FROM users WHERE username LIKE ? OR first_name LIKE ? OR last_name LIKE ?;";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "%" + searchPhrase.getValue() + "%");
            statement.setString(2, "%" + searchPhrase.getValue() + "%");
            statement.setString(3, "%" + searchPhrase.getValue() + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                responseObserver.onNext(User.newBuilder()
                        .setId(resultSet.getInt("id"))
                        .setUsername(resultSet.getString("username"))
                        .setFirstName(resultSet.getString("first_name"))
                        .setLastName(resultSet.getString("last_name"))
                        .build());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        responseObserver.onCompleted();
    }

    @Override
    public void retrieveTweets(User user, StreamObserver<Tweet> responseObserver){
        String query = "SELECT * FROM tweets " +
                "WHERE sender_id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, user.getId());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                responseObserver.onNext(Tweet.newBuilder()
                        .setId(resultSet.getInt("id"))
                        .setSenderId(resultSet.getInt("sender_id"))
                        .setText(resultSet.getString("text"))
                        .setDateCreated(resultSet.getString("date_created"))
                        .setRetweetId(resultSet.getInt("retweet_id"))
                        .build());
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        responseObserver.onCompleted();
    }
}

