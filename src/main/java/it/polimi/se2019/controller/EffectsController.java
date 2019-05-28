package it.polimi.se2019.controller;

import it.polimi.se2019.model.*;

import java.util.*;

public class EffectsController {
    private Board board;
    private List<WeaponEffect> currentEffect;
    private TurnController turnController;
    //da impostare ogni volta che viene scelto un'arma
    private Weapon weapon;
    private List<List<WeaponEffect>> weaponEffects;
    //da impostare ogni volta che viene scelto un effetto
    // (quando un'effetto viene scelto diventa true, se anullato e non eseguito torna false)
    private boolean mainEffectApplied;
    private boolean secondaryEffectOneApplied;
    private boolean secondaryEffectTwoApplied;
    private List<WeaponEffect> firstEffectsQueue;
    private List<WeaponEffect> secondEffectQueue;
    //da impostare ogni volta che viene eseguito un effetto
    private List<Player> hitByMain;
    private List<Player> hitBySecondary;
    private Room chosenRoom;
    private Square chosenSquare;
    private CardinalPoint chosenDirection;


    public void addHitbyMain(Player player) {
        this.hitByMain.add(player);
    }

    public void addHitbySecondary(Player player) {
        this.hitBySecondary.add(player);
    }

    public void setChosenRoom(Room room) {
        this.chosenRoom = room;
    }

    public void setChosenSquare(Square square) {
        if(!square.equals(this.chosenSquare) && this.chosenSquare != null) {
            this.chosenDirection = this.board.getCardinalFromSquares(this.chosenSquare, square);
        } else {
            this.chosenSquare = null;
        }
        this.chosenSquare = square;
    }

    public void setChosenDirection(CardinalPoint chosenDirection) {
        this.chosenDirection = chosenDirection;
    }

    public EffectsController(Board board, TurnController turnController) {
        this.turnController = turnController;
        this.board = board;
        this.hitByMain = new ArrayList<>();
        this.hitBySecondary = new ArrayList<>();
        this.mainEffectApplied = false;
        this.secondaryEffectOneApplied = false;
        this.secondaryEffectTwoApplied = false;
        this.weaponEffects = new ArrayList<>();
    }

    private void buildWeaponEffects(Weapon weapon) {
        this.weapon = weapon;
        this.firstEffectsQueue = new ArrayList<>();
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
        if(!mainEffectApplied) {
            availableWeapons.add(weapon.getPrimaryEffect());
            if(weapon.getAlternativeMode() != null && checkCost(weapon.getAlternativeMode())) {
                availableWeapons.add(weapon.getAlternativeMode());
            }
        }
        if(!secondaryEffectOneApplied && checkCost(weapon.getSecondaryEffectOne())
                && !weapon.getSecondaryEffectTwo().get(0).isCombo() && (
                weapon.getPrimaryEffect().get(0).getEffectDependency().contains("secondaryEffectOne") || mainEffectApplied)) {
            availableWeapons.add(weapon.getSecondaryEffectOne());
        }
        if(!secondaryEffectTwoApplied && checkCost(weapon.getSecondaryEffectTwo())
                && !weapon.getSecondaryEffectTwo().get(0).isCombo() && (
                !(weapon.getSecondaryEffectTwo().get(0).getEffectDependency().contains("secondaryEffectOne") &&
                        !secondaryEffectOneApplied) &&
                        (weapon.getPrimaryEffect().get(0).getEffectDependency().contains("secondaryEffectTwo") || mainEffectApplied))) {
            availableWeapons.add(weapon.getSecondaryEffectTwo());
        }
        return availableWeapons;
    }

    public boolean checkCost(List<WeaponEffect> effect) {
        for(Map.Entry<AmmoType, Integer> cost :  effect.get(0).getCost().entrySet()) {
            Integer powerupAmmo = 0;
            for(Powerup powerup : turnController.getActivePlayer().getPowerups()) {
                if(powerup.getColor() == cost.getKey()){
                    powerupAmmo++;
                }
            }
            if(cost.getValue() > turnController.getActivePlayer().getAvailableAmmos().get(cost.getKey()) + powerupAmmo) {
                return false;
            }
        }
        return true;
    }

