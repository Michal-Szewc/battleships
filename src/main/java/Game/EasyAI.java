package Game;

public class EasyAI extends AI_base{

    public EasyAI(GameSettings gameSettings) {
        super(gameSettings);
    }

    //shoots at random viable position, unless it is tracking shot ship

    @Override
    public Vector2d shot() {
        Vector2d position;
        if(tracking) {
            position = trackedShot();
            if (position != null)
                return position;
        }
        position = viablePositions.get(random.nextInt(viablePositions.size()));
        viablePositions.remove(position);
        return position;
    }
}
