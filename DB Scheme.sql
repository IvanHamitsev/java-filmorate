CREATE TABLE rating (
  id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name varchar UNIQUE NOT NULL
);

CREATE TABLE films (
  id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name varchar NOT NULL,
  description varchar,
  releaseDate date,
  duration integer NOT NULL CHECK (duration > 0),
  rating_id integer REFERENCES rating (id) ON DELETE CASCADE
);

CREATE TABLE genres (
  id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name varchar UNIQUE NOT NULL
);

CREATE TABLE films_genre (
  film_id integer REFERENCES films (id) ON DELETE CASCADE NOT NULL,
  genre_id integer REFERENCES genres (id) ON DELETE CASCADE NOT NULL,
  PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE users (
  id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  first_name varchar NOT NULL,
  last_name varchar,
  login varchar UNIQUE NOT NULL,
  email varchar,
  birthday date,
  EXCLUDE USING btree (lower(email) WITH =)
);

CREATE TABLE friendship (
  source_id integer REFERENCES users (id) ON DELETE CASCADE NOT NULL,
  destination_id integer REFERENCES users (id) ON DELETE CASCADE NOT NULL,
  PRIMARY KEY (user1_id, user2_id)
);

CREATE TABLE likes (
  user_id integer REFERENCES users (id) ON DELETE CASCADE NOT NULL,
  film_id integer REFERENCES films (id) ON DELETE CASCADE NOT NULL,
  PRIMARY KEY (user_id, film_id)
);