    public void effectSelected(String effectType) {
        List<WeaponEffect> effect = new ArrayList<>();
        switch (effectType) {
            case "primary":
                effect = weapon.getPrimaryEffect();
                this.mainEffectApplied = true;
                break;
            case "alternative":
                effect = weapon.getAlternativeMode();
                this.mainEffectApplied = true;
                break;
            case "secondaryOne":
                effect = weapon.getSecondaryEffectOne();
                this.secondaryEffectOneApplied = true;
                if(!this.mainEffectApplied) {
                    this.secondEffectQueue.addAll(weapon.getPrimaryEffect());
                    this.mainEffectApplied = true;
                }
                break;
            case "secondaryTwo":
                effect = weapon.getSecondaryEffectTwo();
                this.secondaryEffectTwoApplied = true;
                break;
        }
        for(WeaponEffect effectPart : effect) {
            boolean practicable = true;
            if(!effectPart.equals(effect.get(0)) && !effectPart.getEffectDependency().isEmpty()) {
                //chiedi al giocatore se vuole attivare prima l'effetto secondario
                practicable = false;
            }
            if(!practicable) {
                this.secondEffectQueue.add(effectPart);
                continue;
            }
            firstEffectsQueue.add(effectPart);
        }
        //chiama metodo per gestione code effeeti
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
        List<Square> squares = null;
        if (constraint.getTarget() == TargetType.SQUARE) {
            squares = this.board.getSquaresByDistance(
                    this.chosenSquare, constraint.getDistanceValues());
        } else {
            squares = this.board.getSquaresByDistance(getTargetPlayer(
                    constraint.getTarget()).getPosition(), constraint.getDistanceValues());
        }
        return squares;
    }

