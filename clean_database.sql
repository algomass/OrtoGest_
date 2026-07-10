-- Script per pulire i dati transazionali (Ordini e Magazzino) senza distruggere le tabelle
USE ortogest;

-- Disabilita temporaneamente i controlli sulle Foreign Key (Chiavi Esterne)
-- Questo ci permette di svuotare le tabelle senza che MySQL ci blocchi con errori di dipendenza
SET FOREIGN_KEY_CHECKS = 0;

-- 1. Elimina tutti i dettagli degli ordini (righe)
TRUNCATE TABLE riga_ordine;

-- 2. Elimina le testate degli ordini
TRUNCATE TABLE ordine;

-- 3. Elimina tutti i lotti di merce caricati in magazzino
TRUNCATE TABLE lotto;

-- 4. Azzera le quantità disponibili di tutti i prodotti (fondamentale perché non ci sono più lotti fisici)
SET SQL_SAFE_UPDATES = 0;
UPDATE prodotto SET quantita_disponibile = 0;
SET SQL_SAFE_UPDATES = 1;

-- (Opzionale) Se vuoi eliminare anche i PRODOTTI personalizzati che hai creato tu 
-- e tenere solo quelli di default definiti nello schema, rimuovi i trattini dalla riga seguente:
-- DELETE FROM prodotto WHERE nome NOT IN ('Mele Golden', 'Zucchine');

-- (Opzionale) Se vuoi eliminare i NUOVI UTENTI che hai registrato e tenere solo a, b, c, d:
-- DELETE FROM utente WHERE email NOT IN ('a', 'b', 'c', 'd');

-- Riabilita i controlli sulle Foreign Key
SET FOREIGN_KEY_CHECKS = 1;

SELECT 'Database pulito con successo. I prodotti sono a giacenza 0 e lo storico ordini è vuoto.' AS Risultato;
