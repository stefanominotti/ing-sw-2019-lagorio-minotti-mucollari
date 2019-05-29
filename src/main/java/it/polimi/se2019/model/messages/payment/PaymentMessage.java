package it.polimi.se2019.model.messages.payment;

import it.polimi.se2019.model.AmmoType;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.MessageType;
import it.polimi.se2019.model.messages.SingleReceiverMessage;

import java.util.Map;

public class PaymentMessage extends Message implements SingleReceiverMessage {

    private GameCharacter character;
    private PaymentMessageType type;
    private PaymentType paymentType;
    private Map<AmmoType, Integer> ammos;

    public PaymentMessage(PaymentMessageType type, PaymentType paymentType, GameCharacter character,
                          Map<AmmoType, Integer> ammos) {
        setMessageType(MessageType.PAYMENT_MESSAGE);
        this.type = type;
        this.paymentType = paymentType;
        this.character = character;
        this.ammos = ammos;
    }

    @Override
    public GameCharacter getCharacter() {
        return this.character;
    }

    public PaymentMessageType getType() {
        return this.type;
    }

    public PaymentType getPaymentType() {
        return this.paymentType;
    }

    public Map<AmmoType, Integer> getAmmos() {
        return this.ammos;
    }
}
