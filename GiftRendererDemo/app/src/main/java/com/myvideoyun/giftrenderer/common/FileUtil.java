package com.myvideoyun.giftrenderer.common;

import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {

    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    deleteFile(f);
                }
            } else {
                boolean result = file.delete();
            }
        }
    }

    public static boolean copyFileFromAssets(String src, String dst, AssetManager manager) {
        try {
            String[] files = manager.list(src);
            if (files.length > 0) {     //如果是文件夹
                File folder = new File(dst);
                if (!folder.exists()) {
                    boolean b = folder.mkdirs();
                    if (!b) {
                        return false;
                    }
                }
                for (String fileName : files) {
                    if (!copyFileFromAssets(src + File.separator + fileName, dst +
                            File.separator + fileName, manager)) {
                        return false;
                    }
                }
            } else {  //如果是文件
                if (!copyAssetsFile(src, dst, manager)) {
                    return false;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private static boolean copyAssetsFile(String src, String dst, AssetManager manager) {
        InputStream in;
        OutputStream out;
        try {
            File file = new File(dst);
            if (!file.exists()) {
                in = manager.open(src);
                out = new FileOutputStream(dst);
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                out.flush();
                out.close();
                in.close();
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
