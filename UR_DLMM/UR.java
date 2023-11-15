package UR_DLMM;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UR {
    public static int makeURMove(int[] occupied, String currentPlayer) {
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
}