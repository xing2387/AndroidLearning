package com.example.xing.androidlearning;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.xing.androidlearning.googleplus.GPlusLoginActivit;
import com.example.xing.androidlearning.natives.JniEntry;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    TextView tvMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lvMenu = (ListView) findViewById(R.id.main_lv_menu);
        List<String> menuList = Arrays.asList("Login&SignUp", "TriangleIndicator", "FlowLayout", "Contacts", "GPlusLoginActivit");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuList);
        if (lvMenu != null) {
            lvMenu.setAdapter(adapter);
            lvMenu.setOnItemClickListener(this);
        }

        tvMain = (TextView) findViewById(R.id.main_tv);
        tvMain.setText(new JniEntry().getString());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                startActivity(new Intent(this, LoginAndSignUpActivity.class));
                break;
            case 1:
                startActivity(new Intent(this, TrianglePageViewIndicator.class));
                break;
            case 2:
                startActivity(new Intent(this, FlowLayoutActivity.class));
                break;
            case 3:
                startActivity(new Intent(this, ContactsHomeActivity.class));
                break;
            case 4:
                startActivity(new Intent(this, GPlusLoginActivit.class));
                break;
            default:
                break;
        }
    }
}
