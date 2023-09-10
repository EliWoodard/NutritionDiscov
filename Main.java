import java.util.*;
import java.nio.file.*;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        // Ask user to make sure to add preferred foods in the database file
        System.out.println("Please make sure to add your preferred foods in the database file.");

        // Ask for height
        System.out.print("What is your height (in inches)? ");
        double height = sc.nextDouble();
        sc.nextLine(); // Consume newline
        
        // Ask for weight
        System.out.print("What is your weight (in lb)? ");
        int weight = sc.nextInt();
        sc.nextLine(); // Consume newline

        // Ask for activity level
        System.out.println("For activity level, please specify either by intensity by duration (1 to 5 or 0 minutes to 60 minutes).");
        System.out.print("What is your activity level? ");
        double activityLevel = sc.nextDouble();

        // Output for verification
        System.out.println("Your details:");
        System.out.println("Height: " + height + " inches");
        System.out.println("Weight: " + weight + " pounds");
        System.out.println("Activity Level: " + activityLevel);
    }
}
