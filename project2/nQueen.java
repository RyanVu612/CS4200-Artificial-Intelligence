package project2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class nQueen {
    // Directional Enum
    enum Direction {
        UP(-1, 0),
        DOWN(1, 0),
        LEFT(0, -1),
        RIGHT(0, 1),
        UPLEFT(-1,-1),
        UPRIGHT(-1, 1),
        DOWNLEFT(1, -1),
        DOWNRIGHT(1, 1);

        int dr, dc;

        Direction(int dr, int dc) {
            this.dr = dr;
            this.dc = dc;
        }
    }
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

        List<int[]> next = steepestAscentStep(queens, board);
        while (next != null) {
            System.out.println("loop");
            queens = copyList(next);
            board = generateBoard(queens, n);
            next = steepestAscentStep(queens, board);
        }

        printBoard(board);
        if (countAttacks(queens) <= 0) {
            System.out.println("Board solved");
            System.out.println("Number of attacks is: " + countAttacks(queens));
        } else {
            System.out.println("Board not solved");
            System.out.println("Number of attacks is: " + countAttacks(queens));
        }
    }

    // return array of [queen index, row, col, attackCount]
    public static List<int[]> steepestAscentStep(List<int[]> queens, int[][] board) {
        List<int[]> next = null;
        int bestAttackCount = countAttacks(queens);

        for (int i = 0; i < queens.size(); i++) {
            int[] currentQueen = queens.get(i);
            int originalRow = currentQueen[0];
            int orignialCol = currentQueen[1];
            // need to check up, up-right, right, down-right, down, down-left, left, up-left
            for (Direction d : Direction.values()) {
                int newRow = originalRow + d.dr;
                int newCol = orignialCol + d.dc;
                while (newRow >= 0 && newRow < queens.size() && 
                       newCol >= 0 && newCol < queens.size() &&
                       board[newRow][newCol] != 1) {

                    List<int[]> currentQueensList = copyList(queens);
                    currentQueensList.set(i, new int[]{newRow, newCol});

                    int currentAttackCount = countAttacks(currentQueensList);

                    if (currentAttackCount < bestAttackCount) {
                        bestAttackCount = currentAttackCount;
                        next = copyList(currentQueensList);
                    }

                    newRow += d.dr;
                    newCol += d.dc;
                }    
            }
        }

        return next;
    }

    public static List<int[]> copyList(List<int[]> list) {
        List<int[]> copy = new ArrayList<>();

        for (int[] queen : list) {
            copy.add(new int[]{queen[0], queen[1]});
        }

        return copy;
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

    public static int[][] generateBoard(List<int[]> queens, int n) {
        int[][] board = new int[n][n];
        for (int[] queen : queens) {
            board[queen[0]][queen[1]] = 1;
        }

        return board;
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
