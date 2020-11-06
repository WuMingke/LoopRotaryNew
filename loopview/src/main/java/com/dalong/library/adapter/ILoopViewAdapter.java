package com.dalong.library.adapter;

import android.view.View;
import android.view.ViewGroup;

public interface ILoopViewAdapter {

    int getCount();

    Object getItem(int position);

    View getView(int position, View convertView, ViewGroup parent);

    void notifyDataSetChanged();
}
