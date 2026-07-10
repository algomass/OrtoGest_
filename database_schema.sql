-- Script di inizializzazione del database OrtoGest

CREATE DATABASE IF NOT EXISTS ortogest;
USE ortogest;

CREATE TABLE IF NOT EXISTS prodotto (
    nome VARCHAR(255) PRIMARY KEY,
    prezzo_attuale DOUBLE NOT NULL,
    quantita_disponibile DOUBLE NOT NULL,
    categoria VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS lotto (
    id_lotto VARCHAR(100) PRIMARY KEY,
    nome_fornitore VARCHAR(255) NOT NULL,
    nome_prodotto VARCHAR(255) NOT NULL,
    quantita_kg DOUBLE NOT NULL,
    data_arrivo VARCHAR(50) NOT NULL,
    data_scadenza VARCHAR(50) NOT NULL,
    costo_acquisto DOUBLE NOT NULL,
    prezzo_vendita DOUBLE DEFAULT 0.0,
    sconto_attivo BOOLEAN DEFAULT FALSE,
    prezzo_scontato DOUBLE DEFAULT 0.0,
    smaltito BOOLEAN DEFAULT FALSE,
    ritirato BOOLEAN DEFAULT FALSE,
    FOREIGN KEY(nome_prodotto) REFERENCES prodotto(nome)
);

CREATE TABLE IF NOT EXISTS ordine (
    id_ordine VARCHAR(100) PRIMARY KEY,
    email_cliente VARCHAR(255) NOT NULL,
    stato VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS riga_ordine (
    id_riga INT AUTO_INCREMENT PRIMARY KEY,
    id_ordine VARCHAR(100) NOT NULL,
    nome_prodotto VARCHAR(255) NOT NULL,
    quantita DOUBLE NOT NULL,
    prezzo_fissato DOUBLE NOT NULL,
    FOREIGN KEY(id_ordine) REFERENCES ordine(id_ordine),
    FOREIGN KEY(nome_prodotto) REFERENCES prodotto(nome)
);

CREATE TABLE IF NOT EXISTS utente (
    email VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    ruolo VARCHAR(50) NOT NULL
);

-- Inserimento di un utente di default per poter accedere al sistema (Responsabile)
INSERT IGNORE INTO utente (email, password, ruolo) VALUES ('a', 'a', 'Responsabile');
INSERT IGNORE INTO utente (email, password, ruolo) VALUES ('b', 'b', 'Cliente');
INSERT IGNORE INTO utente (email, password, ruolo) VALUES ('c', 'c', 'Magazziniere');
INSERT IGNORE INTO utente (email, password, ruolo) VALUES ('d', 'd', 'Operatore');

-- Inserimento di qualche prodotto di base per avere un catalogo iniziale
INSERT IGNORE INTO prodotto (nome, prezzo_attuale, quantita_disponibile, categoria) VALUES 
('Mele Golden', 1.50, 120.0, 'Frutta'),
('Zucchine', 2.00, 70.0, 'Verdura');
