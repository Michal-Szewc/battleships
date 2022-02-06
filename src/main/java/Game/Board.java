package Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {
    List<Ship> ships = new ArrayList<>();
    Vector2d lowerLeft = new Vector2d(0,0);
    Vector2d upperRight;

    Tile[][] boardState;

    public boolean inbounds(Vector2d position){
        return position.follows(lowerLeft) && position.proceeds(upperRight);
    }

    public Board (int width, Tile fill){
        boardState = new Tile[width][width];
        upperRight = new Vector2d(width - 1,width - 1);
        for(int x = 0; x < width; x++){
            for(int y = 0; y < width; y++){
                boardState[x][y] = fill;
            }
        }
    }

    public Board(){
        Random r = new Random();
        boardState = new Tile[10][10];
        upperRight = new Vector2d(9,9);
        for(int x = 0; x < 10; x++){
            for(int y = 0; y < 10; y++){
                boardState[x][y] = switch(r.nextInt(5)){
                    case 0 -> Tile.EMPTY;
                    case 1 -> Tile.HIDDEN;
                    case 2 -> Tile.SHIP;
                    case 3 -> Tile.SHIPWRECK;
                    case 4 -> Tile.SHOT_SHIP;
                    default -> Tile.HIDDEN;
                };
            }
        }
    }

    public boolean valiable(Ship ship){
        if (inbounds(ship.getPosition()) && inbounds(ship.getEnd())){
            if(ships.isEmpty())
                return true;
            for(Ship any_ship: ships){
                if(ship.intersects(any_ship))
                    return false;
            }
            return true;
        }
        return false;
    }

    public void place(Ship ship){
        if(valiable(ship)) {
            ships.add(ship);
            Vector2d pos = ship.getPosition();
            do {
                boardState[pos.getX()][pos.getY()] = Tile.SHIP;
                pos = pos.add(ship.getDirection().toUnitVector());
            } while (!pos.equals(ship.getEnd()));
            boardState[pos.getX()][pos.getY()] = Tile.SHIP;
        }
        else
            throw new IllegalArgumentException("ship is out of bounds or inersects with other one. pos: " + ship.getPosition() + " " + ship.getEnd() + " " + inbounds(ship.getPosition()) + " " + inbounds(ship.getEnd()));
    }

    public int getWidth(){
        return upperRight.getX();
    }

    public Tile getTile(int x, int y){
        if(x > getWidth() || x < 0 || y > getWidth() || y < 0)
            throw new IllegalArgumentException("Coordinates are out of bounds.");
        return boardState[x][y];
    }

    public void setTile(int x,int y, Tile tile){
        if(x > getWidth() || x < 0 || y > getWidth() || y < 0)
            throw new IllegalArgumentException("Coordinates are out of bounds.");
        boardState[x][y] = tile;
    }

    public Ship getShip(Vector2d position){
        for(Ship ship : ships){
            if(ship.inside(position))
                return ship;
        }
        return null;
    }

    public void update(Ship ship){
        Vector2d pos = ship.getPosition();
        do {
            boardState[pos.getX()][pos.getY()] = Tile.SHIPWRECK;
            pos = pos.add(ship.getDirection().toUnitVector());
        } while (!pos.equals(ship.getEnd()));
        boardState[pos.getX()][pos.getY()] = Tile.SHIPWRECK;
    }
}
