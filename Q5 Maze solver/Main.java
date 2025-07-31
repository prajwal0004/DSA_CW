import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Maze Solver");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 800);
            frame.setResizable(false);

            MazePanel mazePanel = new MazePanel(20, 20);
            frame.add(mazePanel);

            frame.setVisible(true);
        });
    }
}
