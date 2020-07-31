package com.example.xing.androidlearning.ruleView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.TintableBackgroundView;

import com.example.xing.androidlearning.R;
import com.example.xing.androidlearning.util.CommomUtil;

import java.util.Locale;

public class RuleView extends AppCompatImageView {

    private Context mContext;
    private String[] mScaleTexts;
    private int[][] mScaleTextsWH;  //字体宽和高

    private int mMinScale = 0;    //最小刻度 * mPrecisionDivisor
    private int mMaxScale = 10;    //最大刻度 * mPrecisionDivisor
    private int mCurrScale = 5;     //当前值 * mPrecisionDivisor
    private float mGapWidth = 7;    //dp 刻度间距
    private int mScaleInterval = 1; //大刻度的距离， 如果每1cm一个大刻度的尺子，值就是1 * mPrecisionDivisor
    private int mSubScaleDivision = 5; //每个刻度分几分（标尺分度），如果上面的尺子要画出毫米的刻度，值就是10，如果值是5，分度值就是2mm
    private int mScaleColor = Color.WHITE;    //刻度颜色
    private int mPointerColor = Color.YELLOW;//刻度指针颜色（正中间的刻度颜色）

    private float mLargeScaleWidth = 2;      //dp 大刻度宽度
    private float mMidScaleWidth = 4 / 3;       //dp 中刻度宽度， 大刻度的2/3
    private float mSmallScaleWidth = 1;     //dp 小刻度宽度， 大刻度的一半
    private float mPointerWidth = 3;        //dp 和大刻度一样
    private float mLargeScaleHeight = 30;   //dp 大刻度高度
    private float mMidScaleHeight = 20;          //dp 中刻度高度， 大刻度的2/3
    private float mSmallScaleHeight = 10;        //dp 小刻度高度， 大刻度的一半
    private int mPrecision = 2;
    private int mPrecisionDivisor = 100;      //精确到小数点后几位的作用， mPrecisionDivisor=10; 0.1 = 1 / mPrecisionDivisor;
    private float mTextMarginTop = 5;       //dp 刻度文本在刻度线下面多远，可以为负
    private float mPointerTextMarginBottom = 8; //dp 读数文本在刻度线上面多远，可以为负
    private boolean mHasMidScale = false;   //大刻度中间值的刻度

    private int mScaleTextPrecision = 2;
    private float mScaleTextSize = 14;      //sp 刻度字体大小
    private int mScaleTextColor = Color.WHITE; //刻度文本颜色
    private float mPointerTextSize = 16;      //sp 读数字体大小
    private int mPointerTextColor = Color.WHITE; //读数文本颜色
    private boolean mShowPointerText = true;
    private boolean mShowScaleText = true;
    private boolean mShowPointer = true;
    private String mUnitText;

    private float mHalfWidth;
    private float mRealWidth;
    private float mStartX;
    private float mEndX;

    private float mLeftScaleX;
    private float mExtraLeftLen;
    private int mMinScaleInterval;

    private float mLargeScaleY;
    private float mMidScaleY;
    private float mSmallScaleY;
    private float mScaleTextY;

    private Paint mPaintLargeScale;
    private Paint mPaintMidScale;
    private Paint mPaintSmallScale;
    private Paint mPaintPointer;
    private Paint mPaintScaleText;
    private Paint mPaintPointerText;

    private OnScaleChangedListener mOnScaleChangedListener;

