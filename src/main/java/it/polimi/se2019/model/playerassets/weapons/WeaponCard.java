package it.polimi.se2019.model.playerassets.weapons;

/**
 * Class for handling weapon card
 * @author stefanominotti
 */
public class WeaponCard {

    private Weapon weaponType;
    private boolean ready;

    /**
     * Class constructor, it builds a weapon card
     * @param weaponType weapon of which you want to build a card
     */
    public WeaponCard(Weapon weaponType) {
        this.weaponType = weaponType;
        this.ready = true;
    }

    /**
     * Gets the weapon of the card
     * @return the corresponding weapon
     */
    public Weapon getWeaponType() {
        return this.weaponType;
    }

    /**
     * Knows if the weapon card is ready to shoot
     * @return true if it is ready, else false
     */
    public boolean isReady() {
        return this.ready;
    }

    /**
     * Sets the weapon ready to shoot
     * @param ready true to make it ready, else false
     */
    public void setReady(boolean ready) {
        this.ready = ready;
    }
}