package Algorithms.UCT;

import java.util.ArrayList;
import Board.Board;
import Algorithms.Algorithm;
import Node.Node;

public class UpperConfidenceBoundSearch extends Algorithm {
  private Node root;
  private final int col;
  private double C;
  private long time;

  public UpperConfidenceBoundSearch(Board board, long time, int param) {
    this.col = board.col;
    this.C = param;
    this.time = time;
    this.root = new Node(null, board.copy());
  }

  public void updateRoot(int move) {
    if (this.root.getChildren()[move] != null) this.root = this.root.getChildren()[move]; 
    else this.root = new Node(null, this.root.getBoard().getNextState(move));
  }

  public int getOptimalMove() {
    for (long stop = System.nanoTime() + time; stop > System.nanoTime();) {
      Node selected = select(this.root);
      if (selected == null) continue;
      Node expand = expand(selected);
      int result = simulate(expand);
      printVerboseInfo(result);
      backpropagate(expand, result);
    }

    return findMaxVisitIndex();
  }

  private Node select(Node parent) {
    for (int i = 0; i < col; i++) {
      if (parent.getChildren()[i] == null && parent.getBoard().canPlace(i)) {
          return parent;
      }
    }
    return selectByUCT(parent);
  }

  private Node selectByUCT(Node parent) {
    double maxSelectionVal = -1;
    int maxIndex = -1;

    for (int i = 0; i < col; i++) {
      if (!parent.getBoard().canPlace(i)) continue;

      Node curr = parent.getChildren()[i];
      double wins = (parent.getBoard().getNextTurn() == Board.PLAYER_YELLOW_TURN) ? curr.getPlayerWins() : (curr.getVisits() - curr.getPlayerWins());

      if (this.root.getBoard().getPrint().equals("verbose")) {
        System.out.println("\nwi: " + wins);
        System.out.println("ni: " + curr.getVisits());
      }

      double selectionVal = wins / curr.getVisits() + C * Math.sqrt(Math.log(parent.getVisits()) / curr.getVisits()); // UCT

      if (selectionVal > maxSelectionVal) {
        maxSelectionVal = selectionVal;
        maxIndex = i;
      }
    }

    return (maxIndex == -1) ? null : select(parent.getChildren()[maxIndex]);
  }

  private Node expand(Node selected) {
    ArrayList<Integer> unvisitedIndices = getUnvisitedIndices(selected);
    int selectedIndex = unvisitedIndices.get((int) (Math.random() * unvisitedIndices.size()));

    if (this.root.getBoard().getPrint().equals("verbose")) System.out.println("Node Added\n");

    Node[] children = selected.getChildren();
    children[selectedIndex] = new Node(selected, selected.getBoard().getNextState(selectedIndex));
    return children[selectedIndex];
  }

  private ArrayList<Integer> getUnvisitedIndices(Node selected) {
    ArrayList<Integer> unvisitedIndices = new ArrayList<>();

    for (int i = 0; i < col; i++) {
      if (selected.getChildren()[i] == null && selected.getBoard().canPlace(i)) unvisitedIndices.add(i);
    }

    return unvisitedIndices;
  }

  private int simulate(Node expand) {
    Board sim = expand.getBoard().copy();

    while (sim.currentGameState() == Board.ONGOING) {
      sim.place((int) (Math.random() * col));
    }

    switch (sim.currentGameState()) {
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

    while (curr != null) {
      if (this.root.getBoard().getPrint().equals("verbose")) {
        System.out.println("Updated values:");
        System.out.println("wi: " + curr.incrPlayerWins(simResult));
        System.out.println("ni: " + curr.incrVisits() + "\n");
      } else {
        curr.incrPlayerWins(simResult);
        curr.incrVisits();
      }

      curr = curr.getParent();
    }
  }

  private void printVerboseInfo(int result) {
    if (this.root.getBoard().getPrint().equals("verbose")) System.out.println("TERMINAL NODE VALUE: " + result + "\n");
  }

  private int findMaxVisitIndex() {
    int maxIndex = -1;

    for (int i = 0; i < col; i++) {
      if (this.root.getChildren()[i] != null) {
        if (maxIndex == -1 || this.root.getChildren()[i].getVisits() > this.root.getChildren()[maxIndex].getVisits()) maxIndex = i;
      }
    }

    if (this.root.getBoard().getPrint().equals("verbose")) printVerboseFinalInfo(maxIndex);

    return maxIndex;
  }

  private void printVerboseFinalInfo(int maxIndex) {
    for (int i = 0; i < 7; i++) {
      Node curr = this.root.getChildren()[i];
      if (curr != null) {
        double averageScore = curr.getPlayerWins() / curr.getVisits();
        System.out.println("Column " + (i + 1) + ": " + (curr.getVisits() > 0 ? averageScore : "Null"));
      }
    }

    System.out.println("Final Move Selected: " + maxIndex);
  }
}