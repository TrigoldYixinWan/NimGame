
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class NimGameGUI {
    private JFrame frame;
    private JTextArea gameArea;
    private JTextField userInput;
    private JButton submitMove;
    private int[] heaps;
    private Random random;
    private boolean isUserTurn = true; // Player first

    public NimGameGUI() {
        initialize();
        random = new Random();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 500, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));

        gameArea = new JTextArea();
        gameArea.setEditable(false);
        frame.getContentPane().add(new JScrollPane(gameArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        
        JLabel inputFormatLabel = new JLabel("Format: [from] + [space] + [to]. ");
        bottomPanel.add(inputFormatLabel);
        
        
        userInput = new JTextField();
        bottomPanel.add(userInput);
        userInput.setColumns(10);

        
        submitMove = new JButton("Submit Move");
        submitMove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processUserMove();
            }
        });
        bottomPanel.add(submitMove);

        JPanel topPanel = new JPanel();
        frame.getContentPane().add(topPanel, BorderLayout.NORTH);

        JButton btnEasy = new JButton("Easy");
        btnEasy.addActionListener(e -> startGame("easy"));
        topPanel.add(btnEasy);

        JButton btnMedium = new JButton("Medium");
        btnMedium.addActionListener(e -> startGame("medium"));
        topPanel.add(btnMedium);

        JButton btnDifficult = new JButton("Difficult");
        btnDifficult.addActionListener(e -> startGame("difficult"));
        topPanel.add(btnDifficult);

        frame.setVisible(true);
    }

    private void startGame(String difficulty) {
        int numHeaps = difficulty.equals("easy") ? 3 + random.nextInt(3) : // difficulties
                        difficulty.equals("medium") ? 10 + random.nextInt(11) : 
                        30 + random.nextInt(21); 

        int maxCoins = difficulty.equals("easy") ? 10 : difficulty.equals("medium") ? 20 : 50;

        heaps = new int[numHeaps];
        for (int i = 0; i < numHeaps; i++) {
            heaps[i] = 1 + random.nextInt(maxCoins);
        }

        updateGameArea();
        isUserTurn = true;
    }

    private void updateGameArea() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < heaps.length; i++) {
            sb.append("Heap ").append(i + 1).append(": ").append(heaps[i]).append("\n");
        }
        gameArea.setText(sb.toString());
    }

    private void processUserMove() {
        if (!isUserTurn || heaps == null) {
            return;
        }

        try {
            String[] parts = userInput.getText().split("\\s+");
            int heapIndex = Integer.parseInt(parts[0]) - 1;
            int objectsToRemove = Integer.parseInt(parts[1]);

            if (heapIndex >= 0 && heapIndex < heaps.length && objectsToRemove > 0 && objectsToRemove <= heaps[heapIndex]) {
                heaps[heapIndex] -= objectsToRemove;
                isUserTurn = false;
                updateGameArea();
                checkGameOver();
                computerMove();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid move, try again.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Invalid input, format: 'heap number'+ [space] +'objects to remove'");
        }
    }

    private void computerMove() {
        if (isGameOver()) {
            return;
        }

        int nimSum = calculateNimSum();
        for (int i = 0; i < heaps.length; i++) {
            int heapXOR = heaps[i] ^ nimSum;
            if (heapXOR < heaps[i]) {
                heaps[i] -= (heaps[i] - heapXOR);
                break;
            }
        }

        isUserTurn = true;
        updateGameArea();
        checkGameOver();
    }

    private int calculateNimSum() {
        int nimSum = 0;
        for (int heap : heaps) {
            nimSum ^= heap;
        }
        return nimSum;
    }

    private boolean isGameOver() {
        for (int heap : heaps) {
            if (heap > 0) {
                return false;
            }
        }
        return true;
    }

    private void checkGameOver() {
        if (isGameOver()) {
            String winner = isUserTurn ? "Computer wins!" : "You win!";
            JOptionPane.showMessageDialog(frame, winner);
        }
    }

    
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                NimGameGUI window = new NimGameGUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
