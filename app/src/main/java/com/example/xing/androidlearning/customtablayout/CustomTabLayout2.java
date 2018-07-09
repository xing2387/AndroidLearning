package com.example.xing.androidlearning.customtablayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xing.androidlearning.R;
import com.example.xing.androidlearning.util.CommomUtil;

import java.util.ArrayList;

public class CustomTabLayout2 extends FrameLayout {

    private float mTextSize = 17;   //sp
    private int mTabPadding = (int) CommomUtil.getRawSize(TypedValue.COMPLEX_UNIT_SP, 18);   //px
    @ColorInt
    private int mIndicatorColor = Color.WHITE;
    private int mIndicatorResId = -1;
    //    private View mIndicatorImg;
    private boolean mIsBoldText = true;
    @ColorInt
    private int mSelectedTextColor = Color.parseColor("#000000");
    @ColorInt
    private int mNormalTextColor = Color.parseColor("#99989ABF");
    private int mIndicatorWidth = -1;      //px
    private int mIndicatorStrokeWidth = (int) CommomUtil.getRawSize(TypedValue.COMPLEX_UNIT_DIP, 3);      //px

    private Paint mIndicatorPaint;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private Bitmap mIndicatorBitmap;

    private LinearLayout mTabContainerLayout;
    private int selectedPosition = -1;
    private int mIndicatorImgHeight = 0;
    private ArrayList<Tab> mTabs = new ArrayList<>();
    private ArrayList<Integer> mIndicatorLeft = new ArrayList<>();

    public CustomTabLayout2(Context context) {
        this(context, null);
    }

