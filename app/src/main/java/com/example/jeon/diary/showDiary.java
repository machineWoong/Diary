package com.example.jeon.diary;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class showDiary extends Activity {
    MediaPlayer mPlayer;

    DiaryContent Edata;
    DiaryContent NewData;
    String Newdate;
    String Newtitle;
    String Newcontent;
    String Newpath;
    String SaveRecPath;
    int bgNumber;
    int rotate;
    boolean change = false;
    boolean save = false;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_diary);

        changeBackground();
        getEditDate(); //객체를 받아온다.

        TextView date = (TextView) findViewById(R.id.showDdate);
        EditText title = (EditText) findViewById(R.id.showDtitle);
        EditText content = (EditText) findViewById(R.id.showDcontent);
        ImageView image = (ImageView) findViewById(R.id.showDimage);
        Button btn = (Button) findViewById(R.id.showDSave);


        ImageButton startRec = (ImageButton) findViewById(R.id.startRec);
        ImageButton stopRec = (ImageButton) findViewById(R.id.stopRec);

        date.setText(Edata.date);
        title.setText(Edata.title);
        content.setText(Edata.content);


        if (Edata.path == null) {
            image.setImageResource(R.drawable.nophoto);
        }
        try { // 이미지가 없을수도 있으니까
            rotate = GetExifOrientation(Edata.path);

            if (rotate == 90) {
                Matrix mt = new Matrix();
                mt.postRotate(90);
                Bitmap cimage = BitmapFactory.decodeFile(Edata.path);
                Bitmap image1 = Bitmap.createBitmap(cimage, 0, 0, cimage.getWidth(), cimage.getHeight(), mt, false);
                image.setImageBitmap(image1);
            } else {
                image.setImageURI(Uri.parse(Edata.path));
            }
            Log.d("rorororororo", "" + rotate);
        } catch (Exception e) {
        }


        //  << 저장 버튼 >>
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save = true;
                returnEditData();
            }
        });

        // << 재생 버튼 >>
        startRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    playRec();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // 정지버튼
        stopRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecPlay();
            }
        });

        image.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });  // 이미지 선택시 나타나는 반응.
    }

    // 재생
    public void playRec() throws Exception {

        if (mPlayer != null) {
            try {
                mPlayer.release();
            } catch (Exception e) {
            }
        }

        mPlayer = new MediaPlayer();
        mPlayer.setDataSource(SaveRecPath);
        mPlayer.prepare();
        mPlayer.start();

    }

    // 재생정지
    public void stopRecPlay() {
        if (mPlayer != null) {
            mPlayer.stop();
        }
    }

    // 리스트 -> 쇼 다이어리 데이터 전달.
    public void getEditDate() {
        Edata = (DiaryContent) getIntent().getSerializableExtra("editData");  // 받아온 데이터 저장.
        SaveRecPath = Edata.recPath;  // 기존 음성 녹음 데이터 저장.

        Log.d("show Diary Data rec WWW", "" + Edata.recPath);
        Log.d("show Diary Data imageWW", "" + Edata.path);

    }

    // 새롭게 객체를 만들어서 전달.
    public void returnEditData() {
        TextView date1 = (TextView) findViewById(R.id.showDdate);
        EditText title1 = (EditText) findViewById(R.id.showDtitle);
        EditText content1 = (EditText) findViewById(R.id.showDcontent);
        ImageView image1 = (ImageView) findViewById(R.id.showDimage);

        Newdate = date1.getText().toString();
        Newtitle = title1.getText().toString();
        Newcontent = content1.getText().toString();

        if (change == false) { // 이미지 수정시 change 값이  true  수정안할경우 default 값은 false로 분기문 실행.
            Newpath = Edata.path;
        }

        NewData = new DiaryContent(Newdate, Newtitle, Newpath, Newcontent, SaveRecPath);
        Intent gotoDList = new Intent();
        gotoDList.putExtra("editDiray", NewData);
        setResult(4321, gotoDList);
        save = false;
        change = false;
        try{
            mPlayer.stop();
            mPlayer.release();

        }catch (Exception e){

        }
        finish();
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);


    }

    @Override
    public void onBackPressed() {
        // 저장을 하고 뒤로가기를 누르면, 바뀐값으로 대체
        if (save == true) {
            returnEditData();
            Toast.makeText(this, "저장 완료", Toast.LENGTH_SHORT).show();
        }
        // 작성중 저장 안하고, 뒤로가면 원래 데이터를 불러옴.
        else if (save == false) {
            Intent gotoDList = new Intent();
            gotoDList.putExtra("editDiray", Edata);
            setResult(4321, gotoDList);
            Toast.makeText(this, "작업 취소", Toast.LENGTH_SHORT).show();
            try{
                mPlayer.stop();
                mPlayer.release();

            }catch (Exception e){

            }
            finish();
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

        }

    }
