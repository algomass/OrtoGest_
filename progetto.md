# Regole Architetturali OrtoGest (BCE & MVC)

Questo documento definisce le linee guida architetturali e di design per lo sviluppo del progetto **OrtoGest** (Sistema gestionale per ortofrutta). L'intelligenza artificiale (Antigravity/Gemini) DEVE attenersi scrupolosamente a queste regole per ogni modifica o generazione di codice.

## 1. Architettura di Riferimento: BCE (Boundary-Control-Entity)
Il progetto implementa una separazione netta delle responsabilità seguendo i pattern BCE e MVC.
La struttura dei package deve essere coerente con i seguenti layer:

*   **`it.ortogest.graphiccontroller` / `it.ortogest.graphiccontrollercli` (Boundary/View)**
    *   **Ruolo**: Gestiscono esclusivamente l'interfaccia utente (JavaFX FXML o interfaccia a riga di comando CLI).
    *   **Regole Strict**:
        *   Non devono **MAI** contenere logica di business o algoritmi decisionali del dominio.
        *   Devono comunicare con gli Application Controller (`appcontroller`) esclusivamente scambiandosi **Beans** (DTO).
        *   Non devono mai accedere direttamente al database (`dao`) o ai modelli di dominio (`model`).

*   **`it.ortogest.appcontroller` (Control/Controller)**
    *   **Ruolo**: Coordinano la logica applicativa e l'esecuzione dei casi d'uso (Use Cases), come ad esempio "Crea Ordine" o "Gestisci Magazzino".
    *   **Regole Strict**:
        *   Ricevono input dai Graphic Controller sotto forma di Beans.
        *   Interagiscono con le `Entity` (Model) e i pattern per l'accesso ai dati (`DAO`) per applicare le regole di business.
        *   Restituiscono i risultati ai Graphic Controller, sempre impacchettati tramite Beans.

*   **`it.ortogest.model` (Entity/Model)**
    *   **Ruolo**: Rappresentano le entità di dominio (es. `Prodotto`, `Ordine`, `Utente`).
    *   **Regole Strict**:
        *   Contengono le regole intrinseche e lo stato di business.
        *   Non sanno nulla di interfacce grafiche, controller applicativi o database.

*   **`it.ortogest.beans` (Data Transfer Objects)**
    *   **Ruolo**: Oggetti leggeri usati per trasportare dati. Devono contenere solo attributi, getter/setter, e metodi per la validazione puramente *sintattica* (es. lunghezza stringa, email valida), ma nessuna logica di business.

*   **`it.ortogest.dao` o pattern in `model` (Data Access Object)**
    *   **Ruolo**: Incapsulano tutta la logica di accesso e persistenza dei dati (Database Relazionale, File System). Espongono interfacce standardizzate.

*   **`it.ortogest.utils` e `it.ortogest.pattern`**
    *   **Ruolo**: Classi di supporto e implementazione di design pattern specifici.

## 2. Regole di Design e Pattern Obbligatori

