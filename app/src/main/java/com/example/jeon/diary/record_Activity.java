package com.example.jeon.diary;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;

public class record_Activity extends Activity {


    //rec버튼 1회 누르면 녹음시작, 2회누르면 녹음 정지
    boolean recOrStop = true;

    MediaRecorder mRecorder;
    MediaPlayer mPlayer;
    boolean mpstartState = false;

    String recPath;
    int bgNumber;

    Thread startRec;
    Thread recPlay;
    Handler recH = new Handler();
    int recProg = 0;
    int getRecProgVal = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_);
        requirePermission(); // 권한 확인 및 요청
        changeBackground(); // 배경 변경

        final ImageButton rec = (ImageButton) findViewById(R.id.recBtn);
        ImageButton start = (ImageButton) findViewById(R.id.start);
        ImageButton stop = (ImageButton) findViewById(R.id.stop);
        Button save = (Button) findViewById(R.id.recSave);
        Button cancle = (Button) findViewById(R.id.recCancle);




        //녹음 버튼
        rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recOrStop == true) {
                    recstart();
                    recProg = 0;
                    recProgressBar();
                    recOrStop = false;
                } else if (recOrStop == false) {
                    recStop();
                    startRec.interrupt();
                    TextView recTv = (TextView) findViewById(R.id.recText);
                    recTv.setText("녹음 완료");
                    recOrStop = true;
                }

            }
        });

        // 재생버튼
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    playRec();
                    recProg = 0;
                    startRecSeekBar();
                } catch (Exception e) {

                }
            }
        });

        // 정지버튼
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecPlay();
                recPlay.interrupt();
                SeekBar recSeek = (SeekBar) findViewById(R.id.startSeekBar);
                recSeek.setProgress(0);
                TextView ptv = (TextView) findViewById(R.id.playTextView);
                ptv.setText("중지됨.");
            }
        });

        // 저장 및 확인 버튼
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (recPath == null) {
                    setResult(3333);
                    finish();
                    overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);



                } else {
                    Intent putRecPath = new Intent();
                    putRecPath.putExtra("RecPath", recPath);
                    setResult(3333, putRecPath);
                    Toast.makeText(record_Activity.this, "파일 경로 저장", Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

                }
            }
        });

        // 취소버튼
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recPath != null) {
                    File delfile = new File(recPath);
                    delfile.delete();
                }
                setResult(3333);
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);


            }
        });

        try {
            seekBarControl();
        } catch (Exception e) {

        }
    }


    // 녹음시작
    public void recstart() {
        if (mRecorder != null) {
            mRecorder.release();
        }
        long timeint = System.currentTimeMillis();
        String time = Long.toString(timeint);


        // 경로지정.
        recPath = "/sdcard/Music/" + time + "recorded.mp4";
        File fileOut = new File(recPath);
        if (fileOut != null) {
            fileOut.delete();
        }

        mRecorder = new MediaRecorder();
        // 어떤 것으로 녹음 할것인가를 설정 ( 마이크 )
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 파이르이 타입 설정 ( 3gpp로 해야 용량도 작고 효율적인 녹음기
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        // 코덱 설정
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        // 녹화 시간 제한 ( 60 초 )
        mRecorder.setMaxDuration(60000);
        mRecorder.setOutputFile(recPath);

        try {
            mRecorder.prepare();
            mRecorder.start();

            Log.d("녹음 파일의 경로 ", recPath);
            Toast.makeText(record_Activity.this, "녹음 시작", Toast.LENGTH_SHORT).show();
            ImageButton start = (ImageButton) findViewById(R.id.start);
            ImageButton stop = (ImageButton) findViewById(R.id.stop);
            start.setEnabled(false);
            stop.setEnabled(false);
        } catch (Exception e) {

        }

    }

    // 녹음정지
    public void recStop() {
        if (mRecorder != null) {
            mRecorder.stop();
        }
        Toast.makeText(record_Activity.this, "녹음 종료", Toast.LENGTH_SHORT).show();
        ImageButton start = (ImageButton) findViewById(R.id.start);
        ImageButton stop = (ImageButton) findViewById(R.id.stop);
        start.setEnabled(true);
        stop.setEnabled(true);
    }

    // 재생

    public void playRec() throws Exception {

        if (mPlayer != null) {
            try {
                mPlayer.release();
                recPlay.interrupt();
                recProg = 0;
            } catch (Exception e) {
            }
        }

        mPlayer = new MediaPlayer();
        mPlayer.setDataSource(recPath);
        mPlayer.prepare();
        mPlayer.start();
        mpstartState = true;
    }

    // 재생정지
    public void stopRecPlay() {
        if (mPlayer != null) {
            mPlayer.stop();
        }
    }


    //권한 요청
    public void requirePermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest
                    .permission.RECORD_AUDIO)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.RECORD_AUDIO
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

    // 배경 변경
    public void changeBackground() {

        LinearLayout LoginBg = (LinearLayout) findViewById(R.id.RecBg);
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


    // 녹음 시작시 프로그래스바
    public void recProgressBar() {
        startRec = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        recProg++;
                        recH.post(new Runnable() {
                            @Override
                            public void run() {
                                if (recProg < 60) {
                                    TextView recTv = (TextView) findViewById(R.id.recText);
                                    recTv.setText("녹음중..." + "(" + recProg + "/ 60초 )");

                                    ProgressBar recPb = (ProgressBar) findViewById(R.id.recProgressBar);
                                    recPb.setProgress(recProg);
                                    getRecProgVal = recProg;
                                } else if (recProg >= 60) {
                                    TextView recTv = (TextView) findViewById(R.id.recText);
                                    recTv.setText("녹음 완료");

                                    ProgressBar recPb = (ProgressBar) findViewById(R.id.recProgressBar);
                                    recPb.setProgress(60);

                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        recProg = 0;
                        break;
                    }
                }

            }
        });

        startRec.start();

    }

    // 재생시 시크바
    public void startRecSeekBar() {

        recPlay = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {

                    try {
                        Thread.sleep(1000);
                        recProg++;
                        recH.post(new Runnable() {
                            @Override
                            public void run() {
                                SeekBar recSeek = (SeekBar) findViewById(R.id.startSeekBar);
                                recSeek.setMax(getRecProgVal);
                                TextView ptv = (TextView) findViewById(R.id.playTextView);
                                if (recProg <= getRecProgVal) {
                                    ptv.setText("미리 듣기..." + "(" + recProg + "/" + getRecProgVal + "초 )");
                                } else {
                                    ptv.setText("미리 듣기 완료");
                                }
                                recSeek.setProgress(recProg);
                            }
                        });
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        });

        recPlay.start();
    }

    // 재생 시크바 컨트롤
    public void seekBarControl() {
        SeekBar recSeek = (SeekBar) findViewById(R.id.startSeekBar);
        recSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                recProg = seekBar.getProgress();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                if (mpstartState == true) {
                    mPlayer.pause();
                    recPlay.interrupt();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPlayer.seekTo(recProg * 1000);
                mPlayer.start();
                recPlay.interrupt();
                startRecSeekBar();
            }
        });


    }

    @Override
    protected void onPause() {

        try {
            mPlayer.stop();
            mPlayer.release();
            startRec.interrupt();
            recPlay.interrupt();
        } catch (Exception e) {

        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        try {
            mPlayer.stop();
            mPlayer.release();
            startRec.interrupt();
            recPlay.interrupt();
        } catch (Exception e) {

        }
        super.onDestroy();
    }
}
