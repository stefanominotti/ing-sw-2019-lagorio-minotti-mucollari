package it.polimi.se2019.view;

import it.polimi.se2019.client.AbstractClient;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.nickname.NicknameMessage;
import it.polimi.se2019.model.messages.nickname.NicknameMessageType;
import it.polimi.se2019.model.messages.timer.TimerMessageType;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

public class GUIView extends View {

    private RegistrationFormApp GUIApp;

    public GUIView(int connection, RegistrationFormApp GUIApp) {
        super(connection);
        this.GUIApp = GUIApp;
    }

    void handleNicknameInput(String input) throws RemoteException {
        if (input.equalsIgnoreCase("")) {
            this.GUIApp.showAlert("Invalid input!");
            return;
        }
        getClient().send(new NicknameMessage(NicknameMessageType.CONNECTED, input));
    }

    @Override
    void handleNicknameRequest() {
        super.handleNicknameRequest();
        this.GUIApp.setScene(SceneType.SELECT_NICKNAME);
    }

    @Override
    void handleNicknameDuplicated() {
        super.handleNicknameDuplicated();
        this.GUIApp.showAlert("Nickname duplicated!");
    }

    @Override
    void handleCharacterSelectionRequest(List<GameCharacter> availables) {
        super.handleCharacterSelectionRequest(availables);
        this.GUIApp.setScene(SceneType.SELECT_CHARACTER);
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
