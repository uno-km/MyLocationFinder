package com.example.mylocationfinder;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MyLocationFinderActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private static final String[] LOCATION_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_location_finder);

        // 위치 권한 체크
        if (checkLocationPermission()) {
            // 권한이 있을 경우 위치 정보 가져오기
            this.getLocation();
        } else {
            // 권한이 없을 경우 권한 요청
            ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    // 위치 권한 체크
    private boolean checkLocationPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), LOCATION_PERMISSIONS[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    // 위치 정보 가져오기
    private void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // GPS 사용 가능 여부 체크
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // 네트워크 사용 가능 여부 체크
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location location = null;

        // GPS나 네트워크가 사용 가능한 경우
        if (isGpsEnabled || isNetworkEnabled) {
            // 위치 정보 제공자 설정
            String provider = isGpsEnabled ? LocationManager.GPS_PROVIDER : LocationManager.NETWORK_PROVIDER;

            // 위치 정보 가져오기
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                location = locationManager.getLastKnownLocation(provider);
            } else {
                // 권한이 없는 경우 권한 요청
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }

        // 위치 정보가 null인 경우 Toast 메시지 출력
        if (location == null) {
            Toast.makeText(this, "위치 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            // 위치 정보 출력
            String msg = "위도: " + location.getLatitude() + "\n경도: " + location.getLongitude();
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    // 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 승인된 경우 위치 정보 가져오기
                getLocation();
            } else {
                // 권한이 거절된 경우 Toast 메시지 출력
                Toast.makeText(this, "위치 권한이 거절되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
