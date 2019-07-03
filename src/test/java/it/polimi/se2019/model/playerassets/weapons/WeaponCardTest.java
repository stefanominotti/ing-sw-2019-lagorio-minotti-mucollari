package it.polimi.se2019.model.playerassets.weapons;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.playerassets.weapons.Weapon;
import it.polimi.se2019.model.playerassets.weapons.WeaponCard;
import org.junit.Test;

import static org.junit.Assert.*;

public class WeaponCardTest {
    private WeaponCard weaponCard = new WeaponCard(Weapon.LOCK_RIFLE);

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