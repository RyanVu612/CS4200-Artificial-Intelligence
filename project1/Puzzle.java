package project1;

import java.util.Scanner;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;

public class Puzzle {
    public static void main (String[] args) {
        Node initialNode = generateBoard();
        Map<String, Integer> gValue = new HashMap<>();  // board -> g value

        // Change comparator to use F
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>((a, b) -> a.getF() - b.getF());

        priorityQueue.add(initialNode);
        gValue.put(initialNode.getBoard(), initialNode.getG());

        // AStar algorithm. Will go until no more states to visit or goal is met.
        // When goal is met, break.
        while (!priorityQueue.isEmpty()) {
            // check each direction
            Node currentNode = priorityQueue.poll();
            int zeroRow = currentNode.getZeroIndex() / 3;
            int zeroCol = currentNode.getZeroIndex() % 3;
            
            for (Direction d: Direction.values()) {
                // make sure not on border
                if (zeroRow + d.dr >= 0 && zeroRow + d.dr <= 2 && zeroCol + d.dc >= 0 && zeroCol + d.dc <= 2) {
                    Node node = generateNeighborNode(currentNode, d);

                    // If repeated node, replace key with smaller g value. Otherwise, add to hashmap.
                    if (gValue.containsKey(node.getBoard()) && node.getG() < gValue.get(node.getBoard())) {
                        gValue.put(node.getBoard(), node.getG());
                    } else {
                        gValue.put(node.getBoard(), node.getG());
                    }

                    // add both old and new nodes. the ones with smaller g will be skipped later on.
                    priorityQueue.add(node);
                }
            }
        }
    }  

    @SuppressWarnings("resource")
    public static Node generateBoard () {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter in a board. If you wish to generate a random board, enter 'random.'");
        System.out.println("Example of board input: '0, 1, 2, 3, 4, 5, 6, 7, 8' will generate this board:");
        System.out.println("0 1 2\n3 4 5\n6 7 8");
        System.out.print("Enter input: ");

        String input = scanner.nextLine();

        Set<Integer> numbersSet = new HashSet<>();
        ArrayList<Integer> numbers = new ArrayList<>();

        // Random Input
        if (input.equals("random")) {
            for (int i = 0; i < 9; i++) {
                numbers.add(i);
            }
            Collections.shuffle(numbers);
        } else {
            while (numbersSet.size() < 8) {
                // Add inputted numbers into a set
                for (int i = 0; i < input.length(); i += 3) {
                    numbersSet.add(Character.getNumericValue(input.charAt(i)));
                }

                // If there were any duplicates, or numbers not between 0-8, prompt for new board
                if (numbersSet.size() < 8 || Collections.min(numbersSet) < 0 || Collections.max(numbersSet) > 8) {
                    numbersSet.clear();
                    System.out.print("Error. Invalid input.\nEnter a valid input: ");
                    input = scanner.nextLine();
                }
            }
            // Add numbers of the set to the arraylist
            numbers.addAll(numbersSet);    
        }
            
        Node node = new Node(numbers.toString(), 0);
        return node;
    }

    public static Node generateNeighborNode(Node node, Direction d) {
        char[] board = node.getBoard().toCharArray();

        board[node.getZeroIndex()] = board[node.getZeroIndex() + (d.dr * 3 + d.dc)];
        board[node.getZeroIndex() + (d.dr * 3 + d.dc)] = 0;

        return new Node(String.valueOf(board), node.getG());
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
        this.g = g++;
        h = calculateH();
        f = g + h;
    }

    public int calculateH() {
        // calculate the distance each tile is from its goal position. (col + row)
        int totalDist = 0;
        int value;
        for (int i = 0; i < board.length(); i++) {
            value = Character.getNumericValue(board.charAt(i));

            //skip 0 since empty spot
            if (value == 0) {
                zeroIndex = i;
                continue;
            }

            // Row dist = i / 3
            // Col dist = i % 3

            // Add the dist for each tile to total distance
            totalDist += (Math.abs(value / 3 - i / 3) + Math.abs(value % 3 - i % 3));
        }

        return totalDist;
    }

    public String getBoard() {
        return board;
    }

    public void printBoard() {
        int j = 0;
        for (int i = 1; i < board.length(); i += 3) {
            System.out.print(board.charAt(i) + " ");
            if (j % 3 == 2) {
                System.out.println();
            }
            j++;
        }
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

    public Integer getZeroIndex() {
        return zeroIndex;
    }
}