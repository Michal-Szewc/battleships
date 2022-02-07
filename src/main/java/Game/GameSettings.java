package Game;

//Class that stores the settings of the game, has only setters and getters, but arguments to functions are more clear.

public class GameSettings {
    private int mapWidth;
    private int turnLimit;
    private int dreadnoughtCount;
    private int dreadnoughtPoints;
    private int cruiserCount;
    private int cruiserPoints;
    private int destroyerCount;
    private int destroyerPoints;

    public GameSettings(){
        mapWidth = 10;
        turnLimit = 30;
        dreadnoughtCount = 1;
        dreadnoughtPoints = 10;
        cruiserCount = 2;
        cruiserPoints = 5;
        destroyerCount = 4;
        destroyerPoints = 2;
    }

    public void setMapWidth(int mapWidth) {
        if(mapWidth < 5 || mapWidth > 50)
            throw new IllegalArgumentException("Map size must be between 5x5 and 50x50.");
        this.mapWidth = mapWidth;
    }

    public void setTurnLimit(int turnLimit) {
        if(turnLimit < 5)
            throw new IllegalArgumentException("Turn limit must be larger than 3.");
        this.turnLimit = turnLimit;
    }

    public void setDreadnoughtCount(int dreadnoughtCount) {
        if(dreadnoughtCount < 0)
            throw new IllegalArgumentException("Number of dreadnoughts must be positive.");
        this.dreadnoughtCount = dreadnoughtCount;
    }

    public void setDreadnoughtPoints(int dreadnoughtPoints) {
        if(dreadnoughtPoints <= 0 || dreadnoughtPoints > 100)
            throw new IllegalArgumentException("Points for dreadnought must be between  1 and 100.");
        this.dreadnoughtPoints = dreadnoughtPoints;
    }

    public void setCruiserCount(int cruiserCount) {
        if(cruiserCount < 0)
            throw new IllegalArgumentException("Number of cruisers must be positive.");
        this.cruiserCount = cruiserCount;
    }

    public void setCruiserPoints(int cruiserPoints) {
        if(cruiserPoints <= 0 || cruiserPoints > 50)
            throw new IllegalArgumentException("Points for cruiser must be between  1 and 100.");
        this.cruiserPoints = cruiserPoints;
    }

    public void setDestroyerCount(int destroyerCount) {
        if(destroyerCount < 0)
            throw new IllegalArgumentException("Number of destroyers must be positive.");
        this.destroyerCount = destroyerCount;
    }

    public void setDestroyerPoints(int destroyerPoints) {
        if(destroyerPoints <= 0 || destroyerPoints > 20)
            throw new IllegalArgumentException("Points for destroyer must be between  1 and 100.");
        this.destroyerPoints = destroyerPoints;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getTurnLimit() {
        return turnLimit;
    }

    public int getDreadnoughtCount() {
        return dreadnoughtCount;
    }

    public int getDreadnoughtPoints() {
        return dreadnoughtPoints;
    }

    public int getCruiserCount() {
        return cruiserCount;
    }

    public int getCruiserPoints() {
        return cruiserPoints;
    }

    public int getDestroyerCount() {
        return destroyerCount;
    }

    public int getDestroyerPoints() {
        return destroyerPoints;
    }
}
