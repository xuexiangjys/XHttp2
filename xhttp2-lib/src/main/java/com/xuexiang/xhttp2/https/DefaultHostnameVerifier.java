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

package com.xuexiang.xhttp2.https;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * 此类是用于主机名验证的基接口。 在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，
 * 则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。策略可以是基于证书的或依赖于其他验证方案。
 * 当验证 URL 主机名使用的默认规则失败时使用这些回调。如果主机名是可接受的，则返回 true
 *
 * @author xuexiang
 * @since 2018/6/21 上午1:13
 */
public class DefaultHostnameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}
