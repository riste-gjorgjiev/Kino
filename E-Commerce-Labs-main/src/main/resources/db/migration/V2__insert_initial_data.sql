-- Countries
INSERT INTO countries (name, continent)
VALUES ('United Kingdom', 'Europe'),
       ('United States', 'North America'),
       ('France', 'Europe'),
       ('Colombia', 'South America'),
       ('Russia', 'Europe'),
       ('North Macedonia', 'Europe');

-- Authors
INSERT INTO authors (name, surname, country_id)
VALUES ('J.R.R.', 'Tolkien', 1),
       ('George', 'Orwell', 1),
       ('Gabriel', 'García Márquez', 4),
       ('Leo', 'Tolstoy', 5),
       ('Agatha', 'Christie', 1),
       ('Jules', 'Verne', 3),
       ('Stephen', 'King', 2),
       ('Blazhe', 'Koneski', 6);

-- Books
INSERT INTO books (name, category, author_id, state, available_copies)
VALUES ('The Lord of the Rings', 'FANTASY', 1, 'GOOD', 5),
       ('The Hobbit', 'FANTASY', 1, 'GOOD', 3),
       ('1984', 'NOVEL', 2, 'GOOD', 4),
       ('Animal Farm', 'NOVEL', 2, 'GOOD', 6),
       ('One Hundred Years of Solitude', 'NOVEL', 3, 'GOOD', 2),
       ('War and Peace', 'HISTORY', 4, 'GOOD', 3),
       ('And Then There Were None', 'THRILLER', 5, 'GOOD', 7),
       ('Murder on the Orient Express', 'THRILLER', 5, 'GOOD', 4),
       ('Twenty Thousand Leagues', 'CLASSICS', 6, 'GOOD', 2),
       ('The Shining', 'THRILLER', 7, 'GOOD', 5),
       ('It', 'THRILLER', 7, 'BAD', 0),
       ('Makedonska Krvava Svadba', 'DRAMA', 8, 'GOOD', 3);
