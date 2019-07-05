package it.polimi.se2019.model.messages.payment;

import it.polimi.se2019.model.playerassets.AmmoType;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.MessageType;
import it.polimi.se2019.model.messages.SingleReceiverMessage;

import java.util.Map;

/**
 * Class for handling payment message
 * @author stefanominotti
 */
public class PaymentMessage extends Message implements SingleReceiverMessage {

    private GameCharacter character;
    private PaymentMessageType type;
    private PaymentType paymentType;
    private Map<AmmoType, Integer> ammos;

    /**
     * Class constructor, it builds a payment message
     * @param type of the payment message
     * @param paymentType type of the payment to require
     * @param character which the payment is required to
     * @param ammos which need to be paid
     */
    public PaymentMessage(PaymentMessageType type, PaymentType paymentType, GameCharacter character,
                          Map<AmmoType, Integer> ammos) {
        setMessageType(MessageType.PAYMENT_MESSAGE);
        this.type = type;
        this.paymentType = paymentType;
        this.character = character;
        this.ammos = ammos;
    }

    /**
     * Gets the addressee character of the payment request
     * @return the Game Character who has to pay
     */
    @Override
    public GameCharacter getCharacter() {
        return this.character;
    }

    /**
     * Gets the type of the payment message
     * @return the type of the payment message
     */
    public PaymentMessageType getType() {
        return this.type;
    }

    /**
     * Gets the payment type
     * @return the type of the payment
     */
    public PaymentType getPaymentType() {
        return this.paymentType;
    }

    /**
     * Gets the ammos which need to be paid
     * @return Map with ammo and its quantity to be paid
     */
    public Map<AmmoType, Integer> getAmmos() {
        return this.ammos;
    }
}