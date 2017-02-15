package com.gnepux.droidserver;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "xupeng";
    
    SimpleHttpServer shs;

    private TextView mTextView;

    private ImageView mImageView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.textview);
        WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wi = wm.getConnectionInfo();
        // 获取32位整型IP地址
        int ipAdd = wi.getIpAddress();
        // 把整型地址转换成“*.*.*.*”地址
        String ip = intToIp(ipAdd);
        mTextView.setText(ip);

        WebConfiguration webConfig = new WebConfiguration();
        webConfig.setPort(8088);
        webConfig.setMaxParallels(50);
        shs = new SimpleHttpServer(webConfig);
        shs.registerResourceHander(new ResourceInAssetHandler(this));
        shs.registerResourceHander(new UploadImageHandler(this) {
            @Override
            protected void onImageLoaded(String path) {
                showImage(path);
            }
        });
        shs.startAsync();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "server onDestroy: ");
        try {
            shs.stopAsync();
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }
        super.onDestroy();
    }

    private static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                + "." + (i >> 24 & 0xFF);
    }

    private void showImage(final String path) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView imageView = (ImageView) findViewById(R.id.imageview);
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                imageView.setImageBitmap(bitmap);
                Toast.makeText(MainActivity.this, "image received and show", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
