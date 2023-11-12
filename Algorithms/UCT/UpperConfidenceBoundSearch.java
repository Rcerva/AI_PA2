package Algorithms.UCT;

import java.util.ArrayList;

import Algorithms.Algorithm;
import Board.Board;

public class UpperConfidenceBoundSearch extends Algorithm {
  
  private UCTNode root; // starting state
  private final int col;
  private static final double EXPLORATION_PARAMETER = Math.sqrt(2);
  private long givenTime;
  
  public UpperConfidenceBoundSearch(Board board, long givenTime) {
    this.col = board.col;
    this.givenTime = givenTime;
    this.root = new UCTNode(null, board.copy());
  }

  // sets root to new board state given move
  public void update(int move) {
        this.root = this.root.children[move] != null 
        ? this.root.children[move] 
        : new UCTNode(null, this.root.board.getNextState(move));
  }

  // returns the optimal move for the current player
  public int getOptimalMove() {
    for (long stop = System.nanoTime()+givenTime; stop>System.nanoTime();) {
      UCTNode selectedNode = select();
      if(selectedNode == null)
        continue;
      UCTNode expandedNode = expand(selectedNode);
      double result = simulate(expandedNode);
      backpropagate(expandedNode, result);
    }

    int maxIndex = -1;
    for(int i = 0; i < col; i++) {
      if(this.root.children[i] != null) {
        if(maxIndex == -1 || this.root.children[i].visits > this.root.children[maxIndex].visits)
          maxIndex = i;
        // System.out.printf("\nlocation%d: p1wins: %f/%d = %f", i, root.children[i].playerYellowWins, root.children[i].visits, root.children[i].playerYellowWins/root.children[i].visits);
      }
    }
    return maxIndex;
  }

  private UCTNode select() {
    return select(this.root);
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
        ? currentChild.playerYellowWins 
        : (currentChild.visits-currentChild.playerYellowWins);
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
  private double simulate(UCTNode expandedNode) {
    Board simulationBoard = expandedNode.board.copy();
    while(simulationBoard.currentGameState() == Board.ONGOING) {
      simulationBoard.place((int)(Math.random()*col));
    }
      // System.out.println(simulationBoard);

    switch(simulationBoard.currentGameState()) {
      case Board.PLAYER_YELLOW_WON:
        return 1;
      case Board.PLAYER_RED_WON:
        return 0;
      default:
        return 0.5;
    }
  }

  private void backpropagate(UCTNode expandedNode, double simulationResult) {
    UCTNode currentNode = expandedNode;
    while(currentNode != null) {
      currentNode.incrementVisits();
      currentNode.incrementPlayerYellowWins(simulationResult);
      currentNode = currentNode.parent;
    }
  }
  

  private class UCTNode {
    private UCTNode parent;
    private UCTNode[] children;
    private int visits;
    private double playerYellowWins;
    private final Board board;
    public UCTNode(UCTNode parent, Board board) {
      this.parent = parent;
      this.board = board;
      this.visits = 0;
      this.playerYellowWins = 0;
      children = new UCTNode[col];
    }

    public int incrementVisits() {
      return ++visits;
    }
    public double incrementPlayerYellowWins(double result) {
      playerYellowWins += result;
      return playerYellowWins;
    }
  }
}
