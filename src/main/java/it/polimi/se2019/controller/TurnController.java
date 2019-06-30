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

/**
 * Class for handling turn controller
 */
class TurnController {

    private GameController controller;
    private Board board;
    private Player activePlayer;
    private int movesLeft;
    private TurnState state;
    private boolean finalFrenzy;
    private boolean beforeFirstPlayer;
    private WeaponCard weaponToGet;
    private WeaponCard switchWeapon;
    private EffectsController effectsController;
    private boolean moveShoot;
    private List<WeaponCard> moveReloadedWeapons;
    private Square originalPosition;

    /**
     * Class constructor, it builds a turn controller
     * @param board of which the turn controller has to be used
     * @param controller the game controller in which the turn controller has to be used
     * @param effectsController the effect controller
     */
    TurnController(Board board, GameController controller, EffectsController effectsController) {
        setState(TurnState.SELECTACTION);
        this.board = board;
        this.controller = controller;
        this.movesLeft = 2;
        this.effectsController = effectsController;
        this.moveReloadedWeapons = new ArrayList<>();
    }

    void setState(TurnState state) {
        this.state = state;
    }

    void setWeaponToGet(WeaponCard weapon) {
        this.weaponToGet = weapon;
    }

    WeaponCard getWeaponToGet() {
        return this.weaponToGet;
    }

    boolean getMoveShoot() {
        return this.moveShoot;
    }

    TurnState getState() {
        return this.state;
    }

    void setFinalFrenzy(boolean finalFrenzy) {
        this.finalFrenzy = finalFrenzy;
    }

    void setMovesLeft(int moves) {
        this.movesLeft = moves;
    }

    void setMoveShoot(boolean moveShoot) {
        this.moveShoot = moveShoot;
    }

    int getMovesLeft() {
        return this.movesLeft;
    }

    /**
     * Gets the current active player
     * @return the active player
     */
    Player getActivePlayer() {
        return this.activePlayer;
    }

    void setActivePlayer(Player player) {
        this.activePlayer = player;
    }

    void activeFinalFrenzy() {
        this.finalFrenzy = true;
    }

    /**
     * Starts a player turn
     * @param type of the turn
     * @param player you want him to start turn
     */
    void startTurn(TurnType type, GameCharacter player) {
        setActivePlayer(this.board.getPlayerByCharacter(player));
        switch (type) {
            case FIRST_TURN:
                this.movesLeft = 2;
                setState(FIRST_RESPAWNING);
                if (this.activePlayer.getPowerups().size() != 2) {
                    this.board.drawPowerup(this.activePlayer);
                    this.board.drawPowerup(this.activePlayer);
                }
                this.controller.send(new SelectionListMessage<>(SelectionMessageType.DISCARD_POWERUP, player,
                        new ArrayList<>(this.activePlayer.getPowerups())));
                break;
            case AFTER_DEATH:
                this.movesLeft = 0;
                setState(DEATH_RESPAWNING);
                if (this.activePlayer.getPowerups().size() < 3) {
                    this.board.drawPowerup(this.activePlayer);
                }
                this.controller.send(new SelectionListMessage<>(SelectionMessageType.DISCARD_POWERUP, player,
                        new ArrayList<>(this.activePlayer.getPowerups())));
                break;
            case NORMAL:
                this.movesLeft = 2;
                sendActions();
                break;
            case FINAL_FRENZY_FIRST:
                this.movesLeft = 2;
                activeFinalFrenzy();
                this.beforeFirstPlayer = true;
                sendActions();
                break;
            case FINAL_FRENZY_AFTER:
                this.movesLeft = 1;
                activeFinalFrenzy();
                this.beforeFirstPlayer = false;
                sendActions();
                break;
        }
        this.board.startTurnTimer(this.activePlayer.getCharacter());
    }

    /**
     * Handles a discarded powerup by the active player
     * @param powerup discarded
     */
    void handlePowerupDiscarded(Powerup powerup) {
        this.board.removePowerup(this.activePlayer, powerup);
        if(this.state == TurnState.FIRST_RESPAWNING || this.state == TurnState.DEATH_RESPAWNING) {
            spawnPlayer(RoomColor.valueOf(powerup.getColor().toString()));
        }
    }

