package com.classichu.mynsd.adpter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.classichu.mynsd.nsd.NSDInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by louisgeek on 2018/8/22.
 */
public class NSDAdapter extends BaseAdapter {
    private List<NSDInfo> mNSDInfoList = new ArrayList<>();

    public void refreshData(List<NSDInfo> nsdInfoList) {
        mNSDInfoList.clear();
        mNSDInfoList.addAll(nsdInfoList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mNSDInfoList.size();
    }

    @Override
    public NSDInfo getItem(int position) {
        return mNSDInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(mNSDInfoList.get(position).name + "\n"
                + mNSDInfoList.get(position).ip + ":" + mNSDInfoList.get(position).port);
        return convertView;
    }
}
