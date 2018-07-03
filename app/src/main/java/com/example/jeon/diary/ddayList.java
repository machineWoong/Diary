package com.example.jeon.diary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ddayList extends Activity {


    ArrayList<DDayContent> ddarr = new ArrayList<>();
    DDayData ddd;

    int bgNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dday_list);
        changeBackground(); // 배경화면
        getDDayDB();

        // 리스트뷰 어댑터 설정

        // 삭제를 위한 롱클릭.
        try {
            ListView lv = (ListView) findViewById(R.id.DdayListVIew);
            ddd = new DDayData(this, ddarr);
            lv.setAdapter(ddd);
            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    listItemLongClickEvent(position);
                    return true;

                }
            });

            ddd.notifyDataSetChanged();
        } catch (Exception e) {

        }

        Button gotoSetDDay = (Button) findViewById(R.id.setDdayBtn);
        gotoSetDDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoSetD = new Intent(ddayList.this, DDaytSetting.class);
                startActivityForResult(gotoSetD, 4444);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                ddd.notifyDataSetChanged();
            }
        });

        try {
            ddd.notifyDataSetChanged();
        }
        catch (Exception e){

        }

    }


    // 데이터 베이스 저장
    public void setDDayDB() {
        SharedPreferences Dday = getSharedPreferences("dday", 0);
        SharedPreferences.Editor DdayE = Dday.edit();
        DdayE.clear();

        for (int i = 0; i < ddarr.size(); i++) {
            String ID = "Data" + i;
            String DataBase = ddarr.get(i).dYear + "@" + ddarr.get(i).dMonth + "@" + ddarr.get(i).dDay + "@" + ddarr.get(i).dTitle;
            DdayE.putString(ID, DataBase);
        }

        DdayE.commit();
    }

    // 데이터 베이스 가져오기
    public void getDDayDB() {
        SharedPreferences Dday = getSharedPreferences("dday", 0);
        int i = 0;
        while (true) {

            String ID = "Data" + i;

            if (Dday.contains(ID) == false) { // 데이터 베이스 안에 키값이 있다면,
                break;
            }

            String getData;  // DB로부터 가져온 데이터
            getData = Dday.getString(ID, null);
            String[] Data; // 구분자로 나눈 데이터를 저장할 배열
            Data = getData.split("@"); // 구분자로 쪼개기

            int year = Integer.parseInt(Data[0]);
            int month = Integer.parseInt(Data[1]);
            int day = Integer.parseInt(Data[2]);
            String title = Data[3];


            // 객체로 만들기
            DDayContent getDdayData = new DDayContent(year, month, day, title);
            ddarr.add(getDdayData); // 리스트의 항목으로 담기
            i++;
        }
    }
    // 롱클릭  ( 삭제 )
    public void listItemLongClickEvent(final int position) {

        AlertDialog.Builder diarySelect = new AlertDialog.Builder(ddayList.this);
        diarySelect.setTitle("삭제 하시겠습니까?");
        diarySelect.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ddarr.remove(position);
                ddd.notifyDataSetChanged();

                Toast.makeText(ddayList.this, "삭제완료", Toast.LENGTH_SHORT).show();
            }
        });

        diarySelect.show();   // 실행
    }


    // 배경 변경
    public void changeBackground() {

        LinearLayout MainBg = (LinearLayout) findViewById(R.id.ddayList);
        SharedPreferences BackGround = getSharedPreferences("BackGround", 0);
        bgNumber = BackGround.getInt("BG", 0);
        if (bgNumber == 0) {
            MainBg.setBackgroundResource(R.drawable.pink);
        } else if (bgNumber == 1) {
            MainBg.setBackgroundResource(R.drawable.background1);
        } else if (bgNumber == 2) {
            MainBg.setBackgroundResource(R.drawable.background2);
        } else if (bgNumber == 3) {
            MainBg.setBackgroundResource(R.drawable.background3);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 4444) {
            if (resultCode == 1111) {
                int getYear = data.getIntExtra("dYear", 0);
                int getMonth = data.getIntExtra("dMonth", 0);
                ;
                int getDay = data.getIntExtra("dDay", 0);
                ;
                String getTitle = data.getStringExtra("dTitle");
                DDayContent newDDcontent = new DDayContent(getYear, getMonth, getDay, getTitle);
                ddarr.add(newDDcontent);
                ddd.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "디데이 설정이 취소되었습니다.", Toast.LENGTH_SHORT).show();
            }

        }

    }

    @Override
    protected void onPause() {
        try {
            setDDayDB();
        } catch (Exception e) {

        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        try {
            setDDayDB();
        } catch (Exception e) {

        }
        super.onDestroy();
    }
}
