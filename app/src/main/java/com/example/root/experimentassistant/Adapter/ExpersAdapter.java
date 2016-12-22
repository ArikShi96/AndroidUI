package com.example.root.experimentassistant.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.root.experimentassistant.R;
import com.example.root.experimentassistant.ViewModel.*;

import java.util.List;

/**
 * Created by root on 2016/12/20.
 */
public class ExpersAdapter extends BaseAdapter{
    private List<ViewExper> expers=null;

    private Context mContext=null;

    public ExpersAdapter(Context context, List<ViewExper> experItems){
        mContext=context;
        expers=experItems;
    }

    @Override
    public int getCount() {
        int rnt=0;
        if(expers!=null){
            rnt=expers.size();
        }
        return rnt;
    }

    @Override
    public ViewExper getItem(int position){
        ViewExper exper=null;
        if(expers!=null){
            exper=expers.get(position);
        }
        return exper;
    }

    @Override
    public long getItemId(int position){
        long id=-1;
        if(expers!=null){
            String tmp=expers.get(position).getId();
            id =Long.parseLong(tmp);
        }
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder=null;
        if (null == convertView)
        {
            holder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            convertView = mInflater.inflate(R.layout.experiment_entry, null);

            holder.name = (TextView) convertView.findViewById(R.id.experimentName);
            holder.time = (TextView) convertView.findViewById(R.id.experimentTime);
            holder.course = (TextView) convertView.findViewById(R.id.experimentCourse);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        ViewExper exper = getItem(position);
        if (null != exper)
        {
            holder.name.setText(exper.getExperName());
            holder.time.setText(exper.getTime());
            holder.course.setText(exper.getCourseName());
        }

        return convertView;
    }

    private static class ViewHolder{
        TextView name;
        TextView time;
        TextView course;
    }
}
