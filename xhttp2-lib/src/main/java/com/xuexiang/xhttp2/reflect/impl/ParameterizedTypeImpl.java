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

package com.xuexiang.xhttp2.reflect.impl;

import com.xuexiang.xhttp2.reflect.TypeException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

/**
 * 泛型类型
 *
 * @author xuexiang
 * @since 2018/6/21 上午12:27
 */
public class ParameterizedTypeImpl implements ParameterizedType {
    private final Class raw;
    private final Type[] args;
    private final Type owner;

    public ParameterizedTypeImpl(Class raw, Type[] args, Type owner) {
        this.raw = raw;
        this.args = args != null ? args : new Type[0];
        this.owner = owner;
        checkArgs();
    }

    private void checkArgs() {
        if (this.raw == null) {
            throw new TypeException("raw class can\'t be null");
        } else {
            TypeVariable[] typeParameters = raw.getTypeParameters();
            if (this.args.length != 0 && typeParameters.length != args.length) {
                throw new TypeException(raw.getName() + " expect " + typeParameters.length + " arg(s), got " + args.length);
            }
        }
    }

    @Override
    public Type[] getActualTypeArguments() {
        return this.args;
    }

    @Override
    public Type getRawType() {
        return raw;
    }

    @Override
    public Type getOwnerType() {
        return owner;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(raw.getName());
        if (args.length != 0) {
            sb.append('<');

            for (int i = 0; i < args.length; ++i) {
                if (i != 0) {
                    sb.append(", ");
                }

                Type type = args[i];
                if (!(type instanceof Class)) {
                    sb.append(args[i].toString());
                } else {
                    Class clazz = (Class) type;
                    if (!clazz.isArray()) {
                        sb.append(clazz.getName());
                    } else {
                        int count = 0;

                        do {
                            ++count;
                            clazz = clazz.getComponentType();
                        } while (clazz.isArray());

                        sb.append(clazz.getName());

                        for (int j = count; j > 0; --j) {
                            sb.append("[]");
                        }
                    }
                }
            }

            sb.append('>');
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && o instanceof ParameterizedTypeImpl) {
            ParameterizedTypeImpl that = (ParameterizedTypeImpl) o;
            return !raw.equals(that.raw) ? false : (!Arrays.equals(args, that.args) ? false : (owner != null ? owner.equals(that.owner) : that.owner == null));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = raw.hashCode();
        result = 31 * result + Arrays.hashCode(args);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        return result;
    }
}
