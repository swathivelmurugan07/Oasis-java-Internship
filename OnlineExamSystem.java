import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class OnlineExamSystem extends JFrame {
    // 1. STATE & DATA (User and Exam)
    private String userId = "admin";
    private String password = "123";
    private int currentQ = 0;
    private int score = 0;
    private int timeLeft = 60; // 60 seconds total

    // The standardized MCQ structure: [Question, Opt A, Opt B, Opt C, Opt D, CorrectChar]
    private String[][] questions = {
        {"Which company developed Java?", "Sun Microsystems", "Microsoft", "Apple", "Google", "A"},
        {"Which keyword is used for inheritance?", "extends", "implements", "this", "super", "A"},
        {"What is the size of int in Java?", "16-bit", "32-bit", "64-bit", "8-bit", "B"}
    };

    // 2. UI COMPONENTS (referenced globally for updates)
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    private JLabel timerLabel = new JLabel("Time Remaining: 60s", SwingConstants.CENTER);
    private JLabel qLabel = new JLabel("", SwingConstants.CENTER);
    private JRadioButton[] options = new JRadioButton[4];
    private ButtonGroup bg = new ButtonGroup();
    private Timer examTimer;

    public OnlineExamSystem() {
        // Standard JFrame setup
        setTitle("Online Examination System");
        setSize(550, 450); // Increased slightly for better layout flow
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centers window on screen

        // Build the three screens and add them to the CardLayout hub
        mainPanel.add(createLoginPanel(), "login");
        mainPanel.add(createMenuPanel(), "menu");
        mainPanel.add(createExamPanel(), "exam");
        
        add(mainPanel);
        setVisible(true);
    }

    // --- Login Layout ---
    private JPanel createLoginPanel() {
        // We use a clean BorderLayout, then nest a compact form inside it
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(60, 80, 60, 80));

        // The centered title
        JLabel titleLabel = new JLabel("Login to System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Nested central panel just for the labels and fields
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();

        // ADDED LABELS 
        formPanel.add(new JLabel("Username:", SwingConstants.LEFT));
        formPanel.add(userField);
        formPanel.add(new JLabel("Password:", SwingConstants.LEFT));
        formPanel.add(passField);
        
        panel.add(formPanel, BorderLayout.CENTER);

        // The large, consistent login button
        JButton loginBtn = new JButton("Login");
        loginBtn.setPreferredSize(new Dimension(0, 40));
        loginBtn.addActionListener(e -> handleLogin(userField.getText(), new String(passField.getPassword())));
        panel.add(loginBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(70, 100, 70, 100));

        JButton updateBtn = new JButton("Update Password");
        JButton startBtn = new JButton("Start Exam");
        JButton logoutBtn = new JButton("Logout");

        // Simple lambda action listeners for the menu
        updateBtn.addActionListener(e -> {
            String newPass = JOptionPane.showInputDialog(this, "Enter New Password:");
            if (newPass != null && !newPass.isEmpty()) password = newPass;
        });
        startBtn.addActionListener(e -> startExam());
        logoutBtn.addActionListener(e -> System.exit(0));

        panel.add(updateBtn); panel.add(startBtn); panel.add(logoutBtn);
        return panel;
    }

    // --- Exam Layout ---
    private JPanel createExamPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Timer is locked to the top
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        panel.add(timerLabel, BorderLayout.NORTH);

        // Center Panel holds both the Question Label and the Options Panel
        JPanel centerPanel = new JPanel(new BorderLayout(10, 20));

        // Question Label setup (styled)
        qLabel.setFont(new Font("Arial", Font.BOLD, 15));
        // ADDED POSITIONING: Question forced to top of center
        centerPanel.add(qLabel, BorderLayout.NORTH);

        // Options Panel setup (GridLayout for clean alignment)
        JPanel optPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        for(int i=0; i<4; i++) {
            options[i] = new JRadioButton();
            bg.add(options[i]); // Ensures only one is selectable
            optPanel.add(options[i]);
        }
        // Options forced below the question
        centerPanel.add(optPanel, BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);

        // Consistent Submit/Next button at bottom
        JButton nextBtn = new JButton("Next / Submit");
        nextBtn.setPreferredSize(new Dimension(0, 40));
        nextBtn.addActionListener(e -> handleNextQuestion());
        panel.add(nextBtn, BorderLayout.SOUTH);

        return panel;
    }

    // --- LOGIC HELPER METHODS ---

    private void handleLogin(String user, String pass) {
        if (user.equals(userId) && pass.equals(password)) {
            cardLayout.show(mainPanel, "menu");
        } else {
            // Replicates the dialog from your screenshot
            JOptionPane.showMessageDialog(this, "Invalid Credentials", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startExam() {
        // 1. Initial State Load
        loadQuestion(0);
        cardLayout.show(mainPanel, "exam");
        
        // 2. Timer Setup
        timeLeft = 60; // Reset
        timerLabel.setText("Time Remaining: 60s");
        examTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                timerLabel.setText("Time Remaining: " + timeLeft + "s");
                if (timeLeft <= 0) {
                    examTimer.stop();
                    finishExam("Time's Up! Auto-submitting.");
                }
            }
        });
        examTimer.start();
    }

    private void handleNextQuestion() {
        // Calculate Score
        String correctAnsStr = questions[currentQ][5];
        char correctChar = correctAnsStr.charAt(0);
        int correctIndex = correctChar - 'A'; // 'A'->0, 'B'->1...
        if (options[correctIndex].isSelected()) score++;

        // State Machine for Question Index
        currentQ++;
        if (currentQ < questions.length) {
            loadQuestion(currentQ);
        } else {
            finishExam("Exam Finished!");
        }
    }

    // Essential function to inject data into the dynamic components
    private void loadQuestion(int index) {
        qLabel.setText("Q" + (index + 1) + ": " + questions[index][0]);
        for(int i=0; i<4; i++) {
            options[i].setText(questions[index][i+1]);
        }
        bg.clearSelection(); // Reset radio buttons
    }

    private void finishExam(String statusMessage) {
        if(examTimer != null) examTimer.stop();
        // Uses string formatting for the result display
        String finalResult = String.format("%s\nYour Final Score: %d / %d", 
                                          statusMessage, score, questions.length);
        JOptionPane.showMessageDialog(this, finalResult);
        System.exit(0);
    }

    public static void main(String[] args) {
        // Ensures Swing components are created on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new OnlineExamSystem());
    }
              }