    private List<Player> damageCase(WeaponEffect effect) {
        EffectTarget target = effect.getTarget();
        TargetPositionType positionType = target.getPositionType();
        List<PositionConstraint> constraints = target.getPositionConstraints();
        Map<Square, List<Player>> availableMultipleSquares = new HashMap<>();

        List<Player> availablePlayers = this.board.getPlayers();
        List<Player> targetPlayers = null;
        switch (positionType) {
            case EVERYWHERE:
                for (PositionConstraint constraint : constraints) {
                    switch (constraint.getType()) {
                        case VISIBLE:
                            targetPlayers = this.board.getVisiblePlayers(getTargetPlayer(
                                    constraint.getTarget()));
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
                break;
            case SAMESQUARE:
                for (PositionConstraint constraint : constraints) {
                    switch (constraint.getType()) {
                        case DISTANCE:
                            targetPlayers = distancebyPlayerCase(constraint);

                    }
                    availablePlayers = (List<Player>) filter(availablePlayers, targetPlayers);
                }
                break;
            case ROOM:
                availablePlayers = this.chosenRoom.getPlayers();
                break;
        }
        return availablePlayers;
    }

    private List<Player> markCase(PositionConstraint constraint) {
        List<Player> targetPlayers = this.board.getVisiblePlayers(getTargetPlayer(constraint.getTarget()));
        return targetPlayers;
    }

    private List<Square> moveSelfCase(WeaponEffect effect) {
        EffectTarget target = effect.getTarget();
        List<PositionConstraint> constraints = target.getPositionConstraints();

        List<Square> availableSquares = null;
        if (target.getAfterPositionConstraints() != null) {
            availableSquares = distancebySquareCase(constraints.get(0));

        } else {
            availableSquares = this.board.getSquaresByDistance(
                    this.turnController.getActivePlayer().getPosition(), effect.getAmount());
        }

        return availableSquares;
    }

    private List<Player> moveOthersPlayersCase(WeaponEffect effect) {
        EffectTarget target = effect.getTarget();
        List<PositionConstraint> constraints = target.getPositionConstraints();
        Player noHitbyMain = null;
        Player noHitbySecondary = null;
        List<Player> availablePlayers = this.board.getPlayers();
        if (target.getTargetConstraints().contains(TargetConstraint.ONLYHITBYMAIN)) {
            availablePlayers = new ArrayList<>(this.hitByMain);
        }
        if (target.getTargetConstraints().contains(TargetConstraint.ONLYHITBYSECONDARY)) {
            availablePlayers = new ArrayList<>(this.hitBySecondary);
        }
        if (constraints != null) {
            availablePlayers = distancebyPlayerCase(constraints.get(0));
        }
        if (target.getTargetConstraints().contains(TargetConstraint.NOHITBYMAIN)) {
            noHitbyMain = this.hitByMain.get(0);
        }
        if (target.getTargetConstraints().contains(TargetConstraint.NOHITBYSECONDARY)) {
            noHitbySecondary = this.hitBySecondary.get(0);
        }
        availablePlayers.remove(turnController.getActivePlayer());
        availablePlayers.remove(noHitbyMain);
        availablePlayers.remove(noHitbySecondary);
        return availablePlayers;
    }

    private List<Square> moveOthersSquaresCase(WeaponEffect effect, Player player) {
        EffectTarget target = effect.getTarget();
        List<Square> availableSquares = null;
        List<Square> targetSquares = null;
        if (target.getAfterPositionConstraints() == null) {
            availableSquares = this.board.getSquaresByDistance(
                    player.getPosition(), effect.getAmount());
        }
        for (PositionConstraint constraint : target.getAfterPositionConstraints()) {
            availableSquares = this.board.getSquaresByDistance(
                    player.getPosition(), effect.getAmount());
            switch (constraint.getType()) {
                case VISIBLE:
                    targetSquares = this.board.getVisibleSquares(getTargetPlayer(
                            constraint.getTarget()));
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

    private List<Square> selectSquareCase(WeaponEffect effect) {
        EffectTarget target = effect.getTarget();
        List<PositionConstraint> constraints = target.getPositionConstraints();
        List<Square> availableSquares = this.board.getArena().getAllSquares();
        List<Square> targetSquares = null;
        for (PositionConstraint constraint : constraints) {
            switch (constraint.getType()) {
                case DISTANCE:
                    targetSquares = distancebySquareCase(constraint);
                    break;
                case SAMEDIRECTION:
                    targetSquares = this.board.getSquaresOnCardinalDirection(
                            this.chosenSquare, this.chosenDirection);
                    break;
                case VISIBLE:
                    targetSquares = this.board.getVisibleSquares(getTargetPlayer(
                            constraint.getTarget()));
                    break;
            }
            availableSquares = (List<Square>) filter(availableSquares, targetSquares);
        }
        return availableSquares;
    }

    private List<Room> selectRoomCase(WeaponEffect effect) {
        EffectTarget target = effect.getTarget();
        List<PositionConstraint> constraints = target.getPositionConstraints();
        List<Room> availableRooms = this.board.getArena().getRoomList();
        List<Room> targetRooms = null;

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
            }
            availableRooms = (List<Room>) filter(availableRooms, targetRooms);
        }
        return availableRooms;
    }

    public EffectPossibilityPack seeEffectpossibility(WeaponEffect effect) {
        EffectTarget target = effect.getTarget();
        TargetPositionType positionType = target.getPositionType();
        List<PositionConstraint> constraints = target.getPositionConstraints();
        Player noHitbyMain = null;
        Player noHitbySecondary = null;
        List<Player> availablePlayers = new ArrayList<>();
        List<Square> availableSquares = new ArrayList<>();
        List<Room> availableRooms = new ArrayList<>();
        List<CardinalPoint> availableCardinal = new ArrayList<>();
        Map<Square, List<Player>> availableMultipleSquares = new HashMap<>();
        List<String> amountTargets = target.getAmount();
        if (target.getTargetConstraints() != null) {
            Set<TargetConstraint> targetConstraints = target.getTargetConstraints();
            if (targetConstraints.contains(TargetConstraint.NOHITBYMAIN)) {
                noHitbyMain = this.hitByMain.get(0);
            }
            if (targetConstraints.contains(TargetConstraint.NOHITBYSECONDARY)) {
                noHitbySecondary = this.hitBySecondary.get(0);
            }
            if (targetConstraints.contains(TargetConstraint.ONLYHITBYMAIN)) {
                availablePlayers = new ArrayList<>(this.hitByMain);
            }
            if (targetConstraints.contains(TargetConstraint.ONLYHITBYSECONDARY)) {
                availablePlayers = new ArrayList<>(this.hitBySecondary);
            }
        }
        switch (effect.getType()) {
            case DAMAGE:
                if (availablePlayers.isEmpty()) {
                    if(positionType != TargetPositionType.MULTIPLESQUARES) {
                        availablePlayers = damageCase(effect);
                    } else {
                        List<Square> targetSquares = distancebySquareCase(constraints.get(0));
                        for (Square square : targetSquares) {
                            availableMultipleSquares.put(square, square.getActivePlayers());
                        }
                    }
                }
                availablePlayers.remove(turnController.getActivePlayer());
                availablePlayers.remove(noHitbyMain);
                availablePlayers.remove(noHitbySecondary);
                break;
            case MARK:
                if (availablePlayers.isEmpty()) {
                    availablePlayers = markCase(effect.getTarget().getPositionConstraints().get(0));
                }
                availablePlayers.remove(turnController.getActivePlayer());
                availablePlayers.remove(noHitbyMain);
                availablePlayers.remove(noHitbySecondary);
                break;
            case MOVE:
                switch (target.getType()) {
                    case SELF:
                        availableSquares = moveSelfCase(effect);
                        break;
                    case OTHERS:
                        availablePlayers = moveOthersPlayersCase(effect);
                        availableSquares = moveOthersSquaresCase(effect, availablePlayers.get(0));
                        break;
                }
                break;
            case SELECT:
                switch (target.getType()) {
                    case SQUARE:
                        availableSquares = selectSquareCase(effect);
                        break;
                    case ROOM:
                        availableRooms = selectRoomCase(effect);
                        break;
                    case CARDINALDIRECTION:
                        for (CardinalPoint cardinal : CardinalPoint.values()) {
                            availableCardinal.add(cardinal);
                        }
                        break;
                }
                break;
        }
        List<GameCharacter> characters = new ArrayList<>();
        for(Player player : availablePlayers) {
            characters.add(player.getCharacter());
        }
        List<Coordinates> coordinates = new ArrayList<>();
        for(Square square : availableSquares) {
            coordinates.add(new Coordinates(square.getX(), square.getY()));
        }
        List<RoomColor> roomColors = new ArrayList<>();
        for (Room room : availableRooms) {
            roomColors.add(room.getColor());
        }
        Map<Coordinates, List<GameCharacter>> multipleSquares = new HashMap<>();
        for(Map.Entry<Square,List<Player>> multiSquare : availableMultipleSquares.entrySet()) {
            Square square = multiSquare.getKey();
            List<GameCharacter> characterList = new ArrayList<>();
            for(Player player : multiSquare.getValue()) {
                characterList.add(player.getCharacter());
            }
            multipleSquares.put(new Coordinates(square.getX(), square.getY()), characterList);
        }
        return (new EffectPossibilityPack(
                amountTargets, effect.getAmount(), characters, coordinates, roomColors, availableCardinal, multipleSquares));
    }

    private Player getTargetPlayer(TargetType target) {
        switch (target) {
            case SELF:
                return this.turnController.getActivePlayer();
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
