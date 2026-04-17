PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS creature_type (
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    nome     TEXT NOT NULL,
    estagio  INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS evolution (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    from_creature  INTEGER NOT NULL,
    to_creature    INTEGER NOT NULL,
    min_happiness  INTEGER NOT NULL,
    max_hunger     INTEGER NOT NULL,
    FOREIGN KEY (from_creature) REFERENCES creature_type(id),
    FOREIGN KEY (to_creature)   REFERENCES creature_type(id)
);

CREATE TABLE IF NOT EXISTS pet (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    nome          TEXT NOT NULL,
    tipo_usuario  TEXT NOT NULL,
    hunger        INTEGER NOT NULL,
    happiness     INTEGER NOT NULL,
    energy        INTEGER NOT NULL,
    image_data    BLOB
);

INSERT INTO creature_type (nome, estagio) VALUES
    ('Ovo',   0),
    ('Baby',  1),
    ('Dragon',2),
    ('Angel', 2),
    ('Demon', 2);

INSERT INTO evolution (from_creature, to_creature, min_happiness, max_hunger) VALUES
    (2, 3, 70, 30),
    (2, 4, 80, 20),
    (2, 5, 30, 80);

INSERT INTO pet (nome, tipo_usuario, hunger, happiness, energy, image_data)
VALUES ('Tama', 'Ovo', 50, 50, 50, NULL);
