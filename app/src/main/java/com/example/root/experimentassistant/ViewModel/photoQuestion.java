package com.example.root.experimentassistant.ViewModel;

import java.util.ArrayList;

/**
 * Created by root on 2017/1/13.
 */
public class photoQuestion extends Question {
    ArrayList<ImageItem> bitmap=new ArrayList<ImageItem>();

    public ArrayList<ImageItem> getBitmap() {
        return bitmap;
    }

    public void setBitmap(ArrayList<ImageItem> bitmap) {
        this.bitmap = bitmap;
    }
}
