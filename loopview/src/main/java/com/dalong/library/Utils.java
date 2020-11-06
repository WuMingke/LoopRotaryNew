package com.dalong.library;

import android.content.res.Resources;
import android.util.TypedValue;

class Utils {
    public static int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }
}
