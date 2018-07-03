package com.example.jeon.diary;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class gameTapTap extends Activity {
    public int count = 10;
    private CountDownTimer countDownTimer;
    public int tapCount = 0;
    public TextView time;
    boolean isStart = false;
    boolean isrun = false;

    int bgNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_tap_tap);
        changeBackground();
        gameDialog();

        time = (TextView)findViewById(R.id.time);
        time.setText(String.valueOf(count));

        //게임 시작
        Button start = (Button) findViewById(R.id.startGame);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( isrun == false && count == 10){
                    countDownTimer();
                    countDownTimer.start();
                    isStart = true;
                    isrun = true;
                }
                else{
                    Toast.makeText(gameTapTap.this, "다시하기를 눌러주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

            // 탭 버튼
            final Button tapBtn = (Button) findViewById(R.id.tapBtn);
            tapBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( isStart == false ){
                        Toast.makeText(gameTapTap.this, "게임 시작을 눌러주세요 ", Toast.LENGTH_SHORT).show();
                    }
                    else if ( isStart == true){
                        tapCount++;
                        TextView taptap = (TextView) findViewById(R.id.tapCountText);
                        taptap.setText(String.valueOf(tapCount));
                    }
                }
            });

        //다시하기 버튼
        Button restart = (Button)findViewById(R.id.restart);
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( isrun == false){
                    count = 10;
                    time = (TextView)findViewById(R.id.time);
                    time.setText(String.valueOf(count));

                    tapCount = 0;
                    TextView taptap = (TextView) findViewById(R.id.tapCountText);
                    taptap.setText(String.valueOf(tapCount));

                    isStart = false;
                    isrun = false;
                }
                else{
                    countDownTimer.cancel();
                    count = 10;
                    time = (TextView)findViewById(R.id.time);
                    time.setText(String.valueOf(count));

                    tapCount = 0;
                    TextView taptap = (TextView) findViewById(R.id.tapCountText);
                    taptap.setText(String.valueOf(tapCount));

                    isStart = false;
                    isrun = false;
                }
            }
        });

    }

    public void countDownTimer() {
        countDownTimer = new CountDownTimer(11000, 1000) {
            public void onTick(long millisUntilFinished) {
                time.setText(String.valueOf(count));
                count--;
            }

            public void onFinish() {
                time.setText(String.valueOf("Finish ."));
                isStart = false;
                isrun = false;
            }
        };
    }

    @Override

    public void onDestroy() {
        super.onDestroy();
        try {
            countDownTimer.cancel();
        } catch (Exception e) {
        }
        countDownTimer = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            countDownTimer.cancel();
        } catch (Exception e) {
        }
        countDownTimer = null;
    }

    public void gameDialog(){
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setTitle("게임 설명");
        bld.setMessage("제한시간 이내에 몬스터 얼굴을 빠르게 눌러 기록을 세워보세요.");
        bld.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        bld.show();
    }


    public void changeBackground() {

        LinearLayout ShowDiaryBg = (LinearLayout) findViewById(R.id.taptapBg);
        SharedPreferences BackGround = getSharedPreferences("BackGround", 0);
        bgNumber = BackGround.getInt("BG", 0);

        if (bgNumber == 0) {
            ShowDiaryBg.setBackgroundResource(R.drawable.pink);
        } else if (bgNumber == 1) {
            ShowDiaryBg.setBackgroundResource(R.drawable.background1);
        } else if (bgNumber == 2) {
            ShowDiaryBg.setBackgroundResource(R.drawable.background2);
        } else if (bgNumber == 3) {
            ShowDiaryBg.setBackgroundResource(R.drawable.background3);
        }

    }
}
