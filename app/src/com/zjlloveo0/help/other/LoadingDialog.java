package com.zjlloveo0.help.other;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zjlloveo0.help.R;

/**
 * Created by 27c1 on 2017/1/4.
 */
public class LoadingDialog extends Dialog {
    private TextView tv;

    /**
     * style很关键
     */
    public LoadingDialog(Context context) {
        super(context, R.style.loadingDialogStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        tv = (TextView) findViewById(R.id.tv);
        tv.setText("加载中...");
        LinearLayout linearLayout = (LinearLayout) this.findViewById(R.id.LinearLayout);
        linearLayout.getBackground().setAlpha(210);
    }
}