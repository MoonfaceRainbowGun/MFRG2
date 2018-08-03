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
import android.os.Bundle;
import android.os.Looper;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private String blkId;
    private OkHttpClient client;
    private String responseBody;
    private Context mainContext;
    private Toast httpToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainContext = this;
        wifiReceiver = new WifiListReceiver();
        mainWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        arrayList = new ArrayList<WifiDataNetwork>();
        client = new OkHttpClient();
        httpToast = new Toast(this);
        if (!mainWifi.isWifiEnabled()) {
            Toast.makeText(this, "wifi was not enabled", Toast.LENGTH_SHORT).show();
            mainWifi.setWifiEnabled(true);
        }

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                results = mainWifi.getScanResults();
                size = results.size();
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

    }

    public void onSubmit(View v) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
        }

        TextView blkIdView = (TextView) findViewById(R.id.blkId);
        blkId = blkIdView.getText().toString();

        if (blkId.equals("")) {
            Toast.makeText(this, "Please enter Office Block ID!", Toast.LENGTH_SHORT).show();
            return;
        }

        arrayList.clear();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mainWifi.startScan();
        Toast.makeText(this, "Scanning WiFi ...", Toast.LENGTH_SHORT).show();

        Log.v(TAG, "Wi-Fi Scan Results ... Count:" + size);
    }

    public void httpCallback(Context context, String info){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mainContext, responseBody, Toast.LENGTH_SHORT).show();
            }
        });
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

            for (WifiDataNetwork wifi: arrayList) {
                Log.v(TAG, "  SSID        = " + wifi.getSsid());
                Log.v(TAG, "  BSSID       = " + wifi.getBssid());
                Log.v(TAG, "  RSSI        = " + wifi.getRssi());
                Log.v(TAG, "---------------");
            }

            ReferencePoint rp = new ReferencePoint(blkId, arrayList);
            final String rpJSON = rp.getJSON();

            Thread networkThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    post(URL_TRAIN, rpJSON);
                }
            });

            networkThread.start();
        }

        private void post(String url, String json) {
            RequestBody body = RequestBody.create(JSON, json);
            Log.v(TAG, "request: " + json);
            Request request = new Request.Builder().url(url).post(body).build();
            try {
                Response response = client.newCall(request).execute();
                responseBody = response.body().string();
                Log.v(TAG, "response: " + responseBody);
                httpCallback(mainContext, responseBody);
            } catch (IOException e) {
                Log.v(TAG, e.toString());
                e.printStackTrace();
            }
        }
    }
}
