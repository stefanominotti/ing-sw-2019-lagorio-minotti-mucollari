# Prova Finale (Ingegneria del Software)
085923 - [San Pietro]

## Gruppo: 46
Antonio Lagorio - antonio.lagorio@mail.polimi.it

Stefano Minotti - stefano1.minotti@mail.polimi.it

Eknid Mucollari - eknid.mucollari@mail.polimi.it

## Requisiti sviluppati

- Regole semplificate
- Recole complete
- Socket
- RMI
- CLI
- GUI
- Persistenza


## Istruzioni per avviare il server 

Copiare il file `AdrenalineServerSettings.json` nella path user home:<br/>(es. `C:\Users\pippo` per Windows - `/Users/pippo` per Mac)

Il file contiene le seguenti impostazioni 
- Timer di gioco
- Porta Socket
- Porta RMI
- Nome del salvataggio

Nella path che contiene il jar, utilizzare il comando:

    $ java -jar adrenalina-server.jar

Oppure utilizzare gli script `adrenalina-server.sh` o `adrenalina-server.bat`


## Istruzioni per avviare il client 

Copiare il file `AdrenalineClientSettings.json` nella path user home:<br/>(es. `C:\Users\pippo` per Windows - `/Users/pippo` per Mac)

Il file contiene le seguenti impostazioni:
- Indirizzo e porta del server
- Tipologia di connessione (0 per Socket, 1 per RMI)
- UI (0 per CLI, 1 per GUI)

Nella path che contiene il jar, utilizzare il comando:

    $ java -jar adrenalina-client.jar

Oppure utilizzare gli script `adrenalina-client.sh` o `adrenalina-client.bat`

## UML

UML disponibile nella cartella `deliveries/final/UML` di questo repository