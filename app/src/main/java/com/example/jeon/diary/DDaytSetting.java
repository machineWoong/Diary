package com.example.jeon.diary;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DDaytSetting extends Activity {
    int tYear;
    int tMonth;
    int tDay;  // 오늘 날짜

    int dYear;
    int dMonth;
    int dDay;  // 선택 날짜

    long TodayMil;
    long DdayMil;
    long ResultMil;  // 시간계산

    int bgNumber;

    int resultNumber=0;
    String DdayTitle;

    Calendar tc = Calendar.getInstance();
    Calendar dc = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ddayt_setting);

        changeBackground();// 배경 변경
        getToday(); //현재 날짜 받아와서 텍스트뷰에 출력
        getDayToCalender();// 선택한 날짜 토스트



        Button save = (Button)findViewById(R.id.ddaySaveBtn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTilte();// 타이틀 저장하기.
                returnDate();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });

        Button cancel = (Button)findViewById(R.id.ddayCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(0000);
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });
    }





    // 현재 날짜 구하기
    public void getToday(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("<<  yyyy-MM-dd  >>");
        String getTime = sdf.format(date);

        TextView today = (TextView)findViewById(R.id.TodayText);
        today.setText(getTime);

        tYear = tc.get(Calendar.YEAR);
        tMonth = tc.get(Calendar.MONTH);
        tDay = tc.get(Calendar.DAY_OF_MONTH);
    }

    // 선택된 날짜 구하기
    public void getDayToCalender(){
        CalendarView dcv = (CalendarView) findViewById(R.id.ddayCalender);
        dcv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Toast.makeText(DDaytSetting.this, "<< "+year +" / "+month +" / "+dayOfMonth +" >>", Toast.LENGTH_SHORT).show();
                TextView selectDay = (TextView)findViewById(R.id.selectDay);
                selectDay.setText(""+year +" / "+month +" / "+dayOfMonth);
                dYear = year;
                dMonth = month;
                dDay = dayOfMonth;

                changeMil();
            }
        });
    }

    // 밀리타임으로변 환
    public void changeMil(){

        dc.set(dYear,dMonth,dDay);

        TodayMil = tc.getTimeInMillis();  //오늘 날짜 밀리 초
        DdayMil = dc.getTimeInMillis(); // 디데이 날짜 밀리 초
        ResultMil = (DdayMil-TodayMil)/(24*60*60*1000); // 디데이 - 오늘날짜  =  결과를 일 단위로 바꿈

        resultNumber=(int)ResultMil;

        if(resultNumber>0){
            TextView ddayCount = (TextView)findViewById(R.id.ddayText);
            ddayCount.setText(String.format("- %d", resultNumber));
        }
        else if(resultNumber < 0){
            int absR=Math.abs(resultNumber);
            TextView ddayCount = (TextView)findViewById(R.id.ddayText);
            ddayCount.setText(String.format("+ %d", absR));
        }
        else{
            TextView ddayCount = (TextView)findViewById(R.id.ddayText);
            ddayCount.setText(String.format(" %d", resultNumber));
        }
    }

    // 에디트텍스트 값 받아오기.
    public void getTilte(){
        try{
            EditText dtilte = (EditText)findViewById(R.id.ddayTitle);
            DdayTitle = dtilte.getText().toString();
        }catch (Exception e){

        }
    }

    // 인텐트 결과 반환
    public void returnDate(){
        if ( dDay == 0 || TextUtils.isEmpty(DdayTitle)){
            Toast.makeText(DDaytSetting.this, "모든 데이터를 입력해 주세요", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent getDDayData = new Intent();
            getDDayData.putExtra("dYear",dYear);
            getDDayData.putExtra("dMonth",dMonth);
            getDDayData.putExtra("dDay",dDay);
            getDDayData.putExtra("dTitle",DdayTitle);
            setResult(1111,getDDayData);
            finish();
        }
    }

    // 배경 변경
    public void changeBackground(){

        LinearLayout MainBg = (LinearLayout)findViewById(R.id.DdaySettingView);
        SharedPreferences BackGround = getSharedPreferences("BackGround",0);
        bgNumber = BackGround.getInt("BG",0);

        if ( bgNumber == 0){
            MainBg.setBackgroundResource(R.drawable.pink);
        }
        else if ( bgNumber == 1){
            MainBg.setBackgroundResource(R.drawable.background1);
        }
        else if ( bgNumber == 2){
            MainBg.setBackgroundResource(R.drawable.background2);
        }
        else if (bgNumber == 3){
            MainBg.setBackgroundResource(R.drawable.background3);
        }

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }
}
