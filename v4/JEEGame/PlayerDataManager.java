import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Manages player data: name, unlocked chapters, and best scores
 * Format: name,unlockedChapters(comma-separated),bestScore,timestamp
 */
public class PlayerDataManager {
    private static final String FILE_NAME = System.getProperty("user.home") + File.separator + ".jeegame_players.csv";
    private static final String HEADER = "name,unlockedChapters,bestScore,timestamp";
    
    public static class PlayerData {
        public String name;
        public boolean[] unlockedChapters; // 13 chapters
        public int bestScore;
        public long timestamp;
        public int maxUnlockedChapter; // Highest chapter index unlocked
        
        public PlayerData(String name, boolean[] unlockedChapters, int bestScore, long timestamp) {
            this.name = name;
            this.unlockedChapters = unlockedChapters;
            this.bestScore = bestScore;
            this.timestamp = timestamp;
            this.maxUnlockedChapter = calculateMaxUnlocked();
        }
        
        private int calculateMaxUnlocked() {
            for (int i = unlockedChapters.length - 1; i >= 0; i--) {
                if (unlockedChapters[i]) return i;
            }
            return 0;
        }
    }
    
    public PlayerDataManager() {
        ensureFileExists();
    }
    
    private void ensureFileExists() {
        Path p = Paths.get(FILE_NAME);
        if (!Files.exists(p)) {
            try {
                Files.createDirectories(p.getParent() == null ? p.toAbsolutePath().getParent() : p.getParent());
                try (BufferedWriter w = Files.newBufferedWriter(p)) {
                    w.write(HEADER);
                    w.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Load player data by name (case-insensitive)
     */
    public PlayerData loadPlayerData(String name) {
        Path p = Paths.get(FILE_NAME);
        if (!Files.exists(p)) return null;
        
        try (BufferedReader r = Files.newBufferedReader(p)) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("name,")) continue;
                String[] parts = line.split(",", 4);
                if (parts.length < 4) continue;
                
                String playerName = parts[0];
                if (playerName.equalsIgnoreCase(name)) {
                    // Parse unlocked chapters
                    String[] unlockedStr = parts[1].split(";");
                    boolean[] unlocked = new boolean[13];
                    for (String s : unlockedStr) {
                        if (!s.isEmpty()) {
                            int idx = Integer.parseInt(s);
                            if (idx >= 0 && idx < 13) unlocked[idx] = true;
                        }
                    }
                    
                    int bestScore = Integer.parseInt(parts[2]);
                    long timestamp = Long.parseLong(parts[3]);
                    
                    return new PlayerData(playerName, unlocked, bestScore, timestamp);
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Save or update player data
     */
    public void savePlayerData(String name, boolean[] unlockedChapters, int score) {
        PlayerData existing = loadPlayerData(name);
        int bestScore = (existing != null) ? Math.max(existing.bestScore, score) : score;
        
        // Merge unlocked chapters (keep any previously unlocked)
        boolean[] finalUnlocked = new boolean[13];
        if (existing != null) {
            for (int i = 0; i < 13; i++) {
                finalUnlocked[i] = existing.unlockedChapters[i] || unlockedChapters[i];
            }
        } else {
            System.arraycopy(unlockedChapters, 0, finalUnlocked, 0, 13);
        }
        
        // Remove old entry and add new one
        removePlayerData(name);
        appendPlayerData(name, finalUnlocked, bestScore);
    }
    
    private void removePlayerData(String name) {
        Path p = Paths.get(FILE_NAME);
        if (!Files.exists(p)) return;
        
        try {
            List<String> lines = Files.readAllLines(p);
            try (BufferedWriter w = Files.newBufferedWriter(p)) {
                for (String line : lines) {
                    if (line.startsWith("name,") || line.trim().isEmpty()) {
                        w.write(line);
                        w.newLine();
                        continue;
                    }
                    String[] parts = line.split(",", 2);
                    if (parts.length > 0 && !parts[0].equalsIgnoreCase(name)) {
                        w.write(line);
                        w.newLine();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void appendPlayerData(String name, boolean[] unlockedChapters, int bestScore) {
        Path p = Paths.get(FILE_NAME);
        
        // Convert unlocked array to semicolon-separated indices
        StringBuilder unlockedStr = new StringBuilder();
        for (int i = 0; i < unlockedChapters.length; i++) {
            if (unlockedChapters[i]) {
                if (unlockedStr.length() > 0) unlockedStr.append(";");
                unlockedStr.append(i);
            }
        }
        
        try (BufferedWriter w = Files.newBufferedWriter(p, StandardOpenOption.APPEND)) {
            w.write(String.format("%s,%s,%d,%d", 
                escape(name), 
                unlockedStr.toString(), 
                bestScore, 
                System.currentTimeMillis()));
            w.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String escape(String s) {
        return s.replace(",", " ").replace(";", " ");
    }
}