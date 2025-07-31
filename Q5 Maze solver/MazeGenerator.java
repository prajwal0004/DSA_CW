import java.util.*;

public class MazeGenerator {
    private static final int[] dr = {-1, 1, 0, 0};
    private static final int[] dc = {0, 0, -1, 1};

    public static int[][] generate(int rows, int cols) {
        int[][] maze = new int[rows][cols];
        for (int[] row : maze) Arrays.fill(row, 1);

        Random rand = new Random();
        dfs(1, 1, maze, rand);

        maze[0][0] = 0;
        maze[rows - 1][cols - 1] = 0;

        return maze;
    }

    private static void dfs(int r, int c, int[][] maze, Random rand) {
        maze[r][c] = 0;
        List<Integer> dirs = Arrays.asList(0, 1, 2, 3);
        Collections.shuffle(dirs, rand);

        for (int dir : dirs) {
            int nr = r + dr[dir] * 2, nc = c + dc[dir] * 2;
            if (inBounds(nr, nc, maze) && maze[nr][nc] == 1) {
                maze[r + dr[dir]][c + dc[dir]] = 0;
                dfs(nr, nc, maze, rand);
            }
        }
    }

    private static boolean inBounds(int r, int c, int[][] maze) {
        return r > 0 && c > 0 && r < maze.length - 1 && c < maze[0].length - 1;
    }
}
