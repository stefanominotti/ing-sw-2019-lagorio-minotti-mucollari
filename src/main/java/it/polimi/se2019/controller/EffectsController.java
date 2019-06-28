package it.polimi.se2019.controller;

import it.polimi.se2019.model.*;
import it.polimi.se2019.model.messages.payment.PaymentMessage;
import it.polimi.se2019.model.messages.payment.PaymentMessageType;
import it.polimi.se2019.model.messages.payment.PaymentType;
import it.polimi.se2019.model.messages.selections.SelectionMessageType;
import it.polimi.se2019.model.messages.selections.SelectionListMessage;
import it.polimi.se2019.model.messages.selections.SingleSelectionMessage;
import java.util.*;
import static it.polimi.se2019.model.WeaponEffectOrderType.SECONDARYONE;
import static it.polimi.se2019.model.WeaponEffectOrderType.SECONDARYTWO;

/**
 * Class for handling weapon effects controller
 */
class EffectsController {
    private Board board;
    private GameController controller;
    private WeaponEffect currentEffect;
    private WeaponEffectOrderType effectOrder;

    //da impostare ogni volta che viene scelto un'arma
    private Weapon weapon;

    //da impostare ogni volta che viene eseguito un effetto
    private boolean mainEffectApplied;
    private boolean secondaryEffectOneApplied;
    private boolean secondaryEffectTwoApplied;
    private List<WeaponEffect> effectsQueue;

    //da impostare ogni volta che viene eseguito parte di un effetto
    private List<Player> hitByMain;
    private List<Player> hitBySecondary;
    private Room chosenRoom;
    private Square chosenSquare;
    private CardinalPoint chosenDirection;
    private Player activePlayer;
    private WeaponEffectOrderType activeCombo;

    /**
     * Class constructor, it builds an effect controller
     * @param board in which the effect controller has to be built
     * @param controller of the game in which the effect controller has to be built
     */
    EffectsController(Board board, GameController controller) {
        this.board = board;
        this.controller = controller;
        this.hitByMain = new ArrayList<>();
        this.hitBySecondary = new ArrayList<>();
        this.mainEffectApplied = false;
        this.secondaryEffectOneApplied = false;
        this.secondaryEffectTwoApplied = false;
        this.effectsQueue = new ArrayList<>();
    }

    /**
     * Sets the active player, who is using the weapon
     * @param player you want to make the active one
     */
    void setActivePlayer(Player player) {
        this.activePlayer = player;
        this.hitByMain = new ArrayList<>();
        this.hitBySecondary = new ArrayList<>();
        this.mainEffectApplied = false;
        this.secondaryEffectOneApplied = false;
        this.secondaryEffectTwoApplied = false;
        this.effectsQueue = new ArrayList<>();
    }

    /**
     * Sets the environment choices when is asked to a player to choose a direction or room
     * @param initialSquare starting square
     * @param finalSquare arrival square
     */
    private void setEnvironment(Square initialSquare, Square finalSquare) {
        if (!finalSquare.equals(initialSquare)) {
            this.chosenDirection = this.board.getCardinalFromSquares(initialSquare, finalSquare);
            this.chosenRoom = finalSquare.getRoom();
        }
        this.chosenSquare = finalSquare;
    }

    void setWeapon(Weapon weapon) {
        resetController();
        this.weapon = weapon;
    }

    /**
     * Gets the available weapons, which ones you can use
     * @return List of the available weapons
     */
    List<Weapon> getAvailableWeapons() {
        List<Weapon> availableWeapons = new ArrayList<>();
        for (WeaponCard weaponCard : this.activePlayer.getWeapons()) {
            this.weapon = weaponCard.getWeaponType();
            if (weaponCard.isReady() && !getAvailableEffects().isEmpty()) {
                availableWeapons.add(weaponCard.getWeaponType());
            }
        }
        this.weapon = null;
        return availableWeapons;
    }

