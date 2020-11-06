package com.dalong.library;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import com.dalong.library.adapter.LoopViewAdapter;
import com.dalong.library.listener.OnItemClickListener;
import com.dalong.library.listener.OnItemSelectedListener;

/***
 * 水平旋转轮播控件
 */
public class LoopViewLayout extends RelativeLayout implements ILoopView<LoopViewAdapter>, LoopViewAdapter.OnLoopViewChangeListener {

    private static final String TAG = "starView";

    private ValueAnimator restAnimator = null;//回位动画

    private int loopRotationX = -27;//x轴旋转和轴旋转，y轴无效果

    private GestureDetector mGestureDetector = null;//手势类

    private int selectItem = 0;//当前选择项

    private int size = 0;//个数

    private float r = 0;//半径

    private float multiple = 1f;//倍数

    private float distance = multiple * r;//camera和观察的旋转物体距离， 距离越长,最大物体和最小物体比例越不明显

    private float angle = 0;    //旋转的角度

    private float last_angle = 0;    //最后的角度，用来记录上一次取消touch之后的角度

    private boolean touching = false;//正在触摸

    private OnItemSelectedListener onItemSelectedListener = null;//选择事件接口

    private LoopViewAdapter mAdapter;

    public LoopViewLayout(Context context) {
        this(context, null);
    }

    public LoopViewLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoopViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setGravity(Gravity.CENTER);
        mGestureDetector = new GestureDetector(context, getGeomeryController());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        updateLoopViews();
    }

    private GestureDetector.SimpleOnGestureListener getGeomeryController() {
        return new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

                angle += distanceX / 10; // 旋转速率
                updateLoopViews();

                return true;
            }
        };
    }


    // 更新view位置
    public void updateLoopViews() {
        int count = getChildCount();
        int measuredWidth = getMeasuredWidth();
        r = (measuredWidth - Utils.dp2px(71)) / 2f;
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            float radians = angle + 180 - (float) (i * 360f / size);
            float x0 = (float) Math.sin(Math.toRadians(radians)) * r;
            float y0 = (float) Math.cos(Math.toRadians(radians)) * r;
            float scale0 = (distance - y0) / (distance + r);//计算子view之间的比例，可以看到distance越大的话 比例越小，也就是大小就相差越小
            Log.i(TAG, "updateLoopViews: scale0----" + scale0);
            view.setScaleX(Math.max(scale0, 0.4f));//对view进行缩放
            view.setScaleY(Math.max(scale0, 0.4f));//对view进行缩放
            view.setAlpha(Math.max(scale0, 0.4f));

            float rotationX_y = (float) Math.sin(Math.toRadians(loopRotationX * Math.cos(Math.toRadians(radians)))) * r;
            view.setTranslationX(x0);
            view.setTranslationY(rotationX_y);

        }
        postInvalidate();
    }

    // 复位
    private void restPosition() {
        if (size == 0) {
            return;
        }
        float finall = 0;
        float part = 360f / size;//一份的角度
        if (angle < 0) {
            part = -part;
        }
        float minvalue = (int) (angle / part) * part;//最小角度
        float maxvalue = (int) (angle / part) * part + part;//最大角度
        if (angle >= 0) {//分为是否小于0的情况
            if (angle - last_angle > 0) {
                finall = maxvalue;
            } else {
                finall = minvalue;
            }
        } else {
            if (angle - last_angle < 0) {
                finall = maxvalue;
            } else {
                finall = minvalue;
            }
        }
        animRotationTo(finall);
    }


    // 动画
    private void animRotationTo(float finall) {
        if (angle == finall) {//如果相同说明不需要旋转
            return;
        }
        restAnimator = ValueAnimator.ofFloat(angle, finall);
        restAnimator.setInterpolator(new DecelerateInterpolator());//设置旋转减速插值器
        restAnimator.setDuration(300);

        restAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (!touching) {
                    angle = (Float) animation.getAnimatedValue();
                    updateLoopViews();
                }
            }
        });
        restAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!touching) {
                    selectItem = calculateItem();
                    if (selectItem < 0) {
                        selectItem = size + selectItem;
                    }
                    if (onItemSelectedListener != null) {
                        onItemSelectedListener.selected(selectItem, getChildAt(selectItem));
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        restAnimator.start();
    }

    // 通过角度计算是第几个item
    private int calculateItem() {

        int maxIndex = 0;
        float maxScale = 0;
        for (int i = 0; i < size; i++) {
            View view = getChildAt(i);
            float scaleX = view.getScaleX();
            if (scaleX >= maxScale) {
                maxScale = scaleX;
                maxIndex = i;
            }
        }
        return (int) getChildAt(maxIndex).getTag();
    }

    // 触摸方法
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                last_angle = angle;
                touching = true;
                break;
        }

        boolean sc = mGestureDetector.onTouchEvent(event);
        if (sc) {
            this.getParent().requestDisallowInterceptTouchEvent(true);//通知父控件勿拦截本控件
        }
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            touching = false;
            restPosition();
            return true;
        }

        return true;
    }

    // 获取角度
    public float getAngle() {
        return angle;
    }

    // 设置角度
    public void setAngle(float angle) {
        this.angle = angle;
    }

    // 获取距离
    public float getDistance() {
        return distance;
    }

    // 设置距离
    public void setDistance(float distance) {
        this.distance = distance;
    }

    // 获取选择是第几个item
    public int getSelectItem() {
        return selectItem;
    }


    // 设置指定位置  选中哪一个
    public void setSelectItem(int pos) {
        int count = getChildCount();
        int currentIndex = calculateItem();
        if (currentIndex == pos) {
            return;
        }
        //目标切换的view
        View targetView = getChildAt(pos);
        float perAngle;
        //当前位置和目标位置的差值(不是直接相减 而是位置相差多少个距离）
        int difPos;
        if (targetView.getTranslationX() >= 0) {//在右边
            if (pos >= currentIndex) {
                difPos = Math.abs(pos - currentIndex);
            } else {
                difPos = Math.abs(pos + count - currentIndex);
            }
            perAngle = 360f / size;
        } else {//在左边
            if (currentIndex >= pos) {
                difPos = Math.abs(currentIndex - pos);
            } else {
                difPos = Math.abs(currentIndex + count - pos);
            }
            perAngle = -360f / size;
        }
        animRotationTo(angle + perAngle * difPos);
    }

    // 选中回调接口实现
    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    // 设置倍数
    public LoopViewLayout setMultiple(float mMultiple) {
        this.multiple = mMultiple;
        distance = multiple * r;
        return this;
    }

    public ValueAnimator getRestAnimator() {
        return restAnimator;
    }

    @Override
    public LoopViewAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void setAdapter(LoopViewAdapter adapter) {
        if (adapter == null || mAdapter != null) {
            return;
        }

        this.mAdapter = adapter;
        mAdapter.setOnLoopViewChangeListener(this);
    }

    // 重置
    public void reset() {
        removeAllViews();
        selectItem = 0;
        angle = 0;
        last_angle = 0;
    }

    // 初始化view
    public void initLoopViews() {
        reset();
        if (mAdapter != null) {
            size = mAdapter.getCount();
            if (size == 0) return;
            for (int i = 0; i < size; i++) {
                View view = mAdapter.getView(i, getChildAt(i), this); //获取指定的子view
                view.setTag(i);
                addView(view);
                // 设置点击事件
            }
        }
    }


    @Override
    public void notifyDataSetChanged() {
        initLoopViews();
        updateLoopViews();
        if (onItemSelectedListener != null) {
            onItemSelectedListener.selected(selectItem, getChildAt(selectItem));
        }
    }
}
