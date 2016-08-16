package com.alex9xu.selectpicture.utils;

import android.content.Context;

/**
 * Created by Alex on 2016/8/16.
 */
public class ImageSizeUtil {

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