//---------------------------------------카메라 갤러리 관련--------------------------------------------

    //  <<다이얼로그 >> 이미지를 누르면 다이얼로그가 등장하여서, 어플리케이션을 선택할수 있는 창을 만들어줌.
    public void selectImage() {
        android.support.v7.app.AlertDialog.Builder cameraSelect = new android.support.v7.app.AlertDialog.Builder(showDiary.this);
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

    // << 카메라 >>
    public void runCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            try {
                File photoFile = createImageFile();
                Uri photoUri = FileProvider.getUriForFile(this, "com.example.jeon.diary.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, 1111);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
        }
    }

    // << 갤러리 >>
    public void runGallery() {
        Toast.makeText(showDiary.this, "갤러리 실행", Toast.LENGTH_SHORT).show();

        Intent gallery = new Intent(Intent.ACTION_PICK);  // 암시적 인텐트 사용.
        gallery.setType(MediaStore.Images.Media.CONTENT_TYPE);
        gallery.setType("image/*");
        gallery.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(gallery, 2222);

    }

    // <카메라 or 갤러리 >  카메라나 , 갤러리 불러오기 실행시 이미지를 불러와 화면에 띄움
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 1111: {   // 카메라 불러오기  이미지 캡쳐
                if (resultCode == RESULT_OK) {
                    ImageView iv = (ImageView) findViewById(R.id.showDimage);
                  /*  uri = Uri.parse(Newpath);
                    iv.setImageURI(uri);*/


                    // 이미지회전.
                    rotate = GetExifOrientation(Newpath);
                    if (rotate == 90) {
                        Matrix mt = new Matrix();
                        mt.postRotate(90);
                        Bitmap image = BitmapFactory.decodeFile(Newpath);
                        Bitmap image1 = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), mt, false);
                        iv.setImageBitmap(image1);
                    } else {
                        uri = Uri.parse(Newpath);
                        iv.setImageURI(uri);
                    }

                    galleryAddPic(); // 갤러리에 이미지 추가
                    change = true;
                    // 카메라의 절대 경로가 path 안에 들어있음.
                    Log.d("After Camera wwwwwwww", "" + Newpath);
                    break;
                }
            }

            case 2222: {
                if (resultCode == Activity.RESULT_OK) {
                    uri = data.getData();
                    Newpath = getRealpath(uri);
                    ImageView iv = (ImageView) findViewById(R.id.showDimage);

                    rotate = GetExifOrientation(Newpath);
                    if (rotate == 90) {
                        Matrix mt = new Matrix();
                        mt.postRotate(90);
                        Bitmap image = BitmapFactory.decodeFile(Newpath);
                        Bitmap image1 = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), mt, false);
                        iv.setImageBitmap(image1);
                    } else {
                        iv.setImageURI(uri);
                    }

                    change = true;
                    Log.d("After Gallary wwwwwwww", "" + Newpath);
                    break;
                }
            }
        }

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
        Newpath = image.getAbsolutePath();
        return image;
    }

    //  << 저장 >>사진 파일 저장
    private void galleryAddPic() {    // 찍은 사진 앨범에 저장
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(Newpath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

        Toast.makeText(this, "사진 저장", Toast.LENGTH_SHORT).show();
    }
//---------------------------------------카메라 갤러리 관련--------------------------------------------

    public void changeBackground() {

        LinearLayout ShowDiaryBg = (LinearLayout) findViewById(R.id.ShowDiaryBg);
        SharedPreferences BackGround = getSharedPreferences("BackGround", 0);
        bgNumber = BackGround.getInt("BG", 0);

        if (bgNumber == 0) {
            ShowDiaryBg.setBackgroundResource(R.drawable.pink);
        } else if (bgNumber == 1) {
            ShowDiaryBg.setBackgroundResource(R.drawable.background1);
        } else if (bgNumber == 2) {
            ShowDiaryBg.setBackgroundResource(R.drawable.background2);
        } else if (bgNumber == 3) {
            ShowDiaryBg.setBackgroundResource(R.drawable.background3);
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

                switch (orientation) {
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
