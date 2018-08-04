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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
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

    private static final String URL_BASE = "http://203.116.214.78:2546/";
    private static final String URL_TRAIN = URL_BASE + "train/";
    private static final String URL_PREDICT = URL_BASE + "predict/";

    private static final String PREDICT_MODE_STOPPED = "PREDICT_MODE_STOPPED";
    private static final String PREDICT_MODE_FOREVER = "PREDICT_MODE_FOREVER";
    private static final String PREDICT_MODE_ONE_TIME = "PREDICT_MODE_ONE_TIME";

//    private static final int COUNT_SCAN_WIFI_DEFAULT = 3;
//    private int scanWifiCount = COUNT_SCAN_WIFI_DEFAULT;
//    private int scanWifiCountRun = 0;

    private WifiManager mainWifi;
    private List<ScanResult> results;
    private ArrayList<WifiDataNetwork> arrayList;
    private int size;
    private WifiListReceiver wifiReceiverTrain;
    private WifiListReceiver wifiReceiverPredict;
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1001;
    private String blkId;
    private OkHttpClient client;
    private String responseBody;
    private Context mainContext;
    private SeekBar seekBarScanWifiCount;
    private String userEmail = "";
    private String predictMode = PREDICT_MODE_STOPPED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainContext = this;
        mainWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReceiverTrain = new WifiListReceiver(URL_TRAIN);
        wifiReceiverPredict = new WifiListReceiver(URL_PREDICT);
        arrayList = new ArrayList<WifiDataNetwork>();
        client = new OkHttpClient();

//        seekBarScanWifiCount = (SeekBar) findViewById(R.id.seekBarScanWifiCount);
//        seekBarScanWifiCount.setProgress(COUNT_SCAN_WIFI_DEFAULT - 1);
//        seekBarScanWifiCount.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(getBaseContext(), "Scan WiFi count: " + String.valueOf(scanWifiCount), Toast.LENGTH_SHORT).show();
//            }
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                scanWifiCount = progress + 1;
//            }
//        });

        if (!mainWifi.isWifiEnabled()) {
            Toast.makeText(this, "wifi was not enabled", Toast.LENGTH_SHORT).show();
            mainWifi.setWifiEnabled(true);
        }
    }

    public void onSubmitTrain(View v) {
        runTrain();
    }

    public void onSubmitPredict(View v) {
        predictMode = PREDICT_MODE_ONE_TIME;
        runPredict();
    }

    public void onSubmitPredictForever(View V) {
        predictMode = PREDICT_MODE_FOREVER;
        runPredict();
    }

    public void onSubmitPredictStop(View v) {
        predictMode = PREDICT_MODE_STOPPED;
        Toast.makeText(this, "Predicting stopped...", Toast.LENGTH_SHORT).show();
    }

    private void runTrain() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
        }

        TextView blkIdView = (TextView) findViewById(R.id.blkId);
        blkId = blkIdView.getText().toString();

        if (blkId.equals("")) {
            Toast.makeText(this, "Please enter Office Block ID!", Toast.LENGTH_SHORT).show();
            return;
        }

        wifiReceiverTrain.setId(blkId);
        arrayList.clear();
        registerReceiver(wifiReceiverTrain, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        mainWifi.startScan();
        Toast.makeText(this, "Scanning WiFi ...", Toast.LENGTH_SHORT).show();
    }

    private void runPredict() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
        }

        TextView userEmailView = (TextView) findViewById(R.id.userEmail);
        userEmail = userEmailView.getText().toString();

        if (userEmail.equals("")) {
            Toast.makeText(this, "Please enter your email!", Toast.LENGTH_SHORT).show();
            return;
        }

        wifiReceiverPredict.setId(userEmail);
        arrayList.clear();
        registerReceiver(wifiReceiverPredict, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));


        mainWifi.startScan();
    }

    public void toastCallback(String info){
        final String infoStr = info;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mainContext, infoStr, Toast.LENGTH_SHORT).show();
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
        private String url;
        private String id = "";

        public WifiListReceiver(String url) {
            this.url = url;
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            if (predictMode.equals(PREDICT_MODE_STOPPED)) {
                return;
            }

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

            if (this.id.equals("")) {
                toastCallback("Please key in ID!");
            }

            String dataJSON = "";
            switch (this.url) {
                case URL_TRAIN:
                    dataJSON = new DataTrain(this.id, arrayList).getJSON();
                    break;
                case URL_PREDICT:
                    dataJSON = new DataPredict(this.id, arrayList).getJSON();
                    break;
                default:
                    dataJSON = new DataPredict(this.id, arrayList).getJSON();
                    break;
            }

            final String json = dataJSON;
            final String postUrl = new String(this.url);
            Thread networkThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    post(postUrl, json);
                }
            });

            if (predictMode.equals(PREDICT_MODE_STOPPED)) {
                return;
            }
            networkThread.start();
        }

        private void post(String url, String json) {
            RequestBody body = RequestBody.create(JSON, json);
            Log.v(TAG, "url: " + url);
            Log.v(TAG, "json: " + json);
            Request request = new Request.Builder().url(url).post(body).build();
            try {
                Response response = client.newCall(request).execute();
                responseBody = response.body().string();
                Log.v(TAG, "response: " + responseBody);
                toastCallback(responseBody);
                if (url.equals(URL_PREDICT) && predictMode.equals(PREDICT_MODE_FOREVER)) {
                    runPredict();
                }
            } catch (IOException e) {
                Log.v(TAG, e.toString());
                e.printStackTrace();
            }
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
