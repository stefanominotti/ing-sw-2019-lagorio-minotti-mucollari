package it.polimi.se2019.controller;

import it.polimi.se2019.model.*;
import it.polimi.se2019.model.messages.*;

import java.util.*;

import static it.polimi.se2019.controller.TurnState.*;
import static java.util.stream.Collectors.toMap;

public class TurnController {

    private GameController controller;
    private Board board;
    private Player activePlayer;
    private int movesLeft;
    private TurnState state;
    private Player powerupTarget;
    private boolean finalFrenzy;
    private WeaponCard switchingWeapon;

    public TurnController(Board board, GameController controller) {
        this.state = TurnState.SELECTACTION;
        this.board = board;
        this.controller = controller;
        this.movesLeft = 2;
    }

    Player getActiveplayer() {
        return this.activePlayer;
    }

    void startTurn(TurnType type, GameCharacter player) {
        this.activePlayer = this.board.getPlayerByCharacter(player);
        switch (type) {
            case FIRST_TURN:
                this.movesLeft = 2;
                this.state = FIRST_RESPAWNING;
                this.board.drawPowerup(this.activePlayer);
                this.board.drawPowerup(this.activePlayer);
                this.controller.send(new DiscardToSpawnMessage(player));
                break;
            case AFTER_DEATH:
                this.movesLeft = 0;
                this.state = DEATH_RESPAWNING;
                countScore();
                this.board.drawPowerup(this.activePlayer);
                this.controller.send(new DiscardToSpawnMessage(player));
                break;
            case NORMAL:
                this.movesLeft = 2;
                if (this.finalFrenzy) {
                    // manda azioni frenesia finale
                } else {
                    List<ActionType> availableActions = Arrays.asList(ActionType.MOVE, ActionType.PICKUP, ActionType.SHOT);
                    this.controller.send(new AvailableActionsMessage(this.activePlayer.getCharacter(), availableActions));
                }
                this.state = SELECTACTION;
                break;
        }
    }

