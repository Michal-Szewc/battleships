package Game;

import java.util.ArrayList;
import java.util.List;

public class MediumAI extends AI_base{
    private int parity;

    public MediumAI(GameSettings gameSettings) {
        super(gameSettings);
        parity = random.nextInt(2);
    }

    //shoots at random position in grid
    //or shoots tracked ship.

    @Override
    public Vector2d shot() {
        Vector2d temp;
        if (tracking) {
            temp = trackedShot();
            if (temp != null)
                return temp;
        }
        List<Vector2d> positions = new ArrayList<>();
        for (Vector2d position: viablePositions){
            if((position.getX() + position.getY())%2 == parity)
                positions.add(position);
        }
        temp = positions.get(random.nextInt(positions.size()));
        viablePositions.remove(temp);
        return temp;
    }
}
