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
            Point current = stack.pop();

            if (current.equals(end)) {
                break;
            }

            for (int i = 0; i < 4; i++) {
                int nr = current.x + dr[i];
                int nc = current.y + dc[i];
                if (valid(nr, nc, maze, visited)) {
                    Point next = new Point(nr, nc);
                    stack.push(next);
                    parent.put(next, current);
                    visited[nr][nc] = true;
                    maze[nr][nc] = 2; // Mark as visited
                    panel.updateMaze(maze);
                    panel.sleep(20);
                }
            }
        }

        markPath(maze, parent, start, end, panel);
    }

    public static void solveBFS(int[][] maze, Point start, Point end, MazePanel panel) {
        Queue<Point> queue = new LinkedList<>();
        boolean[][] visited = new boolean[maze.length][maze[0].length];
        Map<Point, Point> parent = new HashMap<>();

        queue.add(start);
        visited[start.x][start.y] = true;

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            if (current.equals(end)) {
                break;
            }

            for (int i = 0; i < 4; i++) {
                int nr = current.x + dr[i];
                int nc = current.y + dc[i];
                if (valid(nr, nc, maze, visited)) {
                    Point next = new Point(nr, nc);
                    queue.add(next);
                    parent.put(next, current);
                    visited[nr][nc] = true;
                    maze[nr][nc] = 2; // Mark as visited
                    panel.updateMaze(maze);
                    panel.sleep(20);
                }
            }
        }

        markPath(maze, parent, start, end, panel);
    }

    private static void markPath(int[][] maze, Map<Point, Point> parent, Point start, Point end, MazePanel panel) {
        Point current = end;
        while (parent.containsKey(current) && !current.equals(start)) {
            current = parent.get(current);
            if (!current.equals(start)) {
                maze[current.x][current.y] = 3; // Mark as path
                panel.updateMaze(maze);
                panel.sleep(30);
            }
        }
    }

    private static boolean valid(int r, int c, int[][] maze, boolean[][] visited) {
        return r >= 0 && r < maze.length && c >= 0 && c < maze[0].length
                && maze[r][c] == 0 && !visited[r][c];
    }
}
