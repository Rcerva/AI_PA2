package Node;

import Board.Board;

public class Node {
  private Node parent;
  // children[i] represents the next game state in which current player places disc at location i
  private Node[] children;
  private int visits;
  private double playerWins;
  private final Board board;


  public Node(Node parent, Board board) {
    this.parent = parent;
    this.board = board;
    this.visits = 0;
    this.playerWins = 0;
    children = new Node[7];
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
  public Board getBoard(){
    return this.board;
  }
  public Node[] getChildren(){
    return this.children;
  }
  public Node getParent(){
    return this.parent;
  }
}
