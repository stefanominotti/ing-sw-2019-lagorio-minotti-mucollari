package it.polimi.se2019.model.messages.payment;

import it.polimi.se2019.model.AmmoType;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Powerup;

import java.util.List;
import java.util.Map;

public class PaymentSentMessage extends PaymentMessage {

    private List<Powerup> powerups;

    public PaymentSentMessage(PaymentType paymentType, GameCharacter character,
                              Map<AmmoType, Integer> ammos, List<Powerup> powerups) {
        super(PaymentMessageType.SENT, paymentType, character, ammos);
        this.powerups = powerups;
    }

    public List<Powerup> getPowerups() {
        return this.powerups;
    }
}
