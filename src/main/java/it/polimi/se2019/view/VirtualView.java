package it.polimi.se2019.view;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class VirtualView extends Observable implements Observer {

    private List<BoardView> views;

    @Override
    public void update(Observable model, Object event) {}
}
