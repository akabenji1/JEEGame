import java.util.List;
import java.util.Collections;

public abstract class Chapter {
    public String title;
    public List<Question> questions;
    public boolean unlocked = false;

    public Chapter(String title, List<Question> questions) {
        this.title = title;
        this.questions = questions;
    }

    // Unified pass threshold: 70% (matches ResultPanel that shows the unlock button at 70)
    public boolean isPassed(int score) {
        return score >= 70;
    }
    
    public void shuffleQuestions() {
        Collections.shuffle(questions);
    }
}
