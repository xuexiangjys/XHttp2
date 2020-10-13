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

package com.xuexiang.xhttp2.cache;

import android.content.Context;
import android.os.StatFs;

import com.xuexiang.xhttp2.cache.converter.IDiskConverter;
import com.xuexiang.xhttp2.cache.converter.SerializableDiskConverter;
import com.xuexiang.xhttp2.cache.core.CacheCore;
import com.xuexiang.xhttp2.cache.core.LruDiskCache;
import com.xuexiang.xhttp2.cache.core.LruMemoryCache;
import com.xuexiang.xhttp2.cache.model.CacheMode;
import com.xuexiang.xhttp2.cache.model.CacheResult;
import com.xuexiang.xhttp2.cache.stategy.IStrategy;
import com.xuexiang.xhttp2.logs.HttpLog;
import com.xuexiang.xhttp2.utils.TypeUtils;
import com.xuexiang.xhttp2.utils.Utils;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.exceptions.Exceptions;

/**
 * <p>描述：缓存统一入口类</p>
 * <p>
 * <p>主要实现技术：RxJava+DiskLruCache(jakewharton大神开源的LRU库)</p>
 * <p>
 * <p>
 * 主要功能：<br>
 * 1.可以独立使用，单独用RxCache来存储数据<br>
 * 2.采用transformer与网络请求结合，可以实现网络缓存功能,本地硬缓存<br>
 * 3.可以保存缓存 （异步）<br>
 * 4.可以读取缓存（异步）<br>
 * 5.可以判断缓存是否存在<br>
 * 6.根据key删除缓存<br>
 * 7.清空缓存（异步）<br>
 * 8.缓存Key会自动进行MD5加密<br>
 * 9.其它参数设置：缓存磁盘大小、缓存key、缓存时间、缓存存储的转换器、缓存目录、缓存Version<br>
 * <p>
 * <p>
 * 使用说明：<br>
 * RxCache mRxCache = new RxCache.Builder(this)<br>
 * .appVersion(1)//不设置，默认为1</br>
 * .diskDir(new File(getCacheDir().getPath() + File.separator + "data-mCache"))//不设置，默认使用缓存路径<br>
 * .mDiskConverter(new SerializableDiskConverter())//目前只支持Serializable缓存<br>
 * .diskMax(20*1024*1024)//不设置， 默为认50MB<br>
 * .build();</br>
 *
 * @author xuexiang
 * 2018/6/20 下午7:26
 */
public final class RxCache {
    public static final long CACHE_NEVER_EXPIRE = -1;//永久不过期

    private boolean isDiskCache;
    //构建内存缓存需要的属性
    private int memoryMaxSize;

    private final Context context;
    private final CacheCore cacheCore;                                  //缓存的核心管理类
    private final String cacheKey;                                      //缓存的key
    private final long cacheTime;                                       //缓存的时间 单位:秒
    private final IDiskConverter diskConverter;                         //缓存的转换器
    private final File diskDir;                                         //缓存的磁盘目录，默认是缓存目录
    private final int appVersion;                                       //缓存的版本
    private final long diskMaxSize;                                     //缓存的磁盘大小

    public RxCache() {
        this(new Builder());
    }

