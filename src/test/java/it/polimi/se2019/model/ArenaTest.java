package it.polimi.se2019.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.*;

public class ArenaTest {
    private List<Arena> arenaList = new ArrayList<>();
    private static final int NUMBER = 4;

    @Before
    public void setUp() {
        for (int i = 0; i < NUMBER; i++) {
            String number = Integer.toString(i+1);
            Arena arena = new Arena(number);
            arenaList.add(i, arena);
        }
    }

    @Test
    public void createArenaTest() {
        for (int i = 0; i < NUMBER; i++) {
            assertNotNull(this.arenaList.get(i));
        }
    }

    @Test
    public void getSquareByCoordinatesTest() {
        for (int i = 0; i < NUMBER; i++) {
            switch (i) {
                case 0 :
                    for(int x = 0; x < 4; x++) {
                        for(int y = 0; y < 3; y++) {
                            if((x==3 && y==0) || (x==0 && y==2)) {
                                assertNull(this.arenaList.get(i).getSquareByCoordinate(x, y));
                            } else {
                                Square square = this.arenaList.get(i).getSquareByCoordinate(x, y);
                                Assert.assertNotNull(square);
                            }
                        }
                    }
                    break;

                case 1 :
                    for(int x = 0; x < 4; x++) {
                        for (int y = 0; y < 3; y++) {
                            if (x == 0 && y == 2) {
                                assertNull(this.arenaList.get(i).getSquareByCoordinate(x, y));
                            } else {
                                Square square = this.arenaList.get(i).getSquareByCoordinate(x, y);
                                Assert.assertNotNull(square);
                            }
                        }
                    }
                    break;

                case 2 :
                    for(int x = 0; x < 4; x++){
                        for(int y = 0; y < 3; y++){
                            Square square = this.arenaList.get(i).getSquareByCoordinate(x, y);
                            Assert.assertNotNull(square);
                        }
                    }
                    break;

                case 3 :
                    for(int x = 0; x < 4; x++){
                        for(int y = 0; y < 3; y++) {
                            if((x==3 && y==0)){
                                assertNull(this.arenaList.get(i).getSquareByCoordinate(x, y));
                            } else {
                                Square square = this.arenaList.get(i).getSquareByCoordinate(x, y);
                                Assert.assertNotNull(square);
                            }
                        }
                    }
                    break;
            }
        }
    }

    @Test
    public void getRoomsTest() {
        List<Room> rooms;
        for (int i = 0; i < NUMBER; i++) {
            switch (i) {
                case 0 :
                    rooms = this.arenaList.get(i).getRoomList();
                    Assert.assertNotNull(rooms);
                    for(Room room : rooms) {
                        Assert.assertNotNull(room);
                        if(room.getColor().equals(RoomColor.WHITE)) {
                            assertFalse(room.hasSpawn());
                        } else {
                            assertTrue(room.hasSpawn());
                        }
                    }
                    break;

                case 1 :
                    rooms = this.arenaList.get(i).getRoomList();
                    Assert.assertNotNull(rooms);
                    for(Room room : rooms) {
                        Assert.assertNotNull(room);
                        if(room.getColor().equals(RoomColor.WHITE) || room.getColor().equals(RoomColor.GREEN)) {
                            assertFalse(room.hasSpawn());
                        } else {
                            assertTrue(room.hasSpawn());
                        }
                    }
                    break;

                case 2 :
                    rooms = this.arenaList.get(i).getRoomList();
                    Assert.assertNotNull(rooms);
                    for(Room room : rooms) {
                        Assert.assertNotNull(room);
                        if(room.getColor().equals(RoomColor.WHITE)
                                || room.getColor().equals(RoomColor.GREEN)
                                || room.getColor().equals(RoomColor.PURPLE)) {
                            assertFalse(room.hasSpawn());
                        } else {
                            assertTrue(room.hasSpawn());
                        }
                    }
                    break;

                case 3 :
                    rooms = this.arenaList.get(i).getRoomList();
                    Assert.assertNotNull(rooms);
                    for(Room room : rooms) {
                        Assert.assertNotNull(room);
                        if(room.getColor().equals(RoomColor.WHITE)
                                || room.getColor().equals(RoomColor.PURPLE)) {
                            assertFalse(room.hasSpawn());
                        } else {
                            assertTrue(room.hasSpawn());
                        }
                    }
                    break;
            }
        }

    }

