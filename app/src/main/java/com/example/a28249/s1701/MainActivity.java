package com.example.a28249.s1701;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.a28249.s1701.adapter.BlueAdapter;
import com.example.a28249.s1701.pojo.BluetoothDeviceWithRSSI;
import com.example.a28249.s1701.utils.BlueUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button scan, end, clear;

    //搜索结果List
    private ListView resultList;

    //蓝牙工具类
    private BlueUtils blueUtils;

    //蓝牙的Adapter
    private BlueAdapter blueAdapter;

    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        blueUtils = new BlueUtils();
        //初始化工具类
        blueUtils.getInitialization(this);
        //判断是否支持蓝牙
        if (blueUtils.isSupportBlue()) {
            //静默开启蓝牙
            blueUtils.getmBluetoothAdapter().enable();
        }

        //安卓6以上需要打开GPS，如果 API level 是大于等于 23(Android 6.0) 时
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //判断是否具有权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //判断是否需要向用户解释为什么需要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    Toast.makeText(this, "自Android 6.0开始需要打开位置权限才可以搜索到Ble设备", Toast.LENGTH_SHORT).show();
                }

                //请求权限
                ActivityCompat.requestPermissions(this, new String[]
                                {Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_CODE_ACCESS_COARSE_LOCATION);
            }
        }

        init();
    }

    private void init() {

        scan = (Button) findViewById(R.id.scan);
        end = (Button) findViewById(R.id.end);
        clear = (Button) findViewById(R.id.clear);
        resultList = (ListView) findViewById(R.id.result);

        scan.setOnClickListener(this);
        end.setOnClickListener(this);
        clear.setOnClickListener(this);

        blueUtils.setCallback(new BlueUtils.Callbacks() {
            @Override
            public void callbackList(List<BluetoothDeviceWithRSSI> mBlueList) {
                if (blueAdapter == null) {
                    blueAdapter = new BlueAdapter(mBlueList, MainActivity.this);
                    resultList.setAdapter(blueAdapter);
                } else {
                    blueAdapter.notifyDataSetChanged();
                    //实时刷新到底部
                    resultList.setSelection(mBlueList.size() - 1);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scan:
                blueUtils.startBlue();
                Toast.makeText(this, "开启成功", Toast.LENGTH_SHORT).show();
                break;

            case R.id.end:
                blueUtils.stopBlue();
                Toast.makeText(this, "关闭成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.clear:
                blueUtils.stopBlue();
                blueAdapter.clear();
                Toast.makeText(this, "清除成功", Toast.LENGTH_SHORT).show();
        }
    }
}
