package it.polimi.se2019.model;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ArenaTest_2 {
    String number = "2";
    Arena arena;

    @Before
    public void setUp() {
        this.arena = new Arena(number);
    }

    @Test
    public void createArena() {
        assertNotNull(this.arena);
    }

    @Test
    public void getSquareByCoordinates() {
        for(int x = 0; x < 4; x++) {
            for(int y = 0; y < 3; y++) {
                if((x==0 && y==2)) {
                    assertNull(this.arena.getSquareByCoordinate(x, y));
                } else {
                    Square square = this.arena.getSquareByCoordinate(x, y);
                    assertNotNull(square);
                }
            }
        }
    }

    @Test
    public void getRooms() {
        List<Room> rooms = this.arena.getRoomList();
        assertNotNull(rooms);
        for(Room room : rooms) {
            assertNotNull(room);
            if(room.getColor().equals(RoomColor.WHITE) || room.getColor().equals(RoomColor.GREEN)) {
                assertFalse(room.hasSpawn());
            } else {
                assertTrue(room.hasSpawn());
            }
        }
    }

    @Test
    public void getSquaresFromRooms() {
        List<Room> rooms = this.arena.getRoomList();
        assertNotNull(rooms);
        for(Room room : rooms) {
            assertNotNull(room);
            List<Square> squares = room.getSquares();
            assertNotNull(squares);
            for (Square square : squares) {
                assertNotNull(square);
                int x = square.getX();
                int y = square.getY();
                assertEquals(square, this.arena.getSquareByCoordinate(x, y));
                if(x==2 && y==0 || x==0 && y==1 || x==3 && y==2) {
                    assertTrue(square.isSpawn());
                }
                else {
                    assertFalse(square.isSpawn());
                }
            }
        }
    }

    @Test
    public void nearBySquares() {
        for(int x = 0; x < 4; x++) {
            for (int y = 0; y < 3; y++) {
                Square square = this.arena.getSquareByCoordinate(x, y);
                if (square != null) {
                    Map<CardinalPoint, Boolean> nearAccess = square.getNearbyAccessibility();
                    Map<CardinalPoint, Square> nearSquare = square.getNearbySquares();
                    for (CardinalPoint cardinal : CardinalPoint.values()) {
                        switch (cardinal) {
                            case EAST:
                                assertEquals(nearSquare.get(cardinal), this.arena.getSquareByCoordinate(x + 1, y));
                                if(x==0 && y==0 || x==1 && y==0 ||x==2 && y==0
                                        || x==0 && y==1 || x==2 && y==1
                                        || x==1 && y==2 || x==2 && y==2) {
                                    assertTrue(nearAccess.get(cardinal));
                                } else {
                                    assertFalse(nearAccess.get(cardinal));
                                }
                                break;
                            case WEST:
                                assertEquals(nearSquare.get(cardinal), this.arena.getSquareByCoordinate(x - 1, y));
                                if(x==1 && y==0 || x==2 && y==0 || x==3 && y==0
                                        || x==1 && y==1 || x==3 && y==1
                                        || x==2 && y==2 || x==3 && y==2) {
                                    assertTrue(nearAccess.get(cardinal));
                                } else {
                                    assertFalse(nearAccess.get(cardinal));
                                }
                                break;
                            case NORTH:
                                assertEquals(nearSquare.get(cardinal), this.arena.getSquareByCoordinate(x, y - 1));
                                if(x==0 && y==1 || x==2 && y==1 || x==3 && y==1
                                        || x==1 && y==2 || x==2 && y==2 || x==3 && y==2) {
                                    assertTrue(nearAccess.get(cardinal));
                                } else {
                                    assertFalse(nearAccess.get(cardinal));
                                }
                                break;
                            case SOUTH:
                                assertEquals(nearSquare.get(cardinal), this.arena.getSquareByCoordinate(x, y + 1));
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
    }
}