*   **Isolamento Tramite Beans**: Nessuna istanza delle classi `Entity` deve mai trapelare verso la vista (Boundary).
*   **Observer Pattern**: Da utilizzare per mantenere l'interfaccia grafica sincronizzata con lo stato del sistema (es. aggiornamenti dello stato dell'ordine). Le classi del dominio fungeranno da *Subject*, le View/Boundary da *Observer*.
*   **Adapter Pattern**: Obbligatorio per integrare sistemi esterni e API terze. Ad esempio, per l'integrazione con un eventuale terminale POS esterno, l'Application Controller non deve conoscere l'implementazione del fornitore, ma utilizzare un'interfaccia adattata.
*   **Singleton Pattern**: Da usare con moderazione ma essenziale per gestire risorse globali condivise (es. `SessionManager` per mantenere lo stato dell'utente correntemente loggato, `StageManager` per il cambio dinamico delle scene JavaFX).

## 3. Gestione JavaFX e Avvio
*   **Responsabilità dell'AppStarter**: Le classi main (`AppStarter` e `AppStarterCLI`) devono occuparsi solo dell'avvio e del caricamento del primo Boundary.
*   **Navigazione (Cambio Scena)**: Il cambio di schermata in JavaFX non deve comportare accoppiamento diretto tra due Graphic Controller. Delegare sempre questa funzionalità a un Singleton manageriale (es. `SingletonStage` o `StageManager`).
*   **Threading**: Qualsiasi aggiornamento visivo in risposta ad eventi asincroni deve avvenire all'interno del thread JavaFX usando `Platform.runLater()`.

## 4. Modalità Operativa dell'IA (Antigravity)
Ogni volta che si richiede la scrittura di codice, l'IA:
1.  **Analizzerà** la richiesta per identificare il caso d'uso e il pattern BCE/MVC.
2.  **Identificherà** i package corretti in cui operare.
3.  **Applicherà** rigorosamente i confini: rifiuterà approcci in cui un pulsante (Boundary) esegue una query SQL o aggiorna direttamente un'Entity.
4.  **Genererà** codice Java object-oriented pulito, commentato dove necessario, rispettando i principi SOLID.

## 5. Specifiche del Progetto OrtoGest

### 5.1 PANORAMICA DEL SISTEMA
Il sistema, denominato OrtoGest, è un'applicazione software progettata per la gestione integrata e ottimizzata di un punto vendita ortofrutticolo. Il sistema è strutturato per digitalizzare i processi quotidiani e supportare le operazioni di quattro tipologie di attori principali: il Cliente, il Magazziniere, l'Operatore (Cassiere) e il Responsabile. L'obiettivo primario è garantire la tracciabilità delle merci, ridurre l'invenduto e semplificare i processi di vendita sia fisici che digitali.

Le funzionalità principali del sistema si articolano nei seguenti moduli:
*   **Gestione Vendite (Cassa):** Permette all'operatore di registrare le vendite al dettaglio calcolando dinamicamente il costo totale dei prodotti. Il modulo supporta l'elaborazione dei pagamenti specificando il metodo scelto dal cliente, come contanti o circuito bancario/POS.
*   **Gestione Magazzino e Approvvigionamenti:** Consente al magazziniere di registrare i nuovi lotti in entrata, acquisendo dati fondamentali quali fornitore, tipologia di prodotto, peso, data di arrivo e di scadenza. Il sistema aggiorna automaticamente le giacenze. Integra inoltre una procedura automatizzata per la gestione delle anomalie di fornitura, permettendo di segnalare merce mancante o danneggiata e inoltrando direttamente una notifica via mail al Server Mail del fornitore.
*   **Gestione Catalogo e Prezzario:** Fornisce al responsabile gli strumenti di amministrazione per aggiungere o eliminare prodotti e modificarne liberamente il prezzo. Per incentivare l'acquisto e minimizzare gli sprechi, il sistema include una logica di monitoraggio quotidiano automatizzato: a 48 ore dalla data di scadenza dei prodotti in magazzino, il software applica in automatico una riduzione del prezzo di vendita.
*   **Gestione Ordini Online (Clicca & Ritira):** Offre al cliente la possibilità di creare ordini digitali verificando in tempo reale la disponibilità delle giacenze e specificando l'orario di ritiro previsto. L'operatore preleva la merce e aggiorna lo stato dell'ordine attraverso le fasi di lavorazione ("Inviato", "Pronto per il Ritiro", "Ritirato"), garantendo che l'inventario venga scalato definitivamente solo a pagamento e consegna avvenuti.

### 5.2 USER STORIES
*   **US-1**: Come cassiere voglio calcolare il costo totale dei prodotti venduti, affinché il cliente possa pagare il totale corretto.
*   **US-2**: Come responsabile voglio modificare il prezzo dei prodotti, affinché i clienti siano incentivati ad acquistarli, diminuendo la quantità di merce invenduta.
*   **US-3**: Come magazziniere voglio registrare un nuovo lotto di prodotti in entrata, indicando quantità e scadenza dei prodotti, affinché venga garantita la tracciabilità degli stessi.

### 5.3 FUNCTIONAL REQUIREMENTS
*   **FR-1**: Il sistema deve registrare un nuovo lotto di ortofrutta in entrata, acquisendo quantità, tipologia (nome, peso) e scadenza del prodotto.
*   **FR-2**: Il sistema deve elaborare il totale della vendita, applicando i prezzi vigenti dei prodotti.
*   **FR-3**: Il sistema deve provvedere ad aggiornare le giacenze di magazzino.
*   **FR-4**: Il sistema deve offrire la possibilità di modificare il prezzo del prodotto.
*   **FR-5**: Il sistema deve monitorare quotidianamente la data di scadenza dei prodotti presenti in magazzino e, 48 h prima della scadenza, applicare una riduzione del prezzo di vendita secondo una logica definita.

### 5.4 INTERNAL STEPS

**Nome: Crea ordine.**
1. Il cliente seleziona i prodotti dal catalogo online e inserisce le quantità desiderate.
2. Il sistema verifica la disponibilità delle giacenze in tempo reale.
3. Il cliente conferma l'ordine e specifica l'orario previsto per il ritiro.
4. Il sistema salva l'ordine con stato "Inviato" e genera un ID univoco.
5. Il sistema invia una notifica all'operatore aggiornando la sua lista "Ordini da preparare".
6. L'operatore prende in carico l'ordine, preleva i prodotti fisici e conferma il completamento a sistema.
7. Il sistema aggiorna lo stato dell'ordine in "Pronto per il Ritiro".
8. Il cliente si presenta in cassa e comunica l'ID ordine.
9. L'operatore procede alla registrazione del pagamento. 
10. Il sistema imposta l'ordine come "Ritirato" e scala definitivamente le quantità dal magazzino.
*Estensioni/Eccezioni:*
*   2a. Prodotto non disponibile: il sistema non permette l'inserimento nel carrello.
*   9a. Il cliente modifica l'ordine al momento del ritiro: Il sistema ricalcola il totale da pagare, e ripristina (o scala) le giacenze corrispondenti.

**Nome: Registra lotto**
1. Il magazziniere seleziona l'opzione “registra nuovo lotto” dalla schermata principale
2. Il sistema mostra un modulo di inserimento dati.
3. Il magazziniere inserisce le informazioni del documento di trasporto: nome fornitore, tipologia di prodotto, quantità (in kg), data di arrivo e data di scadenza.
4. Il magazziniere conferma l'inserimento cliccando su "Salva".
5. Il sistema verifica che i dati inseriti dal magazziniere siano coerenti con la tipologia richiesta nei campi.
6. Il sistema salva il nuovo lotto nel database e aggiorna automaticamente la quantità totale di quel prodotto disponibile in magazzino.
7. Il sistema mostra un messaggio di conferma: "Lotto registrato con successo" e genera un codice identificativo univoco (ID Lotto).
*Estensioni/Eccezioni:*
*   3a. Il magazziniere segnala merce danneggiata: il sistema apre una nuova schermata dedicata ai resi richiedendo di inserire quantità di merce danneggiata e descrizione del danno, che cliccando “inoltra segnalazione” verranno inoltrati al fornitore tramite Mail. Al termine dell'invio, il sistema riporta il magazziniere allo step 3 per ricalcolare e inserire la quantità di merce effettivamente idonea.
*   3b. Il magazziniere segnala merce mancante: il sistema apre una schermata dedicata ai reclami per segnalare la quantità di merce mancante, che cliccando “inoltra segnalazione” verrà comunicata al fornitore tramite Mail. Al termine dell'invio, il sistema riporta il magazziniere allo step 3 per inserire la quantità di merce realmente ricevuta.
*   5a. Il magazziniere immette dati non coerenti con la tipologia richiesta nei campi: il sistema mostra messaggio di errore evidenziando i campi non coerenti.