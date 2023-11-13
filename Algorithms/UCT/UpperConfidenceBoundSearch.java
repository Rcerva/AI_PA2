package Algorithms.UCT;

import java.util.ArrayList;
import Board.Board;
import Algorithms.Algorithm;

public class UpperConfidenceBoundSearch extends Algorithm {
  
  private UCTNode root; // starting state
  private final int col;
  private double EXPLORATION_PARAMETER;
  private long givenTime;
  
  public UpperConfidenceBoundSearch(Board board, long time, double param) {
    this.col = board.col;
    this.EXPLORATION_PARAMETER = param;
    this.givenTime = time;
    this.root = new UCTNode(null, board.copy());
  }

  // sets root to new board state given move
  public void updateRoot(int move) {
        this.root = this.root.children[move] != null 
        ? this.root.children[move] 
        : new UCTNode(null, this.root.board.getNextState(move));
  }

  // returns the optimal move for the current player
  public int getOptimalMove() {
    for (long stop = System.nanoTime()+givenTime; stop>System.nanoTime();) {
      UCTNode selectedNode = select(this.root);
      if(selectedNode == null)
        continue;
      UCTNode expandNode = expand(selectedNode);
      double result = simulate(expandNode);
      backpropagate(expandNode, result);
    }

    int maxIndex = -1;
    for(int i = 0; i < col; i++) {
      if(this.root.children[i] != null) {
        if(maxIndex == -1 || this.root.children[i].visits > this.root.children[maxIndex].visits)
          maxIndex = i;
      }
    }
    return maxIndex;
  }

  private UCTNode select(UCTNode parent) {
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
      UCTNode currentChild = parent.children[i];
      double wins = parent.board.getNextTurn() == Board.PLAYER_YELLOW_TURN 
        ? currentChild.playerWins 
        : (currentChild.visits-currentChild.playerWins);
      double selectionVal = wins/currentChild.visits 
        + EXPLORATION_PARAMETER*Math.sqrt(Math.log(parent.visits)/currentChild.visits);// UCT
      if(selectionVal > maxSelectionVal) {
        maxSelectionVal = selectionVal;
        maxIndex = i;
      }
    }
    // SOMETIMES -1???
    if(maxIndex == -1)
      return null;
    return select(parent.children[maxIndex]);
  }

  private UCTNode expand(UCTNode selectedNode) {
    // get unvisited child nodes
    ArrayList<Integer> unvisitedChildrenIndices = new ArrayList<Integer>(col);
    for(int i = 0; i < col; i++) {
      if(selectedNode.children[i] == null && selectedNode.board.canPlace(i)) {
        unvisitedChildrenIndices.add(i);
      }
    }

    // randomly select unvisited child and create node for it
    int selectedIndex = unvisitedChildrenIndices.get((int)(Math.random()*unvisitedChildrenIndices.size()));
    selectedNode.children[selectedIndex] = new UCTNode(selectedNode, selectedNode.board.getNextState(selectedIndex));
    return selectedNode.children[selectedIndex];
  } 

  // returns result of simulation
  private double simulate(UCTNode expandNode) {
    //copy of board
    Board simBoard = expandNode.board.copy();
    //keep simulating until terminal state
    while(simBoard.currentGameState() == Board.ONGOING) {
      int random = (int)(Math.random());
      simBoard.place(random*col);
    }

    switch(simBoard.currentGameState()) {
      case Board.PLAYER_YELLOW_WON:
        return 1;
      case Board.PLAYER_RED_WON:
        return 0;
      default:
        return 0.5;
    }
  }

  private void backpropagate(UCTNode expandNode, double simulationResult) {
    UCTNode currNode = expandNode;
    while(currNode != null) {
      //update visits
      currNode.incrVisits();
      //update score
      currNode.incrPlayerWins(simulationResult);
      //go back to prev parent node
      currNode = currNode.parent;
    }
  }

  private class UCTNode {
    private UCTNode parent;
    // children[i] represents the next game state in which current player places disc at location i
    private UCTNode[] children;
    private int visits;
    private double playerWins;
    private final Board board;
    public UCTNode(UCTNode parent, Board board) {
      this.parent = parent;
      this.board = board;
      this.visits = 0;
      this.playerWins = 0;
      children = new UCTNode[col];
    }

    public int incrVisits() {
      return ++visits;
    }
    public double incrPlayerWins(double result) {
      playerWins += result;
      return playerWins;
    }
  }
}