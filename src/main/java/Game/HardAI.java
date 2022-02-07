package Game;

import java.util.ArrayList;
import java.util.List;

public class HardAI extends AI_base{
    private int dreadnoughtCount = 0;
    private int cruiserCount = 0;
    private int destroyerCount = 0;
    private int parity;
    private int turn = 0;
    private double placementPoints;

    public HardAI(GameSettings gameSettings){
        super(gameSettings);
        parity = random.nextInt(2);
        placementPoints = Math.min(Math.min((double)gameSettings.getDreadnoughtPoints(),(double)gameSettings.getCruiserPoints()),(double)gameSettings.getDestroyerPoints())/2;
    }

    //after 5 shots like medium AI
    //calculates the best possible position to shoot to have most points, takes into account random grid, to stick to and gives a bit of priority to edges of map.
    //or shoots tracked ship

    @Override
    public Vector2d shot() {
        if(!tracking)
            search();
        Vector2d temp;
        if (tracking) {
            temp = trackedShot();
            if (temp != null)
                return temp;
        }

        // acting like medium AI to reduce possible places of ships, so that it won't always shoot in the middle area.

        turn += 1;
        if(turn < 5){
            List<Vector2d> positions = new ArrayList<>();
            for (Vector2d position: viablePositions){
                if((position.getX() + position.getY())%2 == parity)
                    positions.add(position);
            }
            temp = positions.get(random.nextInt(positions.size()));
            viablePositions.remove(temp);
            return temp;
        }

        //preparing points array.

        double[][] points = new double[board.getWidth() + 1][board.getWidth() + 1];
        for(int x = 0; x <= board.getWidth(); x++){
            for(int y=0; y <= board.getWidth(); y++){
                points[x][y] = 0;
            }
        }

        //adding points for ships.

        addShipPoints(points,5,gameSettings.getDreadnoughtPoints() * (gameSettings.getDreadnoughtCount() - dreadnoughtCount), Direction.RIGHT);
        addShipPoints(points,5,gameSettings.getDreadnoughtPoints() * (gameSettings.getDreadnoughtCount() - dreadnoughtCount), Direction.UP);
        addShipPoints(points,4,gameSettings.getCruiserPoints() * (gameSettings.getCruiserCount() - cruiserCount), Direction.RIGHT);
        addShipPoints(points,4,gameSettings.getCruiserPoints() * (gameSettings.getCruiserCount() - cruiserCount), Direction.UP);
        addShipPoints(points,2,gameSettings.getDestroyerPoints() * (gameSettings.getDestroyerCount() - destroyerCount), Direction.RIGHT);
        addShipPoints(points,2,gameSettings.getDestroyerPoints() * (gameSettings.getDestroyerCount() - destroyerCount), Direction.UP);

        //adding points based in parity and position.

        for(Vector2d position: viablePositions){
            if((position.getX() + position.getY())%2 == parity)
                points[position.getX()][position.getY()] += placementPoints;
            if(position.getX() <= 2 || position.getX() >= board.getWidth() - 2 || position.getY() <= 2 || position.getY() >= board.getWidth() - 2)
                points[position.getX()][position.getY()] *= 1.1;
        }

        //roughly searching for best positions and returning random one.

        List<Vector2d> bestPositions = new ArrayList<>();
        double max = 0;
        for(Vector2d position: viablePositions){
            if(points[position.getX()][position.getY()] > max + 1){
                max = points[position.getX()][position.getY()];
                bestPositions = new ArrayList<>();
            }
            if(points[position.getX()][position.getY()] > max - 1)
                bestPositions.add(position);
        }

        temp = bestPositions.get(random.nextInt(bestPositions.size()));
        viablePositions.remove(temp);
        return temp;
    }

    //adds points of ships to points table.

    private void addShipPoints(double[][] points, int length, double point, Direction direction){
        for(Vector2d position: viablePositions){
            int x = position.getX();
            int y = position.getY();
            if(board.viableShipShot(new Ship(new Vector2d(x,y),length,direction,board))) {
                Vector2d pos = new Vector2d(x,y);
                do {
                    points[pos.getX()][pos.getY()] += point;
                    pos = pos.add(direction.toUnitVector());
                } while (!pos.equals(new Vector2d(x,y).add(direction.toUnitVector().multiply(length - 1))));
                points[pos.getX()][pos.getY()] += point;
            }
        }
    }

    //updates ship counts and the rest like base AI

    @Override
    public void hit(Vector2d position, Tile tile,Ship ship) {
        board.setTile(position,tile);

        //if ship was sunk increase destroyed ship count.

        if(tile == Tile.SHIPWRECK){
            board.update(ship);
            switch (ship.getLength()){
                case 5 -> dreadnoughtCount += 1;
                case 4 -> cruiserCount += 1;
                case 2 -> destroyerCount += 1;
                default -> throw new Error("This ship does not exist.");
            }
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
}
