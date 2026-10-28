CREATE KEYSPACE IF NOT EXISTS telemetria
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

USE telemetria;

CREATE TABLE IF NOT EXISTS motos (
    id text PRIMARY KEY,
    matricula text,
    modelo text,
    bateria int,
    velocidad double,
    latitud double,
    longitud double,
    estado text,
    timestamp timestamp
);

CREATE TABLE IF NOT EXISTS motos_por_flota (
    flota text,
    id text,
    matricula text,
    modelo text,
    bateria int,
    velocidad double,
    latitud double,
    longitud double,
    estado text,
    timestamp timestamp,
    PRIMARY KEY ((flota), id)
);

CREATE TABLE IF NOT EXISTS telemetria_motos (
    id_moto text,
    timestamp timestamp,
    matricula text,
    modelo text,
    bateria int,
    velocidad double,
    latitud double,
    longitud double,
    estado text,
    PRIMARY KEY ((id_moto), timestamp)
) WITH CLUSTERING ORDER BY (timestamp DESC);
