import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;

public class QuizPanel extends JPanel {
    private Chapter chapter;
    private GameFrame frame;
    private int chapterIndex;
    private int current = 0;
    private int correct = 0;
    private JLabel timerLabel = new JLabel("Temps: 20");
    private Timer timer = new Timer();
    private int timeLeft = 20;
    private JButton submitButton;
    private JRadioButton[] buttons;

    public QuizPanel(Chapter chapter, GameFrame frame, int chapterIndex) {
        this.chapter = chapter;
        this.frame = frame;
        this.chapterIndex = chapterIndex;
        setLayout(new BorderLayout());
        
        // Shuffle questions AND their options
        chapter.shuffleQuestions();
        for (Question q : chapter.questions) {
            q.shuffleOptions(); // Shuffle each question's options
        }

        add(timerLabel, BorderLayout.NORTH);
        showQuestion();
        
        // Enable focus for keyboard input
        setFocusable(true);
        requestFocusInWindow();
    }

    private void showQuestion() {
        // Cancel previous timer
        timer.cancel();
        timer = new Timer();
        
        removeAll();
        if (current >= chapter.questions.size()) {
            showResult();
            return;
        }

        // Reset timer to 20 seconds
        timeLeft = 20;
        timerLabel = new JLabel("Temps: " + timeLeft);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timerLabel.setForeground(Color.RED);
        
        Question q = chapter.questions.get(current);
        JPanel qPanel = new JPanel(new GridLayout(0, 1));
        
        // Question number indicator
        JLabel questionNumber = new JLabel("Question " + (current + 1) + "/" + chapter.questions.size());
        questionNumber.setFont(new Font("Arial", Font.BOLD, 14));
        questionNumber.setForeground(new Color(63, 81, 181));
        qPanel.add(questionNumber);
        
        JLabel questionLabel = new JLabel("<html><body style='width: 500px;'>" + q.text + "</body></html>");
        questionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        qPanel.add(questionLabel);

        ButtonGroup group = new ButtonGroup();
        buttons = new JRadioButton[q.options.length];
        for (int i = 0; i < q.options.length; i++) {
            buttons[i] = new JRadioButton("<html><body style='width: 500px;'>" + q.options[i] + "</body></html>");
            buttons[i].setFont(new Font("Arial", Font.PLAIN, 12));
            group.add(buttons[i]);
            qPanel.add(buttons[i]);
        }

        submitButton = new JButton("Soumettre");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setBackground(new Color(76, 175, 80));
        submitButton.setForeground(Color.WHITE);
        submitButton.addActionListener(e -> submitAnswer(q));
        
        // Add KeyListener for Enter key
        KeyListener enterKeyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    submitButton.doClick();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {}
        };
        
        addKeyListener(enterKeyListener);
        for (JRadioButton btn : buttons) {
            btn.addKeyListener(enterKeyListener);
        }
        submitButton.addKeyListener(enterKeyListener);

        // Timer for this question
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                timeLeft--;
                SwingUtilities.invokeLater(() -> {
                    timerLabel.setText("Temps: " + timeLeft);
                    if (timeLeft <= 5) {
                        timerLabel.setForeground(Color.RED);
                    } else if (timeLeft <= 10) {
                        timerLabel.setForeground(Color.ORANGE);
                    } else {
                        timerLabel.setForeground(new Color(76, 175, 80));
                    }
                });
                if (timeLeft <= 0) {
                    timer.cancel();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(QuizPanel.this, "Temps écoulé !\n" + q.explanation);
                        current++;
                        showQuestion();
                    });
                }
            }
        }, 1000, 1000);

        qPanel.add(submitButton);
        add(timerLabel, BorderLayout.NORTH);
        add(qPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
        
        // Re-enable focus
        requestFocusInWindow();
    }
    
    private void submitAnswer(Question q) {
        timer.cancel();
        boolean answered = false;
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].isSelected()) {
                answered = true;
                if (i == q.correctIndex) {
                    correct++;
                    JOptionPane.showMessageDialog(this, "Correct !\n" + q.explanation);
                } else {
                    JOptionPane.showMessageDialog(this, "Incorrect.\n" + q.explanation);
                }
                current++;
                showQuestion();
                return;
            }
        }
        if (!answered) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une réponse.");
        }
    }

    private void showResult() {
        int score = (int) ((correct * 100.0) / chapter.questions.size());
        frame.setContentPane(new ResultPanel(score, frame, chapterIndex, chapter));
        frame.revalidate();
    }
}