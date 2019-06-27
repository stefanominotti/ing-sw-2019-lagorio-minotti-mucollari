package it.polimi.se2019.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.model.Board;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.messages.*;
import it.polimi.se2019.model.messages.client.*;
import it.polimi.se2019.model.messages.nickname.NicknameMessage;
import it.polimi.se2019.model.messages.nickname.NicknameMessageType;
import it.polimi.se2019.view.VirtualView;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.rmi.RemoteException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private static final String PATH = System.getProperty("user.home");
    private static final String SERVER_SETTINGS = "/server_settings.json";
    private static final int DEFAULT_PORT_SOCKET = 1099;
    private static final int DEFAULT_PORT_RMI = 12345;

    private Map<GameCharacter, VirtualClientInterface> clients;
    private Map<VirtualClientInterface, String> clientNicknames;
    private GameLoader gameLoader;
    private VirtualView view;
    private Board model;
    private boolean connectionAllowed;
    private List<VirtualClientInterface> temporaryClients;
    private SocketServer socketServer;
    private RMIProtocolServer rmiProtocolServer;
    private int portSocket;
    private int portRMI;

    private Server() {
        this.portRMI = DEFAULT_PORT_RMI;
        this.portSocket = DEFAULT_PORT_SOCKET;
        try {
            FileReader settings = new FileReader(PATH + SERVER_SETTINGS);
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            JsonObject jsonElement = (JsonObject)parser.parse(settings);
            this.portSocket = gson.fromJson(jsonElement.get("portSocket"), Integer.class);
            this.portRMI = gson.fromJson(jsonElement.get("portRMI"), Integer.class);
            if(portRMI == portSocket) {
                LOGGER.log(Level.SEVERE, "Invalid ports, ports set to default (RMI: " + DEFAULT_PORT_RMI +
                        ", Socket: " + DEFAULT_PORT_SOCKET + ")");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Invalid settings file, ports set to default (RMI: " + DEFAULT_PORT_RMI +
                    ", Socket: " + DEFAULT_PORT_SOCKET + ")");
        }
        resetServer();
    }

    public static void main(String[] args) {
        new Server();
    }

    private void startMVC(Board board) {
        this.model = board;
        this.view = new VirtualView(this);
        GameController controller = new GameController(this.model, this.view);
        this.view.addObserver(controller);
        this.model.addObserver(this.view);
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
            client.send(new ReconnectionMessage());
        } else {
            client.send(new NicknameMessage(NicknameMessageType.REQUIRE));
        }
    }

    public void removeClient(GameCharacter character) {

        this.clients.get(character).exit();
        this.clients.remove(character);
        LOGGER.log(Level.INFO, "Client disconnected");
    }

    public void removeTemporaryClients(Message message) {
        for (VirtualClientInterface client : this.temporaryClients) {
            client.send(message);
            client.exit();
            this.clientNicknames.remove(client);
        }
        this.temporaryClients = new ArrayList<>();
    }

    public void send(GameCharacter character, Message message) {
        this.clients.get(character).send(message);
    }

    public void sendAll(Message message) {
        for (VirtualClientInterface client : this.clients.values()) {
            client.send(message);
        }
    }

    public void sendOthers(GameCharacter character, Message message) {
        for (Map.Entry<GameCharacter, VirtualClientInterface> client : this.clients.entrySet()) {
            if (client.getKey() == character) {
                continue;
            }
            client.getValue().send(message);
        }
    }

    void receiveMessage(Message message, VirtualClientInterface client) {
        if (this.clients.size() == 5 && !this.clients.containsValue(client)) {
            client.send(new ClientMessage(ClientMessageType.GAME_ALREADY_STARTED, null));
        }
        List<GameCharacter> availables = new ArrayList<>();
        switch (message.getMessageType()) {
            case NICKNAME_MESSAGE:
                for(Player player : this.model.getPlayers()) {
                    if (player.getNickname().equals(((NicknameMessage) message).getNickname())) {
                        client.send(new NicknameMessage(NicknameMessageType.DUPLICATED));
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
            case CLIENT_MESSAGE:
                if (((ClientMessage) message).getType() == ClientMessageType.CHARACTER_SELECTION) {
                    for (Player player : this.model.getPlayers()) {
                        if (player.getNickname().equals(this.clientNicknames.get(client))) {
                            client.send(new NicknameMessage(NicknameMessageType.DUPLICATED));
                            return;
                        }
                    }
                    GameCharacter playerCharacter = ((CharacterMessage) message).getCharacter();
                    if (this.clients.containsKey(playerCharacter)) {
                        for (GameCharacter character : GameCharacter.values()) {
                            if (!this.clients.containsKey(character)) {
                                availables.add(character);
                            }
                        }
                        client.send(new CharacterMessage(availables));
                        return;
                    }
                    this.clients.put(playerCharacter, client);
                    this.temporaryClients.remove(client);
                    LOGGER.log(Level.INFO, "A new client connected");
                    this.view.forwardMessage(
                            new ClientReadyMessage(((CharacterMessage) message).getCharacter(),
                                    this.clientNicknames.get(client), ((CharacterMessage) message).getToken()));
                    this.clientNicknames.remove(client);
                } else {
                    for (Player player : this.model.getPlayers()) {
                        GameCharacter character = player.verifyPlayer(((ReconnectionMessage) message).getToken());
                        if (character != null && !this.clients.containsKey(character)) {
                            this.clients.put(player.getCharacter(), client);
                            this.temporaryClients.remove(client);
                            this.clientNicknames.remove(client);
                            LOGGER.log(Level.INFO, "A new client connected");
                            this.view.forwardMessage(new ClientMessage(ClientMessageType.RECONNECTED,
                                    player.getCharacter()));
                            return;
                        }
                    }
                    client.send(new ClientMessage(ClientMessageType.INVALID_TOKEN));
                }
                break;
            default:
                this.view.forwardMessage(message);
        }
    }

    public void saveGame() {
        this.gameLoader.saveBoard();
    }

    public void deleteGame() {
        this.gameLoader.deleteGame();
        resetServer();
    }

    public void resetServer() {
        if(this.socketServer != null) {
            while (!this.socketServer.isClosed()) {
                socketServer.stopServer();
            }
        }
        if(this.rmiProtocolServer != null) {
            this.rmiProtocolServer.stopServer();
        }
        this.clients = new EnumMap<>(GameCharacter.class);
        this.connectionAllowed = true;
        this.temporaryClients = new ArrayList<>();
        this.clientNicknames = new HashMap<>();
        this.gameLoader = new GameLoader();
        startMVC(this.gameLoader.loadBoard());
        this.socketServer = new SocketServer(this, this.portSocket);
        try {
            this.rmiProtocolServer = new RMIProtocolServer(this, this.portRMI);
        } catch (RemoteException e) {
            LOGGER.log(Level.SEVERE, "Unable to start RMI server");
            System.exit(0);
        }
        LOGGER.log(Level.INFO, "Waiting for clients");
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
