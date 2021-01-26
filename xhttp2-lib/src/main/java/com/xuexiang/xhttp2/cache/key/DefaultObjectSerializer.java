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

package com.xuexiang.xhttp2.cache.key;

/**
 * 默认实现将Object转化为String的序列化器
 *
 * @author xuexiang
 * @since 2021/1/27 12:36 AM
 */
public class DefaultObjectSerializer implements IObjectSerializer {
    @Override
    public String toString(Object obj) {
        return Strings._toString(obj);
    }
}
