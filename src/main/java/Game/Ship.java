package Game;

public class Ship {
    Vector2d position;
    int length;
    int hits;
    Direction direction;
    Board board;

    public Ship(Vector2d position, int length, Direction direction, Board board){
        if(length < 2 || length > 5)
            throw new IllegalArgumentException("The length of ship is between 2 and 5.");
        this.board = board;
        this.position = position;
        this.length = length;
        this.hits = 0;
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public Vector2d getPosition() {
        return position;
    }

    public Vector2d getEnd(){
        return position.add(direction.toUnitVector().multiply(length - 1));
    }

    public Vector2d upperRight(){
        return position.upperRight(getEnd());
    }

    public Vector2d lowerLeft(){
        return position.lowerLeft(getEnd());
    }

    public boolean inside(Vector2d position){
        return position.follows(lowerLeft()) && position.proceeds(upperRight());
    }

    //returns true if this ship intersects with the other one.

    public boolean intersects(Ship other){
        if (this.lowerLeft().getX() > other.upperRight().getX() || other.lowerLeft().getX() > this.upperRight().getX())
            return false;
        return this.lowerLeft().getY() > other.upperRight().getY() || other.lowerLeft().getY() > this.upperRight().getY();
    }

    public int getLength() {
        return length;
    }

    //returns true if it has been hit maximal amount of times

    public boolean wrecked(){
        return hits == length;
    }

    //updates hit count and updates board if ship was destroyed

    public void hit(){
        hits += 1;
        if(wrecked())
            board.update(this);
    }
}
