package com.example.xing.androidlearning.util;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by xing on 5/25/16.
 */
public class CommomUtil {

    /**
     * 将unit对应的单位的大小转为像素
     *
     * @param unit TypeValue.COMPLEX_UNIT_*
     * @param size size
     * @return size in px
     */
    public static float getRawSize(int unit, float size) {
        Resources r = Resources.getSystem();
        return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
    }

}
