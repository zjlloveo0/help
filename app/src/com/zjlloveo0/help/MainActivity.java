package com.zjlloveo0.help;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button bt_add_service;
    private Button bt_add_mission;
    private Snackbar snackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bt_add_service=(Button)findViewById(R.id.bt_add_service);
        bt_add_mission=(Button)findViewById(R.id.bt_add_mission);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackBar= Snackbar.make(view,"", Snackbar.LENGTH_LONG);
                addViewToSnackBar(snackBar,R.layout.snackbar_new_mission,0);
                snackBar.show();
            }
        });
    }
    public void addViewToSnackBar(Snackbar snackbar, int layoutId, int index) {
        View snackBarView = snackbar.getView();
        Snackbar.SnackbarLayout snackBarLayout=(Snackbar.SnackbarLayout)snackBarView;
        View add_view = LayoutInflater.from(snackBarView.getContext()).inflate(layoutId,null);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        p.gravity= Gravity.CENTER_VERTICAL;
        snackBarLayout.addView(add_view,index,p);
    }
    public void addService(View v){
        snackBar.dismiss();
        Toast.makeText(getApplicationContext(),"1111",Toast.LENGTH_SHORT).show();
    }
    public void addMission(View v){
        snackBar.dismiss();
        Toast.makeText(getApplicationContext(),"2222",Toast.LENGTH_SHORT).show();
    }

}
