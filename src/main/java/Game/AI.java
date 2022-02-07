package Game;

import Game.Board;
import Game.Tile;
import Game.Vector2d;

public interface AI {

    /**
     * returns position that the Game.AI wants to shoot
     */

    public Vector2d shot();

    /**
     * informs Game.AI that ship was hit or not
     * @param position position of shot
     * @param tile type of board tile that was hit
     * @param ship ship that was hit (null i none was hit)
     */

    public void hit(Vector2d position, Tile tile, Ship ship);

    /**
     * Generates random board
     * @return board with randomly placed ships
     */

    public Board genBoard();
}
