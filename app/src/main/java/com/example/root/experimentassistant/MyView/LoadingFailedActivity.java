package com.example.root.experimentassistant.MyView;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.root.experimentassistant.R;

/**
 * Created by Json on 2016/12/26.
 */
public class LoadingFailedActivity extends AppCompatActivity {
    private Bundle bundle;
    private TextView title;
    private TextView err_msg;
    private ImageButton backbutton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.err);

        title      = (TextView)    findViewById(R.id.err_title);
        err_msg    = (TextView)    findViewById(R.id.err_content);
        backbutton = (ImageButton) findViewById(R.id.err_back);

        bundle=getIntent().getExtras();
        title.setText(bundle.getString("title"));
        err_msg.setText(bundle.getString("err_msg"));

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
