package Filereader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Filereader {
	    private Character[][] grid = new Character[6][7];
      private String algorithm;
      private String param;
			private String team;

	    public Filereader(String fileName) {
	        try {
	            BufferedReader br = new BufferedReader(new FileReader(fileName));

							//Read first 3 lines for algorithm and param and color
	            this.algorithm = br.readLine();
	            this.param = br.readLine();
							this.team = br.readLine();

	            // Read the board grid
	            for (int i = 0; i < 6; i++) {
	                String row = br.readLine();
	                for (int j = 0; j < 7; j++) {
	                    grid[i][j] = row.charAt(j);
	                }
	            }

	            br.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    public void printBoard() {
	        for (Character[] row : grid) {
	            for (Character value : row) {
	                System.out.print(value + " ");
	            }
	            System.out.println();
	        }
	    }

	    public String getAlgorithm() {
	        return algorithm;
	    }

	    public String getParameter() {
	        return param;
	    }

			public String getTeam() {
	        return team;
	    }

	    public Character[][] getGrid() {
	        return grid;
	    }
}
