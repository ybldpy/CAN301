package com.can301.coursework.util;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    static final int BUFFER_SIZE = 1024*1024;


    private static void createFile(File file) throws IOException {
        if (!file.exists()){
            file.createNewFile();
        }
    }
    public static boolean writeJsonToFile(String jsonStr, File file) {
        FileOutputStream fileOutputStream = null;
        try {
            createFile(file);
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(jsonStr.getBytes(StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {

                }
            }
        }
        return true;
    }


    public static String readJsonFile(File file){
        FileInputStream fileInputStream = null;
        try {
            createFile(file);
            fileInputStream = new FileInputStream(file);
            List<Byte> bytes = new ArrayList<>(fileInputStream.available());
            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = fileInputStream.read(buffer,0,buffer.length))!=-1){
                for(int i=0;i<len;i++){
                    bytes.add(buffer[i]);
                }
            }
            byte[] bytesInArray = new byte[bytes.size()];
            for(int i=0;i<bytes.size();i++){
                bytesInArray[i] = bytes.get(i);
            }
            return new String(bytesInArray,StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            throw null;
        }
        finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
