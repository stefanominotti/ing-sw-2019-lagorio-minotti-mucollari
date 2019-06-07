package it.polimi.se2019.controller;

import it.polimi.se2019.model.*;
import it.polimi.se2019.model.messages.selections.SelectionMessageType;
import it.polimi.se2019.model.messages.selections.SelectionListMessage;
import it.polimi.se2019.model.messages.selections.SingleSelectionMessage;

import java.util.*;

import static it.polimi.se2019.model.WeaponEffectOrderType.SECONDARYONE;
import static it.polimi.se2019.model.WeaponEffectOrderType.SECONDARYTWO;

public class EffectsController {
    private Board board;
    private GameController controller;
    private WeaponEffect currentEffect;
    private WeaponEffectOrderType effectOrder;
    //da impostare ogni volta che viene scelto un'arma
    private Weapon weapon;
    private List<List<WeaponEffect>> weaponEffects;
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

    public EffectsController(Board board, GameController controller) {
        this.board = board;
        this.controller = controller;
        this.hitByMain = new ArrayList<>();
        this.hitBySecondary = new ArrayList<>();
        this.mainEffectApplied = false;
        this.secondaryEffectOneApplied = false;
        this.secondaryEffectTwoApplied = false;
        this.weaponEffects = new ArrayList<>();
    }

    public void setActivePlayer(Player player) {
        this.activePlayer = player;
    }

    //for test only
    public void setEffectsQueue(List<WeaponEffect> effects) {
        this.effectsQueue = effects;
    }

    //for test only
    public void setEffectOrder(WeaponEffectOrderType order) {
        this.effectOrder = order;
    }

    public void addHitbyMain(Player player) {
        this.hitByMain.add(player);
    }

    public void addHitbySecondary(Player player) {
        this.hitBySecondary.add(player);
    }

    public void setEnviroment(Square initialSquare, Square finalSquare) {
        if (!finalSquare.equals(initialSquare)) {
            this.chosenDirection = this.board.getCardinalFromSquares(initialSquare, finalSquare);
            this.chosenRoom = finalSquare.getRoom();
        }
        this.chosenSquare = finalSquare;
    }

    @Deprecated
    private void buildWeaponEffects(Weapon weapon) {
        this.weapon = weapon;
        this.effectsQueue = new ArrayList<>();
        if (!weapon.getPrimaryEffect().isEmpty()) {
            weaponEffects.add(weapon.getPrimaryEffect());
        }
        if (!weapon.getAlternativeMode().isEmpty()) {
            this.weaponEffects.add(weapon.getAlternativeMode());
        } else {
            if (!weapon.getSecondaryEffectOne().isEmpty()) {
                this.weaponEffects.add(weapon.getSecondaryEffectOne());
                if (!weapon.getSecondaryEffectTwo().isEmpty()) {
                    this.weaponEffects.add(weapon.getSecondaryEffectTwo());
                }
            }
        }
    }

    @Deprecated
    public List<List<WeaponEffect>> getWeaponEffects(Weapon weapon) {
        buildWeaponEffects(weapon);
        return this.weaponEffects;
    }

    void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    List<Weapon> getAvailableWeapons() {
        List<Weapon> availableWeapons = new ArrayList<>();
        for(WeaponCard weaponCard : this.activePlayer.getWeapons()) {
            this.weapon = weaponCard.getWeaponType();
            if(weaponCard.isReady() && !getAvailableEffects().isEmpty()) {
                availableWeapons.add(weaponCard.getWeaponType());
            }
        }
        this.weapon = null;
        return availableWeapons;
    }

