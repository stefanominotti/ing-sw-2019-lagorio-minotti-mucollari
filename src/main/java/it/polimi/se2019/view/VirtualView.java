package it.polimi.se2019.view;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.*;
import it.polimi.se2019.model.messages.board.BoardMessage;
import it.polimi.se2019.model.messages.board.BoardMessageType;
import it.polimi.se2019.model.messages.client.ClientDisconnectedMessage;
import it.polimi.se2019.model.messages.client.ClientMessage;
import it.polimi.se2019.model.messages.client.ClientMessageType;
import it.polimi.se2019.model.messages.payment.PaymentMessage;
import it.polimi.se2019.model.messages.payment.PaymentMessageType;
import it.polimi.se2019.model.messages.player.PlayerCreatedMessage;
import it.polimi.se2019.model.messages.player.PlayerMessage;
import it.polimi.se2019.model.messages.player.PlayerMessageType;
import it.polimi.se2019.model.messages.player.PlayerReadyMessage;
import it.polimi.se2019.model.messages.powerups.PowerupMessage;
import it.polimi.se2019.model.messages.powerups.PowerupMessageType;
import it.polimi.se2019.model.messages.selections.SelectionMessageType;
import it.polimi.se2019.model.messages.selections.SingleSelectionMessage;
import it.polimi.se2019.model.messages.turn.TurnMessage;
import it.polimi.se2019.model.messages.turn.TurnMessageType;
import it.polimi.se2019.server.Server;
import java.util.Observable;
import java.util.Observer;

/**
 * Class for handling virtual view
 * @author stefanominotti
 */
public class VirtualView extends Observable implements Observer {

    private Server server;
    private boolean reconnecting;

    /**
     * Class constructor, it builds a virtual view
     * @param server where the virtual view has to be hosted
     */
    public VirtualView(Server server) {
        this.server = server;
    }

    /**
     * Forwards a message, notifying the observers
     * @param message to be forwarded
     */
    public void forwardMessage(Message message) {
        setChanged();
        notifyObservers(message);
    }

    /**
     * Update the virtual view from the model
     * @param model to be observed
     * @param message updated
     */
    @Override
    public void update(Observable model, Object message) {
        switch (((Message) message).getMessageType()) {
            case PLAYER_MESSAGE:
                update((PlayerMessage) message);
                break;
            case CLIENT_MESSAGE:
                update((ClientMessage) message);
                break;
            case BOARD_MESSAGE:
                update((BoardMessage) message);
                break;
            case POWERUP_MESSAGE:
                update((PowerupMessage) message);
                break;
            case SELECTION_LIST_MESSAGE:
                send((SingleReceiverMessage) message);
                break;
            case SINGLE_SELECTION_MESSAGE:
                update((SingleSelectionMessage) message);
                break;
            case TURN_MESSAGE:
                update((TurnMessage) message);
                break;
            case PAYMENT_MESSAGE:
                update((PaymentMessage) message);
                break;
            default:
                sendAll((Message) message);
                break;
        }
    }

    /**
     * Uses to forward a single selection message to clients
     * @param message to be forwarded
     */
    private void update(SingleSelectionMessage message) {
        send(message);
    }

    /**
     * Uses to forward a board message to clients
     * @param message to be forwarded
     */
    private void update(BoardMessage message) {
        if (message.getType() == BoardMessageType.SETUP_INTERRUPTED) {
            this.server.setConnectionAllowed(true);
        }
        sendAll(message);
        if(message.getType() == BoardMessageType.GAME_FINISHED) {
            this.server.deleteGame();
        }
    }

    /**
     * Uses to forward a payment message to clients
     * @param message to be forwarded
     */
    private void update(PaymentMessage message) {
        if (message.getType() == PaymentMessageType.REQUEST) {
            send(message);
        } else {
            sendAll(message);
        }
    }

    /**
     * Uses to forward a player message to clients
     * @param message to be forwarded
     */
    private void update(PlayerMessage message) {
        if (message.getType() == PlayerMessageType.CREATED) {
            send(message);
            sendOthers(message.getCharacter(),
                    new PlayerReadyMessage(message.getCharacter(), ((PlayerCreatedMessage) message).getNickname()));
        } else if (message.getType() == PlayerMessageType.SKULLS_SET || message.getType() == PlayerMessageType.FRENZY) {
            send(message);
        } else if (message.getType() == PlayerMessageType.START_SETUP ||
                message.getType() == PlayerMessageType.MASTER_CHANGED) {
            this.server.setConnectionAllowed(false);
            sendAll(message);
        } else {
            sendAll(message);
        }
    }

    /**
     * Uses to forward a client message to clients
     * @param message to be forwarded
     */
    private void update(ClientMessage message) {
        switch (message.getType()) {
            case DISCONNECTED:
                this.server.removeClient(message.getCharacter());
                this.server.setConnectionAllowed(((ClientDisconnectedMessage) message).isReconnectionAllowed());
                sendAll(message);
                break;
            case GAME_ALREADY_STARTED:
                this.server.removeTemporaryClients(message);
                break;
            case LOAD_VIEW:
                this.server.setConnectionAllowed(false);
                send(message);
                break;
            default:
                break;
        }
    }

    /**
     * Uses to forward a powerup message to clients
     * @param message to be forwarded
     */
    private void update(PowerupMessage message) {
        if (message.getType() == PowerupMessageType.DISCARD) {
            sendAll(message);
            return;
        }
        if (message.getPowerup() == null) {
            sendOthers(message.getCharacter(), message);
        } else {
            send(message);
        }
    }

    /**
     * Uses to forward a turn message to clients
     * @param message to be forwarded
     */
    private void update(TurnMessage message) {
        if (message.getType() == TurnMessageType.START) {
            this.server.saveGame();
            sendAll(message);
        } else if (message.getType() == TurnMessageType.CONTINUATION) {
            send((SingleReceiverMessage) message);
        } else {
            sendAll(message);
        }
    }

    /**
     * Send a message to a single player
     * @param message to be sent
     */
    public void send(SingleReceiverMessage message) {
        this.server.send(message.getCharacter(), (Message) message);
    }

    /**
     * Send a message on broadcast
     * @param message to be sent
     */
    public void sendAll(Message message) {
        if (message.getMessageType() == MessageType.SINGLE_SELECTION_MESSAGE &&
                ((SingleSelectionMessage) message).getType() == SelectionMessageType.PERSISTENCE) {
            this.server.setConnectionAllowed(false);
        }
        this.server.sendAll(message);
    }

    /**
     * Send a message to other characters
     * @param character which the message has not be sent to
     * @param message to be sent
     */
    void sendOthers(GameCharacter character, Message message) {
        this.server.sendOthers(character, message);
    }

    /**
     * Resets the server after a save, ready to accept connections
     */
    public void resetServer() {
        this.server.resetServer();
    }
}
