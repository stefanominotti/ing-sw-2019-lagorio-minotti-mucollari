package it.polimi.se2019.model;

public class WeaponCard {

    private Weapon weaponType;
    private Player owner;
    private boolean ready;

    WeaponCard(Weapon weaponType) {
        this.weaponType = weaponType;
        this.owner = null;
        this.ready = true;
    }

    public Weapon getWeaponType() {
        return this.weaponType;
    }

    public Player getOwner() {
        return this.owner;
    }

    public boolean isReady() {
        return this.ready;
    }

    void setOwner(Player owner) {
        this.owner = owner;
    }

    void setReady(boolean ready) {
        this.ready = ready;
    }
}