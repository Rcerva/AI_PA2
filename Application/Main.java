package Application;
// import Nodes.Node;

// import java.util.List;

import Filereader.Filereader;

public class Main {
	public static void main(String[] args) {
        //Test Case 1:
        String fileName = "test1.txt"; 

        Filereader board = new Filereader(fileName);
        board.printBoard();
        

    }
        
}
