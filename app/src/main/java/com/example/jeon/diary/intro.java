package com.example.jeon.diary;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

public class intro extends Activity {

    Handler hd = new Handler();
    int bgNumber;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        changeBackground();
        Progress();

        TextView tv = (TextView)findViewById(R.id.textView);
        ImageView imv = (ImageView)findViewById(R.id.imageView);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotation);
        tv.startAnimation(animation);
        imv.startAnimation(animation);

    }

    public void changeBackground() {

        RelativeLayout LoginBg = (RelativeLayout) findViewById(R.id.introBg);
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

    public void gotoMainActivity() {
        Thread introStart = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Intent goLogin = new Intent(intro.this, Login.class);
                startActivity(goLogin);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                //  Intent goMain =  new Intent ( intro.this , MainDiary.class);
                //  startActivity(goMain);
            }
        });
        introStart.start();
        finish();
    }

    public void Progress() {

        Thread progressBarTime = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    try {
                        Thread.sleep(30);
                        count++;
                    } catch (InterruptedException e) {
                        break;
                    }

                    hd.post(new Runnable() {
                        @Override
                        public void run() {
                            if ( count > 0 && count < 20){
                                TextView tv =(TextView)findViewById(R.id.introTextView);
                                tv.setText("로딩중...");
                            }

                            if ( count > 20 && count < 50){
                                TextView tv =(TextView)findViewById(R.id.introTextView);
                                tv.setText("데이터를 불러오는 중 ");
                            }

                            if ( count > 50 && count < 100){
                                TextView tv =(TextView)findViewById(R.id.introTextView);
                                tv.setText("업데이트 확인 중");
                            }

                            if( count >= 100){
                                TextView tv =(TextView)findViewById(R.id.introTextView);
                                tv.setText("로딩 완료");
                            }

                            ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
                            pb.setProgress(count);
                        }
                    });

                    if ( count >= 100){
                        gotoMainActivity();
                        break;
                    }
                }
            }
        });
        progressBarTime.start();
    }

}