    /**
     * Checks if a secondary effect of a weapon can be applied before the primary one
     * @return true if it is, else false
     */
    private boolean checkSecondaryFirst() {
        try {
            WeaponEffectOrderType originalOrder = this.effectOrder;
            this.effectOrder = SECONDARYONE;
            seeEffectPossibility(this.weapon.getSecondaryEffectOne().get(0));
            this.effectOrder = originalOrder;
            return true;
        } catch (UnsupportedOperationException e) {
            return false;
        }
    }

    /**
     * Gets the available effects
     * @return map with the effect macro and its list
     */
    Map<WeaponEffectOrderType, List<WeaponEffect>> getAvailableEffects() {
        Map<WeaponEffectOrderType, List<WeaponEffect>> availableWeapons = new LinkedHashMap<>();
        if (!this.mainEffectApplied) {
            availableWeapons.put(WeaponEffectOrderType.PRIMARY, weapon.getPrimaryEffect());
            if (!this.weapon.getAlternativeMode().isEmpty() && checkCost(weapon.getAlternativeMode())) {
                availableWeapons.put(WeaponEffectOrderType.ALTERNATIVE, weapon.getAlternativeMode());
            }
        }
        if (!this.secondaryEffectOneApplied && !this.weapon.getSecondaryEffectOne().isEmpty()
                && checkCost(weapon.getSecondaryEffectOne()) && !this.weapon.getSecondaryEffectOne().get(0).isCombo()
                && (this.mainEffectApplied ||
                this.weapon.getPrimaryEffect().get(0).getEffectDependency().contains(SECONDARYONE) &&
                checkSecondaryFirst())) {
            availableWeapons.put(SECONDARYONE, this.weapon.getSecondaryEffectOne());
        }
        if (!this.secondaryEffectTwoApplied && !this.weapon.getSecondaryEffectTwo().isEmpty()
                && checkCost(this.weapon.getSecondaryEffectTwo())
                && !this.weapon.getSecondaryEffectTwo().get(0).isCombo() && (
                !(this.weapon.getSecondaryEffectTwo().get(0).getEffectDependency().contains(SECONDARYONE) &&
                        !this.secondaryEffectOneApplied) &&
                        (this.weapon.getPrimaryEffect().get(0).getEffectDependency().contains(SECONDARYTWO) ||
                                this.mainEffectApplied))) {
            availableWeapons.put(SECONDARYTWO, this.weapon.getSecondaryEffectTwo());
        }
        List<WeaponEffectOrderType> toRemove = new ArrayList<>();
        for (Map.Entry<WeaponEffectOrderType, List<WeaponEffect>> effect : availableWeapons.entrySet()) {
            boolean valid = false;
            int index = 0;
            while (!valid) {
                try {
                    seeEffectPossibility(effect.getValue().get(index));
                    valid = true;
                } catch (UnsupportedOperationException e) {
                    if (!effect.getValue().get(0).isRequired()) {
                        index = effect.getValue().get(0).getRequiredDependency() + 1;
                    } else {
                        toRemove.add(effect.getKey());
                        valid = true;
                    }
                }
            }
        }
        for (WeaponEffectOrderType w : toRemove) {
            availableWeapons.remove(w);
        }
        return availableWeapons;
    }

