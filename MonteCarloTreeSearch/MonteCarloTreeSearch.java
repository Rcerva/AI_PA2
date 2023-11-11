// Monte Carlo Tree Search for Connect 4

import java.util.ArrayList;
import Board.Board;

public class MonteCarloTreeSearch {
  
  private MCTSNode root; // starting state
  private final int col;
  private static final double EXPLORATION_PARAMETER = Math.sqrt(2);
  private long givenTime;
  
  public MonteCarloTreeSearch(Board board, long givenTime) {
    this.col = board.col;
    this.givenTime = givenTime;
    this.root = new MCTSNode(null, board.copy());
  }

  // sets root to new board state given move
  public void update(int move) {
        this.root = this.root.children[move] != null 
        ? this.root.children[move] 
        : new MCTSNode(null, this.root.board.getNextState(move));
  }

  // returns the optimal move for the current player
  public int getOptimalMove() {
    for (long stop = System.nanoTime()+givenTime; stop>System.nanoTime();) {
      MCTSNode selectedNode = select();
      if(selectedNode == null)
        continue;
      MCTSNode expandedNode = expand(selectedNode);
      double result = simulate(expandedNode);
      backpropagate(expandedNode, result);
    }

    int maxIndex = -1;
    for(int i = 0; i < col; i++) {
      if(this.root.children[i] != null) {
        if(maxIndex == -1 || this.root.children[i].visits > this.root.children[maxIndex].visits)
          maxIndex = i;
        // System.out.printf("\nlocation%d: p1wins: %f/%d = %f", i, root.children[i].player1Wins, root.children[i].visits, root.children[i].player1Wins/root.children[i].visits);
      }
    }
    return maxIndex;
  }

  private MCTSNode select() {
    return select(this.root);
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
      double wins = parent.board.getNextTurn() == Board.PLAYER_YELLOW_TURN 
        ? currentChild.player1Wins 
        : (currentChild.visits-currentChild.player1Wins);
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
    selectedNode.children[selectedIndex] = new MCTSNode(selectedNode, selectedNode.board.getNextState(selectedIndex));
    return selectedNode.children[selectedIndex];
  } 

  // returns result of simulation
  private double simulate(MCTSNode expandedNode) {
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

  private void backpropagate(MCTSNode expandedNode, double simulationResult) {
    MCTSNode currentNode = expandedNode;
    while(currentNode != null) {
      currentNode.incrementVisits();
      currentNode.incrementPlayer1Wins(simulationResult);
      currentNode = currentNode.parent;
    }
  }

  private class MCTSNode {
    private MCTSNode parent;
    // children[i] represents the next game state in which current player places disc at location i
    private MCTSNode[] children;
    private int visits;
    private double player1Wins;
    private final Board board;
    public MCTSNode(MCTSNode parent, Board board) {
      this.parent = parent;
      this.board = board;
      this.visits = 0;
      this.player1Wins = 0;
      children = new MCTSNode[col];
    }

    public int incrementVisits() {
      return ++visits;
    }
    public double incrementPlayer1Wins(double result) {
      player1Wins += result;
      return player1Wins;
    }
  }
}