package com.example.xing.androidlearning;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.xing.androidlearning.fakewechat.ContactsHomeActivity;
import com.example.xing.androidlearning.flowlayout.FlowLayoutActivity;
import com.example.xing.androidlearning.loginsignup.LoginAndSignUpActivity;
import com.example.xing.androidlearning.pageviewindicator.PageViewIndicatorActivity;
import com.example.xing.androidlearning.ruleView.RuleViewActivity;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lvMenu = (ListView) findViewById(R.id.main_lv_menu);
        List<String> menuList = Arrays.asList("Login&SignUp", "TriangleIndicator",
                "FlowLayout", "Contacts", "ruleView");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuList);
        if (lvMenu != null) {
            lvMenu.setAdapter(adapter);
            lvMenu.setOnItemClickListener(this);
        }


        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        Log.d("MainActivity", "new Thread handling msg");
                    }
                };
                Log.d("MainActivity", "before loop");
                Looper.loop();
                Log.d("MainActivity", "after loop");
            }
        }.start();


    }

    private void testVolley() {
        Point point = new Point();
        RequestQueue mRequestQueue;
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("http://www.mocky.io/v2/56c9d8c9110000c62f4e0bb0", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString() + "sdsf");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.getMessage() + "wrrwe");
            }
        }
        );
        mRequestQueue.add(jsonObjectRequest);


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                startActivity(new Intent(this, LoginAndSignUpActivity.class));
                break;
            case 1:
                startActivity(new Intent(this, PageViewIndicatorActivity.class));
                break;
            case 2:
                startActivity(new Intent(this, FlowLayoutActivity.class));
                break;
            case 3:
                startActivity(new Intent(this, ContactsHomeActivity.class));
                break;
            case 4:
                startActivity(new Intent(this, RuleViewActivity.class));
                break;
            default:
                break;
        }
    }
}
