package com.dieyidezui.gson.adapter;

import com.google.gson.*;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Type safe for:
 * 1. Primitive types and box types
 * 2. String
 * 3. Collection
 * 4. Map
 * <p>
 * Character is a little bit different from gson's default policy,
 * it will take string's first char as result instead of throw.
 */
@SuppressWarnings("unchecked")
public class TypeSafeAdapterFactory implements TypeAdapterFactory {

    private static final Map<Class<?>, Number> NUMBER_MAP;

    static {
        NUMBER_MAP = new HashMap<>(16);
        NUMBER_MAP.put(byte.class, (byte) 0);
        NUMBER_MAP.put(Byte.class, (byte) 0);
        NUMBER_MAP.put(short.class, (short) 0);
        NUMBER_MAP.put(Short.class, (short) 0);
        NUMBER_MAP.put(int.class, 0);
        NUMBER_MAP.put(Integer.class, 0);
        NUMBER_MAP.put(float.class, 0f);
        NUMBER_MAP.put(Float.class, 0f);
        NUMBER_MAP.put(double.class, 0.0);
        NUMBER_MAP.put(Double.class, 0.0);
        NUMBER_MAP.put(long.class, 0L);
        NUMBER_MAP.put(Long.class, 0L);
    }

    private static Number getPlaceholderNumber(Class<?> clazz) {
        return NUMBER_MAP.get(clazz);
    }


    public static TypeSafeAdapterFactory newInstance() {
        return newInstance(Collections.<Type, InstanceCreator<?>>emptyMap());
    }

    public static TypeSafeAdapterFactory newInstance(Map<Type, InstanceCreator<?>> emptyCreaters) {
        if (emptyCreaters == null) {
            throw new NullPointerException("map == null");
        }
        return new TypeSafeAdapterFactory(emptyCreaters);
    }

    private final ConstructorConstructor cons;

    private TypeSafeAdapterFactory(Map<Type, InstanceCreator<?>> map) {
        this.cons = new ConstructorConstructor(map);
    }

    private <T> Supplier<T> asSupplier(final TypeToken<T> type) {
        return new Supplier<T>() {
            @Override
            public T get() {
                return cons.get(type).construct();
            }
        };
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<?> raw = type.getRawType();
        Number placeHolder;
        if (raw == String.class) {
            return (TypeAdapter<T>) new SafeStringAdapter(gson.getDelegateAdapter(this, type));
        } else if (Collection.class.isAssignableFrom(raw)) {
            return new SafeCollectionAdapter(gson.getDelegateAdapter(this, type), asSupplier(type));
        } else if (Map.class.isAssignableFrom(raw)) {
            return new SafeMapAdapter(gson.getDelegateAdapter(this, type), asSupplier(type));
        } else if (raw == boolean.class || raw == Boolean.class) {
            return (TypeAdapter<T>) new SafeBooleanAdapter(gson.getDelegateAdapter(this, type));
        } else if ((placeHolder = getPlaceholderNumber(raw)) != null) {
            return (TypeAdapter<T>) new SafeNumberAdapter(gson.getDelegateAdapter(this, type), placeHolder);
        } else if (raw == char.class || raw == Character.class) {
            return (TypeAdapter<T>) new SafeCharAdapter(
                    gson.getDelegateAdapter(this, type),
                    gson.getAdapter(String.class));
        }
        return null;
    }


    static abstract class ForwardingAdapter<T> extends TypeAdapter<T> {

        private final TypeAdapter<T> delegate;

        ForwardingAdapter(TypeAdapter<?> delegate) {
            this.delegate = (TypeAdapter<T>) delegate;
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            delegate.write(out, value);
        }

        @Override
        public T read(JsonReader in) throws IOException {
            return delegate.read(in);
        }
    }

    static class SafeStringAdapter extends ForwardingAdapter<String> {

        SafeStringAdapter(TypeAdapter<?> delegate) {
            super(delegate);
        }

