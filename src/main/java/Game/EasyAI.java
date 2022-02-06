package Game;

public class EasyAI extends AI_base{

    public EasyAI(GameSettings gameSettings) {
        super(gameSettings);
    }

    @Override
    public Vector2d shot() {
        Vector2d position = viablePositions.get(random.nextInt(viablePositions.size()));
        return position;
    }

    @Override
    public void hit(Vector2d position, Tile tile) {
        viablePositions.remove(position);
    }
}
