package Day1;

import java.util.Scanner;

public class Arrays {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int[] seats = new int[300];
        boolean stop = false;

        while(!stop){
            System.out.println("Displaying Numbers of seats available");
            for(int i = 0; i < seats.length; i++){
                if(seats[i] == 0){
                    System.out.print("[" + i + "]");
                } else {
                    System.out.print("[X]");
                }


                if(i < 9) System.out.print("    ");
                else if(i < 99) System.out.print("   ");
                else System.out.print("  ");


                if((i + 1) % 10 == 0){
                    System.out.println();
                }
            }

            System.out.print("\nEnter seat number to book : ");
            int seatNum = sc.nextInt();


            if(seatNum < 0 || seatNum >= 300){
                System.out.println("Invalid seat number!");
            } else if(seats[seatNum] == 1){
                System.out.println("Seat already booked!");
            } else {
                seats[seatNum] = 1;
                System.out.println("Seat " + seatNum + " booked successfully!");

                System.out.println("Continue booking? (Y/N): ");
                char choice = sc.next().toUpperCase().charAt(0);
                if(choice == 'N'){
                    stop = true;
                    System.out.println("Thank you for using the booking system!");
                }
            }
        }
        sc.close();
    }
}
