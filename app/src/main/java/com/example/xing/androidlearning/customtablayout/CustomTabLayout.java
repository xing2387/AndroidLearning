package com.example.xing.androidlearning.customtablayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.xing.androidlearning.R;
import com.example.xing.androidlearning.util.DisplayUtil;

import java.util.ArrayList;

public class CustomTabLayout extends HorizontalScrollView {
    private static final String TAG = "CustomTabLayout2";

    private float mTextSize = 17;   //sp
    private int mTabPadding = DisplayUtil.dip2px(getContext(), 18);   //px
    @ColorInt
    private int mIndicatorColor = Color.WHITE;
    private int mIndicatorResId = -1;
    private boolean mIsBoldText = true;
    @ColorInt
    private int mSelectedTextColor = Color.parseColor("#000000");
    @ColorInt
    private int mNormalTextColor = Color.parseColor("#99989ABF");
    private int mIndicatorWidth = -1;      //px
    private int mIndicatorStrokeWidth = DisplayUtil.dip2px(getContext(), 3);      //px
    private int mIndicatorPadding = 0;
    private int mRedDotBgResId = -1;
    private int mSmallRedDotBgResId = -1;
    private int mRedDotGravity = Gravity.TOP | Gravity.END;
    private int mRedDotMarginTop = DisplayUtil.dip2px(getContext(), 9);                       //px
    private int mRedDotMarginBottom = 0;
    private int mRedDotMarginRight = DisplayUtil.dip2px(getContext(), 9);                       //px
    private int mRedDotMarginLeft = 0;
    private int mScrollThreshold = DisplayUtil.dip2px(getContext(), 100);

    private Paint mIndicatorPaint;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private Bitmap mIndicatorBitmap;

    //    private HorizontalScrollView mScrollWrapper;
    private LinearLayout mTabContainerLayout;
    private int selectedPosition = -1;
    private int mIndicatorImgHeight = 0;
    private int mTranslationX = 0;
    private ArrayList<Tab> mTabs = new ArrayList<>();
    private ArrayList<Integer> mIndicatorLeftList = new ArrayList<>();
    private ArrayList<Integer> mIndicatorWidthList = new ArrayList<>();

