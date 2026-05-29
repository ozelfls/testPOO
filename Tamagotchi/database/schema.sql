PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS creature_type (
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    nome     TEXT NOT NULL UNIQUE,
    estagio  INTEGER NOT NULL CHECK (estagio >= 0)
);

CREATE TABLE IF NOT EXISTS evolution (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    from_creature  INTEGER NOT NULL,
    to_creature    INTEGER NOT NULL,
    min_happiness  INTEGER NOT NULL CHECK (min_happiness BETWEEN 0 AND 100),
    max_hunger     INTEGER NOT NULL CHECK (max_hunger BETWEEN 0 AND 100),
    FOREIGN KEY (from_creature) REFERENCES creature_type(id),
    FOREIGN KEY (to_creature)   REFERENCES creature_type(id),
    UNIQUE (from_creature, to_creature)
);

CREATE TABLE IF NOT EXISTS pet (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    nome          TEXT NOT NULL,
    tipo_usuario  TEXT NOT NULL,
    hunger        INTEGER NOT NULL CHECK (hunger BETWEEN 0 AND 100),
    happiness     INTEGER NOT NULL CHECK (happiness BETWEEN 0 AND 100),
    energy        INTEGER NOT NULL CHECK (energy BETWEEN 0 AND 100),
    last_needs_update_epoch INTEGER NOT NULL DEFAULT (CAST(strftime('%s','now') AS INTEGER)),
    healthy_minutes INTEGER NOT NULL DEFAULT 0,
    care_count    INTEGER NOT NULL DEFAULT 0,
    neglect_minutes INTEGER NOT NULL DEFAULT 0,
    image_data    BLOB
);

CREATE TABLE IF NOT EXISTS pet_care_history (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    pet_id      INTEGER NOT NULL,
    event_type  TEXT NOT NULL,
    event_epoch INTEGER NOT NULL,
    hunger      INTEGER NOT NULL CHECK (hunger BETWEEN 0 AND 100),
    happiness   INTEGER NOT NULL CHECK (happiness BETWEEN 0 AND 100),
    energy      INTEGER NOT NULL CHECK (energy BETWEEN 0 AND 100),
    average     INTEGER NOT NULL CHECK (average BETWEEN 0 AND 100),
    note        TEXT,
    FOREIGN KEY (pet_id) REFERENCES pet(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS app_setting (
    chave  TEXT PRIMARY KEY,
    valor  TEXT
);

INSERT INTO creature_type (nome, estagio)
SELECT 'Ovo', 0 WHERE NOT EXISTS (SELECT 1 FROM creature_type WHERE nome = 'Ovo');

INSERT INTO creature_type (nome, estagio)
SELECT 'Baby', 1 WHERE NOT EXISTS (SELECT 1 FROM creature_type WHERE nome = 'Baby');

INSERT INTO creature_type (nome, estagio)
SELECT 'Dragon', 2 WHERE NOT EXISTS (SELECT 1 FROM creature_type WHERE nome = 'Dragon');

INSERT INTO creature_type (nome, estagio)
SELECT 'Angel', 2 WHERE NOT EXISTS (SELECT 1 FROM creature_type WHERE nome = 'Angel');

INSERT INTO creature_type (nome, estagio)
SELECT 'Demon', 2 WHERE NOT EXISTS (SELECT 1 FROM creature_type WHERE nome = 'Demon');

INSERT INTO evolution (from_creature, to_creature, min_happiness, max_hunger)
SELECT origem.id, destino.id, 70, 30
FROM creature_type origem, creature_type destino
WHERE origem.nome = 'Baby' AND destino.nome = 'Dragon'
  AND NOT EXISTS (
      SELECT 1 FROM evolution e
      WHERE e.from_creature = origem.id AND e.to_creature = destino.id
  );

INSERT INTO evolution (from_creature, to_creature, min_happiness, max_hunger)
SELECT origem.id, destino.id, 80, 20
FROM creature_type origem, creature_type destino
WHERE origem.nome = 'Baby' AND destino.nome = 'Angel'
  AND NOT EXISTS (
      SELECT 1 FROM evolution e
      WHERE e.from_creature = origem.id AND e.to_creature = destino.id
  );

INSERT INTO evolution (from_creature, to_creature, min_happiness, max_hunger)
SELECT origem.id, destino.id, 30, 80
FROM creature_type origem, creature_type destino
WHERE origem.nome = 'Baby' AND destino.nome = 'Demon'
  AND NOT EXISTS (
      SELECT 1 FROM evolution e
      WHERE e.from_creature = origem.id AND e.to_creature = destino.id
  );

INSERT INTO pet (nome, tipo_usuario, hunger, happiness, energy, image_data)
SELECT 'Tama', 'Ovo', 100, 100, 100, NULL
WHERE NOT EXISTS (SELECT 1 FROM pet);

INSERT INTO app_setting (chave, valor)
SELECT 'active_pet_id', CAST(id AS TEXT)
FROM pet
WHERE NOT EXISTS (SELECT 1 FROM app_setting WHERE chave = 'active_pet_id')
ORDER BY id
LIMIT 1;
