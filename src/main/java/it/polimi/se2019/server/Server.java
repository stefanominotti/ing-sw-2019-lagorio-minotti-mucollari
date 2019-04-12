package it.polimi.se2019.server;

import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.model.Board;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.ClientReadyMessage;
import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.view.VirtualView;

import java.rmi.RemoteException;
import java.util.EnumMap;
import java.util.Map;

public class Server {

    private Map<GameCharacter, VirtualClientInterface> clients;
    private VirtualView view;
    private GameController controller;
    private Board model;

    public Server() {
        this.clients = new EnumMap<>(GameCharacter.class);
    }

    public static void main(String[] args) throws RemoteException {
        Server server = new Server();
        server.startMVC();
        SocketServer socketServer = new SocketServer(server);
        RMIProtocolServer rmiServer = new RMIProtocolServer(server);
        System.out.println("Waiting for clients.\n");
    }

    public void startMVC() {
        this.model = new Board();
        this.controller = new GameController(this.model);
        this.view = new VirtualView(this);
        this.view.addObserver(this.controller);
        this.model.addObserver(this.view);
    }

    public void addClient(VirtualClientInterface client) {
        for(GameCharacter character : GameCharacter.values()) {
            if (!this.clients.containsKey(character)) {
                this.clients.put(character, client);
                this.view.forwardMessage(new ClientReadyMessage(character));
                break;
            }
        }
        System.out.println("A new client connected.");
    }

    public void removeClient(VirtualClientInterface client) {
        client.exit();
        this.clients.remove(client);
        System.out.println("Client disconnected.");
    }

    public int getClientsNumber() {
        return this.clients.size();
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

    public void receiveMessage(Message message) {
        this.view.forwardMessage(message);
    }
}
