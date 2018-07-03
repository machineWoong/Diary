package com.example.jeon.diary;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by JEON on 2018-02-05.
 */

public class AlaramService extends Service {

    String b;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String a =intent.getStringExtra("message");
        b = a;

        Log.d("서비스 ",a);
        Toast.makeText(this, b, Toast.LENGTH_SHORT).show();
        // 진동
        Vibrator v1 = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        v1.vibrate(5000);

        noti();

        onDestroy();
        return START_REDELIVER_INTENT;
    }

    public void noti(){
        NotificationManager ntm = ( NotificationManager ) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(android.R.drawable.star_on);
        builder.setTicker("Hello My Friend");
        builder.setContentTitle("Hello My Friend");  //알람제목
        builder.setContentText(b); // 알람내용
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);
/*

        Notification.InboxStyle inboxStyle = new Notification.InboxStyle(builder);
        inboxStyle.addLine("한줄 한줄");
        inboxStyle.addLine("한땀 한땀");
        inboxStyle.addLine("고의 고의");
        inboxStyle.addLine("적어드립니다.");
        builder.setStyle(inboxStyle);
*/

        ntm.notify(0,builder.build());
    }
}
