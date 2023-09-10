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
    
        List<Food> allFoods = parseFoods();

        System.out.println("\n--- All Foods from Database ---");
        for(Food food : allFoods) {
            System.out.println(food.getName());
        }

        List<Food> preferredFoods = loadPreferredFoods("database\\Preferred.txt", allFoods);
        System.out.println("\n--- Preferred Foods ---");
        for(Food food : preferredFoods) {
            System.out.println(food.getName());
        }
        
        
        List<Food> mealPlan = createMealPlan(nutrientNeeds, preferredFoods, allFoods);

        if (mealPlan.isEmpty() == true) {
            printMissingNutrients(nutrientNeeds, mealPlan);
        } else {
            printMealPlan(mealPlan);
        }
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
            Food currentFood = null;
    
            for (String line : lines) {
                line = line.trim();
                
                if (line.startsWith("[")) {  // This detects the start of a new food
                    if (currentFood != null) {
                        foods.add(currentFood);
                    }
                    String foodName = line.substring(1, line.length() - 1).trim(); // Remove brackets
                    currentFood = new Food(foodName);
                } else if (line.startsWith("-")) {  // This detects a nutrient line
                    String[] nutrientData = line.substring(2).split(":");  // Remove the dash and split on the colon
                    if(nutrientData.length != 2) {
                        System.out.println("Error in line: " + line + ". Malformed nutrient data.");
                        continue;  // Skip this nutrient data
                    }
                    String nutrientName = nutrientData[0].trim().replaceAll("\"", "").replace("-", "").trim(); // Remove quotes and hyphen
                    double value = 0;
                    String nutrientValueString = nutrientData[1].trim().replaceAll("[\",]", ""); // Remove quotes and comma, then trim
                    try {
                        value = Double.parseDouble(nutrientValueString); // Parse to double
                    } catch(NumberFormatException e) {
                        System.out.println("Error parsing nutrient value in line: " + line);
                        continue;
                    }
                    currentFood.addNutrient(nutrientName, value);
                } else if (line.equals("}]")) {
                    // End of a food entry, just continue to the next line
                    continue;
                } else {
                    // Skipping unrecognized lines
                    System.out.println("Skipped unrecognized line: " + line);
                }
            }
    
            // Add the last food to the list, if any
            if (currentFood != null) {
                foods.add(currentFood);
            }
    
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
        
        return foods;
    }    

    public static List<Food> createMealPlan(Map<String, Double> nutrientNeeds, List<Food> preferredFoods, List<Food> allFoods) {
        List<Food> mealPlan = new ArrayList<>();
            
        // First, attempt to satisfy nutrient needs using preferred foods.
        while (!isNutrientNeedsSatisfied(nutrientNeeds) && !preferredFoods.isEmpty()) {
            Food bestFood = getBestOverallFood(nutrientNeeds, preferredFoods);
            if (bestFood == null) break;
            mealPlan.add(bestFood);
            preferredFoods.remove(bestFood); 
            allFoods.remove(bestFood); // Added this line
            updateNutrientNeeds(nutrientNeeds, bestFood);
        }
            
        // If nutrient needs are not fully satisfied using preferred foods, use all foods.
        while (!isNutrientNeedsSatisfied(nutrientNeeds)) {
            Food bestFood = getBestOverallFood(nutrientNeeds, allFoods);
            if (bestFood == null) break;
            mealPlan.add(bestFood);
            allFoods.remove(bestFood); // Added this line
            updateNutrientNeeds(nutrientNeeds, bestFood);
        }
            
        return mealPlan;
    }

    public static boolean isNutrientNeedsSatisfied(Map<String, Double> nutrientNeeds) {
        for (Double need : nutrientNeeds.values()) {
            if (need > 0) {
                return false;
            }
        }
        return true;
    }

    public static Food getBestOverallFood(Map<String, Double> nutrientNeeds, List<Food> foods) {
        Food bestFood = null;
        double maxScore = -1;
        
        for (Food food : foods) {
            double score = 0;
            for (String nutrient : nutrientNeeds.keySet()) {
                double amount = food.getNutrientValue(nutrient);
                score += Math.min(amount, nutrientNeeds.get(nutrient));
            }
    
            if (score > maxScore) {
                maxScore = score;
                bestFood = food;
            }
        }
        
        return bestFood;
    }

    public static void updateNutrientNeeds(Map<String, Double> nutrientNeeds, Food food) {
        for (Map.Entry<String, Double> entry : nutrientNeeds.entrySet()) {
            String nutrient = entry.getKey();
            Double need = entry.getValue();
        
            double foodAmount = food.getNutrientValue(nutrient);
            if (foodAmount > 0) {  
                // Commented out the print statements
                // System.out.println("For nutrient " + nutrient + ", food provides: " + foodAmount);
                double newNeed = Math.max(0, need - foodAmount);
                nutrientNeeds.put(nutrient, newNeed);
                // System.out.println("Deducted " + foodAmount + " from " + nutrient);
            }
        }
    }
       
    public static List<Food> loadPreferredFoods(String filepath, List<Food> allFoods) {
        List<Food> preferredFoods = new ArrayList<>();
        
        try {
            List<String> lines = Files.readAllLines(Paths.get(filepath));
            
            // Assuming the first line contains the preferred foods separated by semi-colons
            String[] preferredFoodNames = lines.get(0).split(";");
            
            System.out.println("Total preferred foods in Preferred.txt: " + preferredFoodNames.length);
            
            for (String foodName : preferredFoodNames) {
                foodName = foodName.trim().toLowerCase().replaceAll("-", " ");  // Convert to lowercase and replace hyphens with spaces
                boolean found = false;
                for (Food food : allFoods) {
                    if (food.getName().trim().toLowerCase().replaceAll("[-.]", " ").equals(foodName)) {
                        preferredFoods.add(food);
                        found = true;
                        System.out.println("Loaded preferred food: " + food.getName());
                        break;
                    }
                }
                if (!found) {
                    System.out.println("Warning: Preferred food '" + foodName + "' not found in the main database.");
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading Preferred.txt");
            e.printStackTrace();
        }
        
        return preferredFoods;
    }

    public static void printMealPlan(List<Food> mealPlan) {
        System.out.println("\nYour Meal Plan:");
        for (int i = 0; i < mealPlan.size(); i++) {
            System.out.println("Meal " + (i + 1) + ": " + mealPlan.get(i).getName());
        }
    }

    public static void printMissingNutrients(Map<String, Double> nutrientNeeds, List<Food> mealPlan) {
        System.out.println("\nMissing Nutrients:");
        boolean missingNutrients = false;
    
        for (String nutrient : nutrientNeeds.keySet()) {
            double remainingNeed = nutrientNeeds.get(nutrient);
            if (remainingNeed > 0) {
                System.out.println(nutrient + ": " + remainingNeed + " (Missing)");
                missingNutrients = true;
            }
        }
    
        if (!missingNutrients) {
            System.out.println("No missing nutrients.");
        }
    }    
}

class Food {
    private String name;
    private Map<String, Double> nutrients;

    public Food(String name) {
        this.name = name;
        this.nutrients = new HashMap<>();
    }

    public Food(String name, Map<String, Double> nutrients) {
        this.name = name;
        this.nutrients = nutrients;
    }

    public String getName() {
        return name;
    }

    public double getNutrientValue(String nutrient) {
        return nutrients.getOrDefault(nutrient, 0.0);
    }

    public void addNutrient(String nutrientName, double value) {
        nutrients.put(nutrientName, value);
    }

    @Override
    public String toString() {
        return name + ": " + nutrients.toString();
    }
}