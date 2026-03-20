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

        System.out.println("Enter number of tests: ");
        int tests = scanner.nextInt();

        System.out.println("Select algorithm:\n[1] Steepest Ascent\n[2] Mutation");
        int algorithm = scanner.nextInt();

        if (tests <= 1) {
            // Randomize queen positions
            queens = randomQueens(n); 
            board = generateBoard(queens, n);

            System.out.println("Starting board: ");
            printBoard(board);
            System.out.println("Number of attacks: " + countAttacksTotal(queens));

            List<int[]> next = null;
            if (algorithm == 1) {
                next = steepestAscentStep(queens, board);
            } else {
                // min-conflicts
            }
            
            if (algorithm == 1) {
                while (next != null) {
                    queens = copyList(next);
                    board = generateBoard(queens, n);
                    next = steepestAscentStep(queens, board);

                    System.out.println();
                    printBoard(board);
                    System.out.println("Number of attacks: " + countAttacksTotal(queens));
                }
            } else {

            }

            printBoard(board);
            if (countAttacksTotal(queens) <= 0) {
                System.out.println("Board solved");
                System.out.println("Number of attacks is: " + countAttacksTotal(queens));
            } else {
                System.out.println("Board not solved");
                System.out.println("Number of attacks is: " + countAttacksTotal(queens));
            }
        } else {
            double complete = 0.0;

            for (int i = 0; i < tests; i++) {
                // Create new board and randomize queens
                queens = randomQueens(n); 
                board = generateBoard(queens, n);

                List<int[]> next = null;
                if (algorithm == 1) {
                    next = steepestAscentStep(queens, board);
                } else {
                    // min-conflicts
                }

                while (next != null) {
                    queens = copyList(next);
                    board = generateBoard(queens, n);
                    if (algorithm == 1) {
                        next = steepestAscentStep(queens, board);
                    } else {
                        //min-conflicts
                    }
                }

                if (countAttacksTotal(queens) == 0) {
                    complete++;
                }  
            }

            System.out.println("Percentage of boards completed is: " + (complete / tests) * 100 + "%");
        }
    }

    public static List<int[]> steepestAscentStep(List<int[]> queens, int[][] board) {
        List<int[]> next = null;
        int bestAttackCount = countAttacksTotal(queens);

        for (int i = 0; i < queens.size(); i++) {
            int[] currentQueen = queens.get(i);
            int originalRow = currentQueen[0];
            int orignialCol = currentQueen[1];
            // need to check up, up-right, right, down-right, down, down-left, left, up-left
            for (Direction d : Direction.values()) {
                int newRow = originalRow + d.dr;
                int newCol = orignialCol + d.dc;
                while (newRow >= 0 && newRow < board.length && 
                       newCol >= 0 && newCol < board.length &&
                       board[newRow][newCol] != 1) {

                    List<int[]> currentQueensList = copyList(queens);
                    currentQueensList.set(i, new int[]{newRow, newCol});

                    int currentAttackCount = countAttacksTotal(currentQueensList);

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

    public static List<int[]> minConflictsStep(int n, int maxSteps) {
        // csp: {queens, board, attacks}
        List<int[]> queens = randomQueens(n);
        int[][] board = generateBoard(queens, n);

        List<List<int[]>> potentialBoards = new ArrayList<>();

        for (int i = 0; i < maxSteps; i++) {
            if (countAttacksTotal(queens) == 0) {
                return queens;
            }

            int index = 0;
            int attacks = 0;
            while (attacks == 0) {
                // pick a random queen
                index = (int) (Math.random() * n);
                attacks = countAttacksIndividual(queens, index);
            }

            int[] queen = queens.get(index);
            int originalRow = queen[0];
            int orignialCol = queen[1];

            // find conflicts and hold onto lowest ones
            for (Direction d : Direction.values()) {
                int newRow = originalRow + d.dr;
                int newCol = orignialCol + d.dc;
                while (newRow >= 0 && newRow < board.length && 
                       newCol >= 0 && newCol < board.length &&
                       board[newRow][newCol] != 1) {

                    List<int[]> currentQueensList = copyList(queens);
                    currentQueensList.set(index, new int[]{newRow, newCol});

                    int currentAttackCount = countAttacksIndividual(currentQueensList, index);

                    if (currentAttackCount < attacks) {
                        potentialBoards.clear();
                        attacks = currentAttackCount;
                        potentialBoards.add(copyList(currentQueensList));
                    } else if (currentAttackCount == attacks) {
                        potentialBoards.add(copyList(currentQueensList));
                    }

                    newRow += d.dr;
                    newCol += d.dc;
                }    
            }

            if (!potentialBoards.isEmpty()) {
                int tempIndex = (int) (Math.random() * potentialBoards.size());
                queens = copyList(potentialBoards.get(tempIndex));
                potentialBoards.clear();
            }
        }
        // return failure board
        return queens;
    }

    public static List<int[]> copyList(List<int[]> list) {
        List<int[]> copy = new ArrayList<>();

        for (int[] queen : list) {
            copy.add(new int[]{queen[0], queen[1]});
        }

        return copy;
    }

    public static int countAttacksIndividual(List<int[]> queens, int index) {
        int attacks = 0;
        int r1 = queens.get(index)[0];
        int c1 = queens.get(index)[1];

        for (int i = 0; i < queens.size(); i++) {
            if (i == index) continue;

            int r2 = queens.get(i)[0];
            int c2 = queens.get(i)[1];
            if (r1 == r2 || c1 == c2 || Math.abs(r1 - r2) == Math.abs(c1 - c2)) {
                attacks++;
            }
        }
        return attacks;
    }

    public static int countAttacksTotal(List<int[]> queens) {
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

    public static List<int[]> randomQueens(int n) {
        int i = 0;
        int[][] board = new int[n][n];
        List<int[]> queens = new ArrayList<>();
        while (i < n){
            int row = (int) (Math.random() * (n));
            int col = (int) (Math.random() * (n));

            if (board[row][col] == 0) {
                board[row][col] = 1;
                queens.add(new int[]{row, col});
                i++;
            } 
        }  
        return queens;
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
