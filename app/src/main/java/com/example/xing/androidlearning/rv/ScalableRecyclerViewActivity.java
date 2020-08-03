package com.example.xing.androidlearning.rv;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.xing.androidlearning.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ScalableRecyclerViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scalable_recycler_view);
        final ScalableRecyclerView rv = findViewById(R.id.rv);
        FloatingActionButton btn = findViewById(R.id.flb);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rv.switchStyle();
            }
        });
    }


}