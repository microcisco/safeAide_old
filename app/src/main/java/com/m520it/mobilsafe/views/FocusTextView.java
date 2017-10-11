package com.m520it.mobilsafe.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author 王维波
 * @time 2016/9/5  17:01
 * @desc ${TODD}
 */
public class FocusTextView extends TextView {
    public FocusTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusTextView(Context context) {
        super(context);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
