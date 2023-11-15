package Algorithms.DLMM;

import Algorithms.Algorithm;
import Board.Board;

public class DLMM extends Algorithm{
	private final static int totalRows=5;
    private int[] occupied;
    private String algorithmName;
    private Board board;
    public DLMM(String algorithmName, int[] occupied, Board board) {
        this.algorithmName=algorithmName;
        this.occupied = occupied;
        this.board=board;
    }

    public int getOptimalMove() {
        Character[][] grid = board.getGrid(); // Use the appropriate method to get the grid
        // You might need to adjust the parameters here based on your use case
        return makeMinMaxMove(grid,occupied,board.getTeam(),5);
    }

    @Override
    public void updateRoot(int moveColumn) {
        updateOccupied(moveColumn);
    }
	public static int makeMinMaxMove(Character[][] grid, int[] occupied, char currentPlayer, int depth) {
	    double maxScore = Double.NEGATIVE_INFINITY;
	    int bestMove = -1;

	    // Check if all columns are empty
	    boolean allZeros = true;
	    for (int i : occupied) {
	        if (i != 0) {
	            allZeros = false;
	            break;
	        }
	    }
	 // Check if all columns are empty, and if true, set the final move to the 5th column
	    if (allZeros) {
	        bestMove = 5;
	        System.out.println("All columns: O.O");
	        return bestMove;
	    }
	    for (int col = 0; col < 7; col++) {
	        if (occupied[col] < depth) {
	            // Simulate making a move
	            int row = totalRows - occupied[col];
	            grid[row][col] = currentPlayer;
	            occupied[col]++;

	            // Call MinMax with the specified depth
	            double score = minMax(grid, occupied, 1, depth - 1, currentPlayer!='Y', currentPlayer);
	            // Print the score for each column
	            System.out.println("Column " + (col + 1) + ": " + score);

	            // Undo the move
	            grid[row][col] = 'O';
	            occupied[col]--; 

	            // Update the best move if a better score is found
	            if (score > maxScore) {
	                maxScore = score;
	                bestMove = col + 1; 
	            }
	        } else {
	            // Column is full, so the move is not valid
	            System.out.println("Column " + (col + 1) + ": Null");
	        }
	    }

	    

	    return bestMove;
	}


	private static double minMax(Character[][] grid, int[] occupied, int depth, int maxDepth, boolean maximizingPlayer, char currentPlayer) {
	    // Check for terminal state
	    if (isTerminalState(grid, occupied)) {
	        return evaluateState(grid, occupied);
	    }
	    // Base case: check if the current depth limit is reached
	    if (depth == maxDepth) {
	        return evaluateState(grid, occupied);
	    }

	    double minres = Double.POSITIVE_INFINITY;
	    double maxres = Double.NEGATIVE_INFINITY;

	    if (maximizingPlayer) {
	        for (int col = 0; col < 7; col++) {
	            if (occupied[col] < 6) {
	                int row = totalRows - occupied[col];
	                grid[row][col] = currentPlayer;
	                occupied[col]++;
	                double eval = minMax(grid, occupied, depth + 1, maxDepth, false, currentPlayer);
	                maxres = Math.max(maxres, eval); // Update maxres directly
	                grid[row][col] = 'O'; // Undo the move
	                occupied[col]--;
	            }
	        }
	    } else {
	        for (int col = 0; col < 7; col++) {
	            if (occupied[col] < 6) {
	                int row = totalRows - occupied[col];
	                grid[row][col] = getOpponentSymbol(currentPlayer);
	                occupied[col]++;
	                double eval = minMax(grid, occupied, depth + 1, maxDepth, true, currentPlayer);
	                minres = Math.min(minres, eval); // Update minres directly
	                grid[row][col] = 'O'; // Undo the move
	                occupied[col]--;
	            }
	        }
	    }

	    return maximizingPlayer ? maxres : minres;
	}

	private static boolean isTerminalState(Character[][] grid, int[] occupied) {
	    // Check for a win
	    if (checkForWin(grid)) {
	        return true;
	    }

	    // Check for a draw
	    for (int col = 0; col < 7; col++) {
	        if (occupied[col] < 6) {
	            return false; // If there is an available move, the game is not a draw
	        }
	    }
	    return true; // If all columns are full and there is no win, it's a draw
	}

