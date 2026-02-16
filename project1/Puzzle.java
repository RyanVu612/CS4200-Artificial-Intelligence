package project1;

import java.util.Scanner;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class Puzzle {
    public static void main (String[] args) {
        String board = generateBoard();

        printBoard(board);
    }  

    public void aStar() {

    }

    @SuppressWarnings("resource")
    public static String generateBoard () {
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
            
        return numbers.toString();
    }

    public static void printBoard(String board) {
        int j = 0;
        for (int i = 1; i < board.length(); i += 3) {
            System.out.print(board.charAt(i) + " ");
            if (j % 3 == 2) {
                System.out.println();
            }
            j++;
        }
    }
}