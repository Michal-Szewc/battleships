package Game;

import java.util.Random;

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

    //returns next position clockwise

    public Direction next(){
        return switch(this){
            case UP -> RIGHT;
            case RIGHT -> DOWN;
            case DOWN -> LEFT;
            case LEFT -> UP;
        };
    }

    //returns next position anticlockwise

    public Direction previous(){
        return switch(this){
            case UP -> LEFT;
            case LEFT -> DOWN;
            case DOWN -> RIGHT;
            case RIGHT -> UP;
        };
    }

    //returns opposite position

    public Direction opposite(){
        return switch (this){
            case UP -> DOWN;
            case LEFT -> RIGHT;
            case DOWN -> UP;
            case RIGHT -> LEFT;
        };
    }

    //returns unit vector of the direction

    public Vector2d toUnitVector(){
        return switch(this){
            case UP -> new Vector2d(0,-1);
            case RIGHT -> new Vector2d(1,0);
            case DOWN -> new Vector2d(0,1);
            case LEFT -> new Vector2d(-1,0);
        };
    }

    //returns random direction

    public static Direction random(){
        Random random = new Random();
        return switch(random.nextInt(4)){
          case 0-> UP;
          case 1 -> RIGHT;
          case 2 -> DOWN;
          case 3 -> LEFT;
            default -> throw new Error("Random did something wierd...");
        };
    }
}
