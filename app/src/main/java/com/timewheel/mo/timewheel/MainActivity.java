package com.timewheel.mo.timewheel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.timewheel.mo.timewheel.views.TimeWheelPicker;

public class MainActivity extends AppCompatActivity {

    public String bespeakTime;//滚轮时间，字符串
    public TextView timeText;//预约时间，文字框
    private Button show;
    private TimeWheelPicker timeWheel;//时间滚轮,自定义控件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeWheel = (TimeWheelPicker) findViewById(R.id.time_view);
        timeWheel.setActivity(this);
        timeText = (TextView) findViewById(R.id.choose_time);//预约时间,文字
        show = (Button) findViewById(R.id.show);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeWheel.setVisibility(View.VISIBLE);//隐藏预约时间框
            }
        });
    }
}
