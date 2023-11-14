package Algorithms.UCT;

import java.util.ArrayList;
import Board.Board;
import Algorithms.Algorithm;
import Node.Node;

public class UpperConfidenceBoundSearch extends Algorithm {
  private Node root; // starting state
  private final int col;
  private double C;
  private long time;
  
  public UpperConfidenceBoundSearch(Board board, long time, int param) {
    this.col = board.col;
    this.C = param;
    this.time = time;
    this.root = new Node(null, board.copy());
  }

  // sets root to new board state given move
  public void updateRoot(int move) {
    if(this.root.getChildren()[move] != null ) this.root = this.root.getChildren()[move];
    else this.root = new Node(null, this.root.getBoard().getNextState(move));
  }

  // returns the optimal move for the current player
  public int getOptimalMove() {
    for (long stop = System.nanoTime()+time; stop>System.nanoTime();) {
      Node selected = select(this.root);
      if(selected == null) continue;
      Node expand = expand(selected);
      int result = simulate(expand);
      if(this.root.getBoard().getPrint().equals("verbose")) System.out.println("TERMINAL NODE VALUE: " + result +"\n");
      backpropagate(expand, result);
    }

    int maxIndex = -1;
    for(int i = 0; i < col; i++) {
      if(this.root.getChildren()[i] != null) {
        if(maxIndex == -1 || this.root.getChildren()[i].getVisits() > this.root.getChildren()[maxIndex].getVisits())
          maxIndex = i;
      }
    }
    if(this.root.getBoard().getPrint().equals("verbose")){
      for (int i = 0; i < 7; i++) {
        Node curr = this.root.getChildren()[i];
        if(curr!=null){
          double averageScore = curr.getPlayerWins() / curr.getVisits();
          System.out.println("Column " + (i + 1) + ": " + (curr.getVisits() > 0 ? averageScore : "Null"));
        }
      }
      System.out.println("Final Move Selected: " + maxIndex);
    }
    return maxIndex;
  }

  private Node select(Node parent) {
    // if parent has at least child without statistics, select parent
    for(int i = 0; i < col; i++) {
      if(parent.getChildren()[i] == null && parent.getBoard().canPlace(i)) {
        return parent;
      }
    }

    // if all children have statistics, use UCT to select next node to visit
    double maxSelectionVal = -1;
    int maxIndex = -1;
    for(int i = 0; i < col; i++) {
      if(!parent.getBoard().canPlace(i))
        continue;
      Node curr = parent.getChildren()[i];
      double wins;
      if(parent.getBoard().getNextTurn() == Board.PLAYER_YELLOW_TURN) wins = curr.getPlayerWins();
      else wins = (curr.getVisits()-curr.getPlayerWins());

      if(this.root.getBoard().getPrint().equals("verbose")) {
        System.out.println("\nwi: " + wins);
        System.out.println("ni: " + curr.getVisits());
      }
      
      double selectionVal = wins/curr.getVisits() 
        + C*Math.sqrt(Math.log(parent.getVisits())/curr.getVisits());// UCT
      if(selectionVal > maxSelectionVal) {
        maxSelectionVal = selectionVal;
        maxIndex = i;
      }
    }
    // SOMETIMES -1???
    if(maxIndex == -1)
      return null;
    if(this.root.getBoard().getPrint().equals("verbose"))System.out.println("Move Selected: " + maxIndex);
    return select(parent.getChildren()[maxIndex]);
  }

  private Node expand(Node selected) {
    // get unvisited child nodes
    ArrayList<Integer> unvisitedIndices = new ArrayList<Integer>(col);
    for(int i = 0; i < col; i++) {
      if(selected.getChildren()[i] == null && selected.getBoard().canPlace(i)) {
        unvisitedIndices.add(i);
      }
    }

    // randomly select unvisited child and create node for it
    int selectedIndex = unvisitedIndices.get((int)(Math.random()*unvisitedIndices.size()));
    if(this.root.getBoard().getPrint().equals("verbose"))System.out.println("Node Added\n");
    selected.getChildren()[selectedIndex] = new Node(selected, selected.getBoard().getNextState(selectedIndex));
    return selected.getChildren()[selectedIndex];
  } 

  // returns result of simulation
  private int simulate(Node expand) {
    //copy of board
    Board simBoard = expand.getBoard().copy();
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

  private void backpropagate(Node expand, double simResult) {
    Node curr = expand;
    while(curr != null) {
      //update visits
      if(this.root.getBoard().getPrint().equals("verbose")){
        System.out.println("Updated values:");
        System.out.println("wi: " + curr.incrPlayerWins(simResult)); 
        System.out.println("ni: " + curr.incrVisits()+"\n"); 
      }else{
        curr.incrPlayerWins(simResult);
        curr.incrVisits();
      }
      //update score
      //go back to prev parent node
      curr = curr.getParent();
    }
  }

}