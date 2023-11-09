package Application;
import Filereader.Filereader;

public class Main {
	public static void main(String[] args) {
        //Test Case 1:
        String fileName = "test1.txt"; 
        Filereader board = new Filereader(fileName);

        System.out.println(board.getAlgorithm());
        System.out.println(board.getParameter());
        System.out.println(board.getTeam());
        board.printBoard();
    }
        
}
