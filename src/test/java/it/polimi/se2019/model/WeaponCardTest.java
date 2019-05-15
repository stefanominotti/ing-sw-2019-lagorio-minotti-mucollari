package it.polimi.se2019.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class WeaponCardTest {
    private WeaponCard weaponCard = new WeaponCard(Weapon.LOCK_RIFLE);
    private Player playerTest = new Player(GameCharacter.D_STRUCT_OR);

    @Test
    public void getWeaponTypeTest() { assertEquals(Weapon.LOCK_RIFLE, weaponCard.getWeaponType()); }

    @Test
    public void getOwnerTest() {
        assertNull(weaponCard.getOwner());
        weaponCard.setOwner(playerTest);
        assertEquals(playerTest, weaponCard.getOwner());
    }

    @Test
    public void setOwnerTest() {
        weaponCard.setOwner(playerTest);
        assertEquals(playerTest, weaponCard.getOwner());
    }

    @Test
    public void setReadyTest() {
        weaponCard.setReady(true);
        assertTrue(weaponCard.isReady());
    }

    @Test
    public void isReadyTest() {
        weaponCard.setReady(false);
        assertFalse(weaponCard.isReady());
        weaponCard.setReady(true);
        assertTrue(weaponCard.isReady());
    }
}