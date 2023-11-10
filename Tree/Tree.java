package Tree;
import Nodes.Node;

/*
5 7
1 2
4 3
2 4 2 1 4 5 2
0 1 2 3 5 3 1
2 0 4 4 1 2 4
2 5 5 3 2 0 1
4 3 3 2 1 0 1
*/

/*
 * [[2,4,2,1,4,5,2],
 *  [0,1,2,3,5,3,1],
 *  [2,0,4,4,1,2,4],
 *  [2,5,5,3,2,0,1],
 *  [4,3,3,2,1,0,1]]
 * 
 * 
 */
//row == y
//col == x

public class Tree {
  private Node root;
  private Node goal;

  public void setRoot(Node root){
    this.root = root;
  }

  public Node getRoot(){
    return this.root;
  }

  public void generateTree(int arr[][], Node start, Node end){
    generateEachNode(arr, start, end);
  }

  public void setGoal(Node goal){
    this.goal = goal;
  }

  public Node getGoal(){
    return this.goal;
  }

  //iterate through array of nodes and generate connections
  private void generateConnections(Node arr[][], Node curr, Node end){
    for(int row = 0; row < arr.length; row++){
      for(int col = 0; col < arr[row].length; col++){
        Node currNode = arr[row][col];

        //check left of curr location
        if(col-1 >= 0 && currNode != null) currNode.setLeftNode(arr[row][col-1]);
        //check right of curr location
        if(col+1 < arr[row].length && currNode != null) currNode.setRightNode(arr[row][col+1]);
        //check up of curr location
        if(row-1 >= 0 && currNode != null) currNode.setUpNode(arr[row-1][col]);
        //check down of curr location
        if(row+1 < arr.length && currNode != null) currNode.setDownNode(arr[row+1][col]);
      }
    }
    // printNodeArray(arr);
  }

  //Used to print array contaning heuristic. FOR TESTING
  private void printNodeArray(Node arr[][]){
    System.out.println("NodeArray Content:");
    System.out.println();
    System.out.println("[ ");
    for(int row = 0; row < arr.length; row++){
      System.out.print("[ ");
      for(int col = 0; col < arr[row].length; col++){
        Node currNode = arr[row][col];
        if(currNode != null)System.out.print(currNode.getHeuristic() + ", ");
        else System.out.print(0 + ", ");
      }
      System.out.print(" ],");
      System.out.println();
    }
    System.out.print(" ]");
    System.out.println();
  }

  //Used to print array contaning Nodes, specifically their heuristic value. FOR TESTING
  private void printIntArray(int arr[][]){
    System.out.println("IntArray Content:");
    System.out.println();
    System.out.println("[ ");
    for(int row = 0; row < arr.length; row++){
      System.out.print("[ ");
      for(int col = 0; col < arr[row].length; col++){
        int currInt = arr[row][col];
        System.out.print(currInt  + ", ");
      }
      System.out.print(" ],");
      System.out.println();
    }
    System.out.print(" ]");
    System.out.println();

  }

  // Iterate through number array and create corresponding Node array
  private void generateEachNode(int arr[][], Node start, Node end){
    int startX = start.getCol();
    int startY = start.getRow();

    int endX = end.getCol();
    int endY = end.getRow();

    //new node array to hold corresponding node
    Node[][] nodeArray = new Node [arr.length][arr[0].length];

    //iterate and create new node for every heurstic
    for(int row = 0; row < arr.length; row++){
      for(int col = 0; col < arr[row].length; col++){
        if(row+1 == startY && col+1 == startX){
          //If coordinates are the starting, assign the start node
          this.setRoot(start);
          nodeArray[row][col] = start;
        }else if(row+1 == endY && col+1 == endX){ 
          //else If coordinates are the goal coordinates, assign the end node
          end.setHeuristic(arr[row][col]);
          this.setGoal(end);
          nodeArray[row][col] = end;
        }else if(arr[row][col] != 0){
          //else if heursitic not 0, create new node for array
           nodeArray[row][col] = new Node(row+1, col+1, arr[row][col]);
        }else nodeArray[row][col] = null;
      }
    }
    // printIntArray(arr);
    generateConnections(nodeArray, start, end);
  }
}