    private boolean checkSecondaryFirst() {
        try {
            EffectPossibilityPack pack = seeEffectPossibility(this.weapon.getSecondaryEffectOne().get(0));
            Square originalPosition = this.activePlayer.getPosition();
            for (Coordinates coordinates : pack.getSquares()) {
                Square square = this.board.getArena().getSquareByCoordinate(coordinates.getX(), coordinates.getY());
                this.activePlayer.setPosition(square);
                try {
                    seeEffectPossibility(this.weapon.getPrimaryEffect().get(0));
                    this.activePlayer.setPosition(originalPosition);
                    return true;
                } catch (UnsupportedOperationException e) {
                    // Ignore
                }
            }
            this.activePlayer.setPosition(originalPosition);
            return false;
        } catch (UnsupportedOperationException e) {
            return false;
        }
    }

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
                && (!this.mainEffectApplied &&
                this.weapon.getPrimaryEffect().get(0).getEffectDependency().contains(SECONDARYONE) &&
                checkSecondaryFirst()) || this.mainEffectApplied) {
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
        for(Map.Entry<WeaponEffectOrderType, List<WeaponEffect>> effect : availableWeapons.entrySet()) {
            try {
                seeEffectPossibility(effect.getValue().get(0));
            } catch (UnsupportedOperationException e) {
                toRemove.add(effect.getKey());
            }
        }
        for (WeaponEffectOrderType w : toRemove) {
            availableWeapons.remove(w);
        }
        return availableWeapons;
    }

    boolean checkCost(List<WeaponEffect> effect) {
        for (Map.Entry<AmmoType, Integer> cost : effect.get(0).getCost().entrySet()) {
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

    public void effectSelected(WeaponEffectOrderType effectType) {
        switch (effectType) {
            case PRIMARY:
                this.effectsQueue.addAll(0, weapon.getPrimaryEffect());
                this.effectOrder = WeaponEffectOrderType.PRIMARY;
                break;
            case ALTERNATIVE:
                this.effectsQueue.addAll(0, weapon.getAlternativeMode());
                this.effectOrder = WeaponEffectOrderType.PRIMARY;
                break;
            case SECONDARYONE:
                this.effectsQueue.addAll(0, weapon.getSecondaryEffectOne());
                this.effectOrder = SECONDARYONE;
                if (!this.mainEffectApplied) {
                    this.effectsQueue.addAll(weapon.getPrimaryEffect());
                }
                break;
            case SECONDARYTWO:
                this.effectsQueue.addAll(0, weapon.getSecondaryEffectTwo());
                this.effectOrder = SECONDARYTWO;
                break;
        }
        handleEffectsQueue();
    }

    private List<Player> distanceByPlayerCase(PositionConstraint constraint) {
        List<Player> players;
        if (constraint.getTarget() == TargetType.SQUARE) {
            players = this.board.getPlayersByDistance(
                    this.chosenSquare, constraint.getDistanceValues());
        } else {
            players = this.board.getPlayersByDistance(getTargetPlayer(
                    constraint.getTarget()), constraint.getDistanceValues());
        }
        return players;
    }

    private List<Square> distanceBySquareCase(PositionConstraint constraint) {
        List<Square> squares;
        switch (constraint.getTarget()) {
            case OTHERS:
                Set<Square> targetSquares = new HashSet<>();
                for (Player target : this.board.getPlayers()) {
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
                squares = this.board.getSquaresByDistance(getTargetPlayer(constraint.getTarget()).getPosition(),
                        constraint.getDistanceValues());
        }
        return squares;
    }

    private List<Player> filterByPositionConstraintPlayers(List<PositionConstraint> constraints) {
        List<Player> availablePlayers = this.board.getPlayers();
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
                    targetPlayers = this.board.getNoVisiblePlayers(getTargetPlayer(
                            constraint.getTarget()));
                    break;
                case SAMEDIRECTION:
                    targetPlayers = this.board.getPlayersOnCardinalDirection(
                            this.chosenSquare, this.chosenDirection);
            }
            availablePlayers = (List<Player>) filter(availablePlayers, targetPlayers);
        }
        return availablePlayers;
    }

    private List<Square> filterByPositionConstraintSquares(List<PositionConstraint> constraints) {
        List<Square> availableSquares = this.board.getArena().getAllSquares();
        List<Square> targetSquares = new ArrayList<>();
        for (PositionConstraint constraint : constraints) {
            switch (constraint.getType()) {
                case VISIBLE:
                    targetSquares = this.board.getVisibleSquares(getTargetPlayer(constraint.getTarget()));
                    break;
                case DISTANCE:
                    targetSquares = distanceBySquareCase(constraint);
                    break;
                case SAMEDIRECTION:
                    targetSquares = this.board.getSquaresOnCardinalDirection(
                            this.chosenSquare, this.chosenDirection);
                    break;
            }
            availableSquares = (List<Square>) filter(availableSquares, targetSquares);
        }
        return availableSquares;
    }

    private List<Room> filterByPositionConstraintRooms(List<PositionConstraint> constraints) {
        List<Room> availableRooms = this.board.getArena().getRoomList();
        List<Room> targetRooms = new ArrayList<>();
        for (PositionConstraint constraint : constraints) {
            switch (constraint.getType()) {
                case VISIBLE:
                    targetRooms = this.board.getVisibleRooms(getTargetPlayer(
                            constraint.getTarget()));
                    break;
                case NOTCONTAINS:
                    targetRooms.remove(getTargetPlayer(
                            constraint.getTarget()).getPosition().getRoom());
                    break;
                case DISTANCE:
                    for (Player player : this.board.getPlayers()) {
                        if (!player.equals(this.activePlayer)) {
                            targetRooms.add(player.getPosition().getRoom());
                        }
                    }
                    break;
            }
            availableRooms = (List<Room>) filter(availableRooms, targetRooms);
        }
        return availableRooms;
    }

    private List<CardinalPoint> filterByPositionConstraintCardinals(List<PositionConstraint> constraints) {
        List<CardinalPoint> availableCardinal = new ArrayList<>();
        for (CardinalPoint cardinal : CardinalPoint.values()) {
            if (constraints.get(0).getType() == PositionConstraintType.DISTANCE) {
                if (!this.board.getPlayersOnCardinalDirection(this.activePlayer, cardinal).isEmpty()) {
                    availableCardinal.add(cardinal);
                }
            }
            if (constraints.get(0).getType() == PositionConstraintType.VISIBLE) {
                if (!this.board.getVisibleSquaresOnCardinalDirection(this.activePlayer, cardinal).isEmpty()) {
                    availableCardinal.add(cardinal);
                }
            }
        }
        return availableCardinal;
    }

    private List<Player> damageMarkCase(WeaponEffect effect) {
        EffectTarget target = effect.getTarget();
        TargetPositionType positionType = target.getPositionType();
        List<PositionConstraint> constraints = target.getPositionConstraints();
        List<Player> availablePlayers = this.board.getPlayers();
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
        }
        return availablePlayers;
    }

    private List<Player> moveOthersPlayersCase(WeaponEffect effect) {
        EffectTarget target = effect.getTarget();
        List<PositionConstraint> constraints = target.getPositionConstraints();
        List<Player> availablePlayers = this.board.getPlayers();
        if (!constraints.isEmpty()) {
            availablePlayers = filterByPositionConstraintPlayers(constraints);
            availablePlayers.remove(this.activePlayer);
            return availablePlayers;
        }
        return availablePlayers;
    }

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

    EffectPossibilityPack seeEffectPossibility(WeaponEffect effect) throws UnsupportedOperationException {
        this.currentEffect = effect;
        EffectTarget target = effect.getTarget();
        TargetPositionType positionType = target.getPositionType();
        List<PositionConstraint> constraints = target.getPositionConstraints();
        Boolean noHitbyMain = false;
        Boolean noHitbySecondary = false;
        List<Player> availablePlayers = new ArrayList<>();
        List<Square> availableSquares = new ArrayList<>();
        List<Room> availableRooms = new ArrayList<>();
        List<CardinalPoint> availableCardinal = new ArrayList<>();
        Map<Square, List<Player>> availableMultipleSquares = new HashMap<>();
        List<String> amountTargets = target.getAmount();
        if (!target.getTargetConstraints().isEmpty()) {
            Set<TargetConstraint> targetConstraints = target.getTargetConstraints();
            if (targetConstraints.contains(TargetConstraint.NOHITBYMAIN)) {
                noHitbyMain = true;
            }
            if (targetConstraints.contains(TargetConstraint.NOHITBYSECONDARY)) {
                noHitbySecondary = true;
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
                switch (target.getType()) {
                    case SELF:
                        availablePlayers.add(this.activePlayer);
                        break;
                    case OTHERS:
                        if (availablePlayers.isEmpty()) {
                            availablePlayers = moveOthersPlayersCase(effect);
                        }
                        break;
                }
                if (noHitbyMain) {
                    availablePlayers.removeAll(hitByMain);
                }
                if (noHitbySecondary) {
                    availablePlayers.removeAll(hitBySecondary);
                }
                if (availablePlayers.isEmpty()) {
                    throw new UnsupportedOperationException();
                }
                availableSquares = moveSquaresCase(effect, availablePlayers.get(0));
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
                }
                break;
            default:
                if (availablePlayers.isEmpty()) {
                    if (positionType != TargetPositionType.MULTIPLESQUARES) {
                        availablePlayers = damageMarkCase(effect);
                        availablePlayers.remove(this.activePlayer);
                        if (noHitbyMain) {
                            availablePlayers.removeAll(hitByMain);
                        }
                        if (noHitbySecondary) {
                            availablePlayers.removeAll(hitBySecondary);
                        }
                        if (availablePlayers.isEmpty()) {
                            throw new UnsupportedOperationException();
                        }
                    } else {
                        List<Square> targetSquares = filterByPositionConstraintSquares(constraints);
                        for (Square square : targetSquares) {
                            if (!square.getActivePlayers().isEmpty()) {
                                availableMultipleSquares.put(square, square.getActivePlayers());
                            }
                        }
                        if (availableMultipleSquares.isEmpty()) {
                            throw new UnsupportedOperationException();
                        }
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
                amountTargets, effect.getAmount(), characters, coordinates, roomColors,
                availableCardinal, multipleSquares, effect.isRequired(), effect.getType()));
    }

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
                        setEnviroment(this.activePlayer.getPosition(), square);
                        this.board.movePlayer(this.activePlayer, square);
                    } else {
                        for (GameCharacter character : pack.getCharacters()) {
                            setEnviroment(this.board.getPlayerByCharacter(character).getPosition(), square);
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
                        setEnviroment(square, this.board.getPlayerByCharacter(character).getPosition());
                        setHitByCases(character);

                    }
            }
        }
        if (this.currentEffect.getEffectName() != null && !mainEffectApplied &&
                this.effectOrder == SECONDARYONE) {
            this.effectOrder = WeaponEffectOrderType.PRIMARY;
        }
        this.effectsQueue.remove(0);
        handleEffectsQueue();
    }

    void activateCombo() {
        effectSelected(this.activeCombo);
    }

    private void handleEffectsQueue() {
        try {
            this.currentEffect = this.effectsQueue.get(0);
        } catch (IndexOutOfBoundsException e) {
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
                default:
                    //avvisa fine efetto
                    return;
            }
        }
        if (!this.currentEffect.getEffectDependency().isEmpty()) {
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
            }
            return;
        }
        EffectPossibilityPack pack = seeEffectPossibility(this.currentEffect);
        if (pack.getTargetsAmount().size() == 1 && pack.getTargetsAmount().get(0).equals("MAX")) {
            effectApplication(pack);
        }
        this.controller.send(new SingleSelectionMessage(SelectionMessageType.EFFECT_POSSIBILITY,
                this.activePlayer.getCharacter(), pack));
    }

    private void setHitByCases(GameCharacter character) {
        if (this.effectOrder == WeaponEffectOrderType.PRIMARY ||
                this.effectOrder == WeaponEffectOrderType.ALTERNATIVE) {
            this.hitByMain.add(this.board.getPlayerByCharacter(character));
        } else if (this.effectOrder == SECONDARYONE) {
            this.hitBySecondary.add(this.board.getPlayerByCharacter(character));
        }
    }

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

    private List<?> filter(List<?> ob1, List<?> ob2) {
        Iterator<?> e = ob1.iterator();
        while (e.hasNext()) {
            if (!ob2.contains(e.next())) {
                e.remove();
            }
        }
        return ob1;
    }
}
