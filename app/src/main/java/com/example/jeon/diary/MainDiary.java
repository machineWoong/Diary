package com.example.jeon.diary;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;


public class MainDiary extends Activity {

    boolean Dial = true;

    ArrayList<DiaryContent> dcL = new ArrayList<>();
    DiaryContent getDc;
    String promise;
    int bgNumber;
    Handler hd = new Handler();
    Thread adverThread;
    Boolean isAdver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_diary);

        setAdvertisement(); // 광고 쓰레드 시작
        changeBackground(); // 배경화면변경

        try {
            getDataBase(); // 데이터 베이스 불러오기
        } catch (NullPointerException e) {
        }

        try {
            getTaskDataBase();
        } catch (NullPointerException e) {

        }
        getMypromise(); // 나의다짐 데이터 베이스
        requirePermission();  // 권한 요구


        //리스트로 이동
        Button gotoList = (Button) findViewById(R.id.gotoList);
        gotoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoList();
            }
        });

        // 알람으로 이동
        Button gotoAlram = (Button) findViewById(R.id.gotoAlram);
        gotoAlram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoAlram();
            }
        });

        //설정으로 이동
        Button gotoSetting = (Button) findViewById(R.id.gotoSetting);
        gotoSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSetting();
            }
        });
        toDateData(); // 날짜전달 및 일기장으로 엑티비티 이동

        // 디데이로 이동
        Button gotoDDay = (Button)findViewById(R.id.gotoDDay);
        gotoDDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoDDay = new Intent(MainDiary.this,ddayList.class);
                startActivity(gotoDDay);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);


            }
        });

        // 지도로 이동
        Button gotoMapList = (Button)findViewById(R.id.gotoMapList);
        gotoMapList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoMapList = new Intent(MainDiary.this,LocationList.class);
                startActivity(gotoMapList);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });

        try{
            checkBoxEvent();  // 할일 체크박스
        }catch (Exception e){
        }
    }

    // 체크박스 선택시, 할일을 비워주고, 자동으로 다시 해제가 된다 .
    public void checkBoxEvent(){
        CheckBox cb1 = ( CheckBox)findViewById(R.id.checkbox1);
        CheckBox cb2 = ( CheckBox)findViewById(R.id.checkbox2);
        CheckBox cb3 = ( CheckBox)findViewById(R.id.checkbox3);

        cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ( isChecked == true){
                    EditText t1 = (EditText)findViewById(R.id.task1);
                    t1.setText(null);
                    CheckBox cb1 = ( CheckBox)findViewById(R.id.checkbox1);
                    cb1.setChecked(false);
                }
            }
        });


        cb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ( isChecked == true){
                    EditText t2 = (EditText)findViewById(R.id.task2);
                    t2.setText(null);
                    CheckBox cb2 = ( CheckBox)findViewById(R.id.checkbox2);
                    cb2.setChecked(false);
                }
            }
        });

        cb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ( isChecked == true){
                    EditText t3 = (EditText)findViewById(R.id.task3);
                    t3.setText(null);
                    CheckBox cb3 = ( CheckBox)findViewById(R.id.checkbox3);
                    cb3.setChecked(false);
                }
            }
        });
    }
    // 배경 변경
    public void changeBackground(){

        LinearLayout MainBg = (LinearLayout)findViewById(R.id.mainLinearLayout);
        SharedPreferences BackGround = getSharedPreferences("BackGround",0);
        bgNumber = BackGround.getInt("BG",0);
        Button gotoAlram = (Button) findViewById(R.id.gotoAlram);
        Button gotoList = (Button) findViewById(R.id.gotoList);
        Button gotoSetting = (Button) findViewById(R.id.gotoSetting);
        Button gotoDDay = (Button)findViewById(R.id.gotoDDay);
        Button gotoMapList = (Button)findViewById(R.id.gotoMapList);

        if ( bgNumber == 0){
            MainBg.setBackgroundResource(R.drawable.pink);
            gotoAlram.setBackgroundResource(R.drawable.alarm3);
            gotoList.setBackgroundResource(R.drawable.list3);
            gotoSetting.setBackgroundResource(R.drawable.settings3);
            gotoDDay.setBackgroundResource(R.drawable.dday3);
            gotoMapList.setBackgroundResource(R.drawable.map3);
        }
        else if ( bgNumber == 1){
            MainBg.setBackgroundResource(R.drawable.background1);
            gotoAlram.setBackgroundResource(R.drawable.alarm2);
            gotoList.setBackgroundResource(R.drawable.list2);
            gotoSetting.setBackgroundResource(R.drawable.settings2);
            gotoDDay.setBackgroundResource(R.drawable.dday2);
            gotoMapList.setBackgroundResource(R.drawable.map2);
        }
        else if ( bgNumber == 2){
            MainBg.setBackgroundResource(R.drawable.background2);
            gotoAlram.setBackgroundResource(R.drawable.alarm);
            gotoList.setBackgroundResource(R.drawable.list);
            gotoSetting.setBackgroundResource(R.drawable.settings);
            gotoDDay.setBackgroundResource(R.drawable.dday);
            gotoMapList.setBackgroundResource(R.drawable.map);
        }
        else if (bgNumber == 3){
            MainBg.setBackgroundResource(R.drawable.background3);
            gotoAlram.setBackgroundResource(R.drawable.alarm2);
            gotoList.setBackgroundResource(R.drawable.list2);
            gotoSetting.setBackgroundResource(R.drawable.settings2);
            gotoDDay.setBackgroundResource(R.drawable.dday2);
            gotoMapList.setBackgroundResource(R.drawable.map2);
        }

    }

    // 나의다짐 데이터 베이스
    public void setMypromise() {
        EditText prom = (EditText) findViewById(R.id.MyPromise);
        promise = prom.getText().toString();
        SharedPreferences promi = getSharedPreferences("MyPromise", 0);
        SharedPreferences.Editor pE = promi.edit();
        pE.clear();
        pE.putString("promise", promise);
        pE.commit();
    }
    public void getMypromise() {

        try{
            SharedPreferences promi = getSharedPreferences("MyPromise", 0);
            String val = promi.getString("promise", null);

            if (!TextUtils.isEmpty(val)) {
                promise = val;
                EditText my = (EditText) findViewById(R.id.MyPromise);
                my.setText(promise);
            }
        }catch (Exception e){

        }

    }


    // 일기장 데이터 베이스
    public void setDataBase() { //데이터베이스 저장하기

        /*
        객체 안에 있는 데이터들을 하나의 String에 저장을 하는데, 구분자를 주었다. @
        이후에 데이터 베이스에서 가지고 오려고 할때, 구분자를 가지고 쪼개서 객체를 만든후
        어레이 리스트에 다시 저장하는 방식으로 하려고 한다.
        */

        SharedPreferences DB = getSharedPreferences("DiaryDataBase", 0);
        SharedPreferences.Editor DBE = DB.edit();
        DBE.clear(); // DB 비우기

        for (int i = 0; i < dcL.size(); i++) {
            String ID = "Data" + i;

            String DiaryDataBase = dcL.get(i).date + "@"
                    + dcL.get(i).title + "@"
                    + dcL.get(i).path + "@"
                    + dcL.get(i).content+"@"
                    + dcL.get(i).recPath;   // 데이터 직렬화

          /*  Log.d("qqq 날짜", "" + dcL.get(i).date);
            Log.d("qqq 제목", "" + dcL.get(i).title);
            Log.d("qqq 경로", "" + dcL.get(i).path);
            Log.d("qqq 내용", "" + dcL.get(i).content);
            Log.d("full 이름",DiaryDataBase);*/

            DBE.putString(ID, DiaryDataBase);  // 데이터 담기
        }
        DBE.commit(); // DB 저장

    }
    public void getDataBase() { // 데이터베이스 불러오기

        SharedPreferences DB = getSharedPreferences("DiaryDataBase", 0);

        int i = 0;
        while (true) {

            String ID = "Data" + i;
            if (DB.contains(ID) == false) { // 데이터 베이스 안에 키값이 있다면,
                break;
            }
            String[] Data; // 구분자로 나눈 데이터를 저장할 배열
            String getData;  // DB로부터 가져온 데이터
            getData = DB.getString(ID, null);
            Data = getData.split("@"); // 구분자로 쪼개기
            DiaryContent DBDC = new DiaryContent(Data[0], Data[1], Data[2], Data[3],Data[4]);  // 객체로 만들기
            dcL.add(DBDC); // 리스트의 항목으로 담기
            i++;
        }
    }


    // 할일 데이터 베이스
    public void setTaskDataBase() {  // 데이터 베이스 저장
        SharedPreferences taskDataBase = getSharedPreferences("taskDB", 0);
        SharedPreferences.Editor taskE = taskDataBase.edit();
        taskE.clear();  // 비우고

        EditText t1 = (EditText)findViewById(R.id.task1);
        String t1t = t1.getText().toString();

        EditText t2 = (EditText)findViewById(R.id.task2);
        String t2t = t2.getText().toString();

        EditText t3 = (EditText)findViewById(R.id.task3);
        String t3t = t3.getText().toString();

        String taskData = t1t + "#" + t2t + "#" + t3t; // 한줄로 만들고
        Log.d("xxxxxxx",""+taskData);
        taskE.putString("TaskDataBaseList", taskData);
        taskE.commit();
    }

    public void getTaskDataBase() {
        SharedPreferences taskDataBase = getSharedPreferences("taskDB", 0);

        String[] Data; // 구분자로 나눈 데이터를 저장할 배열
        String getData;  // DB로부터 가져온 데이터

        getData = taskDataBase.getString("TaskDataBaseList", null);
        Data = getData.split("#"); // 구분자로 쪼개기

        Log.d("vvvvvvvvvvv",getData);
        String data1 ;
        String data2 ;
        String data3 ;

        try{
            data1 = Data[0];
            EditText t = (EditText)findViewById(R.id.task1);
            t.setText(data1);
        }catch (Exception e){
        }

        try{
            data2 = Data[1];
            EditText t2 = (EditText)findViewById(R.id.task2);
            t2.setText(data2);
        }catch (Exception e){
        }

        try{
            data3 = Data[2];
            EditText t3 = (EditText)findViewById(R.id.task3);
            t3.setText(data3);
        }catch (Exception e){
        }
    }

    //<< 버튼 >> 일기 목록 ( 모아보기로 이동 )
    public void gotoList() {
        Intent gotoList = new Intent(MainDiary.this, DiaryList.class);
        gotoList.putExtra("Diary", dcL);

        //Toast.makeText(this, "메인 - 일기목록 호출"+dcL.get(0).title, Toast.LENGTH_SHORT).show();
        startActivityForResult(gotoList, 1111);
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

    }

    // << 버튼 >>  알람페이지로 이동.
    public void gotoAlram() {
        Intent gotoAlram = new Intent(MainDiary.this, AlaramList.class);
        startActivity(gotoAlram);
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

    }

    // << 버튼 >>  설정페이지로 이동.
    public void gotoSetting() {
        Intent gotoSetting = new Intent(MainDiary.this, setting.class);
        startActivity(gotoSetting);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);


    }

    // <<인텐트 >> 달력 날짜 선택시 화면전환과 동시에 날짜를 전달하기 ((** 꼭 이렇게 할 필요가 있었나..? ))
    public void toDateData() {
        CalendarView cv = (CalendarView) findViewById(R.id.calendarView);
        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                Intent toDiary = new Intent(MainDiary.this, Diary.class);
                toDiary.putExtra("year", year);
                toDiary.putExtra("month", month + 1);
                toDiary.putExtra("day", dayOfMonth);
                startActivityForResult(toDiary, 0);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);


            }
        });
    }


    @Override // 일기장으로 부터 받아온 데이터를  arrayList로 저장 .
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 일기를 작성하고 값을 받아와서, 리스트에 저장.
        if (requestCode == 0) {

            try {  // 세이브 안하고 도중에 나가게되면 예외가 발생함으로 예외처리 해줌
                getDc = (DiaryContent) data.getSerializableExtra("DiaryData");
                dcL.add(getDc);


                Log.d("Main data wwwwwwww", "" + dcL.get(0).title);
                Log.d("Main data wwwwwwww", "" + dcL.get(0).recPath);

            } catch (Exception e) {
                Toast.makeText(this, "저장 취소.", Toast.LENGTH_SHORT).show();
            }
        }

        // 일기목록을 부르고, 다시저장.
        if (requestCode == 1111) {
            if (resultCode == 1111) {
                dcL = (ArrayList<DiaryContent>) data.getSerializableExtra("deletPosition");
            }
        }
    }


    //권한 요청
    public void requirePermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest
                    .permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, 1);
            }

        } else {
        }
    }

    //권한 요청
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    finish();
                }
                break;
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        changeBackground();
        if (Dial == true) {
            AlertDialog.Builder bld = new AlertDialog.Builder(this);
            bld.setTitle("알림.");
            bld.setMessage("날짜를 선택하면, 일기를 작성 할 수 있습니다.");

            bld.setNegativeButton("다시 보지 않기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Dial = false;
                }
            });

            bld.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Dial = true;
                }
            });
            bld.show();

        }
        startService(new Intent(this, BGM.class));
    }

    @Override
    protected void onPause() {
        stopService(new Intent(this, BGM.class));
        setTaskDataBase();
        setMypromise();
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, BGM.class));
        setDataBase(); // 일기장 디비 저장
        setTaskDataBase(); // 할일 디비 저장
        setMypromise(); // 다짐 디비 저장.
        adverThread.interrupt(); // 광고 쓰레드 종료
        super.onDestroy();
    }
    @Override
    protected void onRestart() {

        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();


            AlertDialog.Builder back = new AlertDialog.Builder(MainDiary.this);
            back.setTitle("알림");
            back.setMessage("종료 하시겠습니까?");

            back.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setDataBase(); // 데이터 베이스 저장
                    finish();
                }
            });

            back.setPositiveButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            back.show();


    }

    // 광고 쓰레드
    public void setAdvertisement(){

        adverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if ( isAdver == false){
                        isAdver = true;
                        try {
                            Thread.sleep(20000);  // 20초
                        } catch (InterruptedException e) {
                            break;
                        }
                        hd.post(new Runnable() {
                            @Override
                            public void run() {
                                dia();
                            }
                        });
                    }
                }
            }
        });
        adverThread.start();

    }
    // 광고 다이얼로그.
    public void dia() {

        Random r  = new Random();
        int i = r.nextInt(3)+1; // 1~ 3 까지.

        ImageView image = new ImageView(getApplicationContext());
        if ( i == 1){
            image.setImageResource(R.drawable.lineage);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent gotoWeb = new Intent(Intent.ACTION_VIEW, Uri.parse("http://naver.com/"));
                    startActivity(gotoWeb);
                }
            });
        }
        else if (i == 2){
            image.setImageResource(R.drawable.beer);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent gotoWeb = new Intent(Intent.ACTION_VIEW, Uri.parse("http://daum.com/"));
                    startActivity(gotoWeb);
                }
            });
        }
        else{
            image.setImageResource(R.drawable.sinraman);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent gotoWeb = new Intent(Intent.ACTION_VIEW, Uri.parse("http://google.com/"));
                    startActivity(gotoWeb);
                }
            });
        }


        AlertDialog.Builder cameraSelect = new AlertDialog.Builder(this);
        cameraSelect.setTitle("광고");
        cameraSelect.setView(image);
        cameraSelect.setPositiveButton("확인",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isAdver = false;
            }
        });
        cameraSelect.create().show();
    }
}