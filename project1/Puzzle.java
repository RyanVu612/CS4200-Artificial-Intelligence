package project1;

import java.util.ArrayList;
import java.util.Collections;

public class Puzzle {
    public static void main (String[] args) {
        int[][] board = generateBoard();
        printBoard(board);
    }  

    public static int[][] generateBoard (int[][] array) {
        return array;
    }

    public static int[][] generateBoard () {
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        for (int i=0; i<9; i++) numbers.add(i);
        Collections.shuffle(numbers);
        
        int[][] board = {
            {numbers.get(0), numbers.get(1), numbers.get(2)},
            {numbers.get(3), numbers.get(4), numbers.get(5)},
            {numbers.get(6), numbers.get(7), numbers.get(8)}
        };
            
        return board;
    }

    public static void printBoard(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.print("\n");
        }
    }
}