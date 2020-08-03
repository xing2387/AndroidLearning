package com.example.xing.androidlearning.rv;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ScalableRecyclerView extends RecyclerView {
    private static final String TAG = "ScalableRecyclerView";

    private final int spanCount = 3;
    private GridLayoutManager lmGrid = new GridLayoutManager(getContext(), spanCount);
    private ScaleGestureDetector scaleGestureDetector =
            new ScaleGestureDetector(getContext(), new OnScaleGestureListener());


    public ScalableRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayoutManager(lmGrid);
        setAdapter(new Adapter());
    }

    public void switchStyle() {
        int count = lmGrid.getSpanCount() == 1 ? spanCount : 1;
        lmGrid.setSpanCount(count);
        getAdapter().notifyItemRangeChanged(0, getAdapter().getItemCount());
    }

    public void switchToLinerStyle() {
        if (lmGrid.getSpanCount() == 1) {
            return;
        }
        lmGrid.setSpanCount(1);
        getAdapter().notifyItemRangeChanged(0, getAdapter().getItemCount());
    }

    public void switchToGridStyle() {
        if (lmGrid.getSpanCount() == spanCount) {
            return;
        }
        lmGrid.setSpanCount(spanCount);
        getAdapter().notifyItemRangeChanged(0, getAdapter().getItemCount());
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return scaleGestureDetector.onTouchEvent(e);
    }

    private class OnScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {

        private final int scaleSpanThreshold = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 30f,
                getContext().getResources().getDisplayMetrics());
        private float spanOnBegin = 0;
        private boolean isSwitched = false;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (!isSwitched) {
                float span = detector.getCurrentSpan() - spanOnBegin;
                if (Math.abs(span) >= scaleSpanThreshold) {
                    if (span > 0) {
                        switchToLinerStyle();
                    } else {
                        switchToGridStyle();
                    }
                    isSwitched = true;
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            spanOnBegin = detector.getCurrentSpan();
            isSwitched = false;
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

        }
    }

    public static class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<Integer> data = new ArrayList<>();

        public Adapter() {
            for (int i = 0; i < 50; i++) {
                data.add(i);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView v = new TextView(parent.getContext());
            v.setId(View.generateViewId());
            v.setGravity(Gravity.CENTER);
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100f,
                    parent.getContext().getResources().getDisplayMetrics());
            int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f,
                    parent.getContext().getResources().getDisplayMetrics());
            ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            layoutParams.setMargins(margin, margin, margin, margin);
            v.setLayoutParams(layoutParams);
            v.setBackgroundColor(Color.BLUE);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((TextView) holder.itemView).setText(data.get(position) + ".");
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
