import javax.swing.*;
import java.awt.*;

/**
 * ResultPanel - displays score and automatically saves progress
 */
public class ResultPanel extends JPanel {

    public ResultPanel(int score, GameFrame frame, int chapterIndex, Chapter chapter) {
        setLayout(new BorderLayout());

        // Automatically save progress
        frame.saveProgress(score);
        
        // Top panel with score
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBackground(score >= 70 ? new Color(76, 175, 80) : new Color(244, 67, 54));
        
        JLabel result = new JLabel("Votre score: " + score + "%", SwingConstants.CENTER);
        result.setFont(new Font("Arial", Font.BOLD, 32));
        result.setForeground(Color.WHITE);
        
        JLabel message = new JLabel("", SwingConstants.CENTER);
        message.setFont(new Font("Arial", Font.PLAIN, 16));
        message.setForeground(Color.WHITE);
        
        if (score >= 70) {
            message.setText("🎉 Félicitations! Chapitre suivant débloqué!");
            frame.unlockNextChapter(chapterIndex);
        } else {
            message.setText("❌ Score insuffisant. Il vous faut 70% pour continuer.");
        }
        
        topPanel.add(result);
        topPanel.add(message);
        add(topPanel, BorderLayout.NORTH);

        // Center panel with player info
        JPanel center = new JPanel(new GridLayout(0, 1, 5, 5));
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel playerInfo = new JLabel("Joueur: " + frame.getPlayerName(), SwingConstants.CENTER);
        playerInfo.setFont(new Font("Arial", Font.BOLD, 16));
        playerInfo.setForeground(new Color(63, 81, 181));
        center.add(playerInfo);
        
        JLabel saveInfo = new JLabel("✓ Progression sauvegardée automatiquement", SwingConstants.CENTER);
        saveInfo.setFont(new Font("Arial", Font.PLAIN, 14));
        saveInfo.setForeground(new Color(76, 175, 80));
        center.add(saveInfo);

        add(center, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());

        if (score >= 70) {
            JButton continueBtn = new JButton("➡️ Continuer au Chapitre Suivant");
            continueBtn.setFont(new Font("Arial", Font.BOLD, 14));
            continueBtn.setBackground(new Color(76, 175, 80));
            continueBtn.setForeground(Color.WHITE);
            continueBtn.setFocusPainted(false);
            continueBtn.addActionListener(e -> frame.refresh());
            buttonPanel.add(continueBtn);
        } else {
            JButton retry = new JButton("🔄 Réessayer");
            retry.setFont(new Font("Arial", Font.BOLD, 14));
            retry.setBackground(new Color(255, 152, 0));
            retry.setForeground(Color.WHITE);
            retry.setFocusPainted(false);
            retry.addActionListener(e -> {
                frame.setContentPane(new QuizPanel(chapter, frame, chapterIndex));
                frame.revalidate();
                frame.repaint();
            });
            buttonPanel.add(retry);
        }

        JButton back = new JButton("🏠 Retour aux Chapitres");
        back.setFont(new Font("Arial", Font.PLAIN, 12));
        back.setFocusPainted(false);
        back.addActionListener(e -> frame.refresh());
        buttonPanel.add(back);

        add(buttonPanel, BorderLayout.SOUTH);
    }
}