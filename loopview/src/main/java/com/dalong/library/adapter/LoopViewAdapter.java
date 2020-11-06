package com.dalong.library.adapter;

import java.util.ArrayList;
import java.util.List;

public abstract class LoopViewAdapter<T> implements ILoopViewAdapter {

    private OnLoopViewChangeListener mOnLoopViewChangeListener;
    private List<T> mDatas;

    public void setDatas(List<T> mDatas) {
        this.mDatas = mDatas;
        notifyDataSetChanged();
    }

    public List<T> getDatas() {
        if (mDatas == null) {
            mDatas = new ArrayList<>();
        }
        return mDatas;
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public T getItem(int position) {
        if (mDatas == null) {
            return null;
        }
        return mDatas.get(position);
    }

    @Override
    public void notifyDataSetChanged() {
        if (mOnLoopViewChangeListener != null) {
            mOnLoopViewChangeListener.notifyDataSetChanged();
        }
    }

    public void setOnLoopViewChangeListener(OnLoopViewChangeListener mOnLoopViewChangeListener) {
        this.mOnLoopViewChangeListener = mOnLoopViewChangeListener;
    }

    public interface OnLoopViewChangeListener {
        void notifyDataSetChanged();
    }
}
