package it.polimi.se2019.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class WeaponCardTest {
    private WeaponCard weaponCard = new WeaponCard(Weapon.LOCK_RIFLE);
    private Player playerTest = new Player(GameCharacter.D_STRUCT_OR, "testNickname", "token");

    @Test
    public void getWeaponTypeTest() {
        assertEquals(Weapon.LOCK_RIFLE, weaponCard.getWeaponType());
    }

    @Test
    public void setReadyTest() {
        this.weaponCard.setReady(true);
        assertTrue(weaponCard.isReady());
    }

    @Test
    public void isReadyTest() {
        this.weaponCard.setReady(false);
        assertFalse(this.weaponCard.isReady());
        this.weaponCard.setReady(true);
        assertTrue(this.weaponCard.isReady());
    }
}