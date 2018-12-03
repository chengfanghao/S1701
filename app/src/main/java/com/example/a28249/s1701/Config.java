package com.example.a28249.s1701;

import com.example.a28249.s1701.pojo.BluetoothDeviceWithRSSI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
    //白名单列表
    public static final List<String> filterList = new ArrayList<>();

    //key:mac地址 value:单例对象
    public static final Map<String, BluetoothDeviceWithRSSI> singletonMap = new HashMap<>();

    //序列号
    public static int sequence = -1;


    //初始化白名单
    static {
        filterList.add("B0:91:22:D6:EE:E6");
        filterList.add("B0:91:22:D7:16:BA");
        /*
        macMap.put("D7:16:BA", "A");
        macMap.put("D7:00:5C", "A");
        macMap.put("75:BE:99", "A");
        macMap.put("D6:EE:D4", "A");
        macMap.put("D7:16:B3", "A");
        macMap.put("D6:EE:E9", "A");


        macMap.put("D6:F7:26", "B");
        macMap.put("D7:0C:02", "B");
        macMap.put("D6:EE:E6", "B");
        macMap.put("D6:EE:BD", "B");
        macMap.put("D7:0C:54", "B");
        macMap.put("75:BE:9F", "B");
        */
    }

    public static String bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }
}
