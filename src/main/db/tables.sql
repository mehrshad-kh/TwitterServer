CREATE TABLE Users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) ,
    last_name VARCHAR(50) ,
    username VARCHAR(50) ,
    password VARCHAR(50) ,
    email VARCHAR(50) ,
    phone_number VARCHAR(50) ,
    country_id INTEGER REFERENCES Countries (id) ,
    birthdate  DATE , 
    bio VARCHAR(280) ,
    location VARCHAR(50),
    website VARCHAR(50),
    date_created TIMESTAMP,
    date_last_modified TIMESTAMP
);

CREATE  TABLE Countries (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) 
);

CREATE  TABLE  ProfilePhotos (
    id SERIAL PRIMARY KEY,    
    filename VARCHAR(100)
);

CREATE   TABLE UserProfilePhotos (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES Users (id),
    profile_photo_id INTEGER REFERENCES ProfilePhotos (id)
);

CREATE TABLE  HeaderPhotos (
    id SERIAL PRIMARY KEY,
    filename VARCHAR(100)
);

CREATE TABLE UserHeaderPhotos (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES Users (id),
    header_photo_id INTEGER REFERENCES HeaderPhotos (id)
);

CREATE TABLE Photos (
    id SERIAL PRIMARY KEY,
    filename VARCHAR(100)
);

CREATE TABLE Tweets (
    id SERIAL PRIMARY KEY,
    text VARCHAR(280),
    photo_id INTEGER REFERENCES Photos (id),
    tweet_id INTEGER REFERENCES Tweets (id),
    date_created TIMESTAMP
  
);

CREATE TABLE Followings (
    id SERIAL PRIMARY KEY,
    follower_id INTEGER,
    followee_id INTEGER
);

CREATE TABLE Likes (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES Users(id),
    tweet_id INTEGER REFERENCES Tweets(id),
    date_created TIMESTAMP,
    date_deleted TIMESTAMP
);

CREATE TABLE Replies (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES Users(id),
    tweet_id INTEGER REFERENCES Tweets(id),
    date_created TIMESTAMP
);

CREATE TABLE DirectMessages (
    id SERIAL PRIMARY KEY,
    sender_id INTEGER,
    receiver_id INTEGER,
    text VARCHAR(280),
    date_created TIMESTAMP
);

CREATE  TABLE Hashtags (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50)
);

CREATE TABLE TweetHashtags (
    id SERIAL PRIMARY KEY,
    tweet_id INTEGER REFERENCES Tweets(id),
    hashtag_id INTEGER REFERENCES Hashtags(id)
);

CREATE TABLE Blacklist (
    id SERIAL PRIMARY KEY,
    blocker_id INTEGER,
    blocked_id INTEGER
);

CREATE TABLE Views (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES Users(id),
    tweet_id INTEGER REFERENCES Tweets(id),
    date_time TIMESTAMP
);


