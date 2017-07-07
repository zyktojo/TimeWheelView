package com.timewheel.mo.timewheel.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.aigestudio.wheelpicker.WheelPicker;
import com.timewheel.mo.timewheel.MainActivity;
import com.timewheel.mo.timewheel.R;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 预约时间滚轮
 * Created by mo on 2017/7/1.
 */

public class TimeWheelPicker extends RelativeLayout implements WheelPicker.OnItemSelectedListener, View.OnClickListener{

    private MainActivity activity;
    private Context context;
    private WheelPicker wheelDay, wheelHour, wheelMinute;
    private Button timeButton, timeCancal;
    private RelativeLayout timeView;
    private List<String> day, hour, minute;//预约车时间，天、小时、分钟
    private boolean isToday = true;//时间滚轮，是否是今天

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    public TimeWheelPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        View view = View.inflate(context, R.layout.view_time_wheel, this);

        wheelDay = (WheelPicker) view.findViewById(R.id.wheel_day);
        wheelHour = (WheelPicker) view.findViewById(R.id.wheel_hour);
        wheelMinute = (WheelPicker) view.findViewById(R.id.wheel_minute);
        timeButton = (Button) view.findViewById(R.id.choose_time_button);
        timeCancal = (Button) view.findViewById(R.id.choose_time_cancel);
        timeButton.setOnClickListener(this);
        timeCancal.setOnClickListener(this);

        wheelDay.setAtmospheric(true);
        wheelDay.setCurved(true);
        wheelDay.setIndicator(true);
        wheelDay.setIndicatorSize(2);
        wheelDay.setIndicatorColor(R.color.color_cdcdcd);

        wheelHour.setAtmospheric(true);
        wheelHour.setCurved(true);
        wheelHour.setIndicator(true);
        wheelHour.setIndicatorSize(2);
        wheelHour.setIndicatorColor(R.color.color_cdcdcd);

        wheelMinute.setAtmospheric(true);
        wheelMinute.setCurved(true);
        wheelMinute.setIndicator(true);
        wheelMinute.setIndicatorSize(2);
        wheelMinute.setIndicatorColor(R.color.color_cdcdcd);

        timeView = (RelativeLayout) view.findViewById(R.id.time_view);//时间框

        //初始化滚轮数据集合
        day = new ArrayList<>();
        hour = new ArrayList<>();
        minute = new ArrayList<>();
        day.add("今天");
        day.add("明天");
        day.add("后天");
        wheelDay.setData(day);
        addHours();//加入选项
        wheelHour.setData(hour);
        addMinute();//加入选项
        wheelMinute.setData(minute);

