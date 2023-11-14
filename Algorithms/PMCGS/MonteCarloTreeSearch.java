package Algorithms.PMCGS;

import java.util.ArrayList;
import Board.Board;
import Algorithms.Algorithm;

public class MonteCarloTreeSearch extends Algorithm {
  private class MCTSNode {
    private MCTSNode parent;
    // children[i] represents the next game state in which current player places disc at location i
    private MCTSNode[] children;
    private int visits;
    private double playerWins;
    private final Board board;
    public MCTSNode(MCTSNode parent, Board board) {
      this.parent = parent;
      this.board = board;
      this.visits = 0;
      this.playerWins = 0;
      children = new MCTSNode[col];
    }

    public int incrVisits() {
      return ++visits;
    }
    public double incrPlayerWins(double result) {
      playerWins += result;
      return playerWins;
    }
    public int getVisits(){
      return this.visits;
    }
    public double getPlayerWins(){
      return this.playerWins;
    }
  }
  
  private MCTSNode root; // starting state
  private final int col;
  private double C;
  private long givenTime;
  
  public MonteCarloTreeSearch(Board board, long time, int param) {
    this.col = board.col;
    this.C = param;
    this.givenTime = time;
    this.root = new MCTSNode(null, board.copy());
  }

  // sets root to new board state given move
  public void updateRoot(int move) {
    if(this.root.children[move] != null ) this.root = this.root.children[move];
    else this.root = new MCTSNode(null, this.root.board.getNextState(move));
  }

  // returns the optimal move for the current player
  public int getOptimalMove() {
    for (long stop = System.nanoTime()+givenTime; stop>System.nanoTime();) {
      MCTSNode selectedNode = select(this.root);
      if(selectedNode == null)
        continue;
      MCTSNode expandNode = expand(selectedNode);
      int result = simulate(expandNode);
      if(this.root.board.getPrint().equals("verbose")) System.out.println("TERMINAL NODE VALUE: " + result +"\n");
      backpropagate(expandNode, result);
    }

    int maxIndex = -1;
    for(int i = 0; i < col; i++) {
      if(this.root.children[i] != null) {
        if(maxIndex == -1 || this.root.children[i].visits > this.root.children[maxIndex].visits)
          maxIndex = i;
      }
    }
    if(this.root.board.getPrint().equals("verbose")){
      for (int i = 0; i < 7; i++) {
        MCTSNode curr = this.root.children[i];
        if(curr!=null){
          double averageScore = curr.getPlayerWins() / curr.getVisits();
          System.out.println("Column " + (i + 1) + ": " + (curr.getVisits() > 0 ? averageScore : "Null"));
        }
      }
      System.out.println("Final Move Selected: " + maxIndex);
    }
    return maxIndex;
  }

  private MCTSNode select(MCTSNode parent) {
    // if parent has at least child without statistics, select parent
    for(int i = 0; i < col; i++) {
      if(parent.children[i] == null && parent.board.canPlace(i)) {
        return parent;
      }
    }

    // if all children have statistics, use UCT to select next node to visit
    double maxSelectionVal = -1;
    int maxIndex = -1;
    for(int i = 0; i < col; i++) {
      if(!parent.board.canPlace(i))
        continue;
      MCTSNode currentChild = parent.children[i];
      double wins;
      if(parent.board.getNextTurn() == Board.PLAYER_YELLOW_TURN) wins = currentChild.playerWins;
      else wins = (currentChild.visits-currentChild.playerWins);

      if(this.root.board.getPrint().equals("verbose")) {
        System.out.println("\nwi: " + wins);
        System.out.println("ni: " + currentChild.visits);
      }
      
      double selectionVal = wins/currentChild.visits 
        + C*Math.sqrt(Math.log(parent.visits)/currentChild.visits);// UCT
      if(selectionVal > maxSelectionVal) {
        maxSelectionVal = selectionVal;
        maxIndex = i;
      }
    }
    // SOMETIMES -1???
    if(maxIndex == -1)
      return null;
    if(this.root.board.getPrint().equals("verbose"))System.out.println("Move Selected: " + maxIndex);
    return select(parent.children[maxIndex]);
  }

  private MCTSNode expand(MCTSNode selectedNode) {
    // get unvisited child nodes
    ArrayList<Integer> unvisitedChildrenIndices = new ArrayList<Integer>(col);
    for(int i = 0; i < col; i++) {
      if(selectedNode.children[i] == null && selectedNode.board.canPlace(i)) {
        unvisitedChildrenIndices.add(i);
      }
    }

    // randomly select unvisited child and create node for it
    int selectedIndex = unvisitedChildrenIndices.get((int)(Math.random()*unvisitedChildrenIndices.size()));
    if(this.root.board.getPrint().equals("verbose"))System.out.println("Node Added\n");
    selectedNode.children[selectedIndex] = new MCTSNode(selectedNode, selectedNode.board.getNextState(selectedIndex));
    return selectedNode.children[selectedIndex];
  } 

  // returns result of simulation
  private int simulate(MCTSNode expandNode) {
    //copy of board
    Board simBoard = expandNode.board.copy();
    //keep simulating until terminal state
    while(simBoard.currentGameState() == Board.ONGOING) {
      simBoard.place((int)(Math.random()*col));
    }

    switch(simBoard.currentGameState()) {
      case Board.PLAYER_YELLOW_WON:
        return 1;
      case Board.PLAYER_RED_WON:
        return -1;
      default:
        return 0;
    }
  }

  private void backpropagate(MCTSNode expandNode, double simulationResult) {
    MCTSNode currNode = expandNode;
    while(currNode != null) {
      //update visits
      if(this.root.board.getPrint().equals("verbose")){
        System.out.println("Updated values:");
        System.out.println("wi: " + currNode.incrPlayerWins(simulationResult)); 
        System.out.println("ni: " + currNode.incrVisits()+"\n"); 
      }else{
        currNode.incrPlayerWins(simulationResult);
        currNode.incrVisits();
      }
      //update score
      //go back to prev parent node
      currNode = currNode.parent;
    }
  }

}