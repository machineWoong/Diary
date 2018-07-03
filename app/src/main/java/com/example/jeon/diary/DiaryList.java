package com.example.jeon.diary;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class DiaryList extends Activity {


    ArrayList<DiaryContent> dcL;
    DiaryData dd;
    DiaryContent editD;
    int editPosition;
    int bgNumber;
    public int filtYear = 0; // 스피너로 받은 년 값
    public int filtMonth = 0;  // 스피너로 받은 월 값;
    int realPosition;
    ArrayList<DiaryContent> copy;

    boolean isFilterOn = false;
    boolean isFiltedItemRemove = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_list);
        startService(new Intent(this, BGM.class));  //배경음악 시작
        requirePermission();  //권한 허가
        getBgDataBase BGchange = new getBgDataBase();
        BGchange.execute(); // 배경 체인지


        // 리스트뷰 어댑터.
        setList();
        try {
            dd.notifyDataSetChanged();
        } catch (Exception e) {

        }
        // 년도별 스피너
        yearSpiner();

        // 월별 스피너
       // monthSpiner();
        filterBtn();
    }

    // 리스트뷰 어댑터 세팅.
    public void setList() {
        try {
            getD();
            ListView DL = (ListView) findViewById(R.id.Diary_ListView);
            dd = new DiaryData(DiaryList.this, dcL);
            DL.setAdapter(dd);
            dd.notifyDataSetChanged();
            // 리스트 아이템 롱클릭
            DL.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    longClickTesk(position);
                    return true;
                }
            });

            // 리스트 아이템 숏 클릭
            DL.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    shortClickTesk(position);
                }
            });

        } // Try 끝

        catch (Exception e) {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        } // Catch 끝
    }

    // 년도별 월별 스피너
    public void yearSpiner() {
        // 년도 별 스피너
        Spinner yearSpinner = (Spinner) findViewById(R.id.yearFilter);
        ArrayAdapter yearAdapter = ArrayAdapter.createFromResource(this, R.array.yearFilter, android.R.layout.simple_spinner_item);
        yearSpinner.setAdapter(yearAdapter);
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ( position == 0){

                    // 년도를 선택을 안할 경우,  스피너 비활성화.
                    Spinner monthSpinner = (Spinner) findViewById(R.id.monthFilter);
                    ArrayAdapter monthAdapter = ArrayAdapter.createFromResource(DiaryList.this, R.array.monthFilter, android.R.layout.simple_spinner_item);
                    monthSpinner.setAdapter(monthAdapter);
                    monthSpinner.setEnabled(false);
                }
                if (position == 1) {
                    filtYear = 2017;
                    monthSpiner();
                }
                if (position == 2) {
                    filtYear = 2018;
                    monthSpiner();
                }
                if (position == 3) {
                    filtYear = 2019;
                    monthSpiner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void monthSpiner() {

        // 월별 스피너
        Spinner monthSpinner = (Spinner) findViewById(R.id.monthFilter);
        ArrayAdapter monthAdapter = ArrayAdapter.createFromResource(this, R.array.monthFilter, android.R.layout.simple_spinner_item);
        monthSpinner.setAdapter(monthAdapter);
        monthSpinner.setEnabled(true); //비활성화된 스피너 ( 년도 선택후 다시 활성화 )
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtMonth = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void filterBtn() {
        /*
        모두 보기인 경우에는 원본 데이터를 보내서 보여주고  수정 삭제도 바로바로 가능하다.
        하지만 년과 월이 정해지는경우 복사본에 년과 월에 알맞은 데이터를 찾아 복사를 해놓고
        수정 삭제시  탐색을 통해서  원본데이터의 인덱스 값을 가지고와서 원본에서 수정 삭제를 한다.
        */
        Button filterButton = (Button) findViewById(R.id.filterButton);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(DiaryList.this, "년"+filtYear+"월"+filtMonth, Toast.LENGTH_SHORT).show();
                if (filtYear == 0 && filtMonth == 0) {  // 전체출력
                    getD();
                    ListView DL = (ListView) findViewById(R.id.Diary_ListView);
                    dd = new DiaryData(DiaryList.this, dcL);
                    DL.setAdapter(dd);
                    dd.notifyDataSetChanged();
                }

                if (filtYear != 0 && filtMonth == 0) {  // 년도별 출력
                    copy = new ArrayList<DiaryContent>();
                    for (int i = 0; i < dcL.size(); i++) {
                        String[] setD = dcL.get(i).date.split("/");
                        int dataYear = Integer.parseInt(setD[0]);

                        if (filtYear == dataYear) {
                            copy.add(dcL.get(i));
                        }
                    }
                    ListView DL = (ListView) findViewById(R.id.Diary_ListView);
                    dd = new DiaryData(DiaryList.this, copy);
                    DL.setAdapter(dd);
                    dd.notifyDataSetChanged();
                    // 리스트 아이템 롱클릭
                    DL.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                            findPosition(position);
                            longClickTesk(realPosition);
                            dd.notifyDataSetChanged();
                            return true;
                        }
                    });

                    // 리스트 아이템 숏 클릭
                    DL.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            findPosition(position);
                            shortClickTesk(realPosition);
                        }
                    });
                }

                if (filtYear != 0 && filtMonth != 0) { // 년도 별 월별 출력
                    copy = new ArrayList<DiaryContent>();
                    for (int i = 0; i < dcL.size(); i++) {
                        String[] setD = dcL.get(i).date.split("/");
                        int dataYear = Integer.parseInt(setD[0]);
                        int dataMonth = Integer.parseInt(setD[1]);

                        if (filtYear == dataYear && filtMonth == dataMonth) {
                            copy.add(dcL.get(i));
                        }
                    }
                    ListView DL = (ListView) findViewById(R.id.Diary_ListView);
                    dd = new DiaryData(DiaryList.this, copy);
                    DL.setAdapter(dd);
                    dd.notifyDataSetChanged();

                    // 리스트 아이템 롱클릭
                    DL.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                            findPosition(position);
                            longClickTesk(realPosition);
                            dd.notifyDataSetChanged();

                            return true;
                        }
                    });

                    // 리스트 아이템 숏 클릭
                    DL.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            findPosition(position);
                            shortClickTesk(realPosition);

                        }
                    });
                }

                dd.notifyDataSetChanged();

                if ( filtYear != 0 && filtMonth != 0 ){
                    isFilterOn = true;
                }
            }
        });
    }

    // 복사본의 내용과 같은 원본 리스트의 인덱스 찾기
    public void findPosition(int position) {
        for (int i = 0; i < dcL.size(); i++) {
            if (copy.get(position).date == dcL.get(i).date && copy.get(position).title == dcL.get(i).title &&
                    copy.get(position).path == dcL.get(i).path && copy.get(position).recPath == dcL.get(i).recPath) {
                realPosition = i;
            }
        }
    }

    // << 롱클릭 리스너 >>  롱클릭시 다이얼로그로, 삭제요청
    public void longClickTesk(final int position) {

        AlertDialog.Builder diarySelect = new AlertDialog.Builder(DiaryList.this);
        diarySelect.setTitle("삭제 하시겠습니까?");
        diarySelect.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dcL.remove(position);
                dd.notifyDataSetChanged();

                Intent gotoMain = new Intent();
                gotoMain.putExtra("deletPosition", dcL);
                setResult(1111, gotoMain);

                Toast.makeText(DiaryList.this, "삭제완료", Toast.LENGTH_SHORT).show();
                isFiltedItemRemove = true;

                try {
                    if ( isFilterOn && isFiltedItemRemove == true){
                        copy = new ArrayList<DiaryContent>();
                        for (int i = 0; i < dcL.size(); i++) {
                            String[] setD = dcL.get(i).date.split("/");
                            int dataYear = Integer.parseInt(setD[0]);
                            int dataMonth = Integer.parseInt(setD[1]);

                            if (filtYear == dataYear && filtMonth == dataMonth) {
                                copy.add(dcL.get(i));
                            }
                        }
                        ListView DL = (ListView) findViewById(R.id.Diary_ListView);
                        dd = new DiaryData(DiaryList.this, copy);
                        DL.setAdapter(dd);
                        dd.notifyDataSetChanged();
                        isFiltedItemRemove = false;
                    }
                }catch (Exception e){
                    dd.notifyDataSetChanged();
                    isFiltedItemRemove = false;
                }

            }
        });

        diarySelect.show();   // 실행

    }

    public void shortClickTesk(int position) {

        Intent gotoShowD = new Intent(DiaryList.this, showDiary.class);


        editD = dcL.get(position);  // 삭제 전 객체에 복사.
        dcL.remove(position);  // 삭제를 하고 새로 저장하려고,
        editPosition = position;  // 변경하는 곳의 인덱스 값을 저장.

        Log.d("ShortBtn wwwwwwww", "" + editD.path);

        gotoShowD.putExtra("editData", editD); //  키값과, 객체 전달.
        startActivityForResult(gotoShowD, 1234);
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);


        //    overridePendingTransition(R.anim.anim_slide_in_top, R.anim.anim_slide_in_top);
        dd.notifyDataSetChanged();
    }

    // 인텐트로 데이터를 가지고 옴.  ( Main - > DiaryList )
    public void getD() {
        Intent getDiary = getIntent();
        dcL = (ArrayList) getDiary.getSerializableExtra("Diary");
        Log.d("List data wwwwwww", dcL.get(0).title);
    }

    //권한 요청
    public void requirePermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest
                    .permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
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

    @Override
    public void onBackPressed() {

        try {
            Intent gotoMainFromList = new Intent();
            gotoMainFromList.putExtra("deletPosition", dcL);
            setResult(1111, gotoMainFromList);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

            Log.d("List backPressed wwwww", "" + dcL.get(0).path);

            Toast.makeText(this, "메인으로 이동합니다.", Toast.LENGTH_SHORT).show();
            stopService(new Intent(this, BGM.class));
        } catch (Exception e) {

        } finally {
            finish();
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);


        }

        finish();
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);


    }

    @Override
    protected void onPause() {
        stopService(new Intent(this, BGM.class));
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
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1234) {
            if (resultCode == 4321) {
                editD = (DiaryContent) data.getSerializableExtra("editDiray");
                dcL.add(editPosition, editD);  //  삭제한 인덱스 번호에 다시 저장.
                dd.notifyDataSetChanged();

            }
            dd.notifyDataSetChanged();

            if( isFilterOn == true){
                copy = new ArrayList<DiaryContent>();
                for (int i = 0; i < dcL.size(); i++) {
                    String[] setD = dcL.get(i).date.split("/");
                    int dataYear = Integer.parseInt(setD[0]);
                    int dataMonth = Integer.parseInt(setD[1]);

                    if (filtYear == dataYear && filtMonth == dataMonth) {
                        copy.add(dcL.get(i));
                    }
                }
                ListView DL = (ListView) findViewById(R.id.Diary_ListView);
                dd = new DiaryData(DiaryList.this, copy);
                DL.setAdapter(dd);
                dd.notifyDataSetChanged();
                isFilterOn = false;
            }


        }
    }

    // 배경 설정 쓰레드
    public class getBgDataBase extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... Void) {
            SharedPreferences BackGround = getSharedPreferences("BackGround", 0);
            bgNumber = BackGround.getInt("BG", 0);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            LinearLayout LoginBg = (LinearLayout) findViewById(R.id.DListBg);
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
    }

}
