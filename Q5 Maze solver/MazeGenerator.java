import java.util.*;

public class MazeGenerator {
    private static final int[] dr = {-1, 1, 0, 0}; // up, down
    private static final int[] dc = {0, 0, -1, 1}; // left, right

    public static int[][] generate(int rows, int cols) {
        int[][] maze = new int[rows][cols];
        for (int[] row : maze) Arrays.fill(row, 1); // initialize all cells as walls

        Random rand = new Random();
        
        // Start DFS from (1,1) to create the main maze structure
        dfs(1, 1, maze, rand);
        
        // Ensure start point (0,0) is connected to the maze
        ensureStartConnected(maze);
        
        // Ensure end point (rows-1, cols-1) is connected to the maze
        ensureEndConnected(maze, rows - 1, cols - 1);
        
        // Verify connectivity and fix if needed
        ensureSolvability(maze, rows, cols);

        return maze;
    }

    private static void dfs(int r, int c, int[][] maze, Random rand) {
        maze[r][c] = 0; // mark as path
        List<Integer> dirs = Arrays.asList(0, 1, 2, 3); // directions
        Collections.shuffle(dirs, rand); // randomize for randomness

        for (int dir : dirs) {
            int nr = r + dr[dir] * 2;
            int nc = c + dc[dir] * 2;

            if (inBounds(nr, nc, maze) && maze[nr][nc] == 1) {
                maze[r + dr[dir]][c + dc[dir]] = 0; // remove wall between
                dfs(nr, nc, maze, rand);
            }
        }
    }

    private static boolean inBounds(int r, int c, int[][] maze) {
        return r > 0 && c > 0 && r < maze.length - 1 && c < maze[0].length - 1;
    }

    // Ensures the start point (0,0) is connected to a path
    private static void ensureStartConnected(int[][] maze) {
        // Check if (0,0) is already connected to a path
        if (maze[0][1] == 0 || maze[1][0] == 0) {
            maze[0][0] = 0; // Make start point a path
            return;
        }
        
        // If not connected, connect it to the nearest path
        if (maze[1][0] == 1) {
            maze[0][0] = 0; // Make start point a path
            maze[1][0] = 0; // Connect to the path below
        } else if (maze[0][1] == 1) {
            maze[0][0] = 0; // Make start point a path
            maze[0][1] = 0; // Connect to the path to the right
        }
    }

    // Ensures the end point is connected to a path
    private static void ensureEndConnected(int[][] maze, int r, int c) {
        // Check if end point is already connected to a path
        for (int i = 0; i < 4; i++) {
            int nr = r + dr[i];
            int nc = c + dc[i];
            if (nr >= 0 && nc >= 0 && nr < maze.length && nc < maze[0].length) {
                if (maze[nr][nc] == 0) {
                    maze[r][c] = 0; // Make end point a path
                    return; // already connected
                }
            }
        }
        
        // Not connected: connect to the nearest path
        if (r > 0 && maze[r - 1][c] == 0) {
            maze[r][c] = 0; // Make end point a path
        } else if (c > 0 && maze[r][c - 1] == 0) {
            maze[r][c] = 0; // Make end point a path
        } else {
            // If no adjacent path, create a path to the nearest accessible cell
            if (r > 0) {
                maze[r][c] = 0;
                maze[r - 1][c] = 0;
            } else if (c > 0) {
                maze[r][c] = 0;
                maze[r][c - 1] = 0;
            }
        }
    }

    // Ensures the maze is solvable by checking connectivity
    private static void ensureSolvability(int[][] maze, int rows, int cols) {
        // Use BFS to check if start and end are connected
        boolean[][] visited = new boolean[rows][cols];
        Queue<Point> queue = new LinkedList<>();
        
        // Start from (0,0)
        queue.add(new Point(0, 0));
        visited[0][0] = true;
        
        while (!queue.isEmpty()) {
            Point current = queue.poll();
            
            // Check all 4 directions
            for (int i = 0; i < 4; i++) {
                int nr = current.x + dr[i];
                int nc = current.y + dc[i];
                
                if (nr >= 0 && nc >= 0 && nr < rows && nc < cols && 
                    !visited[nr][nc] && maze[nr][nc] == 0) {
                    visited[nr][nc] = true;
                    queue.add(new Point(nr, nc));
                }
            }
        }
        
        // If end point is not reachable, create a direct path
        if (!visited[rows - 1][cols - 1]) {
            createDirectPath(maze, rows, cols);
        }
    }
    
    // Creates a direct path from start to end if needed
    private static void createDirectPath(int[][] maze, int rows, int cols) {
        // Create a simple path along the edges
        for (int i = 0; i < rows; i++) {
            maze[i][0] = 0; // Left edge
        }
        for (int j = 0; j < cols; j++) {
            maze[rows - 1][j] = 0; // Bottom edge
        }
    }
    
    // Simple Point class for BFS
    private static class Point {
        int x, y;
        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}

