package project2;

import java.util.Scanner;

public class nQueen {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter number of queens: ");
        int n = scanner.nextInt();
        boolean[][] board = new boolean[n][n];

        // Randomize queen positions
        int i = 0;
        while (i < n){
            int row = (int) (Math.random() * (n));
            int col = (int) (Math.random() * (n));

            if (!board[row][col]) {
                board[row][col] = true;
                i++;
            } 
        }  

        printBoard(board);
    }

    public static void printBoard(boolean[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                System.out.print(board[i][j] + "\t");
            }
            System.out.println();
        }
    }
}
