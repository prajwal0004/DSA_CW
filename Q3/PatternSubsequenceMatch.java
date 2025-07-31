public class PatternSubsequenceMatch {

    // Function to calculate max number of times p2*t2 can be extracted from p1*t1
    public static int getMaxMatches(String p1, int t1, String p2, int t2) {
        int count = 0;      // Count how many full p2 patterns matched
        int p2Index = 0;    // Pointer for p2

        // Repeat p1 t1 times
        for (int i = 0; i < t1; i++) {
            for (int j = 0; j < p1.length(); j++) {
                // Match current character of p1 with p2
                if (p1.charAt(j) == p2.charAt(p2Index)) {
                    p2Index++;
                    // If full p2 matched, increment count and reset pointer
                    if (p2Index == p2.length()) {
                        count++;
                        p2Index = 0;
                    }
                }
            }
        }

        // Return how many full p2*t2 patterns can be made
        return count / t2;
    }

    public static void main(String[] args) {
        // Test input
        String p1 = "bca";
        int t1 = 6;
        String p2 = "ba";
        int t2 = 3;

        int result = getMaxMatches(p1, t1, p2, t2);
        System.out.println("Output: " + result); // Expected: 3
    }
}

