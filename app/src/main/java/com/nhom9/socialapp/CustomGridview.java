package com.nhom9.socialapp;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class CustomGridview extends GridView {
    public CustomGridview(Context context) {
        super(context);
    }

    public CustomGridview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomGridview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomGridview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec;
        if(getLayoutParams().height == LayoutParams.WRAP_CONTENT){
            heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2, MeasureSpec.AT_MOST);
        }else {
            heightSpec = heightMeasureSpec;
        }
        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}
