package it.polimi.se2019.server;

import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.model.Board;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.messages.*;
import it.polimi.se2019.view.VirtualView;

import java.rmi.RemoteException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    private Map<GameCharacter, VirtualClientInterface> clients;
    private Map<VirtualClientInterface, String> clientNicknames;
    private GameLoader gameLoader;
    private VirtualView view;
    private GameController controller;
    private Board model;
    private boolean connectionAllowed;
    private List<VirtualClientInterface> temporaryClients;

    Server() {
        this.clients = new EnumMap<>(GameCharacter.class);
        this.connectionAllowed = true;
        this.temporaryClients = new ArrayList<>();
        this.clientNicknames = new HashMap<>();
    }

    public static void main(String[] args) throws RemoteException {
        Server server = new Server();
        server.gameLoader = new GameLoader();
        server.startMVC(server.gameLoader.loadBoard());
        new SocketServer(server);
        new RMIProtocolServer(server);
        System.out.println("Waiting for clients.\n");
    }



    private void startMVC(Board board) {
        this.model = board;
        this.view = new VirtualView(this);
        this.controller = new GameController(this.model, this.view);
        this.view.addObserver(this.controller);
        this.model.addObserver(this.view);
    }

    public List<GameCharacter> getClientsList() {
        return new ArrayList<>(this.clients.keySet());
    }

    int getClientsNumber() {
        return this.clients.size();
    }

    public void setConnectionAllowed(boolean allowed) {
        this.connectionAllowed = allowed;
    }

    boolean isConnectionAllowed() {
        return this.connectionAllowed;
    }

    void addClient(VirtualClientInterface client) {
        ((Thread) client).start();
        this.temporaryClients.add(client);
        if(this.model.getArena() != null) {
            try {
                client.send(new ReconnectionMessage());
            } catch (RemoteException e) {
                LOGGER.log(Level.SEVERE, ("Error on sending message: "), e);
            }
        } else {
            try {
                client.send(new RequireNicknameMessage());
            } catch (RemoteException e) {
                LOGGER.log(Level.SEVERE, ("Error on sending message: "), e);
            }
        }
    }

    public void removeClient(GameCharacter character, Message message) throws RemoteException {
        this.clients.get(character).sendClose(message);
        this.clients.remove(character);
        System.out.println("Client disconnected.");
    }

    public void removeClient(VirtualClientInterface client) {
        client.exit();
        for (Map.Entry<GameCharacter, VirtualClientInterface> c : this.clients.entrySet()) {
            if (c.getValue() == client) {
                this.clients.remove(c.getKey());
                break;
            }
        }
        this.clientNicknames.remove(client);
        System.out.println("Client disconnected.");
    }

    public void removeClient(GameCharacter character) {

        this.clients.get(character).exit();
        this.clients.remove(character);
        System.out.println("Client disconnected.");
    }

    public void removeTemporaryClients(Message message) throws RemoteException {
        for (VirtualClientInterface client : this.temporaryClients) {
            client.send(message);
            client.exit();
            this.clientNicknames.remove(client);
        }
        this.temporaryClients = new ArrayList<>();
    }

    public void send(GameCharacter character, Message message) throws RemoteException {
        this.clients.get(character).send(message);
    }

    public void sendAll(Message message) throws RemoteException {
        for (VirtualClientInterface client : this.clients.values()) {
            client.send(message);
        }
    }

    public void sendOthers(GameCharacter character, Message message) throws RemoteException {
        for (Map.Entry<GameCharacter, VirtualClientInterface> client : this.clients.entrySet()) {
            if (client.getKey() == character) {
                continue;
            }
            client.getValue().send(message);
        }
    }

    public void sendOthers(List<GameCharacter> character, Message message) throws RemoteException {
        for (Map.Entry<GameCharacter, VirtualClientInterface> client : this.clients.entrySet()) {
            if (character.contains(client.getKey())) {
                continue;
            }
            client.getValue().send(message);
        }
    }

    void receiveMessage(Message message, VirtualClientInterface client) throws RemoteException {
        String messageType = message.getMessageType().getName()
                .replace("it.polimi.se2019.model.messages.", "");
        if (this.clients.size() == 5) {
            client.send(new GameAlreadyStartedMessage());
        }

        List<GameCharacter> availables = new ArrayList<>();
        switch (messageType) {
            case "NicknameMessage":
                for(Player player : this.model.getPlayers()) {
                    if (player.getNickname().equals(((NicknameMessage) message).getNickname())) {
                        client.send(new NicknameDuplicatedMessage());
                        return;
                    }
                }
                this.clientNicknames.put(client, ((NicknameMessage) message).getNickname());
                for(GameCharacter character : GameCharacter.values()){
                    if(!this.clients.containsKey(character)){
                        availables.add(character);
                    }
                }
                client.send(new CharacterMessage(availables));
                break;
            case "CharacterMessage":
                for(Player player : this.model.getPlayers()) {
                    if (player.getNickname().equals(this.clientNicknames.get(client))) {
                        client.send(new NicknameDuplicatedMessage());
                        return;
                    }
                }
                GameCharacter playerCharacter = ((CharacterMessage) message).getCharacter();
                if(this.clients.containsKey(playerCharacter)) {
                    for(GameCharacter character : GameCharacter.values()){
                        if(!this.clients.containsKey(character)){
                            availables.add(character);
                        }
                    }
                    client.send(new CharacterMessage(availables));
                    return;
                }
                this.clients.put(playerCharacter, client);
                this.temporaryClients.remove(client);
                System.out.println("A new client connected.");
                this.view.forwardMessage(
                        new ClientReadyMessage(((CharacterMessage) message).getCharacter(),
                                this.clientNicknames.get(client), ((CharacterMessage) message).getToken()));
                this.clientNicknames.remove(client);
                break;
            case "ReconnectionMessage":
                for(Player player : this.model.getPlayers()) {
                    GameCharacter character = player.verifyPlayer(((ReconnectionMessage) message).getToken());
                    if(character != null && this.clients.containsKey(character)) {
                        this.clients.put(player.getCharacter(), client);
                        this.temporaryClients.remove(client);
                        this.clientNicknames.remove(client);
                        System.out.println("A new client connected.");
                        this.view.forwardMessage(new ClientReconnectedMessage(player.getCharacter()));
                        return;
                    }
                }
                client.send(new InvalidTokenMessage());
                break;
            default:
                this.view.forwardMessage(message);
        }
    }

    public void saveGame() {
        this.gameLoader.saveBoard();
    }

    void notifyDisconnection(VirtualClientInterface client) {
        for (Map.Entry<GameCharacter, VirtualClientInterface> c : this.clients.entrySet()) {
            if (c.getValue() == client) {
                this.view.forwardMessage(new ClientDisconnectedMessage(c.getKey()));
                break;
            }
        }
    }
}
