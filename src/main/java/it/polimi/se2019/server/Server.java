package it.polimi.se2019.server;

import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.model.Board;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.ClientDisconnectedMessage;
import it.polimi.se2019.model.messages.ClientReadyMessage;
import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.view.VirtualView;

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

    public Server() {
        this.clients = new EnumMap<>(GameCharacter.class);
    }

    public static void main(String[] args) throws RemoteException {
        Server server = new Server();
        server.startMVC();
        new SocketServer(server);
        new RMIProtocolServer(server);
        System.out.println("Waiting for clients.\n");
    }

    private void startMVC() {
        this.model = new Board();
        this.controller = new GameController(this.model);
        this.view = new VirtualView(this);
        this.view.addObserver(this.controller);
        this.model.addObserver(this.view);
    }

    public List<GameCharacter> getClientsList() {
        return new ArrayList<>(this.clients.keySet());
    }

    int getClientsNumber() {
        return this.clients.size();
    }

    void addClient(VirtualClientInterface client) {
        ((Thread) client).start();
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

    public void adjustClients(List<GameCharacter> characters) {
        List<GameCharacter> toRemove = new ArrayList<>();
        for (GameCharacter character : this.clients.keySet()) {
            if (!characters.contains(character)) {
                toRemove.add(character);
            }
        }
        for (GameCharacter character : toRemove) {
            this.clients.remove(character);
        }
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

    void receiveMessage(Message message) {
        this.view.forwardMessage(message);
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
