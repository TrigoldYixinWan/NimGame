import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class NimbleGameGUI {
    private JFrame frame;
    private JPanel gamePanel;
    private JTextField userInput;
    private JButton submitMove;
    private int[] squares;
    private Random random = new Random();

    public NimbleGameGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Nimble Game");
        frame.setBounds(100, 100, 800, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        JLabel rulesLabel = new JLabel("<html><body><strong>Game Rules:</strong> " +
                "Move a coin to a higher numbered square. " +
                "<br/>Winning Strategy: Make the last move!</body></html>",
                SwingConstants.CENTER);
        frame.getContentPane().add(rulesLabel, BorderLayout.CENTER);

        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGame(g);
            }
        };
        gamePanel.setLayout(new FlowLayout());

        JPanel difficultyPanel = new JPanel();
        frame.getContentPane().add(difficultyPanel, BorderLayout.NORTH);

        String[] difficulties = {"Easy", "Medium", "Difficult"};
        for (String difficulty : difficulties) {
            JButton difficultyButton = new JButton(difficulty);
            difficultyButton.addActionListener(e -> {
                startGame(difficulty);
                frame.getContentPane().remove(rulesLabel);
                frame.getContentPane().add(gamePanel, BorderLayout.CENTER);
                frame.revalidate();
                frame.repaint();
            });
            difficultyPanel.add(difficultyButton);
        }

        JPanel controlPanel = new JPanel();
        frame.getContentPane().add(controlPanel, BorderLayout.SOUTH);

        JLabel inputFormatLabel = new JLabel("Format: [from] + [space] + [to]. ");
        controlPanel.add(inputFormatLabel);

        userInput = new JTextField();
        userInput.setColumns(10);
        controlPanel.add(userInput);

        submitMove = new JButton("Submit Move");
        submitMove.addActionListener((ActionListener) new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processUserMove();
            }
        });
        controlPanel.add(submitMove);

        frame.setVisible(true);
    }

    private void startGame(String difficulty) {
        int squareCount, maxCoins;
        if ("Easy".equals(difficulty)) {
            squareCount = 3 + random.nextInt(3); // 3-5 squares
            maxCoins = 5;
        } else if ("Medium".equals(difficulty)) {
            squareCount = 5 + random.nextInt(3); // 5-7 squares
            maxCoins = 9;
        } else {
            squareCount = 7 + random.nextInt(4); // 7-10 squares
            maxCoins = 9;
        }

        squares = new int[squareCount];
        for (int i = 0; i < squareCount; i++) {
            squares[i] = difficulty.equals("Difficult") ? 2 + random.nextInt(8) : random.nextInt(maxCoins + 1);
        }
        gamePanel.repaint();
    }

    private void drawGame(Graphics g) {
        if (squares == null) return;

        for (int i = 0; i < squares.length; i++) {
            g.drawRect(80 * i + 10, 50, 70, 70);
            g.drawString("Square " + (i + 1), 80 * i + 20, 45);
            for (int j = 0; j < squares[i]; j++) {
                g.fillOval(80 * i + 20 + (j % 5) * 12, 60 + (j / 5) * 12, 10, 10);
            }
        }
    }

    private void processUserMove() {
        try {
            String[] parts = userInput.getText().split("\\s+");
            int fromSquare = Integer.parseInt(parts[0]) - 1;
            int toSquare = Integer.parseInt(parts[1]) - 1;

            if (isValidMove(fromSquare, toSquare)) {
                makeMove(fromSquare, toSquare);
                gamePanel.repaint();

                if (!isGameOver()) {
                    computerMove();
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid move. Please enter valid square indexes.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Invalid input format. Please enter in the format: [from] [space] [to]");
        }
    }

    private void computerMove() {
        if (isGameOver()) {
            JOptionPane.showMessageDialog(frame, "You win!");
            return;
        }

        int leftMostUnsafeSquare = findLeftMostUnsafeSquare();
        if (leftMostUnsafeSquare != -1) {
            for (int j = leftMostUnsafeSquare + 1; j < squares.length; j++) {
                if (squares[j] < squares.length - j - 1) {
                    makeMove(leftMostUnsafeSquare, j);
                    break;
                }
            }
        } else {
            makeRandomMove();
        }

        gamePanel.repaint();
        if (isGameOver()) {
            JOptionPane.showMessageDialog(frame, "Computer wins!");
        }
    }

    private void makeMove(int fromSquare, int toSquare) {
        squares[fromSquare]--;
        squares[toSquare]++;
        gamePanel.repaint();
    }

    private boolean isValidMove(int fromSquare, int toSquare) {
        return fromSquare < toSquare && fromSquare < squares.length &&
               toSquare < squares.length && squares[fromSquare] > 0;
    }

    private int findLeftMostUnsafeSquare() {
        int totalNimSum = calculateTotalNimSum();
        for (int i = 0; i < squares.length - 1; i++) {
            if ((nimValue(i) ^ totalNimSum) != 0) {
                return i;
            }
        }
        return -1;
    }

    private int calculateTotalNimSum() {
        int nimSum = 0;
        for (int i = 0; i < squares.length; i++) {
            nimSum ^= nimValue(i);
        }
        return nimSum;
    }

    private int nimValue(int index) {
        return squares.length - 1 - index;
    }

    private void makeRandomMove() {
        int fromSquare, toSquare;
        do {
            fromSquare = random.nextInt(squares.length - 1);
            toSquare = fromSquare + 1 + random.nextInt(squares.length - fromSquare - 1);
        } while (squares[fromSquare] == 0);

        makeMove(fromSquare, toSquare);
    }

    private boolean isGameOver() {
        for (int square : squares) {
            if (square > 0) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new NimbleGameGUI());
    }
}
