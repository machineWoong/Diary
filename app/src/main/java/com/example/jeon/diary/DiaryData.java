package com.example.jeon.diary;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by JEON on 2018-01-31.
 */

public class DiaryData extends BaseAdapter {

    Context context;
    ArrayList<DiaryContent> DCarr;
    PersonViewHolder viewHolder;
    AsyncTask<Void, Void, Bitmap> mtask;
    int rotate;  // 사진 회전 값.



    public DiaryData(Context context, ArrayList<DiaryContent> DCarr) {
        this.context = context;
        this.DCarr = DCarr;
    }

    @Override
    public int getCount() {
        return DCarr.size();
    }

    @Override
    public Object getItem(int position) {
        return DCarr.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        viewHolder = new PersonViewHolder();
        viewHolder.position = position;

        if (convertView == null) {
            LayoutInflater li = LayoutInflater.from(context);
            convertView = li.inflate(R.layout.activity_list__list_view, null);

            try {
                viewHolder.date = (TextView) convertView.findViewById(R.id.inflDate);
                viewHolder.title = (TextView) convertView.findViewById(R.id.inflTitle);
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.inflview);
            } catch (Exception e) {
            }
            convertView.setTag(viewHolder);
        }  // 컨버트 뷰가 비어있을 때만 한번 생성. 재활용을 하기위한 컨버트 뷰
        else {
            viewHolder = (PersonViewHolder) convertView.getTag();
        }
                showAll(position);
                return convertView;
    }


    public class PersonViewHolder {
        public ImageView icon;
        public TextView title;
        public TextView date;
        public int position;
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

    // 이미지 회전하고, 이미지 리사이징후 출력해주는 쓰레드
    public class ThumbnailTask extends AsyncTask<Void, Void, Bitmap> {
        String path;
        PersonViewHolder mHolder;
        int width;

        public ThumbnailTask(String path, PersonViewHolder holder, Context context) {
            this.path = path;
            mHolder = holder;
            width = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // mHolder.icon.setVisibility(View.INVISIBLE);
            mHolder.icon.setImageResource(R.drawable.nophoto);
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            Bitmap bm = null;
            try {
                if (path.equals("null")) {
                } else {
                    try {
                        rotate = GetExifOrientation(path);
                        Log.d("어댑터 로테이트 값 ", "" + rotate);
                    } catch (Exception e) {
                    }
                    Matrix mt = new Matrix();
                    mt.postRotate(rotate);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 20;
                    Bitmap orgImage = BitmapFactory.decodeFile(path, options);
                    bm = Bitmap.createBitmap(orgImage, 0, 0, orgImage.getWidth(), orgImage.getHeight(), mt, false);
                }
            } catch (Exception e) {
            }

            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bm) {

            mHolder.icon.setVisibility(View.VISIBLE);

            if (isCancelled()) {
                bm = null;
            }
            if (bm != null) {
                mHolder.icon.setImageBitmap(bm);

            } else {
               cancel(true);
            }
        }
    }

    public void showAll(int position) {

        viewHolder.date.setText("날짜  : " + DCarr.get(position).date);
        viewHolder.title.setText("제목  : " + DCarr.get(position).title);
        // 이미지의 메모리 용량을 줄여주기 위해서 디코딩 함.
        try {
            mtask = new ThumbnailTask(DCarr.get(position).path, viewHolder, context);
            mtask.execute();
        } catch (Exception e) {
        }

    }


}// 끝
