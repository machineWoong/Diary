package com.example.jeon.diary;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class settingBackground extends Activity {

    int bgNumber  = 0;
    int beforeNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_background);
        getBeforeBgdata(); // 기존데이터 가지고옴

        Button bg1 = (Button)findViewById(R.id.choice1);
        Button bg2 = (Button)findViewById(R.id.choice2);
        Button bg3 = (Button)findViewById(R.id.choice3);
        Button bg4 = (Button)findViewById(R.id.choice4);

        final Button savaBg = (Button)findViewById(R.id.saveBg);
        final Button cancleChangeBg = (Button)findViewById(R.id.BgChangeCancel);


        bg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout backgroundL = (LinearLayout)findViewById(R.id.background);
                bgNumber = 0;
                backgroundL.setBackgroundResource(R.drawable.pink);
            }
        });
        bg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout backgroundL = (LinearLayout)findViewById(R.id.background);
                bgNumber = 1;
                backgroundL.setBackgroundResource(R.drawable.background1);
            }
        });
        bg3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout backgroundL = (LinearLayout)findViewById(R.id.background);
                bgNumber = 2;
                backgroundL.setBackgroundResource(R.drawable.background2);
            }
        });
        bg4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout backgroundL = (LinearLayout)findViewById(R.id.background);
                bgNumber = 3;
                backgroundL.setBackgroundResource(R.drawable.background3);
            }
        });


        // 저장버튼
        savaBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBackground();
            }
        });


        // 취소버튼
        cancleChangeBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BgChangeCancle();
            }
        });

    }

    public void saveBackground(){

        Intent newBg = new Intent();
        newBg.putExtra("bgNumber",bgNumber);
        setResult(1111,newBg);
        finish();


    }
    public void BgChangeCancle(){

        Intent cancelBg = new Intent();
        cancelBg.putExtra("bgNumber",beforeNumber);
        setResult(2222,cancelBg);
        finish();
    }

    public void getBeforeBgdata(){
        SharedPreferences BackGround = getSharedPreferences("BackGround",0);
        beforeNumber = BackGround.getInt("BG",0);
    }

    @Override
    public void onBackPressed() {

        Toast.makeText(this, "테마 변경 취소", Toast.LENGTH_SHORT).show();
        finish();

    }
}
