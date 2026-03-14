package project2;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class nQueen {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter number of queens: ");
        int n = scanner.nextInt();
        int[][] board = new int[n][n];
        List<int[]> queens = new ArrayList<>();

        // Randomize queen positions
        int i = 0;
        while (i < n){
            int row = (int) (Math.random() * (n));
            int col = (int) (Math.random() * (n));

            if (board[row][col] == 0) {
                board[row][col] = 1;
                queens.add(new int[]{row, col});
                i++;
            } 
        }  

        printBoard(board);
        System.out.println("Number of attacks: " + countAttacks(queens));
    }

    public static int countAttacks(List<int[]> queens) {
        int attacks = 0;
        for (int i = 0; i < queens.size(); i++) {
            for (int j = i + 1; j < queens.size(); j++) {
                int r1 = queens.get(i)[0];
                int c1 = queens.get(i)[1];
                int r2 = queens.get(j)[0];
                int c2 = queens.get(j)[1];
                if (r1 == r2 || c1 == c2 || Math.abs(r1 - r2) == Math.abs(c1 - c2)) {
                    attacks++;
                }
            }
        }

        return attacks;
    }

    public static void printBoard(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }
}
