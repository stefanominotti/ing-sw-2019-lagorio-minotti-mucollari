package it.polimi.se2019.model;

import it.polimi.se2019.model.arena.CardinalPoint;
import it.polimi.se2019.model.arena.Square;
import it.polimi.se2019.model.messages.board.GameSetMessage;
import it.polimi.se2019.model.messages.client.LoadViewMessage;
import it.polimi.se2019.model.messages.player.ScoreMotivation;
import it.polimi.se2019.model.playerassets.AmmoTile;
import it.polimi.se2019.model.playerassets.AmmoType;
import it.polimi.se2019.model.playerassets.weapons.EffectType;
import it.polimi.se2019.model.playerassets.weapons.Weapon;
import it.polimi.se2019.model.playerassets.weapons.WeaponCard;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.*;

public class BoardTest {
    private Board board;

    @Before
    public void setUp() {
        this.board = new Board();
    }

    @Test
    public void notNullTest() {
        assertNotNull(this.board);
    }

    @Test
    public void toJsonTest() {
        assertEquals("{\"skulls\": 0,\"gameState\": \"ACCEPTING_PLAYERS\",\"currentPlayer\": 0," +
                "\"weaponsDeck\": [],\"powerupsDeck\": [],\"ammosDeck\": [],\"powerupsDiscardPile\": []," +
                "\"ammosDiscardPile\": [],\"killshotTrack\": {},\"finalFrenzyOrder\":[],\"deadPlayers\":[]}",
                this.board.toJson());
    }

