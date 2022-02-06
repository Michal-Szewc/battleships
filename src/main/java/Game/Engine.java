package Game;

public class Engine {
    private GameSettings gameSettings;
    private Board playerBoard;
    private AI_base AI;

    public Engine(GameSettings gameSettings){
        this.gameSettings = gameSettings;
        AI = new EasyAI(gameSettings);
    }
}
