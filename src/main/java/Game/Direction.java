package Game;

public enum Direction {
    UP,
    DOWN,
    RIGHT,
    LEFT;

    public String toString(){
        return switch(this){
            case UP -> "^";
            case RIGHT -> ">";
            case DOWN -> "v";
            case LEFT -> "<";
        };
    }

    public Direction next(){
        return switch(this){
            case UP -> RIGHT;
            case RIGHT -> DOWN;
            case DOWN -> LEFT;
            case LEFT -> UP;
        };
    }

    public Direction previous(){
        return switch(this){
            case UP -> LEFT;
            case LEFT -> DOWN;
            case DOWN -> RIGHT;
            case RIGHT -> UP;
        };
    }

    public Vector2d toUnitVector(){
        return switch(this){
            case UP -> new Vector2d(0,-1);
            case RIGHT -> new Vector2d(1,0);
            case DOWN -> new Vector2d(0,1);
            case LEFT -> new Vector2d(-1,0);
        };
    }
}