    public RuleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setAttr(attrs);
        init();
    }

    private void setAttr(AttributeSet attrs) {

        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.RuleView);

        mScaleTextPrecision = a.getInt(R.styleable.RuleView_scaleTextPrecision, mScaleTextPrecision);
        mShowPointer = a.getBoolean(R.styleable.RuleView_showPointer, mShowPointer);
        mShowScaleText = a.getBoolean(R.styleable.RuleView_showScaleText, mShowScaleText);
        mShowPointerText = a.getBoolean(R.styleable.RuleView_showPointerText, mShowPointerText);
        mUnitText = a.getString(R.styleable.RuleView_unit);
        mUnitText = mUnitText == null ? "" : mUnitText;
        mScaleTextSize = a.getDimension(R.styleable.RuleView_scaleTextSize, mScaleTextSize);
        mScaleTextSize = CommomUtil.getRawSize(TypedValue.COMPLEX_UNIT_SP, mScaleTextSize);
        mScaleTextColor = a.getColor(R.styleable.RuleView_scaleTextColor, mScaleTextColor);
        mPointerTextSize = a.getDimension(R.styleable.RuleView_pointerTextSize, mPointerTextSize);
        mPointerTextSize = CommomUtil.getRawSize(TypedValue.COMPLEX_UNIT_SP, mPointerTextSize);
        mPointerTextColor = a.getColor(R.styleable.RuleView_pointerTextColor, mPointerTextColor);

        mPrecision = a.getInt(R.styleable.RuleView_precision, mPrecision);
        float maxScale = a.getFloat(R.styleable.RuleView_maxScale, mMaxScale);
        float minScale = a.getFloat(R.styleable.RuleView_minScale, mMinScale);
        float scale = a.getFloat(R.styleable.RuleView_scale, (maxScale + minScale) / 2f);
        float scaleInterval = a.getFloat(R.styleable.RuleView_scaleInterval, mScaleInterval);
        mPrecisionDivisor = (int) Math.pow(10, mPrecision);
        mCurrScale = (int) (scale * mPrecisionDivisor);
        mMaxScale = (int) (maxScale * mPrecisionDivisor);
        mMinScale = (int) (minScale * mPrecisionDivisor);
        mScaleInterval = (int) (scaleInterval * mPrecisionDivisor);

        mSubScaleDivision = a.getInteger(R.styleable.RuleView_subScaleDivision, mSubScaleDivision);
        mScaleColor = a.getColor(R.styleable.RuleView_scaleColor, mScaleColor);
        mPointerColor = a.getColor(R.styleable.RuleView_pointerColor, mPointerColor);

        //暂存单位为dp的值, 为了在声明这些变量的时候指定默认值不用换算单位就没用getDimensionPixelSize, 分开写效率一样的
        mTextMarginTop = a.getDimension(R.styleable.RuleView_scaleTextMarginTop, mTextMarginTop);
        mPointerTextMarginBottom = a.getDimension(R.styleable.RuleView_pointerTextMarginBottom, mPointerTextMarginBottom);
        mGapWidth = a.getDimension(R.styleable.RuleView_gapWidth, mGapWidth);
        mLargeScaleHeight = a.getDimension(R.styleable.RuleView_largeScaleHeight, mLargeScaleHeight);
        mSmallScaleHeight = a.getDimension(R.styleable.RuleView_smallScaleHeight, mLargeScaleHeight / 2);
        mLargeScaleWidth = a.getDimension(R.styleable.RuleView_largeScaleWidth, mLargeScaleWidth);
        mSmallScaleWidth = a.getDimension(R.styleable.RuleView_smallScaleWidth, mLargeScaleWidth / 2);
        mHasMidScale = a.hasValue(R.styleable.RuleView_midScaleHeight) || a.hasValue(R.styleable.RuleView_midScaleWidth) ||
                a.getBoolean(R.styleable.RuleView_hasMidScale, false);
        if (mHasMidScale) {
            mMidScaleHeight = a.getDimension(R.styleable.RuleView_midScaleHeight, mLargeScaleHeight * 2 / 3);
            mMidScaleWidth = a.getDimension(R.styleable.RuleView_midScaleHeight, mLargeScaleWidth * 2 / 3);
        }
        //换算成单位为px的值
        mTextMarginTop = CommomUtil.getRawSize(TypedValue.COMPLEX_UNIT_DIP, mTextMarginTop);
        mPointerTextMarginBottom = CommomUtil.getRawSize(TypedValue.COMPLEX_UNIT_DIP, mPointerTextMarginBottom);
        mGapWidth = CommomUtil.getRawSize(TypedValue.COMPLEX_UNIT_DIP, mGapWidth);
        mLargeScaleHeight = CommomUtil.getRawSize(TypedValue.COMPLEX_UNIT_DIP, mLargeScaleHeight);
        mSmallScaleHeight = CommomUtil.getRawSize(TypedValue.COMPLEX_UNIT_DIP, mSmallScaleHeight);
        mMidScaleHeight = CommomUtil.getRawSize(TypedValue.COMPLEX_UNIT_DIP, mMidScaleHeight);
        mLargeScaleWidth = CommomUtil.getRawSize(TypedValue.COMPLEX_UNIT_DIP, mLargeScaleWidth);
        mSmallScaleWidth = CommomUtil.getRawSize(TypedValue.COMPLEX_UNIT_DIP, mSmallScaleWidth);
        mMidScaleWidth = CommomUtil.getRawSize(TypedValue.COMPLEX_UNIT_DIP, mMidScaleWidth);
        mPointerWidth = mLargeScaleWidth + CommomUtil.getRawSize(TypedValue.COMPLEX_UNIT_DIP, 1);

        a.recycle();


        mMinScaleInterval = mScaleInterval / mSubScaleDivision; //分度值
    }

    private void init() {
        if (mScaleTexts == null) {
            int scaleCount = (mMaxScale - mMinScale) / mScaleInterval + 2;
            mScaleTexts = new String[scaleCount];
            mScaleTextsWH = new int[scaleCount][2];
        }

        mPaintScaleText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintScaleText.setTextSize(mScaleTextSize);
        mPaintScaleText.setColor(mScaleColor);

        mPaintPointerText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintPointerText.setTextSize(mPointerTextSize);
        mPaintPointerText.setColor(mScaleColor);

        mPaintLargeScale = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintLargeScale.setColor(mScaleColor);
        mPaintLargeScale.setStyle(Paint.Style.FILL);
        mPaintLargeScale.setStrokeCap(Paint.Cap.ROUND);
        mPaintLargeScale.setStrokeWidth(mLargeScaleWidth);

        mPaintMidScale = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintMidScale.setColor(mScaleColor);
        mPaintMidScale.setStyle(Paint.Style.FILL);
        mPaintMidScale.setStrokeCap(Paint.Cap.ROUND);
        mPaintMidScale.setStrokeWidth(mMidScaleWidth);

        mPaintSmallScale = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintSmallScale.setColor(mScaleColor);
        mPaintSmallScale.setStyle(Paint.Style.FILL);
        mPaintSmallScale.setStrokeCap(Paint.Cap.ROUND);
        mPaintSmallScale.setStrokeWidth(mSmallScaleWidth);

        mPaintPointer = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintPointer.setColor(mPointerColor);
        mPaintPointer.setStyle(Paint.Style.FILL);
        mPaintPointer.setStrokeCap(Paint.Cap.ROUND);
        mPaintPointer.setStrokeWidth(mPointerWidth);

//        setOnScaleChangedListener(new OnScaleChangedListener() {
//            @Override
//            public void onScaleChanged(float scale) {
//
//                Log.d("RuleView", "getScale: " + scale);
//            }
//        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        calScreenInfo();
        calcLeftScaleInfo();
        calcScaleY(Gravity.CENTER_VERTICAL);

        drawBackground(canvas);
        drawScales(canvas);
        drawPointer(canvas);
    }

    /**
     * 画背景
     */
    private void drawBackground(Canvas canvas) {
    }

    /**
     * 画中间那根指示读数的指针
     */
    Rect tempRect = new Rect();

    private void drawPointer(Canvas canvas) {
        if (mShowPointer || mShowPointerText) {
            float dp3 = CommomUtil.getRawSize(TypedValue.COMPLEX_UNIT_DIP, 3);
            float startX = (mEndX - mStartX) / 2 + mStartX;
            if (mShowPointer) {
                canvas.drawLine(startX, mLargeScaleY - dp3, startX,
                        mLargeScaleY + mLargeScaleHeight + dp3, mPaintPointer);
            }
            if (mShowPointerText) {
                String text = String.format("%." + mPrecision + "f ", getScale()) + mUnitText;
                mPaintPointerText.getTextBounds(text, 0, text.length(), tempRect);
                canvas.drawText(text, startX - tempRect.width() / 2,
                        mLargeScaleY - dp3 - mPointerTextMarginBottom, mPaintPointerText);
            }
        }
    }

    /**
     * 画刻度
     */
    private void drawScales(Canvas canvas) {

     /* 用BigDecimal避免浮点运算的误差. emmmmm实际出来new了太多对象。。gc太频繁了卡顿

        float currX = mLeftScaleX;
        BigDecimal currScale = new BigDecimal(String.valueOf(mLeftScale));
        BigDecimal minScaleInterval = new BigDecimal(String.valueOf((mScaleInterval + 0f) / mSubScaleDivision)); //分度值
        BigDecimal midScaleInterval = new BigDecimal(String.valueOf((mScaleInterval + 0f) / 2));
//        long startT = System.currentTimeMillis();
        do {
            if (currScale.floatValue() % mScaleInterval == 0) {  //画大刻度
                canvas.drawLine(currX, mLargeScaleY, currX, mLargeScaleY + mLargeScaleHeight, mPaintLargeScale);
            } else if (mHasMidScale && currScale.floatValue() % midScaleInterval.floatValue() == 0) { //画中间的刻度
                canvas.drawLine(currX, mMidScaleY, currX, mMidScaleY + mMidScaleHeight, mPaintMidScale);
            } else {    //画小刻度
                canvas.drawLine(currX, mSmallScaleY, currX, mSmallScaleY + mSmallScaleHeight, mPaintSmallScale);
            }

            currX += mGapWidth;
            currScale = currScale.add(minScaleInterval);
        } while (currX < getRight() && currScale.floatValue() <= mMaxScale);

        Log.d("aaa", "----------------- " + (System.currentTimeMillis() - startT));
    */
        TintableBackgroundView tintableBackgroundView;
        float currX = mLeftScaleX;
        int scaleNum = (int) ((mExtraLeftLen + (mLeftScaleX - mStartX)) / mGapWidth); //显示的第一个刻度到第一个刻度的距离，除以刻度间距
        scaleNum = Math.max(0, scaleNum);
        int startScaleNum = scaleNum;
        int maxScaleNum = (int) ((mExtraLeftLen + mRealWidth) / mGapWidth);
        maxScaleNum = Math.min(maxScaleNum, (mMaxScale - mMinScale) * mSubScaleDivision / mPrecisionDivisor);
        int midScaleDivision = mSubScaleDivision / 2;

        do {
            if (scaleNum % mSubScaleDivision == 0) {  //画大刻度
                canvas.drawLine(currX, mLargeScaleY, currX, mLargeScaleY + mLargeScaleHeight, mPaintLargeScale);
                drawScaleText(canvas, scaleNum / mSubScaleDivision, currX);
            } else if (mHasMidScale && scaleNum % midScaleDivision == 0) { //画中间的刻度
                canvas.drawLine(currX, mMidScaleY, currX, mMidScaleY + mMidScaleHeight, mPaintMidScale);
            } else {    //画小刻度
                canvas.drawLine(currX, mSmallScaleY, currX, mSmallScaleY + mSmallScaleHeight, mPaintSmallScale);
            }

            scaleNum++;
            currX = mGapWidth * (scaleNum - startScaleNum) + mLeftScaleX;   //不采用currX += mGapWidth的方式，浮点数累加导致误差会越来越大
        } while (currX < mEndX && scaleNum <= maxScaleNum);
    }

    private void drawScaleText(Canvas canvas, int index, float x) {
        if (!mShowScaleText) {
            return;
        }
        String text = mScaleTexts[index];
        if (text == null) {
            float scale = (index * mMinScaleInterval * mSubScaleDivision + mMinScale + 0f) / mPrecisionDivisor;
            text = String.format(Locale.ENGLISH, "%." + mScaleTextPrecision + "f", scale);
            mScaleTexts[index] = text;
            mPaintScaleText.getTextBounds(text, 0, text.length(), tempRect);
            mScaleTextsWH[index] = new int[]{tempRect.width(), tempRect.height()};
        }
        canvas.drawText(text, x - mScaleTextsWH[index][0] / 2, mScaleTextY + mScaleTextsWH[index][1], mPaintScaleText);
    }

    private void calScreenInfo() {
        mStartX = getPaddingLeft();
        mEndX = getWidth() - getPaddingRight();

        mRealWidth = mEndX - mStartX;
        mHalfWidth = mRealWidth / 2f;
    }

    /**
     * 计算各种刻度线的起点的Y坐标
     *
     * @param gravity 相对与最长的线的对齐方式
     */
    private void calcScaleY(int gravity) {
        mLargeScaleY = (getHeight() - mLargeScaleHeight) / 2;
        if (gravity == Gravity.TOP) {               //顶端对齐
            mMidScaleY = mLargeScaleY;
            mSmallScaleY = mLargeScaleY;
        } else if (gravity == Gravity.BOTTOM) {     //底部对齐
            mMidScaleY = mLargeScaleY + (mLargeScaleHeight + mMidScaleHeight) / 2;
            mSmallScaleY = mLargeScaleY + (mLargeScaleHeight + mSmallScaleHeight) / 2;
        } else {                                    //居中对齐
            mMidScaleY = (getHeight() - mMidScaleHeight) / 2;
            mSmallScaleY = (getHeight() - mSmallScaleHeight) / 2;
        }
        mScaleTextY = mLargeScaleY + mLargeScaleHeight + mTextMarginTop;
    }

    private void calcLeftScaleInfo() {
        float totalLeftLength = (mCurrScale - mMinScale) * mGapWidth / mMinScaleInterval;
        mExtraLeftLen = totalLeftLength - mHalfWidth;
        if (mExtraLeftLen > 0) {
            mLeftScaleX = mGapWidth - mExtraLeftLen % mGapWidth;
        } else {
            mLeftScaleX = mHalfWidth - totalLeftLength;
        }
        mLeftScaleX += mStartX;
    }

    public void setOnScaleChangedListener(OnScaleChangedListener onScaleChangedListener) {
        this.mOnScaleChangedListener = onScaleChangedListener;
    }

    public float getScale() {
        return (mCurrScale + 0f) / mPrecisionDivisor;
    }

    public void setScale(float scale) {
        setCurrScale((int) (scale * mPrecisionDivisor));
    }

    private void setCurrScale(int currScale) {
        this.mCurrScale = currScale;
        postInvalidate();
        if (mOnScaleChangedListener != null) {
            mOnScaleChangedListener.onScaleChanged(getScale());
        }
    }

    private float fingerDownX;
    private float deltaX;
    private int tempScale;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                fingerDownX = event.getX();
                deltaX = 0;
                tempScale = mCurrScale;
                break;
            case MotionEvent.ACTION_MOVE:
                float lastScale = mCurrScale;
                deltaX = event.getX() - fingerDownX;
                int deltaScale = (int) (deltaX / mGapWidth * mMinScaleInterval);
                mCurrScale = tempScale - deltaScale;
                mCurrScale = Math.max(mMinScale, Math.min(mMaxScale, mCurrScale));
                if (lastScale != mCurrScale) {
                    setCurrScale(mCurrScale);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }


    public void setScaleRangeAndInterval(float minScale, float maxScale, float scaleInterval) {
        this.mMinScale = (int) (minScale * mPrecisionDivisor);
        this.mMaxScale = (int) (maxScale * mPrecisionDivisor);
        this.mScaleInterval = (int) (scaleInterval * mPrecisionDivisor);

        int scaleCount = (mMaxScale - mMinScale) / mScaleInterval + 2;
        mScaleTexts = new String[scaleCount];
        mScaleTextsWH = new int[scaleCount][2];
    }

    public int getMinScale() {
        return mMinScale;
    }

    public int getMaxScale() {
        return mMaxScale;
    }

    public void setScaleTextPrecision(int scaleTextPrecision) {
        this.mScaleTextPrecision = scaleTextPrecision;
    }

    public int getmScaleTextPrecision() {
        return mScaleTextPrecision;
    }

    public float getGapWidth() {
        return mGapWidth;
    }

    public void setGapWidth(float gapWidth) {
        this.mGapWidth = gapWidth;
    }

    public int getScaleInterval() {
        return mScaleInterval;
    }

    public int getSubScaleDivision() {
        return mSubScaleDivision;
    }

    public void setSubScaleDivision(int subScaleDivision) {
        this.mSubScaleDivision = subScaleDivision;
    }

    public int getScaleColor() {
        return mScaleColor;
    }

    public void setScaleColor(int scaleColor) {
        this.mScaleColor = scaleColor;
    }

    public int getPointerColor() {
        return mPointerColor;
    }

    public void setPointerColor(int pointerColor) {
        this.mPointerColor = pointerColor;
    }

    public float getLargeScaleWidth() {
        return mLargeScaleWidth;
    }

    public void setLargeScaleWidth(float largeScaleWidth) {
        this.mLargeScaleWidth = largeScaleWidth;
    }

    public float getMidScaleWidth() {
        return mMidScaleWidth;
    }

    public void setMidScaleWidth(float midScaleWidth) {
        this.mMidScaleWidth = midScaleWidth;
    }

    public float getSmallScaleWidth() {
        return mSmallScaleWidth;
    }

    public void setSmallScaleWidth(float smallScaleWidth) {
        this.mSmallScaleWidth = smallScaleWidth;
    }

    public float getPointerWidth() {
        return mPointerWidth;
    }

    public void setPointerWidth(float pointerWidth) {
        this.mPointerWidth = pointerWidth;
    }

    public float getLargeScaleHeight() {
        return mLargeScaleHeight;
    }

    public void setLargeScaleHeight(float largeScaleHeight) {
        this.mLargeScaleHeight = largeScaleHeight;
    }

    public float getMidScaleHeight() {
        return mMidScaleHeight;
    }

    public void setMidScaleHeight(float midScaleHeight) {
        this.mMidScaleHeight = midScaleHeight;
    }

    public float getSmallScaleHeight() {
        return mSmallScaleHeight;
    }

    public void setSmallScaleHeight(float smallScaleHeight) {
        this.mSmallScaleHeight = smallScaleHeight;
    }

    public int getPrecision() {
        return mPrecision;
    }

    public void setPrecision(int precision) {
        this.mMinScale = mMinScale / mPrecisionDivisor * precision;
        this.mMaxScale = mMaxScale / mPrecisionDivisor * precision;
        this.mCurrScale = mCurrScale / mPrecisionDivisor * precision;
        this.mScaleInterval = mScaleInterval / mPrecisionDivisor * precision;

        this.mPrecision = precision;
        this.mPrecisionDivisor = (int) Math.pow(10, mPrecision);
    }

    public float getTextMarginTop() {
        return mTextMarginTop;
    }

    public void setTextMarginTop(float textMarginTop) {
        this.mTextMarginTop = textMarginTop;
    }

    public float getPointerTextMarginBottom() {
        return mPointerTextMarginBottom;
    }

    public void setPointerTextMarginBottom(float pointerTextMarginBottom) {
        this.mPointerTextMarginBottom = pointerTextMarginBottom;
    }

    public boolean ismHasMidScale() {
        return mHasMidScale;
    }

    public void setHasMidScale(boolean hasMidScale) {
        this.mHasMidScale = hasMidScale;
    }

    public float getScaleTextSize() {
        return mScaleTextSize;
    }

    public void setScaleTextSize(float scaleTextSize) {
        this.mScaleTextSize = scaleTextSize;
    }

    public int getScaleTextColor() {
        return mScaleTextColor;
    }

    public void setScaleTextColor(int scaleTextColor) {
        this.mScaleTextColor = scaleTextColor;
    }

    public float getPointerTextSize() {
        return mPointerTextSize;
    }

    public void setPointerTextSize(float pointerTextSize) {
        this.mPointerTextSize = pointerTextSize;
    }

    public int getPointerTextColor() {
        return mPointerTextColor;
    }

    public void setPointerTextColor(int pointerTextColor) {
        this.mPointerTextColor = pointerTextColor;
    }

    public boolean ismShowPointerText() {
        return mShowPointerText;
    }

    public void setShowPointerText(boolean showPointerText) {
        this.mShowPointerText = showPointerText;
    }

    public boolean ismShowScaleText() {
        return mShowScaleText;
    }

    public void setShowScaleText(boolean showScaleText) {
        this.mShowScaleText = showScaleText;
    }

    public boolean ismShowPointer() {
        return mShowPointer;
    }

    public void setShowPointer(boolean showPointer) {
        this.mShowPointer = showPointer;
    }

    public String getUnitText() {
        return mUnitText;
    }

    public void setUnitText(String unitText) {
        this.mUnitText = unitText;
    }

    interface OnScaleChangedListener {
        void onScaleChanged(float scale);
    }
}
