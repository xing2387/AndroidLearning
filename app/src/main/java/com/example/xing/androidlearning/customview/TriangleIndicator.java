package com.example.xing.androidlearning.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xing.androidlearning.R;
import com.example.xing.androidlearning.util.CommomUtil;

import java.util.List;

/**
 * Created by xing on 5/23/16.
 * ViewPager的三角形指示器,
 * 可在xml里手动添加TextView, 但如果使用了setTabs方法后xml里添加的TextView就会无效
 * 最后要将ViewPager通过setViewPager方法添加进来
 * attr.xml 这么写:
 * <declare-styleable name="triangleindicator">
 *  <attr name="visible_tab_count" format="integer" />
 *  <attr name="tab_title_text_color" format="color" />
 *  <attr name="tab_title_text_color_highlight" format="color" />
 *  <attr name="tab_title_text_size" format="dimension"/>
 * </declare-styleable>
 */
public class TriangleIndicator extends LinearLayout {
    final private String TAG = "TriangleIndicator";

    private Paint mPaint;
    private Path mPath;
    private int mTriangleWidth;     //三角形指示器的宽度
    private int mTabWidth;          //标签的宽度
    private int mTranslationX;      //标签相对最小位移的位移量
    private int mMinTranslationX;   //指示器最少位移量,即是半个标签宽度
    private int mVisibleTabCount;   //一个屏幕宽度中可见的标签数量
    private int mTabTitleTextColor;
    private int mTabTitleTextColorHighLight;
    private float mTabTitleTextSize;
    private int mCurrentPositionBegin;
    private ViewPager mRelevantViewPager;

    private final int TRIANGLE_WIDTH_MAX = getScreenWidth()/18;
    private final int DEFAULT_VISIBLE_TAB_COUNT = 3;    //默认的可见标签数量
    private final int DEFAULT_TAB_TITLE_TEXT_COLOR = 0xCCCCCC;  //默认的标签文本颜色
    private final int DEFAULT_TAB_TITLE_TEXT_COLOR_HIGHLIGHT = 0xEEEEEE; //默认的标签文本高亮颜色
    private final float DEFAULT_TAB_TITLE_TEXT_SIZE = CommomUtil.getRawSize(TypedValue.COMPLEX_UNIT_SP, 8); //默认的标签文本大小


    public TriangleIndicator(Context context) {
        this(context, null);
    }

    public TriangleIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        //获取可见的标签数量属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.triangleindicator);
        mVisibleTabCount = a.getInt(R.styleable.triangleindicator_visible_tab_count, DEFAULT_VISIBLE_TAB_COUNT);
        mTabTitleTextSize = a.getDimension(R.styleable.triangleindicator_tab_title_text_size, DEFAULT_TAB_TITLE_TEXT_SIZE)/2;
        mTabTitleTextColor = a.getColor(R.styleable.triangleindicator_tab_title_text_color, DEFAULT_TAB_TITLE_TEXT_COLOR);
        mTabTitleTextColorHighLight = a.getColor(R.styleable.triangleindicator_tab_title_text_color_highlight, DEFAULT_TAB_TITLE_TEXT_COLOR_HIGHLIGHT);
        if (mVisibleTabCount <= 0) mVisibleTabCount = 0;
        a.recycle();
        mCurrentPositionBegin = 0;

        //初始化画笔用来绘制三角形指示器
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#FFFFFF"));
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setPathEffect(new CornerPathEffect(3));

    }
//
//    public void setTabTitles(List<String> mTabTitles) {
//        this.mTabTitles = mTabTitles;
//    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        int tabCount = getChildCount();
        if (tabCount == 0) return;

        for (int i = 0; i < tabCount; i++) {
            View tab = getChildAt(i);
            LinearLayout.LayoutParams lp = (LayoutParams) tab.getLayoutParams();
            lp.weight = 0;
            lp.width = getScreenWidth() / mVisibleTabCount;
            tab.setLayoutParams(lp);
            final int position = i;
            tab.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mRelevantViewPager != null) {
                        mRelevantViewPager.setCurrentItem(position, true);
                    }
                }
            });
        }
    }

    /**
     * 获取屏幕宽度
     *
     * @return 屏幕宽度的像素点数量
     */
    private int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mTabWidth = w / mVisibleTabCount;
        mTriangleWidth = Math.min(mTabWidth / 6, TRIANGLE_WIDTH_MAX);
        mMinTranslationX = (mTabWidth - mTriangleWidth) / 2;

        initTriangle(mTriangleWidth);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mMinTranslationX + mTranslationX, getHeight());
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
        super.dispatchDraw(canvas);
    }

    public void setTabs(List<String> tabTitles) {
        if (tabTitles == null || tabTitles.isEmpty()) return;
        this.removeAllViews();
        int i = 0;
        for (String title : tabTitles) {
            addView(generateTab(title, i));
            i++;
        }
    }


    private TextView generateTab(String title, final int position) {
        TextView tvTab = new TextView(getContext());

        LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.width = getScreenWidth() / mVisibleTabCount;
        tvTab.setLayoutParams(lp);
        tvTab.setGravity(Gravity.CENTER);
        tvTab.setTextColor(mTabTitleTextColor);
        tvTab.setTextSize(mTabTitleTextSize);
        tvTab.setText(title);

        tvTab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRelevantViewPager != null) {
                    mRelevantViewPager.setCurrentItem(position, true);
                }
            }
        });

        return tvTab;
    }

    /**
     * 初始化Path对象,并绘制三角形指示器
     */
    private void initTriangle(int triangleWidth) {
        mPath = new Path();
        mPath.moveTo(0, 0);
        mPath.lineTo(triangleWidth, 0);
        mPath.lineTo(triangleWidth / 2, -triangleWidth / 3);
        mPath.close();
    }

    /**
     * 当屏幕滑动时调用,移动三角形指示器和标签组
     *
     * @param position       当前激活标签的索引,从0开始
     * @param positionOffset 当前页面滑动距离相对页面宽度的百分比
     */
    public void scoll(int position, float positionOffset) {
//        Debug.startMethodTracing("TraceTest");
        // 设置指示器的位移
        mTranslationX = (int) ((position + positionOffset) * mTabWidth);

        //移动标签组
        if (positionOffset == 0) {
            //向右移动标签组
            if (position > mCurrentPositionBegin + mVisibleTabCount - 2 && position < getChildCount() - 1) {
                this.scrollTo((++mCurrentPositionBegin) * mTabWidth, 0);
            }
            //向左移动标签组
            else if (mCurrentPositionBegin > 0 && position == mCurrentPositionBegin) {
                this.scrollTo(--mCurrentPositionBegin * mTabWidth, 0);
            }
            // 检查并纠正当前标签组的位置
            if (position < mCurrentPositionBegin || mCurrentPositionBegin + mVisibleTabCount - 2 < position) {
                mCurrentPositionBegin = Math.min(position - 1, getChildCount() - mVisibleTabCount);
                this.scrollTo(mCurrentPositionBegin * mTabWidth, 0);
            }
            highLightTabTitleText(position);
        }
        invalidate();
//        Debug.stopMethodTracing();
    }

    /**
     * 设置关联的ViewPager
     *
     * @param viewPager y
     */
    public void setViewPager(ViewPager viewPager) {
        mRelevantViewPager = viewPager;
        mRelevantViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                scoll(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void highLightTabTitleText(int position) {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof TextView) {
                if (i == position) {
                    ((TextView) view).setTextColor(mTabTitleTextColorHighLight);
                } else {
                    ((TextView) view).setTextColor(mTabTitleTextColor);
                }
            }
        }
    }
}
