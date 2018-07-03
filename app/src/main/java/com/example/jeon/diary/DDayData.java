package com.example.jeon.diary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;


public class DDayData extends BaseAdapter {

    Context context;
    ArrayList<DDayContent> Aarr;
    Calendar tc = Calendar.getInstance();
    Calendar dc = Calendar.getInstance();

    //오늘 날짜
    int tYear;
    int tMonth;
    int tDay;

    public DDayData(Context context, ArrayList<DDayContent> Aarr){
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
            convertView = li.inflate(R.layout.activity_dday_list_format, null);
        }

        TextView title = convertView.findViewById(R.id.listDtile);
        title.setText(Aarr.get(position).dTitle);

        getToday();


        //밀리초로 변환.
        dc.set(Aarr.get(position).dYear,Aarr.get(position).dMonth,Aarr.get(position).dDay);

        long TodayMil;
        long DdayMil;
        long ResultMil;
        TodayMil = tc.getTimeInMillis();  //오늘 날짜 밀리 초
        DdayMil = dc.getTimeInMillis(); // 디데이 날짜 밀리 초
        ResultMil = (DdayMil-TodayMil)/(24*60*60*1000); // 디데이 - 오늘날짜  =  결과를 일 단위로 바꿈

        int resultNumber=0;
        resultNumber=(int)ResultMil;

        if(resultNumber>0){
            TextView day = convertView.findViewById(R.id.listDday);
            day.setText(String.format("- %d", resultNumber));
        }
        else if(resultNumber < 0){
            int absR=Math.abs(resultNumber);
            TextView day = convertView.findViewById(R.id.listDday);
            day.setText(String.format("+ %d", absR));
        }
        else{
            TextView day = convertView.findViewById(R.id.listDday);
            day.setText("= Today");
        }

        return convertView;
    }

    // 현재 날짜 구하기
    public void getToday(){
        tYear = tc.get(Calendar.YEAR);
        tMonth = tc.get(Calendar.MONTH);
        tDay = tc.get(Calendar.DAY_OF_MONTH);
    }

}
