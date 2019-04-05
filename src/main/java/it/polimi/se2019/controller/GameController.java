package it.polimi.se2019.controller;

import it.polimi.se2019.model.Board;

import java.util.Observer;

public class GameController implements Observer<Board> {

    private Board board;

    public GameController(Board board) {
        this.board = board;
    }

    private void newGame(int playersNumber) {

    }
}
