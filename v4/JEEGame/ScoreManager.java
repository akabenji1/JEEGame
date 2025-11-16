import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Simple CSV-backed score manager.
 * CSV columns: name,chapterIndex,score,timestamp
 */
public class ScoreManager {
    private static final String FILE_NAME = System.getProperty("user.home") + File.separator + ".jeegame_scores.csv";
    private static final String HEADER = "name,chapterIndex,score,timestamp";
    private final List<Record> records = new ArrayList<>();

    public static class Record {
        public String name;
        public int chapterIndex;
        public int score;
        public long timestamp;

        public Record(String name, int chapterIndex, int score, long timestamp) {
            this.name = name;
            this.chapterIndex = chapterIndex;
            this.score = score;
            this.timestamp = timestamp;
        }
    }

    public ScoreManager() {
        load();
    }

    private void load() {
        records.clear();
        Path p = Paths.get(FILE_NAME);
        if (!Files.exists(p)) {
            // create parent if needed
            try {
                Files.createDirectories(p.getParent() == null ? p.toAbsolutePath().getParent() : p.getParent());
            } catch (IOException ignored) {}
            return;
        }
        try (BufferedReader r = Files.newBufferedReader(p)) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("name,")) continue;
                String[] parts = line.split(",", 4);
                if (parts.length < 4) continue;
                String name = parts[0];
                int chapterIndex = Integer.parseInt(parts[1]);
                int score = Integer.parseInt(parts[2]);
                long ts = Long.parseLong(parts[3]);
                records.add(new Record(name, chapterIndex, score, ts));
            }
        } catch (IOException ignored) {}
    }

    private void persist() {
        Path p = Paths.get(FILE_NAME);
        try (BufferedWriter w = Files.newBufferedWriter(p, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            w.write(HEADER);
            w.newLine();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            for (Record rec : records) {
                w.write(String.format("%s,%d,%d,%d", escape(rec.name), rec.chapterIndex, rec.score, rec.timestamp));
                w.newLine();
            }
        } catch (IOException ignored) {}
    }

    private String escape(String s) {
        return s.replace(",", " "); // simple escape for commas
    }

    public synchronized List<Record> findByName(String name) {
        List<Record> res = new ArrayList<>();
        for (Record r : records) {
            if (r.name.equalsIgnoreCase(name)) res.add(r);
        }
        return res;
    }

    public synchronized Record findBestByName(String name) {
        List<Record> list = findByName(name);
        Record best = null;
        for (Record r : list) {
            if (best == null || r.score > best.score) best = r;
        }
        return best;
    }

    public synchronized void addRecord(Record rec) {
        records.add(rec);
        persist();
    }

    public synchronized void overwriteRecordsForName(String name, Record newRec) {
        // Remove all records with the same name (case-insensitive) and add newRec
        records.removeIf(r -> r.name.equalsIgnoreCase(name));
        records.add(newRec);
        persist();
    }

    public synchronized String makeUniqueName(String base) {
        if (findByName(base).isEmpty()) return base;
        int i = 1;
        while (true) {
            String candidate = base + "_" + i;
            if (findByName(candidate).isEmpty()) return candidate;
            i++;
        }
    }

    // Utility to create a record from inputs
    public static Record makeRecord(String name, int chapterIndex, int score) {
        return new Record(name, chapterIndex, score, System.currentTimeMillis());
    }
}