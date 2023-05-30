CREATE TABLE IF NOT EXISTS Countries (
    id SERIAL PRIMARY KEY,
    iso CHAR(2),
    name VARCHAR(100),
    nice_name VARCHAR(100),
    iso3 CHAR(3),
    num_code INTEGER,
    phone_code INTEGER
);

CREATE TABLE IF NOT EXISTS Users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    username VARCHAR(50),
    password VARCHAR(50),
    email VARCHAR(50),
    phone_number VARCHAR(50),
    country_id INTEGER REFERENCES Countries (id),
    birthdate  DATE,
    bio VARCHAR(280),
    location VARCHAR(50),
    website VARCHAR(50),
    date_created TIMESTAMP,
    date_last_modified TIMESTAMP
);


CREATE TABLE IF NOT EXISTS ProfilePhotos (
    id SERIAL PRIMARY KEY,    
    filename VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS UserProfilePhotos (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES Users (id),
    profile_photo_id INTEGER REFERENCES ProfilePhotos (id)
);

CREATE TABLE IF NOT EXISTS HeaderPhotos (
    id SERIAL PRIMARY KEY,
    filename VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS UserHeaderPhotos (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES Users (id),
    header_photo_id INTEGER REFERENCES HeaderPhotos (id)
);

CREATE TABLE IF NOT EXISTS Photos (
    id SERIAL PRIMARY KEY,
    filename VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS Tweets (
    id SERIAL PRIMARY KEY,
    text VARCHAR(280),
    photo_id INTEGER REFERENCES Photos (id),
    tweet_id INTEGER REFERENCES Tweets (id),
    date_created TIMESTAMP
  
);

CREATE TABLE IF NOT EXISTS Followings (
    id SERIAL PRIMARY KEY,
    follower_id INTEGER,
    followee_id INTEGER
);

CREATE TABLE IF NOT EXISTS Likes (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES Users (id),
    tweet_id INTEGER REFERENCES Tweets (id),
    date_created TIMESTAMP,
    date_deleted TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Replies (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES Users (id),
    tweet_id INTEGER REFERENCES Tweets (id),
    date_created TIMESTAMP
);

CREATE TABLE IF NOT EXISTS DirectMessages (
    id SERIAL PRIMARY KEY,
    sender_id INTEGER,
    receiver_id INTEGER,
    text VARCHAR(280),
    date_created TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Hashtags (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS TweetHashtags (
    id SERIAL PRIMARY KEY,
    tweet_id INTEGER REFERENCES Tweets (id),
    hashtag_id INTEGER REFERENCES Hashtags (id)
);

CREATE TABLE IF NOT EXISTS Blacklist (
    id SERIAL PRIMARY KEY,
    blocker_id INTEGER,
    blocked_id INTEGER
);

CREATE TABLE IF NOT EXISTS Views (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES Users (id),
    tweet_id INTEGER REFERENCES Tweets (id),
    date_time TIMESTAMP
);


