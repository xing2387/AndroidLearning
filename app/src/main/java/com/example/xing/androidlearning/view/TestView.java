package com.example.xing.androidlearning.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by liuji on 2015/9/11.
 */

public class TestView extends ImageView {

    Paint mPaint;
    float mPoints[] = {0.5f, 0f, 0.5f, 1f, 0f, 0.5f, 1f, 0.5f};
    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        Drawable drawable = getDrawable() ;
//        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        int scale = getWidth();
//        canvas.scale(scale, scale);
//        canvas.rotate(30f);
//        canvas.drawLines(mPoints, mPaint);
        int redius = getWidth()/2;
        canvas.drawCircle(redius,redius,redius,mPaint);
        canvas.restore();
    }
}
