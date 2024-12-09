package pr2mapAgent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.border.LineBorder;
import java.util.Arrays;
import java.util.Scanner;

public class GridLayoutManager extends JFrame implements PositionListener {

    private JPanel gridPanel;
    private CellPanel[][] squares;
    private int height;
    private int width;
    private Environment env;
    private Map map;

    private BufferedImage grassImage;
    private BufferedImage raccoonImage;
    private BufferedImage obstacleImage; // Image of obstacles (walls)
    private BufferedImage endImage; // Image of the target

    private boolean endSet = false;
    private boolean startSet = false;
    private boolean positionsSet = false;
    private int[] startPos = new int[2];
    private int[] endPos = new int[2];  // Target position

    public GridLayoutManager(Map map) {
        super("GUI GridLayout Manager");

        this.env = env;
        this.map = map;

        // Load images for cells
        try {
            grassImage = ImageIO.read(new File("grass.png"));
            raccoonImage = ImageIO.read(new File("raccoon2.png"));
            obstacleImage = ImageIO.read(new File("wall.png"));
            endImage = ImageIO.read(new File("stink.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        width = map.getWidth();
        height = map.getHeight();

        gridPanel = new JPanel(new GridLayout(height, width));
        squares = new CellPanel[height][width];

        createCells();
        add(gridPanel, BorderLayout.CENTER);

        // Add Reset and Start buttons in the south
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Panel with horizontal layout

        // Add Reset button
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> resetPositions());
        resetButton.setPreferredSize(new Dimension(300, 40));
        buttonPanel.add(resetButton); // Add Reset button to the panel

        // Add Start button
        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> startRunning());
        startButton.setPreferredSize(new Dimension(300, 40));
        buttonPanel.add(startButton); // Add Start button to the panel

        // Add the button panel to the south
        add(buttonPanel, BorderLayout.SOUTH);

        // Finalize JFrame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800); // Adjust size as needed
        setVisible(true);
    }

    private void startRunning() {
        positionsSet = true;
    }


    public boolean positionsSet() {
        return positionsSet;
    }

    public int[] getStartPos() {
        return startPos;
    }

    public int[] getEndPos() {
        return endPos;
    }

    private void createCells() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                squares[i][j] = new CellPanel(i, j);
                squares[i][j].setBorder(new LineBorder(Color.BLACK, 1));
                setCellStyle(i, j); // Set initial cell style
                gridPanel.add(squares[i][j]);
            }
        }
    }

    private void setCellStyle(int i, int j) {
        // Set obstacle or free cell based on matrix value
        if (map.getMatrix()[i][j] == -1) {
            squares[i][j].setBackground(Color.RED);  // Mark obstacle
        } else {
            squares[i][j].setBackground(Color.WHITE);  // Mark free cell
        }
    }

    public void onPositionUpdated(int[] oldPos, int[] currentPos, boolean targetReached, int energy) {
        if (!startSet || !endSet) {
            JOptionPane.showMessageDialog(this, "Please set both start and target positions.");
            return;
        }

            if (targetReached) {
                squares[currentPos[1]][currentPos[0]].setTarget(false);
                squares[oldPos[1]][oldPos[0]].setRaccoon(false);
                squares[currentPos[1]][currentPos[0]].setRaccoon(true);
                gridPanel.repaint();
                JOptionPane.showMessageDialog(null, "Raccoon reached the target with an energy of " + energy + ".");

            } else if (!Arrays.equals(oldPos, currentPos)) {
                System.out.println("Agent moved from [" + oldPos[0] + ", " + oldPos[1] + "] to [" + currentPos[0] + ", " + currentPos[1] + "]");
                squares[oldPos[1]][oldPos[0]].setRaccoon(false);
                squares[currentPos[1]][currentPos[0]].setRaccoon(true);
                gridPanel.repaint();
            }
    }

    private void resetPositions() {
        startSet = false;
        endSet = false;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                squares[i][j].setRaccoon(false);
                squares[i][j].setTarget(false);
            }
        }
        gridPanel.repaint();
    }

    private class CellPanel extends JPanel {
        private int row, col;
        private boolean hasRaccoon = false;
        private boolean hasTarget = false;

        public CellPanel(int row, int col) {
            this.row = row;
            this.col = col;

            // Mouse listener to set start or target position
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (map.getMatrix()[row][col] == -1) {
                        JOptionPane.showMessageDialog(null, "Obstacle cell. Cannot place raccoon or target here.");
                        return;
                    }
                    // Set start position if not set
                    if (!startSet) {
                        startPos[0] = col;
                        startPos[1] = row;
                        hasRaccoon = true;
                        startSet = true;
                    }
                    // Set target position if not set and it's not the start position
                    else if (!endSet) {
                        if (row == startPos[0] && col == startPos[1]) {
                            JOptionPane.showMessageDialog(null, "The target position cannot be the same as the start position.");
                        } else {
                            endPos[0] = col;
                            endPos[1] = row;
                            hasTarget = true;
                            endSet = true;
                        }
                    }
                    gridPanel.repaint();
                }
            });
        }

        public void setRaccoon(boolean hasRaccoon) {
            this.hasRaccoon = hasRaccoon;
        }

        public void setTarget(boolean hasTarget) {
            this.hasTarget = hasTarget;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Draw appropriate image based on cell content
            if (map.getMatrix()[row][col] == -1) {
                g.drawImage(obstacleImage, 0, 0, getWidth(), getHeight(), null);
            } else {
                g.drawImage(grassImage, 0, 0, getWidth(), getHeight(), null);
            }
            if (hasRaccoon) {
                g.drawImage(raccoonImage, 0, 0, getWidth(), getHeight(), null);
            }
            if (hasTarget) {
                g.drawImage(endImage, 0, 0, getWidth(), getHeight(), null);
            }
        }
    }
}
