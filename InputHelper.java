import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

class InputHelper {

    public static String readText(Scanner input, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String text = input.nextLine().trim();
                if (text.isEmpty()) {
                    throw new Main.InvalidInputException("This field cannot be empty.");
                }
                return text.replace("|", " ");
            } catch (Main.InvalidInputException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public static String readOptionalText(Scanner input, String prompt) {
        System.out.print(prompt);
        return input.nextLine().trim().replace("|", " ");
    }

    public static int readInt(Scanner input, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(input.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Error: please enter a whole number.");
            }
        }
    }

    public static int readIntInRange(Scanner input, String prompt, int min, int max) {
        while (true) {
            int value = readInt(input, prompt);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.println("Error: please enter a number from " + min + " to " + max + ".");
        }
    }

    public static LocalDate readDate(Scanner input, String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = input.nextLine().trim();
            if (text.isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(text);
            } catch (DateTimeParseException e) {
                System.out.println("Error: invalid date. Use the format yyyy-MM-dd, for example 2026-12-15.");
            }
        }
    }

    public static boolean readYesNo(Scanner input, String prompt) {
        while (true) {
            System.out.print(prompt);
            String answer = input.nextLine().trim().toLowerCase();
            if (answer.equals("y") || answer.equals("yes")) {
                return true;
            }
            if (answer.equals("n") || answer.equals("no")) {
                return false;
            }
            System.out.println("Error: please type y or n.");
        }
    }

    public static String formatDate(LocalDate date) {
        return date == null ? "Not set" : date.toString();
    }
}
