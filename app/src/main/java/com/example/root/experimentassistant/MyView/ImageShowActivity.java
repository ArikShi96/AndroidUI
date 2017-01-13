package com.example.root.experimentassistant.MyView;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.root.experimentassistant.R;

public class ImageShowActivity extends AppCompatActivity {
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_show);

        image =(ImageView) findViewById(R.id.show_image);
        Bitmap bitmap=getIntent().getParcelableExtra("bitmap");
        image.setImageBitmap(bitmap);
    }
}
