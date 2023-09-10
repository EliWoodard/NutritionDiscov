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
        sc.nextLine(); 
        
        // Ask for weight
        System.out.print("What is your weight (in lb)? ");
        int weight = sc.nextInt();
        sc.nextLine(); 

        // Ask for age
        System.out.print("What is your age? ");
        int age = sc.nextInt();
        sc.nextLine();

        // Ask for gender
        System.out.print("Please specify your gender (male/female): ");
        String gender = sc.nextLine().trim().toLowerCase();

        // Ask for activity level
        System.out.println("For activity level, please specify either by intensity by duration (1 to 5 or 0 minutes to 60 minutes).");
        System.out.print("What is your activity level? ");
        double activityLevel = sc.nextDouble();

        // Output for verification
        System.out.println("Your details:");
        System.out.println("Height: " + height + " inches");
        System.out.println("Weight: " + weight + " pounds");
        System.out.println("Age: " + age);
        System.out.println("Gender: " + gender);
        System.out.println("Activity Level: " + activityLevel);

        double bmr = calculateBMR(weight, height, age, gender);
        double tdee = calculateTDEE(bmr, activityLevel);
    
        System.out.println("Your BMR is: " + bmr + " calories/day");
        System.out.println("Your TDEE is: " + tdee + " calories/day");
        
        Map<String, Double> nutrientNeeds = calculateNutrientNeeds(tdee);
    
        for (Map.Entry<String, Double> entry : nutrientNeeds.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    
        List<Food> foods = parseFoods();
    }

    public static double calculateBMR(int weight, double height, int age, String gender) {
        if (gender.equals("male")) {
            return 88.362 + (13.39 * weight) + (4.79 * height) - (5.67 * age);
        } else {
            return 447.593 + (9.24 * weight) + (3.09 * height) - (4.33 * age);
        }
    }
    
    public static double calculateTDEE(double bmr, double activityLevel) {
        double multiplier;
    
        switch ((int) activityLevel) {
            case 1:
                multiplier = 1.1;
                break;
            case 2:
                multiplier = 1.2;
                break;
            case 3:
                multiplier = 1.3;
                break;
            case 4:
                multiplier = 1.4;
                break;
            case 5:
                multiplier = 1.5;
                break;
            default:
                multiplier = 1; 
                break;
        }
    
        return bmr * multiplier;
    }
    
    public static Map<String, Double> calculateNutrientNeeds(double tdee) {
        Map<String, Double> nutrients = new HashMap<>();
        
        // Calories & Macronutrients
        nutrients.put("Calories", tdee);
        nutrients.put("Total fat(g)", tdee * 0.0333);  // Assuming around 30% of TDEE from fats
        nutrients.put("Protein(g)", tdee * 0.0375);  // Assuming around 15% of TDEE from proteins
        nutrients.put("Carbohydrate(g)", tdee * 0.1375);  // Assuming around 55% of TDEE from carbs
        nutrients.put("Fiber(g)", 25.0); // General recommendation for adults
        nutrients.put("Sugar(g)", (tdee * 0.05) /4); // Aim to limit added sugars to 5% of daily caloric intake
    
        // Vitamins
        nutrients.put("Vitamin A(mg)", 900.0); // RDA for men
        nutrients.put("Vitamin C(mg)", 90.0); // RDA for men
        nutrients.put("Vitamin D(mg)", 20.0); // General RDA for adults
        nutrients.put("Vitamin E(mg)", 15.0); // General RDA for adults
        nutrients.put("Vitamin K(micro-grams)", 120.0); // RDA for men
        nutrients.put("Vitamin B12(mg)", 2.4); // General RDA for adults
        nutrients.put("Folate(Vitamin B9)(micro-grams)", 400.0); // General RDA for adults
    
        // Minerals
        nutrients.put("Calcium(mg)", 1000.0); // General RDA for adults
        nutrients.put("Iron(mg)", 8.0); // RDA for men
        nutrients.put("Magnesium(mg)", 420.0); // RDA for men
        nutrients.put("Zinc(mg)", 11.0); // RDA for men
    
        // Others (some nutrients are not directly related to caloric intake but are included for completion)
        nutrients.put("Cholesterol(mg)", 300.0); // Recommended maximum intake per day
        nutrients.put("Sodium(mg)", 1500.0); // Upper intake level
        nutrients.put("Potassium(mg)", 3400.0); // RDA for men
    
        return nutrients;
    }

    public static List<Food> parseFoods() {
        List<Food> foods = new ArrayList<>();
        Path filePath = Paths.get("database\\Foods.txt");
        
        try {
            List<String> lines = Files.readAllLines(filePath);
            for (String line : lines) {
                // Stop reading when we reach the "--" symbol
                if (line.trim().equals("--")) {
                    break;
                }

                if (line.length() < 2) {
                    continue;
                }
                
                // Parse the food data
                String cleaned = line.substring(1, line.length() - 1); // remove brackets
                String[] parts = cleaned.split(", ");
                
                Food food = new Food(parts[0]);
                for (int i = 1; i < parts.length; i++) {
                    food.addNutrient(Double.parseDouble(parts[i]));
                }
                foods.add(food);
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
    
        return foods;
    }    
}

class Food {
    private String name;
    private List<Double> nutrients;

    public Food(String name) {
        this.name = name;
        this.nutrients = new ArrayList<>();
    }

    public void addNutrient(double value) {
        nutrients.add(value);
    }

    @Override
    public String toString() {
        return name + ": " + nutrients.toString();
    }
}
