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
     */

    public void hit(Vector2d position, Tile tile);

    public Board genBoard();
}
