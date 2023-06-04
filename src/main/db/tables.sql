CREATE TABLE IF NOT EXISTS countries (
    id SERIAL PRIMARY KEY,
    iso CHAR(2),
    name VARCHAR(100),
    nice_name VARCHAR(100),
    iso3 CHAR(3),
    num_code INTEGER,
    phone_code INTEGER
);

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    username VARCHAR(50) UNIQUE,
    password VARCHAR(50),
    email VARCHAR(50) UNIQUE,
    phone_number VARCHAR(50) UNIQUE,
    country_id INTEGER REFERENCES countries (id),
    birthdate  DATE,
    bio VARCHAR(160),
    location VARCHAR(50),
    website VARCHAR(50),
    date_created TIMESTAMP,
    date_last_modified TIMESTAMP
);


CREATE TABLE IF NOT EXISTS profile_photos (
    id SERIAL PRIMARY KEY,    
    filename VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS user_profile_photos (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users (id),
    profile_photo_id INTEGER REFERENCES profile_photos (id)
);

CREATE TABLE IF NOT EXISTS header_photos (
    id SERIAL PRIMARY KEY,
    filename VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS user_header_photos (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users (id),
    header_photo_id INTEGER REFERENCES header_photos (id)
);

CREATE TABLE IF NOT EXISTS photos (
    id SERIAL PRIMARY KEY,
    filename VARCHAR(100)
);
--we forgot to  add the user_id column
CREATE TABLE IF NOT EXISTS tweets (
    id SERIAL PRIMARY KEY,
    text VARCHAR(280),
    photo_id INTEGER REFERENCES photos (id),
    tweet_id INTEGER REFERENCES tweets (id),
    user_id INTEGER REFERENCES users (id),
    date_created TIMESTAMP
  
);

CREATE TABLE IF NOT EXISTS followings (
    id SERIAL PRIMARY KEY,
    follower_id INTEGER,
    followee_id INTEGER,
    date_created TIMESTAMP,
    date_deleted TIMESTAMP
);

CREATE TABLE IF NOT EXISTS likes (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users (id),
    tweet_id INTEGER REFERENCES tweets (id),
    date_created TIMESTAMP,
    date_deleted TIMESTAMP
);

CREATE TABLE IF NOT EXISTS replies (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users (id),
    tweet_id INTEGER REFERENCES tweets (id),
    date_created TIMESTAMP
);

CREATE TABLE IF NOT EXISTS direct_messages (
    id SERIAL PRIMARY KEY,
    sender_id INTEGER,
    receiver_id INTEGER,
    text VARCHAR(280),
    date_created TIMESTAMP
);

CREATE TABLE IF NOT EXISTS hashtags (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS tweet_hashtags (
    id SERIAL PRIMARY KEY,
    tweet_id INTEGER REFERENCES tweets (id),
    hashtag_id INTEGER REFERENCES hashtags (id)
);

CREATE TABLE IF NOT EXISTS blacklist (
    id SERIAL PRIMARY KEY,
    blocker_id INTEGER,
    blocked_id INTEGER
);

CREATE TABLE IF NOT EXISTS views (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users (id),
    tweet_id INTEGER REFERENCES tweets (id),
    date_time TIMESTAMP
);
