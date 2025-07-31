import java.awt.*;
import java.util.*;

public class MazeSolver {
    private static final int[] dr = {-1, 1, 0, 0};
    private static final int[] dc = {0, 0, -1, 1};

    public static void solveDFS(int[][] maze, Point start, Point end, MazePanel panel) {
        Stack<Point> stack = new Stack<>();
        boolean[][] visited = new boolean[maze.length][maze[0].length];
        Map<Point, Point> parent = new HashMap<>();

        stack.push(start);
        visited[start.x][start.y] = true;

        while (!stack.isEmpty()) {
            Point p = stack.pop();
            if (p.equals(end)) break;

            for (int i = 0; i < 4; i++) {
                int nr = p.x + dr[i], nc = p.y + dc[i];
                if (valid(nr, nc, maze, visited)) {
                    visited[nr][nc] = true;
                    maze[nr][nc] = 2;
                    parent.put(new Point(nr, nc), p);
                    stack.push(new Point(nr, nc));
                    panel.updateMaze(maze);
                    panel.sleep(30);
                }
            }
        }

        markPath(maze, parent, end, panel);
    }

    public static void solveBFS(int[][] maze, Point start, Point end, MazePanel panel) {
        Queue<Point> queue = new LinkedList<>();
        boolean[][] visited = new boolean[maze.length][maze[0].length];
        Map<Point, Point> parent = new HashMap<>();

        queue.add(start);
        visited[start.x][start.y] = true;

        while (!queue.isEmpty()) {
            Point p = queue.poll();
            if (p.equals(end)) break;

            for (int i = 0; i < 4; i++) {
                int nr = p.x + dr[i], nc = p.y + dc[i];
                if (valid(nr, nc, maze, visited)) {
                    visited[nr][nc] = true;
                    maze[nr][nc] = 2;
                    parent.put(new Point(nr, nc), p);
                    queue.add(new Point(nr, nc));
                    panel.updateMaze(maze);
                    panel.sleep(30);
                }
            }
        }

        markPath(maze, parent, end, panel);
    }

    private static void markPath(int[][] maze, Map<Point, Point> parent, Point end, MazePanel panel) {
        Point current = end;
        while (parent.containsKey(current)) {
            current = parent.get(current);
            maze[current.x][current.y] = 3;
            panel.updateMaze(maze);
            panel.sleep(50);
        }
    }

    private static boolean valid(int r, int c, int[][] maze, boolean[][] visited) {
        return r >= 0 && c >= 0 && r < maze.length && c < maze[0].length &&
               maze[r][c] == 0 && !visited[r][c];
    }
}
