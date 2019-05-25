package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.AmmoType;
import it.polimi.se2019.model.Powerup;

import java.util.List;
import java.util.Map;

public class WeaponPaymentMessage extends Message {

    private List<Powerup> paidPowerups;
    private Map<AmmoType, Integer> paidAmmos;

    public WeaponPaymentMessage(List<Powerup> paidPowerups, Map<AmmoType, Integer> paidAmmos) {
        setMessageType(this.getClass());
        this.paidPowerups = paidPowerups;
        this.paidAmmos = paidAmmos;
    }

    public List<Powerup> getPaidPowerups() {
        return this.paidPowerups;
    }

    public Map<AmmoType, Integer> getPaidAmmos() {
        return this.paidAmmos;
    }
}
