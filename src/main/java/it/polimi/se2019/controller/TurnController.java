package it.polimi.se2019.controller;

import it.polimi.se2019.model.*;
import it.polimi.se2019.model.messages.payment.PaymentMessage;
import it.polimi.se2019.model.messages.payment.PaymentMessageType;
import it.polimi.se2019.model.messages.payment.PaymentType;
import it.polimi.se2019.model.messages.selections.SelectionMessageType;
import it.polimi.se2019.model.messages.selections.SelectionListMessage;

import java.util.*;
import java.util.List;

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
    private boolean beforeFirstPlayer;
    private WeaponCard weaponToGet;
    private WeaponCard switchWeapon;
    private EffectsController effectsController;

    public TurnController(Board board, GameController controller, EffectsController effectsController) {
        this.state = TurnState.SELECTACTION;
        this.board = board;
        this.controller = controller;
        this.movesLeft = 2;
        this.effectsController = effectsController;
    }

    Player getActivePlayer() {
        return this.activePlayer;
    }

    void startTurn(TurnType type, GameCharacter player) {
        this.activePlayer = this.board.getPlayerByCharacter(player);
        switch (type) {
            case FIRST_TURN:
                this.movesLeft = 2;
                this.state = FIRST_RESPAWNING;
                if (this.activePlayer.getPowerups().size() != 2) {
                    this.board.drawPowerup(this.activePlayer);
                    this.board.drawPowerup(this.activePlayer);
                }
                this.controller.send(new SelectionListMessage<>(SelectionMessageType.DISCARD_POWERUP, player,
                        new ArrayList<>(this.activePlayer.getPowerups())));
                break;
            case AFTER_DEATH:
                this.movesLeft = 0;
                this.state = DEATH_RESPAWNING;
                countScore();
                this.board.drawPowerup(this.activePlayer);
                this.controller.send(new SelectionListMessage<>(SelectionMessageType.DISCARD_POWERUP, player,
                        new ArrayList<>(this.activePlayer.getPowerups())));
                break;
            case NORMAL:
                this.movesLeft = 2;
                sendActions();
                break;
            case FINAL_FRENZY_FIRST:
                this.movesLeft = 2;
                this.finalFrenzy = true;
                this.beforeFirstPlayer = true;
                sendActions();
                break;
            case FINAL_FRENZY_AFTER:
                this.movesLeft = 1;
                this.finalFrenzy = true;
                this.beforeFirstPlayer = false;
                sendActions();
                break;
        }
        this.board.startTurnTimer(this.activePlayer);
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
                    this.activePlayer.getKillshotPoints().get(index));
            index++;
        }
    }

    void handlePowerupDiscarded(Powerup powerup) {
        this.board.removePowerup(this.activePlayer, powerup);
        if(this.state == TurnState.FIRST_RESPAWNING || this.state == TurnState.DEATH_RESPAWNING) {
            spawnPlayer(RoomColor.valueOf(powerup.getColor().toString()));
        }
    }

    void spawnPlayer(RoomColor color) {
        for(Room room : this.board.getArena().getRoomList()){
            if(room.getColor() == color){
                this.board.respawnPlayer(this.activePlayer, room);
                if(this.state == TurnState.FIRST_RESPAWNING) {
                    sendActions();
                }
                if (this.state == TurnState.DEATH_RESPAWNING){
                    this.board.endTurn(this.activePlayer);
                }
            }
        }
    }

    void calculateMovementAction() {
        int maxDistance = 3;
        if (this.finalFrenzy) {
            maxDistance = 4;
        }
        Square position = this.activePlayer.getPosition();
        List<Coordinates> movements = new ArrayList<>();
        for (Square s : this.board.getArena().getAllSquares()) {
            if(s == position) {
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

        this.controller.send(new SelectionListMessage<>(SelectionMessageType.MOVE, this.activePlayer.getCharacter(),
                movements));
    }

    void calculatePickupAction() {
        Square position = this.activePlayer.getPosition();
        int maxDistance = 1;
        if (this.activePlayer.getDamages().size() > 2 || (this.finalFrenzy && this.beforeFirstPlayer)) {
            maxDistance = 2;
        } else if (this.finalFrenzy) {
            maxDistance = 3;
        }
        List<Coordinates> movements = new ArrayList<>();
        for (Square s : this.board.getArena().getAllSquares()) {
            if(s.getWeaponsStore() != null && s.getWeaponsStore().size() == 0) {
                continue;
            }
            if(s.getWeaponsStore() != null) {
                List<Weapon> availableWeapons = new ArrayList<>();
                for (WeaponCard weapon : s.getWeaponsStore()) {
                    Map<AmmoType, Integer> availablaPayments = new EnumMap<>(AmmoType.class);
                    for (Map.Entry<AmmoType, Integer> ammo : this.activePlayer.getAvailableAmmos().entrySet()) {
                        int value = ammo.getValue();
                        for (Powerup p : this.activePlayer.getPowerups()) {
                            if (p.getColor() == ammo.getKey()) {
                                value += 1;
                            }
                        }
                        availablaPayments.put(ammo.getKey(), value);
                    }
                    boolean valid = true;
                    for (Map.Entry<AmmoType, Integer> ammoCost : weapon.getWeaponType().getBuyCost().entrySet()) {
                        if (ammoCost.getValue() > availablaPayments.get(ammoCost.getKey())) {
                            valid = false;
                            break;
                        }
                    }
                    if (valid) {
                        availableWeapons.add(weapon.getWeaponType());
                    }
                }
                if (availableWeapons.isEmpty()) {
                    continue;
                }
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

        this.controller.send(new SelectionListMessage<>(SelectionMessageType.PICKUP,
                this.activePlayer.getCharacter(), movements));
    }

    void handleAction(ActionType action) {
        this.switchWeapon = null;
        this.weaponToGet = null;
        switch (action) {
            case CANCEL:
                cancelAction();
                break;
            case ENDTURN:
                handleReload();
                break;
            case MOVE:
                this.movesLeft--;
                calculateMovementAction();
                break;
            case PICKUP:
                this.movesLeft--;
                calculatePickupAction();
                break;
            case POWERUP:
                calculatePowerupAction();
                break;
            case RELOAD:
                handleReload();
                break;
            case SHOT:
                this.movesLeft--;
                calculateShootAction();
                break;
        }
    }

    void cancelAction() {
        if ((!this.finalFrenzy && this.movesLeft < 2) ||
                (this.finalFrenzy && !this.beforeFirstPlayer && this.movesLeft < 1) ||
                (this.finalFrenzy && this.beforeFirstPlayer && this.movesLeft < 2)) {
            this.movesLeft++;
        }
        sendActions();
    }

    void cancelPowerup() {
        sendActions();
    }

    void calculatePowerupAction() {
        List<Powerup> availablePowerups = new ArrayList<>();
        for (Powerup p : this.activePlayer.getPowerups()) {
            if (p.getType() == PowerupType.TELEPORTER || p.getType() == PowerupType.NEWTON) {
                if (p.getType() == PowerupType.NEWTON) {
                    List<GameCharacter> targets = new ArrayList<>();
                    for (Player player : this.board.getPlayers()) {
                        if (player.getPosition() != null && player.isConnected()  && player != this.activePlayer) {
                            targets.add(player.getCharacter());
                        }
                    }
                    if (!targets.isEmpty()) {
                        availablePowerups.add(p);
                    }
                } else {
                    availablePowerups.add(p);
                }
            }
        }
        this.controller.send(new SelectionListMessage<>(SelectionMessageType.USE_POWERUP,
                this.activePlayer.getCharacter(), availablePowerups));
    }

    private void sendActions() {
        List<ActionType> availableActions = new ArrayList<>();
        if (this.movesLeft == 0) {
            if (canReload()) {
                availableActions.add(ActionType.RELOAD);
            }
        } else {
            if (this.finalFrenzy && this.beforeFirstPlayer) {
                availableActions = new ArrayList<>(Arrays.asList(ActionType.MOVE, ActionType.PICKUP));
            } else if (this.finalFrenzy && !this.beforeFirstPlayer) {
                availableActions = new ArrayList<>(Arrays.asList(ActionType.PICKUP));
            } else {
                availableActions = new ArrayList<>(Arrays.asList(ActionType.MOVE, ActionType.PICKUP));
            }
            this.effectsController.setActivePlayer(this.activePlayer);
            if (!this.effectsController.getAvailableWeapons().isEmpty() && !this.finalFrenzy) {
                availableActions.add(ActionType.SHOT);
            }
        }
        for (Powerup p : this.activePlayer.getPowerups()) {
            if (p.getType() == PowerupType.TELEPORTER || (p.getType() == PowerupType.NEWTON && canUseNewton())) {
                availableActions.add(ActionType.POWERUP);
                break;
            }
        }
        if(availableActions.isEmpty()) {
            endTurn();
            return;
        }
        this.controller.send(new SelectionListMessage<>(SelectionMessageType.ACTION, this.activePlayer.getCharacter(),
                availableActions));
        this.state = SELECTACTION;
    }

    void handleEndAction() {
        sendActions();
        this.state = TurnState.SELECTACTION;
    }

    void handleEndPowerup() {
        sendActions();
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
                Map<AmmoType, Integer> availablaPayments = new EnumMap<>(AmmoType.class);
                for (Map.Entry<AmmoType, Integer> ammo : this.activePlayer.getAvailableAmmos().entrySet()) {
                    int value = ammo.getValue();
                    for (Powerup p : this.activePlayer.getPowerups()) {
                        if (p.getColor() == ammo.getKey()) {
                            value += 1;
                        }
                    }
                    availablaPayments.put(ammo.getKey(), value);
                }
                boolean valid = true;
                for (Map.Entry<AmmoType, Integer> ammoCost : weapon.getWeaponType().getBuyCost().entrySet()) {
                    if (ammoCost.getValue() > availablaPayments.get(ammoCost.getKey())) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    availableWeapons.add(weapon.getWeaponType());
                }
            }
            this.controller.send(new SelectionListMessage<>(SelectionMessageType.PICKUP_WEAPON,
                    this.activePlayer.getCharacter(), availableWeapons));
            return;
        }
        this.board.giveAmmoTile(this.activePlayer, targetSquare.getAvailableAmmoTile());
        handleEndAction();
    }

    void pickupWeapon(Weapon weapon) {
        for (WeaponCard weaponCard : this.activePlayer.getPosition().getWeaponsStore()) {
            if (weaponCard.getWeaponType() == weapon) {
                this.weaponToGet = weaponCard;
                break;
            }
        }
        if (this.activePlayer.getWeapons().size() == 3) {
            List<Weapon> toSend = new ArrayList<>();
            for (WeaponCard w : this.activePlayer.getWeapons()) {
                toSend.add(w.getWeaponType());
            }
            this.controller.send(new SelectionListMessage<>(SelectionMessageType.SWITCH,
                    this.activePlayer.getCharacter(), toSend));
            return;
        }
        boolean free = true;
        for (Map.Entry<AmmoType, Integer> ammo : weapon.getBuyCost().entrySet()) {
            if (ammo.getValue() != 0) {
                free = false;
                break;
            }
        }
        if (free) {
            this.board.giveWeapon(this.activePlayer, this.weaponToGet);
            handleEndAction();
        } else {
            this.controller.send(new PaymentMessage(PaymentMessageType.REQUEST, PaymentType.WEAPON,
                    this.activePlayer.getCharacter(), weapon.getBuyCost()));
        }
    }

    void paidWeapon() {
        if (this.switchWeapon == null) {
            this.board.giveWeapon(this.activePlayer, this.weaponToGet);
        } else {
            this.board.switchWeapon(this.activePlayer, this.switchWeapon,  this.weaponToGet);
        }
        handleEndAction();
    }

    void switchWeapon(Weapon weapon) {
        for (WeaponCard weaponCard : this.activePlayer.getWeapons()) {
            if (weaponCard.getWeaponType() == weapon) {
                this.switchWeapon = weaponCard;
                break;
            }
        }
        boolean free = true;
        for (Map.Entry<AmmoType, Integer> ammo : this.weaponToGet.getWeaponType().getBuyCost().entrySet()) {
            if (ammo.getValue() != 0) {
                free = false;
                break;
            }
        }
        if (free) {
            this.board.switchWeapon(this.activePlayer, this.switchWeapon, this.weaponToGet);
            handleEndAction();
        } else {
            this.controller.send(new PaymentMessage(PaymentMessageType.REQUEST, PaymentType.WEAPON,
                    this.activePlayer.getCharacter(), this.weaponToGet.getWeaponType().getBuyCost()));
        }
    }

    List<Weapon> getRechargeableWeapons() {
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
        return unloadedWeapons;
    }

    boolean canReload() {
        return !getRechargeableWeapons().isEmpty();
    }

    void handleReload() {
        if (!canReload() && !this.finalFrenzy) {
            endTurn();
            return;
        }
        if (canReload() && this.finalFrenzy) {
            // gestione ricarica durante azione frenesia finale TODO
            return;
        }
        this.controller.send(new SelectionListMessage<>(SelectionMessageType.RELOAD,
                this.activePlayer.getCharacter(), getRechargeableWeapons()));
    }

    void reloadWeapon(Weapon weapon) {
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
        if (this.movesLeft != 0) {
            // muoviti e spara (frenesia finale) TODO
        } else {
            handleReload();
        }
    }

    void useWeapon(Weapon weapon) {
        this.effectsController.setWeapon(weapon);
        this.controller.send(new SelectionListMessage<>(SelectionMessageType.EFFECT,
                this.activePlayer.getCharacter(),
                new ArrayList<>(this.effectsController.getAvailableEffects().keySet())));
    }

    private void calculateShootAction() {
        this.effectsController.setActivePlayer(this.activePlayer);
        this.controller.send(new SelectionListMessage<>(SelectionMessageType.USE_WEAPON,
                this.activePlayer.getCharacter(), this.effectsController.getAvailableWeapons()));
    }

    void endTurn() {
        this.board.endTurn(this.activePlayer);
    }

    public boolean canUseNewton() {
        List<Player> targets = new ArrayList<>();
        for (Player target : this.board.getPlayers()) {
            if (!target.isConnected() || target.getPosition() == null || target == this.activePlayer) {
                continue;
            }
            int x = target.getPosition().getX();
            int y = target.getPosition().getY();
            Square current = this.board.getArena().getSquareByCoordinate(x + 1, y);
            if (current != null && current.getNearbyAccessibility().get(CardinalPoint.WEST)) {
                return true;
            }
            current = this.board.getArena().getSquareByCoordinate(x - 1, y);
            if (current != null && current.getNearbyAccessibility().get(CardinalPoint.EAST)) {
                targets.add(target);
                return true;
            }
            current = this.board.getArena().getSquareByCoordinate(x, y + 1);
            if (current != null && current.getNearbyAccessibility().get(CardinalPoint.SOUTH)) {
                targets.add(target);
                return true;
            }
            current = this.board.getArena().getSquareByCoordinate(x, y - 1);
            if (current != null && current.getNearbyAccessibility().get(CardinalPoint.NORTH)) {
                targets.add(target);
                return true;
            }
        }
        return false;
    }
}
