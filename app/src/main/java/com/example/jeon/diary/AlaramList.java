package com.example.jeon.diary;

        import android.app.Activity;
        import android.app.AlarmManager;
        import android.app.AlertDialog;
        import android.app.PendingIntent;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.LinearLayout;
        import android.widget.ListView;
        import android.widget.Toast;

        import java.util.ArrayList;
        import java.util.Calendar;

public class AlaramList extends Activity {


    ArrayList<AlaramContent> dataA = new ArrayList<>();
    AlaramContent Nac;
    AlaramData ma;

    int bgNumber;

    int savePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alramlist);
        changeBackground();
        startService(new Intent(this, BGM.class));

        try {
            getDataBase(); // 데이터 베이스 로드
            Toast.makeText(this, "Data Load", Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            Toast.makeText(this, "No have Data", Toast.LENGTH_SHORT).show();
        }
        try{
            RingtheBall();
        }catch (Exception e){

        }

        //  알람을 설정 하러 이동 (( 인텐트 이동. ))
        Button gotoSet = (Button) findViewById(R.id.goToSetAlaram);
        gotoSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoSetAlram = new Intent(AlaramList.this, alramSetting.class);
                startActivityForResult(gotoSetAlram, 1111);
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });

        try {
            ListView lv = (ListView) findViewById(R.id.AlaramlistView);
            // 어뎁터를 상속한 클래스
            ma = new AlaramData(this, dataA);
            lv.setAdapter(ma);  // 어뎁터 세팅;

            // <<리스트 아이템 롱클릭 >>  삭제 다이얼로그
            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    longClickTesk(position);
                    return true;
                }
            });

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    savePosition = position;  // 삭제 위치 저장.

                    Intent gotoEditAlaram = new Intent(AlaramList.this, alramSetting.class);
                    AlaramContent Eac = dataA.get(position);
                    dataA.remove(position);
                    gotoEditAlaram.putExtra("EditData", Eac);

                    startActivityForResult(gotoEditAlaram, 2222);
                    ma.notifyDataSetChanged();
                }
            });

            ma.notifyDataSetChanged();
        } // try끝
        catch (Exception e) {
            Toast.makeText(this, "설정된 알람이 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 알람 설정.
    public void RingtheBall() {
        PendingIntent [] Parr = new PendingIntent[dataA.size()];
        for ( int k = 0 ; k < dataA.size() ;k++){

            // 시간 설정.
            Calendar cl = Calendar.getInstance();
            cl.set(dataA.get(k).year,dataA.get(k).month,dataA.get(k).day,dataA.get(k).hour,dataA.get(k).min,0);


            if( cl.getTimeInMillis() < System.currentTimeMillis()){
                // 설정된 알람시간이, 현재시간보다 과거이면, 아무것도하지 않는다.
            }
            else{
                //인텐트
                Intent i = new Intent(AlaramList.this, AlaramReceiver.class);
                i.putExtra("message",dataA.get(k).message);

                Log.d("보내기전 ",dataA.get(k).message);

                // 알람매니저와, 브로드캐스트 리시버 호출
                Parr[k] = PendingIntent.getBroadcast(AlaramList.this,k,i,PendingIntent.FLAG_UPDATE_CURRENT);
                // 팬딩인텐트의 두번째 같도록 이전과같이 0으로 주면, 오버라이트가 발생해서 알람이 새롭게 적용되는 것이 아니라 갱신이 되버린다.
                // 그래서 두번째 값에 k라는 유동적인값? 인덱스를 주면 각 각 울리게 된다.

                AlarmManager am =(AlarmManager)getSystemService(Context.ALARM_SERVICE);
                am.setExact(AlarmManager.RTC_WAKEUP,cl.getTimeInMillis(),Parr[k]);
            }

        }
    }

    // 알람 데이터 베이스
    public void setDataBase() { //데이터베이스 저장하기

        /*
        객체 안에 있는 데이터들을 하나의 String에 저장을 하는데, 구분자를 주었다. !
        이후에 데이터 베이스에서 가지고 오려고 할때, 구분자를 가지고 쪼개서 객체를 만든후
        어레이 리스트에 다시 저장하는 방식으로 하려고 한다.
        */


        SharedPreferences DB = getSharedPreferences("AlaramDataBase", 0);
        SharedPreferences.Editor DBE = DB.edit();
        DBE.clear();

        for (int i = 0; i < dataA.size(); i++) {
            String ID = "AlaramData" + i;

            String year = String.valueOf(dataA.get(i).year);
            String month = String.valueOf(dataA.get(i).month);
            String day = String.valueOf(dataA.get(i).day);
            String hour = String.valueOf(dataA.get(i).hour);
            String min = String.valueOf(dataA.get(i).min);
            String message = String.valueOf(dataA.get(i).message);

            String AlaramDataBase = year + "!" + month + "!" + day + "!" + hour + "!" + min + "!" + message;
            DBE.putString(ID, AlaramDataBase);
        }
        DBE.commit();

    }

    public void getDataBase() { // 데이터베이스 불러오기
        SharedPreferences DB = getSharedPreferences("AlaramDataBase", 0);
        int i = 0;
        while (true) {

            String ID = "AlaramData" + i;

            if (DB.contains(ID) == false) { // 데이터 베이스 안에 키값이 있없다면,
                break;
            }

            String[] Data;
            String getData;

            getData = DB.getString(ID, null);
            Data = getData.split("!");
            // 구분자 !를 기준으로  데이터를 쪼갠다.


            //쪼개진 데이터를 각각의 변수에 넣는다.
            int year = Integer.parseInt(Data[0]);
            int month = Integer.parseInt(Data[1]);
            int day = Integer.parseInt(Data[2]);
            int hour = Integer.parseInt(Data[3]);
            int min = Integer.parseInt(Data[4]);
            String message = Data[5];

            Log.d("알람데이터가 없는데 왜 생성?",getData);

            AlaramContent AlaramDataBaseC = new AlaramContent(year, month, day, hour, min, message);
            dataA.add(AlaramDataBaseC);
            i++;
        }
    }


    // << 롱클릭 리스너 >>  롱클릭시 다이얼로그로, 삭제요청
    public void longClickTesk(final int position) {

        AlertDialog.Builder diarySelect = new AlertDialog.Builder(AlaramList.this);
        diarySelect.setTitle("삭제");
        diarySelect.setMessage("알람을 삭제 하시겠습니까.?");

        diarySelect.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dataA.remove(position);
                Toast.makeText(AlaramList.this, "삭제완료", Toast.LENGTH_SHORT).show();
                ma.notifyDataSetChanged();
            }
        });


        diarySelect.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                savePosition = position;  // 삭제 위치 저장.
