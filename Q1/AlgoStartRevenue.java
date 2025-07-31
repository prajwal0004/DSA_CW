import java.util.*;

public class AlgoStartRevenue {

    static class Project {
        int investment;
        int revenue;

        Project(int investment, int revenue) {
            this.investment = investment;
            this.revenue = revenue;
        }
    }

    public static int maximizeCapital(int k, int c, int[] revenues, int[] investments) {
        int n = revenues.length;
        List<Project> projects = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            projects.add(new Project(investments[i], revenues[i]));
        }

        // Sort projects by investment needed (ascending)
        projects.sort(Comparator.comparingInt(p -> p.investment));

        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        int index = 0;

        for (int i = 0; i < k; i++) {
            // Add all affordable projects to the heap
            while (index < n && projects.get(index).investment <= c) {
                maxHeap.offer(projects.get(index).revenue);
                index++;
            }

            if (!maxHeap.isEmpty()) {
                c += maxHeap.poll();
            } else {
                break;
            }
        }

        return c;
    }

    public static void main(String[] args) {
        // Test Case 1
        int k1 = 2, c1 = 0;
        int[] revenues1 = {2, 5, 8};
        int[] investments1 = {0, 2, 3};
        System.out.println("Max Capital (Example 1): " + maximizeCapital(k1, c1, revenues1, investments1));  // 7

        // Test Case 2
        int k2 = 3, c2 = 1;
        int[] revenues2 = {3, 6, 10};
        int[] investments2 = {1, 3, 5};
        System.out.println("Max Capital (Example 2): " + maximizeCapital(k2, c2, revenues2, investments2));  // 19
    }
}
