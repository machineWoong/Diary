package com.example.jeon.diary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by JEON on 2018-02-05.
 */

public class AlaramReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String a = intent.getStringExtra("message");

        Log.d("브로드캐스트 리시버",a);


        // 서비스 호출
        Intent gotoSurvice = new Intent(context, AlaramService.class);
        gotoSurvice.putExtra("message",a);
        context.startService(gotoSurvice);

    }
}
