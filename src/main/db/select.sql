SELECT id, text, tweet_id, user_id, date_created
FROM tweets
WHERE user_id IN (
    SELECT followee_id 
    FROM followings
    WHERE follower_id = 19 AND date_deleted NOT NULL
) AND tweet_id NOT IN (
    SELECT tweet_id
    FROM tweet_views
    WHERE user_id = 19
);

-- Select Big Tweets.
SELECT tweet_id
FROM tweet_views
GROUP BY tweet_id
HAVING COUNT(tweet_id) >= 10;

-- Exclude from:
-- Tweets form those who've blocked me.
SELECT tweet_id
FROM tweets
WHERE user_id IN (
    SELECT blocker_id
    FROM blacklist
    WHERE blocked_id = 19
);

-- Combination of both.
SELECT tweet_id
FROM tweet_views
WHERE tweet_id NOT IN (
    SELECT tweet_id
    FROM tweets
    WHERE user_id IN (
        SELECT blocker_id
        FROM blacklist
        WHERE blocked_id = 19
    )
)
GROUP BY tweet_id
HAVING COUNT(tweet_id) >= 10;

-- Final.
SELECT id, text, tweet_id, user_id, date_created
FROM tweets
WHERE id IN (
    SELECT tweet_id
    FROM tweet_views
    WHERE tweet_id NOT IN (
        SELECT tweet_id
        FROM tweets
        WHERE user_id IN (
            SELECT blocker_id
            FROM blacklist
            WHERE blocked_id = 19
        )
    )
    GROUP BY tweet_id
    HAVING COUNT(tweet_id) >= 10;
    ) 
OR (user_id IN (
        SELECT followee_id 
        FROM followings
        WHERE follower_id = 19 AND date_deleted NOT NULL
    ) AND tweet_id NOT IN (
        SELECT tweet_id
        FROM tweet_views
        WHERE user_id = 19
    )
);

-- Tweets from people I follow and that I haven't seen.
-- Tweets from people I follow...
SELECT id, text, tweet_id, user_id, date_created
FROM tweets
WHERE user_id IN (
    SELECT followee_id 
    FROM followings
    WHERE follower_id = 19 AND date_deleted NOT NULL
) 
-- ...and that I haven't seen.
AND tweet_id NOT IN (
    SELECT tweet_id
    FROM tweet_views
    WHERE user_id = 19
)
UNION
-- Big Tweets except from those who've blocked me.
SELECT id, text, tweet_id, user_id, date_created
FROM tweets
WHERE id IN (
    SELECT tweet_id
    FROM tweet_views
    WHERE tweet_id NOT IN (
        SELECT tweet_id
        FROM tweets
        WHERE user_id IN (
            SELECT blocker_id
            FROM blacklist
            WHERE blocked_id = 19
        )
    )
    GROUP BY tweet_id
    HAVING COUNT(tweet_id) >= 10;
);
