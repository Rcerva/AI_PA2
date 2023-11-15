package UR_DLMM;
public class Main {
    public static void main(String[] args) {
        
        Filereader filereader = new Filereader("/Users/carlo/Downloads/PA2/AI_PA2/UR_DLMM/game2.txt");

        // Print the initial board
        System.out.println("Initial Board: ");
        filereader.printBoard();
        // Check if the current state is terminal
        if (!filereader.isTerminal()) {
        	if ("DLMM".equals(filereader.getAlgorithm())) {
                int res=filereader.makeMinMaxMove(); 
            	System.out.println("Final move selected:"+ res );
            } else if ("UR".equals(filereader.getAlgorithm())) {
            	 // Make a UR move
                int selectedColumn = filereader.makeURMove();  
                System.out.println("FINAL Move selected: " + selectedColumn);
                }
           

            
        }
    }
}