    @Test
    public void getSquaresFromRoomsTest() {
       List<Room> rooms;
        for (int i = 0; i < NUMBER; i++) {
            rooms = this.arenaList.get(i).getRoomList();
            Assert.assertNotNull(rooms);
            for(Room room : rooms) {
                Assert.assertNotNull(room);
                List<Square> squares = room.getSquares();
                Assert.assertNotNull(squares);
                for (Square square : squares) {
                    Assert.assertNotNull(square);
                    int x = square.getX();
                    int y = square.getY();
                    assertEquals(square, arenaList.get(i).getSquareByCoordinate(x, y));
                    if (x == 2 && y == 0 || x == 0 && y == 1 || x == 3 && y == 2) {
                        assertTrue(square.isSpawn());
                    } else {
                        assertFalse(square.isSpawn());
                    }
                }
            }
        }
    }

    @Test
    public void nearbySquaresTest() {
        for(int i = 0; i <= NUMBER; i++) {
            switch (i) {
                case 0:
                    for(int x = 0; x < 4; x++) {
                        for (int y = 0; y < 3; y++) {
                            Square square = this.arenaList.get(i).getSquareByCoordinate(x, y);
                            if (square != null) {
                                Map<CardinalPoint, Boolean> nearAccess = square.getNearbyAccessibility();
                                Map<CardinalPoint, Square> nearSquare = square.getNearbySquares();
                                for (CardinalPoint cardinal : CardinalPoint.values()) {
                                    switch (cardinal) {
                                        case EAST:
                                            assertEquals(nearSquare.get(cardinal), this.arenaList.get(i).getSquareByCoordinate(x + 1, y));
                                            if(x==0 && y==0 || x==1 && y==0
                                                    || x==0 && y==1 || x==1 && y==1 || x==2 && y==1
                                                    || x==1 && y==2 || x==2 && y==2){
                                                assertTrue(nearAccess.get(cardinal));
                                            } else {
                                                assertFalse(nearAccess.get(cardinal));
                                            }
                                            break;
                                        case WEST:
                                            assertEquals(nearSquare.get(cardinal), this.arenaList.get(i).getSquareByCoordinate(x - 1, y));
                                            if(x==1 && y==0 || x==2 && y==0
                                                    || x==1 && y==1 || x==2 && y==1 || x==3 && y==1
                                                    || x==2 && y==2 || x==3 && y==2){
                                                assertTrue(nearAccess.get(cardinal));
                                            } else {
                                                assertFalse(nearAccess.get(cardinal));
                                            }
                                            break;
                                        case NORTH:
                                            assertEquals(nearSquare.get(cardinal), this.arenaList.get(i).getSquareByCoordinate(x, y - 1));
                                            if(x==0 && y==1 || x==2 && y==1
                                                    || x==1 && y==2 || x==3 && y==2){
                                                assertTrue(nearAccess.get(cardinal));
                                            } else {
                                                assertFalse(nearAccess.get(cardinal));
                                            }
                                            break;
                                        case SOUTH:
                                            assertEquals(nearSquare.get(cardinal), this.arenaList.get(i).getSquareByCoordinate(x, y + 1));
                                            if(x==0 && y==0 || x==2 && y==0
                                                    || x==1 && y==1 || x==3 && y==1){
                                                assertTrue(nearAccess.get(cardinal));
                                            } else {
                                                assertFalse(nearAccess.get(cardinal));
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    }
                    break;
                
                case 1:
                    for(int x = 0; x < 4; x++) {
                        for (int y = 0; y < 3; y++) {
                            Square square = this.arenaList.get(i).getSquareByCoordinate(x, y);
                            if (square != null) {
                                Map<CardinalPoint, Boolean> nearAccess = square.getNearbyAccessibility();
                                Map<CardinalPoint, Square> nearSquare = square.getNearbySquares();
                                for (CardinalPoint cardinal : CardinalPoint.values()) {
                                    switch (cardinal) {
                                        case EAST:
                                            assertEquals(nearSquare.get(cardinal), this.arenaList.get(i).getSquareByCoordinate(x + 1, y));
                                            if(x==0 && y==0 || x==1 && y==0 ||x==2 && y==0
                                                    || x==0 && y==1 || x==2 && y==1
                                                    || x==1 && y==2 || x==2 && y==2) {
                                                assertTrue(nearAccess.get(cardinal));
                                            } else {
                                                assertFalse(nearAccess.get(cardinal));
                                            }
                                            break;
                                        case WEST:
                                            assertEquals(nearSquare.get(cardinal), this.arenaList.get(i).getSquareByCoordinate(x - 1, y));
                                            if(x==1 && y==0 || x==2 && y==0 || x==3 && y==0
                                                    || x==1 && y==1 || x==3 && y==1
                                                    || x==2 && y==2 || x==3 && y==2) {
                                                assertTrue(nearAccess.get(cardinal));
                                            } else {
                                                assertFalse(nearAccess.get(cardinal));
                                            }
                                            break;
                                        case NORTH:
                                            assertEquals(nearSquare.get(cardinal), this.arenaList.get(i).getSquareByCoordinate(x, y - 1));
                                            if(x==0 && y==1 || x==2 && y==1 || x==3 && y==1
                                                    || x==1 && y==2 || x==2 && y==2 || x==3 && y==2) {
                                                assertTrue(nearAccess.get(cardinal));
                                            } else {
                                                assertFalse(nearAccess.get(cardinal));
                                            }
                                            break;
                                        case SOUTH:
                                            assertEquals(nearSquare.get(cardinal), this.arenaList.get(i).getSquareByCoordinate(x, y + 1));
                                            if(x==0 && y==0 || x==2 && y==0 || x==3 && y==0
                                                    || x==1 && y==1 || x==2 && y==1 || x==3 && y==1) {
                                                assertTrue(nearAccess.get(cardinal));
                                            } else {
                                                assertFalse(nearAccess.get(cardinal));
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    }
                    break;
                
                case 2:
                    for(int x = 0; x < 4; x++) {
                        for (int y = 0; y < 3; y++) {
                            Square square = this.arenaList.get(i).getSquareByCoordinate(x, y);
                            if (square != null) {
                                Map<CardinalPoint, Boolean> nearAccess = square.getNearbyAccessibility();
                                Map<CardinalPoint, Square> nearSquare = square.getNearbySquares();
                                for (CardinalPoint cardinal : CardinalPoint.values()) {
                                    switch (cardinal) {
                                        case EAST:
                                            assertEquals(nearSquare.get(cardinal), this.arenaList.get(i).getSquareByCoordinate(x + 1, y));
                                            if(x==0 && y==0 || x==1 && y==0 ||x==2 && y==0
                                                    || x==2 && y==1
                                                    || x==0 && y==2 || x==1 && y==2 || x==2 && y==2) {
                                                assertTrue(nearAccess.get(cardinal));
                                            } else {
                                                assertFalse(nearAccess.get(cardinal));
                                            }
                                            break;
                                        case WEST:
                                            assertEquals(nearSquare.get(cardinal), this.arenaList.get(i).getSquareByCoordinate(x - 1, y));
                                            if(x==1 && y==0 || x==2 && y==0 || x==3 && y==0
                                                    || x==3 && y==1
                                                    || x==1 && y==2 || x==2 && y==2 || x==3 && y==2) {
                                                assertTrue(nearAccess.get(cardinal));
                                            } else {
                                                assertFalse(nearAccess.get(cardinal));
                                            }
                                            break;
                                        case NORTH:
                                            assertEquals(nearSquare.get(cardinal), this.arenaList.get(i).getSquareByCoordinate(x, y - 1));
                                            if(x==0 && y==1 || x==1 && y==1 || x==2 && y==1 || x==3 && y==1
                                                    || x==0 && y==2 || x==1 && y==2 || x==2 && y==2 || x==3 && y==2) {
                                                assertTrue(nearAccess.get(cardinal));
                                            } else {
                                                assertFalse(nearAccess.get(cardinal));
                                            }
                                            break;
                                        case SOUTH:
                                            assertEquals(nearSquare.get(cardinal), this.arenaList.get(i).getSquareByCoordinate(x, y + 1));
                                            if(x==0 && y==0 || x==1 && y==0 || x==2 && y==0 || x==3 && y==0
                                                    || x==0 && y==1 || x==1 && y==1 || x==2 && y==1 || x==3 && y==1) {
                                                assertTrue(nearAccess.get(cardinal));
                                            } else {
                                                assertFalse(nearAccess.get(cardinal));
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    }
                    break;
                    
                case 3:
                    for(int x = 0; x < 4; x++) {
                        for (int y = 0; y < 3; y++) {
                            Square square = this.arenaList.get(i).getSquareByCoordinate(x, y);
                            if (square != null) {
                                Map<CardinalPoint, Boolean> nearAccess = square.getNearbyAccessibility();
                                Map<CardinalPoint, Square> nearSquare = square.getNearbySquares();
                                for (CardinalPoint cardinal : CardinalPoint.values()) {
                                    switch (cardinal) {
                                        case EAST:
                                            assertEquals(nearSquare.get(cardinal), this.arenaList.get(i).getSquareByCoordinate(x + 1, y));
                                            if(x==0 && y==0 || x==1 && y==0
                                                    || x==2 && y==1
                                                    || x==0 && y==2 || x==1 && y==2 || x==2 && y==2) {
                                                assertTrue(nearAccess.get(cardinal));
                                            } else {
                                                assertFalse(nearAccess.get(cardinal));
                                            }
                                            break;
                                        case WEST:
                                            assertEquals(nearSquare.get(cardinal), this.arenaList.get(i).getSquareByCoordinate(x - 1, y));
                                            if(x==1 && y==0 || x==2 && y==0
                                                    || x==2 && y==1 || x==3 && y==1
                                                    || x==1 && y==2 || x==2 && y==2 || x==3 && y==2) {
                                                assertTrue(nearAccess.get(cardinal));
                                            } else {
                                                assertFalse(nearAccess.get(cardinal));
                                            }
                                            break;
                                        case NORTH:
                                            assertEquals(nearSquare.get(cardinal), this.arenaList.get(i).getSquareByCoordinate(x, y - 1));
                                            if(x==0 && y==1 || x==1 && y==1 || x==2 && y==1
                                                    || x==0 && y==2 || x==1 && y==2 || x==3 && y==2) {
                                                assertTrue(nearAccess.get(cardinal));
                                            } else {
                                                assertFalse(nearAccess.get(cardinal));
                                            }
                                            break;
                                        case SOUTH:
                                            assertEquals(nearSquare.get(cardinal), this.arenaList.get(i).getSquareByCoordinate(x, y + 1));
                                            if(x==0 && y==0 || x==1 && y==0 || x==2 && y==0
                                                    || x==0 && y==1 || x==1 && y==1 || x==3 && y==1) {
                                                assertTrue(nearAccess.get(cardinal));
                                            } else {
                                                assertFalse(nearAccess.get(cardinal));
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    }
                    break;
            }
        }
    }
}