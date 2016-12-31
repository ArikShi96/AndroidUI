package com.example.root.experimentassistant.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.root.experimentassistant.R;
import com.example.root.experimentassistant.ViewModel.ViewCourse;
import com.example.root.experimentassistant.ViewModel.ViewExper;

import java.util.List;

/**
 * Created by root on 2016/12/21.
 */
public class CoursesAdapter extends BaseAdapter {
    private List<ViewCourse> courses = null;

    private Context mContext = null;

    public CoursesAdapter(Context context, List<ViewCourse> courseItems) {
        mContext = context;
        courses = courseItems;
    }

    @Override
    public int getCount() {
        int rnt = 0;
        if (courses != null) {
            rnt = courses.size();
        }
        return rnt;
    }

    @Override
    public ViewCourse getItem(int position) {
        ViewCourse course = null;
        if (courses != null) {
            course = courses.get(position);
        }
        return course;
    }

    @Override
    public long getItemId(int position) {
        long id = -1;
        if (courses != null) {
            String tmp = courses.get(position).getId();
            id = Long.parseLong(tmp);
        }
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            holder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            convertView = mInflater.inflate(R.layout.course_entry, null);

            holder.name = (TextView) convertView.findViewById(R.id.courseName);
            holder.time = (TextView) convertView.findViewById(R.id.courseTime);
            holder.teacher = (TextView) convertView.findViewById(R.id.courseTeacher);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ViewCourse course = getItem(position);
        if (null != course) {
            holder.name.setText(course.getCourseName());
            holder.time.setText(course.getTime());
            holder.teacher.setText(course.getTeacher());
        }

        return convertView;
    }

    private class ViewHolder {
        TextView name;
        TextView time;
        TextView teacher;
    }

    public void addData(List<ViewCourse> data){
        courses.addAll(data);
        notifyDataSetChanged();
    }
}

