package com.example.jeon.diary;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Random;

public class oneToGame extends Activity {

    Button[] BtnArr = new Button[25];
    int [] BtnId = {
            R.id.btn1,R.id.btn2,R.id.btn3,R.id.btn4,R.id.btn5,
            R.id.btn6,R.id.btn7,R.id.btn8,R.id.btn9,R.id.btn10,
            R.id.btn11,R.id.btn12,R.id.btn13,R.id.btn14,R.id.btn15,
            R.id.btn16,R.id.btn17,R.id.btn18,R.id.btn19,R.id.btn20,
            R.id.btn21,R.id.btn22,R.id.btn23,R.id.btn24,R.id.btn25,
    };

    int [] arr= new int [25];
    int count = 1;
    boolean isStart = false;
    Chronometer chronometer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_to_game);

        gameDialog(); // 다이얼로그

        chronometer = (Chronometer) findViewById(R.id.chronometer);
        gameMain();  //초기 화면시 버튼들의 텍스트를 초기화 해주기 위한 메소드.

        Button Start = (Button)findViewById(R.id.startGame2);  // 시작 버튼
        Button reset = (Button)findViewById(R.id.resetArr);  // 배열 생성.

        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( isStart == false){
                    isStart = true;
                    setBtnArr();
                    count = 1;
                    chronometer.stop();
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                }
                else{
                    Toast.makeText(oneToGame.this, "리셋을 눌르세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visibleBtn();
                isStart = false;
                count = 1;
                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());

            }
        });
    }

    // 버튼 배열에 들어갈 텍스트 값인 랜덤 숫자를 바꾸는 메소드
    public void arrSet(){
        Random r  = new Random();
        for ( int i = 0 ; i < arr.length ; i++){
            arr[i] = r.nextInt(25)+1; //1~25숫자중 랜덤으로 하나를 뽑아서 저장
            for(int j=0;j<i;j++) //중복제거를 위한 for문
            {
                if(arr[i]==arr[j])
                {
                    i--;
                }
            }
        }
    }

    // 버튼 배열에 랜덤 값을 텍스트로 넣고  카운트 값과 비교하여, 같으면 그버튼을 숨기고, 카툰트 증가, 다르면, 진동
    public void setBtnArr(){
        arrSet();
        for ( int i = 0 ; i < BtnId.length ; i++ ){
            BtnArr[i] = (Button)findViewById(BtnId[i]); // 버튼 배열 순서대로 아이디 할당.
            BtnArr[i].setText(String.valueOf(arr[i]));  // 랜덤으로 생긴 값으로, 버튼이름 지정.

            final int ff = i;

            BtnArr[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(arr[ff]== count){
                        Toast.makeText(oneToGame.this, "good", Toast.LENGTH_SHORT).show();
                        Log.d("배열값 ",""+arr[ff]);
                        Log.d("카운트값",""+count);
                        BtnArr[ff].setVisibility(View.INVISIBLE);
                        count++;
                        if(count > 25){
                            chronometer.stop();
                            Vibrator v1 = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                            v1.vibrate(2000);
                        }
                    }
                    else{
                        Vibrator v1 = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                        v1.vibrate(400);
                    }

                }
            });
        }

    }

    // 리셋시 안보이게 되었던 버튼들을 다시 보여주는 메소드
    public void visibleBtn(){
        for ( int i= 0 ; i < 25; i++){
            BtnArr[i].setVisibility(View.VISIBLE);
            BtnArr[i].setText("?");
        }
    }

    //초기 화면시 버튼들의 텍스트를 초기화 해주기 위한 메소드.
    public void gameMain(){
        for( int i = 0; i < 25 ; i++) {
            BtnArr[i] = (Button)findViewById(BtnId[i]);
            BtnArr[i].setText("?");
        }
    }

    @Override
    public void onBackPressed() {
        chronometer.stop();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        chronometer.stop();
        super.onDestroy();
    }

    public void gameDialog(){
            AlertDialog.Builder bld = new AlertDialog.Builder(this);
            bld.setTitle("게임 설명");
            bld.setMessage("1부터 25까지의 버튼을 순서대로 빠르게 누르세요!");
            bld.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            bld.show();
    }

}