    @Test
    public void createModelViewTest() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        this.board.loadArena("1");
        LoadViewMessage message = this.board.createModelView(this.board.getPlayers().get(0));
        assertEquals("playerTest1", message.getNickname());
        assertEquals(0, message.getSkulls());
        assertEquals(10, message.getSquares().size());
        assertEquals(0, message.getScore());
        assertFalse(message.isBeforeFirstPlayer());
        assertFalse(message.isFrenzy());
        assertEquals(GameCharacter.BANSHEE, message.getCharacter());
    }

    @Test
    public void playersTest() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        this.board.addPlayer(GameCharacter.DOZER, "playerTest2", "token");
        this.board.addPlayer(GameCharacter.D_STRUCT_OR, "playerTest3", "token");
        this.board.loadArena("1");
        this.board.finalizeGameSetup();
        assertNotNull(this.board.getPlayers());
        assertEquals(3, this.board.getPlayers().size());
    }

    @Test
    public void getPlayerByCharacter() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        assertNotNull(this.board.getValidCharacters());
        assertEquals(this.board.getPlayers().get(0), this.board.getPlayerByCharacter(GameCharacter.BANSHEE));
    }

    @Test
    public void skullsTest() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        this.board.setSkulls(5);
        assertEquals(5, this.board.getSkulls());
    }

    @Test
    public void timersTest() {
        this.board.setDefaultTimers();
        assertEquals(10L*1000L, this.board.getPowerupsTimerDuration());
    }

    @Test
    public void arenaTest() {
        this.board.loadArena("1");
        assertEquals("1", this.board.getArena().toJson());
        this.board.loadArena("2");
        assertEquals("2", this.board.getArena().toJson());
        this.board.loadArena("3");
        assertEquals("3", this.board.getArena().toJson());
        this.board.loadArena("4");
        assertEquals("4", this.board.getArena().toJson());

        GameSetMessage message = this.board.createArenaMessage();

        assertEquals(4, message.getArenaNumber());
        assertEquals(11, message.getSpawnPoints().size());
    }

    @Test
    public void deadPlayersTest() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        this.board.addDeadPlayer(this.board.getPlayers().get(0));
        assertEquals(1, this.board.getDeadPlayers().size());
    }

    @Test
    public void handleDeathTest() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        Player player1 = this.board.getPlayers().get(0);
        this.board.addPlayer(GameCharacter.D_STRUCT_OR, "playerTest2", "token");
        Player player2 = this.board.getPlayers().get(1);
        this.board.addPlayer(GameCharacter.DOZER, "playerTest3", "token");
        Player player3 = this.board.getPlayers().get(2);
        this.board.addPlayer(GameCharacter.VIOLET, "playerTest4", "token");
        Player player4 = this.board.getPlayers().get(3);
        this.board.setSkulls(2);
        this.board.attackPlayer(player1.getCharacter(), player2.getCharacter(), 10, EffectType.DAMAGE);
        assertEquals(10, player2.getDamages().size());
        this.board.attackPlayer(player1.getCharacter(), player2.getCharacter(), 2, EffectType.DAMAGE);
        assertEquals(0, player2.getDamages().size());
        assertEquals(GameCharacter.BANSHEE, this.board.getKillshotTrack().get(2).get(0));
        assertEquals(GameCharacter.BANSHEE, this.board.getKillshotTrack().get(2).get(1));
        assertEquals(9, player1.getScore());
        assertTrue(player2.isDead());
        assertEquals(6, (long) player2.getKillshotPoints().get(0));
        assertEquals(1, this.board.getSkulls());
        this.board.attackPlayer(player1.getCharacter(), player3.getCharacter(), 5, EffectType.DAMAGE);
        assertEquals(5, player3.getDamages().size());
        this.board.attackPlayer(player2.getCharacter(), player3.getCharacter(), 5, EffectType.DAMAGE);
        assertEquals(10, player3.getDamages().size());
        this.board.attackPlayer(player4.getCharacter(), player3.getCharacter(), 1, EffectType.DAMAGE);
        assertEquals(0, player3.getDamages().size());
        assertEquals(18, player1.getScore());
        assertEquals(6, player2.getScore());
        assertEquals(4, player4.getScore());
        assertEquals(1, this.board.getKillshotTrack().get(1).size());
        assertEquals(GameCharacter.VIOLET, this.board.getKillshotTrack().get(1).get(0));
    }

    @Test
    public void marksTest() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        Player player1 = this.board.getPlayers().get(0);
        this.board.addPlayer(GameCharacter.D_STRUCT_OR, "playerTest2", "token");
        Player player2 = this.board.getPlayers().get(1);
        this.board.attackPlayer(player1.getCharacter(), player2.getCharacter(), 3, EffectType.MARK);
        assertEquals(3, player2.getRevengeMarks().size());
        this.board.attackPlayer(player1.getCharacter(), player2.getCharacter(), 2, EffectType.MARK);
        assertEquals(3, player2.getRevengeMarks().size());
        this.board.attackPlayer(player1.getCharacter(), player2.getCharacter(), 2, EffectType.DAMAGE);
        assertEquals(5, player2.getDamages().size());
    }

    @Test
    public void finalFrenzyStartTest() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        Player player1 = this.board.getPlayers().get(0);
        this.board.addPlayer(GameCharacter.D_STRUCT_OR, "playerTest2", "token");
        Player player2 = this.board.getPlayers().get(1);
        this.board.setSkulls(1);
        this.board.attackPlayer(player1.getCharacter(), player2.getCharacter(), 11, EffectType.DAMAGE);
        assertEquals(0, this.board.getSkulls());
        this.board.startFinalFrenzy(GameCharacter.BANSHEE);
        assertEquals(2, this.board.getFinalFrenzyOrder().size());
        assertEquals(GameCharacter.D_STRUCT_OR, this.board.getFinalFrenzyOrder().get(0));
        assertEquals(GameCharacter.BANSHEE, this.board.getFinalFrenzyOrder().get(1));
    }

    @Test
    public void endGameTest() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        Player player1 = this.board.getPlayers().get(0);
        this.board.addPlayer(GameCharacter.D_STRUCT_OR, "playerTest2", "token");
        Player player2 = this.board.getPlayers().get(1);
        this.board.addPlayer(GameCharacter.DOZER, "playerTest3", "token");
        Player player3 = this.board.getPlayers().get(2);
        this.board.setSkulls(5);
        this.board.attackPlayer(player1.getCharacter(), player2.getCharacter(), 1, EffectType.DAMAGE);
        assertEquals(1, player2.getDamages().size());
        this.board.attackPlayer(player3.getCharacter(), player2.getCharacter(), 7, EffectType.DAMAGE);
        assertEquals(8, player2.getDamages().size());
        this.board.attackPlayer(player1.getCharacter(), player2.getCharacter(), 4, EffectType.DAMAGE);
        assertEquals(0, player2.getDamages().size());
        this.board.attackPlayer(player1.getCharacter(), player2.getCharacter(), 12, EffectType.DAMAGE);
        assertEquals(0, player2.getDamages().size());
        this.board.attackPlayer(player2.getCharacter(), player1.getCharacter(), 12, EffectType.DAMAGE);
        assertEquals(0, player1.getDamages().size());
        this.board.attackPlayer(player2.getCharacter(), player1.getCharacter(), 12, EffectType.DAMAGE);
        assertEquals(0, player1.getDamages().size());
        this.board.attackPlayer(player3.getCharacter(), player1.getCharacter(), 12, EffectType.DAMAGE);
        assertEquals(0, player1.getDamages().size());
        this.board.endGame();
        assertEquals(22, player1.getScore());
        assertEquals(22, player2.getScore());
        assertEquals(17, player3.getScore());
    }

    @Test
    public void calculateRankingTest() {
        endGameTest();
        Map<GameCharacter, Integer> ranking = this.board.calculateRanking();
        assertEquals(GameCharacter.BANSHEE, ranking.keySet().toArray()[0]);
        assertEquals(GameCharacter.D_STRUCT_OR, ranking.keySet().toArray()[1]);
        assertEquals(GameCharacter.DOZER, ranking.keySet().toArray()[2]);
    }

    @Test
    public void currentPlayerTest() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        this.board.addPlayer(GameCharacter.D_STRUCT_OR, "playerTest2", "token");
        assertEquals(0, this.board.getCurrentPlayer());
        this.board.incrementCurrentPlayer();
        assertEquals(1, this.board.getCurrentPlayer());
    }

    @Test
    public void frenzyOrderTest() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        this.board.addPlayer(GameCharacter.D_STRUCT_OR, "playerTest2", "token");
        this.board.addFrenzyOrderPlayer(this.board.getPlayers().get(0));
        this.board.addFrenzyOrderPlayer(this.board.getPlayers().get(1));
        assertEquals(GameCharacter.BANSHEE, this.board.getFinalFrenzyOrder().get(0));
        assertEquals(GameCharacter.D_STRUCT_OR, this.board.getFinalFrenzyOrder().get(1));
    }

    @Test
    public void decksTest() {
        this.board.fillPowerupsDeck();
        this.board.fillAmmosDeck();
        this.board.fillWeaponsDeck();
        assertEquals(24, this.board.getPowerupsDeck().size());
        assertEquals(36, this.board.getAmmosDeck().size());
        assertEquals(21, this.board.getWeaponsDeck().size());
        assertEquals(0, this.board.getAmmosDiscardPile().size());
        assertEquals(0, this.board.getPowerupsDiscardPile().size());
    }

    @Test
    public void fillSquaresTest() {
        this.board.loadArena("1");
        this.board.fillPowerupsDeck();
        this.board.fillAmmosDeck();
        this.board.fillWeaponsDeck();
        this.board.fillAmmoTiles();
        this.board.fillWeaponStores();

        for (Square s : this.board.getArena().getAllSquares()) {
            if (s.isSpawn()) {
                assertEquals(3, s.getWeaponsStore().size());
            } else {
                assertNotNull(s.getAvailableAmmoTile());
            }
        }
    }

    @Test
    public void powerupsTest() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        this.board.fillPowerupsDeck();
        this.board.drawPowerup(this.board.getPlayers().get(0));
        assertEquals(23, this.board.getPowerupsDeck().size());
        this.board.removePowerup(this.board.getPlayers().get(0), this.board.getPlayers().get(0).getPowerups().get(0));
        assertEquals(1, this.board.getPowerupsDiscardPile().size());
    }

    @Test
    public void weaponsPickupTest() {
        this.board.loadArena("1");
        this.board.fillWeaponsDeck();
        this.board.fillWeaponStores();
        Square spawn = null;
        for (Square s : this.board.getArena().getAllSquares()) {
            if (s.isSpawn()) {
                spawn = s;
                break;
            }
        }
        assertNotNull(spawn);
        WeaponCard weapon = spawn.getWeaponsStore().get(0);
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        Player player = this.board.getPlayers().get(0);
        player.setPosition(spawn);
        this.board.giveWeapon(player, weapon);
        assertEquals(weapon, player.getWeapons().get(0));
        WeaponCard newWeapon = spawn.getWeaponsStore().get(0);
        this.board.switchWeapon(player, weapon, newWeapon);
        assertEquals(newWeapon, player.getWeapons().get(0));
    }

    @Test
    public void reloadTest() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        Player player = this.board.getPlayers().get(0);
        player.addWeapon(new WeaponCard(Weapon.PLASMA_GUN));
        WeaponCard weaponCard = player.getWeapons().get(0);
        this.board.unloadWeapon(player, weaponCard);
        assertFalse(weaponCard.isReady());
        this.board.loadWeapon(player, weaponCard);
        assertTrue(weaponCard.isReady());
    }

    @Test
    public void giveAmmoTileTest() {
        this.board.loadArena("1");
        this.board.fillPowerupsDeck();
        this.board.fillAmmosDeck();
        this.board.fillAmmoTiles();
        Square square = null;
        for (Square s : this.board.getArena().getAllSquares()) {
            if (!s.isSpawn()) {
                square = s;
                break;
            }
        }
        assertNotNull(square);
        AmmoTile tile = square.getAvailableAmmoTile();
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        Player player = this.board.getPlayers().get(0);
        player.setPosition(square);
        this.board.giveAmmoTile(player, tile);
        if (tile.hasPowerup()) {
            assertEquals(1, player.getPowerups().size());
        }
        assertEquals((long) tile.getAmmos().get(AmmoType.RED) + 1,
                (long) player.getAvailableAmmos().get(AmmoType.RED));
        assertEquals((long) tile.getAmmos().get(AmmoType.BLUE) + 1,
                (long) player.getAvailableAmmos().get(AmmoType.BLUE));
        assertEquals((long) tile.getAmmos().get(AmmoType.YELLOW) + 1,
                (long) player.getAvailableAmmos().get(AmmoType.YELLOW));
    }

    @Test
    public void useAmmosTest() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        Player player = this.board.getPlayers().get(0);
        Map<AmmoType, Integer> ammos = new EnumMap<>(AmmoType.class);
        ammos.put(AmmoType.RED, 1);
        ammos.put(AmmoType.YELLOW, 1);
        this.board.useAmmos(player, ammos);
        assertEquals(0, (long) player.getAvailableAmmos().get(AmmoType.RED));
        assertEquals(1, (long) player.getAvailableAmmos().get(AmmoType.BLUE));
        assertEquals(0, (long) player.getAvailableAmmos().get(AmmoType.YELLOW));
    }

    @Test
    public void spawnMovePlayerTest() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        Player player = this.board.getPlayers().get(0);
        this.board.loadArena("1");
        Square spawn = null;
        Square square = null;
        for (Square s : this.board.getArena().getAllSquares()) {
            if (spawn != null && square != null) {
                break;
            }
            if (s.isSpawn() && spawn == null) {
                spawn = s;
            } else if (!s.isSpawn() && square == null) {
                square = s;
            }
        }
        assertNotNull(spawn);
        assertNotNull(square);
        this.board.respawnPlayer(player, spawn.getRoom());
        assertEquals(spawn, player.getPosition());
        assertEquals(spawn.getActivePlayers().get(0), player);
        this.board.movePlayer(player, square);
        assertEquals(0, spawn.getActivePlayers().size());
        assertEquals(square, player.getPosition());
        assertEquals(square.getActivePlayers().get(0), player);
    }

    @Test
    public void raisePlayerScoreTest() {
        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        Player player = this.board.getPlayers().get(0);
        this.board.raisePlayerScore(player, 10, ScoreMotivation.KILLSHOT_TRACK, null);
        assertEquals(10, player.getScore());
        this.board.raisePlayerScore(player, 10, ScoreMotivation.KILLSHOT_TRACK, null);
        assertEquals(20, player.getScore());
    }

    @Test
    public void getVisiblePlayersTest() {
        this.board.loadArena("1");

        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        this.board.addPlayer(GameCharacter.D_STRUCT_OR, "playerTest2", "token");
        this.board.addPlayer(GameCharacter.SPROG, "playerTest3", "token");
        this.board.addPlayer(GameCharacter.DOZER, "playerTest4", "token");
        this.board.addPlayer(GameCharacter.VIOLET, "playerTest5", "token");
        Player p1 = this.board.getPlayerByCharacter(GameCharacter.BANSHEE);
        Player p2 = this.board.getPlayerByCharacter(GameCharacter.D_STRUCT_OR);
        Player p3 = this.board.getPlayerByCharacter(GameCharacter.SPROG);
        Player p4 = this.board.getPlayerByCharacter(GameCharacter.DOZER);
        Player p5 = this.board.getPlayerByCharacter(GameCharacter.VIOLET);

        this.board.movePlayer(p1, this.board.getArena().getSquareByCoordinate(0, 1));
        this.board.movePlayer(p2, this.board.getArena().getSquareByCoordinate(1, 1));
        this.board.movePlayer(p3, this.board.getArena().getSquareByCoordinate(2, 1));
        this.board.movePlayer(p4, this.board.getArena().getSquareByCoordinate(3, 1));
        this.board.movePlayer(p5, this.board.getArena().getSquareByCoordinate(2, 2));

        List<Player> visiblePlayers = new ArrayList<>();
        visiblePlayers.add(p1);
        visiblePlayers.add(p2);
        visiblePlayers.add(p4);

        assertEquals(visiblePlayers, this.board.getVisiblePlayers(p3));


        /* DEBUG
        for (Player p : board.getPlayers()){
            System.out.println(p.getCharacter() + " - " + p.getNickname() + " ("+ p.getPosition().getX() +", "+p.getPosition().getY()+")");
        }
        System.out.println("----");

        for (Player p0 : board.getPlayers()){
            System.out.println("\n"+p0.getNickname() + " vede:");
            for (Player p9 : board.getVisiblePlayers(p0)){
                System.out.println(p9.getNickname());
            }
        } */
    }

    @Test
    public void getPlayersByDistanceTest() {
        board.loadArena("1");

        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        this.board.addPlayer(GameCharacter.D_STRUCT_OR, "playerTest2", "token");
        this.board.addPlayer(GameCharacter.SPROG, "playerTest3", "token");
        this.board.addPlayer(GameCharacter.DOZER, "playerTest4", "token");
        this.board.addPlayer(GameCharacter.VIOLET, "playerTest5", "token");
        Player p1 = board.getPlayerByCharacter(GameCharacter.BANSHEE);
        Player p2 = board.getPlayerByCharacter(GameCharacter.D_STRUCT_OR);
        Player p3 = board.getPlayerByCharacter(GameCharacter.SPROG);
        Player p4 = board.getPlayerByCharacter(GameCharacter.DOZER);
        Player p5 = board.getPlayerByCharacter(GameCharacter.VIOLET);

        this.board.movePlayer(p1, board.getArena().getSquareByCoordinate(2, 2));
        this.board.movePlayer(p2, board.getArena().getSquareByCoordinate(1, 1));
        this.board.movePlayer(p3, board.getArena().getSquareByCoordinate(2, 1));
        this.board.movePlayer(p4, board.getArena().getSquareByCoordinate(3, 1));
        this.board.movePlayer(p5, board.getArena().getSquareByCoordinate(2, 2));

        List<Player> players = new ArrayList<>();
        players.add(p2);
        players.add(p3);
        players.add(p4);

        List<String> distances = new ArrayList<>();
        distances.add("2");
        distances.add("3");

        assertEquals(players, board.getPlayersByDistance(p1, distances));

        /* DEBUG
        for (Player p : board.getPlayersByDistance(p1, distances)) {
            System.out.println(p.getNickname());
        } */
    }

    @Test
    public void getSquaresByDistanceTest() {
        board.loadArena("1");

        List<String> distances = new ArrayList<>();
        distances.add("2");
        distances.add("3");

        List<Square> squares = new ArrayList<>();
        squares.add(board.getArena().getSquareByCoordinate(2, 0));
        squares.add(board.getArena().getSquareByCoordinate(1, 1));
        squares.add(board.getArena().getSquareByCoordinate(2, 1));
        squares.add(board.getArena().getSquareByCoordinate(1, 2));

        assertEquals(squares, board.getSquaresByDistance(board.getArena().getSquareByCoordinate(0, 0), distances));

        /* DEBUG
        for (Square s : board.getSquaresByDistance(board.getArena().getSquareByCoordinate(0, 0), distances)){
            System.out.println(s.getX() + ", " + s.getY());
        } */
    }

    @Test
    public void getSquaresOnCardinalDirectionTest() {
        board.loadArena("1");

        List<Square> squares = new ArrayList<>();
        squares.add(board.getArena().getSquareByCoordinate(1, 1));
        squares.add(board.getArena().getSquareByCoordinate(2, 1));
        squares.add(board.getArena().getSquareByCoordinate(3, 1));

        assertEquals(squares, board.getSquaresOnCardinalDirection(board.getArena().getSquareByCoordinate(0, 1), CardinalPoint.EAST));
        /* DEBUG
        for(Square s : board.getSquaresOnCardinalDirection(board.getArena().getSquareByCoordinate(0, 1), CardinalPoint.EAST)) {
            System.out.println(s.getX() + ", " + s.getY());
            for(Player p : s.getActivePlayers()) {
                p.getNickname();
            }
        } */

    }

    @Test
    public void getPlayersOnCardinalDirectionTest() {
        board.loadArena("1");

        this.board.addPlayer(GameCharacter.BANSHEE, "playerTest1", "token");
        this.board.addPlayer(GameCharacter.D_STRUCT_OR, "playerTest2", "token");
        this.board.addPlayer(GameCharacter.SPROG, "playerTest3", "token");
        this.board.addPlayer(GameCharacter.DOZER, "playerTest4", "token");
        this.board.addPlayer(GameCharacter.VIOLET, "playerTest5", "token");
        Player p1 = board.getPlayerByCharacter(GameCharacter.BANSHEE);
        Player p2 = board.getPlayerByCharacter(GameCharacter.D_STRUCT_OR);
        Player p3 = board.getPlayerByCharacter(GameCharacter.SPROG);
        Player p4 = board.getPlayerByCharacter(GameCharacter.DOZER);
        Player p5 = board.getPlayerByCharacter(GameCharacter.VIOLET);

        this.board.movePlayer(p1, board.getArena().getSquareByCoordinate(0, 1));
        this.board.movePlayer(p2, board.getArena().getSquareByCoordinate(1, 1));
        this.board.movePlayer(p3, board.getArena().getSquareByCoordinate(2, 1));
        this.board.movePlayer(p4, board.getArena().getSquareByCoordinate(3, 1));
        this.board.movePlayer(p5, board.getArena().getSquareByCoordinate(2, 2));

        List<Player> players = new ArrayList<>();
        players.add(p2);
        players.add(p3);
        players.add(p4);

        assertEquals(players, board.getPlayersOnCardinalDirection(p1.getPosition(), CardinalPoint.EAST));
    }

    @Test
    public void disconnectionTest() {
        this.board.addPlayer(GameCharacter.BANSHEE, "1", "token");
        this.board.setGameState(GameState.ACCEPTING_PLAYERS);
        this.board.handleDisconnection(GameCharacter.BANSHEE);
        assertEquals(0, this.board.getPlayers().size());
        this.board.addPlayer(GameCharacter.BANSHEE, "1", "token");
        this.board.addPlayer(GameCharacter.DOZER, "2", "token");
        this.board.addPlayer(GameCharacter.VIOLET, "2", "token");
        this.board.setGameState(GameState.SETTING_UP_GAME);
        this.board.handleDisconnection(GameCharacter.BANSHEE);
        assertEquals(2, this.board.getPlayers().size());
        assertEquals(GameState.ACCEPTING_PLAYERS, this.board.getGameState());
        assertEquals(GameCharacter.DOZER, this.board.getPlayers().get(0).getCharacter());
        this.board.handleDisconnection(GameCharacter.DOZER);
        assertEquals(1, this.board.getPlayers().size());
        this.board.addPlayer(GameCharacter.BANSHEE, "1", "token");
        this.board.addPlayer(GameCharacter.DOZER, "2", "token");
        this.board.setGameState(GameState.IN_GAME);
        this.board.handleDisconnection(GameCharacter.BANSHEE);
        assertEquals(3, this.board.getPlayers().size());
    }
}