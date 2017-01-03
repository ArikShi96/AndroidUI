package com.example.root.experimentassistant.ViewModel;

/**
 * Created by Json on 2016/12/29.
 */
public class Question {
    private int id;
    private String question;
    private boolean answer_type;
    private String answer;

    public Question(){
        question="";
        answer="";
        answer_type=false;
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

    public boolean getAnswer_type() {
        return answer_type;
    }

    public void setAnswer_type(boolean answer_type) {
        this.answer_type = answer_type;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
