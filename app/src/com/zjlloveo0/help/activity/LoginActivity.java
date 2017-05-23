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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.permission.MPermission;
import com.netease.nim.uikit.permission.annotation.OnMPermissionDenied;
import com.netease.nim.uikit.permission.annotation.OnMPermissionGranted;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.zjlloveo0.help.R;
import com.zjlloveo0.help.bean.UserSchool;
import com.zjlloveo0.help.config.preference.Preferences;
import com.zjlloveo0.help.utils.DemoCache;
import com.zjlloveo0.help.utils.Request2Server;
import com.zjlloveo0.help.utils.SYSVALUE;
import com.zjlloveo0.help.utils.SystemUtil;

import org.json.JSONObject;

import java.util.Date;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends UI {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private static final String KICK_OUT = "KICK_OUT";
    private UserLoginTask mAuthTask = null;
    private UserSchool currentUser = null;
    private int nimLoginRe;//默认0 成功1 密码错误2 异常3
    private int resLogin;//默认0 成功1 密码错误2 异常3
    // UI references.
    private Button btLogin;
    private EditText mAccountView;
    private EditText mPasswordView;
    private EditText mRePWD;
    private EditText mName;
    private View mProgressView;
    private View mLoginFormView;
    private Drawable filedLegal;
    private LinearLayout ll_checkNum;
    private EditText et_checkNum;
    private Button bt_getCheckNum;
    private int i = 0;
    private boolean trueCheckNum = false;
    private EventHandler eh;
    private Thread checkTimer;
    private final int BASIC_PERMISSION_REQUEST_CODE = 1100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SYSVALUE.currentUser = null;
        SMSSDK.initSDK(this, "1d3bfce999723", "d02c9bfe1e3f40f6b4b62e1a38613768");
        eh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
//                showToast(event+"*"+result+"*"+data.toString());
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                trueCheckNum = true;
                                et_checkNum.setError("", filedLegal);
                                et_checkNum.setEnabled(false);
                                bt_getCheckNum.setEnabled(false);
                                showToast("验证成功！");
                            }
                        });
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                        boolean smart = (Boolean) data;
                        if (smart) {
                            //通过智能验证
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    trueCheckNum = true;
                                    et_checkNum.setError("", filedLegal);
                                    et_checkNum.setEnabled(false);
                                    bt_getCheckNum.setEnabled(false);
                                    showToast("验证通过！" + mAccountView.getText().toString() + "为诚信号码，无须验证短信！");
                                }
                            });
                        } else {
                            //依然走短信验证
                            checkTimer = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            bt_getCheckNum.setEnabled(false);
                                        }
                                    });
                                    i = 90;
                                    try {
                                        while (!trueCheckNum) {
                                            i--;
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (i < 0) {
                                                        bt_getCheckNum.setText("获取验证码");
                                                        bt_getCheckNum.setEnabled(true);
                                                    } else {
                                                        bt_getCheckNum.setText(i + "S");
                                                    }
                                                }
                                            });
                                            Thread.sleep(1000);
                                            if (i < 0) {
                                                break;
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            checkTimer.start();
                        }
                    }
                } else if (result == SMSSDK.RESULT_ERROR) {
                    try {
                        JSONObject resMsg = new JSONObject(((Throwable) data).getMessage());
                        showToast(resMsg.getString("detail"));
                    } catch (Exception e) {
                        showToast("系统异常！");
                        e.printStackTrace();
                    }
                } else {
                    showToast("系统异常！");
                    ((Throwable) data).printStackTrace();
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调
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
        ll_checkNum = (LinearLayout) findViewById(R.id.ll_checkNum);
        et_checkNum = (EditText) findViewById(R.id.et_checkNum);
        bt_getCheckNum = (Button) findViewById(R.id.bt_getCheckNum);

        et_checkNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_checkNum.getText().toString().length() == 4) {
                    SMSSDK.submitVerificationCode("86", mAccountView.getText().toString().trim(), et_checkNum.getText().toString().trim());
                }
            }
        });

        mAccountView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (checkTimer != null && checkTimer.isAlive()) {
                    i = -1;
                }
                et_checkNum.setText("");
                trueCheckNum = false;
                et_checkNum.setEnabled(true);
                et_checkNum.setError(null);
                bt_getCheckNum.setEnabled(true);
                bt_getCheckNum.setText("获取验证码");
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
                                            mRePWD.setText("");
                                            mName.setVisibility(View.GONE);
                                            mRePWD.setVisibility(View.GONE);
                                            ll_checkNum.setVisibility(View.GONE);
                                        }
                                    });
                                } else if (findRes.getInt("code") == 0) {//未注册
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mAccountView.setError("此号码未注册请注册", filedLegal);
                                            btLogin.setText("注册");
                                            mPasswordView.setText("");
                                            mRePWD.setText("");
                                            mName.setVisibility(View.VISIBLE);
                                            mRePWD.setVisibility(View.VISIBLE);
                                            ll_checkNum.setVisibility(View.VISIBLE);
                                            bt_getCheckNum.setEnabled(true);
                                            bt_getCheckNum.setText("获取验证码");
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
                SMSSDK.unregisterEventHandler(eh);//注销回调接口
                if (mRePWD.getVisibility() == View.VISIBLE) {
                    attemptReg();
                } else {
                    attemptLogin();
                }
            }
        });
        bt_getCheckNum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SMSSDK.getVerificationCode("86", mAccountView.getText().toString().trim());
            }
        });
        requestBasicPermission();
    }

    private int loginToServer(final String mAccount, final String mPassword) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject requestRes;
                try {
                    requestRes = new JSONObject(Request2Server.getRequsetResult(SYSVALUE.HOST + "login?phone=" + mAccount + "&password=" + mPassword));
                    switch (requestRes.getInt("code")) {
                        case 201:
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
                            currentUser.setUpdateTime(SystemUtil.convert(jsonObj.getString("updateTime")));
                            currentUser.setSchoolName(jsonObj.getString("schoolName"));
                            currentUser.setCollegeName(jsonObj.getString("collegeName"));
                            SYSVALUE.currentUser = currentUser;
                            resLogin = 1;
                            break;//登录成功
                        case 106:
                            currentUser = null;
                            resLogin = 2;
                            showToast("密码错误");
                            break;//登录失败、密码错误
                        case 112:
                            resLogin = 0;
                            currentUser = null;
                            showToast("登录信息不完整！");
                            break;//缺少登录信息
                        default:
                            resLogin = 3;
                            currentUser = null;
                            showToast("系统错误！");
                            break;//系统错误
                    }
                } catch (Exception e) {
                    resLogin = 3;
                    currentUser = null;
                    showToast("系统或网络异常！");
                    e.printStackTrace();
                }
            }
        }).start();
        try {
            long startTime = System.currentTimeMillis();
            while (resLogin == 0) {
                Thread.sleep(1000);
                if (System.currentTimeMillis() - startTime > 10000L) {
                    showToast(getString(R.string.internet_error));
                    break;
                }
            }
        } catch (InterruptedException e) {
            showToast(getString(R.string.system_error));
            currentUser = null;
            resLogin = 3;
        }
        return resLogin;
    }

    private void attemptReg() {
        mAccountView.setError(null);
        mPasswordView.setError(null);
        mRePWD.setError(null);
        mName.setError(null);
        et_checkNum.setError(null);

        String phone = mAccountView.getText().toString();
        String password = mPasswordView.getText().toString();
        String rePassword = mRePWD.getText().toString();
        String checkNum = et_checkNum.getText().toString();
        String name = mName.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(checkNum) && !trueCheckNum) {
            et_checkNum.setError(getString(R.string.error_field_required));
            focusView = et_checkNum;
            cancel = true;
        } else if (!isCheckNumValid(checkNum)) {
            et_checkNum.setError("验证码为四位数字");
            focusView = et_checkNum;
            cancel = true;
        } else if (!trueCheckNum) {
            et_checkNum.setError("验证码错误");
            focusView = et_checkNum;
            cancel = true;
        }

        if (TextUtils.isEmpty(name)) {
            et_checkNum.setError(getString(R.string.error_field_required));
            focusView = et_checkNum;
            cancel = true;
        }

        if (TextUtils.isEmpty(rePassword)) {
            mRePWD.setError(getString(R.string.error_field_required));
            focusView = mRePWD;
            cancel = true;
        } else if (!isPasswordValid(rePassword)) {
            mRePWD.setError(getString(R.string.error_invalid_password));
            focusView = mRePWD;
            cancel = true;
        } else if (!password.equals(rePassword)) {
            mRePWD.setError("两次密码不一致！");
            focusView = mRePWD;
            cancel = true;
        }
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if (TextUtils.isEmpty(phone)) {
            mAccountView.setError(getString(R.string.error_field_required));
            focusView = mAccountView;
            cancel = true;
        } else if (!isAccountValid(phone)) {
            mAccountView.setError(getString(R.string.error_invalid_account));
            focusView = mAccountView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
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
                                currentUser.setId(Integer.valueOf(obj.getString("accid")));
                                currentUser.setPassword(obj.getString("token"));
                                showToast(currentUser.getName() + " 恭喜您注册成功！");
                                mAuthTask = new UserLoginTask(currentUser.getId().toString(), currentUser.getPassword(), mAccountView.getText().toString().trim());
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
            if (loginToServer(account, password) == 1) {
                mAuthTask = new UserLoginTask(currentUser.getId().toString(), password, currentUser.getPhone());
                mAuthTask.execute((Void) null);
            } else {
                showProgress(false);
            }
        }
    }

    private boolean isAccountValid(String account) {
        return !(account == null || "".equals(account) || account.length() != 11 || !account.matches(SYSVALUE.REGEX_PHONE));
    }

    private boolean isPasswordValid(String password) {
        return !(password == null || "".equals(password) || password.length() < 6 || !password.matches(SYSVALUE.REGEX_PASSWORD));
    }

    private boolean isCheckNumValid(String checkNum) {
        return !(checkNum == null || "".equals(checkNum) || checkNum.length() < 4);
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
        private final String mPhone;

        UserLoginTask(String account, String password, String phone) {
            mAccount = account;
            mPassword = password;
            mPhone = phone;
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            nimLoginRe=0;
            LoginInfo info = new LoginInfo(mAccount,mPassword); // config...

            RequestCallback<LoginInfo> callback =
                    new RequestCallback<LoginInfo>() {
                        @Override
                        public void onSuccess(LoginInfo loginInfo) {
                            DemoCache.setAccount(currentUser.getId() + "");
                            saveLoginInfo(mPhone, mPassword);
                            nimLoginRe = 1;
                        }
                        @Override
                        public void onFailed(int code) {
                            if (code == 302 || code == 404) {
                                Toast.makeText(LoginActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "登录失败: " + code, Toast.LENGTH_SHORT).show();
                            }
                            mAccountView.setError(null);
                            saveLoginInfo("", "");
                            currentUser = null;
                            nimLoginRe=2;
                        }
                        @Override
                        public void onException(Throwable throwable) {
                            Toast.makeText(LoginActivity.this, "系统异常", Toast.LENGTH_SHORT).show();
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

    private void saveLoginInfo(String account, String token) {
        Preferences.saveUserAccount(account);
        Preferences.saveUserToken(token);
    }

    //    public void getPermission() {
//        SharedPreferences sp = getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
//        if (!sp.getBoolean("isFirst", true)) {
//            readLoginInfo();
//            return;
//        }
//        int PERMISSION_REQUEST_CODE = 1;
//        String[] permissions = new String[2];
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.READ_SMS)
//                    == PackageManager.PERMISSION_DENIED) {
//                permissions[0] = Manifest.permission.READ_SMS;
//            }
//            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
//                    == PackageManager.PERMISSION_DENIED) {
//                permissions[1] = Manifest.permission.READ_PHONE_STATE;
//            }
//            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
//        }
//
//        SharedPreferences.Editor editor = getSharedPreferences("LoginInfo", Context.MODE_PRIVATE).edit();
//        editor.putBoolean("isFirst", false);
//        editor.commit();
//    }
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

    private void requestBasicPermission() {
        MPermission.with(LoginActivity.this)
                .addRequestCode(BASIC_PERMISSION_REQUEST_CODE)
                .permissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS
                )
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @OnMPermissionGranted(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionSuccess() {
        SharedPreferences sp = getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
        if (!sp.getBoolean("isFirst", true)) {
            readLoginInfo();
            return;
        }
        String localPhone = getPhoneNum();
        SharedPreferences.Editor editor = getSharedPreferences("LoginInfo", Context.MODE_PRIVATE).edit();
        mAccountView.setText(localPhone);
        editor.putString("localPhone", localPhone);
        editor.putBoolean("isFirst", false);
        editor.commit();
        Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
    }

    @OnMPermissionDenied(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionFailed() {
        Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
    }

    //    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case 1:
//                if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                    // 同意授权
//                    String localPhone = getPhoneNum();
//                    SharedPreferences.Editor editor = getSharedPreferences("LoginInfo", Context.MODE_PRIVATE).edit();
//                    mAccountView.setText(localPhone);
//                    editor.putString("localPhone", localPhone);
//                    editor.commit();
//                } else {
//                    // 不同意授权
//                    showToast("用户拒绝接受权限");
//                }
//                break;
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }
    public static void start(Context context) {
        start(context, false);
    }

    public static void start(Context context, boolean kickOut) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(KICK_OUT, kickOut);
        context.startActivity(intent);
    }
}

