package it.polimi.se2019.view;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class VirtualView extends Observable implements Observer {

    private List<BoardView> views;

    @Override
    public void update(Observable model, Object event) {}

    public void update(Observable model, MovePlayerMessage message) {}

    public void update(Observable model, IncrementScoreMessage message) {}

    public void update(Observable model, GivePowerupMessage message) {}

    public void update(Observable model, RemovePowerupMessage message) {}

    public void update(Observable model, GiveWeaponMessage message) {}

    public void update(Observable model, RemoveWeaponMessage message) {}

    public void update(Observable model, FillStoreMessage message) {}

    public void update(Observable model, DamageMessage message) {}

    public void update(Observable model, MarkMessage message) {}

    public void update(Observable model, AddKillshotMessage message) {}

    public void update(Observable model, ResetPlayerBoardMessage message) {}

    public void update(Observable model, GiveAmmoMessage message) {}

    public void update(Observable model, RemoveAmmoMessage message) {}

    public void update(Observable model, AddAmmoTileMessage message) {}

    public void update(Observable model, RemoveAmmoTileMessage message) {}
}
