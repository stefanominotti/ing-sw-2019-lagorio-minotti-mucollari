package it.polimi.se2019.model;

import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ArenaTest_1 {
    String number = "1";
    Arena arena;

    @Before
    public void setUp() {
        arena = new Arena(number);
    }

    @Test
    public void createArena() {
        assertNotNull(arena);
    }

    @Test
    public void getSquareByCoordinates() {
        for(int x = 0; x < 4; x++){
            for(int y = 0; y < 3; y++){
                if((x==3 && y==0) || (x==0 && y==2)){
                    System.out.println("Not exist Square ["+x+","+y+"]");
                    assertNull(arena.getSquareByCoordinate(x, y));
                }
                else {
                    Square square = arena.getSquareByCoordinate(x, y);
                    System.out.println("Exist Square ["+x+","+y+"]");
                    assertNotNull(square);
                }
            }
        }
    }

    @Test
    public void getRooms() {
        List<Room> rooms = arena.getRoomList();
        assertNotNull(rooms);
        for(Room room : rooms){
            assertNotNull(room);
            if(room.getColor().equals(RoomColor.WHITE)){
                System.out.println(room.getColor()+" not has spawn");
                assertFalse(room.hasSpawn());
            }
            else {
                System.out.println(room.getColor()+ " has spawn");
                assertTrue(room.hasSpawn());
            }
        }
    }

    @Test
    public void getSquaresFromRooms() {
        List<Room> rooms = arena.getRoomList();
        assertNotNull(rooms);
        for(Room room : rooms) {
            assertNotNull(room);
            System.out.println(room.getColor());
            List<Square> squares = room.getSquares();
            assertNotNull(squares);
            for (Square square : squares) {
                assertNotNull(square);
                int x = square.getX();
                int y = square.getY();
                assertEquals(square, arena.getSquareByCoordinate(x, y));
                if(x==2 && y==0 || x==0 && y==1 || x==3 && y==2){
                    assertTrue(square.isSpawn());
                    System.out.println("["+x+","+y+"] is spawn");
                }
                else {
                    assertFalse(square.isSpawn());
                    System.out.println("["+x+","+y+"] is not spawn");
                }
            }
        }
    }

    @Test
    public void nearBySquares() {
        for(int x = 0; x < 4; x++) {
            for (int y = 0; y < 3; y++) {
                Square square = arena.getSquareByCoordinate(x, y);
                if (square != null) {
                    Map<CardinalPoint, Boolean> nearAccess = square.getNearbyAccessibility();
                    Map<CardinalPoint, Square> nearSquare = square.getNearbySquares();
                    for (CardinalPoint cardinal : CardinalPoint.values()) {
                        switch (cardinal) {
                            case EAST:
                                assertEquals(nearSquare.get(cardinal), arena.getSquareByCoordinate(x + 1, y));
                                if(x==0 && y==0 || x==1 && y==0
                                        || x==0 && y==1 || x==1 && y==1 || x==2 && y==1
                                        || x==1 && y==2 || x==2 && y==2){
                                    assertTrue(nearAccess.get(cardinal));
                                }
                                else {assertFalse(nearAccess.get(cardinal));}
                                break;
                            case WEST:
                                assertEquals(nearSquare.get(cardinal), arena.getSquareByCoordinate(x - 1, y));
                                if(x==1 && y==0 || x==2 && y==0
                                        || x==1 && y==1 || x==2 && y==1 || x==3 && y==1
                                        || x==2 && y==2 || x==3 && y==2){
                                    assertTrue(nearAccess.get(cardinal));
                                }
                                else {assertFalse(nearAccess.get(cardinal));}
                                break;
                            case NORTH:
                                assertEquals(nearSquare.get(cardinal), arena.getSquareByCoordinate(x, y - 1));
                                if(x==0 && y==1 || x==2 && y==1
                                        || x==1 && y==2 || x==3 && y==2){
                                    assertTrue(nearAccess.get(cardinal));
                                }
                                else {assertFalse(nearAccess.get(cardinal));}
                                break;
                            case SOUTH:
                                assertEquals(nearSquare.get(cardinal), arena.getSquareByCoordinate(x, y + 1));
                                if(x==0 && y==0 || x==2 && y==0
                                        || x==1 && y==1 || x==3 && y==1){
                                    assertTrue(nearAccess.get(cardinal));
                                }
                                else {assertFalse(nearAccess.get(cardinal));}
                                break;
                        }
                    }
                }
            }
        }
    }
}