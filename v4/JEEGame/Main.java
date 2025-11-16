import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            // Create name input dialog
            JDialog nameDialog = new JDialog((Frame) null, "Quête Maîtrise JEE", true);
            nameDialog.setSize(500, 250);
            nameDialog.setLocationRelativeTo(null);
            nameDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            panel.setBackground(new Color(63, 81, 181));
            
            // Title
            JLabel titleLabel = new JLabel("🎓 Bienvenue!", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
            titleLabel.setForeground(Color.WHITE);
            panel.add(titleLabel, BorderLayout.NORTH);
            
            // Center panel with name input
            JPanel centerPanel = new JPanel(new GridLayout(3, 1, 5, 10));
            centerPanel.setBackground(new Color(63, 81, 181));
            
            JLabel promptLabel = new JLabel("Entrez votre nom:", SwingConstants.CENTER);
            promptLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            promptLabel.setForeground(Color.WHITE);
            centerPanel.add(promptLabel);
            
            JTextField nameField = new JTextField(20);
            nameField.setFont(new Font("Arial", Font.PLAIN, 16));
            nameField.setHorizontalAlignment(JTextField.CENTER);
            JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            fieldPanel.setBackground(new Color(63, 81, 181));
            fieldPanel.add(nameField);
            centerPanel.add(fieldPanel);
            
            JLabel infoLabel = new JLabel(" ", SwingConstants.CENTER);
            infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            infoLabel.setForeground(Color.WHITE);
            centerPanel.add(infoLabel);
            
            panel.add(centerPanel, BorderLayout.CENTER);
            
            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            buttonPanel.setBackground(new Color(63, 81, 181));
            
            JButton startButton = new JButton("▶ Commencer");
            startButton.setFont(new Font("Arial", Font.BOLD, 14));
            startButton.setBackground(new Color(76, 175, 80));
            startButton.setForeground(Color.WHITE);
            startButton.setFocusPainted(false);
            
            buttonPanel.add(startButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);
            
            nameDialog.add(panel);
            
            // Handle start button click
            Runnable startGame = () -> {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    infoLabel.setText("⚠ Veuillez entrer un nom valide");
                    infoLabel.setForeground(Color.YELLOW);
                    return;
                }
                
                PlayerDataManager pdm = new PlayerDataManager();
                PlayerDataManager.PlayerData data = pdm.loadPlayerData(name);
                
                if (data != null) {
                    // Existing player
                    String msg = String.format(
                        "Joueur trouvé! Chapitres débloqués: %d/%d\nMeilleur score: %d%%\nContinuer avec ces données?",
                        data.maxUnlockedChapter + 1, 13, data.bestScore
                    );
                    int choice = JOptionPane.showConfirmDialog(nameDialog, msg, 
                        "Joueur Existant", JOptionPane.YES_NO_OPTION);
                    
                    if (choice == JOptionPane.YES_OPTION) {
                        nameDialog.dispose();
                        new GameFrame(name, data.unlockedChapters);
                    } else {
                        infoLabel.setText("Choisissez un autre nom ou cliquez Commencer pour écraser");
                        infoLabel.setForeground(Color.YELLOW);
                    }
                } else {
                    // New player
                    nameDialog.dispose();
                    boolean[] initialUnlocked = new boolean[13];
                    initialUnlocked[0] = true; // Only chapter 1 unlocked
                    new GameFrame(name, initialUnlocked);
                }
            };
            
            startButton.addActionListener(e -> startGame.run());
            nameField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        startGame.run();
                    }
                }
            });
            
            nameDialog.setVisible(true);
            nameField.requestFocus();
        });
    }
}