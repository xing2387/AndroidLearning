package com.example.xing.androidlearning.flowlayout;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xing.androidlearning.R;

import java.lang.reflect.Field;

public class FlowLayoutActivity extends AppCompatActivity implements View.OnClickListener {
    final static String TAG = "FlowLayoutActivity";

    private FloatingActionButton fab_add1;
    private FloatingActionButton fab_add2;
    private FloatingActionButton fab_add3;
    private FloatingActionButton fab_add4;
    private Spinner spChildGravity;
    private Spinner spChildGravity2;
    private TextView textView;

    private ScrollableFlowLayout flContent;

    private int height;
    private int width;
    private float density;

    int gravity1 = 0;
    int gravity2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_layout);
        fab_add1 = (FloatingActionButton) findViewById(R.id.fab_add1);
        fab_add2 = (FloatingActionButton) findViewById(R.id.fab_add2);
        fab_add3 = (FloatingActionButton) findViewById(R.id.fab_add3);
        fab_add4 = (FloatingActionButton) findViewById(R.id.fab_add4);
        flContent = (ScrollableFlowLayout) findViewById(R.id.fl_content);
        spChildGravity = (Spinner) findViewById(R.id.sp_childgravity);
        spChildGravity2 = (Spinner) findViewById(R.id.sp_childgravity2);
        textView = (TextView) findViewById(R.id.textView2);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height = metrics.heightPixels;
        width = metrics.widthPixels;
        density = metrics.density;

        Log.d(TAG, "display width: " + width);

        fab_add1.setOnClickListener(this);
        fab_add2.setOnClickListener(this);
        fab_add3.setOnClickListener(this);
        fab_add4.setOnClickListener(this);

        final String presetStr = textView.getText().toString();
        textView.setText(presetStr + spChildGravity.getSelectedItem().toString().toUpperCase());
        spChildGravity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String gravityStr = spChildGravity.getSelectedItem().toString().toUpperCase();
                Class<?> clazz = null;
                int gravity = -1;
                try {
                    clazz = Class.forName("android.view.Gravity");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    Field field = clazz.getField(gravityStr);
                    gravity = (int) field.get(new Gravity());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "set child gravity = " + gravityStr + "(" + gravity + ")");
                textView.setText(presetStr + gravityStr);
                flContent.removeAllViews();
                gravity1 = gravity;
                flContent.setChildGravity(gravity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spChildGravity2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String gravityStr = spChildGravity2.getSelectedItem().toString().toUpperCase();
                Class<?> clazz = null;
                int gravity = -1;
                try {
                    clazz = Class.forName("android.view.Gravity");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    Field field = clazz.getField(gravityStr);
                    gravity = (int) field.get(new Gravity());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "set child gravity = " + gravityStr + "(" + gravity + ")");
                textView.append("|" + gravityStr);
                flContent.removeAllViews();
                gravity2 = gravity;
                flContent.setChildGravity(gravity1 | gravity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private int count = 0;

    @Override
    public void onClick(View v) {
//        final ImageView imageView = new ImageView(this);
//        imageView.setImageDrawable(getResources().getDrawable(R.drawable.bg_tesk));
//        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        final CardView cardView = new CardView(this);
        cardView.setCardBackgroundColor(Color.rgb(113, 195, 222));
        cardView.setRadius(8 * density);
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        switch (v.getId()) {
            case R.id.fab_add1:
                lp.width = this.width;
                lp.width -= (int) (16 * density);
                lp.height = lp.width / 16 * 9;
                break;
            case R.id.fab_add2:
                lp.width = this.width / 2;
                lp.width -= (int) (16 * density);
                lp.height = lp.width / 4 * 3;
                break;
            case R.id.fab_add3:
                lp.width = this.width / 3;
                lp.width -= (int) (16 * density);
                lp.height = lp.width / 4 * 3;
                break;
            case R.id.fab_add4:
                lp.width = this.width / 4;
                lp.width -= (int) (16 * density);
                lp.height = lp.width / 4 * 3;
                break;
        }
        lp.setMargins((int) (8 * density), (int) (8 * density), (int) (8 * density), (int) (8 * density));
        cardView.setLayoutParams(lp);


        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flContent.removeView(cardView);
                Toast.makeText(FlowLayoutActivity.this, "remove view" + cardView.getTag(), Toast.LENGTH_SHORT).show();
            }
        });
        cardView.setTag(count);
        flContent.addView(cardView);
        Toast.makeText(FlowLayoutActivity.this, "added view" + count++, Toast.LENGTH_SHORT).show();

    }
}
