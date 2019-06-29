package it.polimi.se2019.view;

import it.polimi.se2019.model.*;
import it.polimi.se2019.model.messages.board.ArenaMessage;
import it.polimi.se2019.model.messages.board.SkullsMessage;
import it.polimi.se2019.model.messages.client.CharacterMessage;
import it.polimi.se2019.model.messages.nickname.NicknameMessage;
import it.polimi.se2019.model.messages.nickname.NicknameMessageType;
import it.polimi.se2019.model.messages.timer.TimerMessageType;

import java.util.*;

import static it.polimi.se2019.view.ClientState.*;

/**
 * Class for handling GUI view
 */
public class GUIView extends View {

    private GUIApp guiApp;
    private AbstractSceneController controller;

    /**
     * Class constructor, it builds a CLI view
     * @param connection "0" for socket, "1" for RMI
     * @param ip of the server
     * @param port of the server
     * @param guiApp the gui app
     */
    GUIView(int connection, String ip, int port,  GUIApp guiApp) {
        super();
        this.guiApp = guiApp;
        super.connect(connection, ip, port);
    }

    /**
     * Sets the controller for GUI scene
     * @param controller to be set
     */
    void setActiveController(AbstractSceneController controller) {
        this.controller = controller;
    }

    /**
     * Sets the scene to be shown
     * @param scene to bne shown
     */
    private void setScene(SceneType scene) {
        this.controller = null;
        this.guiApp.setScene(scene);
        synchronized (this) {
            while(this.controller == null) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * Handles client nickname input
     * @param input nickname of the client
     */
    void handleNicknameInput(String input) {
        if (input.equalsIgnoreCase("")) {
            this.guiApp.showAlert("Invalid input!");
            return;
        }
        getClient().send(new NicknameMessage(NicknameMessageType.CONNECTED, input));
    }

    /**
     * Handles client character choice
     * @param character chosen
     */
    void handleCharacterInput(GameCharacter character) {
        if (getState() == CHOOSING_CHARACTER) {
            getClient().send(new CharacterMessage(character, generateToken()));
        }
    }

    /**
     * Handles client skulls number input
     * @param skullsNumber skulls number chosen
     */
    void handleSkullsInput(int skullsNumber) {
        getClient().send(new SkullsMessage(skullsNumber));
    }

    /**
     * Handles client arena choice
     * @param arenaNumber arena chosen
     */
    void handleArenaInput(String arenaNumber) {
        getClient().send(new ArenaMessage(arenaNumber));
    }

    /**
     * Sets the scene for reconnection attempt
     */
    @Override
    void handleReconnectionRequest() {
        super.handleReconnectionRequest();
        setScene(SceneType.RELOAD_GAME);
    }

    /**
     * Sets the scene for connection error
     */
    @Override
    public void handleConnectionError() {
        this.guiApp.setScene(SceneType.CONNECTION_ERROR);
        (new Timer()).schedule(new TimerTask() {
            @Override
            public void run() {
               GUIView.super.handleConnectionError();
            }
        }, 7*1000L);
    }

    /**
     * Sets the scene for invalid token
     */
    @Override
    void handleInvalidToken() {
        this.guiApp.setScene(SceneType.INVALID_TOKEN);
    }

    /**
     * Sets the scene for nickname request
     */
    @Override
    void handleNicknameRequest() {
        super.handleNicknameRequest();
        setScene(SceneType.SELECT_NICKNAME);
    }


    /**
     * Handles case of nickname duplicated, resetting the scene
     */
    @Override
    void handleNicknameDuplicated() {
        if (getState() == CHOOSING_CHARACTER) {
            setScene(SceneType.SELECT_NICKNAME);
            super.resetSelections();
        }
        super.handleNicknameDuplicated();
        this.guiApp.showAlert("Nickname duplicated!");
    }

    /**
     * Sets the scene for character selection request
     */
    @Override
    void handleCharacterSelectionRequest(List<GameCharacter> availables) {
        super.handleCharacterSelectionRequest(availables);
        if(!getCharactersSelection().isEmpty()) {
            this.guiApp.showAlert("Character already choosen!");
            ((SelectCharacterController) this.controller).enableCharacters(availables);
            return;
        }
        setCharactersSelection(availables);
        setScene(SceneType.SELECT_CHARACTER);
        ((SelectCharacterController) this.controller).enableCharacters(availables);
    }

    /**
     * Handles a player disconnection fromt the lobby when a game is not yet started
     * @param character
     */
    @Override
    void handleClientDisconnected(GameCharacter character) {
        super.handleClientDisconnected(character);
        if (getState() == WAITING_START || getState() == WAITING_SETUP) {
            ((LobbyController) this.controller).removePlayer(character);
        }
    }

    /**
     * Sets lobby scene when a player setup is finished
     * @param character chosen
     * @param nickname chosen
     * @param otherPlayers Map with game characters and their nicknames
     */
    @Override
    void handlePlayerCreated(GameCharacter character, String nickname, Map<GameCharacter, String> otherPlayers) {
        super.handlePlayerCreated(character, nickname, otherPlayers);
        Map<GameCharacter, String> players = new LinkedHashMap<>(otherPlayers);
        players.put(character, nickname);
        setScene(SceneType.LOBBY);
        ((LobbyController) this.controller).setMessage("loading.gif", "Waiting for players...");
        ((LobbyController) this.controller).setPlayers(players);
    }

    /**
     * Handles player ready, adding it to the the controller
     * @param character chosen
     * @param nickname chosen
     */
    @Override
    void handleReadyPlayer(GameCharacter character, String nickname) {
        super.handleReadyPlayer(character, nickname);
        if (getState() == WAITING_START) {
            ((LobbyController) this.controller).addPlayer(character, nickname);
        }
    }

    /**
     * Handles case of game setup aborting, resetting the scene to "lobby" and "waiting for players" state
     */
    @Override
    void handleSetupInterrupted() {
        if(getState() == SETTING_SKULLS || getState() == SETTING_ARENA) {
            setScene(SceneType.LOBBY);
            Map<GameCharacter, String> players = new LinkedHashMap<>();
            players.put(getCharacter(), getSelfPlayerBoard().getNickname());
            for (PlayerBoard player : getEnemyBoards()) {
                players.put(player.getCharacter(), player.getNickname());
            }
            ((LobbyController) this.controller).setPlayers(players);
        }
        super.handleSetupInterrupted();
        ((LobbyController) this.controller).setMessage("loading.gif", "Waiting for players...");
    }

    /**
     * Handles game setup timer
     * @param action type of timer message
     * @param duration
     */
    @Override
    void handleGameSetupTimer(TimerMessageType action, long duration) {
        if (getState() == WAITING_START) {
            switch (action) {
                case START:
                    ((LobbyController) this.controller).setMessage("loading.gif", "Setup will start soon...");
                    break;
                case STOP:
                    ((LobbyController) this.controller).setMessage("loading.gif", "Waiting for players...");
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Handles start setup, setting the scene
     * @param character
     */
    @Override
    void handleStartSetup(GameCharacter character) {
        super.handleStartSetup(character);
        if (character == getCharacter()) {
            setScene(SceneType.SELECT_SKULLS);
        } else {
            ((LobbyController) this.controller).setMessage("setting.gif", "Master player is setting up the game...");
        }
    }

    /**
     * Sets select arena scene when skulls number is set
     */
    @Override
    void handleSkullsSet() {
        super.handleSkullsSet();
        setScene(SceneType.SELECT_ARENA);
    }

    /**
     * Handles case of master changed resetting the scene to select skulls
     * @param character new master
     */
    @Override
    void handleMasterChanged(GameCharacter character) {
        super.handleMasterChanged(character);
        if (character == getCharacter()) {
            setScene(SceneType.SELECT_SKULLS);
        }
    }

    /**
     * Shows game settings message
     * @param colors Map with room colors and their coordinates
     * @param spawns Map with coordinates and true if they are a spawn point, else false
     * @param nearbyAccessibility Map with coordinates  and map with cardinal points and their accessibility
     * @param skulls set for the game
     * @param arena chosen for the game
     */
    @Override
    void handleGameSet(Map<Coordinates, RoomColor> colors, Map<Coordinates, Boolean> spawns,
                       Map<Coordinates, Map<CardinalPoint, Boolean>> nearbyAccessibility, int skulls, int arena) {
        super.handleGameSet(colors, spawns, nearbyAccessibility, skulls, arena);
        setScene(SceneType.BOARD);
        ((BoardController) this.controller).updateKillshotTrack();
        ((BoardController) this.controller).setPlayerBoard(getCharacter());
    }

    @Override
    void handleStoresRefilled(Map<Coordinates, Weapon> weapons) {
        super.handleStoresRefilled(weapons);
        ((BoardController) this.controller).updateStores();
    }

    /**
     * Handles end turn of a player
     * @param character who ends the turn
     */
    @Override
    void handleEndTurn(GameCharacter character) {

    }

    /**
     * Handles game finish setting the ranking scene
     * @param ranking map with game characters and total points raised
     */
    @Override
    void handleGameFinished(Map<GameCharacter, Integer> ranking) {

    }

    /**
     * Handles persistence finish
     */
    @Override
    void handlePersistenceFinish() {

    }

    /**
     * Handles require payment
     */
    @Override
    public void requirePayment() {

    }
}
