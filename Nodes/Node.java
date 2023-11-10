package Nodes;

public class Node{
private int row;
private int col;
private int heuristic;
private int dist = 0;
private Node prev = null;
private Node next = null;

private Node up;
private Node down;
private Node left;
private Node right;


public Node(int row, int col, int heuristic) {
  this.row = row;
  this.col = col;
  this.heuristic = heuristic;
}

public Node(int heuristic) {
  this.heuristic = heuristic;
}

public void printCoordinate(){
  System.out.print("(" + this.row + ", " + this.col + ")");
}

public int getRow(){
  return this.row;
}

public int getCol(){
  return this.col;
}

public int getDist(){
  return this.dist;
}

public void setDist(int dist){
  this.dist = dist + this.heuristic;
}

public Node getPrev(){
  return this.prev;
}

public void setPrev(Node prev){
  this.prev = prev;
}

public Node getNext(){
  return this.next;
}

public void setNext(Node next){
  this.next = next;
}

public int getHeuristic(){
  return this.heuristic;
}

public int setHeuristic(int heuristic){
  return this.heuristic = heuristic;
}

public void setUpNode(Node node) {
  this.up = node;
}

public void setDownNode(Node node) {
  this.down = node;
}

public void setLeftNode(Node node) {
  this.left = node;
}

public void setRightNode(Node node) {
  this.right = node;
}

public Node getUpNode() {
  return this.up;
}

public Node getDownNode() {
  return this.down;
}

public Node getLeftNode() {
  return this.left;
}

public Node getRightNode() {
  return this.right;
}

public void printPathToNode(){
	Node curr = connectPrevToCurr();;
	System.out.print("Path: ( ");
	while(curr != null){
		System.out.print("(" + curr.getRow() + ", " + curr.getCol() + ") ");
		curr = curr.getNext();
	}
	System.out.println(")");
}

private Node connectPrevToCurr(){
	Node curr = this;
	Node prev = this;
	while(curr != null){
		prev = curr;
		curr = curr.getPrev();
		if(curr != null) curr.setNext(prev);
	}
	return prev;
}

}