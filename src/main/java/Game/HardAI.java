package Game;

import java.util.ArrayList;
import java.util.List;

public class HardAI extends AI_base{
    private int parity;
    private boolean tracking;
    private Vector2d trackedPosition;
    private Direction trackingDirection;

    public HardAI(GameSettings gameSettings) {
        super(gameSettings);
        parity = random.nextInt(2);
    }

    @Override
    public Vector2d shot() {
        if (tracking) {

        }
        List<Vector2d> positions = new ArrayList<>();
        for (Vector2d position: viablePositions){
            if((position.getY() + position.getY())%2 == parity)
                positions.add(position);
        }
        return positions.get(random.nextInt(positions.size()));
    }

    @Override
    public void hit(Vector2d position, Tile tile) {
        
    }
}
