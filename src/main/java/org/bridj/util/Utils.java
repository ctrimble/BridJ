/*
 * BridJ - Dynamic and blazing-fast native interop for Java.
 * http://bridj.googlecode.com/
 *
 * Copyright (c) 2010-2015, Olivier Chafik (http://ochafik.com/)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Olivier Chafik nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY OLIVIER CHAFIK AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.bridj.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import org.bridj.cpp.CPPObject;
import org.bridj.cpp.CPPType;

/**
 * Miscellaneous utility methods.
 *
 * @author ochafik
 */
public class Utils {

    public static final int getEnclosedConstructorParametersOffset(final Constructor<?> c) {
        Class<?> enclosingClass = c.getDeclaringClass().getEnclosingClass();
        Class<?>[] params = c.getParameterTypes();
        int overrideOffset = params.length > 0 && enclosingClass != null && enclosingClass == params[0] ? 1 : 0;
        return overrideOffset;
    }

    public static final boolean isDirect(final Buffer b) {
        if (b instanceof ByteBuffer) {
            return ((ByteBuffer) b).isDirect();
        }
        if (b instanceof IntBuffer) {
            return ((IntBuffer) b).isDirect();
        }
        if (b instanceof LongBuffer) {
            return ((LongBuffer) b).isDirect();
        }
        if (b instanceof DoubleBuffer) {
            return ((DoubleBuffer) b).isDirect();
        }
        if (b instanceof FloatBuffer) {
            return ((FloatBuffer) b).isDirect();
        }
        if (b instanceof ShortBuffer) {
            return ((ShortBuffer) b).isDirect();
        }
        if (b instanceof CharBuffer) {
            return ((CharBuffer) b).isDirect();
        }
        return false;
    }

    public static final Object[] takeRight(final Object[] array, final int n) {
        if (n == array.length) {
            return array;
        } else {
            Object[] res = new Object[n];
            System.arraycopy(array, array.length - n, res, 0, n);
            return res;
        }
    }

    public static final Object[] takeLeft(final Object[] array, final int n) {
        if (n == array.length) {
            return array;
        } else {
            Object[] res = new Object[n];
            System.arraycopy(array, 0, res, 0, n);
            return res;
        }
    }

    public static final boolean isSignedIntegral(final Type tpe) {
        return tpe == int.class || tpe == Integer.class
                || tpe == long.class || tpe == Long.class
                || tpe == short.class || tpe == Short.class
                || tpe == byte.class || tpe == Byte.class;
    }

    public static final String toString(final Type t) {
        if (t == null) {
            return "?";
        }
        if (t instanceof Class) {
            return ((Class<?>) t).getName();
        }
        return t.toString();
    }

    public static final String toString(final Throwable th) {
        if (th == null)
            return "<no trace>";
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        th.printStackTrace(pw);
        return sw.toString();
    }

    public static final boolean eq(final Object a, final Object b) {
        if ((a == null) != (b == null)) {
            return false;
        }
        return !(a != null && !a.equals(b));
    }

