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
    }

    public static boolean checkAttack(){

        return false;
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
