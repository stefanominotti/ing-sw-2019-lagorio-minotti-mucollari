package it.polimi.se2019.controller;

import it.polimi.se2019.model.Board;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.WeaponCard;

import java.util.Observable;
import java.util.Observer;

public class GameController implements Observer {

    private Board board;

    public GameController(Board board) {
        this.board = board;
    }

    void newGame(int playersNumber) {}

    @Override
    public void update(Observable view, Object event) {}

    public void update(Observable view, CardPressedEvent event) {}

    public void update(Observable view, ActionSelectedEvent event) {}

    public void update(Observable view, ShotEvent event) {}

    public void update(Observable view, PickupEvent event) {}

    public void update(Observable view, MoveEvent event) {}

    public void update(Observable view, RealoadEvent event) {}

    public void update(Observable view, EffectSelectedEvent event) {}

    public void update(Observable view, TargetSelectedEvent event) {}

    public void update(Observable view, PowerupCardSelectedEvent event) {}
}
