import java.util.*;

public class SecureTransmission {
    private final int n; // Number of offices (nodes)
    private final List<List<int[]>> graph; // Adjacency list: each node connects to [neighbor, strength]

    // Constructor: builds the graph from given links
    public SecureTransmission(int n, int[][] links) {
        this.n = n;
        graph = new ArrayList<>();
        for (int i = 0; i < n; i++) graph.add(new ArrayList<>());

        // Populate the graph with edges
        for (int[] link : links) {
            int u = link[0], v = link[1], w = link[2];
            graph.get(u).add(new int[]{v, w}); // add edge u -> v
            graph.get(v).add(new int[]{u, w}); // add edge v -> u (undirected)
        }
    }

    // Method to check if message can be transmitted from sender to receiver
    public boolean canTransmit(int sender, int receiver, int maxStrength) {
        boolean[] visited = new boolean[n]; // To track visited offices
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(sender);
        visited[sender] = true;

        // BFS traversal to find a valid path under strength limit
        while (!queue.isEmpty()) {
            int curr = queue.poll();

            if (curr == receiver) return true; // Reached destination

            for (int[] neighbor : graph.get(curr)) {
                int next = neighbor[0], strength = neighbor[1];

                // Proceed only if link is under the maxStrength and not visited yet
                if (!visited[next] && strength < maxStrength) {
                    visited[next] = true;
                    queue.offer(next);
                }
            }
        }

        return false; // No valid path found
    }

    // Main method to test the implementation
    public static void main(String[] args) {
        int[][] links = {
            {0, 2, 4},
            {2, 3, 1},
            {2, 1, 3},
            {4, 5, 5},
            {3, 0, 2} // This was shown in the image, adds extra path
        };

        SecureTransmission st = new SecureTransmission(6, links);

        // Test cases
        System.out.println("Can transmit 2 → 3 with strength < 2: " + st.canTransmit(2, 3, 2)); // true
        System.out.println("Can transmit 1 → 3 with strength < 3: " + st.canTransmit(1, 3, 3)); // false
        System.out.println("Can transmit 2 → 0 with strength < 3: " + st.canTransmit(2, 0, 3)); // true (via 3)
        System.out.println("Can transmit 0 → 5 with strength < 6: " + st.canTransmit(0, 5, 6)); // false
    }
}
