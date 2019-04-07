package it.polimi.se2019.view;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.RoomColor;
import it.polimi.se2019.model.Weapon;

import java.util.ArrayList;
import java.util.List;

public class FillStoreMessage {

    private RoomColor room;
    private List<Weapon> weapons;

    public FillStoreMessage(RoomColor room, List<Weapon> weapons) {
        this.room = room;
        this.weapons = weapons;
    }

    public RoomColor getRoom() {
        return room;
    }

    public List<Weapon> getWeapons() {
        return new ArrayList<>(weapons);
    }
}
