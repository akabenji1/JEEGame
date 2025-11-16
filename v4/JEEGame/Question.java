import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Question {
    public String text;
    public String[] options;
    public int correctIndex;
    public String explanation;

    public Question(String text, String[] options, int correctIndex, String explanation) {
        this.text = text;
        this.options = options;
        this.correctIndex = correctIndex;
        this.explanation = explanation;
    }
    
    /**
     * Shuffles the answer options and updates the correctIndex accordingly.
     * This ensures the correct answer is tracked after shuffling.
     */
    public void shuffleOptions() {
        // Store the correct answer before shuffling
        String correctAnswer = options[correctIndex];
        
        // Convert array to list for shuffling
        List<String> optionsList = Arrays.asList(options);
        Collections.shuffle(optionsList);
        
        // Convert back to array
        options = optionsList.toArray(new String[0]);
        
        // Find and update the new index of the correct answer
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(correctAnswer)) {
                correctIndex = i;
                break;
            }
        }
    }
}