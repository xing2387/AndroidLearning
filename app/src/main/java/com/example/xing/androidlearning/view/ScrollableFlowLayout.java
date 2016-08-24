package com.example.xing.androidlearning.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import com.example.xing.androidlearning.R;

import java.util.LinkedList;

/**
 * Created by xing on 5/25/16.
 */
public class ScrollableFlowLayout extends ViewGroup {

    public final String TAG = "FlowLayout";

    private LinkedList<Integer> mLineWidthList = new LinkedList<>();
    private int totalLineHeight;
    private int mChildGravity;
    private int touchSlop;

    private LayoutTransition mLayoutTransition;

    public ScrollableFlowLayout(Context context) {
        this(context, null);
    }

    public ScrollableFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollableFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.flowlayout);
        mChildGravity = typedArray.getInt(R.styleable.flowlayout_child_gravity, Gravity.LEFT);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mLayoutTransition = new LayoutTransition();
        this.setLayoutTransition(mLayoutTransition);
        mLayoutTransition.setDuration(100);
        customLayoutTransition();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        mLineWidthList.clear();
        totalLineHeight = 0;
        int totalWidth = 0;

        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        //去掉padding和margin后实际的宽和高
        int parentUsableWidth = sizeWidth - getPaddingLeft() - getPaddingRight();
        int parentUsableHeight = sizeHeight - getPaddingTop() - getPaddingBottom();
        Log.e(TAG, "parentUsableWidth: " + parentUsableWidth + ", parentUsableHeight: " + parentUsableHeight);

        int currentLineWidth = 0;
        int currentLineHeight = 0;

        int childCount = getChildCount();
        if (childCount <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            //测量子View
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            Log.d(TAG, String.format("measureChildWithMargins(%s,%s,%s,%s)", widthMeasureSpec, currentLineWidth, heightMeasureSpec, totalLineHeight));

            //带Margin的
            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
            int childWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            Log.d(TAG, "childWidth: " + childWidth + ", childHeight: " + childHeight +
                    String.format(", child margin:%s,%s,%s,%s", lp.leftMargin, lp.rightMargin, lp.topMargin, lp.bottomMargin));
            if (currentLineWidth + childWidth > parentUsableWidth) {
                //换行处理
                Log.v(TAG, "换行," + (currentLineWidth + childWidth) + "," + parentUsableWidth);
                totalLineHeight += currentLineHeight;   //累加高度
                mLineWidthList.add(currentLineWidth);   //记录当前行宽
                totalWidth = Math.max(currentLineWidth, totalWidth);
                currentLineWidth = childWidth;
                currentLineHeight = childHeight;
            } else {
                currentLineWidth += childWidth;
                currentLineHeight = Math.max(childHeight, currentLineHeight);
            }

            //遍历到最后一个ChildView时未换行, 单独处理
            if (i == childCount - 1) {
                Log.v(TAG, "换行," + (currentLineWidth + childWidth) + "," + parentUsableWidth);
                totalLineHeight += currentLineHeight;
                mLineWidthList.add(currentLineWidth);
                totalWidth = Math.max(currentLineWidth, totalWidth);
            }

            //根据需要，是否要将子View总的宽高设为layout的宽高
            setMeasuredDimension(modeWidth == MeasureSpec.EXACTLY ? sizeWidth : totalWidth + getPaddingLeft() + getPaddingRight(),
                    modeHeight == MeasureSpec.EXACTLY ? sizeHeight : totalLineHeight + getPaddingTop() + getPaddingBottom());
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        //获得在onMeasure方法中计算后得到的这个FlowLayout的宽度, 用的currentRight包含了getPaddingLeft，所以可用大小要加回去
        int parentUsableWidth = getWidth() - getPaddingLeft() - getPaddingRight() + getPaddingLeft();
        Log.e(TAG, "layout parentUsableWidth: " + parentUsableWidth);

        int childCount = getChildCount();
        if (childCount <= 0) {
            //super是个抽象方法。。
            return;
        }

        int currLine = 0; //当前行游标
        Point point = getCurrentTopLeft(getLeft(), getTop(), getHeight(), getWidth(), mLineWidthList.get(currLine++));
        int currentRight = point.x; //代表左上角点的两个坐标
        int currentTop = point.y;

        int currentLineHeight = 0;

        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
            int childWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            if (currentRight + childWidth > parentUsableWidth) {
                //换行处理
                currentTop += currentLineHeight;
                currentRight = getCurrentTopLeft(getLeft(), getTop(), getHeight(), getWidth(), mLineWidthList.get(currLine++)).x;
                currentLineHeight = 0;
            }

            Log.d(TAG, "child layout, top: " + currentTop + ", left: " + currentRight);
            childView.layout(currentRight + lp.leftMargin, currentTop + lp.topMargin,
                    currentRight + childWidth - lp.rightMargin, currentTop + childHeight - lp.bottomMargin);
            //为下一个view的layout设置当前状态
            currentLineHeight = Math.max(currentLineHeight, childHeight);
            currentRight += childWidth;
        }

        //纠正一下子View的整体位置，当有上下滑动过，纠正位移是必要的
        if (isViewAdded || isViewRemoved) {
            correctScrollY();
            isViewAdded = false;
            isViewRemoved = false;
        }
    }

    /**
     * 根据对齐方式计算当前行最左上角那个点的坐标
     * @param lineWidth 当前行宽
     * @return Point x代表当前行的起始的横坐标(left)，y代表当前行顶部的坐标(top)
     */
    private Point getCurrentTopLeft(int parentLeft, int parentTop, int parentHeight, int parentWidth, int lineWidth) {
        Log.d(TAG, String.format("getCurrentTopLeft(%s,%s,%s,%s,%s) ", parentLeft, parentTop, parentHeight, parentWidth, lineWidth) + "totalLineHeight:" + totalLineHeight);
        Point point = new Point(getPaddingLeft(), getPaddingTop());
        if ((mChildGravity & Gravity.CENTER) == Gravity.CENTER) {
            Log.d(TAG, "child gravity = CENTER");
            point.x = (parentWidth - lineWidth) / 2;
            point.y = (parentHeight - totalLineHeight) / 2;
        }

        if ((mChildGravity & Gravity.CENTER_VERTICAL) == Gravity.CENTER_VERTICAL) {
            Log.d(TAG, "child gravity = CENTER_VERTICAL");
            point.y = (parentHeight - totalLineHeight) / 2;
        }
        if ((mChildGravity & Gravity.CENTER_HORIZONTAL) == Gravity.CENTER_HORIZONTAL) {
            Log.d(TAG, "child gravity = CENTER_HORIZONTAL");
            point.x = (parentWidth - lineWidth) / 2;
        }
        if ((mChildGravity & Gravity.LEFT) == Gravity.LEFT) {
            Log.d(TAG, "child gravity = LEFT");
            point.x = getPaddingLeft();
        }
        if ((mChildGravity & Gravity.RIGHT) == Gravity.RIGHT) {
            Log.d(TAG, "child gravity = RIGHT");
            point.x = parentWidth - getPaddingLeft() - lineWidth;
        }
        if ((mChildGravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
            Log.d(TAG, "child gravity = BOTTOM");
            point.y = parentHeight - totalLineHeight;
        }
        if ((mChildGravity & Gravity.TOP) == Gravity.TOP) {
            Log.d(TAG, "child gravity = TOP");
            point.y = getPaddingTop();
        }
        return point;
    }

    private float mLastMotionY;
    private boolean scrollEnable;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onInterceptTouchEvent, ACTION_DOWN");
                mLastMotionY = ev.getY();
                scrollEnable = false;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onInterceptTouchEvent, ACTION_MOVE");
                if (Math.abs(mLastMotionY - ev.getY()) > touchSlop) {
                    Log.d(TAG, "onInterceptTouchEvent, return true");
                    return true;    //不传递
                }
        }
        return false;
    }

    private boolean isViewAdded = false;
    private boolean isViewRemoved = false;

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        isViewAdded = true;
    }

    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        isViewRemoved = true;
    }

    /**
     * 自定义添加删除View的动画效果
     */
    public void customLayoutTransition() {
        //为LayoutTransition设置动画及动画类型
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(null, "alpha", 0.0f, 0.3f, 0.6f, 0.8f, 0.9f, 1.0f).setDuration(100);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        mLayoutTransition.setAnimator(LayoutTransition.APPEARING, fadeIn);
        fadeIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                View view = (View) ((ObjectAnimator) animation).getTarget();
                view.setAlpha(1f);
            }
        });
    }


    /**
     * 在滑动过之后，添加或者删除View之后子View整体的Y位移需要调整
     *     要在onLayout之后用，不然拿到的top跟bottom是添加之前的值
     */
    void correctScrollY() {
        int firstChildTop = getChildAt(0).getTop();
        int lastChildBottom = getChildAt(getChildCount() - 1).getBottom();
        Log.d(TAG, "correctScrollY , firstChildTop=" + firstChildTop +
                ", lastChildBottom=" + lastChildBottom);
        if (firstChildTop > 0 && lastChildBottom < getHeight()) {
            Log.e(TAG, "correctScrollY");
            scrollTo(0, 0);
        } else {
            Point p = getCurrentTopLeft(getLeft(), getTop(), getHeight(), getWidth(), mLineWidthList.get(0));
            if (isViewAdded) {
                scrollTo(0, totalLineHeight - getHeight() + p.y);
            } else if (isViewRemoved && lastChildBottom - getScrollY() < getHeight()) {
                scrollTo(0, totalLineHeight - getHeight() + p.y);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        float y = event.getY();
        int firstChildTop = getChildAt(0).getTop();
        int lastChildBottom = getChildAt(getChildCount() - 1).getBottom();
        if (firstChildTop > 0 && lastChildBottom < getHeight()) {   //子View没有超出layout，不滑动
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouchEvent, ACTION_DOWN");
                mLastMotionY = y; //记住开始落下的点
                break;
            case MotionEvent.ACTION_MOVE:
                int detaY = (int) (mLastMotionY - y);
                Log.d(TAG, "onTouchEvent ACTION_MOVE, detaY=" + detaY + ", touchSlop=" + touchSlop);
                if (scrollEnable || Math.abs(detaY) > touchSlop) {
                    scrollBy(0, detaY);
                    mLastMotionY = y;
                    scrollEnable = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onTouchEvent ACTION_UP, firstChildTop=" + firstChildTop +
                        ", lastChildBottom=" + lastChildBottom +
                        ", ScrollY=" + getScrollY());
                Point point = getCurrentTopLeft(getLeft(), getTop(), getHeight(), getWidth(), mLineWidthList.get(0));
                if (firstChildTop - getScrollY() > 0) { //第一个子View的顶部在layout的top下
                    scrollTo(0, point.y);
                } else if (lastChildBottom - getScrollY() < getHeight()) {  //最后一个子View的底部在layout的bottom上
                    scrollTo(0, totalLineHeight - getHeight() + point.y);
                }
        }
        return true;
    }


    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
    }

    public int getChildGravity() {
        return mChildGravity;
    }

    public void setChildGravity(int childGravity) {
        this.mChildGravity = childGravity;
    }

}
