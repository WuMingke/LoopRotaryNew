package com.example.looprotaryswitch;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dalong.library.adapter.LoopViewAdapter;
import com.dalong.library.listener.OnItemClickListener;
import com.dalong.library.listener.OnItemSelectedListener;
import com.dalong.library.LoopViewLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LoopViewLayout mLoopRotarySwitchView;

    private MyAdapter myAdapter;

    private static final String TAG = "starView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initLinstener();
    }

    private void initLinstener() {

        /**
         * 选中回调
         */
        mLoopRotarySwitchView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void selected(int position, View view) {
                Log.i(TAG, "selected: position-" + position);
            }
        });

    }

    /**
     * 初始化布局
     */
    private void initView() {
        mLoopRotarySwitchView = (LoopViewLayout) findViewById(R.id.mLoopRotarySwitchView);

        myAdapter = new MyAdapter();
        mLoopRotarySwitchView.setAdapter(myAdapter);
        //mLoopRotarySwitchView.setLoopRotationX(-50);

        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");
        list.add("7");
        list.add("8");
        myAdapter.setDatas(list);
    }


    public class MyAdapter extends LoopViewAdapter<String> {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loopview_item_view0, null);
            TextView tv = (TextView) view.findViewById(R.id.loopView0_tv1);
            tv.setText(String.valueOf(position + 1));
            return view;
        }
    }

}
