import java.util.*;

public class AlphameticSolver {

    // Entry point: takes the left words and right result word
    public static void solve(String[] leftWords, String resultWord) {
        Set<Character> uniqueChars = new HashSet<>();
        for (String word : leftWords) for (char c : word.toCharArray()) uniqueChars.add(c);
        for (char c : resultWord.toCharArray()) uniqueChars.add(c);

        // More than 10 unique letters means it's impossible
        if (uniqueChars.size() > 10) {
            System.out.println("Too many unique letters (>10), no solution possible.");
            return;
        }

        List<Character> letters = new ArrayList<>(uniqueChars);
        boolean[] usedDigits = new boolean[10];
        Map<Character, Integer> map = new HashMap<>();

        backtrack(0, letters, usedDigits, map, leftWords, resultWord);
    }

    private static void backtrack(int index, List<Character> letters, boolean[] usedDigits,
                                  Map<Character, Integer> map, String[] leftWords, String resultWord) {
        if (index == letters.size()) {
            if (isValid(map, leftWords, resultWord)) {
                System.out.println("Solution:");
                for (char ch : map.keySet()) {
                    System.out.println(ch + " = " + map.get(ch));
                }
                System.out.println();
            }
            return;
        }

        for (int digit = 0; digit <= 9; digit++) {
            if (!usedDigits[digit]) {
                usedDigits[digit] = true;
                map.put(letters.get(index), digit);
                backtrack(index + 1, letters, usedDigits, map, leftWords, resultWord);
                usedDigits[digit] = false;
                map.remove(letters.get(index));
            }
        }
    }

    private static boolean isValid(Map<Character, Integer> map, String[] leftWords, String resultWord) {
        // No leading zeros
        for (String word : leftWords) {
            if (map.get(word.charAt(0)) == 0) return false;
        }
        if (map.get(resultWord.charAt(0)) == 0) return false;

        long leftSum = 0;
        for (String word : leftWords) {
            leftSum += wordToNumber(word, map);
        }

        long right = wordToNumber(resultWord, map);

        return leftSum == right;
    }

    private static long wordToNumber(String word, Map<Character, Integer> map) {
        long num = 0;
        for (char c : word.toCharArray()) {
            num = num * 10 + map.get(c);
        }
        return num;
    }

    public static void main(String[] args) {
        // Example 1: Valid
        System.out.println("Trying: SEND + MORE = MONEY");
        solve(new String[]{"SEND", "MORE"}, "MONEY");

        // Example 2: Invalid
        System.out.println("Trying: CODE + BUG = DEBUG");
        solve(new String[]{"CODE", "BUG"}, "DEBUG");

        // You can try any other equation by modifying the above
    }
}
