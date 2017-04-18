package com.fee.clockview;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private TextView mTvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mTvTime = (TextView) findViewById(R.id.tv_time);
        mHandler.sendEmptyMessage(0);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mTvTime.setText(getTime());
            mHandler.sendEmptyMessage(0);
        }
    };
    private String getTime() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR)+":"+
                calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);
    }
}
