package Game;

import java.util.ArrayList;
import java.util.List;

public class Board {
    List<Ship> ships = new ArrayList<>();
    Vector2d lowerLeft = new Vector2d(0,0);
    Vector2d upperRight;

    Tile[][] boardState;

    //returns true if the position is in bounds of board

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

    //returns true if the ship placement is valiable - in bounds and not intersection with other ships.

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

    //returns true if there could be ship in this position.

    public boolean viableShipShot(Ship ship){
        if (inbounds(ship.getPosition()) && inbounds(ship.getEnd())){
            Vector2d pos = ship.getPosition();
            do {
                if(boardState[pos.getX()][pos.getY()] == Tile.SHOT || boardState[pos.getX()][pos.getY()] == Tile.SHIPWRECK)
                    return false;
                pos = pos.add(ship.getDirection().toUnitVector());
            } while (!pos.equals(ship.getEnd()));
            return boardState[pos.getX()][pos.getY()] == Tile.SHOT_SHIP || boardState[pos.getX()][pos.getY()] == Tile.HIDDEN;
        }
        return false;
    }

    //tries to place given ship on board

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
            throw new IllegalArgumentException("ship is out of bounds or inersects with other one. pos: ");
    }

    //returns largest coordinates of map

    public int getWidth(){
        return upperRight.getX();
    }

    //returns tile with given coordinates

    public Tile getTile(int x, int y){
        if(!inbounds(new Vector2d(x,y)))
            throw new IllegalArgumentException("Coordinates are out of bounds.");
        return boardState[x][y];
    }

    //returns tile at given position

    public Tile getTile(Vector2d position){
        if(!inbounds(position))
            throw new IllegalArgumentException("Coordinates are out of bounds.");
        return boardState[position.getX()][position.getY()];
    }

    //sets tile at given coordinates

    public void setTile(int x,int y, Tile tile){
        if(!inbounds(new Vector2d(x,y)))
            throw new IllegalArgumentException("Coordinates are out of bounds.");
        boardState[x][y] = tile;
    }

    //sets tile at given position

    public void setTile(Vector2d position, Tile tile){
        if(!inbounds(position))
            throw new IllegalArgumentException("Coordinates are out of bounds.");
        boardState[position.getX()][position.getY()] = tile;
    }

    //returns ship if there is one on given position

    public Ship getShip(Vector2d position){
        for(Ship ship : ships){
            if(ship.inside(position))
                return ship;
        }
        return null;
    }

    //updates destroyed ship - no need to check, it's only used by wrecked ships.

    public void update(Ship ship){
        Vector2d pos = ship.getPosition();
        do {
            boardState[pos.getX()][pos.getY()] = Tile.SHIPWRECK;
            pos = pos.add(ship.getDirection().toUnitVector());
        } while (!pos.equals(ship.getEnd()));
        boardState[pos.getX()][pos.getY()] = Tile.SHIPWRECK;
    }

    //returns true if player/AI can shoot given position

    public boolean toShoot(Vector2d position){
        return inbounds(position) && getTile(position) == Tile.HIDDEN;
    }
}
