package com.example.jeon.diary;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by JEON on 2018-01-29.
 */

public class BGM extends Service {

    public MediaPlayer mp;
    public IBinder onBind(Intent arg0) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mp = MediaPlayer.create(this, R.raw.bgm);
        mp.setLooping(true); // 반복 재생 설정 (true와 false로 조정 가능)
        mp.start();
        return START_STICKY;
    }

    public void onDestroy() {
        Log.d("Example", "Service onDestroy()");
        super.onDestroy();
        mp.stop();
    }
}



