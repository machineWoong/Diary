package com.example.jeon.diary;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class setting extends Activity {
    int bgNumber = 0;
    Handler hd = new Handler();
    int countAd = 0;
    Thread th;

    String getPass;
    String comPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        changeBackground();

        // 고객센터 버튼
        Button developer = (Button) findViewById(R.id.developer);
        developer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                developerBtn();
            }
        });


        // 홈페이지 이동
        Button gotoWebBtn = (Button) findViewById(R.id.gotoWeb);
        gotoWebBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoWeb();
            }
        });


        // 비밀번호 변경
        Button setPassWord = (Button) findViewById(R.id.setPassword);
        setPassWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordChange();
            }
        });


        // 배경화면 변경
        Button setBackground = (Button) findViewById(R.id.setBackG);
        setBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoSetBackground = new Intent(setting.this, settingBackground.class);
                startActivityForResult(gotoSetBackground, 1111);
            }
        });
        startService(new Intent(this, BGM.class));

        // 탭 게임
        Button gotoTapGame = (Button) findViewById(R.id.gotoTapGame);
        gotoTapGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoGame = new Intent(setting.this, gameTapTap.class);
                startActivity(gotoGame);
            }
        });

        // 1 to 25 게임
        Button goto1To25 = (Button) findViewById(R.id.goto1to25);
        goto1To25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoGame = new Intent(setting.this, oneToGame.class);
                startActivity(gotoGame);
            }
        });

    }

    public void changeBackground() {

        LinearLayout SettingLinear = (LinearLayout) findViewById(R.id.SettingLinear);
        SharedPreferences BackGround = getSharedPreferences("BackGround", 0);
        bgNumber = BackGround.getInt("BG", 0);

        if (bgNumber == 0) {
            SettingLinear.setBackgroundResource(R.drawable.pink);
        } else if (bgNumber == 1) {
            SettingLinear.setBackgroundResource(R.drawable.background1);
        } else if (bgNumber == 2) {
            SettingLinear.setBackgroundResource(R.drawable.background2);
        } else if (bgNumber == 3) {
            SettingLinear.setBackgroundResource(R.drawable.background3);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1111) {  // 배경환경 변경
            if (resultCode == 1111) {
                bgNumber = data.getIntExtra("bgNumber", 0);

                SharedPreferences background = getSharedPreferences("BackGround", 0);
                SharedPreferences.Editor bE = background.edit();
                bE.clear();
                bE.putInt("BG", bgNumber);
                bE.commit();

                Toast.makeText(this, "테마가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                Log.d("배경테마", "" + bgNumber);
                changeBackground();
            } else if (resultCode == 2222) {
                bgNumber = data.getIntExtra("bgNumber", 0);

                SharedPreferences background = getSharedPreferences("BackGround", 0);
                SharedPreferences.Editor bE = background.edit();
                bE.clear();
                bE.putInt("BG", bgNumber);
                bE.commit();

                Toast.makeText(this, "테마 변경 취소.", Toast.LENGTH_SHORT).show();
                Log.d("배경테마취소", "" + bgNumber);
                changeBackground();

            }

        }
    }

    // << 버튼 , 다이얼로그 >>  고객센터,
    public void developerBtn() {
        AlertDialog.Builder devle = new AlertDialog.Builder(setting.this);
        devle.setTitle("고객센터");
        devle.setMessage("선택해 주십시오.");

        devle.setPositiveButton("전화", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String tel = "tel:1544000000000";
                startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
            }
        });

        devle.setNegativeButton("메일", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                try {
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"teamNova3Woongs@gmail.com"});
                    emailIntent.setType("text/html");
                    emailIntent.setPackage("com.google.android.gm");
                    if (emailIntent.resolveActivity(getPackageManager()) != null)
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

        devle.show();

    }

    // 홈페이지 이동
    public void gotoWeb() {
        Intent gotoWeb = new Intent(Intent.ACTION_VIEW, Uri.parse("http://naver.com/"));
        startActivity(gotoWeb);

    }

    @Override
    protected void onResume() {
        ad();
        super.onResume();
    }

    @Override
    protected void onPause() {
        stopService(new Intent(this, BGM.class));
        th.interrupt();
        super.onPause();
    }

    @Override
    protected void onRestart() {
        startService(new Intent(this, BGM.class));
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, BGM.class));
        th.interrupt();
        super.onDestroy();
    }

    // 비밀번호 확인
    public void passwordChange() {
        AlertDialog.Builder setPassDi = new AlertDialog.Builder(this);

        setPassDi.setTitle("비밀번호 변경");
        setPassDi.setMessage("기존 비밀번호를 입력해 주세요.");
        final EditText beforePass = new EditText(this);
        beforePass.setInputType(0x00000012);
        setPassDi.setView(beforePass);


        beforePass.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)}); //글자수 제한

        setPassDi.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String getCheckPass = beforePass.getText().toString();  // 기존비밀번호 입력.
                SharedPreferences sSave = getSharedPreferences("DiaryDataSave", 0);
                String realPassword = sSave.getString("passWord", "000000");

                if (getCheckPass.equals(realPassword)) {
                    changePassWord();
                } else {
                    Toast.makeText(setting.this, "비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show();
                    Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v1.vibrate(500);
                }

            }
        });

        setPassDi.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(setting.this, "변경 취소.", Toast.LENGTH_SHORT).show();
            }
        });

        setPassDi.show();
    }

    // 비밀번호 변경
    public void changePassWord() {


        AlertDialog.Builder setPassDi = new AlertDialog.Builder(this);

        setPassDi.setTitle("비밀번호 변경");
        setPassDi.setMessage("변경 할 비밀번호 입력");


        final EditText pass = new EditText(this);
        pass.setInputType(0x00000012);
        setPassDi.setView(pass);

        pass.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)}); //글자수 제한


        setPassDi.setPositiveButton("다음", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getPass = pass.getText().toString();  // 변경할 비밀번호를 받아서 저장.

                checkChangePass();

            }
        });

        setPassDi.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        setPassDi.show();

    }
    // 비밀번호 변경 확인
    public void checkChangePass(){
        AlertDialog.Builder setPassDi = new AlertDialog.Builder(this);

        setPassDi.setTitle("비밀번호 변경");
        setPassDi.setMessage("비밀번호 확인");


        final EditText pass = new EditText(this);
        pass.setInputType(0x00000012);
        setPassDi.setView(pass);

        pass.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)}); //글자수 제한


        setPassDi.setPositiveButton("변경", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                comPass = pass.getText().toString();  // 변경할 비밀번호를 받아서 저장.

                if( getPass.equals(comPass)){
                    SharedPreferences sSava = getSharedPreferences("DiaryDataSave", 0);
                    SharedPreferences.Editor sSEdit = sSava.edit();
                    sSEdit.putString("passWord", getPass);
                    Toast.makeText(setting.this, "변경 완료", Toast.LENGTH_SHORT).show();
                    sSEdit.commit();// 저장
                }

                else{
                    Toast.makeText(setting.this, "비밀번호 설정 오류 ( 동일 하지 않습니다 )", Toast.LENGTH_SHORT).show();
                    Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v1.vibrate(500);
                }


            }
        });

        setPassDi.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        setPassDi.show();
    }

    public void ad() {
        th = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {

                        hd.post(new Runnable() {
                            @Override
                            public void run() {
                                if (countAd == 0) {
                                    ImageView adImageView = (ImageView) findViewById(R.id.settingAd);
                                    adImageView.setImageResource(R.drawable.ad1);
                                    countAd++;
                                } else if (countAd == 1) {
                                    ImageView adImageView = (ImageView) findViewById(R.id.settingAd);
                                    adImageView.setImageResource(R.drawable.ad2);
                                    countAd++;
                                } else if (countAd == 2) {
                                    ImageView adImageView = (ImageView) findViewById(R.id.settingAd);
                                    adImageView.setImageResource(R.drawable.ad3);
                                    countAd++;
                                } else if (countAd == 3) {
                                    ImageView adImageView = (ImageView) findViewById(R.id.settingAd);
                                    adImageView.setImageResource(R.drawable.ad4);
                                    countAd = 0;
                                }
                            }

                        });
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }

            }
        });
        th.start();
    }
}
