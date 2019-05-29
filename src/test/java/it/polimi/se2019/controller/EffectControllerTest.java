package it.polimi.se2019.controller;

import it.polimi.se2019.controller.EffectPossibilityPack;
import it.polimi.se2019.controller.EffectsController;
import it.polimi.se2019.controller.TurnController;
import it.polimi.se2019.model.*;
import it.polimi.se2019.server.GameLoader;
import org.junit.Before;
import org.junit.Test;


public class EffectControllerTest {
    Weapon weapon;
    Board board;
    TurnController turnController;
    EffectsController controller;

    @Test
    public void cyberbladeTest() {
        this.weapon = (new WeaponCard(Weapon.CYBERBLADE)).getWeaponType();
        this.board = (new GameLoader()).loadBoard();
        this.turnController = new TurnController(this.board, null);
        this.controller = new EffectsController(this.board, turnController);
        //this.turnController.setActivePlayer(this.board.getPlayers().get(this.board.getCurrentPlayer()));
        Player player = this.board.getPlayers().get(this.board.getCurrentPlayer());

        for(WeaponEffect effect : weapon.getPrimaryEffect()) {
            EffectPossibilityPack pack = controller.seeEffectpossibility(effect);
            controller.addHitbyMain(this.board.getPlayerByCharacter(pack.getCharacters().get(0)));
            controller.setChosenSquare(this.board.getPlayerByCharacter(pack.getCharacters().get(0)).getPosition());
            controller.setChosenRoom(this.board.getPlayerByCharacter(pack.getCharacters().get(0)).getPosition().getRoom());
        }
        for(WeaponEffect effect : weapon.getSecondaryEffectOne()) {
            EffectPossibilityPack pack = controller.seeEffectpossibility(effect);
            Coordinates coordinates = pack.getSquares().get(0);
            this.board.movePlayer(player, this.board.getArena().getSquareByCoordinate(coordinates.getX(), coordinates.getY()));
            controller.setChosenSquare(player.getPosition());
            controller.setChosenRoom(player.getPosition().getRoom());
        }
        for(WeaponEffect effect : weapon.getSecondaryEffectTwo()) {
            EffectPossibilityPack pack = controller.seeEffectpossibility(effect);
            //non fa nulla perchè nella casella in cui si sposta non ci sono nemici
        }
    }

    @Test
    public void electroscytheTest() {
        this.weapon = (new WeaponCard(Weapon.ELECTROSCYTHE)).getWeaponType();
        this.board = (new GameLoader()).loadBoard();
        this.turnController = new TurnController(this.board, null);
        this.controller = new EffectsController(this.board, turnController);
        //this.turnController.setActivePlayer(this.board.getPlayers().get(this.board.getCurrentPlayer()));
        Player player = this.board.getPlayers().get(this.board.getCurrentPlayer());

        for(WeaponEffect effect : weapon.getPrimaryEffect()) {
            EffectPossibilityPack pack = controller.seeEffectpossibility(effect);
            //sfruttando il targetAmount aggiungi ad Hitby main tutti i player colpibili
            //controller.addHitbyMain(qualcosa);
            //square e room non cambiano
            controller.setChosenSquare(this.board.getPlayers().get(this.board.getCurrentPlayer()).getPosition());
            controller.setChosenRoom(this.board.getPlayerByCharacter(pack.getCharacters().get(0)).getPosition().getRoom());
        }
        for(WeaponEffect effect : weapon.getAlternativeMode()) {
            EffectPossibilityPack pack = controller.seeEffectpossibility(effect);
            //sfruttando il targetAmount aggiungi ad Hitby main tutti i player colpibili
            //controller.addHitbyMain(qualcosa);
            //square e room non cambiano
            controller.setChosenSquare(this.board.getPlayers().get(this.board.getCurrentPlayer()).getPosition());
            controller.setChosenRoom(this.board.getPlayerByCharacter(pack.getCharacters().get(0)).getPosition().getRoom());
        }
    }
}
