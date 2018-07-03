package com.example.jeon.diary;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import static android.location.LocationManager.GPS_PROVIDER;

public class LocationSet extends FragmentActivity implements OnMapReadyCallback {
    SupportMapFragment smf;
    LatLng getLng;
    LatLng userLocation;
    LatLng searchLoction;

    GoogleMap gm ;

    int bgNumber;
    boolean existMark = false;

    String locationName;  // 사용자가 입력한 장소명을 적을 것
    String memo;  // 사용자의 메모
    String latlngString; // 사용자가 지정한 맵의위치 ( 스트링으로 변환할 것이다. 이후 스필트를 통해서 다시 long 값으로 변환할 것임 );

    String savelocationName;   // 사용자가 새로운 데이터가 아닌 기존의 데이터를 가지왓을 경우
    String savememo;
    String savelatlngString;

    Double lat;  // 앞 ( 랏 )
    Double lng;  // 뒤 ( 랭 )

    Double userLat;   //현재 위치를 받아서  LatLng userLocation; 을 초기화 하여 사용 할 것이다.
    Double userLng;



    boolean ifGetData = false; // 데이터를 가져온것인지, 새로만드는 것인지 확인 여부
    boolean isChange = false; // 데이터가 수정되어있는지 여부

    boolean isOnline = false;  // 네트워크 켜져있는지 여부
    boolean gpsEnable = false; //GPS 기능이 켜져있는지 여부


    private LocationManager locationManager;
    private Location lastKnownLocation = null;


    //---------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_set);
        changeBackground();
        requirePermission(); // 권한 요청
        //----------------------------------------
        isConnectedInternet(this); // 네트워크 연결 여부확인
        isConnectedGPS(this); // GPS 연결 여부 확인
        howToUseMap(); // 설명 다이얼로그
        //----------------------------------------


