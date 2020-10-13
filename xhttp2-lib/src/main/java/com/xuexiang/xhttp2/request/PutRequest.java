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

package com.xuexiang.xhttp2.request;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Put请求
 *
 * @author xuexiang
 * @since 2018/6/24 下午11:54
 */
public class PutRequest extends BaseBodyRequest<PutRequest> {

    public PutRequest(String url) {
        super(url);
    }

    @Override
    protected Observable<ResponseBody> generateRequest() {
        // 自定义的请求体
        if (mRequestBody != null) {
            return mApiManager.putBody(getUrl(), mRequestBody);
        } else if (mJson != null) {
            // Json
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), mJson);
            return mApiManager.putJson(getUrl(), body);
        } else if (mObject != null) {
            // 自定义的请求object
            return mApiManager.putBody(getUrl(), mObject);
        } else if (mString != null) {
            // 文本内容
            RequestBody body = RequestBody.create(mMediaType, mString);
            return mApiManager.putBody(getUrl(), body);
        } else {
            return mApiManager.put(getUrl(), mParams.urlParamsMap);
        }
    }
}
