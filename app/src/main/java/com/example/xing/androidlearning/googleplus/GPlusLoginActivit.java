package com.example.xing.androidlearning.googleplus;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.xing.androidlearning.R;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.IOException;

public class GPlusLoginActivit extends AppCompatActivity implements View.OnClickListener {
    final private String TAG = "GPlusLoginActivit";

    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;

    private boolean isGPlayServicesAvailable = false;
    private String mEmail;
    private final String GPLUS_SCOPE = "https://www.googleapis.com/auth/plus.login";
    private final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile" + " " +GPLUS_SCOPE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gplus_login);

        findViewById(R.id.gpl_btn_pickup_an_account).setOnClickListener(this);

        switch (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)) {
            case ConnectionResult.SUCCESS:
                isGPlayServicesAvailable = true;
                Log.d(TAG, "GooglePlayServicesAvailable SUCCESS");
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                isGPlayServicesAvailable = false;
                Log.d(TAG, "GooglePlayServicesAvailable UPDATE_REQUIRED");
                break;
            default:
                isGPlayServicesAvailable = false;
                break;
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.gpl_btn_pickup_an_account) {
            if (isGPlayServicesAvailable) {
                pickUserAccount();
            }
        }
    }

    private void pickUserAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            Log.d(TAG, "requestCode: " + resultCode);
            // Receiving a result from the AccountPicker
            if (resultCode == RESULT_OK) {
                mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                Log.d(TAG, "mEmail: " + mEmail);
                // With the account name acquired, go get the auth token
                getUsername(this);
            } else if (resultCode == RESULT_CANCELED) {
                // The account picker dialog closed without selecting an account.
                // Notify users that they must pick an account to proceed.
                Toast.makeText(this, "no account selected", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void getUsername(Context context) {

        final Context cxt = context;
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                Account account = new Account(mEmail, "com.google");
                String token;
                try {
                    token = GoogleAuthUtil.getToken(cxt, account, SCOPE);
                    if (token != null) {
                        Log.d(TAG, "token: " + token);
                    }
                } catch (Exception e) {
                    if (e instanceof UserRecoverableAuthException) {
                        Log.e(TAG, "Exception: UserRecoverableAuth");
                        Intent intent = ((UserRecoverableAuthException) e).getIntent();
                        startActivityForResult(intent,
                                REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    } else {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        }.execute();

    }
}
