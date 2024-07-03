CREATE TABLE "Films" (
  "film_id" integer PRIMARY KEY,
  "name" varchar,
  "description" varchar,
  "releaseDate" date,
  "duration" integer,
  "rating_id" integer
);

CREATE TABLE "Films_genre" (
  "film_id" integer,
  "genre_id" integer
);

CREATE TABLE "Genre" (
  "genre_id" integer PRIMARY KEY,
  "name" varchar
);

CREATE TABLE "Rating" (
  "rating_id" integer PRIMARY KEY,
  "name" varchar
);

CREATE TABLE "User" (
  "user_id" integer PRIMARY KEY,
  "first_name" varchar,
  "last_name" varchar,
  "login" varchar,
  "email" varchar,
  "birthday" date
);

CREATE TABLE "Friendship" (
  "user1_id" integer,
  "user2_id" integer,
  "user1_authorized" bool,
  "user2_authorized" bool
);

CREATE TABLE "Like" (
  "user_id" integer,
  "film_id" integer
);

ALTER TABLE "Films_genre" ADD FOREIGN KEY ("film_id") REFERENCES "Films" ("film_id");

ALTER TABLE "Films_genre" ADD FOREIGN KEY ("genre_id") REFERENCES "Genre" ("genre_id");

ALTER TABLE "Films" ADD FOREIGN KEY ("rating_id") REFERENCES "Rating" ("rating_id");

ALTER TABLE "Friendship" ADD FOREIGN KEY ("user1_id") REFERENCES "User" ("user_id");

ALTER TABLE "Friendship" ADD FOREIGN KEY ("user2_id") REFERENCES "User" ("user_id");

ALTER TABLE "Like" ADD FOREIGN KEY ("user_id") REFERENCES "User" ("user_id");

ALTER TABLE "Like" ADD FOREIGN KEY ("film_id") REFERENCES "Films" ("film_id");
