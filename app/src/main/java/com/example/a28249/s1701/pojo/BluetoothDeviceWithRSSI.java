package com.example.a28249.s1701.pojo;

import android.bluetooth.BluetoothDevice;

public class BluetoothDeviceWithRSSI {
    private BluetoothDevice bluetoothDevice;

    private int RSSI;

    private String time;

    //"","A","B"
    private String positionStatus = "";

    //准备进校，准备离校，已经进校，已经离校
    private String judgeDesc = "";

    private String data;

    private String desc;


    public BluetoothDeviceWithRSSI(BluetoothDevice bluetoothDevice, int RSSI, String time, String data) {
        this.bluetoothDevice = bluetoothDevice;
        this.RSSI = RSSI;
        this.time = time;
        this.data = data;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public int getRSSI() {
        return RSSI;
    }

    public void setRSSI(int RSSI) {
        this.RSSI = RSSI;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPositionStatus() {
        return positionStatus;
    }

    public void setPositionStatus(String positionStatus) {
        this.positionStatus = positionStatus;
    }

    public String getJudgeDesc() {
        return judgeDesc;
    }

    public void setJudgeDesc(String judgeDesc) {
        this.judgeDesc = judgeDesc;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
