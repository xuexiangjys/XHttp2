/*
 * Copyright (C) 2021 xuexiangjys(xuexiangjys@163.com)
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

package com.xuexiang.xhttp2demo.entity;

import com.xuexiang.xutil.net.JsonUtil;

import java.util.List;

/**
 * 查询结果
 *
 * @author xuexiang
 * @since 2021/10/25 10:21 PM
 */
public class QueryResult<T> {

    /**
     * 查询结果
     */
    public List<T> result;

    /**
     * 第几页数
     */
    public int pageNum;
    /**
     * 每页的数量
     */
    public int pageSize;

    public List<T> getResult() {
        return result;
    }

    public QueryResult<T> setResult(List<T> result) {
        this.result = result;
        return this;
    }

    public int getPageNum() {
        return pageNum;
    }

    public QueryResult<T> setPageNum(int pageNum) {
        this.pageNum = pageNum;
        return this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public QueryResult<T> setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    @Override
    public String toString() {
        return "QueryResult{" +
                "result=" + JsonUtil.toJson(result) +
                ", pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                '}';
    }
}
