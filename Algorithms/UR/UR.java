package Algorithms.UR;

import Algorithms.Algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UR extends Algorithm {
    private String currentPlayer;
    private int[] occupied;
    public UR(String currentPlayer, int[] occupied) {
        this.currentPlayer = currentPlayer;
        this.occupied = occupied;
    }

    @Override
    public int getOptimalMove() {
        return makeURMove();
    }

    @Override
    public void update(int moveColumn) {
        updateOccupied(moveColumn);
    }

    private int makeURMove() {
        List<Integer> availableColumns = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            if (occupied[i] < 6) {
                availableColumns.add(i);
            }
        }

        if (availableColumns.isEmpty()) {
            throw new IllegalStateException("No available columns to make a move.");
        }

        Random random = new Random();
        int randomIndex = random.nextInt(availableColumns.size());
        int selectedColumn = availableColumns.get(randomIndex);
        return selectedColumn;
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
