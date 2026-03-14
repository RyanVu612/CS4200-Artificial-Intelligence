package project2;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class nQueen {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter number of queens: ");
        int n = scanner.nextInt();
        List<int[]> queens = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            queens.add(new int[n]);
        }

        // Randomize queen positions
        int i = 0;
        while (i < n){
            int row = (int) (Math.random() * (n));
            int col = (int) (Math.random() * (n));

            if (queens.get(row)[col] == 0) {
                queens.get(row)[col] = 1;
                i++;
            } 
        }  

        printBoard(queens);
    }

    public static boolean checkAttack(){

        return false;
    }

    public static void printBoard(List<int[]> queens) {
        for (int i = 0; i < queens.size(); i++) {
            for (int j = 0; j < queens.size(); j++) {
                System.out.print(queens.get(i)[j] + " ");
            }
            System.out.println();
        }
    }
}
