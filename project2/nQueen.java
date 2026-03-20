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

        int steps = 0;

        System.out.print("Enter number of tests: ");
        int tests = scanner.nextInt();

        System.out.println("Select algorithm:\n[1] Steepest Ascent\n[2] Min-Conflict");
        int algorithm = scanner.nextInt();

        int max_steps = 0;
        if (algorithm == 2) {
            System.out.print("Enter max-steps: ");
            max_steps = scanner.nextInt();
        }

        scanner.close();

        if (tests <= 1) {
            // Randomize queen positions
            queens = randomQueens(n); 
            board = generateBoard(queens, n);

            System.out.println("Starting board: ");
            printBoard(board);
            System.out.println("Number of attacks: " + countAttacksTotal(queens));

            double startNs = System.nanoTime();
            List<int[]> next = null;
            if (algorithm == 1) {
                next = steepestAscentStep(queens, board);
            } else {
                next = minConflictsStep(queens, board);
            }
            
            int i = 0;
            while (next != null) {
                queens = copyList(next);
                board = generateBoard(queens, n);

                if (algorithm == 1) {
                    next = steepestAscentStep(queens, board);
                } else {
                    next = minConflictsStep(queens, board);
                }

                steps++;

                System.out.println();
                printBoard(board);
                System.out.println("Number of attacks: " + countAttacksTotal(queens));

                if (algorithm == 2) {
                    if (i > max_steps) break;
                    i++;
                }
            }
            double endNs = System.nanoTime();
            double runtimeMs = (endNs - startNs) / 1000000.0;

            System.out.println("========================");
            printBoard(board);

            if (algorithm == 2 && i > max_steps) {
                System.out.println("Max Steps reached");
            }

            if (countAttacksTotal(queens) <= 0) {
                System.out.println("Board solved");
            } else {
                System.out.println("Board not solved");
            }

            System.out.println("Number of attacks is: " + countAttacksTotal(queens));
            System.out.println("Total Steps is: " + steps);
            System.out.println("Runtime (ms): " + runtimeMs);
        } else {
            int complete = 0;
            double totalRuntimeMs = 0;

            for (int i = 0; i < tests; i++) {
                int counter = 0;
                // Create new board and randomize queens
                queens = randomQueens(n); 
                board = generateBoard(queens, n);

                double startNs = System.nanoTime();
                List<int[]> next = null;
                if (algorithm == 1) {
                    next = steepestAscentStep(queens, board);
                } else {
                    next = minConflictsStep(queens, board);
                }

                int j = 0;
                while (next != null) {
                    queens = copyList(next);
                    board = generateBoard(queens, n);
                    if (algorithm == 1) {
                        next = steepestAscentStep(queens, board);
                    } else {
                        next = minConflictsStep(queens, board);
                    }

                    counter++;

                    if (algorithm == 2) {
                        if (j > max_steps) break;
                        j++;
                    }
                }
                totalRuntimeMs += (System.nanoTime() - startNs) / 1000000.0;

                if (countAttacksTotal(queens) == 0) {
                    complete++;
                    steps += counter;
                }  
            }

            System.out.println("Percentage of boards completed is: " + ((double)complete / tests) * 100 + "%");
            System.out.println("Average number of steps is: " + ((double)steps / complete));
            System.out.println("Runtime (ns): " + totalRuntimeMs);
            System.out.println("Average runtime (ns): " + (totalRuntimeMs / (double) tests) + "ms");
        }
    }

    public static List<int[]> steepestAscentStep(List<int[]> queens, int[][] board) {
        List<int[]> next = null;
        int bestAttackCount = countAttacksTotal(queens);

        for (int i = 0; i < queens.size(); i++) {
            int[] currentQueen = queens.get(i);
            int originalRow = currentQueen[0];
            int orignialCol = currentQueen[1];
            // Check for every row it can be in within its column
            for (int newRow = 0; newRow < queens.size(); newRow++) {
                if (newRow == originalRow) continue;

                List<int[]> currentQueensList = copyList(queens);
                currentQueensList.set(i, new int[]{newRow, orignialCol});

                int currentAttackCount = countAttacksTotal(currentQueensList);

                if (currentAttackCount < bestAttackCount) {
                    bestAttackCount = currentAttackCount;
                    next = currentQueensList;
                }
            }
        }

        return next;
    }

    public static List<int[]> minConflictsStep(List<int[]> queens, int[][] board) {
        // csp: {queens, board, attacks}
        List<List<int[]>> potentialBoards = new ArrayList<>();

        if (countAttacksTotal(queens) == 0) {
            return null;
        }

        int index = 0;
        int attacks = 0;
        while (attacks == 0) {
            // pick a random queen
            index = (int) (Math.random() * queens.size());
            attacks = countAttacksIndividual(queens, index);
        }

        int[] queen = queens.get(index);
        int originalRow = queen[0];
        int originalCol = queen[1];

        // find conflicts and hold onto lowest ones
        for (int newRow = 0; newRow < queens.size(); newRow++) {
            if (newRow == originalRow) continue;

            List<int[]> currentQueensList = copyList(queens);
            currentQueensList.set(index, new int[]{newRow, originalCol});

            int currentAttackCount = countAttacksIndividual(currentQueensList, index);

            if (currentAttackCount < attacks) {
                potentialBoards.clear();
                attacks = currentAttackCount;
                potentialBoards.add(copyList(currentQueensList));
            } else if (currentAttackCount == attacks) {
                potentialBoards.add(copyList(currentQueensList));
            }  
        }

        if (potentialBoards.isEmpty()) {
            return null;
        }

        int tempIndex = (int) (Math.random() * potentialBoards.size());
        return copyList(potentialBoards.get(tempIndex));
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
            if (r1 == r2 || Math.abs(r1 - r2) == Math.abs(c1 - c2)) {
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
                if (r1 == r2 || Math.abs(r1 - r2) == Math.abs(c1 - c2)) {
                    attacks++;
                }
            }
        }

        return attacks;
    }

    public static List<int[]> randomQueens(int n) {
        List<int[]> queens = new ArrayList<>();
        for (int col = 0; col < n; col++) {
            int row = (int) (Math.random() * n);
            queens.add(new int[]{row, col});
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
