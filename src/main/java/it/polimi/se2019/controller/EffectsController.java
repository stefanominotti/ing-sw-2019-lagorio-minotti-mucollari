package it.polimi.se2019.controller;

import it.polimi.se2019.model.*;
import org.omg.PortableServer.THREAD_POLICY_ID;

import java.util.*;

public class EffectsController {
    private Board board;
    private WeaponEffect currentEffect;
    private WeaponEffectOrderType effectOrder;
    private TurnController turnController;
    //da impostare ogni volta che viene scelto un'arma
    private Weapon weapon;
    private List<List<WeaponEffect>> weaponEffects;
    //da impostare ogni volta che viene eseguito un effetto
    private int effectApplied;
    private boolean mainEffectApplied;
    private boolean secondaryEffectOneApplied;
    private boolean secondaryEffectTwoApplied;
    private List<WeaponEffect> effectsQueue;
    private List<WeaponEffect> secondEffectQueue;
    //da impostare ogni volta che viene eseguito parte di un effetto
    private List<Player> hitByMain;
    private List<Player> hitBySecondary;
    private Room chosenRoom;
    private Square chosenSquare;
    private CardinalPoint chosenDirection;
    private Player activePlayer;

    public EffectsController(Board board, TurnController turnController) {
        this.turnController = turnController;
        this.board = board;
        this.hitByMain = new ArrayList<>();
        this.hitBySecondary = new ArrayList<>();
        this.mainEffectApplied = false;
        this.secondaryEffectOneApplied = false;
        this.secondaryEffectTwoApplied = false;
        this.weaponEffects = new ArrayList<>();
        this.activePlayer = turnController.getActivePlayer();
    }

    //for test only
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

    public void setChosenRoom(Room room) {
        this.chosenRoom = room;
    }

    public void setEnviroment(Square initialSquare, Square finalSquare) {
        if (!finalSquare.equals(initialSquare)) {
            this.chosenDirection = this.board.getCardinalFromSquares(initialSquare, finalSquare);
            this.chosenRoom = finalSquare.getRoom();
        }
        this.chosenSquare = finalSquare;
    }

    public void setChosenDirection(CardinalPoint chosenDirection) {
        this.chosenDirection = chosenDirection;
    }

    private void buildWeaponEffects(Weapon weapon) {
        this.weapon = weapon;
        this.effectsQueue = new ArrayList<>();
        this.secondEffectQueue = new ArrayList<>();
        if (weapon.getPrimaryEffect() != null) {
            weaponEffects.add(weapon.getPrimaryEffect());
        }
        if (weapon.getAlternativeMode() != null) {
            weaponEffects.add(weapon.getAlternativeMode());
        } else {
            if (weapon.getSecondaryEffectOne() != null) {
                weaponEffects.add(weapon.getSecondaryEffectOne());
                if (weapon.getSecondaryEffectTwo() != null) {
                    weaponEffects.add(weapon.getSecondaryEffectTwo());
                }
            }
        }
    }

    public List<List<WeaponEffect>> getWeaponEffects(Weapon weapon) {
        buildWeaponEffects(weapon);
        return weaponEffects;
    }

    public List<List<WeaponEffect>> getAvailableEffects() {
        List<List<WeaponEffect>> availableWeapons = new ArrayList<>();
        if (!mainEffectApplied) {
            availableWeapons.add(weapon.getPrimaryEffect());
            if (weapon.getAlternativeMode() != null && checkCost(weapon.getAlternativeMode())) {
                availableWeapons.add(weapon.getAlternativeMode());
            }
        }
        if (!secondaryEffectOneApplied && checkCost(weapon.getSecondaryEffectOne())
                && !weapon.getSecondaryEffectTwo().get(0).isCombo() && (
                weapon.getPrimaryEffect().get(0).getEffectDependency().contains("secondaryEffectOne") || mainEffectApplied)) {
            availableWeapons.add(weapon.getSecondaryEffectOne());
        }
        if (!secondaryEffectTwoApplied && checkCost(weapon.getSecondaryEffectTwo())
                && !weapon.getSecondaryEffectTwo().get(0).isCombo() && (
                !(weapon.getSecondaryEffectTwo().get(0).getEffectDependency().contains("secondaryEffectOne") &&
                        !secondaryEffectOneApplied) &&
                        (weapon.getPrimaryEffect().get(0).getEffectDependency().contains("secondaryEffectTwo") || mainEffectApplied))) {
            availableWeapons.add(weapon.getSecondaryEffectTwo());
        }
        return availableWeapons;
    }

