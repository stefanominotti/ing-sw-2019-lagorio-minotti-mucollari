package it.polimi.se2019.model;

public class WeaponCard {

    private Weapon weaponType;
    private boolean ready;

    public WeaponCard(Weapon weaponType) {
        this.weaponType = weaponType;
        this.ready = true;
    }

    public Weapon getWeaponType() {
        return this.weaponType;
    }

    public boolean isReady() {
        return this.ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
