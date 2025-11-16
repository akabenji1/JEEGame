import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class GameFrame extends JFrame {
    private List<Chapter> chapters = new ArrayList<>();
    private boolean[] unlocked;
    private String playerName;
    private PlayerDataManager dataManager = new PlayerDataManager();

    private static final int TOTAL_CHAPTERS = 13;

    public GameFrame(String playerName, boolean[] initialUnlocked) {
        this.playerName = playerName;
        this.unlocked = initialUnlocked;
        
        setTitle("Quête Maîtrise JEE - " + playerName);
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        buildChapterListAndUI();
        setVisible(true);
    }

    private void buildChapterListAndUI() {
        chapters.clear();

        chapters.add(new Chapter1());
        chapters.add(new Chapter2());
        chapters.add(new Chapter3());
        chapters.add(new Chapter4());
        chapters.add(new Chapter5());
        chapters.add(new Chapter6());
        chapters.add(new Chapter7());
        chapters.add(new Chapter8());
        chapters.add(new Chapter9());
        chapters.add(new Chapter10());
        chapters.add(new Chapter11());
        chapters.add(new Chapter12());
        chapters.add(new Chapter13());

        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Title panel with player name
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(63, 81, 181));
        
        JLabel title = new JLabel("🎓 Quête Maîtrise JEE", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        
        JLabel playerLabel = new JLabel("Joueur: " + playerName, SwingConstants.RIGHT);
        playerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        playerLabel.setForeground(Color.WHITE);
        playerLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        titlePanel.add(title, BorderLayout.CENTER);
        titlePanel.add(playerLabel, BorderLayout.EAST);
        
        // Chapters panel with scroll
        JPanel panel = new JPanel(new GridLayout(chapters.size(), 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        for (int i = 0; i < chapters.size(); i++) {
            Chapter chapter = chapters.get(i);
            if (i < unlocked.length) {
                chapter.unlocked = unlocked[i];
            }

            JButton btn = new JButton();
            
            if (unlocked[i]) {
                btn.setText("✓ " + chapter.title);
                btn.setBackground(new Color(76, 175, 80));
                btn.setForeground(Color.WHITE);
            } else {
                btn.setText("🔒 " + chapter.title);
                btn.setBackground(new Color(189, 189, 189));
                btn.setForeground(Color.DARK_GRAY);
            }
            
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            
            btn.setEnabled(unlocked[i]);
            
            int finalI = i;
            btn.addActionListener(e -> setContentPane(new QuizPanel(chapters.get(finalI), this, finalI)));
            panel.add(btn);
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
        revalidate();
        repaint();
    }

    public void refresh() {
        getContentPane().removeAll();
        buildChapterListAndUI();
    }
    
    public void unlockNextChapter(int currentChapterIndex) {
        if (currentChapterIndex + 1 < unlocked.length) {
            unlocked[currentChapterIndex + 1] = true;
            if (currentChapterIndex + 1 < chapters.size()) {
                chapters.get(currentChapterIndex + 1).unlocked = true;
            }
        }
    }
    
    public void saveProgress(int score) {
        dataManager.savePlayerData(playerName, unlocked, score);
    }
    
    public String getPlayerName() {
        return playerName;
    }
}