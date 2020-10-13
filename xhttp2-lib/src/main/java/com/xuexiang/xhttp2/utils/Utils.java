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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.RequiresApi;

import com.xuexiang.xhttp2.XHttp;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import okhttp3.MediaType;

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

    private static final String EXT_STORAGE_PATH = getExtStoragePath();

    private static final String EXT_STORAGE_DIR = EXT_STORAGE_PATH + File.separator;

    private static final String APP_EXT_STORAGE_PATH = EXT_STORAGE_DIR + "Android";

    private static final String EXT_DOWNLOADS_PATH = getExtDownloadsPath();

    private static final String EXT_PICTURES_PATH = getExtPicturesPath();

    private static final String EXT_DCIM_PATH = getExtDCIMPath();


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
        if (null == manager) {
            return false;
        }
        NetworkInfo info = manager.getActiveNetworkInfo();
        return null != info && info.isAvailable();
    }

    /**
     * 关闭 IO
     *
     * @param closeables closeables
     */
    public static void closeIO(final Closeable... closeables) {
        if (closeables == null) {
            return;
        }
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

    /**
     * 是否是分区存储模式：在公共目录下file的api无效了
     *
     * @return 是否是分区存储模式
     */
    public static boolean isScopedStorageMode() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Environment.isExternalStorageLegacy();
    }

    /**
     * 是否是公有目录
     *
     * @return 是否是公有目录
     */
    public static boolean isPublicPath(File file) {
        if (file == null) {
            return false;
        }
        try {
            return isPublicPath(file.getCanonicalFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 是否是公有目录
     *
     * @return 是否是公有目录
     */
    public static boolean isPublicPath(String filePath) {
        if (isEmpty(filePath)) {
            return false;
        }
        return filePath.startsWith(EXT_STORAGE_PATH) && !filePath.startsWith(APP_EXT_STORAGE_PATH);
    }

    /**
     * 获取下载目录
     * <pre>path: /storage/emulated/0/Download</pre>
     *
     * @return 下载目录
     */
    public static String getExtDownloadsPath() {
        return Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getAbsolutePath();
    }

    /**
     * 获取图片目录
     * <pre>path: /storage/emulated/0/Pictures</pre>
     *
     * @return 图片目录
     */
    public static String getExtPicturesPath() {
        return Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .getAbsolutePath();
    }

    /**
     * 获取相机拍摄的照片和视频的目录
     * <pre>path: /storage/emulated/0/DCIM</pre>
     *
     * @return 照片和视频目录
     */
    public static String getExtDCIMPath() {
        return Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                .getAbsolutePath();
    }

    /**
     * 获取 Android 外置储存的根目录
     * <pre>path: /storage/emulated/0</pre>
     *
     * @return 外置储存根目录
     */
    public static String getExtStoragePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 判断字符串是否为 null 或长度为 0
     *
     * @param s 待校验字符串
     * @return {@code true}: 空<br> {@code false}: 不为空
     */
    public static boolean isEmpty(final CharSequence s) {
        return s == null || s.length() == 0;
    }

    /**
     * String转Long（防止崩溃）
     *
     * @param value
     * @param defValue 默认值
     * @return
     */
    public static long toLong(final String value, final long defValue) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return defValue;
    }

    /**
     * 获取文件保存到外部公共下载目录的uri
     *
     * @param dirPath   文件目录
     * @param fileName  文件名
     * @param mediaType 文件类型
     * @return uri
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static Uri getDownloadFileUri(String dirPath, String fileName, MediaType mediaType) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.TITLE, fileName);
        values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
        values.put(MediaStore.Downloads.MIME_TYPE, mediaType.toString());
        values.put(MediaStore.Downloads.RELATIVE_PATH, getRelativePath(dirPath));
        return getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
    }


    /**
     * 获取外部公共多媒体的uri
     *
     * @param dirPath   文件目录
     * @param fileName  文件名
     * @param mediaType 文件类型
     * @return 图片的uri
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static Uri getMediaFileUri(String dirPath, String fileName, MediaType mediaType) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, mediaType.toString());
        values.put(MediaStore.Images.Media.ORIENTATION, 0);
        values.put(MediaStore.Images.Media.RELATIVE_PATH, getRelativePath(dirPath));
        if ("image".equals(mediaType.type())) {
            return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } else if ("audio".equals(mediaType.type())) {
            return getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
        } else if ("video".equals(mediaType.type())) {
            return getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        } else {
            return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
    }

    /**
     * 获取外部公共普通文件的uri
     *
     * @param dirPath   文件目录
     * @param fileName  文件名
     * @param mediaType 文件类型
     * @return 普通文件的uri
     */
    public static Uri getNormalFileUri(String dirPath, String fileName, MediaType mediaType) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, getFilePath(dirPath, fileName));
        values.put(MediaStore.Images.Media.MIME_TYPE, mediaType.toString());
        return getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
    }

    private static String getRelativePath(String dirPath) {
        int index = dirPath.indexOf(EXT_STORAGE_DIR);
        if (index != -1) {
            return dirPath.substring(EXT_STORAGE_DIR.length());
        }
        return dirPath;
    }

    /**
     * 获取外部公共目录文件的输出流
     *
     * @param dirPath   文件目录
     * @param fileName  文件名
     * @param mediaType 文件类型
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static OutputStream getOutputStream(String dirPath, String fileName, MediaType mediaType) throws FileNotFoundException {
        Uri uri = getFileUri(dirPath, fileName, mediaType);
        return openOutputStream(uri);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static Uri getFileUri(String dirPath, String fileName, MediaType mediaType) {
        Uri uri;
        if (dirPath.startsWith(EXT_DOWNLOADS_PATH)) {
            uri = getDownloadFileUri(dirPath, fileName, mediaType);
        } else if (dirPath.startsWith(EXT_PICTURES_PATH) || dirPath.startsWith(EXT_DCIM_PATH)) {
            uri = getMediaFileUri(dirPath, fileName, mediaType);
        } else {
            uri = getNormalFileUri(dirPath, fileName, mediaType);
        }
        return uri;
    }

    /**
     * 从uri资源符中获取输入流
     *
     * @param uri 文本资源符
     * @return InputStream
     */
    public static OutputStream openOutputStream(Uri uri) throws FileNotFoundException {
        return getContentResolver().openOutputStream(uri);
    }

    private static ContentResolver getContentResolver() {
        return XHttp.getContext().getContentResolver();
    }

    /**
     * 获取文件的路径
     *
     * @param dirPath  目录
     * @param fileName 文件名
     * @return 拼接的文件的路径
     */
    public static String getFilePath(String dirPath, String fileName) {
        return getDirPath(dirPath) + fileName;
    }

    /**
     * 获取文件目录的路径，自动补齐"/"
     *
     * @param dirPath 目录路径
     * @return 自动补齐"/"的目录路径
     */
    public static String getDirPath(String dirPath) {
        if (isEmpty(dirPath)) {
            return "";
        }

        if (!dirPath.trim().endsWith(File.separator)) {
            dirPath = dirPath.trim() + File.separator;
        }
        return dirPath;
    }

}
