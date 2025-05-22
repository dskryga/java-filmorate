DROP TABLE IF EXISTS "users", "films", "friends", "likes", "genres", "mpa", "filmToGenre";

CREATE TABLE IF NOT EXISTS "users" (
  "id" bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "email" varchar,
  "login" varchar,
  "name" varchar,
  "birthday" timestamp
);

CREATE TABLE IF NOT EXISTS "films" (
  "id" bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "name" varchar,
  "description" varchar,
  "release_date" timestamp,
  "duration" integer,
  "genre_id" integer,
  "mpa_id" integer
);

CREATE TABLE IF NOT EXISTS "friends" (
  "id" bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "user_id" integer,
  "friend_id" integer
);

CREATE TABLE IF NOT EXISTS "likes" (
  "id" bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "user_id" integer,
  "film_id" integer
);

CREATE TABLE IF NOT EXISTS "genres" (
  "id" integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "genre_name" varchar NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS "mpa" (
  "id" integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "mpa_name" varchar NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS "filmToGenre" (
  "id" bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "film_id" bigint,
  "genre_id" integer
);

ALTER TABLE "likes" ADD FOREIGN KEY ("film_id") REFERENCES "films" ("id");

ALTER TABLE "likes" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "films" ADD FOREIGN KEY ("mpa_id") REFERENCES "mpa" ("id");

ALTER TABLE "friends" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "friends" ADD FOREIGN KEY ("friend_id") REFERENCES "users" ("id");

ALTER TABLE "filmToGenre" ADD FOREIGN KEY ("film_id") REFERENCES "films" ("id");

ALTER TABLE "filmToGenre" ADD FOREIGN KEY ("genre_id") REFERENCES "genres" ("id");
