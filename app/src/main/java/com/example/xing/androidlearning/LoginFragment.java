package com.example.xing.androidlearning;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment {

    Button btnCommit;
    public LoginFragment() {
    }

    ScrollView sl_login ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        sl_login = (ScrollView) rootView.findViewById(R.id.sl_login);
        (rootView.findViewById(R.id.et_login_username)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                changeScrollView();
                return false;
            }
        });
        (rootView.findViewById(R.id.et_login_password)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                changeScrollView();
                return false;
            }
        });
        (rootView.findViewById(R.id.btn_login_signup)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.fl_login_signup, new SignUpFragment()).commit();
            }
        });
        btnCommit = (Button)rootView.findViewById(R.id.btn_signin_commit);
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.fl_login_signup, new SignUpFragment()).commit();
            }
        });
        return rootView;
    }
    private void changeScrollView(){
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                sl_login.scrollTo(0, sl_login.getHeight());
            }
        }, 300);
    }
}