    private RxCache(Builder builder) {
        this.context = builder.context;
        this.isDiskCache = builder.isDiskCache;
        this.memoryMaxSize = builder.memoryMaxSize;
        this.cacheKey = builder.cacheKey;
        this.cacheTime = builder.cacheTime;
        this.diskDir = builder.diskDir;
        this.appVersion = builder.appVersion;
        this.diskMaxSize = builder.diskMaxSize;
        this.diskConverter = builder.diskConverter;
        if (isDiskCache) {
            cacheCore = new CacheCore(new LruDiskCache(diskConverter, diskDir, appVersion, diskMaxSize));
        } else {
            cacheCore = new CacheCore(new LruMemoryCache(builder.memoryMaxSize));
        }
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    /**
     * 缓存transformer
     *
     * @param cacheMode 缓存类型
     * @param type      缓存clazz
     */
    @SuppressWarnings(value = {"unchecked"})
    public <T> ObservableTransformer<T, CacheResult<T>> transformer(final CacheMode cacheMode, final Type type) {
        final IStrategy strategy = loadStrategy(cacheMode);//获取缓存策略
        return new ObservableTransformer<T, CacheResult<T>>() {
            @Override
            public ObservableSource<CacheResult<T>> apply(@NonNull Observable<T> upstream) {
                HttpLog.i("cacheMode=" + cacheMode + ", cacheKey=" + RxCache.this.cacheKey);
                Type tempType = type;
                if (type instanceof ParameterizedType) {//自定义ApiResult
                    Class<T> cls = (Class) ((ParameterizedType) type).getRawType();
                    if (CacheResult.class.isAssignableFrom(cls)) {
                        tempType = TypeUtils.getParameterizedType(type, 0);
                    }
                }
                return strategy.execute(RxCache.this, RxCache.this.cacheKey, RxCache.this.cacheTime, upstream, tempType);
            }
        };
    }

    private static abstract class SimpleSubscribe<T> implements ObservableOnSubscribe<T> {
        @Override
        public void subscribe(@NonNull ObservableEmitter<T> subscriber) throws Exception {
            try {
                T data = execute();
                if (!subscriber.isDisposed()) {
                    subscriber.onNext(data);
                }
            } catch (Throwable e) {
                HttpLog.e(e);
                if (!subscriber.isDisposed()) {
                    subscriber.onError(e);
                }
                Exceptions.throwIfFatal(e);
                return;
            }

            if (!subscriber.isDisposed()) {
                subscriber.onComplete();
            }
        }

        abstract T execute() throws Throwable;
    }

    /**
     * 获取缓存
     *
     * @param type 保存的类型
     * @param key  缓存key
     */
    public <T> Observable<T> load(final Type type, final String key) {
        return load(type, key, -1);
    }

    /**
     * 根据时间读取缓存
     *
     * @param type 保存的类型
     * @param key  缓存key
     * @param time 保存时间
     */
    public <T> Observable<T> load(final Type type, final String key, final long time) {
        return Observable.create(new SimpleSubscribe<T>() {
            @Override
            T execute() {
                return cacheCore.load(type, key, time);
            }
        });
    }

    /**
     * 保存
     *
     * @param key   缓存key
     * @param value 缓存Value
     */
    public <T> Observable<Boolean> save(final String key, final T value) {
        return Observable.create(new SimpleSubscribe<Boolean>() {
            @Override
            Boolean execute() throws Throwable {
                cacheCore.save(key, value);
                return true;
            }
        });
    }

    /**
     * 是否包含
     */
    public Observable<Boolean> containsKey(final String key) {
        return Observable.create(new SimpleSubscribe<Boolean>() {
            @Override
            Boolean execute() throws Throwable {
                return cacheCore.containsKey(key);
            }
        });
    }

    /**
     * 删除缓存
     */
    public Observable<Boolean> remove(final String key) {
        return Observable.create(new SimpleSubscribe<Boolean>() {
            @Override
            Boolean execute() throws Throwable {
                return cacheCore.remove(key);
            }
        });
    }

    /**
     * 清空缓存
     */
    public Observable<Boolean> clear() {
        return Observable.create(new SimpleSubscribe<Boolean>() {
            @Override
            Boolean execute() throws Throwable {
                return cacheCore.clear();
            }
        });
    }

    /**
     * 利用反射，加载缓存策略模型
     */
    private IStrategy loadStrategy(CacheMode cacheMode) {
        try {
            String pkName = IStrategy.class.getPackage().getName();
            return (IStrategy) Class.forName(pkName + "." + cacheMode.getClassName()).newInstance();
        } catch (Exception e) {
            throw new RuntimeException("loadStrategy(" + cacheMode + ") err!!" + e.getMessage());
        }
    }

    public long getCacheTime() {
        return cacheTime;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public Context getContext() {
        return context;
    }

    public CacheCore getCacheCore() {
        return cacheCore;
    }

    public IDiskConverter getDiskConverter() {
        return diskConverter;
    }

    public File getDiskDir() {
        return diskDir;
    }

    public int getAppVersion() {
        return appVersion;
    }

    public long getDiskMaxSize() {
        return diskMaxSize;
    }

    public static final class Builder {
        private static final int MIN_DISK_CACHE_SIZE = 5 * 1024 * 1024; // 5MB
        private static final int MAX_DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

        private Context context;
        private boolean isDiskCache;
        //构建内存缓存需要的属性
        private int memoryMaxSize;
        //构建磁盘缓存需要的属性
        private int appVersion;
        private long diskMaxSize;
        private File diskDir;
        private IDiskConverter diskConverter;
        private String cacheKey;
        private long cacheTime;

        public Builder() {
            this.isDiskCache = true;
            this.diskConverter = new SerializableDiskConverter();
            this.cacheTime = CACHE_NEVER_EXPIRE;
            this.appVersion = 1;
        }

        public Builder(RxCache rxCache) {
            this.context = rxCache.context;
            this.isDiskCache = rxCache.isDiskCache;
            this.memoryMaxSize = rxCache.memoryMaxSize;
            this.appVersion = rxCache.appVersion;
            this.diskMaxSize = rxCache.diskMaxSize;
            this.diskDir = rxCache.diskDir;
            this.diskConverter = rxCache.diskConverter;
            this.cacheKey = rxCache.cacheKey;
            this.cacheTime = rxCache.cacheTime;
        }

        public Builder init(Context context) {
            this.context = context;
            return this;
        }

        /**
         * 设置是否是磁盘缓存
         *
         * @param isDiskCache
         * @return
         */
        public Builder isDiskCache(boolean isDiskCache) {
            this.isDiskCache = isDiskCache;
            return this;
        }

        /**
         * 设置内存缓存的最大数量
         *
         * @param memoryMaxSize
         * @return
         */
        public Builder memoryMaxSize(int memoryMaxSize) {
            this.memoryMaxSize = memoryMaxSize;
            return this;
        }

        /**
         * 不设置，默认为1
         */
        public Builder appVersion(int appVersion) {
            this.appVersion = appVersion;
            return this;
        }

        /**
         * 默认为缓存路径
         *
         * @param directory
         * @return
         */
        public Builder diskDir(File directory) {
            this.diskDir = directory;
            return this;
        }


        public Builder diskConverter(IDiskConverter converter) {
            this.diskConverter = converter;
            return this;
        }

        /**
         * 不设置， 默为认50MB
         */
        public Builder diskMax(long maxSize) {
            this.diskMaxSize = maxSize;
            return this;
        }

        public Builder cacheKey(String cacheKey) {
            this.cacheKey = cacheKey;
            return this;
        }

        public Builder cacheTime(long cacheTime) {
            this.cacheTime = cacheTime;
            return this;
        }

        public RxCache build() {
            if (isDiskCache) { //初始化磁盘缓存的属性
                if (this.diskDir == null && this.context != null) {
                    this.diskDir = Utils.getDiskCacheDir(this.context, "data-cache");
                }
                Utils.checkNotNull(this.diskDir, "diskDir == null");
                if (!this.diskDir.exists()) {
                    this.diskDir.mkdirs();
                }
                if (this.diskConverter == null) {
                    this.diskConverter = new SerializableDiskConverter();
                }
                if (diskMaxSize <= 0) {
                    diskMaxSize = calculateDiskCacheSize(diskDir);
                }
                appVersion = Math.max(1, this.appVersion);
            } else {
                if (memoryMaxSize <= 0) {
                    memoryMaxSize = (int) (Runtime.getRuntime().maxMemory() / 1024) / 8;
                }
            }
            cacheTime = Math.max(CACHE_NEVER_EXPIRE, this.cacheTime);
            return new RxCache(this);
        }

        private static long calculateDiskCacheSize(File dir) {
            long size = 0;
            try {
                StatFs statFs = new StatFs(dir.getAbsolutePath());
                long available = ((long) statFs.getBlockCount()) * statFs.getBlockSize();
                size = available / 50;
            } catch (IllegalArgumentException ignored) {
            }
            return Math.max(Math.min(size, MAX_DISK_CACHE_SIZE), MIN_DISK_CACHE_SIZE);
        }
    }
}