    public static final boolean containsTypeVariables(final Type type) {
        if (type instanceof TypeVariable) {
            return true;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            for (Type t : pt.getActualTypeArguments()) {
                if (containsTypeVariables(t)) {
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static final <T> Class<T> getClass(final Type type) {
        if (type == null) {
            return null;
        }
        if (type instanceof Class<?>) {
            return (Class<T>) type;
        }
        if (type instanceof ParameterizedType) {
            return getClass(((ParameterizedType) type).getRawType());
        }
        if (type instanceof GenericArrayType) {
            return (Class<T>) Array.newInstance(getClass(((GenericArrayType) type).getGenericComponentType()), 0).getClass();
        }
        if (type instanceof WildcardType) {
            return null;
        }
        if (type instanceof TypeVariable) {
            Type[] bounds = ((TypeVariable<?>) type).getBounds();
            return getClass(bounds[0]);
        }
        throw new UnsupportedOperationException("Cannot infer class from type " + type);
    }

    // This was in PointerIO.  Should it be the same as Utils.getClass(Type)?  It has
    // the same functionality, but returns null in more conditions.
  	public static final Class<?> getClassOrParameterizedRawType(final Type type) {
  		if (type instanceof Class<?>)
  			return (Class<?>)type;
  		if (type instanceof ParameterizedType)
  			return getClass(((ParameterizedType)type).getRawType());
  		return null;
  	}
  	
  	public static final boolean isClassOrParameterizedType( final Type type ) {
  		return type instanceof Class || type instanceof ParameterizedType;
  	}

    public static final Type getParent(final Type type) {
        if (type instanceof Class) {
            return ((Class<?>) type).getSuperclass();
        } else // TODO handle templates !!!
        {
            return getParent(getClass(type));
        }
    }

    public static final Class<?>[] getClasses(final Type[] types) {
        int n = types.length;
        Class<?>[] ret = new Class[n];
        for (int i = 0; i < n; i++) {
            ret[i] = getClass(types[i]);
        }
        return ret;
    }

    public static final Type getUniqueParameterizedTypeParameterOrNull(final Type type) {
        return (type instanceof ParameterizedType) ? ((ParameterizedType) type).getActualTypeArguments()[0] : null;
    }
    
    public static final Type getUniqueParameterizedTypeParameter(final Type type) {
      return ((ParameterizedType) type).getActualTypeArguments()[0];
  }

    public static final boolean parametersComplyToSignature(final Object[] values, final Class<?>[] parameterTypes) {
        if (values.length != parameterTypes.length) {
            return false;
        }
        for (int i = 0, n = values.length; i < n; i++) {
            Object value = values[i];
            Class<?> parameterType = parameterTypes[i];
            if (!parameterType.isInstance(value)) {
                return false;
            }
        }
        return true;
    }

		public static final Type resolveType(final Type tpe, final Type structType) {
		        if (tpe == null || (tpe instanceof WildcardType)) {
		            return null;
		        }
		
		        Type ret;
		        if (tpe instanceof ParameterizedType) {
		            ParameterizedType pt = (ParameterizedType) tpe;
		            Type[] actualTypeArguments = pt.getActualTypeArguments();
		            Type[] resolvedActualTypeArguments = new Type[actualTypeArguments.length];
		            for (int i = 0; i < actualTypeArguments.length; i++) {
		                resolvedActualTypeArguments[i] = resolveType(actualTypeArguments[i], structType);
		            }
		            Type resolvedOwnerType = resolveType(pt.getOwnerType(), structType);
		            Type rawType = pt.getRawType();
		            if ((tpe instanceof CPPType) || CPPObject.class.isAssignableFrom(getClass(rawType))) // TODO args
		            {
		                ret = new CPPType(resolvedOwnerType, rawType, (Object[])resolvedActualTypeArguments);
		            } else {
		                ret = new DefaultParameterizedType(resolvedOwnerType, rawType, resolvedActualTypeArguments);
		            }
		        } else if (tpe instanceof TypeVariable) {
		            TypeVariable<?> tv = (TypeVariable<?>) tpe;
		            Class<?> structClass = getClass(structType);
		            TypeVariable<?>[] typeParameters = structClass.getTypeParameters();
		            int i = Arrays.asList(typeParameters).indexOf(tv);
		            // TODO recurse on pt.getOwnerType() if i < 0.
		            if (i >= 0) {
		                if (structType instanceof ParameterizedType) {
		                    ParameterizedType pt = (ParameterizedType) structType;
		                    //Type[] typeParams = CPPRuntime.getTemplateTypeParameters(null, tpe)
		                    ret = pt.getActualTypeArguments()[i];
		                } else {
		                    throw new RuntimeException("Type " + structType + " does not have params, cannot resolve " + tpe);
		                }
		            } else {
		                throw new RuntimeException("Type param " + tpe + " not found in params of " + structType + " (" + Arrays.asList(typeParameters) + ")");
		            }
		        } else {
		            ret = tpe;
		        }
		
		        assert !containsTypeVariables(ret);
		//            throw new RuntimeException("Type " + ret + " cannot be resolved");
		
		        return ret;
		    }
}
