package com.recruitment.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Robust console input utility to prevent common Scanner newline traps
 * and provide formatted parsing with validation.
 */
public class InputUtil {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private InputUtil() {
        // Utility class
    }

    /**
     * Reads a mandatory string prompt from console.
     */
    public static String readString(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String input = SCANNER.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("  [!] Field cannot be empty. Please enter a valid value.");
        }
    }

    /**
     * Reads an optional string with a fallback default value.
     */
    public static String readOptionalString(String prompt, String defaultValue) {
        System.out.print(prompt + (defaultValue != null && !defaultValue.isEmpty() ? " [" + defaultValue + "]: " : ": "));
        String input = SCANNER.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }

    /**
     * Reads an integer within an optional minimum and maximum range.
     */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt + " (" + min + " - " + max + "): ");
            String input = SCANNER.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("  [!] Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid number format. Please enter a valid integer.");
            }
        }
    }

    /**
     * Reads any valid integer.
     */
    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String input = SCANNER.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid number format. Please enter a valid integer.");
            }
        }
    }

    /**
     * Reads a double value with a minimum bound.
     */
    public static double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt + " (" + min + " - " + max + "): ");
            String input = SCANNER.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("  [!] Value must be between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid decimal format. Please enter a valid number.");
            }
        }
    }

    /**
     * Reads a double value without bounds.
     */
    public static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String input = SCANNER.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid decimal format. Please enter a valid number.");
            }
        }
    }

    /**
     * Reads a date in YYYY-MM-DD format.
     */
    public static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt + " (YYYY-MM-DD): ");
            String input = SCANNER.nextLine().trim();
            try {
                return LocalDate.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("  [!] Invalid date format. Please use YYYY-MM-DD (e.g., 2026-12-31).");
            }
        }
    }

    /**
     * Reads an optional date or returns default.
     */
    public static LocalDate readOptionalDate(String prompt, LocalDate defaultDate) {
        System.out.print(prompt + " (YYYY-MM-DD) [" + (defaultDate != null ? defaultDate.toString() : "None") + "]: ");
        String input = SCANNER.nextLine().trim();
        if (input.isEmpty()) {
            return defaultDate;
        }
        try {
            return LocalDate.parse(input, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("  [!] Invalid format. Kept default date.");
            return defaultDate;
        }
    }

    /**
     * Confirms yes/no choice.
     */
    public static boolean readConfirmation(String prompt) {
        System.out.print(prompt + " (y/n): ");
        String input = SCANNER.nextLine().trim().toLowerCase();
        return input.startsWith("y");
    }
}