    /**
     * Spawns the active player
     * @param color where you need to spawn the player
     */
    private void spawnPlayer(RoomColor color) {
        for(Room room : this.board.getArena().getRoomList()){
            if(room.getColor() == color){
                this.board.respawnPlayer(this.activePlayer, room);
                if(this.state == TurnState.FIRST_RESPAWNING) {
                    sendActions();
                } else {
                    this.board.endTurn(this.activePlayer.getCharacter());
                }
            }
        }
    }

    /**
     * Handles available movements for the active player
     */
    List<Coordinates> calculateMovementAction() {
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
        return movements;
    }

    private void sendMovementAction() {
        this.controller.send(new SelectionListMessage<>(SelectionMessageType.MOVE, this.activePlayer.getCharacter(),
                calculateMovementAction()));
    }

    /**
     * Handles available movements for "move and pick up" for the active player
     */
    List<Coordinates> calculatePickupAction() {
        Square position = this.activePlayer.getPosition();
        int maxDistance = 1;
        if ((this.activePlayer.getDamages().size() > 2 && !this.finalFrenzy) || (this.finalFrenzy && this.beforeFirstPlayer)) {
            maxDistance = 2;
        } else if (this.finalFrenzy) {
            maxDistance = 3;
        }
        List<Coordinates> movements = new ArrayList<>();
        for (Square s : this.board.getArena().getAllSquares()) {
            if(s.getWeaponsStore() != null && s.getWeaponsStore().isEmpty()) {
                continue;
            }
            if(s.getWeaponsStore() != null) {
                List<Weapon> availableWeapons = new ArrayList<>();
                for (WeaponCard weapon : s.getWeaponsStore()) {
                    Map<AmmoType, Integer> availablePayments = new EnumMap<>(AmmoType.class);
                    for (Map.Entry<AmmoType, Integer> ammo : this.activePlayer.getAvailableAmmos().entrySet()) {
                        int value = ammo.getValue();
                        for (Powerup p : this.activePlayer.getPowerups()) {
                            if (p.getColor() == ammo.getKey()) {
                                value += 1;
                            }
                        }
                        availablePayments.put(ammo.getKey(), value);
                    }
                    boolean valid = true;
                    for (Map.Entry<AmmoType, Integer> ammoCost : weapon.getWeaponType().getBuyCost().entrySet()) {
                        if (ammoCost.getValue() > availablePayments.get(ammoCost.getKey())) {
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
        return movements;
    }

    private void sendPickupAction() {
        this.controller.send(new SelectionListMessage<>(SelectionMessageType.PICKUP,
                this.activePlayer.getCharacter(), calculatePickupAction()));
    }

    /**
     * Handles player action selection
     * @param action type of the action chosen
     */
    void handleAction(ActionType action) {
        this.switchWeapon = null;
        setWeaponToGet(null);
        switch (action) {
            case CANCEL:
                cancelAction();
                break;
            case ENDTURN:
                handleReload();
                break;
            case MOVE:
                this.movesLeft--;
                sendMovementAction();
                break;
            case PICKUP:
                this.movesLeft--;
                sendPickupAction();
                break;
            case POWERUP:
                sendPowerupAction();
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

    /**
     * Aborts player selected action
     */
    void cancelAction() {
        if ((!this.finalFrenzy && this.movesLeft < 2) ||
                (this.finalFrenzy && !this.beforeFirstPlayer && this.movesLeft < 1) ||
                (this.finalFrenzy && this.beforeFirstPlayer && this.movesLeft < 2)) {
            this.movesLeft++;
            if(!this.moveReloadedWeapons.isEmpty()) {
                for(WeaponCard weapon : this.moveReloadedWeapons) {
                    weapon.setReady(false);
                }
                this.moveReloadedWeapons = new ArrayList<>();
                this.controller.payBack();
            }
            if (this.moveShoot) {
                this.activePlayer.setPosition(this.originalPosition);
            }
        }
        sendActions();
    }

    /**
     * Gets the available squares coordinates for move-reload-shot action
     * @return the coordinates of the available squares
     */
    List<Coordinates> getMoveReloadShootMovements() {
        this.board.pauseTurnTimer();
        List<String> distance;
        boolean reload = false;
        if ((this.finalFrenzy && this.beforeFirstPlayer) ||
                (!this.finalFrenzy && this.activePlayer.getDamages().size() > 5)) {
            distance = Arrays.asList("0", "1");
        } else {
            distance = Arrays.asList("0", "2");
        }
        if (this.finalFrenzy) {
            reload = true;
        }
        List<Coordinates> movements = new ArrayList<>();
        Map<Weapon, Boolean> originalLoadedWeapons = new EnumMap<>(Weapon.class);
        if (reload) {
            for (WeaponCard weapon : this.activePlayer.getWeapons()) {
                originalLoadedWeapons.put(weapon.getWeaponType(), weapon.isReady());

                if (getRechargeableWeapons().contains(weapon.getWeaponType())) {
                    weapon.setReady(true);
                }
            }
        }
        Square originalPosition = this.activePlayer.getPosition();
        this.effectsController.setActivePlayer(this.activePlayer);
        for (Square s : this.board.getSquaresByDistance(this.activePlayer, distance)) {
            this.activePlayer.setPosition(s);
            if (!this.effectsController.getAvailableWeapons().isEmpty()) {
                movements.add(new Coordinates(s.getX(), s.getY()));
            }
        }
        if (reload) {
            for (Map.Entry<Weapon, Boolean> w : originalLoadedWeapons.entrySet()) {
                this.activePlayer.getWeaponCardByWeapon(w.getKey()).setReady(w.getValue());
            }
        }
        this.activePlayer.setPosition(originalPosition);
        this.board.resumeTurnTimer();
        return movements;
    }

    /**
     *
     */
    private void calculateShootAction() {
        if (!this.finalFrenzy && this.activePlayer.getDamages().size() < 6) {
            this.effectsController.setActivePlayer(this.activePlayer);
            this.controller.send(new SelectionListMessage<>(SelectionMessageType.USE_WEAPON,
                    this.activePlayer.getCharacter(), this.effectsController.getAvailableWeapons()));
            return;
        }
        this.moveShoot = true;
        this.controller.send(new SelectionListMessage<>(SelectionMessageType.MOVE,
                this.activePlayer.getCharacter(), getMoveReloadShootMovements()));
    }

    List<Powerup> calculatePowerupAction() {
        List<Powerup> availablePowerups = new ArrayList<>();
        for (Powerup p : this.activePlayer.getPowerups()) {
            if (p.getType() == PowerupType.TELEPORTER || p.getType() == PowerupType.NEWTON) {
                if (p.getType() == PowerupType.NEWTON) {
                    List<GameCharacter> targets = new ArrayList<>();
                    for (Player player : this.board.getAvailablePlayers()) {
                        if (player != this.activePlayer) {
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
        return availablePowerups;
    }

    private void sendPowerupAction() {
        this.controller.send(new SelectionListMessage<>(SelectionMessageType.USE_POWERUP,
                this.activePlayer.getCharacter(), calculatePowerupAction()));
    }

    /**
     * Calculates available actions
     * @return list of the available actions
     */
    List<ActionType> calculateActions() {
        this.moveShoot = false;
        List<ActionType> availableActions = new ArrayList<>();
        if (this.movesLeft == 0 || this.board.getSkulls() == 0) {
            if (canReload()) {
                availableActions.add(ActionType.RELOAD);
            }
        } else {
            if (this.finalFrenzy && this.beforeFirstPlayer) {
                availableActions = new ArrayList<>(Arrays.asList(ActionType.MOVE, ActionType.PICKUP));
            } else if (this.finalFrenzy) {
                availableActions = new ArrayList<>(Arrays.asList(ActionType.PICKUP));
            } else {
                availableActions = new ArrayList<>(Arrays.asList(ActionType.MOVE, ActionType.PICKUP));
            }

            if (!this.finalFrenzy && this.activePlayer.getDamages().size() < 6) {
                this.effectsController.setActivePlayer(this.activePlayer);
                if (!this.effectsController.getAvailableWeapons().isEmpty()) {
                    availableActions.add(ActionType.SHOT);
                }
            } else if (!getMoveReloadShootMovements().isEmpty()) {
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
            return new ArrayList<>();
        }
        return availableActions;
    }

    private void sendActions() {
        List<ActionType> avialableActions = calculateActions();
        if(!avialableActions.isEmpty()) {
            setState(SELECTACTION);
            this.controller.send(new SelectionListMessage<>(SelectionMessageType.ACTION, this.activePlayer.getCharacter(),
                    calculateActions()));
        }
    }

    /**
     * Handles end action
     */
    void handleEndAction() {
        this.moveReloadedWeapons = new ArrayList<>();
        this.controller.resetPayment();
        this.moveShoot = false;
        sendActions();
        setState(TurnState.SELECTACTION);
    }

    /**
     * Handles end powerup
     */
    void handleEndPowerup() {
        sendActions();
    }

    /**
     * Handles movement action
     * @param coordinates chosen where the player wants to move
     */
    void movementAction(Coordinates coordinates) {
        handleMovementAction(coordinates);
        if (!this.moveShoot) {
            handleEndAction();
        } else {
            handleReload();
        }
    }

    void handleMovementAction(Coordinates coordinates) {
        this.originalPosition = this.activePlayer.getPosition();
        this.board.movePlayer(this.activePlayer, this.board.getArena().getSquareByCoordinate(coordinates.getX(),
                coordinates.getY()));
    }

    /**
     * Handles pickup action
     * @param coordinates chosen where the player wants to pickup
     */
    void pickupAction(Coordinates coordinates) {
        List<Weapon> weapons = handlePickupAction(coordinates);
        if(weapons != null) {
            this.controller.send(new SelectionListMessage<>(SelectionMessageType.PICKUP_WEAPON,
                    this.activePlayer.getCharacter(), weapons));
            return;
        }
        handleEndAction();
    }

    List<Weapon> handlePickupAction(Coordinates coordinates) {
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
            return availableWeapons;
        }
        this.board.giveAmmoTile(this.activePlayer, targetSquare.getAvailableAmmoTile());
        return null;
    }

    /**
     * Handles pickup weapon action
     * @param weapon chosen to pickup by the player
     */
    void pickupWeapon(Weapon weapon) {
        for (WeaponCard weaponCard : this.activePlayer.getPosition().getWeaponsStore()) {
            if (weaponCard.getWeaponType() == weapon) {
                setWeaponToGet(weaponCard);
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

    /**
     * Handles weapon payment
     */
    void paidWeapon() {
        handlePaidWeapon();
        handleEndAction();
    }

    void handlePaidWeapon() {
        if (this.switchWeapon == null) {
            this.board.giveWeapon(this.activePlayer, this.weaponToGet);
        } else {
            this.board.switchWeapon(this.activePlayer, this.switchWeapon,  this.weaponToGet);
        }
    }

    /**
     * Handles weapons switching when a player got max weapons and wants to change with another one
     * @param weapon to be changed
     */
    void switchWeapon(Weapon weapon) {
        if (handleSwitchWeapon(weapon)) {
            this.board.switchWeapon(this.activePlayer, this.switchWeapon, this.weaponToGet);
            handleEndAction();
        } else {
            this.controller.send(new PaymentMessage(PaymentMessageType.REQUEST, PaymentType.WEAPON,
                    this.activePlayer.getCharacter(), this.weaponToGet.getWeaponType().getBuyCost()));
        }
    }

    boolean handleSwitchWeapon(Weapon weapon) {
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
        return free;
    }

    /**
     * Gets the unloaded weapons
     * @return List of the unloaded weapons
     */
    private List<Weapon> getRechargeableWeapons() {
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
                Integer powerupAmmo = 0;
                for (Powerup powerup : this.activePlayer.getPowerups()) {
                    if (powerup.getColor() == ammo.getKey()) {
                        powerupAmmo++;
                    }
                }
                if (ammo.getValue() > this.activePlayer.getAvailableAmmos().get(ammo.getKey()) + powerupAmmo) {
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

    /**
     * Knows if there are any unloaded weapons
     * @return true if there are, else false
     */
    boolean canReload() {
        return !getRechargeableWeapons().isEmpty();
    }

    /**
     * Handles weapons reloading
     */
    private void handleReload() {
        List<Weapon> toReload = getRechargeableWeapons();
        if (toReload.isEmpty() && !this.moveShoot) {
            endTurn();
            return;
        }
        if (toReload.isEmpty()) {
            this.effectsController.setActivePlayer(this.activePlayer);
            if (this.effectsController.getAvailableWeapons().isEmpty()) {
                handleEndAction();
                return;
            }
            this.controller.send(new SelectionListMessage<>(SelectionMessageType.USE_WEAPON,
                    this.activePlayer.getCharacter(), this.effectsController.getAvailableWeapons()));
            return;
        }
        this.controller.send(new SelectionListMessage<>(SelectionMessageType.RELOAD,
                this.activePlayer.getCharacter(), toReload));
    }

    /**
     * Sends to the player the payment request for reloading the weapon
     * @param weapon which the player wants to reload
     */
    void sendReloadPaymentRequest(Weapon weapon) {
        if (weapon == null && !this.moveShoot) {
            endTurn();
            return;
        }
        if (weapon == null) {
            this.effectsController.setActivePlayer(this.activePlayer);
            if (this.effectsController.getAvailableWeapons().isEmpty()) {
                handleEndAction();
                return;
            }
            this.controller.send(new SelectionListMessage<>(SelectionMessageType.USE_WEAPON,
                    this.activePlayer.getCharacter(), this.effectsController.getAvailableWeapons()));
            return;
        }
        setWeaponToGet(this.activePlayer.getWeaponCardByWeapon(weapon));
        Map<AmmoType, Integer> requiredAmmo = new EnumMap<>(AmmoType.class);
        requiredAmmo.putAll(weapon.getBuyCost());
        int toSum = requiredAmmo.get(weapon.getColor()) + 1;
        requiredAmmo.put(weapon.getColor(), toSum);

        this.controller.send(new PaymentMessage(PaymentMessageType.REQUEST, PaymentType.RELOAD,
                this.activePlayer.getCharacter(), requiredAmmo));
    }

    /**
     * Handles weapon reloading
     */
    void reloadWeapon() {
        this.board.loadWeapon(this.activePlayer, this.weaponToGet);
        if (canReload()) {
            this.controller.send(new SelectionListMessage<>(SelectionMessageType.RELOAD,
                    this.activePlayer.getCharacter(), getRechargeableWeapons()));
            return;
        }
        if (this.moveShoot) {
            this.moveReloadedWeapons.add(this.weaponToGet);
            this.effectsController.setActivePlayer(this.activePlayer);
            List<Weapon> weapons = this.effectsController.getAvailableWeapons();
            if (weapons.isEmpty()) {
                handleEndAction();
                return;
            }
            this.controller.send(new SelectionListMessage<>(SelectionMessageType.USE_WEAPON,
                    this.activePlayer.getCharacter(), weapons));
        } else {
            endTurn();
        }

    }

    /**
     * Handles weapon use
     * @param weapon which the player wants to use
     */
    void useWeapon(Weapon weapon) {
        this.effectsController.setWeapon(weapon);
        this.effectsController.setActivePlayer(this.activePlayer);
        this.controller.send(new SelectionListMessage<>(SelectionMessageType.EFFECT,
                this.activePlayer.getCharacter(),
                new ArrayList<>(this.effectsController.getAvailableEffects().keySet())));
    }

    /**
     * Ends turn
     */
    void endTurn() {
        this.board.endTurn(this.activePlayer.getCharacter());
    }

    /**
     * Knows if the player can use Newton powerup
     * @return true if he can, else false
     */
    boolean canUseNewton() {
        for (Player target : this.board.getAvailablePlayers()) {
            if (target == this.activePlayer) {
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
                return true;
            }
            current = this.board.getArena().getSquareByCoordinate(x, y + 1);
            if (current != null && current.getNearbyAccessibility().get(CardinalPoint.SOUTH)) {
                return true;
            }
            current = this.board.getArena().getSquareByCoordinate(x, y - 1);
            if (current != null && current.getNearbyAccessibility().get(CardinalPoint.NORTH)) {
                return true;
            }
        }
        return false;
    }
}
