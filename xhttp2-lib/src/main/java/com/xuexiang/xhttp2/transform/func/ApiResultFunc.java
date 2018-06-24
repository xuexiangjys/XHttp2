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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xuexiang.xhttp2.exception.ApiException;
import com.xuexiang.xhttp2.logs.HttpLog;
import com.xuexiang.xhttp2.model.ApiResult;
import com.xuexiang.xhttp2.utils.TypeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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
    private Type type;
    private Gson gson;
    /**
     * 是否保持原有的json
     */
    private boolean keepJson;

    public ApiResultFunc(Type type, boolean keepJson) {
        gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .create();
        this.type = type;
        this.keepJson = keepJson;
    }

    @Override
    public ApiResult<T> apply(ResponseBody responseBody) throws Exception {
        ApiResult<T> apiResult = new ApiResult<T>();
        apiResult.setCode(-1);
        if (type instanceof ParameterizedType) {//自定义ApiResult
            Class<T> cls = (Class) ((ParameterizedType) type).getRawType();
            if (ApiResult.class.isAssignableFrom(cls)) {
                Type[] params = ((ParameterizedType) type).getActualTypeArguments();
                Class clazz = TypeUtils.getClass(params[0], 0);
                try {
                    String json = responseBody.string();
                    if (keepJson && clazz.equals(String.class)) {
                        apiResult.setData((T) json);
                        apiResult.setCode(0);
                    } else {
                        ApiResult result = checkApiResult(json);
                        if (result != null) {
                            return result;
                        } else {
                            apiResult.setMsg("json is null");
                        }
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                    apiResult.setCode(e.getCode());
                    apiResult.setMsg(e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    apiResult.setMsg(e.getMessage());
                } finally {
                    responseBody.close();
                }
            } else {
                apiResult.setMsg("ApiResult.class.isAssignableFrom(cls) err!!");
            }
        } else {//默认ApiResult
            try {
                String json = responseBody.string();
                Class<T> clazz = TypeUtils.getClass(type, 0);
                if (clazz.equals(String.class)) {
                    apiResult.setData((T) json);
                    apiResult.setCode(0);
                } else {
                    ApiResult result = parseApiResult(json, apiResult);
                    if (result != null) {
                        apiResult = result;
                        if (apiResult.getData() != null) {
                            T data = gson.fromJson(apiResult.getData().toString(), clazz);
                            apiResult.setData(data);
                        } else {
                            apiResult.setMsg("ApiResult's data is null");
                        }
                    } else {
                        apiResult.setMsg("json is null");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                apiResult.setMsg(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                apiResult.setMsg(e.getMessage());
            } finally {
                responseBody.close();
            }
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

    /**
     * 检验API规范
     *
     * @param json
     * @return
     * @throws JSONException
     * @throws ApiException
     */
    private ApiResult checkApiResult(String json) throws JSONException, ApiException {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        JSONObject jsonObject = new JSONObject(json);
        int code = jsonObject.getInt(CODE);
        String msg = jsonObject.getString(MSG);
        if (code == 0) {
            return gson.fromJson(json, type);
        } else {
            HttpLog.e("请求不成功， msg：" + msg + ", code:" + code);
            throw new ApiException(msg, code);
        }
    }

}
