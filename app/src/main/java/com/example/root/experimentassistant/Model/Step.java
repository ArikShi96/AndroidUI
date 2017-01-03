package com.example.root.experimentassistant.Model;

import com.example.root.experimentassistant.ViewModel.Question;

import java.util.ArrayList;

/**
 * Created by Json on 2016/12/30.
 */
public class Step {
    private int id;
    private String name;
    private int suggest_time;
    private int prefix_time;
    private String content;
    private String note;
    private ArrayList<Question> question_list=new ArrayList<>();
    private ArrayList<String> image_list=new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSuggest_time() {
        return suggest_time;
    }

    public void setSuggest_time(int suggest_time) {
        this.suggest_time = suggest_time;
    }

    public int getPrefix_time() {
        return prefix_time;
    }

    public void setPrefix_time(int prefix_time) {
        this.prefix_time = prefix_time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ArrayList<Question> getQuestion_list() {
        return question_list;
    }

    public void setQuestion_list(ArrayList<Question> question_list) {
        this.question_list = question_list;
    }

    public ArrayList<String> getImage_list() {
        return image_list;
    }

    public void setImage_list(ArrayList<String> image_list) {
        this.image_list = image_list;
    }
}
