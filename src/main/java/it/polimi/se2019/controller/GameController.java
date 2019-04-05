package it.polimi.se2019.controller;

import it.polimi.se2019.model.Board;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.WeaponCard;

import java.util.Observer;

public class GameController implements Observer<Board> {

    private Board board;

    public GameController(Board board) {
        this.board = board;
    }

    void newGame(int playersNumber) {}

    public void update(NewGameEvent event) {}

    public void update(CardPressedEvent event) {}

    public void update(ActionSelectedEvent event) {}

    public void update(ShotEvent event) {}

    public void update(PickupEvent event) {}

    public void update(MoveEvent event) {}

    public void update(RealoadEvent event) {}

    public void update(EffectSelectedEvent event) {}

    public void update(TargetSelectedEvent event) {}

    public void update(PowerupCardSelectedEvent event) {}
}