        setWheelTime();//设置滚轮时间,提前半个小时
        wheelHour.setOnWheelChangeListener(hourChangeListener);
    }

    @Override
    public void onItemSelected(WheelPicker picker, Object data, int position) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choose_time_button:
                this.setVisibility(View.GONE);
                showTimeView();
                break;
            case R.id.choose_time_cancel:
                //预约时间，取消按钮
                this.setVisibility(View.GONE);
                break;
        }
    }

    //显示预约时间滚轮
    public void showTimeView() {
        //预约时间，确定按钮
        String d = day.get(wheelDay.getCurrentItemPosition());
        //正则获取选中小时的int值
        String nowHour = hour.get(wheelHour.getCurrentItemPosition());
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher matcher = p.matcher(nowHour);
        int h = Integer.valueOf(matcher.replaceAll("").trim());
        //正则获取选中分钟的int值
        String nowMinute = minute.get(wheelMinute.getCurrentItemPosition());
        String regExMin = "[^0-9]";
        Pattern p2 = Pattern.compile(regExMin);
        Matcher matcher2 = p2.matcher(nowMinute);
        String m = matcher2.replaceAll("").trim();

        timeView.setVisibility(View.GONE);
        String curDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if (wheelDay.getCurrentItemPosition() == 0) {//今天
            activity.bespeakTime = curDate + " " + h + ":" + m + ":00";//组合预约时间，格式YYYY-MM-DD HH:MM:SS
        } else {//明天或后天
            activity.bespeakTime = getSpecifiedDayAfter(wheelDay.getCurrentItemPosition())
                    + " " + h + ":" + m + ":00";//组合预约时间，格式YYYY-MM-DD HH:MM:SS
        }
        activity.timeText.setText(d + " " + h + ":" + m);
    }

    //设置滚轮时间,提前半个小时
    public void setWheelTime() {
        //如果选今天，则去掉已经过去的那些“小时”选项
        wheelDay.setOnWheelChangeListener(new WheelPicker.OnWheelChangeListener() {
            @Override
            public void onWheelScrolled(int offset) {

            }

            @Override
            public void onWheelSelected(int position) {
                if (position == 0) {//选了今天
                    isToday = true;
                    if (hour.size() == 24) {
                        //如果选今天的当前小时，则只加载那些未来的小时数
                        hour.clear();
                        addHours();
                        wheelHour.setData(hour);
                    }
                    //如果小时是当前小时，分钟选项相应减少
                    if (wheelHour.getCurrentItemPosition() == 0) {
                        minute.clear();
                        addMinute();
                        wheelMinute.setData(minute);
                    }
                } else {
                    //选了明天或者后天
                    isToday = false;
                    if (hour.size() != 24) {
                        hour.clear();
                        for (int i = 0; i < 24; i++) {
                            hour.add(i + "时");
                        }
                        wheelHour.setData(hour);
                    }
                    //没选今天，分钟数没填满，自动填满
                    if (minute.size() != 6) {
                        minute.clear();
                        for (int i = 0; i < 6; i++) {
                            minute.add(i + "0分");
                        }
                        wheelMinute.setData(minute);
                    }
                }
            }

            @Override
            public void onWheelScrollStateChanged(int state) {

            }
        });
    }

    WheelPicker.OnWheelChangeListener hourChangeListener = new WheelPicker.OnWheelChangeListener() {
        @Override
        public void onWheelScrolled(int offset) {

        }

        @Override
        public void onWheelSelected(int position) {
            if (position == 0) {
                if (isToday) {
                    minute.clear();
                    addMinute();
                    wheelMinute.setData(minute);
                }
            } else {
                if (minute.size() != 6) {
                    minute.clear();
                    for (int i = 0; i < 6; i++) {
                        minute.add(i + "0分");
                    }
                    wheelMinute.setData(minute);
                }
            }
        }

        @Override
        public void onWheelScrollStateChanged(int state) {

        }
    };

    //按照提前40分钟的逻辑，加载小时列表
    private void addHours() {
        if (Calendar.getInstance().get(Calendar.MINUTE) > 19) {
            if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 23){
                for (int i = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 1; i < 24; i++) {
                    hour.add(i + "时");
                }
            }else{
                hour.add("23时");
            }
        } else {
            for (int i = Calendar.getInstance().get(Calendar.HOUR_OF_DAY); i < 24; i++) {
                hour.add(i + "时");
            }
        }
    }

    //按照提前40分钟的逻辑，加载分钟列表
    private void addMinute() {
        if (Calendar.getInstance().get(Calendar.MINUTE) > 19) {
            if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 23){
                for (int i = Calendar.getInstance().get(Calendar.MINUTE) / 10 - 2; i < 6; i++) {
                    minute.add(i + "0分");
                }
            }else{
                for (int i = Calendar.getInstance().get(Calendar.MINUTE) / 10; i < 6; i++) {
                    minute.add(i + "0分");
                }
            }
        } else {
            for (int i = Calendar.getInstance().get(Calendar.MINUTE) / 10 + 4; i < 6; i++) {
                minute.add(i + "0分");
            }
        }
    }

    /**
     * 获得指定日期的后几天
     * @param i
     * @return
     */
    public static String getSpecifiedDayAfter(int i){
        Format f = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DAY_OF_MONTH, i);// 今天+1天
        Date tomorrow = c.getTime();
        return  f.format(tomorrow);
    }
}
