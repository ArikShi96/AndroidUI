package com.example.root.experimentassistant.ViewModel;

import java.util.List;

import lecho.lib.hellocharts.model.PointValue;

/**
 * Created by root on 2017/1/14.
 */
public class chartQuestion extends Question {
    private int minY;
    private int maxY;
    private int setoffX;
    private List<PointValue> points;

    public int getMinY() {
        return minY;
    }

    public void setMinY(int minY) {
        this.minY = minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public int getSetoffX() {
        return setoffX;
    }

    public void setSetoffX(int setoffX) {
        this.setoffX = setoffX;
    }

    public List<PointValue> getPoints() {
        return points;
    }

    public void setPoints(List<PointValue> points) {
        this.points = points;
    }
}
