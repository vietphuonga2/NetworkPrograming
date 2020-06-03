/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package olympia;

/**
 *
 * @author H
 */
public class Quiz {
    private String question;
    private String answer;
    private int score;
    private boolean hopestar;

    public Quiz(String question, String answer, int score, boolean hopestar) {
        this.question = question;
        this.answer = answer;
        this.score = score;
        this.hopestar = hopestar;
    }

    public Quiz() {
    }



    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isHopestar() {
        return hopestar;
    }

    public void setHopestar(boolean hopestar) {
        this.hopestar = hopestar;
    }
    
}
