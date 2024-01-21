package org.example.questions;

public class Questions {
    private String category;
    private String clue;
    private String answer;

    public Questions(String category, String clue, String answer) {
        this.category = category;
        this.clue = clue;
        this.answer = answer;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getClue() {
        return clue;
    }

    public void setClue(String clue) {
        this.clue = clue;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