        // 지도 불러 오는 프래그먼트 매니저
        smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.MapF);
        smf.getMapAsync(this);  // 반드시메인 쓰레드에서 호출 되어야 한다.

        try {
            getDataForShow();
            Toast.makeText(this, "Show data", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "New data", Toast.LENGTH_SHORT).show();
        }
        //----------------------------------------------------------------------------

        // --------------------------------------------------------- 유저위치

        Button MyGPS = (Button) findViewById(R.id.userLocationBtn);
        MyGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if( userLat != null){
                   userLocation = new LatLng(userLat, userLng);
                   gm.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
               }

               // 현재위치를 받은경우 현재위치로 이동하여 보여줌.



                isConnectedInternet(LocationSet.this); // 네트워크 연결 여부확인
                isConnectedGPS(LocationSet.this); // GPS 연결 여부 확인

               if ( gpsEnable == false || isOnline == false){
                   isOnGPS();
               }
            }
        });

        // ----------------------------------------------------------------------------

        Button save = (Button) findViewById(R.id.saveMap);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBtn();
            }
        });
        Button mapCancel = (Button) findViewById(R.id.cancelMap);
        mapCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelBtn();
            }
        });

        //---------------------------------------------------------------------------------

    }



    // 구글 place API 사용
    public void search(){
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                String name = (String) place.getName();
                searchLoction = place.getLatLng();
                gm.moveCamera(CameraUpdateFactory.newLatLngZoom(searchLoction, 16));
                gm.addMarker(new MarkerOptions().position(searchLoction).title(name).icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

            }

            @Override
            public void onError(Status status) {
            }
        });

    }






    // 맵이 호출 되면 아래의 메소드 안에 있는 작업을 수행
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gm = googleMap;
        getRecentLocation();  //  현재 위치 바로 가져와지는 구만~!?
        isConnectedGPS(LocationSet.this);
        search();  // 검색 API 호출

        if( ifGetData == true){
            // 데이터 가져오기 인경우 찍은 곳 보이기.
            getLocationForShow();
        } else if( gpsEnable == false){
            // 새로운 데이터를 만드려고 하거나, GPS 기능이 꺼져있는경우 디폴트위치 보여주기
            getLng = new LatLng(37.554816, 126.970180);
            gm.moveCamera(CameraUpdateFactory.newLatLngZoom(getLng, 15));
        }

        // 지도에 표시된 위치 찾기
        searchMark();

        // 지도 클릭 이벤트
        gm.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (existMark == false) {
                    getLng = latLng;
                    gm.addMarker(new MarkerOptions().position(latLng).title("선택"));
                    existMark = true;
                } else {
                    Toast.makeText(LocationSet.this, "이미 표시된 지역이 있습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 마커 클릭 이벤트
        gm.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                clickMarkEvent(marker);
                return false;
            }
        });

    }


    // 마커 위치 보기
    public void searchMark() {

        Button searchMarkLocation = (Button) findViewById(R.id.markLocationBtn);
        searchMarkLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (existMark == true) {
                    gm.moveCamera(CameraUpdateFactory.newLatLngZoom(getLng, 15));
                } else {
                    Toast.makeText(LocationSet.this, "표시한 위치가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 마커 선택시 다이얼로그 발생
    public void clickMarkEvent(final Marker marker) {
        AlertDialog.Builder clickMark = new AlertDialog.Builder(LocationSet.this);
        clickMark.setTitle("작업 선택");

        clickMark.setNegativeButton("마커 삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                marker.remove();  // 마커 삭제
                getLng = null;
                existMark = false;
            }
        });

        clickMark.show();
    }


    // ------------------------------------------  데이터 가지고 와서 세팅

    // 아이템 보기로 왔을때 ( 데이터를 가지고 온 것임 ) - 저장된 위치와 데이터를 배치해준다.
    public void getDataForShow() {
        Intent getD = getIntent();
        savelocationName = getD.getStringExtra("showName");  // 사용자가 입력한 장소명을 적을 것
        savememo = getD.getStringExtra("showMemo");  // 사용자의 메모
        savelatlngString = getD.getStringExtra("showLocationValue"); // 사용자가 지정한 맵의위치 ( 스트링으로 변환할 것이다. 이후 스필트를 통해서 다시 long 값으로 변환할 것임 );


        // 텍스트 설정
        EditText ElocationName = (EditText) findViewById(R.id.LocationNameSet);
        EditText Ememo = (EditText) findViewById(R.id.LocationMemoSet);
        ElocationName.setText(savelocationName);
        Ememo.setText(savememo);

        // 지도 좌표 설정
        String[] splitData;
        splitData = savelatlngString.split(",");


        // 가지고 온 데이터 특수 문자 제거
        String a = splitData[0].replace("lat/lng: (", "");  // 특수문자제거
        String b = splitData[1].replace(")", ""); // 특수문자 제거
        lat = Double.parseDouble(a);
        lng = Double.parseDouble(b);

        ifGetData = true;
    }

    // 아이템 위치 보기
    public void getLocationForShow() {
        // 기존 데이터 확인시 불러옴.
        try {
            if (ifGetData == true) {
                getLng = new LatLng(lat, lng);
                gm.addMarker(new MarkerOptions().position(getLng).title("선택"));
                gm.moveCamera(CameraUpdateFactory.newLatLngZoom(getLng, 15));
                existMark = true;
            }
        } catch (Exception e) {

        }
    }
    // --------------------------------------  데이터 변경 여부 확인

    // 데이터 수정여부 ( 위치명, 메모, 로케이션 비교 )
    public void checkChangeData() {

        try {
            latlngString = getLng.toString();
        } catch (Exception e) {
        }

        try {
            getEditTextData();
        } catch (Exception e) {

        }

        try {
            if (savelocationName.equals(locationName) && savememo.equals(memo) && savelatlngString.equals(latlngString)) {
                isChange = false;
            } else {
                isChange = true;
            }
        } catch (Exception e) {
        }

    }

    // 데이터 변경이 없는 경우  ( 취소와 그냥 확인시 사용 )
    public void noChangeData() {
        setResult(3333, null);
        ifGetData = false;
        isChange = false;
        finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }


    // ----------------------------------------- 버튼 이벤트 ----------------------
    // 저장버튼
    public void saveBtn() {
        checkChangeData();

        if (ifGetData == false) {  // 새로운 데이터 인경우
            try {
                latlngString = getLng.toString();
            } catch (Exception e) {
                Toast.makeText(this, "위치를 선택해 주세요.", Toast.LENGTH_SHORT).show();
            }
            getEditTextData();  // 장소명 메모 가져오기

            Intent returnToList = new Intent();
            returnToList.putExtra("LocationName", locationName);
            returnToList.putExtra("LocationMemo", memo);
            returnToList.putExtra("LocationValue", latlngString);
            setResult(1111, returnToList);
            Toast.makeText(this, "저장 완료", Toast.LENGTH_SHORT).show();
            ifGetData = false;
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }

        if (isChange == true && ifGetData == true) {  // 데이터가 변경된 경우.
            try {
                latlngString = getLng.toString();
            } catch (Exception e) {
                Toast.makeText(this, "위치를 선택해 주세요.", Toast.LENGTH_SHORT).show();
            }
            getEditTextData();  // 장소명 메모 가져오기

            Intent returnToList = new Intent();
            returnToList.putExtra("LocationName", locationName);
            returnToList.putExtra("LocationMemo", memo);
            returnToList.putExtra("LocationValue", latlngString);
            setResult(2222, returnToList);
            ifGetData = false;
            isChange = false;
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }

        if (isChange == false && ifGetData == true) {  //데이터 변동이 없는 경우
            noChangeData();
        }

    }

    //  저장 할 때 장소명 메모 가져오기
    public void getEditTextData() {
        EditText ElocationName = (EditText) findViewById(R.id.LocationNameSet);
        EditText Ememo = (EditText) findViewById(R.id.LocationMemoSet);

        try {
            locationName = ElocationName.getText().toString();
            memo = Ememo.getText().toString();
        } catch (Exception e) {
            Toast.makeText(this, "장소명과 메모를 입력해 주세요", Toast.LENGTH_SHORT).show();
        }
    }

    // 취소버튼
    public void cancelBtn() {   // 이후에 그냥보러 들어온 경우를 판단하는 분기를 가지고와서 데이터 세이브여부를 묻지 않겟다.

        if (ifGetData == false) {
            noSaveData();
        } else {
            noChangeData();
        }


    }

    // 취소또는 뒤로가시 시 다이얼로그
    public void noSaveData() {
        android.support.v7.app.AlertDialog.Builder setBack = new android.support.v7.app.AlertDialog.Builder(LocationSet.this);

        setBack.setTitle("알림");
        setBack.setMessage("저장되지 않은 데이터가 있습니다. \n이 페이지를 벗어나시겠습니까?");

        setBack.setNegativeButton("Yes", new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
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


    // ---------------------------------------------- 권한 연결 문제 ----------------

    // <<  권한 설정  >>
    public void requirePermission() {
        String[] per = new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        ArrayList<String> lper = new ArrayList<>();

        for (String pers : per) {
            if (ContextCompat.checkSelfPermission(this, pers) == PackageManager.PERMISSION_DENIED) {
                //권한이 허가가 안됬을경우 요청할 권한을 모집하는 부분
                lper.add(pers);
            }
        }

        if (!lper.isEmpty()) {
            // 권한 요청 하는 부분
            ActivityCompat.requestPermissions(this, lper.toArray(new String[lper.size()]), 1);
        }

    }

    // 네트워크 연결 상태 확인
    public void isConnectedInternet(Context context) {
        try {
            ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo.State wifi = conMan.getNetworkInfo(1).getState();
            if (wifi == NetworkInfo.State.CONNECTED) {
                isOnline = true;
            }

            NetworkInfo.State mobile = conMan.getNetworkInfo(0).getState();
            if (mobile == NetworkInfo.State.CONNECTED) {
                isOnline = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // GPS 연결 상태 확인
    public void isConnectedGPS(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(GPS_PROVIDER)) {
            gpsEnable = true;
        } else {
            gpsEnable = false;
        }
    }

    // ------------------------------------------ 기타 ------------------------------

    // << 다이얼로그 >>설명서
    public void howToUseMap() {
        if (ifGetData == false) {
            AlertDialog.Builder howtoUse = new AlertDialog.Builder(this);
            howtoUse.setTitle("설명");
            howtoUse.setMessage("표시하고 싶은 위치를 길게 누르면, 표시할 수 있습니다. \n\n표시를 삭제 하려면,\n표시 마크를 눌러서 삭제합니다.");
            howtoUse.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            howtoUse.show();
        }
    }

    // GPS 확인
    public void isOnGPS() {
        //GPS가 켜져있는지 체크
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("GPS 연결 확인");
        ab.setMessage("GPS가 연결되어 있지 않습니다. 연결 하시겠습니까?");
        ab.setPositiveButton("연결", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //GPS 설정화면으로 이동
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivity(intent);
            }
        });


        ab.setNegativeButton("거부", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gpsEnable = false;
            }
        });

        ab.show();
    }


    // 배경설정
    public void changeBackground() {

        LinearLayout LoginBg = (LinearLayout) findViewById(R.id.LocationSetActivity);
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
    public void onBackPressed() {
        if (ifGetData == false) {
            noSaveData();
        } else {
            noChangeData();
        }
    }


    // ------------------------------------------ 사용자 위치 가지고 오기 -------------

    public void getRecentLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(locationListener);    // Stop the update if it is in progress.

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            gm.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }

        if(  isOnline == true ){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, locationListener);  // 네트워크 로 받음
        }else  if ( gpsEnable == true){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);  // GPS로 받음
        }else {

        }
        // requestLocationUpdates를 통해서 위치정보 업데이트를 받는다.  이때 리스너를 등록해준다.
    }


    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            lastKnownLocation = location;

            Log.d("test", "onLocationChanged, lastKnownLocation:" + lastKnownLocation);
            Log.d("test", "onLocationChanged, location:" + location);

            userLat = lastKnownLocation.getLatitude();
            userLng = lastKnownLocation.getLongitude();


            Log.d("test", "onLocationChanged, location:" + userLat);
            Log.d("test", "onLocationChanged, location:" + userLng);

            if(userLng == null){
                Toast.makeText(LocationSet.this, "실패", Toast.LENGTH_SHORT).show();
            }else if ( userLng != null && ifGetData == false){
                userLocation = new LatLng(userLat, userLng);
                gm.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14));
            }

            locationManager.removeUpdates(this);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
             public void onProviderDisabled(String provider) {
            AlertDialog.Builder startGPS = new AlertDialog.Builder(LocationSet.this);
            startGPS.setTitle("알림");
            startGPS.setMessage("GPS 기능이 비활성화 상태입니다. \n기능을 활성화 하시겠습니까?");
            startGPS.setPositiveButton("수락", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(i);
                    gpsEnable= true;
                }
            });

            startGPS.setNegativeButton("거절", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            startGPS.show();
        }
    };



}
