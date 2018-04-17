package com.example.root.experimentassistant.FirstLevel;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.example.root.experimentassistant.Adapter.ExpersAdapter;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.example.root.experimentassistant.Adapter.ExpersAdapter;
import com.example.root.experimentassistant.Adapter.MySpinnerAdapter;
import com.example.root.experimentassistant.Model.RequestCallBack;
import com.example.root.experimentassistant.Model.User;
import com.example.root.experimentassistant.R;
import com.example.root.experimentassistant.SecondLevel.ExperDetailActivity;
import com.example.root.experimentassistant.ViewModel.ViewExper;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ExperimentList extends Fragment {
    ListView experimentList;

    private MaterialRefreshLayout myMaterialRefreshLayout;

    private int weekCnt = 1;

    private Button spinnerText;

    private ListView weekList = null;

    private Button setWeekBtn;

    private LinearLayout layout;

    private ArrayList<String> weekArr;

    private OptionsPickerView weekPicker;

    private MySpinnerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.experiment_list, container, false);
        Log.d("Exper", "create");
        experimentList = (ListView) view.findViewById(R.id.experiment_list);
        myMaterialRefreshLayout = (MaterialRefreshLayout) view.findViewById(R.id.experiment_refresh);

        weekArr = new ArrayList<String>();
        for (int i = 1; i <= 25; i++) {
            weekArr.add("第" + i + "周实验");
        }

        spinnerText = (Button) view.findViewById(R.id.testSpinner);
        adapter = new MySpinnerAdapter(getContext(), weekArr);
        weekCnt=User.getInstance().getCurrentWeek(getContext().getSharedPreferences("user", Context.MODE_PRIVATE));

        //刷新回调函数
        myMaterialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                if (User.getInstance().isLogin()) bindExperiment();
                else {
                    myMaterialRefreshLayout.finishRefresh();
                    Toast.makeText(ExperimentList.this.getContext(),"请先登录",Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                myMaterialRefreshLayout.finishRefreshLoadMore();
            }
        });

        spinnerText.setText(adapter.getItem(weekCnt - 1));
        adapter.setSelectItem(weekCnt-1);

        spinnerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWindow(v);
            }
        });

        weekPicker = new OptionsPickerView(getContext());
        weekPicker.setPicker(weekArr);
        weekPicker.setCyclic(false);
        weekPicker.setTitle("选择星期");
        weekPicker.setSelectOptions(weekCnt - 1);

        weekPicker.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                Log.d("weekPicker","press");
                weekCnt = options1 + 1;
                adapter.setSelectItem(options1);
                spinnerText.setText(adapter.getItem(options1));
                User.getInstance().setCurrentWeek(ExperimentList.this.getContext().getSharedPreferences("user", Context.MODE_PRIVATE),weekCnt);
                bindExperiment();
            }
        });

        experimentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                long id = adapterView.getAdapter().getItemId(position);

                Intent intent=new Intent(ExperimentList.this.getContext(), ExperDetailActivity.class);
                intent.putExtra("exper_id",id);

                startActivity(intent);
            }
        });

        if (User.getInstance().isLogin()) {
            weekCnt = User.getInstance().getCurrentWeek(getContext().getSharedPreferences("user", Context.MODE_PRIVATE));
            if (weekCnt < 1 || weekCnt > 25) weekCnt = 1;

            if(adapter!=null) {
                spinnerText.setText(adapter.getItem(weekCnt - 1));
            }
            bindExperiment();
        }

        return view;
    }

    private void showWindow(View v) {
        layout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.my_spinner, null);
        weekList = (ListView) layout.findViewById(R.id.mySpinnerListView);
        setWeekBtn = (Button) layout.findViewById(R.id.setWeekBtn);
        weekList.setAdapter(adapter);
//        weekList.setSelection(weekCnt-1);
        weekList.setSelectionFromTop(weekCnt - 1, (int) (getContext().getResources().getDisplayMetrics().density * 240) / 2);
        adapter.setSelectItem(weekCnt - 1);

        final PopupWindow popupWindow = new PopupWindow(layout, spinnerText.getWidth() / 2, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setContentView(layout);
        popupWindow.showAsDropDown(v, spinnerText.getWidth() / 4, 0);

        Log.d("showWindow", "first");
        weekList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("item listem", position + "");
                weekCnt = position + 1;
                adapter.setSelectItem(position);
                spinnerText.setText(adapter.getItem(position));
                bindExperiment();

                popupWindow.dismiss();
            }
        });

        setWeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekPicker.setSelectOptions(weekCnt - 1);
                weekPicker.show();
                popupWindow.dismiss();
            }
        });
    }

    private void bindExperiment() {
        if (!User.getInstance().isLogin()) return;
        Log.d("bindExper", "week" + weekCnt);
        User.getInstance().getCntExper(weekCnt, new getExpersCallback());
    }

    private class getExpersCallback implements RequestCallBack {
        public void onRequestSuccess(Object sender) {
            myMaterialRefreshLayout.finishRefresh();
            ExpersAdapter adapter = new ExpersAdapter(ExperimentList.this.getContext(), (List<ViewExper>) sender);
            experimentList.setAdapter(adapter);
        }

        public void onRequestFailure(Object sender) {
            myMaterialRefreshLayout.finishRefresh();

            String message = (String) sender;
            Toast.makeText(ExperimentList.this.getContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}
