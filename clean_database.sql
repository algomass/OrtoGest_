-- Script per pulire i dati transazionali (Ordini e Magazzino) senza distruggere le tabelle
USE ortogest;


SET FOREIGN_KEY_CHECKS = 0;


TRUNCATE TABLE riga_ordine;


TRUNCATE TABLE ordine;


TRUNCATE TABLE lotto;


SET SQL_SAFE_UPDATES = 0;
UPDATE prodotto SET quantita_disponibile = 0;
SET SQL_SAFE_UPDATES = 1;


DELETE FROM prodotto WHERE nome NOT IN ('Mele Golden', 'Zucchine');

DELETE FROM utente WHERE email NOT IN ('a', 'b', 'c', 'd');


SET FOREIGN_KEY_CHECKS = 1;

SELECT 'Database pulito con successo. I prodotti sono a giacenza 0 e lo storico ordini è vuoto.' AS Risultato;
