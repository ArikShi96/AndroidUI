package com.example.root.experimentassistant.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.root.experimentassistant.R;
import com.example.root.experimentassistant.ViewModel.ViewExper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 2016/12/26.
 */
public class MySpinnerAdapter extends BaseAdapter{
    private List<String> weeks=null;

    private Context mContext=null;

    private int selectItem=-1;

    public MySpinnerAdapter(Context context, List<String> list){
        mContext=context;

        weeks=list;
    }

    @Override
    public int getCount() {
        int rnt=0;
        if(weeks!=null){
            rnt=weeks.size();
        }
        return rnt;
    }

    @Override
    public String getItem(int position){
        String week=null;
        if(weeks!=null){
            week=weeks.get(position);
        }
        return week;
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder=null;
        if (null == convertView)
        {
            holder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            convertView = mInflater.inflate(R.layout.my_spinner_item, null);

            holder.mySpinnerText = (TextView) convertView.findViewById(R.id.mySpinnerItemText);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        String str=getItem(position);
        if (null != str)
        {
            holder.mySpinnerText.setText(str);

            if(position==selectItem){
                holder.mySpinnerText.setSelected(true);
            }
            else{
                holder.mySpinnerText.setSelected(false);
            }
        }

        return convertView;
    }

    private static class ViewHolder{
        TextView mySpinnerText;
    }

    public void setSelectItem(int i){
        selectItem=i;
        Log.d("spinner adapter",""+i);
        notifyDataSetChanged();
    }
}
