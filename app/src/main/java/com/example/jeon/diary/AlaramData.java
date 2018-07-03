package com.example.jeon.diary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by JEON on 2018-01-31.
 */

public class AlaramData extends BaseAdapter {

    Context context;
    ArrayList<AlaramContent>  Aarr;

    public AlaramData (Context context , ArrayList<AlaramContent> Aarr){
        this.context = context;
        this.Aarr = Aarr;
    }

    @Override
    public int getCount() {
        return Aarr.size();
    }

    @Override
    public Object getItem(int position) {
        return Aarr.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if( convertView == null){
            LayoutInflater li =  LayoutInflater.from(context);
            convertView = li.inflate(R.layout.activity_alaram_list_list_view, null);
        }

            TextView time = ( TextView)convertView.findViewById(R.id.AlaramTime);
            time.setText("날짜: "+ (Aarr.get(position).month+1) +" 월 " + Aarr.get(position).day
                    + " 일 "+ Aarr.get(position).hour + " 시 " +  Aarr.get(position).min + " 분 "   );

            TextView title = (TextView)convertView.findViewById(R.id.AlaramTitle);
            title.setText("제목 : "+  Aarr.get(position).message);

           // ringAlaram(Aarr,position,context);


        return convertView;
    }


}
