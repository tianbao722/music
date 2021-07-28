package com.example.music.zview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.recyclerview.widget.RecyclerView;

import com.example.music.R;

public class MaxHeightRecyclerView extends RecyclerView {
    private int mHeightMax;
 
    public MaxHeightRecyclerView(Context context) {
        super(context);
    }
 
    public MaxHeightRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }
 
    public MaxHeightRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }
 
    private void initialize(Context context, AttributeSet attrs) {
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.maxHeight_RecyclerView);
        mHeightMax = arr.getLayoutDimension(R.styleable.maxHeight_RecyclerView_maxHeightt, mHeightMax);
        arr.recycle();
    }
 
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mHeightMax > 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mHeightMax, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
