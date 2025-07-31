import javax.swing.*;
import java.awt.*;

public class MazePanel extends JPanel {
    private final int rows, cols;
    private final int cellSize = 30;
    private int[][] maze;
    private Point start = new Point(0, 0);
    private Point end;
    private boolean solving = false;

    public MazePanel(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.end = new Point(rows - 1, cols - 1);
        generateMaze();

        JButton dfsBtn = new JButton("Solve with DFS");
        JButton bfsBtn = new JButton("Solve with BFS");
        JButton genBtn = new JButton("Generate New Maze");

        dfsBtn.addActionListener(_ -> {
            if (!solving) {
                solving = true;
                new Thread(() -> {
                    MazeSolver.solveDFS(maze, start, end, this);
                    solving = false;
                }).start();
            }
        });

        bfsBtn.addActionListener(_ -> {
            if (!solving) {
                solving = true;
                new Thread(() -> {
                    MazeSolver.solveBFS(maze, start, end, this);
                    solving = false;
                }).start();
            }
        });

        genBtn.addActionListener(_ -> generateMaze());

        add(dfsBtn);
        add(bfsBtn);
        add(genBtn);
    }

    private void generateMaze() {
        this.maze = MazeGenerator.generate(rows, cols);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (maze == null) return;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int val = maze[r][c];
                switch (val) {
                    case 1 -> g.setColor(Color.BLACK);     // Wall
                    case 2 -> g.setColor(Color.CYAN);      // Visited
                    case 3 -> g.setColor(Color.YELLOW);    // Solution path
                    default -> g.setColor(Color.WHITE);    // Path
                }
                g.fillRect(c * cellSize, r * cellSize, cellSize, cellSize);
                g.setColor(Color.GRAY);
                g.drawRect(c * cellSize, r * cellSize, cellSize, cellSize);
            }
        }

        // Draw Start and End
        g.setColor(Color.GREEN);
        g.fillRect(start.y * cellSize, start.x * cellSize, cellSize, cellSize);

        g.setColor(Color.RED);
        g.fillRect(end.y * cellSize, end.x * cellSize, cellSize, cellSize);
    }

    public void updateMaze(int[][] newMaze) {
        this.maze = newMaze;
        repaint();
    }

    public void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }
}
