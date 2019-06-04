package it.polimi.se2019.view;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.board.ArenaMessage;
import it.polimi.se2019.model.messages.board.SkullsMessage;
import it.polimi.se2019.model.messages.client.CharacterMessage;
import it.polimi.se2019.model.messages.nickname.NicknameMessage;
import it.polimi.se2019.model.messages.nickname.NicknameMessageType;
import it.polimi.se2019.model.messages.timer.TimerMessageType;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static it.polimi.se2019.view.ClientState.*;

public class GUIView extends View {

    private GUIApp GUIApp;
    private AbstractSceneController controller;

    public GUIView(int connection, it.polimi.se2019.view.GUIApp GUIApp) {
        super(connection);
        this.GUIApp = GUIApp;
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

    void handleNicknameInput(String input) throws RemoteException {
        if (input.equalsIgnoreCase("")) {
            this.GUIApp.showAlert("Invalid input!");
            return;
        }
        getClient().send(new NicknameMessage(NicknameMessageType.CONNECTED, input));
    }

    void handleCharacterInput(GameCharacter character) throws RemoteException {
        if (getState() == CHOOSINGCHARACTER) {
            getClient().send(new CharacterMessage(character, generateToken()));
        }
    }

    void handleSkullsInput(int skullsNumber) throws RemoteException {
        getClient().send(new SkullsMessage(skullsNumber));
    }

    void handleArenaInput(String arenaNumber) throws RemoteException {
        getClient().send(new ArenaMessage(arenaNumber));
        System.out.println("Arena Scelta");
    }

    @Override
    void handleNicknameRequest() {
        super.handleNicknameRequest();
        setScene(SceneType.SELECT_NICKNAME);
    }

    @Override
    void handleNicknameDuplicated() {
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
        if (getState() == WAITINGSTART) {
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
    void handleStartSetup(GameCharacter character) {
        super.handleStartSetup(character);
        if (character == getCharacter()) {
            setScene(SceneType.SELECT_SKULLS);
        } else {
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
    void handleGameSetupTimer(TimerMessageType action, long duration) {

    }

    @Override
    void handleEndTurn(GameCharacter character) {

    }

    @Override
    public void requirePayment() {

    }
}
