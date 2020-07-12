/*
 * Copyright (C) 2020 xuexiangjys(xuexiangjys@163.com)
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
 *
 */

package com.xuexiang.xhttp2.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.xuexiang.xhttp2.XHttp;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * 路径相关工具类
 *
 * @author xuexiang
 * @since 2020/6/6 12:32 AM
 */
public final class PathUtils {

    private PathUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }


    /**
     * 根据uri获取文件的绝对路径，解决Android 4.4以上 根据uri获取路径的方法
     *
     * @param uri 资源路径
     * @return 文件路径
     */
    public static String getFilePathByUri(Uri uri) {
        return getFilePathByUri(XHttp.getContext(), uri);
    }

    /**
     * 根据uri获取文件的绝对路径，解决Android 4.4以上 根据uri获取路径的方法
     *
     * @param context 上下文
     * @param uri     资源路径
     * @return 文件路径
     */
    public static String getFilePathByUri(Context context, Uri uri) {
        if (context == null || uri == null) {
            return null;
        }

        String scheme = uri.getScheme();
        // 以 file:// 开头的
        if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            return uri.getPath();
        }
        // 以 content:// 开头的，比如 content://media/extenral/images/media/17766
        if (ContentResolver.SCHEME_CONTENT.equals(scheme) && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            String path = null;
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (columnIndex > -1) {
                        path = cursor.getString(columnIndex);
                    }
                }
                cursor.close();
            }
            return path;
        }

        // 4.4及之后的 是以 content:// 开头的，比如 content://com.android.providers.media.documents/document/image%3A235700
        if (ContentResolver.SCHEME_CONTENT.equals(scheme) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                if (split.length == 2) {
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    } else {
                        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
                        try {
                            Class<?> storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
                            Method getVolumeList = storageManager.getClass().getMethod("getVolumeList");
                            Method getUuid = storageVolumeClazz.getMethod("getUuid");
                            Method getState = storageVolumeClazz.getMethod("getState");
                            Method getPath = storageVolumeClazz.getMethod("getPath");
                            Method isPrimary = storageVolumeClazz.getMethod("isPrimary");
                            Method isEmulated = storageVolumeClazz.getMethod("isEmulated");

                            Object result = getVolumeList.invoke(storageManager);

                            final int length = Array.getLength(result);
                            for (int i = 0; i < length; i++) {
                                Object storageVolumeElement = Array.get(result, i);
                                final boolean mounted = Environment.MEDIA_MOUNTED.equals(getState.invoke(storageVolumeElement))
                                        || Environment.MEDIA_MOUNTED_READ_ONLY.equals(getState.invoke(storageVolumeElement));
                                //if the media is not mounted, we need not get the volume details
                                if (!mounted) {
                                    continue;
                                }
                                //Primary storage is already handled.
                                if ((Boolean) isPrimary.invoke(storageVolumeElement)
                                        && (Boolean) isEmulated.invoke(storageVolumeElement)) {
                                    continue;
                                }
                                String uuid = (String) getUuid.invoke(storageVolumeElement);
                                if (uuid != null && uuid.equals(type)) {
                                    return getPath.invoke(storageVolumeElement) + "/" + split[1];
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            } else if (isDownloadsDocument(uri)) {
                String documentId = DocumentsContract.getDocumentId(uri);

                if (TextUtils.isEmpty(documentId)) {
                    return null;
                }
                if (documentId.startsWith("raw:")) {
                    return documentId.substring("raw:".length());
                }

                if (documentId.startsWith("msf:") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    String[] split = documentId.split(":");
                    if (split.length == 2) {
                        // content://media/external/downloads
                        Uri contentUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
                        String selection = MediaStore.Images.Media._ID + "=?";
                        String[] selectionArgs = new String[]{split[1]};
                        return getDataColumn(context, contentUri, selection, selectionArgs);
                    }
                }

                long id = Utils.toLong(documentId, -1);
                if (id != -1) {
                    return getDownloadPathById(context, id);
                }
            } else if (isMediaDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(scheme)) {
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            } else if (isHuaWeiUri(uri)) {
                String uriPath = uri.getPath();
                if (!Utils.isEmpty(uriPath) && uriPath.startsWith("/root")) {
                    return uriPath.replace("/root", "");
                }
            } else if (isQQUri(uri)) {
                String uriPath = uri.getPath();
                if (!Utils.isEmpty(uriPath)) {
                    return Environment.getExternalStorageDirectory() + uriPath.substring("/QQBrowser".length());
                }
            }
            return getDataColumn(context, uri, null, null);
        } else if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(scheme)) {
            return uri.getPath();
        }
        return null;
    }

    @Nullable
    private static String getDownloadPathById(Context context, long id) {
        Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), id);
        return getDataColumn(context, contentUri, null, null);
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * content://com.huawei.hidisk.fileprovider/root/storage/emulated/0/Android/data/com.xxx.xxx/
     *
     * @param uri uri The Uri to check.
     * @return Whether the Uri authority is HuaWei Uri.
     */
    public static boolean isHuaWeiUri(Uri uri) {
        return "com.huawei.hidisk.fileprovider".equals(uri.getAuthority());
    }

    /**
     * content://com.tencent.mtt.fileprovider/QQBrowser/Android/data/com.xxx.xxx/
     *
     * @param uri uri The Uri to check.
     * @return Whether the Uri authority is QQ Uri.
     */
    public static boolean isQQUri(Uri uri) {
        return "com.tencent.mtt.fileprovider".equals(uri.getAuthority());
    }

}
