package com.zjlloveo0.help.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.zjlloveo0.help.R;
import com.zjlloveo0.help.model.UserSchool;
import com.zjlloveo0.help.utils.Request2Server;
import com.zjlloveo0.help.utils.SYSVALUE;

import org.json.JSONObject;

import java.util.Date;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private UserSchool currentUser = null;
    private int nimLoginRe;//默认0 成功1 密码错误2 异常3
    // UI references.
    private Button btLogin;
    private EditText mAccountView;
    private EditText mPasswordView;
    private EditText mRePWD;
    private EditText mName;
    private View mProgressView;
    private View mLoginFormView;
    private Drawable filedLegal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        filedLegal = getResources().getDrawable(R.drawable.field_legal);
        filedLegal.setBounds(0, 0, 50, 50);

        // Set up the login form.
        btLogin = (Button) findViewById(R.id.account_sign_in_button);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mAccountView = (EditText) findViewById(R.id.et_account);
        mPasswordView = (EditText) findViewById(R.id.password);
        mRePWD = (EditText) findViewById(R.id.et_rePWD);
        mName = (EditText) findViewById(R.id.et_name);

        mAccountView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mAccountView.getText().toString().trim().length() == 11) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String isReg = "";
                            JSONObject findRes = null;
                            try {
                                isReg = Request2Server.getRequsetResult(SYSVALUE.HOST + "findUser?phone=" + mAccountView.getText());
                                findRes = new JSONObject(isReg);
                                if (findRes.getInt("code") > 0) {//已注册
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mAccountView.setError("此号码已注册请登录", filedLegal);
                                            btLogin.setText("登录");
                                            mName.setVisibility(View.GONE);
                                            mRePWD.setVisibility(View.GONE);
                                        }
                                    });
                                } else if (findRes.getInt("code") == 0) {//未注册
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mAccountView.setError("此号码未注册请注册", filedLegal);
                                            btLogin.setText("注册");
                                            mName.setVisibility(View.VISIBLE);
                                            mRePWD.setVisibility(View.VISIBLE);
                                        }
                                    });
                                } else {
                                    showToast("系统异常！");
                                }
                            } catch (Exception e) {
                                showToast("系统或网络异常！");
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });

        Button mAccountSignInButton = (Button) findViewById(R.id.account_sign_in_button);
        mAccountSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRePWD.getVisibility() == View.VISIBLE) {
                    attemptReg();
                } else {
                    attemptLogin();
                }
            }
        });
        getPermission();
    }

    private void attemptReg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject requestRes;
                try {
                    requestRes = new JSONObject(Request2Server.getRequsetResult(SYSVALUE.HOST + "reg?phone=" + mAccountView.getText() + "&password=" + mPasswordView.getText() + "&name=" + mName.getText()));
                    //{"code":200,"content":{"code":200,"info":{"token":"111s111","accid":"13253375511","name":"123"}}}
                    //{"code":105,"content":"密码格式错误！"}
                    //{"code":104,"content":"手机号格式错误！"}
                    //{"code":112,"content":"您填写的用户信息不完整！"}
                    if (requestRes.getInt("code") == 200) {
                        JSONObject jsonObj = requestRes.getJSONObject("content");
                        if (jsonObj.getInt("code") == 200) {
                            JSONObject obj = jsonObj.getJSONObject("info");
                            currentUser = new UserSchool();
                            currentUser.setName(obj.getString("name"));
                            currentUser.setPhone(obj.getString("accid"));
                            currentUser.setPassword(obj.getString("token"));
                            mAuthTask = new UserLoginTask(currentUser.getPhone(), currentUser.getPassword());
                            mAuthTask.execute((Void) null);
                        }
                    } else {
                        showToast(requestRes.getString("content"));
                    }
                } catch (Exception e) {
                    showToast("系统或网络异常！");
                    e.printStackTrace();
                }
            }
        }).start();
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

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
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
        return !(account == null || "".equals(account) || account.length() != 11 || !account.matches(SYSVALUE.REGEX_PHONE));
    }

    private boolean isPasswordValid(String password) {
        return !(password == null || "".equals(password) || password.length() < 6 || !password.matches(SYSVALUE.REGEX_PASSWORD));
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

    public void showToast(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        });
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
        @Override
        protected Boolean doInBackground(Void... params) {
            nimLoginRe=0;
            LoginInfo info = new LoginInfo(mAccount,mPassword); // config...

            RequestCallback<LoginInfo> callback =
                    new RequestCallback<LoginInfo>() {
                        @Override
                        public void onSuccess(LoginInfo loginInfo) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    JSONObject requestRes;
                                    try {
                                        requestRes = new JSONObject(Request2Server.getRequsetResult(SYSVALUE.HOST + "login?phone=" + mAccount + "&password=" + mPassword));
                                        switch (requestRes.getInt("code")) {
                                            case 201:
                                                /**
                                                 * {"id": 1,"name": "zjl","phone": "13253375512","password": "111111","img": "head/13253375512.jpg","stuNum": "4554554","point": 100,"collegeId": 1,
                                                 "star": 5,"isEnable": 1,"updateTime": "Mon Apr 10 12:27:35 CST 2017","schoolName": "河南工业大学","collegeName": "信息科学与工程学院","createMissionNum": 2,"createServerNum": 1}
                                                 */
                                                JSONObject jsonObj = requestRes.getJSONObject("content");
                                                currentUser = new UserSchool();
                                                String id = jsonObj.getString("id");
                                                String point = jsonObj.getString("point");
                                                String collegeId = jsonObj.getString("collegeId");
                                                String star = jsonObj.getString("star");
                                                String isEnable = jsonObj.getString("isEnable");
                                                String createMissionNum = jsonObj.getString("createMissionNum");
                                                String createServerNum = jsonObj.getString("createServerNum");

                                                currentUser.setId(id.equals("null") ? null : Integer.valueOf(id));
                                                currentUser.setPoint(point.equals("null") ? null : Integer.valueOf(point));
                                                currentUser.setCollegeId(collegeId.equals("null") ? null : Integer.valueOf(collegeId));
                                                currentUser.setStar(star.equals("null") ? null : Integer.valueOf(star));
                                                currentUser.setIsEnable(isEnable.equals("null") ? null : Integer.valueOf(isEnable));
                                                currentUser.setCreateMissionNum(createMissionNum.equals("null") ? null : Integer.valueOf(createMissionNum));
                                                currentUser.setCreateServerNum(createServerNum.equals("null") ? null : Integer.valueOf(createServerNum));
                                                currentUser.setName(jsonObj.getString("name"));
                                                currentUser.setPhone(jsonObj.getString("phone"));
                                                currentUser.setPassword(jsonObj.getString("password"));
                                                currentUser.setImg(jsonObj.getString("img"));
                                                currentUser.setStuNum(jsonObj.getString("stuNum"));
                                                currentUser.setUpdateTime(new Date(jsonObj.getString("updateTime")));
                                                currentUser.setSchoolName(jsonObj.getString("schoolName"));
                                                currentUser.setCollegeName(jsonObj.getString("collegeName"));

                                                nimLoginRe = 1;
                                                break;//登录成功
                                            case 106:
                                                currentUser = null;
                                                nimLoginRe = 2;
                                                showToast("密码错误");
                                                break;//登录失败、密码错误
                                            case 112:
                                                nimLoginRe = 0;
                                                currentUser = null;
                                                showToast("登录信息不完整！");
                                                break;//缺少登录信息
                                            default:
                                                nimLoginRe = 3;
                                                currentUser = null;
                                                showToast("系统错误！");
                                                break;//系统错误
                                        }
                                    } catch (Exception e) {
                                        nimLoginRe = 3;
                                        currentUser = null;
                                        showToast("系统或网络异常！");
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                        @Override
                        public void onFailed(int i) {
                            saveLoginInfo("", "");
                            currentUser = null;
                            nimLoginRe=2;
                        }
                        @Override
                        public void onException(Throwable throwable) {
                            nimLoginRe=3;
                            currentUser = null;
                            saveLoginInfo("", "");
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
                showToast(getString(R.string.system_error));
                currentUser = null;
                nimLoginRe = 3;
            }
            if(nimLoginRe==1){
                showToast(getString(R.string.login_success));
                saveLoginInfo(mAccount, mPassword);
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                return true;
            }

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

    private void saveLoginInfo(String account, String password) {
        SharedPreferences.Editor editor = getSharedPreferences("LoginInfo", Context.MODE_PRIVATE).edit();
        editor.putString("account", account);
        editor.putString("token", password);
        editor.commit();
    }

    public void getPermission() {
        SharedPreferences sp = getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
        if (!sp.getBoolean("isFirst", true)) {
            readLoginInfo();
            return;
        }
        int PERMISSION_REQUEST_CODE = 1;
        String[] permissions = new String[2];

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_SMS)
                    == PackageManager.PERMISSION_DENIED) {
                permissions[0] = Manifest.permission.READ_SMS;
            }
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_DENIED) {
                permissions[1] = Manifest.permission.READ_PHONE_STATE;
            }
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }

        SharedPreferences.Editor editor = getSharedPreferences("LoginInfo", Context.MODE_PRIVATE).edit();
        editor.putBoolean("isFirst", false);
        editor.commit();
    }
    private void readLoginInfo() {
        SharedPreferences sp=getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
        String account="";
        String token="";
        String localPhone = "";

        if(sp!=null) {
            account = sp.getString("account", "");
            token = sp.getString("token", "");
            localPhone = sp.getString("localPhone", "");
        }
        if (!("".equals(account) || "".equals(token))) {
            mAccountView.setText(account);
            mPasswordView.setText(token);
            // TODO:自动登录
//            if(mAuthTask==null) {
//                mAuthTask = new UserLoginTask(account, token);
//                mAuthTask.execute((Void) null);
//            }
        } else {
            mAccountView.setText(localPhone);
        }
    }

    private String getPhoneNum() {
        String phoneNum = "";
        try {
            TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            phoneNum = tm.getLine1Number();//获取本机号码
//          String deviceid = tm.getDeviceId();//获取智能设备唯一编号
//          String imei = tm.getSimSerialNumber();//获得SIM卡的序号
//          String imsi = tm.getSubscriberId();//得到用户Id
            if (phoneNum.startsWith("+86")) {
                phoneNum = phoneNum.substring(3);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return phoneNum;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // 同意授权
                    String localPhone = getPhoneNum();
                    SharedPreferences.Editor editor = getSharedPreferences("LoginInfo", Context.MODE_PRIVATE).edit();
                    mAccountView.setText(localPhone);
                    editor.putString("localPhone", localPhone);
                    editor.commit();
                } else {
                    // 不同意授权
                    showToast("用户拒绝接受权限");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}

