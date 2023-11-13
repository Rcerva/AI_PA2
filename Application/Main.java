package Application;
import java.util.concurrent.TimeUnit;

import Algorithms.Algorithm;
import Algorithms.PMCGS.MonteCarloTreeSearch;
import Algorithms.UCT.UpperConfidenceBoundSearch;
import Board.Board;

public class Main{
  public static final String UR = "UR";
  public static final String DLMM = "DLMM";
  public static final String PMCGS = "PMCGS";
  public static final String UCT = "UCT";

  public static String[] algortihmList = new String[6];

  public static void main(String[] args) {

    Algorithm ai;
    Algorithm opponent;
    String fileName;
    String print;

    final long GIVEN_TIME = TimeUnit.SECONDS.toNanos(args.length > 0 ? Integer.parseInt(args[0]) : 2);
    
    //Test Algorithms part1
    fileName = "test4.txt"; 
    print = "Verbose";
    Board board = new Board(fileName, print.toLowerCase());
    ai = getTheAlgorithm(board, GIVEN_TIME);
    opponent = getTheAlgorithm(board, GIVEN_TIME);
    challengeAis(board, ai, opponent);
    
    //Tournament part 2
    generateTournament(GIVEN_TIME);
  }

  public static void generateTournament(long time){
    generateChallengers();
    for(String ai: algortihmList)
      for(String opponenet: algortihmList){ 
        Board board = new Board();
        board.setTeam('Y');
        challengeAis(board, getChallengers(board,ai,time), getChallengers(board,opponenet,time));}
      }



  public static int challengeAis(Board board, Algorithm ai, Algorithm opponent){
    if(opponent == null || ai == null)return -1;
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
      ai.updateRoot(moveColumn);
      opponent.updateRoot(moveColumn);
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
      case UR:
        System.out.println("Alogrithm: UR.");
        // return new UR();
        break;
      case DLMM:
        System.out.println("Alogrithm: DLMM.");
        // return new DLMM();
        break;
      case PMCGS:
        System.out.println("Alogrithm: PMCGS.");
        return new MonteCarloTreeSearch(board, GIVEN_TIME, board.getParameter());

      case UCT:
        System.out.println("Alogrithm: UCT.");
        return new UpperConfidenceBoundSearch(board, GIVEN_TIME, board.getParameter());
      default:
        return null;

    }
    return null;
  }

  public static Algorithm getChallengers(Board board, String algorithm, long GIVEN_TIME){
     switch(algorithm){
      case UR:
        System.out.println("Alogrithm: UR.\n");
        // return new UR(GIVEN_TIME);
        break;
      case DLMM:
        System.out.println("Alogrithm: DLMM.\n");
        // return new DLMM(GIVEN_TIME, 5);
        break;
      case PMCGS+"500":
        System.out.println("Alogrithm: PMCGS(500).\n");
        return new MonteCarloTreeSearch(board, GIVEN_TIME, 500);
      case PMCGS+"10000":
        System.out.println("Alogrithm: PMCGS(10000).\n");
        return new MonteCarloTreeSearch(board, GIVEN_TIME, 10000);
      case UCT+"500":
        System.out.println("Alogrithm: UCT(500).\n");
        return new UpperConfidenceBoundSearch(board, GIVEN_TIME, 500);
      case UCT+"10000":
        System.out.println("Alogrithm: UCT(10000).\n");
        return new UpperConfidenceBoundSearch(board, GIVEN_TIME, 10000);
      default:
        return null;

    }
    return null;
  }

  public static void generateChallengers(){
    algortihmList[0] = UR;
    algortihmList[1] = DLMM;
    algortihmList[2] = PMCGS+"500";
    algortihmList[3] = PMCGS+"10000";
    algortihmList[4] = UCT+"500";
    algortihmList[5] = UCT+"10000";
  }
}
