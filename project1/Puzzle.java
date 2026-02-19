package project1;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Puzzle {
    public static void main (String[] args) {
        Node currentNode = generateBoard();

        if (!checkSolvability(currentNode.getBoard())) {
            System.out.println("Board is not solvable");
            return;
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
            if (currentNode.getG() > gValue.get(currentNode.getBoard())) {
                continue; // outdated version of this board
            }

            // check each direction
            if (currentNode.getH() == 0) {
                break;
            }

            int zeroRow = currentNode.getZeroIndex() / 3;
            int zeroCol = currentNode.getZeroIndex() % 3;
            
            for (Direction d: Direction.values()) {
                // make sure not on border
                if (zeroRow + d.dr >= 0 && zeroRow + d.dr <= 2 && zeroCol + d.dc >= 0 && zeroCol + d.dc <= 2) {
                    Node node = generateNeighborNode(currentNode, d);

                    // If repeated node, replace key with smaller g value. Otherwise, add to hashmap.
                    // In the case of repeated but larger g value, don't do anything.
                    if (!gValue.containsKey(node.getBoard())) {
                        gValue.put(node.getBoard(), node.getG());
                        parentMap.put(node.getBoard(), currentNode.getBoard());
                    } else if (node.getG() < gValue.get(node.getBoard())) {
                        gValue.put(node.getBoard(), node.getG());
                        parentMap.put(node.getBoard(), currentNode.getBoard());
                    }
                    // add both old and new nodes. the ones with smaller g will be skipped later on.
                    priorityQueue.add(node);
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
        for (String s : path) {
            System.out.println("=============2");
            printBoard(s);
        }
    }  

    @SuppressWarnings("resource")
    public static Node generateBoard () {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Enter board as 9 digits. E.g.(012345678 or 'random'");
            String input = scanner.nextLine();
            ArrayList<Character> numbers = new ArrayList<>();

            // Random Input
            if (input.equalsIgnoreCase("random")) {
                for (char c = '0'; c < '9'; c++) {
                    numbers.add(c);
                }
                Collections.shuffle(numbers);
                StringBuilder sb = new StringBuilder(9);
                for (int i = 0; i < numbers.size(); i++) {
                    sb.append(numbers.get(i));
                }
                return new Node(sb.toString(), 0);
            } 

            // Input validation
            if (input.length() != 9) {
                System.out.println("Error: Input must be exactly 9 numbers");
                continue;
            }

            boolean[] seen = new boolean[9];
            boolean ok = true;
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                if (c < '0' || c > '8') {
                    ok = false;
                    break;
                }
                int v = c - '0';
                if (seen[v]) {
                    ok = false;
                    break;
                }
                seen[v] = true;
            }

            if (!ok) {
                System.out.println("Error: There must be no repeating values");
                continue;
            }
            
            return new Node(input, 0);
        }
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

    public Integer getF() {
        return f;
    }

    public Integer getG() {
        return g;
    }

    public Integer getH() {
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

    public Integer getZeroIndex() {
        return zeroIndex;
    }

    public Integer findZeroIndex() {
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