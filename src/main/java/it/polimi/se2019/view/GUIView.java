package it.polimi.se2019.view;

import it.polimi.se2019.client.AbstractClient;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.nickname.NicknameMessage;
import it.polimi.se2019.model.messages.nickname.NicknameMessageType;
import it.polimi.se2019.model.messages.timer.TimerMessageType;

import java.rmi.RemoteException;

public class GUIView extends View {

    public GUIView(AbstractClient client) {
        super(client);
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

    public void handleNicknameInput(String input) throws RemoteException {
        getClient().send(new NicknameMessage(NicknameMessageType.CONNECTED, input));
    }
}
