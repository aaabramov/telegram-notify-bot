CREATE TABLE users
(
    user_id BIGINT NOT NULL PRIMARY KEY
);

CREATE TABLE groups
(
    user_id BIGINT REFERENCES users (user_id),
    name    VARCHAR(256)   NOT NULL,
    users   VARCHAR(256)[] NOT NULL DEFAULT ARRAY []::VARCHAR[]
);