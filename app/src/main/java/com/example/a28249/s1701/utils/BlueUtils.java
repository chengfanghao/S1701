package com.example.a28249.s1701.utils;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.a28249.s1701.Config;
import com.example.a28249.s1701.pojo.BluetoothDeviceWithRSSI;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class BlueUtils {
    //蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;
    //搜索状态的标示
    private boolean mScanning = true;
    //蓝牙适配器List
    private List<BluetoothDeviceWithRSSI> mBlueList;
    //上下文
    private Context context;
    //单例模式
    private static BlueUtils blueUtils;
    //蓝牙的回调地址
    private ScanCallback mLeScanCall;
    //扫描执行回调
    private Callbacks callback;

    private BluetoothLeScanner bluetoothLeScanner;

    /**
     * 返回蓝牙对象
     *
     * @return
     */
    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    //单例模式
    public static BlueUtils getBlueUtils() {
        if (blueUtils == null) {
            blueUtils = new BlueUtils();
        }
        return blueUtils;
    }


    /***
     * 初始化蓝牙的一些信息
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void getInitialization(final Context context) {

        this.context = context;

        //获取蓝牙管理器
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        //获取默认的蓝牙适配器
        mBluetoothAdapter = bluetoothManager.getAdapter();
        //初始化List
        mBlueList = new ArrayList<>();

        //实例化蓝牙回调
        mLeScanCall = new ScanCallback() {
            //蓝牙对象 蓝牙信号强度 以及蓝牙的广播包
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                BluetoothDevice bluetoothDevice = result.getDevice();
                //解析上报的数据
                byte[] bytes = result.getScanRecord().getBytes();

                if (((int) bytes[1] != -1) || ((int) bytes[2] != 1) || ((int) bytes[3] != 112) || ((int) bytes[4] != -19)) {
                    return;
                }

                //判断是否为同一信号
                if ((int) bytes[5] == Config.sequence) {
                    return;
                } else {
                    Config.sequence = (int) bytes[5];
                }

                //判断上报地址
                String curPosition;
                if ((int) (bytes[6]) == 10) {
                    curPosition = "A";
                } else {
                    curPosition = "B";
                }

                String hexStr = Config.bytes2HexString(bytes);

                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
                BluetoothDeviceWithRSSI bluetoothDeviceWithRSSI = new BluetoothDeviceWithRSSI
                        (bluetoothDevice, result.getRssi(), dateFormat.format(date), hexStr);

                if (bytes[0] == 10) {
                    bluetoothDeviceWithRSSI.setDesc(hexStr.substring(14, 20) + " rssi:" + (int) (bytes[10]));
                } else {
                    bluetoothDeviceWithRSSI.setDesc(hexStr.substring(14, 20) + " rssi:" + (int) (bytes[10]) + "    "
                            + hexStr.substring(22, 28) + " rssi:" + (int) (bytes[14]));
                }

                if (!Config.singletonMap.containsKey(bluetoothDevice.getAddress())) {
                    if (curPosition.equals("A")) {
                        bluetoothDeviceWithRSSI.setPositionStatus("A");
                        bluetoothDeviceWithRSSI.setJudgeDesc("准备进校");
                    } else {
                        bluetoothDeviceWithRSSI.setPositionStatus("B");
                        bluetoothDeviceWithRSSI.setJudgeDesc("准备离校");
                    }
                } else {
                    BluetoothDeviceWithRSSI singletonBluetooth = Config.singletonMap.get(bluetoothDevice.getAddress());
                    //如果上次状态为A
                    if (singletonBluetooth.getPositionStatus().equals("A")) {
                        if (curPosition.equals("A")) {
                            bluetoothDeviceWithRSSI.setPositionStatus("A");
                            bluetoothDeviceWithRSSI.setJudgeDesc("准备进校");
                        } else {
                            bluetoothDeviceWithRSSI.setPositionStatus("B");
                            bluetoothDeviceWithRSSI.setJudgeDesc("已经进校");
                        }
                    } else {
                        if (curPosition.equals("A")) {
                            bluetoothDeviceWithRSSI.setPositionStatus("A");
                            bluetoothDeviceWithRSSI.setJudgeDesc("已经离校");
                        } else {
                            bluetoothDeviceWithRSSI.setPositionStatus("B");
                            bluetoothDeviceWithRSSI.setJudgeDesc("准备离校");
                        }
                    }
                }
                //更新单例Map
                Config.singletonMap.put(bluetoothDevice.getAddress(), bluetoothDeviceWithRSSI);

                mBlueList.add(bluetoothDeviceWithRSSI);
                //接口回调,设置adapter或者通知数据变更
                callback.callbackList(mBlueList);
            }
        };
    }

    /**
     * 开启蓝牙扫描
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startBlue() {
        if (mScanning) {
            mScanning = false;
            //开始扫描并设置回调
            bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

            //设置过滤条件，减少系统开销。
            List<ScanFilter> scanFilters = new ArrayList<>();

            //从数据库获取学生所有的mac地址信息
            for (String macStr : Config.filterList) {
                ScanFilter.Builder builder = new ScanFilter.Builder();
                builder.setDeviceAddress(macStr);
                scanFilters.add(builder.build());
            }

            //设置蓝牙以低功耗形式进行扫描
            ScanSettings.Builder sBuilder = new ScanSettings.Builder();
            sBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);

            //开始扫描
            bluetoothLeScanner.startScan(scanFilters, sBuilder.build(), mLeScanCall);
            //bluetoothLeScanner.startScan(mLeScanCall);
        }
    }

    /**
     * 停止蓝牙扫描
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void stopBlue() {
        if (!mScanning) {
            //结束蓝牙扫描
            mScanning = true;
            bluetoothLeScanner.stopScan(mLeScanCall);
        }
    }

    /**
     * 判断是否支持低功耗蓝牙
     *
     * @return
     */
    public boolean isSupportBlue() {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return true;
        }

        return false;
    }

    /**
     * 接口回调
     */
    public interface Callbacks {
        void callbackList(List<BluetoothDeviceWithRSSI> mBlueList);
    }

    /**
     * 设置接口回调
     *
     * @param callback 自身
     */
    public void setCallback(Callbacks callback) {
        this.callback = callback;
    }

    public List<BluetoothDeviceWithRSSI> getmBlueList() {
        return mBlueList;
    }

    public void setmBlueList(List<BluetoothDeviceWithRSSI> mBlueList) {
        this.mBlueList = mBlueList;
    }
}