//
//                Intent gotoEditAlaram = new Intent(AlaramList.this, alramSetting.class);
//                AlaramContent Eac = dataA.get(position);
//                dataA.remove(position);
//                gotoEditAlaram.putExtra("EditData", Eac);
//
//                startActivityForResult(gotoEditAlaram, 2222);
//                ma.notifyDataSetChanged();
            }
        });

        diarySelect.show();   // 실행

    }

    // 인텐트를 보낸 이후에 결과값.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1111: {
                if (requestCode == RESULT_OK) ;
                try {  // 세이브 안하고 도중에 나가게되면 예외가 발생함으로 예외처리 해줌

                    Nac = (AlaramContent) data.getSerializableExtra("NewData");
                    dataA.add(Nac);
                    ma.notifyDataSetChanged();  // 갱신!!!!!!!!!!!!!!!!!!!!!!

                    Intent returnToMain = new Intent();
                    returnToMain.putExtra("AlaramData", dataA);
                    setResult(RESULT_OK, returnToMain);
                    setDataBase(); // 데이터 베이스 저장.
                    Toast.makeText(this, "저장 완료", Toast.LENGTH_SHORT).show();


                } catch (Exception e) {
                    Toast.makeText(this, "저장 취소.", Toast.LENGTH_SHORT).show();
                }

                break;

                // *********************** 값을 가져오는데에 성공함  ****************************

            }

            case 2222: {  // 수정
                try {
                    Nac = (AlaramContent) data.getSerializableExtra("NewData");
                    dataA.add(savePosition, Nac);
                    setDataBase();
                    ma.notifyDataSetChanged();
                } catch (Exception e) {

                }
                break;
            }
        }


    }

    @Override
    protected void onRestart() {
        startService(new Intent(this, BGM.class));
        RingtheBall();
        super.onRestart();
    }

    @Override
    protected void onPause() {
        stopService(new Intent(this, BGM.class));
        try{
            RingtheBall();
        }catch (Exception e){
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        stopService(new Intent(this, BGM.class));
        setDataBase();
        finish();
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);


    }

    public void changeBackground(){

        LinearLayout LoginBg = (LinearLayout)findViewById(R.id.AlaramListBg);
        SharedPreferences BackGround = getSharedPreferences("BackGround",0);
        bgNumber = BackGround.getInt("BG",0);


        if ( bgNumber == 0){
            LoginBg.setBackgroundResource(R.drawable.pink);
        }
        else if ( bgNumber == 1){
            LoginBg.setBackgroundResource(R.drawable.background1);
        }
        else if ( bgNumber == 2){
            LoginBg.setBackgroundResource(R.drawable.background2);
        }
        else if (bgNumber == 3){
            LoginBg.setBackgroundResource(R.drawable.background3);
        }

    }
}
