package com.example.root.experimentassistant.ThirdLevel;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.experimentassistant.ViewModel.*;
import com.example.root.experimentassistant.Internet.CookieUnits;
import com.example.root.experimentassistant.Internet.ExperimentHttpClient;
import com.example.root.experimentassistant.Model.User;
import com.example.root.experimentassistant.R;
import com.example.root.experimentassistant.ViewModel.Question;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class LineChatAnswer extends AppCompatActivity {
    private LineChartData lineChartData;

    private LineChartView lineChartView;

    private List<Line> linesList;

    private List<PointValue> pointValueList;

    private ImageView back;

    private Button add;

    private Button undo;

    private TextView upload;

    private EditText chartX;

    private EditText chartY;

    private Axis axisY;

    private Axis axisX;

    private int minY;

    private int maxY;

    private int offsetX;

    private int questionId;

    Question question;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chat_answer);

        back=(ImageView)findViewById(R.id.Cancle);
        upload=(TextView)findViewById(R.id.chartUpload);
        add=(Button)findViewById(R.id.ChartAdd);
        undo=(Button)findViewById(R.id.ChartUndo);
        chartX=(EditText)findViewById(R.id.ChartX);
        chartY=(EditText)findViewById(R.id.ChartY);
        lineChartView=(LineChartView)findViewById(R.id.lineChart);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        minY=bundle.getInt("minY");
        maxY=bundle.getInt("maxY");
        offsetX=bundle.getInt("offset");
        questionId=bundle.getInt("questionId");
        question= User.getInstance().getExperiment().getQuestions().get(questionId);

        init();
    }

    private void init(){
        pointValueList = new ArrayList<>();
        linesList = new ArrayList<>();

        //初始化坐标轴
        axisY = new Axis();
        axisX = new Axis();
        axisY.setTextColor(Color.parseColor("#000000"));
        axisX.setTextColor(Color.parseColor("#000000"));

        Viewport port = initViewPort(0,10*offsetX,maxY,minY);
        lineChartView.setCurrentViewportWithAnimation(port);    //设置显示范围
        lineChartView.setInteractive(false);
        lineChartView.setScrollEnabled(false);
        lineChartView.setValueTouchEnabled(false);
        lineChartView.setFocusableInTouchMode(false);
        lineChartView.setViewportCalculationEnabled(false);
        lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChartView.startDataAnimation();

        if(question.isAnswered()&&question.getAnswer_type()==Question.CHARTQUESTION){
            chartQuestion chart=(chartQuestion)question;

            minY=chart.getMinY();
            maxY=chart.getMaxY();
            offsetX=chart.getSetoffX();
            pointValueList=chart.getPoints();

            if(pointValueList.size()>0) {
                float x = pointValueList.get(pointValueList.size() - 1).getX();
                drawByPoints(x);
            }
        }
        lineChartData = initDatas(linesList);
        lineChartView.setLineChartData(lineChartData);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Upload();
            }
        });

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pointValueList.remove(pointValueList.size()-1);
                drawByPoints(pointValueList.get(pointValueList.size()-1).getX());
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float x=Float.parseFloat(chartX.getText().toString());
                float y=Float.parseFloat(chartY.getText().toString());
                PointValue point=new PointValue();
                point.set(x,y);

                pointValueList.add(point);
                drawByPoints(x);
            }
        });
    }

    private void drawByPoints(float x){
        Line line = new Line(pointValueList);
        line.setColor(Color.parseColor("#00BFFF"));
        line.setShape(ValueShape.CIRCLE);
        line.setCubic(true);//曲线是否平滑，即是曲线还是折线

        linesList.add(line);
        lineChartData = initDatas(linesList);
        lineChartView.setLineChartData(lineChartData);

        Viewport port;
        if(x > 10*offsetX){
            port = initViewPort(x-10*offsetX,x,maxY,minY);
        }
        else {
            port = initViewPort(0,10*offsetX,maxY,minY);
        }
        lineChartView.setMaximumViewport(port);
        lineChartView.setCurrentViewport(port);
    }

    private LineChartData initDatas(List<Line> lines) {
        LineChartData data = new LineChartData(lines);
        data.setAxisYLeft(axisY);
        data.setAxisXBottom(axisX);
        return data;
    }

    private Viewport initViewPort(float left,float right,int top,int buttom) {
        Viewport port = new Viewport();
        port.top = top;
        port.bottom = buttom;
        port.left = left;
        port.right = right;
        return port;
    }

    private void Upload(){
        RequestParams params=new RequestParams();
        params.put("ImageNum",-1);
        params.put("exper_id",User.getInstance().getExperiment().getId());
        params.put("student_id",User.getInstance().getId());
        params.put("step_index",questionId+1);
        params.put("x_count",pointValueList.size());
        params.put("p_count",pointValueList.size());
        params.put("x_name","x");
        params.put("y_name","y");

        File file=new File(getFilesDir(),"tmp.txt");
        try {
            if (!file.exists()) file.createNewFile();
            params.put("xxx",file);
        }catch (Exception e){
            e.printStackTrace();
            Log.d("file","exception");
        }

        for(int i=0;i<pointValueList.size();i++){
            params.put("x["+i+"]",pointValueList.get(i).getX());
            params.put("p["+i+"]",pointValueList.get(i).getY());
        }

        ExperimentHttpClient.getInstance().post("/student/submitstep", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(LineChatAnswer.this,"上传失败",Toast.LENGTH_SHORT);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                question.setAnswered(true);
                if(question.getAnswer_type()==Question.CHARTQUESTION){
                    chartQuestion chart=(chartQuestion)question;
                    chart.setMinY(minY);
                    chart.setMaxY(maxY);
                    chart.setSetoffX(offsetX);
                    chart.setPoints(pointValueList);
                }

                Intent res=new Intent();
                res.putExtra("questionId",questionId);
                setResult(RESULT_OK,res);
                finish();
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
