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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class LocationList extends Activity {

    int bgNumber;

    ArrayList<LocationContent> LoArr = new ArrayList<LocationContent>();
    LocationContent LoC;
    LocationData LoD;

    int getPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);

        changeBackground(); // 배경 설정

        //  데이터 베이스 로드
        try {
            LocationDataBaseLoad();
        } catch (Exception e) {
            Toast.makeText(this, "NoData", Toast.LENGTH_SHORT).show();
        }

        try {
            // 어댑터 세팅
            setLocationAdapterSet();
        } catch (Exception e) {

        }

        Button gotoLocationSet = (Button) findViewById(R.id.gotoLocationSet);
        gotoLocationSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoLocationSet = new Intent(LocationList.this, LocationSet.class);
                startActivityForResult(gotoLocationSet, 1111);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                LoD.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1111) {
            if (resultCode == 1111) { // 새로운 데이터
                String name = data.getStringExtra("LocationName");
                String memo = data.getStringExtra("LocationMemo");
                String loVal = data.getStringExtra("LocationValue");

                LoC = new LocationContent(name, memo, loVal);
                LoArr.add(LoC);
                LoD.notifyDataSetChanged();
            }
            LoD.notifyDataSetChanged();
        }

        if (requestCode == 2222) { // 수정 데이터.
            if (resultCode == 2222) {
                LoArr.remove(getPosition);
                String name = data.getStringExtra("LocationName");
                String memo = data.getStringExtra("LocationMemo");
                String loVal = data.getStringExtra("LocationValue");

                LoC = new LocationContent(name, memo, loVal);
                LoArr.add(LoC);
                LoD.notifyDataSetChanged();
            }
            LoD.notifyDataSetChanged();
        }

        if (requestCode == 3333) { // 취소 하고 돌아온경우.
            if (resultCode == 3333) {
                Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            }
            LoD.notifyDataSetChanged();
        }
        LoD.notifyDataSetChanged();
    }

    // 데이터 베이스 세이브
    public void LocationDataBaseSave() {

        SharedPreferences LocationDataBase = getSharedPreferences("LocationDataBase", 0);
        SharedPreferences.Editor LocationDBE = LocationDataBase.edit();
        LocationDBE.clear(); // DB 비우기

        for (int i = 0; i < LoArr.size(); i++) {
            String ID = "Data" + i;
            String total = LoArr.get(i).name + "@" + LoArr.get(i).memo + "@" + LoArr.get(i).locate;

            LocationDBE.putString(ID, total);
        }
        LocationDBE.commit();
    }

    // 데이터 베이스 로드
    public void LocationDataBaseLoad() {

        SharedPreferences LocationDataBase = getSharedPreferences("LocationDataBase", 0);

        int i = 0;
        while (true) {
            String ID = "Data" + i;
            if (LocationDataBase.contains(ID) == false) {
                break;
            }
            String[] splitData;
            String getTotalData;
            getTotalData = LocationDataBase.getString(ID, null);
            splitData = getTotalData.split("@");

            LoC = new LocationContent(splitData[0], splitData[1], splitData[2]);
            LoArr.add(LoC);

            i++;
        }

    }

    // 어댑터 설정  ( 이거 호출하기 이전에 SharedPreference 에서 데이터 불러오기 호출  필요)
    public void setLocationAdapterSet() {
        try {
            ListView LocationList = (ListView) findViewById(R.id.Locationlist);
            LoD = new LocationData(LocationList.this, LoArr);
            LocationList.setAdapter(LoD);


            // 아이템 보기
            LocationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    listItemClick(position);
                }
            });

            // 삭제 버튼.
            LocationList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    listItemLongClick(position);
                    return true;
                }
            });

            LoD.notifyDataSetChanged();

        } catch (Exception e) {

        }
    }


    //  숏클릭시 보기
    public void listItemClick(int position) {

        Intent showMapData = new Intent(LocationList.this, LocationSet.class);
        showMapData.putExtra("showName", LoArr.get(position).name);
        showMapData.putExtra("showMemo", LoArr.get(position).memo);
        showMapData.putExtra("showLocationValue", LoArr.get(position).locate);
        getPosition = position;
        startActivityForResult(showMapData, 2222);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        LoD.notifyDataSetChanged();

    }

    // 롱클릭시 삭제
    public void listItemLongClick(final int position) {
        AlertDialog.Builder Select = new AlertDialog.Builder(LocationList.this);
        Select.setTitle("삭제 하시겠습니까?");
        Select.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LoArr.remove(position);
                LoD.notifyDataSetChanged();

                Toast.makeText(LocationList.this, "삭제완료", Toast.LENGTH_SHORT).show();
            }
        });
        Select.show();   // 실행*/
    }


    // 배경 화면
    public void changeBackground() {

        LinearLayout LoginBg = (LinearLayout) findViewById(R.id.mapListActivity);
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


    @Override
    protected void onPause() {
        LocationDataBaseSave();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        LocationDataBaseSave();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        LocationDataBaseSave();
        finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }
}
