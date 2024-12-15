package pr2mapAgent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.border.LineBorder;
import java.util.Arrays;
import java.util.List;

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
    private List<BufferedImage> stopImages = Arrays.asList();

    private boolean santaSet = false;
    private boolean startSet = false;
    private boolean positionsSet = false;
    private int[] startPos = new int[2];
    private int[] endPos = new int[2];  // Target position

    private JTextArea chatArea; // Chat area for displaying messages

    // List of reindeer names
    private List<String> reindeerNames = Arrays.asList("Dasher", "Dancer", "Prancer", "Vixen", "Comet", "Cupid", "Donner");
    private int reindeerIndex = 0;

    public GridLayoutManager(Map map) {
        super("GUI GridLayout Manager");

        this.env = env;
        this.map = map;

        // Load images for cells
        try {
            grassImage = ImageIO.read(new File("snow.png"));
            raccoonImage = ImageIO.read(new File("agent.png"));
            obstacleImage = ImageIO.read(new File("grass.png"));
            endImage = ImageIO.read(new File("house.jpg"));

            stopImages = Arrays.asList(
                    ImageIO.read(new File("deer.png")),
                    ImageIO.read(new File("deer2.png")),
                    ImageIO.read(new File("deer3.png")),
                    ImageIO.read(new File("deer4.png")),
                    ImageIO.read(new File("deer5.png")),
                    ImageIO.read(new File("deer6.png")),
                    ImageIO.read(new File("deer7.png")),
                    ImageIO.read(new File("deer8.png"))
                    // Add more images as needed
            );
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

        // Add the chat panel to the east
        chatArea = new JTextArea(20, 30);
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setBorder(BorderFactory.createTitledBorder("Chat Messages"));
        add(chatScrollPane, BorderLayout.EAST);

        // Finalize JFrame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800); // Adjust size to accommodate the chat area
        setVisible(true);
    }

    private void startRunning() {
        positionsSet = true;
        appendToChat("Search started!");
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
                squares[i][j].setBorder(new LineBorder(Color.BLACK, 0));
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

    private String getNextReindeerName() {
        if (reindeerIndex < reindeerNames.size()) {
            return reindeerNames.get(reindeerIndex++);
        }
        return "Reached Santa!";
    }

    public void onPositionUpdated(int[] oldPos, int[] currentPos, boolean stopReached, boolean targetReached, int energy) {
        if (!startSet || !santaSet) {
            JOptionPane.showMessageDialog(this, "Please set scout and santa position.");
            return;
        }

        if (targetReached) {
            squares[currentPos[1]][currentPos[0]].setTarget(false);
            squares[oldPos[1]][oldPos[0]].setScout(false);
            squares[currentPos[1]][currentPos[0]].setScout(true);
            gridPanel.repaint();
            appendToChat("Scout reached Santa " + energy);

        } else if (stopReached) {
            squares[oldPos[1]][oldPos[0]].setScout(false);
            squares[currentPos[1]][currentPos[0]].setScout(true);
            squares[currentPos[1]][currentPos[0]].setStopImage(null); // Remove the current stop image
            if (reindeerIndex < stopImages.size()) {
                squares[currentPos[1]][currentPos[0]].setStopImage(stopImages.get(reindeerIndex)); // Show next stop image
            }
            gridPanel.repaint();
            String reindeerName = getNextReindeerName();
            appendToChat("Scout found: " + reindeerName + ". Energy cost: " + energy);

        } else if (!Arrays.equals(oldPos, currentPos)) {
            squares[oldPos[1]][oldPos[0]].setScout(false);
            squares[currentPos[1]][currentPos[0]].setScout(true);
            gridPanel.repaint();
        }
    }

    private void resetPositions() {
        startSet = false;
        santaSet = false;
        reindeerIndex = 0; // Reset the reindeer index
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                squares[i][j].setScout(false);
                squares[i][j].setTarget(false);
                squares[i][j].setStopImage(null); // Clear stop images
            }
        }
        gridPanel.repaint();
        appendToChat("Positions have been reset.");
    }

    public void appendToChat(String message) {
        chatArea.append(message + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    private class CellPanel extends JPanel {
        private int row, col;
        private boolean hasRaccoon = false;
        private boolean hasTarget = false;
        private BufferedImage stopImage = null;

        public CellPanel(int row, int col) {
            this.row = row;
            this.col = col;

            // Mouse listener to set start or target position
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (map.getMatrix()[row][col] == -1) {
                        JOptionPane.showMessageDialog(null, "Obstacle cell. Cannot place scout or santa here.");
                        return;
                    }
                    // Set start position if not set
                    if (!startSet) {
                        startPos[0] = col;
                        startPos[1] = row;
                        hasRaccoon = true;
                        startSet = true;
                        appendToChat("Scout position set to: [" + col + ", " + row + "].");
                    }
                    // Set target position if not set and it's not the start position
                    else if (!santaSet) {
                        if (row == startPos[0] && col == startPos[1]) {
                            JOptionPane.showMessageDialog(null, "The santa position cannot be the same as the scout position.");
                        } else {
                            endPos[0] = col;
                            endPos[1] = row;
                            hasTarget = true;
                            santaSet = true;
                            appendToChat("Santa position set to: [" + col + ", " + row + "].");
                        }
                    }
                    gridPanel.repaint();
                }
            });
        }

        public void setScout(boolean hasRaccoon) {
            this.hasRaccoon = hasRaccoon;
        }

        public void setTarget(boolean hasTarget) {
            this.hasTarget = hasTarget;
        }

        public void setStopImage(BufferedImage stopImage) {
            this.stopImage = stopImage;
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
            if (stopImage != null) {
                g.drawImage(stopImage, 0, 0, getWidth(), getHeight(), null);
            }
        }
    }
}
