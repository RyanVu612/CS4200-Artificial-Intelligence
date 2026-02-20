package project1;

import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;

public class Puzzle {
    public static void main (String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Select:\n[1] Single Test Puzzle\n[2] Multi-Test Puzzle\n[3] Exit");
            int test = scanner.nextInt();

            int numberOfTests;
            while (true) {
                if (test == 1) {
                    // Single Test Puzzle
                    numberOfTests = 1;
                    break;
                } else if (test == 2) {
                    // Multi-Test Puzzle
                    System.out.println("Enter number of tests: (1 - 100)");
                    numberOfTests = scanner.nextInt();
                    if (numberOfTests < 1 || numberOfTests > 100) {
                        System.out.println("Invalid input.");
                        continue;
                    }
                    break;
                } else if (test == 3) {
                    // Exit
                    scanner.close();
                    return;
                } else {
                    System.out.println("Invalid input.");
                    continue;
                }
            }

            System.out.println("Select Input Method:\n[1] Random\n[2] File");
            int method = scanner.nextInt();

            while (true) {
                if (method == 1) {
                    // Random
                    break;
                } else if (method == 2) {
                    // File
                    break;
                } else {
                    System.out.println("Invalid Input.");
                    continue;
                }
            }

            System.out.println("Select Solution Depth (2-20)");
            int depth = scanner.nextInt();
            Node initialBoard = generateRandomBoard(depth);

            System.out.println("Puzzle:");
            printBoard(initialBoard.getBoard());

            System.out.println("Select H Function:\n[1] H1\n[2] H2");
            int hFunction = scanner.nextInt();

            while (true) {
                if (hFunction == 1) {
                    break;
                } else if (hFunction == 2) {
                    if (numberOfTests == 1) {
                        aStarH2(initialBoard, true);
                    } else {
                        int totalSearchCost = 0;
                        for (int i = 0; i < numberOfTests; i++) {
                            Node board = generateRandomBoard(depth);
                            totalSearchCost += aStarH2(board, false);
                        }
                        System.out.println("Average Search Cost: " + totalSearchCost);
                    }
                    break;
                } else if (hFunction == 3) {
                    break;
                } else {
                    System.out.println("Invalid input");
                    continue;
                }
            }
        }
    }  

    public static int aStarH1Individual(String board) {
        return 0;
    }

    public static int aStarH2(Node node, boolean individual) {
        Node currentNode = node;
        int count = 0;
        printBoard(currentNode.getBoard());

        if (!checkSolvability(currentNode.getBoard())) {
            System.out.println("Board is not solvable");
        }
        
        Map<String, Integer> gValue = new HashMap<>();      // board -> g value
        Map<String, String> parentMap = new HashMap<>();    // board -> parent board

        // Change comparator to use F
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>((a, b) -> a.getF() - b.getF());

        priorityQueue.add(currentNode);
        gValue.put(currentNode.getBoard(), currentNode.getG());
        String parentBoard = currentNode.getBoard();
        parentMap.put(currentNode.getBoard(), null);    // no parent to the initial

        // AStar algorithm. Will go until no more states to visit or goal is met.
        // When goal is met, break.
        while (!priorityQueue.isEmpty()) {
            currentNode = priorityQueue.poll();
            if (currentNode.getH() == 0) {
                break;
            }

            if (currentNode.getG() > gValue.get(currentNode.getBoard())) {
                continue; // outdated version of this board
            }

            int zeroRow = currentNode.getZeroRow();
            int zeroCol = currentNode.getZeroCol();
            
            for (Direction d: Direction.values()) {
                // make sure not on border
                if (zeroRow + d.dr >= 0 && zeroRow + d.dr <= 2 && zeroCol + d.dc >= 0 && zeroCol + d.dc <= 2) {
                    Node neighborNode = generateNeighborNode(currentNode, d);
                    count++;

                    // If repeated node, replace key with smaller g value. Otherwise, add to hashmap.
                    // In the case of repeated but larger g value, don't do anything.
                    if (!gValue.containsKey(neighborNode.getBoard())) {
                        gValue.put(neighborNode.getBoard(), neighborNode.getG());
                        parentMap.put(neighborNode.getBoard(), currentNode.getBoard());
                    } else if (neighborNode.getG() < gValue.get(neighborNode.getBoard())) {
                        gValue.put(neighborNode.getBoard(), neighborNode.getG());
                        parentMap.put(neighborNode.getBoard(), currentNode.getBoard());
                    }
                    // add both old and new nodes. the ones with smaller g will be skipped later on.
                    priorityQueue.add(neighborNode);
                }
            }
        }

        // currentNode now holds the node with goal. 
        // Need to figure out the path by retracing parents.
        String sBoard = currentNode.getBoard();
        ArrayList<String> path = new ArrayList<>();

        while (sBoard != null) {
            path.add(sBoard);
            sBoard = parentMap.get(sBoard);
        }

        // add parent
        path.add(parentBoard);

        // Reverse path to get from original path to goal
        Collections.reverse(path);

        // Print path :D
        if (individual) {
            for (int i = 0; i < path.size(); i++) {
                System.out.println("Step " + (i + 1));
                printBoard(path.get(i));
                System.out.println();
            }
        }

        return count;
    }

    public static int aStarH2Multiple(int depth) {
        return 0;
    }

    public static Node generateRandomBoard (int depth) {
        // Random Input
        Set<String> previousBoards = new HashSet<>();
        Node currentBoard = new Node("012345678", 0);
        
        int zeroRow;
        int zeroCol; 

        Direction[] directions = Direction.values();

        for (int i = 0; i < depth; i++) {
            while (true) {
                Direction direction = directions[ThreadLocalRandom.current().nextInt(directions.length)];

                zeroRow = currentBoard.getZeroRow();
                zeroCol = currentBoard.getZeroCol();

                int nRow = zeroRow + direction.dr;
                int nCol = zeroCol + direction.dc;

                if (nRow < 0 || nRow > 2 || nCol < 0 || nCol > 2) continue;

                Node nextNode = generateNeighborNode(currentBoard, direction);
                if (previousBoards.contains(nextNode.getBoard())) continue;

                currentBoard = nextNode;
                previousBoards.add(currentBoard.getBoard());
                break;
            }
        }

        return currentBoard;
    }

    public static Node generateNeighborNode(Node node, Direction d) {
        char[] board = node.getBoard().toCharArray();
        char temp = board[node.getZeroIndex() + (d.dr * 3 + d.dc)];

        board[node.getZeroIndex() + (d.dr * 3 + d.dc)] = '0';
        board[node.getZeroIndex()] = temp;

        return new Node(String.valueOf(board), node.getG() + 1);
    }

    public static boolean checkSolvability(String board) {
        int counter = 0;

        for (int i = 0; i < board.length(); i++) {
            int a = board.charAt(i) - '0';
            if (a == 0) continue;
            for (int j = i + 1; j < board.length(); j++) {
                int b = board.charAt(j) - '0';
                if (b == 0) continue;
                if (a > b) {
                    counter++;
                }
            }
        }

        return counter % 2 == 0;
    }

    public static void printBoard(String board) {
        int j = 0;
        for (int i = 0; i < board.length(); i ++) {
            System.out.print(board.charAt(i) + " ");
            if (j % 3 == 2) {
                System.out.println();
            }
            j++;
        }
    }
}

