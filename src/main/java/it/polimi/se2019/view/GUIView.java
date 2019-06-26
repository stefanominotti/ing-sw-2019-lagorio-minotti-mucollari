package it.polimi.se2019.view;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Powerup;
import it.polimi.se2019.model.Weapon;
import it.polimi.se2019.model.messages.board.ArenaMessage;
import it.polimi.se2019.model.messages.board.SkullsMessage;
import it.polimi.se2019.model.messages.client.CharacterMessage;
import it.polimi.se2019.model.messages.nickname.NicknameMessage;
import it.polimi.se2019.model.messages.nickname.NicknameMessageType;
import it.polimi.se2019.model.messages.timer.TimerMessageType;

import java.util.*;

import static it.polimi.se2019.view.ClientState.*;

public class GUIView extends View {

    private GUIApp GUIApp;
    private AbstractSceneController controller;

    public GUIView(int connection, String ip, GUIApp GUIApp) {
        super();
        this.GUIApp = GUIApp;
        super.connect(connection, ip);
    }

    void setActiveController(AbstractSceneController controller) {
        this.controller = controller;
    }

    private void setScene(SceneType scene) {
        this.controller = null;
        this.GUIApp.setScene(scene);
        synchronized (this) {
            while(this.controller == null) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void handleNicknameInput(String input) {
        if (input.equalsIgnoreCase("")) {
            this.GUIApp.showAlert("Invalid input!");
            return;
        }
        getClient().send(new NicknameMessage(NicknameMessageType.CONNECTED, input));
    }

    void handleCharacterInput(GameCharacter character) {
        if (getState() == CHOOSINGCHARACTER) {
            getClient().send(new CharacterMessage(character, generateToken()));
        }
    }

    void handleSkullsInput(int skullsNumber) {
        getClient().send(new SkullsMessage(skullsNumber));
    }

    void handleArenaInput(String arenaNumber) {
        getClient().send(new ArenaMessage(arenaNumber));
    }

    @Override
    void handleReconnectionRequest() {
        super.handleReconnectionRequest();
        setScene(SceneType.RELOAD_GAME);
    }

    @Override
    public void handleConnectionError() {
        this.GUIApp.setScene(SceneType.CONNECTION_ERROR);
        (new Timer()).schedule(new TimerTask() {
            @Override
            public void run() {
               GUIView.super.handleConnectionError();
            }
        }, 7*1000L);
    }

    @Override
    void handleInvalidToken() {
        this.GUIApp.setScene(SceneType.INVALID_TOKEN);
    }

    @Override
    void handleNicknameRequest() {
        super.handleNicknameRequest();
        setScene(SceneType.SELECT_NICKNAME);
    }

    @Override
    void handleNicknameDuplicated() {
        if (getState() == CHOOSINGCHARACTER) {
            setScene(SceneType.SELECT_NICKNAME);
            super.resetSelections();
        }
        super.handleNicknameDuplicated();
        this.GUIApp.showAlert("Nickname duplicated!");
    }

    @Override
    void handleCharacterSelectionRequest(List<GameCharacter> availables) {
        super.handleCharacterSelectionRequest(availables);
        if(!getCharactersSelection().isEmpty()) {
            this.GUIApp.showAlert("Character already choosen!");
            ((SelectCharacterController) this.controller).enableCharacters(availables);
            return;
        }
        setCharactersSelection(availables);
        setScene(SceneType.SELECT_CHARACTER);
        ((SelectCharacterController) this.controller).enableCharacters(availables);
    }

    @Override
    void handleClientDisconnected(GameCharacter character) {
        super.handleClientDisconnected(character);
        if (getState() == WAITINGSTART || getState() == WAITINGSETUP) {
            ((LobbyController) this.controller).removePlayer(character);
        }
    }

    @Override
    void handlePlayerCreated(GameCharacter character, String nickname, Map<GameCharacter,
            String> otherPlayers) {
        super.handlePlayerCreated(character, nickname, otherPlayers);
        Map<GameCharacter, String> players = new LinkedHashMap<>(otherPlayers);
        players.put(character, nickname);
        setScene(SceneType.LOBBY);
        ((LobbyController) this.controller).setMessage("loading.gif", "Waiting for players...");
        ((LobbyController) this.controller).setPlayers(players);
    }

    @Override
    void handleReadyPlayer(GameCharacter character, String nickname) {
        super.handleReadyPlayer(character, nickname);
        if (getState() == WAITINGSTART) {
            ((LobbyController) this.controller).addPlayer(character, nickname);
        }
    }

    @Override
    void handleSetupInterrupted() {
        if(getState() == SETTINGSKULLS || getState() == SETTINGARENA) {
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

    @Override
    void handleGameSetupTimer(TimerMessageType action, long duration) {
        if (getState() == WAITINGSTART) {
            switch (action) {
                case START:
                    ((LobbyController) this.controller).setMessage("loading.gif", "Setup will start soon...");
                    break;
                case STOP:
                    ((LobbyController) this.controller).setMessage("loading.gif", "Waiting for players...");
                    break;
            }
        }
    }

    @Override
    void handleStartSetup(GameCharacter character) {
        super.handleStartSetup(character);
        if (character == getCharacter()) {
            setScene(SceneType.SELECT_SKULLS);
        } else {
            ((LobbyController) this.controller).setMessage("setting.gif", "Master player is setting up the game...");
        }
    }

    @Override
    void handleSkullsSet() {
        super.handleSkullsSet();
        setScene(SceneType.SELECT_ARENA);
    }

    @Override
    void handleMasterChanged(GameCharacter character) {
        super.handleMasterChanged(character);
        if (character == getCharacter()) {
            setScene(SceneType.SELECT_SKULLS);
        } else {
        }
    }

    @Override
    void handleEndTurn(GameCharacter character) {

    }

    @Override
    void handleGameFinished(Map<GameCharacter, Integer> ranking) {

    }

    @Override
    void handlePersistenceFinish() {

    }

    @Override
    public void requirePayment() {

    }
}
