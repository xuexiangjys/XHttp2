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

package com.xuexiang.xhttp2.reflect;

import com.xuexiang.xhttp2.reflect.impl.ParameterizedTypeImpl;
import com.xuexiang.xhttp2.reflect.impl.WildcardTypeImpl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 泛型生成工具，用法详细参见：https://github.com/ikidou/TypeBuilder
 * <li>用法：</li>
 * *************************************************
 * <p>
 * 1.Example for List<String> :
 * Type type = TypeBuilder
 * .newInstance(List.class)
 * .addTypeParam(String.class)
 * .build();
 * <p>
 * *************************************************
 * <p>
 * 2.Example for List<? super String>:
 * Type type = TypeBuilder
 * .newInstance(List.class)
 * .addTypeParamSuper(String.class)
 * .build();
 * <p>
 * *************************************************
 * <p>
 * 3.Example for List<? extends CharSequence>:
 * Type type = TypeBuilder
 * .newInstance(List.class)
 * .addTypeParamExtends(CharSequence.class)
 * .build();
 * <p>
 * *************************************************
 * <p>
 * 4.Example for Map<String, String[]>:
 * Type type = TypeBuilder
 * .newInstance(HashMap.class)
 * .addTypeParam(String.class)
 * .addTypeParam(String[].class)
 * .build();
 * <p>
 * *************************************************
 * <p>
 * 5.Example for Map<String, List<String>>:
 * Type type = TypeBuilder.newInstance(Map.class)
 * .addTypeParam(String.class)
 * .beginSubType(List.class) //开始 List<String> 部分
 * .addTypeParam(String.class) //设置List的泛型值
 * .endSubType() //结束 List<String> 部分
 * .build();
 * *************************************************
 *
 * @author xuexiang
 * @since 2018/6/21 上午12:24
 */
public final class TypeBuilder {

    private final TypeBuilder parent;
    private final Class raw;
    private final List<Type> args = new ArrayList<>();

    private TypeBuilder(Class raw, TypeBuilder parent) {
        assert raw != null;
        this.raw = raw;
        this.parent = parent;
    }

    /**
     * 获取类型构建者
     *
     * @param raw
     * @return
     */
    public static TypeBuilder newInstance(Class raw) {
        return new TypeBuilder(raw, null);
    }

    /**
     * 获取类型构建者
     *
     * @param raw
     * @return
     */
    private static TypeBuilder newInstance(Class raw, TypeBuilder parent) {
        return new TypeBuilder(raw, parent);
    }

    /**
     * 开始泛型类型
     *
     * @param raw
     * @return
     */
    public TypeBuilder beginSubType(Class raw) {
        return newInstance(raw, this);
    }

    /**
     * 结束泛型类型
     *
     * @return
     */
    public TypeBuilder endSubType() {
        if (parent == null) {
            throw new TypeException("expect beginSubType() before endSubType()");
        }

        parent.addTypeParam(getType());

        return parent;
    }

    /**
     * 增加泛型类型
     *
     * @param clazz
     * @return
     */
    public TypeBuilder addTypeParam(Class clazz) {
        return addTypeParam((Type) clazz);
    }

    /**
     * 增加泛型类型
     *
     * @param type
     * @return
     */
    public TypeBuilder addTypeParam(Type type) {
        if (type == null) {
            throw new NullPointerException("addTypeParam expect not null Type");
        }

        args.add(type);

        return this;
    }

    /**
     * 增加泛型extends类型
     *
     * @param classes
     * @return
     */
    public TypeBuilder addTypeParamExtends(Class... classes) {
        if (classes == null) {
            throw new NullPointerException("addTypeParamExtends() expect not null Class");
        }

        WildcardTypeImpl wildcardType = new WildcardTypeImpl(null, classes);

        return addTypeParam(wildcardType);
    }

    /**
     * 增加泛型Super类型
     *
     * @param classes
     * @return
     */
    public TypeBuilder addTypeParamSuper(Class... classes) {
        if (classes == null) {
            throw new NullPointerException("addTypeParamSuper() expect not null Class");
        }

        WildcardTypeImpl wildcardType = new WildcardTypeImpl(classes, null);

        return addTypeParam(wildcardType);
    }

    /**
     * 构建类型
     *
     * @return
     */
    public Type build() {
        if (parent != null) {
            throw new TypeException("expect endSubType() before build()");
        }

        return getType();
    }

    private Type getType() {
        if (args.isEmpty()) {
            return raw;
        }
        return new ParameterizedTypeImpl(raw, args.toArray(new Type[args.size()]), null);
    }
}
