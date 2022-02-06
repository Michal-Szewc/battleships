package Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

abstract public class AI_base implements AI {
    protected GameSettings gameSettings;
    protected Board board;
    protected List<Vector2d> viablePositions = new ArrayList<>();
    protected Random random;

    AI_base(GameSettings gameSettings){
        this.gameSettings = gameSettings;
        this.board = new Board(gameSettings.getMapWidth(), Tile.HIDDEN);
        for(int x = 0; x <= board.getWidth(); x++){
            for(int y=0; y <= board.getWidth(); y++){
                viablePositions.add(new Vector2d(x,y));
            }
        }
        random = new Random();
    }

    public void placeShips(Board board, int length, int limit){
        for(int i =0 ;i < limit; i++){
            List<Vector2d> viablePositions1 = new ArrayList<>();
            List<Vector2d> viablePositions2 = new ArrayList<>();
            for(int x =0; x < gameSettings.getMapWidth(); x++){
                for(int y =0; y < gameSettings.getMapWidth(); y ++){
                    if(board.valiable(new Ship(new Vector2d(x,y),length,Direction.RIGHT,board)))
                        viablePositions1.add(new Vector2d(x,y));
                }
            }
            for(int x =0; x < gameSettings.getMapWidth(); x++){
                for(int y =0; y < gameSettings.getMapWidth(); y ++){
                    if(board.valiable(new Ship(new Vector2d(x,y),length,Direction.UP,board)))
                        viablePositions2.add(new Vector2d(x,y));
                }
            }
            if(viablePositions1.isEmpty() && viablePositions2.isEmpty())
                throw new Error("Board to small to generate much random ship placements, reduce ship number or increase board size.");
            if (viablePositions1.isEmpty())
                board.place(new Ship(viablePositions2.get(random.nextInt(viablePositions2.size())), length,Direction.UP, board));
            else if (viablePositions2.isEmpty())
                board.place(new Ship(viablePositions1.get(random.nextInt(viablePositions1.size())), length,Direction.RIGHT, board));
            else if(random.nextBoolean())
                board.place(new Ship(viablePositions2.get(random.nextInt(viablePositions2.size())), length,Direction.UP, board));
            else
                board.place(new Ship(viablePositions1.get(random.nextInt(viablePositions1.size())), length,Direction.RIGHT, board));
        }
    }

    @Override
    public Board genBoard(){
        Board temp = new Board(gameSettings.getMapWidth(),Tile.EMPTY);
        try {
            placeShips(temp, 5, gameSettings.getDreadnoughtCount());
            placeShips(temp, 4, gameSettings.getCruiserCount());
            placeShips(temp, 2, gameSettings.getDestroyerCount());
            return temp;
        } catch (Error e){
            return new Board();
        }
    }
}
