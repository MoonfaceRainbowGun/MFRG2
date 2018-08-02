package com.mfrg.liujq.wifisniffer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private String TAG = "SearchWifi";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String URL_BASE = "http://203.116.214.78:2546/";
    private String URL_TRAIN = URL_BASE + "train/";
    private String URL_PREDICT = URL_BASE + "predict/";

    private WifiManager mainWifi;
    private List<ScanResult> results;
    private ArrayList<WifiDataNetwork> arrayList;
    private int size;
    private WifiListReceiver wifiReceiver;
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1001;
    private int x;
    private int y;
    private OkHttpClient client;
    private String responseBody;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiReceiver = new WifiListReceiver();
        mainWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        arrayList = new ArrayList<WifiDataNetwork>();
        client = new OkHttpClient();
        if (!mainWifi.isWifiEnabled()) {
            Toast.makeText(this, "wifi was not enabled", Toast.LENGTH_SHORT).show();
            mainWifi.setWifiEnabled(true);
        }

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent)
            {
                results = mainWifi.getScanResults();
                size = results.size();
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    public void onSubmit(View v) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
        }

        arrayList.clear();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mainWifi.startScan();
        Toast.makeText(this, "Scanning WiFi ...", Toast.LENGTH_SHORT).show();

        TextView pointX = (TextView) findViewById(R.id.pointX);
        TextView pointY = (TextView) findViewById(R.id.pointY);
        x = Integer.parseInt(pointX.getText().toString());
        y = Integer.parseInt(pointY.getText().toString());

        Log.v(TAG, "Wi-Fi Scan Results ... Count:" + size);
        Toast.makeText(this, responseBody, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // TODO: What you want to do when it works or maybe .PERMISSION_DENIED if it works better
        }
    }



    class WifiListReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            results = mainWifi.getScanResults();
            Collections.sort(results, new Comparator<ScanResult>() {
                @Override
                public int compare(ScanResult scanResult, ScanResult scanResult2) {
                    //  return 1 if rhs should be before lhs
                    //  return -1 if lhs should be before rhs
                    //  return 0 otherwise
                    if (scanResult.level > scanResult2.level) {
                        return -1;
                    } else if (scanResult.level < scanResult2.level) {
                        return 1;
                    }
                    return 0;
                }
            });

            unregisterReceiver(this);

            for (ScanResult result : results) {
                String ssid = result.SSID;
                if (ssid.startsWith("Garena1")) {
                    WifiDataNetwork wifi = new WifiDataNetwork(result);
                    arrayList.add(wifi);
                }
            }
            size = arrayList.size();

            for(WifiDataNetwork wifi: arrayList) {
                Log.v(TAG, "  SSID        =" + wifi.getSsid());
                Log.v(TAG, "  BSSID       =" + wifi.getBssid());
                Log.v(TAG, "  Level       =" + wifi.getLevel());
                Log.v(TAG, "---------------");
            }

            ReferencePoint rp = new ReferencePoint(x, y, arrayList);
            String rpJSON = rp.getJSON();
            Log.v(TAG, "body: " + rpJSON);

            RequestBody body = RequestBody.create(JSON, rpJSON);
            Request request = new Request.Builder()
                    .url(URL_TRAIN)
                    .post(body)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                responseBody = response.body().toString();
                Log.v(TAG, "response: " + responseBody);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