    private void countScore() {
        Map<GameCharacter, Integer> playersOrder = new LinkedHashMap<>();
        int points;
        List<GameCharacter> damages = this.activePlayer.getDamages();
        this.board.getPlayerByCharacter(damages.get(0)).raiseScore(1);
        for(Player player : this.board.getPlayers()) {
            points = 0;
            for(GameCharacter present : damages){
                if(player.getCharacter() == present) {
                    points++;
                }
            }
            if(points > 0) {
                playersOrder.put(player.getCharacter(), points);
            }
        }
        playersOrder = playersOrder.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));
        int index = 0;
        for(GameCharacter player : playersOrder.keySet()) {
            this.board.raisePlayerScore(this.board.getPlayerByCharacter(player),
                    this.getActiveplayer().getKillshotPoints().get(index));
            index++;
        }
    }

    void handlePowerupDiscarded(Powerup powerup) {
        this.board.removePowerup(this.activePlayer, powerup);

        if(this.state == TurnState.FIRST_RESPAWNING || this.state == TurnState.DEATH_RESPAWNING) {
            spawnPlayer(RoomColor.valueOf(powerup.getColor().toString()));
        } else {
            //usa powerup
        }
    }

    void spawnPlayer(RoomColor color) {
        for(Room room : this.board.getArena().getRoomList()){
            if(room.getColor() == color){
                this.board.respawnPlayer(this.activePlayer, room);
                if(this.state == TurnState.FIRST_RESPAWNING) {
                    List<ActionType> availableActions = Arrays.asList(ActionType.MOVE, ActionType.PICKUP, ActionType.SHOT);
                    this.controller.send(new AvailableActionsMessage(this.activePlayer.getCharacter(), availableActions));
                    this.state = TurnState.SELECTACTION;
                }
                if (this.state == TurnState.DEATH_RESPAWNING){
                    this.board.endTurn(this.activePlayer);
                }
            }
        }
    }

    void calculateMovementAction() {
        Square position = this.activePlayer.getPosition();
        List<Coordinates> movements = new ArrayList<>();
        for (Square s : this.board.getArena().getAllSquares()) {
            if(s == position) {
                continue;
            }
            List<List<Square>> paths = position.pathsTo(s);
            for (List<Square> path : paths) {
                if (path.size() - 1 <= 3) {
                    movements.add(new Coordinates(s.getX(), s.getY()));
                    break;
                }
            }
        }

        this.controller.send(new AvailableMoveActionMessage(this.activePlayer.getCharacter(), movements));
    }

    void calculatePickupAction() {
        Square position = this.activePlayer.getPosition();
        int maxDistance = 1;
        if (this.activePlayer.getDamages().size() > 2) {
            maxDistance = 2;
        }
        List<Coordinates> movements = new ArrayList<>();
        for (Square s : this.board.getArena().getAllSquares()) {
            if(s.getWeaponsStore() != null && s.getWeaponsStore().size() == 0) {
                continue;
            }
            if(s.getAvailableAmmoTile() == null && s.getWeaponsStore() == null) {
                continue;
            }
            List<List<Square>> paths = position.pathsTo(s);
            for (List<Square> path : paths) {
                if (path.size() - 1 <= maxDistance) {
                    movements.add(new Coordinates(s.getX(), s.getY()));
                    break;
                }
            }
        }

        this.controller.send(new AvailablePickupActionMessage(this.activePlayer.getCharacter(), movements));
    }

    void handleAction(ActionType action) {
        this.movesLeft--;
        switch (action) {
            case CANCEL:
                cancelAction();
                break;
            case ENDTURN:
                this.state = ENDING;
                handleRecharge();
                break;
            case MOVE:
                this.state = MOVING;
                calculateMovementAction();
                break;
            case PICKUP:
                this.state = PICKINGUP;
                calculatePickupAction();
                break;
        }
    }

    void cancelAction() {
        if(!this.finalFrenzy) {
            this.movesLeft++;
            List<ActionType> availableActions = Arrays.asList(ActionType.MOVE, ActionType.PICKUP, ActionType.SHOT);
            this.controller.send(new AvailableActionsMessage(this.activePlayer.getCharacter(), availableActions));
            this.state = TurnState.SELECTACTION;
        } else {
            // manda azioni frenesia
        }
    }

    void handleEndAction() {
        if (this.movesLeft == 0) {
            handleRecharge();
            return;
        }
        if (this.finalFrenzy) {
            // invia mosse frenesia finale
            return;
        }
        List<ActionType> availableActions = Arrays.asList(ActionType.MOVE, ActionType.PICKUP, ActionType.SHOT);
        this.controller.send(new AvailableActionsMessage(this.activePlayer.getCharacter(), availableActions));
        this.state = TurnState.SELECTACTION;
    }

    void movementAction(Coordinates coordinates) {
        this.board.movePlayer(this.activePlayer, this.board.getArena().getSquareByCoordinate(coordinates.getX(), coordinates.getY()));
        handleEndAction();
    }

    void pickupAction(Coordinates coordinates) {
        int x = coordinates.getX();
        int y = coordinates.getY();
        Square targetSquare = this.board.getArena().getSquareByCoordinate(x, y);
        if (this.activePlayer.getPosition() != targetSquare) {
            this.board.movePlayer(this.activePlayer, targetSquare);
        }
        if (targetSquare.getWeaponsStore() != null) {
            List<Weapon> availableWeapons = new ArrayList<>();
            for (WeaponCard weapon : this.activePlayer.getPosition().getWeaponsStore()) {
                boolean valid = true;
                for (Map.Entry<AmmoType, Integer> ammoCost : weapon.getWeaponType().getBuyCost().entrySet()) {
                    if (ammoCost.getValue() > this.activePlayer.getAvailableAmmos().get(ammoCost.getKey())) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    availableWeapons.add(weapon.getWeaponType());
                }
            }
            this.controller.send(new WeaponPickupSelectionMessage(this.activePlayer.getCharacter(), availableWeapons));
            return;
        }
        this.board.giveAmmoTile(this.activePlayer, targetSquare.getAvailableAmmoTile());
        handleEndAction();
    }

    void pickupWeapon(Weapon weapon) {
        if (this.activePlayer.getWeapons().size() == 3) {
            for (WeaponCard weaponCard : this.activePlayer.getPosition().getWeaponsStore()) {
                if (weaponCard.getWeaponType() == weapon) {
                    this.switchingWeapon = weaponCard;
                    break;
                }
            }
            this.controller.send(new RequireWeaponSwitchMessage(this.activePlayer.getCharacter(), weapon));
            return;
        }
        this.board.useAmmos(this.activePlayer, weapon.getBuyCost());
        for (WeaponCard weaponCard : this.activePlayer.getPosition().getWeaponsStore()) {
            if (weaponCard.getWeaponType() == weapon) {
                this.board.giveWeapon(this.activePlayer, weaponCard);
                break;
            }
        }
        handleEndAction();
    }

    void switchWeapon(Weapon weapon) {
        this.board.useAmmos(this.activePlayer, weapon.getBuyCost());
        for (WeaponCard weaponCard : this.activePlayer.getPosition().getWeaponsStore()) {
            if (weaponCard.getWeaponType() == weapon) {
                this.board.switchWeapon(this.activePlayer, this.switchingWeapon, weaponCard);
                break;
            }
        }
    }

    void handleRecharge() {
        List<Weapon> unloadedWeapons = new ArrayList<>();
        for (WeaponCard weapon : this.activePlayer.getWeapons()) {
            if (weapon.isReady()) {
                continue;
            }
            Map<AmmoType, Integer> requiredAmmo = new EnumMap<>(AmmoType.class);
            requiredAmmo.putAll(weapon.getWeaponType().getBuyCost());
            int toSum = requiredAmmo.get(weapon.getWeaponType().getColor()) + 1;
            requiredAmmo.put(weapon.getWeaponType().getColor(), toSum);
            boolean rechargeable = true;
            for (Map.Entry<AmmoType, Integer> ammo : requiredAmmo.entrySet()) {
                if (this.activePlayer.getAvailableAmmos().get(ammo.getKey()) < ammo.getValue()) {
                    rechargeable = false;
                    break;
                }
            }
            if(rechargeable) {
                unloadedWeapons.add(weapon.getWeaponType());
            }
        }
        if (unloadedWeapons.isEmpty() && !this.finalFrenzy) {
            endTurn();
            return;
        }
        if (!unloadedWeapons.isEmpty() && this.finalFrenzy) {
            // gestione ricarica durante azione frenesia finale
            return;
        }
        this.controller.send(new RequireWeaponLoadMessage(this.activePlayer.getCharacter(), unloadedWeapons));
    }

    void rechargeWeapon(Weapon weapon) {
        if (weapon == null) {
            endTurn();
            return;
        }
        Map<AmmoType, Integer> requiredAmmo = new EnumMap<>(AmmoType.class);
        requiredAmmo.putAll(weapon.getBuyCost());
        int toSum = requiredAmmo.get(weapon.getColor()) + 1;
        requiredAmmo.put(weapon.getColor(), toSum);
        this.board.useAmmos(this.activePlayer, requiredAmmo);
        for (WeaponCard w : this.activePlayer.getWeapons()) {
            if (w.getWeaponType() == weapon) {
                this.board.loadWeapon(this.activePlayer, w);
                break;
            }
        }
        handleRecharge();
    }

    void endTurn() {
        this.board.endTurn(this.activePlayer);
    }
}
