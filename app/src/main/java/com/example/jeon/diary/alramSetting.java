package com.example.jeon.diary;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class alramSetting extends Activity {

    int year1;
    int month1;
    int day1 ;
    int hour1 = -1;
    int min1 = -1;
    String message1;

    int bgNumber;

    AlaramContent Edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alram_setting);

        changeBackground();

        try {
            Edit = (AlaramContent) getIntent().getSerializableExtra("EditData");
            if (Edit.message != null) {
                EditText backUp = (EditText) findViewById(R.id.alaramMessage);
                backUp.setText(Edit.message);
            }
        } catch (Exception e) {
            Toast.makeText(this, "New Data", Toast.LENGTH_SHORT).show();
        }


        getDate();  //날짜정보 변수에 저장.
        Button saveAlaram = (Button) findViewById(R.id.alaramSave);
        saveAlaram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    saveBtn();
                } catch (Exception e) {

                    if (day1 == 0) {
                        Toast.makeText(alramSetting.this, "날짜를 선택해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                    else if (hour1 < 0 ) {
                        Toast.makeText(alramSetting.this, "시간을 선택해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                    else if (min1 < 0 ) {
                        Toast.makeText(alramSetting.this, "분을 선택해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    // << 인텐트 >> 세이브버튼시 이전 엑티비티에 결과값을 반환
    public void saveBtn() {
        getData();

        if ( hour1 > 23){
            Toast.makeText(this, "시간 범위를 초과했습니다.", Toast.LENGTH_SHORT).show();
        }
        else if(min1 > 59){
            Toast.makeText(this, "분 범위를 초과했습니다.", Toast.LENGTH_SHORT).show();
        }
        else if ( message1.equals("")){
            Toast.makeText(this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
        }
        else {
            AlaramContent ac = new AlaramContent(year1, month1, day1, hour1, min1, message1);
            // 인텐트에 키값을 key로 주고  객체를 넣는다 ?
            Intent gotoList = new Intent();
            gotoList.putExtra("NewData", ac);

            // 결과 돌려주기
            setResult(RESULT_OK, gotoList);
            finish();
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);


        }
    }

    // 변수에 시 분 메세지 저장.
    public void getData() {
        EditText hour = (EditText) findViewById(R.id.hour);
        EditText min = (EditText) findViewById(R.id.min);
        EditText message = (EditText) findViewById(R.id.alaramMessage);

        hour1 = Integer.parseInt(hour.getText().toString());  // 문자열을 숫자로 저장.
        min1 = Integer.parseInt(min.getText().toString());
        message1 = message.getText().toString();

    }

    // 날짜 정보 받아오기. ( 변수에 저장 )
    public void getDate() {

        CalendarView cv = (CalendarView) findViewById(R.id.alaramCalendar);
        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                year1 = year;
                month1 = month;
                day1 = dayOfMonth;
                Toast.makeText(alramSetting.this, year1 + " / " + (month1 + 1) + " / " + day1, Toast.LENGTH_SHORT).show();
            }
        });

    }

    // <<다이얼로그 >> 퍼즈상태에서 돌아왔을때, 다이얼로그
    public void returnActivity() {
        AlertDialog.Builder returnChoice = new AlertDialog.Builder(alramSetting.this);
        returnChoice.setTitle("선택");
        returnChoice.setMessage("작업 중인 화면이 있습니다.\n작업을 계속 진행하시겠습니까?");

        // 작업을 진행하겠습니다.
        returnChoice.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(alramSetting.this, "작업을 진행합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        returnChoice.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(alramSetting.this, "메인으로 이동합니다.", Toast.LENGTH_SHORT).show();
                finish();  // no선택시 종료, 인텐트로 보내니까, 뒤로가기하면 다시 이화면이 열림.. 그래서 finish()
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);


            }
        });
        returnChoice.show();

    }

    //  <<다이얼로그 >>  저장 없이 뒤로갈때
    public void noSaveBackDialog() {
        AlertDialog.Builder setBack = new AlertDialog.Builder(alramSetting.this);

        setBack.setTitle("알림");
        setBack.setMessage("작성중인 문서가 있습니다. \n저장하지 않으면 데이터가 사라집니다.\n이 페이지를 벗어나시겠습니까?");

        setBack.setNegativeButton("Yes", new Dialog.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                stopService(new Intent(alramSetting.this, BGM.class));
                finish();
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

            }
        });

        setBack.setPositiveButton("No", new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        setBack.show();
    }


    @Override
    protected void onRestart() {
        returnActivity();
        startService(new Intent(this, BGM.class));
        super.onRestart();
    }

    @Override
    protected void onPause() {
        stopService(new Intent(this, BGM.class));
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        noSaveBackDialog();
    }


    public void changeBackground() {

        LinearLayout LoginBg = (LinearLayout) findViewById(R.id.AlaramSettingBg);
        SharedPreferences BackGround = getSharedPreferences("BackGround", 0);
        bgNumber = BackGround.getInt("BG", 0);

        if (bgNumber == 0) {
            LoginBg.setBackgroundResource(R.drawable.pink);
        } else if (bgNumber == 1) {
            LoginBg.setBackgroundResource(R.drawable.background1);
        } else if (bgNumber == 2) {
            LoginBg.setBackgroundResource(R.drawable.background2);
        } else if (bgNumber == 3) {
            LoginBg.setBackgroundResource(R.drawable.background3);
        }

    }
}
