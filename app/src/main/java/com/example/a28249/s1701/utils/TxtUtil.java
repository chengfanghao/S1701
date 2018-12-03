package com.example.a28249.s1701.utils;


import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class TxtUtil {

    public static void writeTxt(String fileName, List<String> contentList) {
        FileWriter writer = null;
        try {
            File path = new File(Environment.getExternalStorageDirectory() + File.separator + "test");

            if (!path.exists()) {
                path.mkdirs();
            }

            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileWriter(path + File.separator + fileName, true);
            for (String content : contentList) {
                writer.write(content + "\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
