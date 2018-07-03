package com.example.jeon.diary;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Diary extends Activity {


    String date;
    String title;
    String Dcontent;
    String recPath;
    String path; // 이미지 파일의 절대 경로.
    Uri uri;

    int rotate;

    int bgNumber;


    boolean nocamera = true; // 퍼즈에서 리스타트 갔을때 카메라에 들어갔을때는 다이얼로그가 뜨지 않도록 예외처리
    boolean noRec = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        changeBackground();
        requirePermission();

        date = getDay();  // 날짜 받아오기,  엑티비티 들어오자마자 날짜가 나오도록 하기위해 크리에이트에다가 설정.

        ImageView user_image = (ImageView) findViewById(R.id.user_image);
        Button saveD = (Button) findViewById(R.id.addData);
        Button gotoRec = (Button) findViewById(R.id.gotoRec);

        saveD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  // 저장하기 버튼을 누르면, 값을 리스트에 전달하면서, 화면전환.
                saveDiary();
            }
        });


        user_image.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });  // 이미지 선택시 나타나는 반응.

        gotoRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noRec= false;
                goToRec();
            }
        });

        startService(new Intent(this, BGM.class));
    }


    // << 녹음 >>
    public void goToRec() {

        Intent gotoRec = new Intent(this, record_Activity.class);
        startActivityForResult(gotoRec, 3333);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);


    }


    // <<버튼 >> 세이브 버튼  ( 결과 반환.)
    public void saveDiary() {   // 저장 버튼을 눌렀을때, ( 다이어리 리스트에 전달. )


        // 배경음악.
        setDiary(); // 제목과 내용 받아오기

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(Dcontent)) {
            Toast.makeText(this, "제목과 내용을 반드시 입력해 주세요.", Toast.LENGTH_SHORT).show();
        } else {
            DiaryContent dc = new DiaryContent(date, title, path, Dcontent, recPath);
            Intent backMain = new Intent();
            backMain.putExtra("DiaryData", dc);
            setResult(RESULT_OK, backMain);
            // Log.d("Diary Save btn wwwwwwww",""+title);
            Toast.makeText(Diary.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);


        }

    }

    // 다이어리 메인에서 날짜를 클릭시 인텐트로 날짜 정보를 받아옴.  다이어리컨텐츠객체에 날짜 저장.
    public String getDay() {
        String selectDate;
        int year = getIntent().getIntExtra("year", 1);
        int month = getIntent().getIntExtra("month", 1);
        int day = getIntent().getIntExtra("day", 1);
        TextView date = (TextView) findViewById(R.id.date);
        date.setText("<< " + year + " 년 " + month + " 월 " + day + " 일  >>");
        selectDate = year + "/" + month + "/" + day ;
        return selectDate;
    }

    // 다이어리컨탠츠 객체에 제목, 내용 저장
    public void setDiary() {

        EditText title1 = (EditText) findViewById(R.id.diaryTitle);
        EditText content = (EditText) findViewById(R.id.diary_content);

        String D_title = title1.getText().toString();
        String D_content = content.getText().toString();

        title = D_title;
        Dcontent = D_content;
    }


    // << 카메라 >>
    public void runCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            try {
                File photoFile = createImageFile();
                Uri photoUri = FileProvider.getUriForFile(this, "com.example.jeon.diary.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, 1111);
            //    overridePendingTransition(R.anim.anim_slide_in_bottom, R.anim.anim_slide_in_bottom);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
        }
        nocamera = false;
    }


    // << 갤러리 >>
    public void runGallery() {
        Toast.makeText(Diary.this, "갤러리 실행", Toast.LENGTH_SHORT).show();

        Intent gallery = new Intent(Intent.ACTION_PICK);  // 암시적 인텐트 사용.
        gallery.setType(MediaStore.Images.Media.CONTENT_TYPE);
        gallery.setType("image/*");
        gallery.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(gallery, 2222);
    //    overridePendingTransition(R.anim.anim_slide_in_bottom, R.anim.anim_slide_in_bottom);

        nocamera = false;
    }


    // <카메라 or 갤러리 >  카메라나 , 갤러리 불러오기 실행시 이미지를 불러와 화면에 띄움
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 1111: {   // 카메라 불러오기  이미지 캡쳐
                if (resultCode == RESULT_OK) {
                    ImageView iv = (ImageView)findViewById(R.id.user_image);

                    // 정면샷 =  90;
                    // 배경샷이 = 0;
                    rotate = GetExifOrientation(path);

                    // 이미지회전
                    if( rotate == 90){
                        Matrix mt = new Matrix();
                        mt.postRotate(rotate);
                        Bitmap image = BitmapFactory.decodeFile(path);
                        Bitmap image1 = Bitmap.createBitmap(image,0,0,image.getWidth(),image.getHeight(),mt,false);
                        iv.setImageBitmap(image1);
                    }
                    else{
                        uri = Uri.parse(path);
                        iv.setImageURI(uri);
                    }
                     galleryAddPic();

                    // 카메라의 절대 경로가 path 안에 들어있음.
                    // Log.d("After Camera wwwwwwww", "" + uri);
                    break;
                }
            } //  리퀘스트코드 1111( 카메라 ) 끝

            case 2222: {
                if (resultCode == Activity.RESULT_OK) {  // 갤러리
                    uri = data.getData();
                    path = getRealpath(uri);
                    ImageView iv = (ImageView) findViewById(R.id.user_image);

                    // 배경샷 == 90;
                    // 세로샷 == 0;
                    rotate = GetExifOrientation(path);
                    if(rotate == 90){
                        Matrix mt = new Matrix();
                        mt.postRotate(rotate);
                        Bitmap image = BitmapFactory.decodeFile(path);
                        Bitmap image1 = Bitmap.createBitmap(image,0,0,image.getWidth(),image.getHeight(),mt,false);
                        iv.setImageBitmap(image1);
                    }else{
                        iv.setImageURI(uri);
                    }

                    //Log.d("After Gallary wwwwwwww", ""+rotate);
                    break;
                }
            } // 리퀘스트스 코드 2222 ( 갤러리 끝 )

            case 3333: {  // 녹음후 결과 받아오기.
                noRec = false;
                try {
                    recPath = data.getStringExtra("RecPath");
                    if (recPath.equals("")) {
                        recPath = "NoRecData";
                    }
                } catch (Exception e) {
                }
                break;
            }
        }// 스위치 끝.

    }  //  온 엑티비티 리절트 끝


    // << 절대 경로 >> 갤러리에서 가져온 이미지.
    public String getRealpath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor c = managedQuery(uri, proj, null, null, null);
        int index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        c.moveToFirst();
        String path = c.getString(index);

        return path;
    }

    // << 파일 >> 이미지 파일로 생성하는 부분
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        // File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        path = image.getAbsolutePath();
        return image;
    }

    //  << 저장 >>사진 파일 저장
    private void galleryAddPic() {    // 찍은 사진 앨범에 저장
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

        Toast.makeText(this, "사진 저장", Toast.LENGTH_SHORT).show();
    }


    //  <<다이얼로그 >> 이미지를 누르면 다이얼로그가 등장하여서, 어플리케이션을 선택할수 있는 창을 만들어줌.
    public void selectImage() {
        AlertDialog.Builder cameraSelect = new AlertDialog.Builder(Diary.this);
        cameraSelect.setTitle("사진등록 방법");
        cameraSelect.setMessage("선택해 주십시오.");


// 인텐트로, 카메라를 실행할 것인지.
        cameraSelect.setNegativeButton("카메라", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                runCamera();
            }
        });

        // 인텐트로, 갤러리를 실행할 것인지.

        cameraSelect.setPositiveButton("갤러리", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                runGallery();
            }
        });
        cameraSelect.show();  // 다이얼로그 실행
    }

    // < 다이얼로그 >퍼즈상태에서 돌아왔을때, 다이얼로그
    public void returnActivity() {
        AlertDialog.Builder returnChoice = new AlertDialog.Builder(Diary.this);
        returnChoice.setTitle("선택");
        returnChoice.setMessage("작성중인 일기가 있습니다.\n계속 작성하시겠습니까?");

        // 작업을 진행하겠습니다.
        returnChoice.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Diary.this, "작업을 진행합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        returnChoice.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Diary.this, "메인으로 이동합니다.", Toast.LENGTH_SHORT).show();
                finish();  // no선택시 종료, 인텐트로 보내니까, 뒤로가기하면 다시 이화면이 열림.. 그래서 finish()
            }
        });
        returnChoice.show();

    }

    // <<다이얼로그 >> 자료를 저장하지 않고  뒤로가기를 눌렀을때, 다이얼로그가 나오게 된다.
    public void noSaveBackDialog() {
        AlertDialog.Builder setBack = new AlertDialog.Builder(Diary.this);

        setBack.setTitle("알림");
        setBack.setMessage("저장되지 않은 데이터가 있습니다. \n이 페이지를 벗어나시겠습니까?");

        setBack.setNegativeButton("Yes", new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

                //   overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_out_right);
            }
        });

        setBack.setPositiveButton("No", new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        setBack.show();
    }

    // <<  권한 설정  >>
    public void requirePermission() {
        String[] per = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        ArrayList<String> lper = new ArrayList<>();

        for (String pers : per) {
            if (ContextCompat.checkSelfPermission(this, pers) == PackageManager.PERMISSION_DENIED) {
                //권한이 헉 가 안됬을경우 요청할 권한을 모집하는 부분
                lper.add(pers);
            }
        }

        if (!lper.isEmpty()) {
            // 권한 요청 하는 부분
            ActivityCompat.requestPermissions(this, lper.toArray(new String[lper.size()]), 1);
        }

    }


    @Override  // 화면을 떠나면 음악 정지.
    protected void onPause() {
        stopService(new Intent(this, BGM.class));
        super.onPause();
        // 자료저장 없이 뒤로가기 누르면 다이얼로그 출력;
    }

    @Override  // 다시 돌아왔을경우, 화면을 유지 할 것인지,  메인으로 돌아갈 것인지 선택.
    protected void onRestart() {
        startService(new Intent(this, BGM.class));
        // 카메라에 들어갔을때는 다이얼로그가 뜨지 않도록 예외처리

        if (nocamera == true && noRec == true ) {
            returnActivity();
        }
        else {
            nocamera = true;
            noRec = true;
        }

        super.onRestart();
    }

    @Override
    public void onBackPressed() {
            noSaveBackDialog();
    }

    // 배경화면
    public void changeBackground(){

        LinearLayout LoginBg = (LinearLayout)findViewById(R.id.DiaryBg);
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

    // 이미지 회전수 구하기
    public synchronized static int GetExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            if (orientation != -1) {

                switch(orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;


                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        }
        return degree;
    }

}


