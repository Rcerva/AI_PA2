package Application;
import java.util.concurrent.TimeUnit;

import Board.Board;
import MonteCarloTreeSearch.MonteCarloTreeSearch;

public class Main{

  public static void main(String[] args) {

    final long GIVEN_TIME = TimeUnit.SECONDS.toNanos(args.length > 0 ? Integer.parseInt(args[0]) : 2);
    String fileName = "test1.txt"; 
    Board board = new Board(fileName);
    System.out.println(board.getAlgorithm());
    System.out.println(board.getParameter());
    System.out.println(board.getTeam());

    while(board.currentGameState() == Board.ONGOING) {
      System.out.println("\n\n"+board);
      int moveColumn;
      do {
        System.out.printf("AI %d determining move: ", board.getNextTurn() == Board.PLAYER_YELLOW_TURN ? 'Y' : 'R');
        MonteCarloTreeSearch ai = new MonteCarloTreeSearch(board, GIVEN_TIME);
        moveColumn = ai.getOptimalMove();
        System.out.println(moveColumn);
      } while(!board.canPlace(moveColumn));
      board.place(moveColumn);
    }

    int gameState = board.currentGameState();
    System.out.println("\n\n\n\n\n");
    System.out.println(board);
    switch(gameState) {
      case Board.PLAYER_YELLOW_WON:
        System.out.println("Player Yellow won.\n");
        break;
      case Board.PLAYER_RED_WON:
        System.out.println("Player Red won.\n");
        break;
      default:
        System.out.println("Tie.\n");
        break;
    }
  }
}
