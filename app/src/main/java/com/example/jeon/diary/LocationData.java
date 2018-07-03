package com.example.jeon.diary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by JEON on 2018-02-25.
 */

public class LocationData extends BaseAdapter {

    Context context;
    ArrayList<LocationContent> arr;

    public LocationData(Context context, ArrayList<LocationContent> arr){
        this.context = context;
        this.arr = arr;
    }

    @Override
    public int getCount() {
        return arr.size();
    }

    @Override
    public Object getItem(int position) {
        return arr.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater li = LayoutInflater.from(context);
            convertView = li.inflate(R.layout.activity_location_item, null);

            try{
                TextView name =(TextView)convertView.findViewById(R.id.LocationNameItem);
                TextView memo =(TextView)convertView.findViewById(R.id.LocationMemoItem);
                name.setText(arr.get(position).name);
                memo.setText(arr.get(position).memo);
            }catch (Exception e){

            }


        }  // 컨버트 뷰가 비어있을 때만 한번 생성. 재활용을 하기위한 컨버트 뷰
        else {
        }
        return convertView;
    }
}
