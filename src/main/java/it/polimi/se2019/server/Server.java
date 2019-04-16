package it.polimi.se2019.server;

import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.model.Board;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.*;
import it.polimi.se2019.view.VirtualView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Server {

    private Map<GameCharacter, VirtualClientInterface> clients;
    private VirtualView view;
    private GameController controller;
    private Board model;
    private boolean canAccept;

    Server() {
        this.clients = new EnumMap<>(GameCharacter.class);
        this.canAccept = true;
    }

    public static void main(String[] args) throws RemoteException {
        Server server = new Server();
        server.startMVC();
        try {
            new SocketServer(server);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            new RMIProtocolServer(server);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        System.out.println("Waiting for clients.\n");
    }

    boolean canAccept() {
        return this.canAccept;
    }

    public void denyConnections() {
        this.canAccept = false;
    }

    void startMVC() {
        this.model = new Board();
        this.controller = new GameController(this.model);
        this.view = new VirtualView(this);
        this.view.addObserver(this.controller);
        this.model.addObserver(this.view);
    }

    void addClient(VirtualClientInterface client) {
        for(GameCharacter character : GameCharacter.values()) {
            if (!this.clients.containsKey(character)) {
                this.clients.put(character, client);
                this.view.forwardMessage(new ClientReadyMessage(character));
                break;
            }
        }
        System.out.println("A new client connected.");
    }

    public List<GameCharacter> getClientsList() {
        return new ArrayList<>(this.clients.keySet());
    }

    public void removeClient(VirtualClientInterface client) {
        client.exit();
        for (Map.Entry<GameCharacter, VirtualClientInterface> c : this.clients.entrySet()) {
            if (c.getValue() == client) {
                this.clients.remove(c.getKey());
                break;
            }
        }
        System.out.println("Client disconnected.");
    }

    public void removeClient(GameCharacter character) {
        this.clients.remove(character);
        System.out.println("Client disconnected.");
    }

    int getClientsNumber() {
        return this.clients.size();
    }

    public void send(GameCharacter character, Message message) throws IOException {
        this.clients.get(character).send(message);
    }

    public void sendAll(Message message) throws IOException {
        for (VirtualClientInterface client : this.clients.values()) {
            client.send(message);
        }
    }

    public void sendOthers(GameCharacter character, Message message) throws IOException {
        for (Map.Entry<GameCharacter, VirtualClientInterface> client : this.clients.entrySet()) {
            if (client.getKey() == character) {
                continue;
            }
            client.getValue().send(message);
        }
    }

    public void sendOthers(List<GameCharacter> character, Message message) throws IOException {
        for (Map.Entry<GameCharacter, VirtualClientInterface> client : this.clients.entrySet()) {
            if (character.contains(client.getKey())) {
                continue;
            }
            client.getValue().send(message);
        }
    }

    void receiveMessage(Message message) {
        this.view.forwardMessage(message);
    }

    void notifyDisconnection(VirtualClientInterface client) {
        for (Map.Entry<GameCharacter, VirtualClientInterface> c : this.clients.entrySet()) {
            if (c.getValue() == client) {
                this.view.forwardMessage(new ClientDisconnectedMessage(c.getKey()));
            }
        }
    }
}