    private OnClickListener mOnTabClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() instanceof Integer) {
                int index = (int) v.getTag();
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(index);
                }
            }
        }
    };

    public CustomTabLayout(Context context) {
        this(context, null);
    }

    public CustomTabLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTabLayout(@NonNull Context context, @Nullable AttributeSet attrs,
                           @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
                R.styleable.CustomTabLayout, defStyleAttr, 0);
        mTextSize = typedArray.getDimension(R.styleable.CustomTabLayout_tabTextSize, mTextSize);
        mTabPadding = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout_tabPadding, mTabPadding);
        mIndicatorColor = typedArray.getColor(R.styleable.CustomTabLayout_indicatorColor, mIndicatorColor);
        mIndicatorResId = typedArray.getResourceId(R.styleable.CustomTabLayout_indicatorImg, mIndicatorResId);
        mIsBoldText = typedArray.getBoolean(R.styleable.CustomTabLayout_boldSelected, mIsBoldText);
        mNormalTextColor = typedArray.getColor(R.styleable.CustomTabLayout_tabTextColor, mNormalTextColor);
        mSelectedTextColor = typedArray.getColor(R.styleable.CustomTabLayout_selectTextColor, mSelectedTextColor);
        mIndicatorWidth = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout_indicatorWidth, mIndicatorWidth);
        mIndicatorStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout_indicatorStrokeWidth, mIndicatorStrokeWidth);
        mIndicatorPadding = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout_indicatorPadding, mIndicatorPadding);
        mRedDotBgResId = typedArray.getResourceId(R.styleable.CustomTabLayout_redDotBgResId, mRedDotBgResId);
        mSmallRedDotBgResId = typedArray.getResourceId(R.styleable.CustomTabLayout_smallDotBgResId, mSmallRedDotBgResId);
        mRedDotMarginLeft = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout_redDotMarginLeft, mRedDotMarginLeft);
        mRedDotMarginTop = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout_redDotMarginTop, mRedDotMarginTop);
        mRedDotMarginRight = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout_redDotMarginRight, mRedDotMarginRight);
        mRedDotMarginBottom = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout_redDotMarginBottom, mRedDotMarginBottom);
        mRedDotGravity = typedArray.getInteger(R.styleable.CustomTabLayout_redDotGravity, mRedDotGravity);
        typedArray.recycle();

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setHorizontalScrollBarEnabled(false);
    }

    private void addTabContainerLayout() {
        if (mTabContainerLayout != null) {
            return;
        }

        mTabContainerLayout = new LinearLayout(getContext());
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mTabContainerLayout.setOrientation(LinearLayout.HORIZONTAL);
        addView(mTabContainerLayout, lp);
    }

    public void setupViewPager(@NonNull ViewPager viewPager) {
        if (mTabContainerLayout == null) {
            addTabContainerLayout();
        } else {
            removeAllTabs();
        }
        addIndicator();
        this.mViewPager = viewPager;
        this.mPagerAdapter = viewPager.getAdapter();
        this.mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                scroll(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                selectTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        if (mPagerAdapter != null) {
            final int adapterCount = mPagerAdapter.getCount();
            for (int i = 0; i < adapterCount; i++) {
                CharSequence pageTitle = mPagerAdapter.getPageTitle(i);
                TextView textView = genTabView();
                textView.setText(String.valueOf(pageTitle));
                setNormalStyle(textView);
                Tab tab = new Tab(i, textView, false);
                this.mTabs.add(tab);
                tab.rootView.setOnClickListener(mOnTabClickListener);
                mTabContainerLayout.addView(tab.rootView);
            }
            if (adapterCount > 0) {
                final int curItem = mViewPager.getCurrentItem();
                if (curItem != selectedPosition) {
                    selectTab(curItem);
                }
            }
        }
    }


    public void addUnreadDot(int index) {
        this.addUnreadDot(index, null);
    }

    public void addUnreadDot(int index, String count) {
        this.addUnreadDot(index, mRedDotBgResId, count);
    }

    /**
     * 有数字的未读角标
     */
    public void addUnreadDot(int index, int dotBgResId, String count) {
        addUnreadDot(index, genRedDotTextView(dotBgResId), count);
    }

    public void addUnreadDot(int index, TextView dotTextView, String count) {
        if (count == null || index < 0 || index > mTabs.size()) {
            return;
        }
        Tab tab = mTabs.get(index);
        dotTextView.setText(count);
        RedDot redDot = new RedDot(dotTextView, count);
        tab.addRedDot(redDot);
    }

    private final int px_14dp = DisplayUtil.dip2px(getContext(), 14);
    private final int px_3dp = DisplayUtil.dip2px(getContext(), 3);

    public TextView genRedDotTextView(int dotBgResId) {
        TextView textView = new TextView(getContext());
        MarginLayoutParams lp = new MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, px_14dp);
        lp.leftMargin = mRedDotMarginLeft;
        lp.topMargin = mRedDotMarginTop;
        lp.rightMargin = mRedDotMarginRight;
        lp.bottomMargin = mRedDotMarginBottom;
        textView.setPadding(px_3dp, 0, px_3dp, 0);
        textView.setLayoutParams(lp);
        textView.setTextSize(10);
        textView.setGravity(Gravity.CENTER);
        if (dotBgResId > 0) {
            textView.setBackgroundResource(dotBgResId);
        }
        textView.setMinWidth(px_14dp);
        textView.setTextColor(getResources().getColor(android.R.color.white));
        return textView;
    }

    /**
     * 只是一个小点
     */
    public void addSmallDot(int index) {
        TextView textView = new TextView(getContext());
        final int size = DisplayUtil.dip2px(getContext(), 10);
        MarginLayoutParams lp = new MarginLayoutParams(size, size);
        lp.leftMargin = mRedDotMarginLeft;
        lp.topMargin = mRedDotMarginTop;
        lp.rightMargin = mRedDotMarginRight;
        lp.bottomMargin = mRedDotMarginBottom;
        textView.setLayoutParams(lp);
        textView.setTextSize(10);
        if (mSmallRedDotBgResId > 0) {
            textView.setBackgroundResource(mSmallRedDotBgResId);
        }
        addSmallDot(index, textView);
    }

    public void addSmallDot(int index, TextView dotTextView) {
        if (index < 0 || index > mTabs.size()) {
            return;
        }
        Tab tab = mTabs.get(index);
        RedDot redDot = new RedDot(dotTextView, "");
        tab.addRedDot(redDot);
    }

    public void setUnreadCount(int index, String count) {
        if (count == null || index < 0 || index > mTabs.size()) {
            return;
        }
        Tab tab = mTabs.get(index);
        if (tab.redDot != null && tab.redDot.vDot != null) {
            tab.redDot.vDot.setText(count);
            tab.redDot.vDot.setVisibility(VISIBLE);
        }
    }

    public void dismissRedDot(int index) {
        if (index < 0 || index > mTabs.size()) {
            return;
        }
        Tab tab = mTabs.get(index);
        if (tab.redDot != null && tab.redDot.vDot != null) {
            tab.redDot.vDot.setVisibility(GONE);
        }
    }

    private boolean isImgIndicator() {
        return mIndicatorResId > 0;
    }

    private void addIndicator() {
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

    private int getIndicatorWidth(int index) {
        if (index >= 0 && index < mIndicatorWidthList.size()) {
            return mIndicatorWidthList.get(index);
        }
        return mIndicatorWidth;
    }

    private int indicatorTop;
    private int lineLength;

    private void drawLineIndicator(Canvas canvas) {
        canvas.save();
        canvas.translate(mTranslationX + mIndicatorPadding, indicatorTop + mIndicatorStrokeWidth / 2);
        canvas.drawLine(0, 0, lineLength, 0, mIndicatorPaint);
        canvas.restore();
    }

    private void drawImgIndicator(Canvas canvas) {
        canvas.save();
        canvas.translate(mTranslationX, indicatorTop);
        canvas.drawBitmap(mIndicatorBitmap, 0, 0, mIndicatorPaint);
        Log.d(TAG, "drawImgIndicator: " + mTranslationX);
        canvas.restore();
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


    private TextView genTabView() {
        TextView tab = new TextView(getContext());
        if (mTextSize > 0) {
            tab.setTextSize(mTextSize);
        }
        tab.setSingleLine();
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
        mIndicatorLeftList.clear();
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
        mIndicatorLeftList.clear();
        mIndicatorWidthList.clear();
        int tabCount = mTabContainerLayout.getChildCount();
        int tabLayoutWidth = mTabContainerLayout.getMeasuredWidth();
        int totalLeft = 0;
        for (int i = 0; i < tabCount; i++) {//&& totalLeft < tabLayoutWidth
            View view = mTabContainerLayout.getChildAt(i);
            int tabWidth = view.getMeasuredWidth();
            if (mIndicatorWidth < 0) {
                mIndicatorLeftList.add(totalLeft);
                mIndicatorWidthList.add(tabWidth - mIndicatorPadding * 2);
            } else {
                mIndicatorLeftList.add(totalLeft + (tabWidth - mIndicatorWidth) / 2);
            }
            totalLeft += tabWidth;
        }
        mIndicatorLeftList.add(totalLeft);
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

    private int lastPosition = -1;
    private float lastPositionOffset = -1;

    private void scroll(int position, float positionOffset) {
        if (position < 0 || position >= mIndicatorLeftList.size() - 1 ||
                (lastPositionOffset == positionOffset && lastPosition == position)) {
            return;
        }
        lastPosition = position;
        lastPositionOffset = positionOffset;
        int currTabWidth = getIndicatorWidth(position);
        int nextTabWidth = getIndicatorWidth(position + 1);
        int currTabLeft = mIndicatorLeftList.get(position);
        int nextTabLeft = mIndicatorLeftList.get(position + 1);
        if (!isImgIndicator()) {
            lineLength = (int) (currTabWidth + (nextTabWidth - currTabWidth) * positionOffset);
        }
        mTranslationX = (int) (currTabLeft + (nextTabLeft - currTabLeft) * positionOffset);
        scrollView(mTranslationX, lineLength);
        invalidate();
    }

    private boolean scrollView(int indicatorLeft, int indicatorLength) {
        int rootWidth = getWidth();
        int leftInView = indicatorLeft - getScrollX();
        int dist = 0;
        if (leftInView < mScrollThreshold) {
            dist = -(mScrollThreshold - leftInView);
        } else if (leftInView + indicatorLength > rootWidth - mScrollThreshold) {
            dist = (leftInView + indicatorLength - rootWidth + mScrollThreshold);
        }
        scrollBy(dist, 0);
        return dist != 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private class Tab {
        private int index;
        private FrameLayout rootView;
        private TextView textView;
        private RedDot redDot;
        private boolean isSelected;

        public Tab(int index, TextView textView, boolean isSelected) {
            this(index, textView, null, isSelected);
        }

        public Tab(int index, @NonNull TextView textView, @Nullable RedDot redDot, boolean isSelected) {
            this.index = index;
            this.textView = textView;
            this.isSelected = isSelected;
            this.redDot = redDot;

            this.rootView = new FrameLayout(getContext());
            this.rootView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ViewGroup.LayoutParams lp = ensureLayoutParam(this.textView);
            this.rootView.addView(this.textView, lp);
            this.rootView.setTag(this.index);
            addRedDotView(this.redDot);
        }

        private FrameLayout.LayoutParams setRedDotGravity(View redDotView, int gravity) {
            ViewGroup.LayoutParams lp = ensureLayoutParam(redDotView);
            FrameLayout.LayoutParams flp;
            if (lp instanceof FrameLayout.LayoutParams) {
                flp = (LayoutParams) lp;
            } else {
                flp = new FrameLayout.LayoutParams(lp);
                if (lp instanceof MarginLayoutParams) {
                    flp.leftMargin = ((MarginLayoutParams) lp).leftMargin;
                    flp.topMargin = ((MarginLayoutParams) lp).topMargin;
                    flp.rightMargin = ((MarginLayoutParams) lp).rightMargin;
                    flp.bottomMargin = ((MarginLayoutParams) lp).bottomMargin;
                }
            }
            flp.gravity = gravity;
            return flp;
        }

        private ViewGroup.LayoutParams ensureLayoutParam(View view) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp == null) {
                lp = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            return lp;
        }

        public void addRedDot(RedDot redDot) {
            if (this.redDot != null) {
                this.redDot.vDot.setText(redDot.num);
                this.redDot.vDot.setLayoutParams(setRedDotGravity(this.redDot.vDot, redDot.gravity));
            } else {
                this.redDot = redDot;
                addRedDotView(redDot);
            }
        }

        private void addRedDotView(RedDot redDot) {
            if (redDot != null) {
                rootView.addView(redDot.vDot, setRedDotGravity(redDot.vDot, redDot.gravity));
            }
        }

    }

    private class RedDot {
        TextView vDot;
        String num;
        int gravity;

        public RedDot(TextView vDot, String num) {
            this(vDot, num, Gravity.END | Gravity.TOP);
        }

        public RedDot(TextView vDot, String num, int gravity) {
            this.vDot = vDot;
            this.num = num;
            this.gravity = gravity;
        }
    }

}
