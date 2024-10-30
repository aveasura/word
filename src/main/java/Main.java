import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            startGameRound(sc);
        }
    }

    public static boolean startGameRound(Scanner sc) {
        System.out.println("Для запуска игры введите: \"Да\", а для выхода введите любой символ.");
        String userInput = input(sc);

        if (!userInput.equals("да")) {
            System.out.println("Вы вышли из игры");
            return false;
        } else {
            System.out.println("Игра начинается!");
            String randomWord = generateHiddenWord();
            startGameLoop(randomWord, sc);

            // рекурсивный вызов после победы / поражения
            return startGameRound(sc);
        }
    }

    public static String generateHiddenWord() {
        List<String> words = readFromFile("src/main/resources/word.txt");
        return words.isEmpty() ? "" : words.get(new Random().nextInt(words.size()));
    }

    public static List<String> readFromFile(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            return br.lines()
                    .flatMap(line -> Stream.of(line.toLowerCase().split(",\\s*")))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать файл: " + path, e);
        }
    }

    public static void startGameLoop(String randomWord, Scanner sc) {
        Set<Character> guessedLetters = new HashSet<>();
        String hiddenWord = "-".repeat(randomWord.length());

        int maxErrors = randomWord.length();
        int errors = 0;

        while (!hiddenWord.equals(randomWord)) {
            System.out.println(hiddenWord);
            System.out.println("Введите букву");
            String input = input(sc);

            if (isBlankString(input) || isDuplicate(guessedLetters, input.charAt(0))) {
                System.out.println("Вы ввели пустую строку");
                continue;
            }

            char userInput = input.charAt(0);
            guessedLetters.add(userInput);

            String updatedWord = revealGuessedLetters(randomWord, hiddenWord, userInput);
            if (updatedWord.equals(hiddenWord)) {
                System.out.println("Мимо! Счетчик ошибок: " + ++errors);
                if (errors == maxErrors) {
                    System.out.println("Вы проиграли! Загаданное слово было: " + randomWord + "\n");
                    return;
                }
            } else {
                hiddenWord = updatedWord;
            }
        }
        System.out.println("Вы выиграли! Загаданное слово было: " + randomWord + "\n");
    }

    public static String revealGuessedLetters(String word, String currentDisplay, char guess) {
        StringBuilder sb = new StringBuilder(currentDisplay);

        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == guess) {
                sb.setCharAt(i, guess);
            }
        }
        return sb.toString();
    }

    public static boolean isDuplicate(Set<Character> guessedLetters, char userInput) {
        if (!guessedLetters.add(userInput)) {
            System.out.println("Вы уже вводили эту букву: " + userInput + ", попробуйте ввести другую.");
            return true;
        }
        return false;
    }

    public static boolean isBlankString(String input) {
        return Optional.ofNullable(input)
                .filter(Predicate.not(String::isBlank))
                .isEmpty();
    }

    public static String input(Scanner sc) {
        return sc.nextLine().trim().toLowerCase();
    }
}