package project1;

import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class Puzzle {
    public static void main (String[] args) {
        // Build BFS Map of all boards to be able to find boards of optimal depths.
        Map<String, Integer> depthMap = buildDepthMap();
        int depth;
        int numberOfTests;

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Select:\n[1] Single Test Puzzle\n[2] Multi-Test Puzzle\n[3] Exit");
            int test = scanner.nextInt();

            while (true) {
                if (test == 1) {
                    // Single Test Puzzle
                    numberOfTests = 1;
                    break;
                } else if (test == 2) {
                    // Multi-Test Puzzle
                    System.out.println("Enter number of tests (1 - 100): ");
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

            System.out.println("Select Input Method:\n[1] Random\n[2] File\n[3] System.in");
            int method = scanner.nextInt();

            while (true) {
                if (method >= 1 && method <= 3) {
                    break;
                } else {
                    System.out.println("Invalid Input.");
                    continue;
                }
            }

            if (method == 1) {
                System.out.println("Select Solution Depth (2-20): ");

                while (true) {
                    depth = scanner.nextInt();
                    
                    if (depth >= 2 && depth <= 20) {
                        break;
                    }
                    System.out.println("Invalid depth size.");
                }

                String initialBoardString = getBoardAtDepth(depthMap, depth);
                Node initialBoard = new Node (initialBoardString, 0);

                if (numberOfTests == 1) {
                    System.out.println("Puzzle:");
                    printBoard(initialBoard.getBoard());
                }

                System.out.println("\nSelect H Function:\n[1] H1\n[2] H2\n[3] Compare H1 and H2");
                int hInput = scanner.nextInt();

                while (true) {
                    if (hInput >= 1 && hInput <= 3) {
                        break;
                    } else {
                        System.out.println("Invalid input");
                        continue;
                    }
                }

                if (numberOfTests == 1) {
                    if (hInput == 1) {
                        int searchCost = aStarIndividual(initialBoard, true, true);
                        System.out.println("Search Cost for H1: " + searchCost);
                    } else if (hInput == 2) {
                        int searchCost = aStarIndividual(initialBoard, false, true);
                        System.out.println("Search Cost for H2: " + searchCost);
                    } else {
                        int searchCostH1 = aStarIndividual(initialBoard, true, true);
                        int searchCostH2 = aStarIndividual(initialBoard, false, false);
                        System.out.println("Search Cost for H1: " + searchCostH1);
                        System.out.println("Search Cost for H2: " + searchCostH2);
                    }
                } else {
                    int totalSearchCostH1 = 0;
                    int totalSearchCostH2 = 0;
                    for (int i = 0; i < numberOfTests; i++) {
                        String boardString = getBoardAtDepth(depthMap, depth);
                        if (hInput == 1 || hInput == 3) {
                            totalSearchCostH1 += aStarMultiple(new Node (boardString, 0), true);
                        } 
                        
                        if (hInput == 2 || hInput == 3) {
                            totalSearchCostH2 += aStarMultiple(new Node (boardString, 0), false);
                        } 
                    }

                    if (hInput == 1 || hInput == 3) {
                        System.out.println("Average Search Cost for H1: " + ((double) totalSearchCostH1 / numberOfTests));
                    }

                    if (hInput == 2 || hInput == 3) {
                        System.out.println("Average Search Cost for H2: " + ((double) totalSearchCostH2 / numberOfTests));
                    }
                }
            } else if (method == 2) {
                // File input
                scanner.nextLine();
                System.out.print("Enter file name: ");
                String fileName = scanner.nextLine();
                int tests = 0;

                System.out.println("\nSelect H Function:\n[1] H1\n[2] H2\n[3] Compare H1 and H2");
                int hInput = scanner.nextInt();

                while (true) {
                    if (hInput >= 1 && hInput <= 3) {
                        break;
                    } else {
                        System.out.println("Invalid input");
                        continue;
                    }
                }
                
                int totalSearchCostH1 = 0;
                int totalSearchCostH2 = 0;
                try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                    String line;
                    String board = "";
                    int count = 0;
                    while ((line = br.readLine()) != null) {
                        for (int i = 0; i < line.length(); i++) {
                            char c = line.charAt(i);

                            if (Character.isDigit(c)) {
                                board += c;
                            }
                        }

                        if (board.length() == 9) {
                            // Full board formed
                            if (numberOfTests == 1) {
                                if (hInput == 1) {
                                    totalSearchCostH1 = aStarIndividual(new Node(board, 0), true, true);
                                } else if (hInput == 2) {
                                    totalSearchCostH2 = aStarIndividual(new Node(board, 0), false, true);
                                } else {
                                    totalSearchCostH1 = aStarIndividual(new Node(board, 0), true, false);
                                    totalSearchCostH2 = aStarIndividual(new Node(board, 0), false, true);
                                }
                                break;
                            } else {
                                if (hInput == 1 || hInput == 3) {
                                    totalSearchCostH1 += aStarMultiple(new Node(board, 0), true);
                                }

                                if (hInput == 2 || hInput == 3) {
                                    totalSearchCostH2 += aStarMultiple(new Node(board, 0), false);
                                }

                                board = "";
                                tests++;
                                if (tests >= numberOfTests) {
                                    break;
                                }
                            }
                        }
                        count++;
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }

                if (numberOfTests == 1) {
                    if (hInput == 1 || hInput == 3) {
                        System.out.println("Total Search Cost for H1: " + totalSearchCostH1);
                    }

                    if (hInput == 2 || hInput == 3) {
                        System.out.println("Total Search Cost for H2: " + totalSearchCostH2);
                    }
                } else {
                    if (hInput == 1 || hInput == 3) {
                        System.out.println("Average Search Cost for H1: " + totalSearchCostH1 / tests);
                    }

                    if (hInput == 2 || hInput == 3) {
                        System.out.println("Average Search Cost for H2: " + totalSearchCostH2 / tests);
                    }
                }
            } else {
                // User input
                System.out.println("Enter your board: ");

                StringBuilder sb = new StringBuilder();

                while (true) {
                    boolean valid = true;
                    HashSet<Integer> seen = new HashSet<>();
                    for (int i = 0; i < 9; i++) {
                        int j = scanner.nextInt();
                        if (j < 0 || j > 8) valid = false;
                        seen.add(j);
                        sb.append(j);
                    }

                    if (seen.size() < 9 || !valid) {
                        System.out.println("Invalid board. Enter new board: ");
                        continue;
                    }

                    break;
                }

                Node initialBoard = new Node (sb.toString(), 0);

                System.out.println("\nSelect H Function:\n[1] H1\n[2] H2\n[3] Compare H1 and H2");
                int hInput = scanner.nextInt();

                while (true) {
                    if (hInput >= 1 && hInput <= 3) {
                        break;
                    } else {
                        System.out.println("Invalid input");
                        continue;
                    }
                }

                if (numberOfTests == 1) {
                    if (hInput == 1) {
                        int searchCost = aStarIndividual(initialBoard, true, true);
                        System.out.println("Search Cost: " + searchCost);
                    } else if (hInput == 2) {
                        int searchCost = aStarIndividual(initialBoard, false, true);
                        System.out.println("Search Cost: " + searchCost);
                    } else {
                        int searchCostH1 = aStarIndividual(initialBoard, true, false);
                        int searchCostH2 = aStarIndividual(initialBoard, false, true);
                        System.out.println("Search Cost for H1: " + searchCostH1);
                        System.out.println("Search Cost for H2: " + searchCostH2);
                    }
                } else {
                    int totalSearchCostH1 = 0;
                    int totalSearchCostH2 = 0;
                    for (int i = 0; i < numberOfTests; i++) {
                        System.out.println("Enter the next board: ");

                        sb.setLength(0);
                        for (int j = 0; j < 9; j++) {
                            sb.append(scanner.nextInt());
                        }

                        String boardString = sb.toString();
                        if (hInput == 1 || hInput == 3) {
                            totalSearchCostH1 += aStarMultiple(new Node (boardString, 0), true);
                        } 
                        
                        if (hInput == 2 || hInput == 3) {
                            totalSearchCostH2 += aStarMultiple(new Node (boardString, 0), false);
                        } 
                    }

                    if (hInput == 1 || hInput == 3) {
                        System.out.println("Average Search Cost for H1: " + ((double) totalSearchCostH1 / numberOfTests));
                    }

                    if (hInput == 2 || hInput == 3) {
                        System.out.println("Average Search Cost for H2: " + ((double) totalSearchCostH2 / numberOfTests));
                    }
                }
            }
        }
    }  

    public static int aStarIndividual(Node node, boolean h1, boolean print) {
        Node currentNode = node;

        if (h1) {
            currentNode.calculateH1();
        } else {
            currentNode.calculateH2();
        }

        int count = 0;

        if (!checkSolvability(currentNode.getBoard())) {
            System.out.println("Board is not solvable");
            return -1;
        }
        
        Map<String, Integer> gValue = new HashMap<>();      // board -> g value
        Map<String, String> parentMap = new HashMap<>();    // board -> parent board

        // Change comparator to use F
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>((a, b) -> a.getF() - b.getF());

        priorityQueue.add(currentNode);
        gValue.put(currentNode.getBoard(), currentNode.getG());
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
                    
                    if (h1) {
                        neighborNode.calculateH1();
                    } else {
                        neighborNode.calculateH2();
                    }

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

        if (currentNode.getH() != 0) {
            System.out.println("No solution found.");
            return count;
        }

        // currentNode now holds the node with goal. 
        // Need to figure out the path by retracing parents.
        String sBoard = currentNode.getBoard();
        ArrayList<String> path = new ArrayList<>();

        while (sBoard != null) {
            path.add(sBoard);
            sBoard = parentMap.get(sBoard);
        }

        // Reverse path to get from original path to goal
        Collections.reverse(path);

        // Print path :D
        if (print) {
            for (int i = 0; i < path.size(); i++) {
                System.out.println("Step: " + (i));
                printBoard(path.get(i));
                System.out.println();
            }
        }

        return count;
    }

    public static int aStarMultiple(Node node, boolean h1) {
        Node currentNode = node;

        if (h1) {
            currentNode.calculateH1();
        } else {
            currentNode.calculateH2();
        }

        int count = 0;

        if (!checkSolvability(currentNode.getBoard())) {
            System.out.println("Board is not solvable");
            return -1;
        }
        
        Map<String, Integer> gValue = new HashMap<>();      // board -> g value
        Map<String, String> parentMap = new HashMap<>();    // board -> parent board

        // Change comparator to use F
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>((a, b) -> a.getF() - b.getF());

        priorityQueue.add(currentNode);
        gValue.put(currentNode.getBoard(), currentNode.getG());
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
                    
                    if (h1) {
                        neighborNode.calculateH1();
                    } else {
                        neighborNode.calculateH2();
                    }

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

        if (currentNode.getH() != 0) {
            System.out.println("No solution found.");
            return count;
        }

        return count;
    }

    public static Map<String, Integer> buildDepthMap() {
        Map<String, Integer> depthMap = new HashMap<>();
        Queue<Node> queue = new LinkedList<>();
        
        Node start = new Node("012345678", 0);

        queue.add(start);
        depthMap.put(start.getBoard(), 0);

        while(!queue.isEmpty()) {
            Node currentNode = queue.poll();
            int currentDepth = depthMap.get(currentNode.getBoard());

            int zeroRow = currentNode.getZeroRow();
            int zeroCol = currentNode.getZeroCol();

            // Check each direction
            for (Direction d : Direction.values()) {
                int newRow = zeroRow + d.dr;
                int newCol = zeroCol + d.dc;

                if (newRow < 0 || newRow > 2 || newCol < 0 || newCol > 2) continue;

                Node neighborNode = generateNeighborNode(currentNode, d);

                if (!depthMap.containsKey(neighborNode.getBoard())) {
                    depthMap.put(neighborNode.getBoard(), currentDepth + 1);
                    queue.add(neighborNode);
                }
            }
        }

        return depthMap;
    }

    public static String getBoardAtDepth (Map<String, Integer> depthMap, int depth) {
        ArrayList<String> boards = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : depthMap.entrySet()) {
            if (entry.getValue() == depth) {
                boards.add(entry.getKey());
            }
        }

        if (boards.isEmpty()) {
            System.out.println("No boards of depth " + depth);
            return null;
        }

        return boards.get(ThreadLocalRandom.current().nextInt(boards.size()));
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

    public int calculateH1() {
        // calculate the number of misplaced tiles
        int count = 0;
        for (int i = 0; i < board.length(); i++) {
            if (board.charAt(i) != '0' && board.charAt(i) != (char)(i + '0')) {
                count++;
            }
        }
        h = count;
        f = g + h;
        return count;
    }

    public int calculateH2() {
        // calculate the distance each tile is from its goal position. (col + row)
        int totalDist = 0;
        for (int i = 0; i < board.length(); i++) {
            int value = Character.getNumericValue(board.charAt(i));

            //skip 0 since empty spot
            if (value == 0) {
                continue;
            }

            // Row dist = i / 3
            // Col dist = i % 3
            int goalIndex = value;
            int goalRow = goalIndex / 3;
            int goalCol = goalIndex % 3;

            int currentRow = i / 3;
            int currentCol = i % 3;

            // Add the dist for each tile to total distance
            totalDist += (Math.abs(goalRow - currentRow) + Math.abs(goalCol - currentCol));
        }
        h = totalDist;
        f = g + h;
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