    public boolean checkCost(List<WeaponEffect> effect) {
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

    public void effectSelected(String effectType) {
        switch (effectType) {
            case "primary":
                this.effectsQueue.addAll(0, weapon.getPrimaryEffect());
                this.effectOrder = WeaponEffectOrderType.PRIMARY;
                break;
            case "alternative":
                this.effectsQueue.addAll(0, weapon.getAlternativeMode());
                this.effectOrder = WeaponEffectOrderType.PRIMARY;
                break;
            case "secondaryOne":
                this.effectsQueue.addAll(0, weapon.getSecondaryEffectOne());
                this.effectOrder = WeaponEffectOrderType.SECONDARYONE;
                if (!this.mainEffectApplied) {
                    this.effectsQueue.addAll(weapon.getPrimaryEffect());
                }
                break;
            case "secondaryTwo":
                this.effectsQueue.addAll(0, weapon.getSecondaryEffectTwo());
                this.effectOrder = WeaponEffectOrderType.SECONDARYTWO;
                break;
        }
        handleEffectsQueue();
    }

    private List<Player> distancebyPlayerCase(PositionConstraint constraint) {
        List<Player> players = null;
        if (constraint.getTarget() == TargetType.SQUARE) {
            players = this.board.getPlayersByDistance(
                    this.chosenSquare, constraint.getDistanceValues());
        } else {
            players = this.board.getPlayersByDistance(getTargetPlayer(
                    constraint.getTarget()), constraint.getDistanceValues());
        }
        return players;
    }

    private List<Square> distancebySquareCase(PositionConstraint constraint) {
        List<Square> squares;
        if (constraint.getTarget() == TargetType.SQUARE) {
            squares = this.board.getSquaresByDistance(
                    this.chosenSquare, constraint.getDistanceValues());
        } else if (constraint.getTarget() == TargetType.OTHERS) {
            Set<Square> targetSquares = new HashSet<>();
            for (Player target : this.board.getPlayers()) {
                if (!target.equals(this.activePlayer)) {
                    targetSquares.addAll(this.board.getSquaresByDistance(target, constraint.getDistanceValues()));
                }
            }
            squares = new ArrayList<>(targetSquares);
        } else {
            squares = this.board.getSquaresByDistance(getTargetPlayer(
                    constraint.getTarget()).getPosition(), constraint.getDistanceValues());
        }
        return squares;
    }

    private List<Player> filterByPositionConstraintPlayers(List<PositionConstraint> constraints) {
        List<Player> availablePlayers = this.board.getPlayers();
        List<Player> targetPlayers = null;
        for (PositionConstraint constraint : constraints) {
            switch (constraint.getType()) {
                case VISIBLE:
                    targetPlayers = this.board.getVisiblePlayers(getTargetPlayer(constraint.getTarget()));
                    break;
                case DISTANCE:
                    targetPlayers = distancebyPlayerCase(constraint);
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
        List<Square> targetSquares = null;
        for (PositionConstraint constraint : constraints) {
            switch (constraint.getType()) {
                case VISIBLE:
                    targetSquares = this.board.getVisibleSquares(getTargetPlayer(constraint.getTarget()));
                    break;
                case DISTANCE:
                    targetSquares = distancebySquareCase(constraint);
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
        if (constraints != null) {
            availablePlayers = filterByPositionConstraintPlayers(constraints);
            availablePlayers.remove(this.activePlayer);
            return availablePlayers;
        }
        return availablePlayers;
    }

    private List<Square> moveSquaresCase(WeaponEffect effect, Player player) {
        EffectTarget target = effect.getTarget();
        List<Square> availableSquares;
        if (target.getAfterPositionConstraints() == null) {
            availableSquares = this.board.getSquaresByDistance(player, effect.getAmount());
        } else {
            availableSquares = filterByPositionConstraintSquares(target.getAfterPositionConstraints());
        }
        return availableSquares;
    }

    public EffectPossibilityPack seeEffectpossibility(WeaponEffect effect) throws UnsupportedOperationException {
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
        if (target.getTargetConstraints() != null) {
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

    public void effectApplication(EffectPossibilityPack pack) {
        Square square = this.activePlayer.getPosition();
        ;
        try {
            Coordinates coordinates = pack.getSquares().get(0);
            square = this.board.getArena().getSquareByCoordinate(coordinates.getX(), coordinates.getY());
        } catch (IndexOutOfBoundsException ignore) {
        }
        if (pack.isRequire()) {
            switch (this.currentEffect.getType()) {
                case SELECT:
                    this.chosenSquare = square;
                    try {
                        this.chosenRoom = this.board.getArena().getRoombyColor(pack.getRooms().get(0));
                    } catch (IndexOutOfBoundsException ignore) {
                    }
                    try {
                        this.chosenDirection = pack.getCardinalPoints().get(0);
                    } catch (IndexOutOfBoundsException ignore) {
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
                this.effectOrder == WeaponEffectOrderType.SECONDARYONE) {
            this.effectOrder = WeaponEffectOrderType.PRIMARY;
        }
        this.effectsQueue.remove(0);
        handleEffectsQueue();
    }

    private void handleEffectsQueue() throws UnsupportedOperationException {
        try {
            currentEffect = this.effectsQueue.get(0);
        } catch (IndexOutOfBoundsException e) {
            switch (this.effectOrder) {
                case PRIMARY:
                    this.mainEffectApplied = true;
                    break;
                case SECONDARYONE:
                    this.secondaryEffectOneApplied = true;
                    break;
                case SECONDARYTWO:
                    this.secondaryEffectTwoApplied = true;
                    break;
            }
            //avvisa fine efetto
            return;
        }
        if (currentEffect.getEffectDependency() != null) {
            //chidei al giocatore se vuole attivare "combo"
            return;
        }
        EffectPossibilityPack pack = seeEffectpossibility(currentEffect);
        //manda al giocatore le possibilità
    }

    private void setHitByCases(GameCharacter character) {
        if (this.effectOrder == WeaponEffectOrderType.PRIMARY) {
            this.hitByMain.add(this.board.getPlayerByCharacter(character));
        } else if (this.effectOrder == WeaponEffectOrderType.SECONDARYONE) {
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