// Directional Enum
enum Direction {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    int dr, dc;

    Direction(int dr, int dc) {
        this.dr = dr;
        this.dc = dc;
    }
}

class Node {
    private String board;
    private int f;
    private int g;
    private int h;
    private int zeroIndex;

    public Node (String board, int g) {
        this.board = board;
        this.g = g;
        h = calculateH();
        f = g + h;
        zeroIndex = findZeroIndex();
    }

    public String getBoard() {
        return board;
    }

    public int getF() {
        return f;
    }

    public int getG() {
        return g;
    }

    public int getH() {
        return h;
    }

    public int calculateH() {
        // calculate the distance each tile is from its goal position. (col + row)
        int totalDist = 0;
        int value;
        for (int i = 0; i < board.length(); i++) {
            value = Character.getNumericValue(board.charAt(i));

            //skip 0 since empty spot
            if (value == 0) {
                continue;
            }

            // Row dist = i / 3
            // Col dist = i % 3

            // Add the dist for each tile to total distance
            totalDist += (Math.abs(value / 3 - i / 3) + Math.abs(value % 3 - i % 3));
        }

        return totalDist;
    }

    public int getZeroIndex() {
        return zeroIndex;
    }

    public int getZeroRow() {
        return zeroIndex / 3;
    }

    public int getZeroCol() {
        return zeroIndex % 3;
    }

    public int findZeroIndex() {
        for (int i = 0; i < board.length(); i++) {
            if (board.charAt(i) == '0') {
                zeroIndex = i;
                return zeroIndex;
            }
        }
        System.out.println("0 not found");
        return -1;
    }
}