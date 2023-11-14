package Board;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Board {
  public final int row;
  public final int col;
  private Character[][] board;
  private boolean nextTurn;
  private String algorithm;
  private int param;
  private Character team;

  // board contents
  public static final Character EMPTY_SLOT = 'O';
  public static final Character PLAYER_YELLOW_DISK = 'Y';
  public static final Character PLAYER_RED_DISK = 'R';

  // turns
  public static final boolean PLAYER_YELLOW_TURN = true;
  public static final boolean PLAYER_RED_TURN = false;

  // game state
  public static final int ONGOING = 2;
  public static final int PLAYER_YELLOW_WON = 1;
  public static final int PLAYER_RED_WON = -1;
  public static final int TIE = 0;

  public Board(int col, int row) {
    this.col = col;
    this.row = row;
    board = new Character[row][col]; // default all 0
    nextTurn = PLAYER_YELLOW_TURN;
  }

  public Board(String filename){
    this.col = 7;
    this.row = 6;
    board = new Character[row][col];
    fileIngestor(filename);
  }

  public Board(Character[][] contents, boolean nextTurn) {
    this(contents[0].length, contents.length);
    loadContents(contents);
    this.nextTurn = nextTurn;
  }

  //check if column allows to drop coin in
  public boolean canPlace(int column) {
    return column >= 0 && column < col && board[0][column] == 'O';
  }

  public boolean place(int column) {
    Character disk = (nextTurn == PLAYER_YELLOW_TURN) ? PLAYER_YELLOW_DISK : PLAYER_RED_DISK; //ternary to choose Y or R depending on turn
    //if can't place at column -> false
    if(!canPlace(column)) return false;

    int diskrow = row - 1;

    /*iterate over rows until empty in column until empty    
            |      col = 3 
    ->[Y,R,R,Y,O,R,R] row 5 !empty row--
      [Y,R,R,Y,O,R,R] row 4 !empty row--
      [Y,R,R,Y,O,R,R] row 3 !empty row--
      [Y,R,R,O,O,R,R] row 2 <- empty in row 2 so update postion here here
      [Y,R,R,O,O,R,R] row 1
      [Y,R,R,O,O,R,R] row 0
    */
    while(board[diskrow][column] != EMPTY_SLOT) diskrow--;
    board[diskrow][column] = disk; // replace O with R or Y
    nextTurn = !nextTurn; // Turn is over
    return true; // finishied placing
  }

  public Board getNextState(int column) {
    Board next = this.copy();
    next.place(column);
    return next;
  }

  private void loadContents(Character[][] contents) {
    for(int i = 0; i < row; i++)
      for(int j = 0; j < col; j++)
        this.board[i][j] = contents[i][j];
  }

  //make copy of board with constructor that gets the state of the board and the players turn
  public Board copy() {
    return new Board(board, this.nextTurn);
  }

  //check for hori, vert, and both diags | _ / \
  private boolean didPlayerWin(int playerDisk) {
    // check horizontal _
    int row = board.length;
    int col = board[0].length;
    for(int i = 0; i < row; i++)
      for(int j = 0; j < col - 3; j++)
        for(int k = j; k < j + 4 && board[i][k] == playerDisk; k++)
          if(k == j+3) return true;
    // check vertical |
    for(int i = 0; i < row - 3; i++)
      for(int j = 0; j < col; j++)
        for(int k = i; k < i + 4 && board[k][j] == playerDisk; k++)
          if(k == i+3) return true;
    // check diagonal down right \
    for(int i = 0; i < row - 3; i++)
      for(int j = 0; j < col - 3; j++)
        for(int k = 0; k < 4 && board[i+k][j+k] == playerDisk; k++)
          if(k == 3) return true;
    // check diagonal down /
    for(int i = 0; i < row - 3; i++)
      for(int j = 3; j < col; j++)
        for(int k = 0; k < 4 && board[i+k][j-k] == playerDisk; k++)
          if(k == 3) return true;

    return false;
  }

  //iterate through top of board, if top row is full == board is full
  private boolean isFull() {
    for(int j = 0; j < board[0].length; j++)
      if(board[0][j] == EMPTY_SLOT) return false;
    return true;
  }

  //get current state
  public int currentGameState() {
    //if player yellow won -> retun that yellow won
    //elseif player red won -> retun that red won
    //elseif the board is full, it is a draw
    //else it has to be an on going game
    return this.didPlayerWin(PLAYER_YELLOW_DISK) ? PLAYER_YELLOW_WON
      : this.didPlayerWin(PLAYER_RED_DISK) ? PLAYER_RED_WON
      : this.isFull() ? TIE
      : ONGOING;
  }

  //check if it is the alogorithms turn
  public boolean isTurn(){
    if(this.nextTurn == PLAYER_YELLOW_TURN && this.team == 'Y') return true;
    else if(this.nextTurn == PLAYER_RED_TURN && this.team == 'R') return true;
    return false;
  }

  //Getters / Setters

  public Character[][] getGrid(){
    return board;
  }
  public int getRow(){
    return row;
  }
  public int getCol(){
    return col;
  }
  public void switchTurn(){
    nextTurn=!nextTurn;
  }
  public boolean getNextTurn() {
    return nextTurn;
  }

  public String getAlgorithm(){
    return this.algorithm.toUpperCase();
  }

  public int getParameter(){
    return this.param;
  }

  public Character getTeam(){
    return this.team;
  }

  private void fileIngestor(String filename){
    try {
      BufferedReader br = new BufferedReader(new FileReader(filename));

      //Read first 3 lines for algorithm and param and color

      this.algorithm = br.readLine();
      this.param = Integer.parseInt(br.readLine());
      this.team = br.readLine().charAt(0);

      // Read the board grid
      for (int i = 0; i < 6; i++) {
        String row = br.readLine();
        for (int j = 0; j < 7; j++) this.board[i][j] = row.charAt(j);
      }

      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  //print(board) () -> {}
  @Override
  public String toString() {
    String result = "|-";
    for(int j = 0; j < col; j++) result += "--|-";
    result = result.substring(0, result.length()-1) + "\n";
    for(int i = 0; i < row; i++) {
      result += "| ";
      for(int j = 0; j < col; j++) result += (board[i][j] == EMPTY_SLOT ? " " : (board[i][j] == 'Y' ? "Y" : "R"))+" | ";
      result = result.substring(0, result.length()-1);
      result += "\n|-";
      for(int j = 0; j < col; j++) result += "--|-";
      result = result.substring(0, result.length()-1);
      result += "\n";
    }
    result+="  0   1   2   3   4   5   6  ";
    return result.substring(0, result.length()-1);
  }
}