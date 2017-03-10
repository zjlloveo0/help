package com.zjlloveo0.help.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.zjlloveo0.help.R;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private int nimLoginRe;//默认0 成功1 密码错误2 异常3
    // UI references.
    private EditText mAccountView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mAccountView = (EditText) findViewById(R.id.et_account);
        mPasswordView = (EditText) findViewById(R.id.password);
        readLoginInfo();
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mAccountSignInButton = (Button) findViewById(R.id.account_sign_in_button);
        mAccountSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }
    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        mAccountView.setError(null);
        mPasswordView.setError(null);

        String account = mAccountView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(account)) {
            mAccountView.setError(getString(R.string.error_field_required));
            focusView = mAccountView;
            cancel = true;
        } else if (!isAccountValid(account)) {
            mAccountView.setError(getString(R.string.error_invalid_account));
            focusView = mAccountView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserLoginTask(account, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isAccountValid(String account) {
        //TODO: Replace this with your own logic
//        return account.length()==11;
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 6;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mAccount;
        private final String mPassword;

        UserLoginTask(String account, String password) {
            mAccount = account;
            mPassword = password;
        }
        public void showToast(final String s){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
                }
            });
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            nimLoginRe=0;
            LoginInfo info = new LoginInfo(mAccount,mPassword); // config...

            RequestCallback<LoginInfo> callback =
                    new RequestCallback<LoginInfo>() {
                        @Override
                        public void onSuccess(LoginInfo loginInfo) {
                            saveLoginInfo(mAccount,mPassword,mPassword);
                            nimLoginRe=1;
                        }

                        @Override
                        public void onFailed(int i) {

                            nimLoginRe=2;
                        }

                        @Override
                        public void onException(Throwable throwable) {
                            nimLoginRe=3;
                        }
                    };
            NIMClient.getService(AuthService.class).login(info)
                    .setCallback(callback);
            try {
                long startTime=System.currentTimeMillis();

                while(nimLoginRe==0){
                    Thread.sleep(1000);
                    if(System.currentTimeMillis()-startTime>10000L){
                        showToast(getString(R.string.internet_error));
                        break;
                    }
                }
            } catch (InterruptedException e) {
                showToast(getString(R.string.system_error)+"(USERLOGINTASK)");
                nimLoginRe = 3;
            }
            if(nimLoginRe==1){
                showToast(getString(R.string.login_success));
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                return true;
            }
//          for (String credential : DUMMY_CREDENTIALS) {
//                String[] pieces = credential.split(":");
//                if (pieces[0].equals(mAccount)) {
//                    // Account exists, return true if the password matches.
//                    return pieces[1].equals(mPassword);
//                }
//            }


            // TODO: register the new account here.
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
    private void saveLoginInfo(String account,String password, String token) {
        SharedPreferences.Editor editor = getSharedPreferences("LoginInfo", Context.MODE_PRIVATE).edit();
        editor.putString("account", account);
        editor.putString("token", token);
        editor.putString("password",password);
        editor.commit();
    }
    private void readLoginInfo() {
        SharedPreferences sp=getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
        String account="";
        String token="";
        String password="";
        if(sp!=null) {
            account = sp.getString("account", "");
            token = sp.getString("token", "");
            password = sp.getString("password", "");
        }
        if(!("".equals(account)||"".equals(token)||"".equals(password))){
            mAccountView.setText(account);
            mPasswordView.setText(password);
        }
    }
}