    /**
     * Checks if a player has enough ammo to apply an effect macro
     * @param effects list you want to be check
     * @return true if he can, else false
     */
    boolean checkCost(List<WeaponEffect> effects) {
        for (Map.Entry<AmmoType, Integer> cost : effects.get(0).getCost().entrySet()) {
            Integer powerupAmmo = 0;
            for (Powerup powerup : this.activePlayer.getPowerups()) {
                if (powerup.getColor() == cost.getKey()) {
                    powerupAmmo++;
                }
            }
            if (cost.getValue() > this.activePlayer.getAvailableAmmos().get(cost.getKey()) + powerupAmmo) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the macro effect cost
     * @param effectType macro effect of which you want to get the cost
     * @return Map with ammo and its quantity to be paid for using the effect
     */
    Map<AmmoType, Integer> getEffectCost(WeaponEffectOrderType effectType) {
        switch (effectType) {
            case SECONDARYONE:
                return this.weapon.getSecondaryEffectOne().get(0).getCost();
            case SECONDARYTWO:
                return this.weapon.getSecondaryEffectTwo().get(0).getCost();
            case ALTERNATIVE:
                return this.weapon.getAlternativeMode().get(0).getCost();
            default:
                break;

        }
        return null;
    }

    void selectEffect(WeaponEffectOrderType effectType) {
        effectSelected(effectType);
        handleEffectsQueue();
    }

    /**
     * Handles player effect selection
     * @param effectType choosen by the player
     */
    void effectSelected(WeaponEffectOrderType effectType) {
        this.board.unloadWeapon(this.activePlayer, this.activePlayer.getWeaponCardByWeapon(this.weapon));
        switch (effectType) {
            case PRIMARY:
                this.effectsQueue.addAll(0, this.weapon.getPrimaryEffect());
                this.effectOrder = WeaponEffectOrderType.PRIMARY;
                break;
            case ALTERNATIVE:
                this.effectsQueue.addAll(0, this.weapon.getAlternativeMode());
                this.effectOrder = WeaponEffectOrderType.ALTERNATIVE;
                break;
            case SECONDARYONE:
                this.effectsQueue.addAll(0, this.weapon.getSecondaryEffectOne());
                this.effectOrder = SECONDARYONE;
                if (!this.mainEffectApplied) {
                    this.effectsQueue.addAll(this.weapon.getPrimaryEffect());
                }
                break;
            case SECONDARYTWO:
                this.effectsQueue.addAll(0, weapon.getSecondaryEffectTwo());
                this.effectOrder = SECONDARYTWO;
                break;
        }
    }

    /**
     * Handles the position constraint case distance from a player, getting players that satisfy that constraint
     * @param constraint to be satisfied
     * @return List of the player that satisies the constraint
     */
    private List<Player> distanceByPlayerCase(PositionConstraint constraint) {
        List<Player> players = new ArrayList<>();
        if (constraint.getTarget() == TargetType.SQUARE) {
            players = this.board.getPlayersByDistance(
                    this.chosenSquare, constraint.getDistanceValues());
        } else {
            Player target = getTargetPlayer(constraint.getTarget());
            if (target != null) {
                players = this.board.getPlayersByDistance(target, constraint.getDistanceValues());
            }
        }
        return players;
    }

    /**
     * Handles the position constraint case distance from a square, getting squares that satisfy that constraint
     * @param constraint to be satisfied
     * @return List of the squares that satisfy the constraint
     */
    private List<Square> distanceBySquareCase(PositionConstraint constraint) {
        List<Square> squares = new ArrayList<>();
        switch (constraint.getTarget()) {
            case OTHERS:
                Set<Square> targetSquares = new HashSet<>();
                for (Player target : this.board.getAvailablePlayers()) {
                    if (!target.equals(this.activePlayer)) {
                        targetSquares.addAll(this.board.getSquaresByDistance(target, constraint.getDistanceValues()));
                    }
                }
                squares = new ArrayList<>(targetSquares);
                break;
            case SQUARE:
                squares = this.board.getSquaresByDistance(this.chosenSquare, constraint.getDistanceValues());
                break;
            default:
                Player target = getTargetPlayer(constraint.getTarget());
                if (target != null) {
                    squares = this.board.getSquaresByDistance(target.getPosition(),
                            constraint.getDistanceValues());
                }
        }
        return squares;
    }

    /**
     * Filters the available targets by a list of position constraints
     * @param constraints to be satisfied
     * @return List of the players that satisfy the constraints
     */
    private List<Player> filterByPositionConstraintPlayers(List<PositionConstraint> constraints) {
        List<Player> availablePlayers = this.board.getAvailablePlayers();
        List<Player> targetPlayers = new ArrayList<>();
        for (PositionConstraint constraint : constraints) {
            switch (constraint.getType()) {
                case VISIBLE:
                    targetPlayers = this.board.getVisiblePlayers(getTargetPlayer(constraint.getTarget()));
                    break;
                case DISTANCE:
                    targetPlayers = distanceByPlayerCase(constraint);
                    break;
                case NOTVISIBLE:
                    targetPlayers = this.board.getNotVisiblePlayers(getTargetPlayer(
                            constraint.getTarget()));
                    break;
                case SAMEDIRECTION:
                    targetPlayers = this.board.getPlayersOnCardinalDirection(
                            this.chosenSquare, this.chosenDirection);
                    break;
                default:
                    break;
            }
            availablePlayers = (List<Player>) filter(availablePlayers, targetPlayers);
        }
        return availablePlayers;
    }

    /**
     * Filters the available squares by a list of position constraints
     * @param constraints to be satisfied
     * @return List of the squares that satisfy the constraints
     */
    private List<Square> filterByPositionConstraintSquares(List<PositionConstraint> constraints) {
        List<Square> availableSquares = this.board.getArena().getAllSquares();
        List<Square> targetSquares = new ArrayList<>();
        for (PositionConstraint constraint : constraints) {
            switch (constraint.getType()) {
                case VISIBLE:
                    Player target = getTargetPlayer(constraint.getTarget());
                    if (target != null) {
                        targetSquares = this.board.getVisibleSquares(target);
                    }
                    break;
                case DISTANCE:
                    targetSquares = distanceBySquareCase(constraint);
                    break;
                case SAMEDIRECTION:
                    targetSquares = this.board.getSquaresOnCardinalDirection(
                            this.chosenSquare, this.chosenDirection);
                    break;
                default:
                    break;
            }
            availableSquares = (List<Square>) filter(availableSquares, targetSquares);
        }
        return availableSquares;
    }

    /**
     * Filters the available rooms by a list of position constraints
     * @param constraints to be satisfied
     * @return List of the rooms that satisfy the constraints
     */
    private List<Room> filterByPositionConstraintRooms(List<PositionConstraint> constraints) {
        List<Room> availableRooms = this.board.getArena().getRoomList();
        List<Room> targetRooms = new ArrayList<>();
        for (PositionConstraint constraint : constraints) {
            Player target;
            switch (constraint.getType()) {
                case VISIBLE:
                    target = getTargetPlayer(constraint.getTarget());
                    if (target != null) {
                        targetRooms = this.board.getVisibleRooms(target);
                    }
                    break;

                case NOTCONTAINS:
                    target = getTargetPlayer(constraint.getTarget());
                    if (target != null) {
                        targetRooms.remove(target.getPosition().getRoom());
                    }
                    break;

                case DISTANCE:
                    for (Player player : this.board.getAvailablePlayers()) {
                        if (!player.equals(this.activePlayer) && player.getPosition() != null) {
                            targetRooms.add(player.getPosition().getRoom());
                        }
                    }
                    break;

                default:
                    break;
            }
            availableRooms = (List<Room>) filter(availableRooms, targetRooms);
        }
        return availableRooms;
    }

    /**
     * Filters the available cardinal points by a list of position constraints
     * @param constraints to be satisfied
     * @return List of the cardinal points that satisfy the constraints
     */
    private List<CardinalPoint> filterByPositionConstraintCardinals(List<PositionConstraint> constraints) {
        List<CardinalPoint> availableCardinal = new ArrayList<>();
        for (CardinalPoint cardinal : CardinalPoint.values()) {
            if (constraints.get(0).getType() == PositionConstraintType.DISTANCE &&
                    !this.board.getPlayersOnCardinalDirection(this.activePlayer, cardinal).isEmpty()) {
                availableCardinal.add(cardinal);
            }
            if (constraints.get(0).getType() == PositionConstraintType.VISIBLE &&
                    !this.board.getVisibleSquaresOnCardinalDirection(this.activePlayer, cardinal).isEmpty()) {
                availableCardinal.add(cardinal);
            }
        }
        return availableCardinal;
    }

    /**
     * Handles damage and mark effect
     * @param effect the damage or mark effect
     * @return List of players that can be damaged or marked
     */
    private List<Player> damageMarkCase(WeaponEffect effect) {
        EffectTarget target = effect.getTarget();
        TargetPositionType positionType = target.getPositionType();
        List<PositionConstraint> constraints = target.getPositionConstraints();
        List<Player> availablePlayers = this.board.getAvailablePlayers();
        switch (positionType) {
            case EVERYWHERE:
                availablePlayers = filterByPositionConstraintPlayers(constraints);
                break;
            case SAMESQUARE:
                availablePlayers = filterByPositionConstraintPlayers(constraints);
                break;
            case ROOM:
                availablePlayers = this.chosenRoom.getPlayers();
                break;
            default:
                break;
        }
        return availablePlayers;
    }

    /**
     * Handles move other players effect
     * @param effect the movement effect
     * @return List of players that can be moved
     */
    private List<Player> moveOthersPlayersCase(WeaponEffect effect) {
        EffectTarget target = effect.getTarget();
        List<PositionConstraint> constraints = target.getPositionConstraints();
        List<Player> availablePlayers = this.board.getAvailablePlayers();
        if (!constraints.isEmpty()) {
            availablePlayers = filterByPositionConstraintPlayers(constraints);
        }
        availablePlayers.remove(this.activePlayer);
        return availablePlayers;
    }

    /**
     * Handles movement of players
     * @param effect the movement effect
     * @param player you want to move
     * @return List of available squares where you can perform the movement
     */
    private List<Square> moveSquaresCase(WeaponEffect effect, Player player) {
        EffectTarget target = effect.getTarget();
        List<Square> availableSquares;
        if (target.getAfterPositionConstraints().isEmpty()) {
            availableSquares = this.board.getSquaresByDistance(player, effect.getAmount());
        } else {
            availableSquares = filterByPositionConstraintSquares(target.getAfterPositionConstraints());
        }
        return availableSquares;
    }

    /**
     * Calculates the possible application for the effect
     * @param effect of which you want to calculate the possible application
     * @return an effect possibility pack containing the application way
     */
    EffectPossibilityPack seeEffectPossibility(WeaponEffect effect) {
        EffectTarget target = effect.getTarget();
        TargetPositionType positionType = target.getPositionType();
        List<PositionConstraint> constraints = target.getPositionConstraints();
        boolean noHitByMain = false;
        boolean noHitBySecondary = false;
        List<Player> availablePlayers = new ArrayList<>();
        List<Square> availableSquares = new ArrayList<>();
        List<Room> availableRooms = new ArrayList<>();
        List<CardinalPoint> availableCardinal = new ArrayList<>();
        Map<Square, List<Player>> availableMultipleSquares = new LinkedHashMap<>();
        List<String> amountTargets = target.getAmount();
        if (!target.getTargetConstraints().isEmpty()) {
            Set<TargetConstraint> targetConstraints = target.getTargetConstraints();
            if (targetConstraints.contains(TargetConstraint.NOHITBYMAIN)) {
                noHitByMain = true;
            }
            if (targetConstraints.contains(TargetConstraint.NOHITBYSECONDARY)) {
                noHitBySecondary = true;
            }
            if (targetConstraints.contains(TargetConstraint.ONLYHITBYMAIN)) {
                availablePlayers = new ArrayList<>(this.hitByMain);
            }
            if (targetConstraints.contains(TargetConstraint.ONLYHITBYSECONDARY)) {
                availablePlayers = new ArrayList<>(this.hitBySecondary);
            }
        }
        switch (effect.getType()) {
            case MOVE:
                if (target.getType() == TargetType.SELF) {
                    availablePlayers.add(this.activePlayer);
                }
                else if (target.getType() == TargetType.OTHERS  && availablePlayers.isEmpty()) {
                    availablePlayers = moveOthersPlayersCase(effect);
                }
                if (noHitByMain) {
                    availablePlayers.removeAll(hitByMain);
                }
                if (noHitBySecondary) {
                    availablePlayers.removeAll(hitBySecondary);
                }
                if (availablePlayers.isEmpty()) {
                    throw new UnsupportedOperationException();
                }
                availableSquares = moveSquaresCase(effect, availablePlayers.get(0));
                if (this.effectOrder == SECONDARYONE && !this.mainEffectApplied) {
                    Square originalPosition = this.activePlayer.getPosition();
                    List<Square> toRemove = new ArrayList<>();

                    for (Square square : availableSquares) {
                        this.activePlayer.setPosition(square);
                        try {
                            seeEffectPossibility(this.weapon.getPrimaryEffect().get(0));
                        } catch (UnsupportedOperationException e) {
                            toRemove.add(square);
                        }
                    }
                    this.activePlayer.setPosition(originalPosition);
                    availableSquares.removeAll(toRemove);
                }
                if (availableSquares.isEmpty()) {
                    throw new UnsupportedOperationException();
                }
                break;
            case SELECT:
                switch (target.getType()) {
                    case SQUARE:
                        availableSquares = filterByPositionConstraintSquares(constraints);
                        if (availableSquares.isEmpty()) {
                            throw new UnsupportedOperationException();
                        }
                        break;
                    case ROOM:
                        availableRooms = filterByPositionConstraintRooms(constraints);
                        if (availableRooms.isEmpty()) {
                            throw new UnsupportedOperationException();
                        }
                        break;
                    case CARDINALDIRECTION:
                        availableCardinal = filterByPositionConstraintCardinals(constraints);
                        if (availableCardinal.isEmpty()) {
                            throw new UnsupportedOperationException();
                        }
                        break;
                    default:
                        break;
                }
                break;
            default:
                if (positionType == TargetPositionType.MULTIPLESQUARES) {
                    List<Square> targetSquares = filterByPositionConstraintSquares(constraints);
                    for (Square square : targetSquares) {
                        if (!square.getActivePlayers().isEmpty()) {
                            availableMultipleSquares.put(square, square.getActivePlayers());
                        }
                    }
                    if (availableMultipleSquares.isEmpty()) {
                        throw new UnsupportedOperationException();
                    }
                } else {
                    if (availablePlayers.isEmpty()) {
                        availablePlayers = damageMarkCase(effect);
                        availablePlayers.remove(this.activePlayer);
                    }
                    if (noHitByMain) {
                        availablePlayers.removeAll(this.hitByMain);
                    }
                    if (noHitBySecondary) {
                        availablePlayers.removeAll(this.hitBySecondary);
                    }
                    if (availablePlayers.isEmpty()) {
                        throw new UnsupportedOperationException();
                    }
                }
        }
        List<GameCharacter> characters = new ArrayList<>();
        for (Player player : availablePlayers) {
            characters.add(player.getCharacter());
        }
        List<Coordinates> coordinates = new ArrayList<>();
        for (Square square : availableSquares) {
            coordinates.add(new Coordinates(square.getX(), square.getY()));
        }
        List<RoomColor> roomColors = new ArrayList<>();
        for (Room room : availableRooms) {
            roomColors.add(room.getColor());
        }
        Map<Coordinates, List<GameCharacter>> multipleSquares = new HashMap<>();
        for (Map.Entry<Square, List<Player>> multiSquare : availableMultipleSquares.entrySet()) {
            Square square = multiSquare.getKey();
            List<GameCharacter> characterList = new ArrayList<>();
            for (Player player : multiSquare.getValue()) {
                characterList.add(player.getCharacter());
            }
            multipleSquares.put(new Coordinates(square.getX(), square.getY()), characterList);
        }
        return (new EffectPossibilityPack(
                amountTargets, characters, coordinates, roomColors,
                availableCardinal, multipleSquares, effect.isRequired(), effect.getType(), effect.getDescription()));
    }

    /**
     * Handles effect pack application
     * @param pack which you want to apply
     */
    void effectApplication(EffectPossibilityPack pack) {
        Square square = this.activePlayer.getPosition();
        try {
            Coordinates coordinates = pack.getSquares().get(0);
            square = this.board.getArena().getSquareByCoordinate(coordinates.getX(), coordinates.getY());
        } catch (IndexOutOfBoundsException e) {
            // Ignore
        }
        if (pack.isRequire()) {
            switch (this.currentEffect.getType()) {
                case SELECT:
                    this.chosenSquare = square;
                    try {
                        this.chosenRoom = this.board.getArena().getRoomByColor(pack.getRooms().get(0));
                    } catch (IndexOutOfBoundsException e) {
                        // Ignore
                    }
                    try {
                        this.chosenDirection = pack.getCardinalPoints().get(0);
                    } catch (IndexOutOfBoundsException e) {
                        // Ignore
                    }
                    break;
                case MOVE:
                    if (pack.getCharacters().isEmpty()) {
                        setEnvironment(this.activePlayer.getPosition(), square);
                        this.board.movePlayer(this.activePlayer, square);
                    } else {
                        for (GameCharacter character : pack.getCharacters()) {
                            setEnvironment(this.board.getPlayerByCharacter(character).getPosition(), square);
                            this.board.movePlayer(this.board.getPlayerByCharacter(character), square);
                            setHitByCases(character);
                        }
                    }
                    break;
                default:
                    for (GameCharacter character : pack.getCharacters()) {
                        int damage = Integer.parseInt(this.currentEffect.getAmount().get(0));
                        this.board.attackPlayer(this.activePlayer.getCharacter(),
                                character, damage, this.currentEffect.getType());
                        setEnvironment(square, this.board.getPlayerByCharacter(character).getPosition());
                        setHitByCases(character);
                    }

            }
        } else {
            for (int i = 0; i < this.currentEffect.getRequiredDependency(); i++) {
                this.effectsQueue.remove(1);
            }
        }
        if (!mainEffectApplied && this.effectOrder == SECONDARYONE) {
            this.secondaryEffectOneApplied = true;
            this.effectOrder = WeaponEffectOrderType.PRIMARY;
        }
        this.effectsQueue.remove(0);
        if (this.currentEffect.getType() == EffectType.DAMAGE) {
            this.controller.askPowerup(pack.getCharacters());
            return;
        }
        handleEffectsQueue();

    }

    /**
     * Handles an effect combo activation
     * @param active true if you want to active, else false
     */
    void activateCombo(boolean active) {
        if (active) {
            Map<AmmoType, Integer> effectCost = null;
            if(this.activeCombo == SECONDARYONE) {
                effectCost = this.weapon.getSecondaryEffectOne().get(0).getCost();
            } else if (this.activeCombo == SECONDARYTWO) {
                effectCost = this.weapon.getSecondaryEffectTwo().get(0).getCost();
            }
            if (effectCost != null && (effectCost.get(AmmoType.BLUE) > 0 ||
                    effectCost.get(AmmoType.RED) > 0 || effectCost.get(AmmoType.YELLOW) > 0)) {
                this.controller.setEffectSelection(this.activeCombo);
                this.controller.send(new PaymentMessage(PaymentMessageType.REQUEST, PaymentType.EFFECT,
                        this.activePlayer.getCharacter(), effectCost));

            } else {
                selectEffect(this.activeCombo);
            }
        } else {
            handleEffectsQueue();
        }
    }

    /**
     * Handles the effects queue to apply
     */
    void handleEffectsQueue() {
        try {
            this.currentEffect = this.effectsQueue.get(0);
            switch (this.effectOrder) {
                case PRIMARY:
                    this.mainEffectApplied = true;
                    break;
                case ALTERNATIVE:
                    this.mainEffectApplied = true;
                    break;
                case SECONDARYONE:
                    this.secondaryEffectOneApplied = true;
                    break;
                case SECONDARYTWO:
                    this.secondaryEffectTwoApplied = true;
                    break;
            }

        } catch (IndexOutOfBoundsException e) {
            if (!getAvailableEffects().isEmpty()) {
                this.controller.send(new SelectionListMessage<>(SelectionMessageType.EFFECT,
                        this.activePlayer.getCharacter(),
                        new ArrayList<>(getAvailableEffects().keySet())));
            } else {
                resetController();
                this.controller.endEffects();
            }
            return;
        }
        if (!this.currentEffect.getEffectDependency().isEmpty() && this.activeCombo == null &&
                this.currentEffect.getEffectName() == null) {
            switch (this.currentEffect.getEffectDependency().get(0)) {
                case SECONDARYTWO:
                    this.activeCombo = SECONDARYTWO;
                    break;
                case SECONDARYONE:
                    this.activeCombo = SECONDARYONE;
                    break;
                default:
                    this.activeCombo = null;
            }
            if (this.activeCombo != null) {
                this.controller.send(new SingleSelectionMessage(SelectionMessageType.EFFECT_COMBO,
                        this.activePlayer.getCharacter(), this.activeCombo));
                return;
            }
        }
        EffectPossibilityPack pack;
        try {
            pack = seeEffectPossibility(this.currentEffect);
        } catch (UnsupportedOperationException e) {
            if (this.currentEffect.isRequired()) {
                this.effectsQueue = new ArrayList<>();
            } else {
                for (int i=0; i<=this.currentEffect.getRequiredDependency(); i++) {
                    this.effectsQueue.remove(0);
                }
            }
            handleEffectsQueue();
            return;
        }
        this.currentEffect = this.effectsQueue.get(0);
        if (pack.getTargetsAmount().size() == 1 && pack.getTargetsAmount().get(0).equals("MAX")) {
            effectApplication(pack);
        } else {
            this.controller.send(new SingleSelectionMessage(SelectionMessageType.EFFECT_POSSIBILITY,
                    this.activePlayer.getCharacter(), pack));
        }
    }

    /**
     * Resets the effect controller after an application or aborting
     */
    private void resetController() {
        this.hitByMain = new ArrayList<>();
        this.hitBySecondary = new ArrayList<>();
        this.mainEffectApplied = false;
        this.secondaryEffectOneApplied = false;
        this.secondaryEffectTwoApplied = false;
        this.effectsQueue = new ArrayList<>();
        this.activeCombo = null;
    }

    /**
     * Sets a character as hit by an effect
     * @param character you want to set hit
     */
    void setHitByCases(GameCharacter character) {
        if (this.effectOrder == WeaponEffectOrderType.PRIMARY ||
                this.effectOrder == WeaponEffectOrderType.ALTERNATIVE) {
            this.hitByMain.add(this.board.getPlayerByCharacter(character));
        } else if (this.effectOrder == SECONDARYONE || this.effectOrder == SECONDARYTWO) {
            this.hitBySecondary.add(this.board.getPlayerByCharacter(character));
        }
    }

    /**
     * Gets the target player, based on the target type
     * @param target type
     * @return the target player
     */
    private Player getTargetPlayer(TargetType target) {
        switch (target) {
            case SELF:
                return this.activePlayer;
            case FIRSTPLAYER:
                return this.hitByMain.get(0);
            case SECONDPLAYER:
                return this.hitBySecondary.get(0);
            case SQUARE:
                return this.chosenSquare.getActivePlayers().get(0);
            default:
                return null;
        }
    }

    /**
     * Service method to filter things
     * @param ob1 first object
     * @param ob2 second object
     * @return List filtered
     */
    private List<?> filter(List<?> ob1, List<?> ob2) {
        Iterator<?> e = ob1.iterator();
        while (e.hasNext()) {
            if (!ob2.contains(e.next())) {
                e.remove();
            }
        }
        return new ArrayList<>(ob1);
    }
}