        @Override
        public String read(JsonReader in) throws IOException {
            String str = null;

            try {
                str = super.read(in);
            } catch (RuntimeException ignored) {
                in.skipValue();
            }
            if (str == null) {
                str = "";
            }
            return str;
        }
    }

    static class SafeNumberAdapter extends ForwardingAdapter<Number> {

        private final Number placeHolder;

        SafeNumberAdapter(TypeAdapter<?> delegate, Number placeHolder) {
            super(delegate);
            this.placeHolder = placeHolder;
        }

        @Override
        public Number read(JsonReader in) throws IOException {
            Number number = null;
            try {
                number = super.read(in);
            } catch (RuntimeException ignored) {
                in.skipValue();
            }
            if (number == null) {
                number = placeHolder;
            }
            return number;
        }
    }

    static class SafeBooleanAdapter extends ForwardingAdapter<Boolean> {

        SafeBooleanAdapter(TypeAdapter<?> delegate) {
            super(delegate);
        }

        @Override
        public Boolean read(JsonReader in) throws IOException {
            Boolean bool = null;
            try {
                bool = super.read(in);
            } catch (RuntimeException ignored) {
                in.skipValue();
            }
            if (bool == null) {
                bool = Boolean.FALSE;
            }
            return bool;
        }
    }

    static class SafeCollectionAdapter<E> extends ForwardingAdapter<Collection<E>> {
        private final Supplier<Collection<E>> supplier;

        SafeCollectionAdapter(TypeAdapter<?> delegate, Supplier<Collection<E>> supplier) {
            super(delegate);
            this.supplier = supplier;
        }

        @Override
        public Collection<E> read(JsonReader in) throws IOException {
            Collection<E> collection = null;
            if (in.peek() == JsonToken.BEGIN_ARRAY) {
                collection = super.read(in);
            } else {
                in.skipValue();
            }
            if (collection == null) {
                collection = supplier.get();
            }
            return collection;
        }
    }

    static class SafeMapAdapter<K, V> extends ForwardingAdapter<Map<K, V>> {
        private final Supplier<Map<K, V>> supplier;

        SafeMapAdapter(TypeAdapter<?> delegate, Supplier<Map<K, V>> supplier) {
            super(delegate);
            this.supplier = supplier;
        }

        @Override
        public Map<K, V> read(JsonReader in) throws IOException {
            Map<K, V> map = null;
            if (in.peek() != JsonToken.BEGIN_ARRAY && in.peek() != JsonToken.BEGIN_OBJECT) {
                in.skipValue();
            } else {
                try {
                    map = super.read(in);
                } catch (JsonParseException e) {
                    if (e.getMessage() != null && e.getMessage().startsWith("duplicate key")) {
                        // Array: [ [ 'k1': 'v1'], ['k2': 'v2'] ]
                        if (in.peek() == JsonToken.END_ARRAY) {
                            in.endArray();
                            while (in.peek() != JsonToken.END_ARRAY) {
                                in.beginArray();
                                in.skipValue();
                                in.skipValue();
                                in.endArray();
                            }
                            in.endArray();
                        } else { // Object: { 'k1': 'v1', 'k2': 'v2'}
                            while (in.peek() != JsonToken.END_OBJECT) {
                                in.skipValue();
                                in.skipValue();
                            }
                            in.endObject();
                        }
                    } else {
                        throw e;
                    }
                }
            }
            if (map == null) {
                map = supplier.get();
            }
            return map;
        }
    }

    static class SafeCharAdapter extends ForwardingAdapter<Character> {

        private final TypeAdapter<String> stringAdapter;

        SafeCharAdapter(TypeAdapter<?> delegate, TypeAdapter<String> stringAdapter) {
            super(delegate);
            this.stringAdapter = stringAdapter;
        }

        @Override
        public Character read(JsonReader in) throws IOException {
            String str = stringAdapter.read(in);
            if (!str.isEmpty()) {
                return str.charAt(0);
            }
            return (char) 0;
        }
    }

    private interface Supplier<T> {
        T get();
    }
}