    public CustomTabLayout2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTabLayout2(@NonNull Context context, @Nullable AttributeSet attrs,
                            @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
                R.styleable.CustomTabLayout2, defStyleAttr, 0);
        mTextSize = typedArray.getDimension(R.styleable.CustomTabLayout2_tabTextSize, mTextSize);
        mTabPadding = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout2_tabPadding, mTabPadding);
        mIndicatorColor = typedArray.getColor(R.styleable.CustomTabLayout2_indicatorColor, mIndicatorColor);
        mIndicatorResId = typedArray.getResourceId(R.styleable.CustomTabLayout2_indicatorImg, mIndicatorResId);
        mIsBoldText = typedArray.getBoolean(R.styleable.CustomTabLayout2_boldSelected, mIsBoldText);
        mNormalTextColor = typedArray.getColor(R.styleable.CustomTabLayout2_tabTextColor, mNormalTextColor);
        mSelectedTextColor = typedArray.getColor(R.styleable.CustomTabLayout2_selectTextColor, mSelectedTextColor);
        mIndicatorWidth = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout2_indicatorWidth, mIndicatorWidth);
        mIndicatorStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout2_indicatorStrokeWidth, mIndicatorStrokeWidth);
        typedArray.recycle();

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setupViewPager(@NonNull ViewPager viewPager) {
        if (mTabContainerLayout == null) {
            mTabContainerLayout = new LinearLayout(getContext());
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            addView(mTabContainerLayout, lp);
        } else {
            removeAllTabs();
        }
        addIndicator();
        this.mViewPager = viewPager;
        this.mPagerAdapter = viewPager.getAdapter();
        if (mPagerAdapter != null) {
            final int adapterCount = mPagerAdapter.getCount();
            for (int i = 0; i < adapterCount; i++) {
                CharSequence pageTitle = mPagerAdapter.getPageTitle(i);
                TextView textView = getTabView();
                textView.setText(String.valueOf(pageTitle));
                setNormalStyle(textView);
                this.mTabs.add(new Tab(textView, false));
                textView.measure(0, 0);
                mTabContainerLayout.addView(textView);
            }
            if (adapterCount > 0) {
                final int curItem = mViewPager.getCurrentItem();
                if (curItem != selectedPosition) {
                    selectTab(curItem);
                }
            }
        }
    }

    private boolean isImgIndicator() {
        return mIndicatorResId > 0;
    }

    public void addIndicator() {
        if (!isImgIndicator()) {      //画下划线
            if (mIndicatorPaint == null) {
                mIndicatorPaint = new Paint();
                mIndicatorPaint.setColor(mIndicatorColor);
                mIndicatorPaint.setStrokeWidth(mIndicatorStrokeWidth);
            }
        } else {                        //添加图片
            mIndicatorPaint = new Paint();
            Drawable drawable = getContext().getResources().getDrawable(mIndicatorResId);
            if (drawable instanceof BitmapDrawable) {
                mIndicatorBitmap = ((BitmapDrawable) drawable).getBitmap();
                mIndicatorWidth = drawable.getIntrinsicWidth();
                mIndicatorImgHeight = drawable.getIntrinsicHeight();
            }
        }
    }

    private int indicatorTop;

    private void drawLineIndicator(Canvas canvas) {
        if (selectedPosition >= 0 && selectedPosition < mIndicatorLeft.size() - 1) {
            int lineLength = mIndicatorWidth;
            if (lineLength < 0) {
                lineLength = mIndicatorLeft.get(selectedPosition + 1) - mIndicatorLeft.get(selectedPosition);
            }
            int indicatorLeft = mIndicatorLeft.get(selectedPosition);
            canvas.save();
            canvas.translate(indicatorLeft, indicatorTop + mIndicatorStrokeWidth / 2);
            canvas.drawLine(0, 0, lineLength, 0, mIndicatorPaint);
            canvas.restore();
        }
    }

    private void drawImgIndicator(Canvas canvas) {
        if (selectedPosition > 0 && selectedPosition < mIndicatorLeft.size() - 1) {
            int indicatorLeft = mIndicatorLeft.get(selectedPosition);
            canvas.save();
            canvas.translate(indicatorLeft, indicatorTop);
            canvas.drawBitmap(mIndicatorBitmap, 0, 0, mIndicatorPaint);
            canvas.restore();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (!isImgIndicator()) {
            drawLineIndicator(canvas);
        } else {
            drawImgIndicator(canvas);
        }
        super.dispatchDraw(canvas);
    }


    public TextView getTabView() {
        TextView tab = new TextView(getContext());
        if (mTextSize > 0) {
            tab.setTextSize(mTextSize);
        }
        tab.setGravity(Gravity.CENTER);
        ViewGroup.MarginLayoutParams marginLayoutParams =
                new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tab.setLayoutParams(marginLayoutParams);
        tab.setPadding(mTabPadding, 0, mTabPadding, 0);
        return tab;
    }

    private void removeAllTabs() {
        if (mTabContainerLayout != null) {
            mTabContainerLayout.removeAllViews();
        }
        mIndicatorLeft.clear();
        mTabs.clear();
    }

    private void selectTab(int index) {
        if (index < 0 || index >= mTabs.size()) {
            return;
        }
        if (index == selectedPosition) {
            return;
        }
        unSelectTab(selectedPosition);
        Tab tab = mTabs.get(index);
        if (tab.isSelected) {
            return;
        }
        setSelectedStyle(tab.textView);
        tab.isSelected = true;
        selectedPosition = index;
    }

    private void unSelectTab(int index) {
        if (index < 0 || index >= mTabs.size()) {
            return;
        }
        Tab tab = mTabs.get(index);
        if (!tab.isSelected) {
            return;
        }
        setNormalStyle(tab.textView);
        tab.isSelected = false;
    }

    private void setNormalStyle(TextView textView) {
        if (mIsBoldText) {
            Paint paint = textView.getPaint();
            paint.setFakeBoldText(false);
        }
        textView.setTextColor(mNormalTextColor);
    }

    private void setSelectedStyle(TextView textView) {
        if (mIsBoldText) {
            Paint paint = textView.getPaint();
            paint.setFakeBoldText(true);
        }
        textView.setTextColor(mSelectedTextColor);
    }

    private void calcIndicatorLeft() {
        mIndicatorLeft.clear();
        int tabCount = mTabContainerLayout.getChildCount();
        int tabLayoutWidth = mTabContainerLayout.getMeasuredWidth();
        int totalLeft = 0;
        for (int i = 0; i < tabCount && totalLeft < tabLayoutWidth; i++) {
            View view = mTabContainerLayout.getChildAt(i);
            int tabWidth = view.getMeasuredWidth();
            if (mIndicatorWidth < 0) {
                mIndicatorLeft.add(totalLeft);
            } else {
                mIndicatorLeft.add(totalLeft + (tabWidth - mIndicatorWidth) / 2);
            }
            totalLeft += tabWidth;
        }
        mIndicatorLeft.add(totalLeft);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (isImgIndicator()) {
            indicatorTop = h - mIndicatorImgHeight;
        } else {
            indicatorTop = h - mIndicatorStrokeWidth;
        }
        calcIndicatorLeft();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private class Tab {
        TextView textView;
        boolean isSelected;

        public Tab(TextView textView, boolean isSelected) {
            this.textView = textView;
            this.isSelected = isSelected;
        }
    }

    private class RedDot {
        View vDot;
        int num;

        public RedDot(View vDot, int num) {
            this.vDot = vDot;
            this.num = num;
        }
    }

}
