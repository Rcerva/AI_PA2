package Application;
import java.util.concurrent.TimeUnit;

import Algorithms.Algorithm;
import Algorithms.PMCGS.MonteCarloTreeSearch;
import Algorithms.UCT.UpperConfidenceBoundSearch;
import Board.Board;

public class Main{
  public static final String UR_alg = "UR";
  public static final String DLMM_alg = "DLMM";
  public static final String PMCGS_alg = "PMCGS";
  public static final String UCT_alg = "UCT";

  public static final int UR = 1;
  public static final int DLMM_5 = 2;
  public static final int PMCGS_500 = 3;
  public static final int PMCGS_10000 = 4;
  public static final int UCT_500 = 5;
  public static final int UCT_10000 = 6;


  public static void main(String[] args) {

    Algorithm ai;
    Algorithm opponent;

    final long GIVEN_TIME = TimeUnit.SECONDS.toNanos(args.length > 0 ? Integer.parseInt(args[0]) : 2);
    String fileName = "test4.txt"; 
    Board board = new Board(fileName);

    ai = getTheAlgorithm(board, GIVEN_TIME);
    opponent = getOpponent(board, GIVEN_TIME);
    challengeAis( board, GIVEN_TIME, ai,opponent);
  }


  public static int challengeAis(Board board, long GIVEN_TIME, Algorithm ai, Algorithm opponent){
    if(opponent == null){ System.out.println("Invalid Opponent Parameter"); return -1;}
    while(board.currentGameState() == Board.ONGOING) {
      System.out.println("\n\n"+board);
      int moveColumn;
      do {
        if(board.isTurn()) {
          System.out.print("AI: " + (board.getNextTurn() == Board.PLAYER_YELLOW_TURN ? 'Y' : 'R') + " determining move: " );
          moveColumn = ai.getOptimalMove();
          System.out.println(moveColumn);
        }
        else {
          System.out.print("Challenger: " + (board.getNextTurn() == Board.PLAYER_YELLOW_TURN ? 'Y' : 'R') + " determining move: " );
          moveColumn = opponent.getOptimalMove();
          System.out.println(moveColumn);
        }
      } while(!board.canPlace(moveColumn));
      board.place(moveColumn);
      ai.update(moveColumn);
      opponent.update(moveColumn);
    }

    System.out.println("\n\n\n\n\n");
    System.out.println(board);
    int gameState = board.currentGameState();
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

    return gameState;
  }


  public static Algorithm getTheAlgorithm(Board board, long GIVEN_TIME){
     switch(board.getAlgorithm()){
      case UR_alg:
        System.out.println("Alogrithm: UR.\n");
        // return new UR();
        break;
      case DLMM_alg:
        System.out.println("Alogrithm: DLMM.\n");
        // return new DLMM();
        break;
      case PMCGS_alg:
        System.out.println("Alogrithm: PMCGS.\n");
        return new MonteCarloTreeSearch(board, GIVEN_TIME);

      case UCT_alg:
        System.out.println("Alogrithm: UCT.\n");
        return new UpperConfidenceBoundSearch(board, GIVEN_TIME);
      default:
        return null;

    }
    return null;
  }

  
  public static Algorithm getOpponent(Board board, long GIVEN_TIME){
    switch(board.getParameter()) {
      case UR:
        System.out.println("Challenger: UR.\n");
        // return new UR();
        break;
      case DLMM_5:
        System.out.println("Challenger: DLMM_5.\n");
        // return new DLMM();
        break;
      case PMCGS_500:
        System.out.println("Challenger: PMCGS_500.\n");
        return new MonteCarloTreeSearch(board, GIVEN_TIME);
      case PMCGS_10000:
        System.out.println("Challenger: PMCGS_10000.\n");
        return new MonteCarloTreeSearch(board, GIVEN_TIME);
      case UCT_500:
        System.out.println("Challenger: UCT_500.\n");
        return new UpperConfidenceBoundSearch(board, GIVEN_TIME);
      case UCT_10000:
        System.out.println("Challenger: UCT_10000.\n");
        return new UpperConfidenceBoundSearch(board, GIVEN_TIME);
      default:
        System.out.println("No Challenger.\n");
        return null;
    }
    return null;
  }
}
