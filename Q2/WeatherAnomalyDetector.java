import java.util.*;

public class WeatherAnomalyDetector {

    // Main function to count valid subarrays with sum in range [low, high]
    public static int countTemperatureAnomalies(int[] temp, int low, int high) {
        int n = temp.length;

        // Step 1: Compute prefix sums
        // prefix[i] = sum of elements temp[0] to temp[i-1]
        long[] prefix = new long[n + 1];
        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + temp[i];
        }

        // Step 2: Use divide and conquer (modified merge sort) to count valid ranges
        return countRangeSum(prefix, 0, n + 1, low, high);
    }

    // Recursive function to count valid subarray sums in [low, high] using prefix sums
    private static int countRangeSum(long[] prefix, int left, int right, int low, int high) {
        if (right - left <= 1) return 0;  // Base case: 1 or no element

        int mid = (left + right) / 2;

        // Count in left half + right half
        int count = countRangeSum(prefix, left, mid, low, high)
                  + countRangeSum(prefix, mid, right, low, high);

        int start = mid, end = mid;

        // For every prefix[i] in left half, count how many prefix[j] in right half
        // satisfy: low ≤ prefix[j] - prefix[i] ≤ high
        for (int i = left; i < mid; i++) {
            // Move start pointer to find the first index j where (prefix[j] - prefix[i]) >= low
            while (start < right && prefix[start] - prefix[i] < low) start++;
            // Move end pointer to find the first index j where (prefix[j] - prefix[i]) > high
            while (end < right && prefix[end] - prefix[i] <= high) end++;
            count += end - start;
        }

        // Merge the two sorted halves of prefix[] (standard merge step of merge sort)
        List<Long> merged = new ArrayList<>();
        int i = left, j = mid;
        while (i < mid && j < right) {
            if (prefix[i] <= prefix[j]) {
                merged.add(prefix[i++]);
            } else {
                merged.add(prefix[j++]);
            }
        }
        // Add remaining elements
        while (i < mid) merged.add(prefix[i++]);
        while (j < right) merged.add(prefix[j++]);

        // Copy merged elements back into original prefix array
        for (int k = 0; k < merged.size(); k++) {
            prefix[left + k] = merged.get(k);
        }

        return count;
    }

    // Example usage
    public static void main(String[] args) {
        int[] changes1 = {3, -1, -4, 6, 2};
        int low1 = 2, high1 = 5;
        System.out.println("Valid periods (Example 1): " +
                countTemperatureAnomalies(changes1, low1, high1));  // Output: 3

        int[] changes2 = {-2, 3, 1, -5, 4};
        int low2 = -1, high2 = 2;
        System.out.println("Valid periods (Example 2): " +
                countTemperatureAnomalies(changes2, low2, high2));  // Output: 5
    }
}
