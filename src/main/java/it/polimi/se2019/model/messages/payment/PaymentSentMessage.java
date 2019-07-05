package it.polimi.se2019.model.messages.payment;

import it.polimi.se2019.model.playerassets.AmmoType;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.playerassets.Powerup;

import java.util.List;
import java.util.Map;

/**
 * Class for handling payment sent message
 * @author stefanominotti
 */
public class PaymentSentMessage extends PaymentMessage {

    private List<Powerup> powerups;

    /**
     * Class constructor, it builds a payment sent message
     * @param paymentType type of the payment
     * @param character character who has paid
     * @param ammos used to pay
     * @param powerups used to pay
     */
    public PaymentSentMessage(PaymentType paymentType, GameCharacter character,
                              Map<AmmoType, Integer> ammos, List<Powerup> powerups) {
        super(PaymentMessageType.SENT, paymentType, character, ammos);
        this.powerups = powerups;
    }

    /**
     * Gets the power ups used to pay
     * @return List of the powerups used to pay
     */
    public List<Powerup> getPowerups() {
        return this.powerups;
    }
}