package com.example.xing.androidlearning;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;


public class LoadingDialog extends Dialog {
    TextView tv;

    public LoadingDialog(Context context) {
        super(context, R.style.LoadingDialog);
        setContentView(R.layout.dlg_loading_small);
        setCanceledOnTouchOutside(false);

        tv = (TextView) findViewById(R.id.loading_Stv);
    }

    public void setLoadingText(String text) {
        tv.setText(text);
    }

    public void setLoadingText(int resid) {
        tv.setText(resid);
    }

    public void resetLoadingText() {
        tv.setText(R.string.loading);
    }
}