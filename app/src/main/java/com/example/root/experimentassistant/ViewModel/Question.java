package com.example.root.experimentassistant.ViewModel;

/**
 * Created by Json on 2016/12/29.
 */
public class Question {
    private int id;
    private String question;
    private int answer_type;
    private String answer;
    private boolean answered=false;

    public static int TEXTQUESTION=0;
    public static int PHOTOQUESTION=1;

    public Question(){
        question="";
        answer="";
        answer_type=TEXTQUESTION;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getAnswer_type() {
        return answer_type;
    }

    public void setAnswer_type(int answer_type) {
        this.answer_type = answer_type;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }
}
