package Filereader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Filereader {
	private Character[][] grid = new Character[6][7];
	private int[] occupied = new int[7];
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
							if(grid[i][j] != 'O') occupied[j]++;
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
		System.out.println();
	}

	public void printOccupied() {
		for (int count : occupied) {
			System.out.print(count + " ");
		}
		System.out.println();
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

	public boolean isTerminal(){
		return (isWinner() || isDraw());
	}

	public boolean isWinner(){
		for(int col = 6; col >= 0; col--){
			for(int row = 5; row >= 0; row--){
				if(grid[row][col] == 'O') continue;
				//Check Column
				if(row > 3 && grid[row][col] == grid[row-1][col] && grid[row-1][col] == grid[row-2][col] && grid[row-2][col] == grid[row-3][col]) return true;
				//Check Row
				if(col > 3 && grid[row][col] == grid[row][col-1] && grid[row][col-1] == grid[row][col-2] && grid[row][col-2] == grid[row][col-3]) return true;
				//Check Diags left
				if(col > 3 && grid[row][col] == grid[row-1][col-1] && grid[row-1][col-1] == grid[row-2][col-2] && grid[row-2][col-2] == grid[row-3][col-3]) return true;
				//Check Diags right
				if(col <= 3 && grid[row][col] == grid[row-1][col+1] && grid[row-1][col+1] == grid[row-2][col+2] && grid[row-2][col+2] == grid[row-3][col+3]) return true;
			}
		}
		return false;
	}

	public String getWinner(){
		String res = "";
		for(int col = 6; col >= 0; col--){
			for(int row = 5; row >= 0; row--){
				if(grid[row][col] == 'O') continue;
				//Check Column
				if(row > 3 && grid[row][col] == grid[row-1][col] && grid[row-1][col] == grid[row-2][col] && grid[row-2][col] == grid[row-3][col]) return res += grid[row][col];
				//Check Row
				if(col > 3 && grid[row][col] == grid[row][col-1] && grid[row][col-1] == grid[row][col-2] && grid[row][col-2] == grid[row][col-3]) return res += grid[row][col];
				//Check Diags left
				if(col > 3 && grid[row][col] == grid[row-1][col-1] && grid[row-1][col-1] == grid[row-2][col-2] && grid[row-2][col-2] == grid[row-3][col-3]) return res += grid[row][col];
				//Check Diags right
				if(col <= 3 && grid[row][col] == grid[row-1][col+1] && grid[row-1][col+1] == grid[row-2][col+2] && grid[row-2][col+2] == grid[row-3][col+3]) return res += grid[row][col];
			}
		}
		return "FALSE";
	}

	public boolean isDraw(){
		int count = 0;
		for(int i = 0; i < 6; i++){
			if(occupied[i] == 6) count++;
		}
		if (count == 6) return true;
		// for(int col = 6; col >= 0; col--){
		// 	for(int row = 5; row-3 >= 0; row--){
		// 		if(grid[row][col] == 'O') continue;
		// 		//Check Column
		// 		if(grid[row][col] == grid[row-1][col] && grid[row-1][col] == grid[row-2][col] && grid[row-2][col] == grid[row-3][col]) return true;
		// 		//Check Row
		// 		if(col > 3 && grid[row][col] == grid[row][col-1] && grid[row][col-1] == grid[row][col-2] && grid[row][col-2] == grid[row][col-3]) return true;
		// 		//Check Diags left
		// 		if(col > 3 && grid[row][col] == grid[row-1][col-1] && grid[row-1][col-1] == grid[row-2][col-2] && grid[row-2][col-2] == grid[row-3][col-3]) return true;
		// 		//Check Diags right
		// 		if(col <= 3 && grid[row][col] == grid[row-1][col+1] && grid[row-1][col+1] == grid[row-2][col+2] && grid[row-2][col+2] == grid[row-3][col+3]) return true;
		// 	}
		// }
		return false;
	}
}
