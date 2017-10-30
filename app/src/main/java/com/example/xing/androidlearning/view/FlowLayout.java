package com.example.xing.androidlearning.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by xing on 5/25/16.
 */
public class FlowLayout extends ViewGroup {

    public final String TAG = "FlowLayout";

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        //去掉padding和margin后实际的宽和高
        int parentUsableWidth = sizeWidth - getPaddingLeft() - getPaddingRight();
        int parentUsableHeight = sizeHeight - getPaddingTop() - getPaddingBottom();

        int parentWidth = 0;
        int parentHeight = 0;

        int currentLineWidth = 0;
        int currentLineHeight = 0;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);

            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
            int childWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            Log.d(TAG, "childWidth: " + childWidth + ", childHeight: " + childHeight);
            if (currentLineWidth + childWidth > parentUsableWidth) {
                parentWidth = Math.max(currentLineWidth, parentWidth);
                parentHeight += currentLineHeight;
                currentLineWidth = childWidth;
                currentLineHeight = childHeight;
            } else {
                currentLineWidth += childWidth;
                currentLineHeight = Math.max(childHeight, currentLineHeight);
            }
            //遍历到最后一个ChildView时未换行, 单独处理
            if (i == childCount - 1) {
                parentWidth = Math.max(currentLineWidth, parentWidth);
                parentHeight += currentLineHeight;
            }

            //计算好父控件的宽高之后我们将它们set进去
            setMeasuredDimension(modeWidth == MeasureSpec.EXACTLY ? sizeWidth : parentWidth + getPaddingLeft() + getPaddingRight(),
                    modeHeight == MeasureSpec.EXACTLY ? sizeHeight : parentHeight + getPaddingTop() + getPaddingBottom());
            Log.d(TAG, "parentWidth: " + parentWidth + ", parentHeight: " + parentHeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        //获得在onMeasure方法中计算后得到的这个FlowLayout的宽度
        int parentUsableWidth = getWidth() - getPaddingLeft() - getPaddingRight();

        int currentRight = getPaddingLeft();
        int currentTop = getPaddingTop();

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
            int childWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            if (currentRight + childWidth > parentUsableWidth) {
                currentTop += childHeight;
                currentRight = getPaddingLeft();
            }
            childView.layout(currentRight, currentTop, currentRight + childWidth, currentTop + childHeight);
            currentRight += childWidth;
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
