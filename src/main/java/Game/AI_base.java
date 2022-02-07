package Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

abstract public class AI_base implements AI {
    protected GameSettings gameSettings;
    protected Board board;
    protected List<Vector2d> viablePositions = new ArrayList<>();
    protected Random random;

    protected boolean tracking = false;
    protected boolean firstDirection = false;  //first time finding possible direction of ship
    protected boolean secondDirection = false; //reversing the first direction found
    protected Vector2d trackedPosition;        //last position that has been hit
    protected Direction trackingDirection;     //the suspected direction of hit ship

    AI_base(GameSettings gameSettings){
        this.gameSettings = gameSettings;
        this.board = new Board(gameSettings.getMapWidth(), Tile.HIDDEN);
        for(int x = 0; x <= board.getWidth(); x++){
            for(int y=0; y <= board.getWidth(); y++){
                viablePositions.add(new Vector2d(x,y));
            }
        }
        random = new Random();
    }

    // Places number of ships of set length randomly on board

    public void placeShips(Board board, int length, int number){
        for(int i =0 ;i < number; i++){
            List<Vector2d> viablePositions1 = new ArrayList<>();
            List<Vector2d> viablePositions2 = new ArrayList<>();
            for(int x =0; x < gameSettings.getMapWidth(); x++){
                for(int y =0; y < gameSettings.getMapWidth(); y ++){
                    if(board.valiable(new Ship(new Vector2d(x,y),length,Direction.RIGHT,board)))
                        viablePositions1.add(new Vector2d(x,y));
                }
            }
            for(int x =0; x < gameSettings.getMapWidth(); x++){
                for(int y =0; y < gameSettings.getMapWidth(); y ++){
                    if(board.valiable(new Ship(new Vector2d(x,y),length,Direction.UP,board)))
                        viablePositions2.add(new Vector2d(x,y));
                }
            }
            if(viablePositions1.isEmpty() && viablePositions2.isEmpty())
                throw new Error("Board to small to generate much random ship placements, reduce ship number or increase board size.");
            if (viablePositions1.isEmpty())
                board.place(new Ship(viablePositions2.get(random.nextInt(viablePositions2.size())), length,Direction.UP, board));
            else if (viablePositions2.isEmpty())
                board.place(new Ship(viablePositions1.get(random.nextInt(viablePositions1.size())), length,Direction.RIGHT, board));
            else if(random.nextBoolean())
                board.place(new Ship(viablePositions2.get(random.nextInt(viablePositions2.size())), length,Direction.UP, board));
            else
                board.place(new Ship(viablePositions1.get(random.nextInt(viablePositions1.size())), length,Direction.RIGHT, board));
        }
    }

    //generates randomly board of ships.
    //if the board is too small or there are too many ships to be placed,
    //it may cause an error.

    @Override
    public Board genBoard(){
        Board temp = new Board(gameSettings.getMapWidth(),Tile.EMPTY);
        try {
            placeShips(temp, 5, gameSettings.getDreadnoughtCount());
            placeShips(temp, 4, gameSettings.getCruiserCount());
            placeShips(temp, 2, gameSettings.getDestroyerCount());
            return temp;
        } catch (Error e){
            return null;
        }
    }

    // updates AI based on the tile and maybe ship that it hit

    @Override
    public void hit(Vector2d position, Tile tile,Ship ship) {
        board.setTile(position,tile);
        if(tile == Tile.SHIPWRECK){
            board.update(ship);
            search();
            return;
        }

        // if target wasn't hit try in different direction

        if(tile == Tile.SHOT){
            if(tracking){
                if(!firstDirection) {
                    trackingDirection = getDirection(position);
                    if(trackingDirection == null)
                        search();
                    return;
                }
                if(!secondDirection) {
                    secondDirection = true;
                    trackingDirection = trackingDirection.opposite();
                    while(board.inbounds(trackedPosition.add(trackingDirection.toUnitVector())) && board.getTile(trackedPosition.add(trackingDirection.toUnitVector())) == Tile.SHOT_SHIP)
                        trackedPosition = trackedPosition.add(trackingDirection.toUnitVector());
                    return;
                }
                search();
                return;
            }
            return;
        }

        //hit new target, start tracking it

        if(!tracking) {
            trackedPosition = position;
            trackingDirection = Direction.random();
            firstDirection = false;
            secondDirection = false;
            tracking = true;
            return;
        }

        //hit target again - keep tracking

        firstDirection = true;
        trackedPosition = position;
    }

    //searches for ships, that may have been hit, but have not been sunk - it may happen, because there is no restriction in their placement.

    void search(){
        List<Vector2d> shot = new ArrayList<>();
        for(int x = 0; x <= board.getWidth(); x++){
            for(int y = 0; y <= board.getWidth(); y++) {
                if (board.getTile(x, y) == Tile.SHOT_SHIP)
                    shot.add(new Vector2d(x, y));
            }
        }
        if(shot.isEmpty()){
            tracking = false;
            firstDirection = false;
            secondDirection = false;
            return;
        }
        trackedPosition = shot.get(random.nextInt(shot.size()));
        trackingDirection = getDirection(trackedPosition);
        firstDirection = false;
        secondDirection = false;
    }

    // returns direction that ship can be facing.

    public Direction getDirection(Vector2d position){
        List<Direction> viableDirections = new ArrayList<>();
        Direction direction = Direction.random();
        for(int i=0;i<4;i++){
            direction = direction.next();
            if (board.toShoot(position.add(direction.toUnitVector())))
                viableDirections.add(direction);
        }
        if(viableDirections.isEmpty())
            return null;
        return viableDirections.get(random.nextInt(viableDirections.size()));
    }

    //shoots place, that may be a ship, based on the previous hits.

    protected Vector2d trackedShot(){
        if(board.toShoot(trackedPosition.add(trackingDirection.toUnitVector())))
            return trackedPosition.add(trackingDirection.toUnitVector());
        search();
        if(tracking)
            return trackedPosition.add(trackingDirection.toUnitVector());
        return null;
    }
}
