package com.example.jeon.diary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Login extends Activity {
    int bgNumber;
    int count = 1; // 비밀번호 시도 횟수
    @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        changeBackground();



        Button logBtn = (Button)findViewById(R.id.loginButton);
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText pass = (EditText)findViewById(R.id.passWord);
                String password = pass.getText().toString();

                SharedPreferences sSava = getSharedPreferences("DiaryDataSave", 0);
                String compere =sSava.getString("passWord","000000");

                if (password.equals(compere)){
                    gotoMain();
                }
                else{
                    errorPass();
                }

            }
        });
        Button forgatPass = ( Button )findViewById(R.id.forgat);
        forgatPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPass();
            }
        });
    }

    public void gotoMain(){
        Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show();
        Intent gotoMain = new Intent(Login.this,MainDiary.class);
        startActivity(gotoMain);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

       // overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        finish();  // 로그인 이후  메인화면에서 뒤로 가기 눌렀을시 다시 로그인이 뜨지 않도록.
    }

    public void errorPass(){

        if ( count < 3){
            Toast.makeText(this, "PassWord Error - "+count, Toast.LENGTH_SHORT).show();
            Vibrator v1 = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
            v1.vibrate(500);
            count++;
        }
        else{
            Toast.makeText(this, " 횟수 초과 ", Toast.LENGTH_SHORT).show();
            count = 1;
            finish();
        }

    }  // 비밀번호 틀린 횟수를 정하여, 초과시 어플 종료

    public void findPass(){
        {
            AlertDialog.Builder pass = new AlertDialog.Builder(Login.this);
            pass.setTitle("비밀번호 분실");
            pass.setMessage("고객센터에 비밀번호 초기화를 요구합니다.");

            pass.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);

                    try {
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"teamNova3Woongs@gmail.com"});
                        emailIntent.setType("text/html");
                        emailIntent.setPackage("com.google.android.gm");
                        if(emailIntent.resolveActivity(getPackageManager())!=null)
                            startActivity(emailIntent);
                        startActivity(emailIntent);
                    } catch (Exception e) {
                        e.printStackTrace();

                        emailIntent.setType("text/html");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"teamNova3Woongs@gmail.com"});

                        startActivity(Intent.createChooser(emailIntent, "Send Email"));
                    }
                }
            });

            pass.show();
        }
    }  // 고객센터 이메일과 연동
    @Override
    protected void onPause() {
        stopService(new Intent(this, BGM.class));
        super.onPause();
    }


    public void changeBackground(){

        LinearLayout LoginBg = (LinearLayout)findViewById(R.id.LoginBg);
        SharedPreferences BackGround = getSharedPreferences("BackGround",0);
        bgNumber = BackGround.getInt("BG",0);

       ImageView iv = (ImageView)findViewById(R.id.loginImageView);
       Button login = (Button)findViewById(R.id.loginButton);
       Button forget = (Button)findViewById(R.id.forgat);

        if ( bgNumber == 0){
            LoginBg.setBackgroundResource(R.drawable.pink);
            iv.setImageResource(R.drawable.padlock3);
            login.setBackgroundResource(R.drawable.key3);
            forget.setBackgroundResource(R.drawable.search3);
        }
        else if ( bgNumber == 1){
            LoginBg.setBackgroundResource(R.drawable.background1);
            iv.setImageResource(R.drawable.padlock2);
            login.setBackgroundResource(R.drawable.key2);
            forget.setBackgroundResource(R.drawable.search2);
        }
        else if ( bgNumber == 2){
            LoginBg.setBackgroundResource(R.drawable.background2);
            iv.setImageResource(R.drawable.padlock);
            login.setBackgroundResource(R.drawable.key);
            forget.setBackgroundResource(R.drawable.search);
        }
        else if (bgNumber == 3){
            LoginBg.setBackgroundResource(R.drawable.background3);
            iv.setImageResource(R.drawable.padlock2);
            login.setBackgroundResource(R.drawable.key2);
            forget.setBackgroundResource(R.drawable.search2);
        }

    }

}
