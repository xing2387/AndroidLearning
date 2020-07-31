package com.example.xing.androidlearning.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.viewpager.widget.ViewPager;

/**
 * Created by james on 2016/7/8.
 */
public class BottomMenuBar extends LinearLayout implements ViewPager.OnPageChangeListener {

    final private String TAG = "BottomMenuBar";
    private ViewPager viewPager;

    public BottomMenuBar(Context context) {
        this(context, null);
    }

    public BottomMenuBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
        viewPager.addOnPageChangeListener(this);
    }

    private boolean isInited = false;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount(); i++) {
            final int position = i;
            getChildAt(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewPager != null) {
                        viewPager.setCurrentItem(position, false);
                    }
                    initChild();
                }
            });
        }
    }

    private int currentSelectedPosition = 0;
    private int fromPosition = 0;
    private int toPosition = 0;
    private boolean isSwapRight = true;

    private void initChild() {
        for (int i = 0; i < getChildCount(); i++) {
            if (i == currentSelectedPosition) {
                swap((ViewGroup) getChildAt(i), -2);
            } else {
                swap((ViewGroup) getChildAt(i), -1);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (!isInited) {
            initChild();
            isInited = true;
        }
        if (positionOffset == 0) {
            currentSelectedPosition = position;
        } else {
            //滑动时position只有当positionOffset==1的时候才变化，所以往右滑position到最后才变化，往左滑是一开始就变化，根据这个设置下面三个变量
            isSwapRight = currentSelectedPosition > position;
            toPosition = isSwapRight ? position : position + 1;
            fromPosition = isSwapRight ? position + 1 : position;
            //滑进去时
            swap((ViewGroup) getChildAt(toPosition), (isSwapRight ? 1 - positionOffset : positionOffset));
            //滑走时
            swap((ViewGroup) getChildAt(isSwapRight ? position + 1 : position), (isSwapRight ? positionOffset : 1 - positionOffset));
//            Log.d(TAG, currentSelectedPosition + "->" + position + ", " + positionOffset + ", " + (isSwapRight ? "left" : "right"));
        }
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //这个方法不是滑动结束就立即调用的。。。要过个零点几秒
        if (state == ViewPager.SCROLL_STATE_IDLE) {
//            Log.e(TAG, "SCROLL_STATE_IDLE");
        }
    }

    /**
     * 滑动时通过设置叠加的几张图片的透明度实现渐变效果
     *
     * @param viewGroup      每个“按钮”（按钮用framelayout，包含所需的几张图片，第一张叠在最下面，\
     *                       往上依次是：选中时的效果图、没有选中时的效果图、中间挖空的作为蒙版的图；\
     *                       按钮底部是两个TextView，从底往上依次是选中时的、没有选中时的）
     * @param positionOffset ViewPager中onPageScrolled的参数直接传进来
     */
    public void swap(ViewGroup viewGroup, float positionOffset) {
        if (positionOffset < 0) {
            //用-2代表选中
            if (positionOffset == -2) {
                viewGroup.getChildAt(1).setAlpha(0f);
                viewGroup.getChildAt(2).setAlpha(0f);
                viewGroup.getChildAt(4).setAlpha(0f);
            }
            //-1代表没有选中
            if (positionOffset == -1) {
                viewGroup.getChildAt(1).setAlpha(1f);
                viewGroup.getChildAt(2).setAlpha(1f);
                viewGroup.getChildAt(4).setAlpha(1f);
            }
        } else {
            viewGroup.getChildAt(4).setAlpha(1 - positionOffset);
            //小于0.5时设置中间层的透明度
            if (positionOffset <= 0.5) {
                viewGroup.getChildAt(1).setAlpha(1 - positionOffset * 2);
                viewGroup.getChildAt(2).setAlpha(1f);
            } else {
                //大于0.5时设置顶层蒙版的透明度
                viewGroup.getChildAt(1).setAlpha(0f);
                viewGroup.getChildAt(2).setAlpha((float) (1 - (positionOffset - 0.5) * 2));
            }
        }
    }
}
