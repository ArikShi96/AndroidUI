package com.example.root.experimentassistant.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.root.experimentassistant.R;
import com.example.root.experimentassistant.ViewModel.ViewExper;
import com.example.root.experimentassistant.ViewModel.ViewExperScore;

import java.util.List;

/**
 * Created by root on 2016/12/27.
 */
public class ScoresAdapter extends BaseAdapter {
    private List<ViewExperScore> scores=null;

    private Context mContext=null;

    public ScoresAdapter(Context context, List<ViewExperScore> scoreItems){
        mContext=context;
        scores=scoreItems;
    }

    @Override
    public int getCount() {
        int rnt=0;
        if(scores!=null){
            rnt=scores.size();
        }
        return rnt;
    }

    @Override
    public ViewExperScore getItem(int position){
        ViewExperScore score=null;
        if(scores!=null){
            score=scores.get(position);
        }
        return score;
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
            convertView = mInflater.inflate(R.layout.score_entry, null);

            holder.experName = (TextView) convertView.findViewById(R.id.scoreExperName);
            holder.courseName = (TextView) convertView.findViewById(R.id.scoreCourseName);
            holder.score = (TextView) convertView.findViewById(R.id.score);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        ViewExperScore score = getItem(position);
        if (null != score)
        {
            holder.experName.setText(score.getExperName());
            holder.courseName.setText(score.getCourseName());
            if(score.getScore()!=null) {
                holder.score.setText(score.getScore());
            }
            else{
                holder.score.setText("×");
            }
        }

        return convertView;
    }

    private static class ViewHolder{
        TextView experName;
        TextView courseName;
        TextView score;
    }
}
