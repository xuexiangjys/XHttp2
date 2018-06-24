/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xuexiang.xhttp2.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import com.xuexiang.xhttp2.XHttp;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * 通用工具类
 *
 * @author xuexiang
 * @since 2018/6/20 上午12:46
 */
public final class Utils {

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 检查是否为null
     *
     * @param t
     * @param message
     * @param <T>
     * @return
     */
    public static <T> T checkNotNull(T t, String message) {
        if (t == null) {
            throw new NullPointerException(message);
        }
        return t;
    }

    /**
     * 当前是否有网
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (null == manager)
            return false;
        NetworkInfo info = manager.getActiveNetworkInfo();
        return null != info && info.isAvailable();
    }

    /**
     * 关闭 IO
     *
     * @param closeables closeables
     */
    public static void closeIO(final Closeable... closeables) {
        if (closeables == null) return;
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 应用程序缓存原理：
     * 1.当SD卡存在或者SD卡不可被移除的时候，就调用getExternalCacheDir()方法来获取缓存路径，否则就调用getCacheDir()方法来获取缓存路径<br>
     * 2.前者是/sdcard/Android/data/<application package>/cache 这个路径<br>
     * 3.后者获取到的是 /data/data/<application package>/cache 这个路径<br>
     *
     * @param uniqueName 缓存目录
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (isSDCardExist() && context.getExternalCacheDir() != null) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    private static boolean isSDCardExist() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable();
    }

    /**
     * 获取磁盘的文件目录
     *
     * @return SD卡不存在: /data/data/com.xxx.xxx/files;<br>
     * 存在: /storage/emulated/0/Android/data/com.xxx.xxx/files;
     */
    private static String getDiskFilesDir() {
        return isSDCardExist() && XHttp.getContext().getExternalFilesDir(null) != null ? XHttp.getContext().getExternalFilesDir(null).getPath() : XHttp.getContext().getFilesDir().getPath();
    }

    /**
     * 获取磁盘的自定义文件目录
     *
     * @return SD卡不存在: /data/data/com.xxx.xxx/files/fileDir;<br>
     * 存在: /storage/emulated/0/Android/data/com.xxx.xxx/files/fileDir;
     */
    public static String getDiskFilesDir(String fileDir) {
        return getDiskFilesDir() + File.separator + fileDir;
    }
}
