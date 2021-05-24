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

package com.xuexiang.xhttp2.transform.func;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.model.ApiResult;
import com.xuexiang.xhttp2.utils.ApiUtils;
import com.xuexiang.xhttp2.utils.TypeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

import static com.xuexiang.xhttp2.model.ApiResult.CODE;
import static com.xuexiang.xhttp2.model.ApiResult.DATA;
import static com.xuexiang.xhttp2.model.ApiResult.MSG;


/**
 * 定义了ApiResult结果转换Func
 *
 * @author xuexiang
 * @since 2018/5/22 下午4:45
 */
public class ApiResultFunc<T> implements Function<ResponseBody, ApiResult<T>> {
    private Type mType;
    private Gson mGson;
    /**
     * 是否保持原有的json
     */
    private boolean mKeepJson;

    public ApiResultFunc(Type type, boolean keepJson) {
        mGson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .create();
        mType = type;
        mKeepJson = keepJson;
    }

    @Override
    public ApiResult<T> apply(ResponseBody responseBody) throws Exception {
        ApiResult<T> apiResult = new ApiResult<>();
        apiResult.setCode(-1);
        if (mType instanceof ParameterizedType) {
            // 自定义ApiResult
            apiResult = parseCustomApiResult(responseBody, apiResult);
        } else {
            // 默认ApiResult
            apiResult = parseApiResult(responseBody, apiResult);
        }
        return apiResult;
    }

    @NonNull
    private ApiResult<T> parseCustomApiResult(ResponseBody responseBody, ApiResult<T> apiResult) {
        final Class<T> cls = (Class) ((ParameterizedType) mType).getRawType();
        if (ApiResult.class.isAssignableFrom(cls)) {
            final Type[] params = ((ParameterizedType) mType).getActualTypeArguments();
            final Class clazz = TypeUtils.getClass(params[0], 0);
            final Class rawType = TypeUtils.getClass(mType, 0);
            try {
                String json = responseBody.string();
                if (mKeepJson && !List.class.isAssignableFrom(rawType) && String.class.equals(clazz)) {
                    apiResult.setData((T) (json == null ? "" : json));
                    apiResult.setCode(ApiUtils.getSuccessCode());
                } else {
                    ApiResult result = mGson.fromJson(json, mType);
                    if (result != null) {
                        handleApiResult(clazz, rawType, result);
                        return result;
                    } else {
                        apiResult.setMsg("json is null");
                    }
                }
            } catch (Exception e) {
                apiResult.setMsg(e.getMessage());
            } finally {
                responseBody.close();
            }
        } else {
            apiResult.setMsg("ApiResult.class.isAssignableFrom(cls) err!!");
        }
        return apiResult;
    }

    /**
     * 处理解析的结果，判断是否需要处理data为null的情况
     */
    private void handleApiResult(Class clazz, Class rawType, ApiResult result) throws Exception {
        if (!XHttp.getInstance().isInStrictMode()) {
            if (result.getData() == null) {
                if (List.class.isAssignableFrom(rawType)) {
                    result.setData(new ArrayList<>());
                } else if (Map.class.isAssignableFrom(rawType)) {
                    result.setData(new HashMap<>());
                } else if (String.class.equals(clazz)) {
                    result.setData("");
                } else if (Boolean.class.equals(clazz)) {
                    result.setData(false);
                } else if (Integer.class.equals(clazz)
                        || Short.class.equals(clazz)
                        || Long.class.equals(clazz)
                        || Double.class.equals(clazz)
                        || Float.class.equals(clazz)) {
                    result.setData(0);
                } else {
                    result.setData(TypeUtils.newInstance(clazz));
                }
            }
        }
    }

    @NonNull
    private ApiResult<T> parseApiResult(ResponseBody responseBody, ApiResult<T> apiResult) {
        try {
            final String json = responseBody.string();
            final Class<T> clazz = TypeUtils.getClass(mType, 0);
            if (mKeepJson && String.class.equals(clazz)) {
                apiResult.setData((T) (json == null ? "" : json));
                apiResult.setCode(ApiUtils.getSuccessCode());
            } else {
                final ApiResult result = parseApiResult(json, apiResult);
                if (result != null) {
                    apiResult = result;
                    if (apiResult.getData() != null) {
                        T data = mGson.fromJson(apiResult.getData().toString(), clazz);
                        apiResult.setData(data);
                    } else {
                        apiResult.setMsg("ApiResult's data is null");
                    }
                } else {
                    apiResult.setMsg("json is null");
                }
            }
        } catch (JSONException e) {
            apiResult.setMsg(e.getMessage());
        } catch (IOException e) {
            apiResult.setMsg(e.getMessage());
        } finally {
            responseBody.close();
        }
        return apiResult;
    }

    /**
     * 解析请求到的json内容
     *
     * @param json
     * @param apiResult
     * @return
     * @throws JSONException
     */
    private ApiResult parseApiResult(String json, ApiResult apiResult) throws JSONException {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        JSONObject jsonObject = new JSONObject(json);
        if (jsonObject.has(CODE)) {
            apiResult.setCode(jsonObject.getInt(CODE));
        }
        if (jsonObject.has(DATA)) {
            apiResult.setData(jsonObject.getString(DATA));
        }
        if (jsonObject.has(MSG)) {
            apiResult.setMsg(jsonObject.getString(MSG));
        }
        return apiResult;
    }

}
