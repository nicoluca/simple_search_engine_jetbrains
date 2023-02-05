package search;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static List<String> lines;
    private static Map<String, Set<Integer>> invertedIndex;
    final static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {

        if (args.length < 2 || !args[0].equals("--data") || args[1].isBlank() ) {
            System.err.println("Please provide a data file: --data <file>");
            scanner.close();
            return;
        }

        String fileName = args[1];
        readFile(fileName);

        final Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();
            int choice = getInt();

            switch (choice) {
                case 1:
                    search();
                    break;
                case 2:
                    System.out.println("=== List of people ===");
                    lines.forEach(System.out::println);
                    break;
                case 0:
                    System.out.println("Bye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Incorrect option! Try again.");
            }
        }
    }

    private static void readFile(String fileName) throws IOException {
        Path filePath = Paths.get(fileName);
        lines = Files.readAllLines(filePath);
        invertedIndex = new HashMap<>();
        for (int i = 0; i < lines.size(); i++) {
            for (String s : lines.get(i).split(" ")) {
                s = s.toLowerCase();
                if (invertedIndex.containsKey(s)) {
                    invertedIndex.get(s).add(i);
                } else {
                    invertedIndex.put(s, new HashSet<>(Arrays.asList(i)));
                }
            }
        }
    }

    private static void printMenu() {
        System.out.println("=== Menu ===");
        System.out.println("1. Find a person");
        System.out.println("2. Print all people");
        System.out.println("0. Exit");
    }

    private static String getViableString(String prompt, String errorMessage, String... options) {
        System.out.println(prompt);
        String input = scanner.nextLine();
        for (String option : options) {
            if (input.equals(option)) {
                return input;
            }
        }
        System.out.println(errorMessage);
        return getViableString(prompt, errorMessage, options);
    }

    private static void search() {
        String strategy = getViableString("Select a matching strategy: ALL, ANY, NONE",
                "Incorrect option. Try again.",
                "ALL", "ANY", "NONE");
        Set<Integer> resultingIndices = new HashSet<>();
        switch (strategy) {
            case "ALL":
                resultingIndices =
                        Arrays.stream(scanner.nextLine().split(" "))
                                .distinct()
                                .map(String::toLowerCase)
                                .filter(invertedIndex::containsKey)
                                .map(invertedIndex::get)
                                .reduce((a, b) -> {
                                    a.retainAll(b);
                                    return a;
                                })
                                .orElse(new HashSet<>());
                break;
            case "ANY":
                resultingIndices =
                        Arrays.stream(scanner.nextLine().split(" "))
                        .distinct()
                        .map(String::toLowerCase)
                        .filter(invertedIndex::containsKey)
                        .map(invertedIndex::get)
                        .reduce((a, b) -> {
                            a.addAll(b);
                            return a;
                        })
                        .orElse(new HashSet<>());
                break;
            case "NONE":
                resultingIndices = lines.stream().
                        map(lines::indexOf).
                        collect(Collectors.toSet());

                Set<Integer> intermediateIndices =
                        Arrays.stream(scanner.nextLine().split(" "))
                                .distinct()
                                .map(String::toLowerCase)
                                .filter(invertedIndex::containsKey)
                                .map(invertedIndex::get)
                                .reduce((a, b) -> {
                                    a.addAll(b);
                                    return a;
                                })
                                .orElse(new HashSet<>());

                resultingIndices.removeAll(intermediateIndices);
                break;
        }

        resultingIndices.forEach(i -> System.out.println(lines.get(i)));
        if (resultingIndices.isEmpty()) {
            System.out.println("No matching people found.");
        }
    }

    public static int getInt() {
        while (true) {
            try {
                return Integer.valueOf(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Not an integer, try again.");
            }
        }
    }

}
