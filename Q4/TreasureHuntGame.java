import java.util.*;

public class TreasureHuntGame {
    private static final int DRAW = 0;
    private static final int P1_WIN = 1;
    private static final int P2_WIN = 2;

    // Memoization: memo[player1Pos][player2Pos][turn] = result
    private int[][][] memo;
    private List<List<Integer>> graph;
    private int maxTurns;

    public int playGame(List<List<Integer>> graphInput) {
        this.graph = graphInput;
        int n = graph.size();
        memo = new int[n][n][2 * n];
        maxTurns = 2 * n;

        return dfs(1, 2, 0);
    }

    // DFS with memoization
    private int dfs(int p1, int p2, int turn) {
        if (turn >= maxTurns) return DRAW; // cycle detected
        if (p1 == 0) return P1_WIN;        // Player 1 reaches treasure
        if (p1 == p2) return P2_WIN;       // Player 2 catches Player 1
        if (memo[p1][p2][turn] != 0) return memo[p1][p2][turn];

        boolean isP1Turn = (turn % 2 == 0);
        int result;

        if (isP1Turn) {
            // Player 1's turn
            result = P2_WIN; // Assume worst case
            for (int next : graph.get(p1)) {
                int nextResult = dfs(next, p2, turn + 1);
                if (nextResult == P1_WIN) {
                    result = P1_WIN;
                    break;
                } else if (nextResult == DRAW) {
                    result = DRAW;
                }
            }
        } else {
            // Player 2's turn
            result = P1_WIN; // Assume worst case
            for (int next : graph.get(p2)) {
                if (next == 0) continue; // Player 2 can't go to treasure
                int nextResult = dfs(p1, next, turn + 1);
                if (nextResult == P2_WIN) {
                    result = P2_WIN;
                    break;
                } else if (nextResult == DRAW) {
                    result = DRAW;
                }
            }
        }

        memo[p1][p2][turn] = result;
        return result;
    }

    public static void main(String[] args) {
        // Representing the graph
        List<List<Integer>> graph = new ArrayList<>();
        graph.add(Arrays.asList(2, 5));      // 0
        graph.add(Arrays.asList(3));         // 1 (Player 1 start)
        graph.add(Arrays.asList(0, 4, 5));   // 2 (Player 2 start)
        graph.add(Arrays.asList(1, 4, 5));   // 3
        graph.add(Arrays.asList(2, 3));      // 4
        graph.add(Arrays.asList(0, 2, 3));   // 5

        TreasureHuntGame game = new TreasureHuntGame();
        int result = game.playGame(graph);

        System.out.println("Game Result: " + result); // Output: 0 (Draw)
    }
}
