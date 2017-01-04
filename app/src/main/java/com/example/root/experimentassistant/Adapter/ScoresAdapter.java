package com.example.root.experimentassistant.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.root.experimentassistant.R;
import com.example.root.experimentassistant.StaticSetting.StaticConfig;
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
    public View getView(final int position, View convertView, ViewGroup parent){
        ViewHolder holder=null;
        if (null == convertView)
        {
            holder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            convertView = mInflater.inflate(R.layout.score_entry, null);

            holder.experName = (TextView) convertView.findViewById(R.id.scoreExperName);
            holder.report = (TextView) convertView.findViewById(R.id.experReport);
            holder.score = (TextView) convertView.findViewById(R.id.score);
            holder.noScore=(ImageView)convertView.findViewById(R.id.noScore);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        final ViewExperScore score = getItem(position);
        if (null != score)
        {
            holder.experName.setText(score.getExperName());

            holder.report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("report","click");
                    Intent intent=new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri attach_url=Uri.parse(StaticConfig.BASE_URL+score.getAttach_url());
                    intent.setData(attach_url);
                    mContext.startActivity(intent);
                }
            });
            if(score.getScore()!="null") {
                holder.score.setText(score.getScore());
                holder.score.setVisibility(View.VISIBLE);
                holder.noScore.setVisibility(View.GONE);
            }
            else{
                holder.score.setText("×");
                holder.score.setVisibility(View.GONE);
                holder.noScore.setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }

    private static class ViewHolder{
        TextView experName;
        TextView report;
        TextView score;
        ImageView noScore;
    }
}