    protected static boolean checkForWin(Character[][] grid) {
        // Check for a win in rows
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 4; col++) {
                if (checkLine(grid[row][col], grid[row][col + 1], grid[row][col + 2], grid[row][col + 3])) {
                    return true;
                }
            }
        }
        // Check for a win in columns
        for (int col = 0; col < 7; col++) {
            for (int row = 0; row < 3; row++) {
                if (checkLine(grid[row][col], grid[row + 1][col], grid[row + 2][col], grid[row + 3][col])) {
                    return true;
                }
            }
        }

        // Check for a win in diagonals (left to right)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                if (checkLine(grid[row][col], grid[row + 1][col + 1], grid[row + 2][col + 2], grid[row + 3][col + 3])) {
                    return true;
                }
            }
        }

        // Check for a win in diagonals (right to left)
        for (int row = 0; row < 3; row++) {
            for (int col = 3; col < 7; col++) {
                if (checkLine(grid[row][col], grid[row + 1][col - 1], grid[row + 2][col - 2], grid[row + 3][col - 3])) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean checkLine(char a, char b, char c, char d) {
        return a != 'O' && a == b && b == c && c == d;
    }

    // private static void printBoard(Character[][] grid) {
    //     for (int row = 0; row < 6; row++) {
    //         for (int col = 0; col < 7; col++) {
    //             System.out.print(grid[row][col] + " ");
    //         }
    //         System.out.println();
    //     }
    //     System.out.println();
    // }
    private static double evaluateState(Character[][] grid, int[] occupied) {
        double score = 0.0;

        // Evaluate based on the number of pieces in each line
        score += evaluateLines(grid, 'Y'); 
        score -= evaluateLines(grid, 'R'); 
        return score;
    }
    private static double evaluateLines(Character[][] grid, char player) {
        double score = 0.0;
        // Evaluate horizontal lines
        score += evaluateLine(grid, player, 0, 1);
        
        // Evaluate vertical lines
        score += evaluateLine(grid, player, 1, 0);

        // Evaluate diagonal (right-down) lines
        score += evaluateLine(grid, player, 1, 1);

        // Evaluate diagonal (left-down) lines
        score += evaluateLine(grid, player, 1, -1);

        return score;
    }

    private static double evaluateLine(Character[][] grid, char player, int deltaRow, int deltaCol) {
        double score = 0.0;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                // Evaluate each cell in the specified direction
            	double cellScore = evaluateCell(grid, player, getOpponentSymbol(player), row, col, deltaRow, deltaCol);
                score += cellScore;
            }
        }

        return score;
    }

    private static double evaluateCell(Character[][] grid, char player, char opponent, int startRow, int startCol, int deltaRow, int deltaCol) {
        int countPlayer = 0;
        int countOpponent = 0;
        int countEmpty = 0;

        for (int i = 0; i < 4; i++) {
            int currentRow = startRow + i * deltaRow;
            int currentCol = startCol + i * deltaCol;

            if (currentRow >= 0 && currentRow < grid.length && currentCol >= 0 && currentCol < grid[0].length) {
                if (grid[currentRow][currentCol] == player) {
                    countPlayer++;
                } else if (grid[currentRow][currentCol] == opponent) {
                    countOpponent++;
                } else if (grid[currentRow][currentCol] == 'O') {
                    countEmpty++;
                }
            }
        }
        return evaluateCount(countPlayer, countEmpty, countOpponent);
    }
    private static double evaluateCount(int countPlayer, int countEmpty, int countOpponent) {
        // Your scoring logic goes here
        if (countPlayer == 4) {
            return 1.0; // Winning move
        } else if (countPlayer == 3 && countEmpty == 1) {
            return 0.5; // Three in a row with one empty space
        } else if (countPlayer == 2 && countEmpty == 2) {
            return 0.0; // Two in a row with two empty spaces
        } else if (countPlayer == 1 && countEmpty == 3) {
            return -0.5; // One in a row with three empty spaces
        }

        // Consider opponent's threats and penalize them
        if (countOpponent == 4) {
            return -1.0; // Opponent's winning move
        } else if (countOpponent == 3 && countEmpty == 1) {
            return -0.5; // Opponent has three in a row with one empty space
        } else if (countOpponent == 2 && countEmpty == 2) {
            return 0.0; // Opponent has two in a row with two empty spaces
        } else if (countOpponent == 1 && countEmpty == 3) {
            return 0.5; // Opponent has one in a row with three empty spaces
        }

        return 0.0;
    }

    private static char getOpponentSymbol(char currentPlayerSymbol) {
        return (currentPlayerSymbol == 'R') ? 'Y' : 'R';
    }
    private void updateOccupied(int moveColumn) {
        // Add logic to update the occupied array based on the moveColumn
        if (moveColumn >= 0 && moveColumn < 7) {
            occupied[moveColumn]++;
        } else {
            throw new IllegalArgumentException("Invalid moveColumn: " + moveColumn);
        }
    }
}