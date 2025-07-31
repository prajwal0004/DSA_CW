

public class MagicalWordMaxProduct {

    /**
     * Returns the maximum power (product of lengths) obtainable from two
     * non-overlapping odd-length palindromic substrings of M.
     */
    public static long maxMagicalProduct(String M) {
        int n = M.length();
        if (n < 2) return 0;              // need two substrings

        char[] s = M.toCharArray();

        /* -----------------------------------------------------------
         * 1. Manacher’s algorithm for odd palindromes
         *    rad[i] = radius of odd palindrome centred at i (inclusive)
         *             ⇒ palindrome length = 2*rad[i] + 1
         * ----------------------------------------------------------- */
        int[] rad = new int[n];
        int centre = 0, right = -1;
        for (int i = 0; i < n; i++) {
            int r = 0;
            if (i <= right) {
                int mirror = 2 * centre - i;
                r = Math.min(rad[mirror], right - i);
            }
            // try to expand further
            while (i - r - 1 >= 0 && i + r + 1 < n && s[i - r - 1] == s[i + r + 1])
                r++;
            rad[i] = r;
            if (i + r > right) {
                centre = i;
                right  = i + r;
            }
        }

        /* -----------------------------------------------------------
         * 2. Fill bestEndingAt[] and bestStartingAt[]
         * ----------------------------------------------------------- */
        int[] bestEndingAt   = new int[n];         // default 0
        int[] bestStartingAt = new int[n];

        for (int c = 0; c < n; c++) {
            int r = rad[c];
            if (r == 0) {                 // single letter, still magical
                bestEndingAt[c]   = Math.max(bestEndingAt[c]  , 1);
                bestStartingAt[c] = Math.max(bestStartingAt[c], 1);
            } else {
                int len = 2 * r + 1;
                int start = c - r;
                int end   = c + r;
                bestEndingAt[end]     = Math.max(bestEndingAt[end]    , len);
                bestStartingAt[start] = Math.max(bestStartingAt[start], len);
            }
        }

        /* -----------------------------------------------------------
         * 3. Prefix and suffix maxima
         * ----------------------------------------------------------- */
        int[] prefixMax = new int[n];
        int[] suffixMax = new int[n];

        prefixMax[0] = bestEndingAt[0];
        for (int i = 1; i < n; i++)
            prefixMax[i] = Math.max(prefixMax[i - 1], bestEndingAt[i]);

        suffixMax[n - 1] = bestStartingAt[n - 1];
        for (int i = n - 2; i >= 0; i--)
            suffixMax[i] = Math.max(suffixMax[i + 1], bestStartingAt[i]);

        /* -----------------------------------------------------------
         * 4. Evaluate every split point
         * ----------------------------------------------------------- */
        long bestProduct = 0;
        for (int i = 0; i < n - 1; i++) {          // split between i and i+1
            long left  = prefixMax[i];
            long rightSide = suffixMax[i + 1];
            bestProduct = Math.max(bestProduct, left * rightSide);
        }
        return bestProduct;
    }

    // -------------------- Demo --------------------
    public static void main(String[] args) {
        System.out.println(maxMagicalProduct("xyzyxabc"));          // 5
        System.out.println(maxMagicalProduct("levelwowracecar"));   // 35
        System.out.println(maxMagicalProduct("aaaaa"));             // 9 (aaa & aaa overlap? choose "aaa" (3) and "a"(1) product 3)
    }
}
