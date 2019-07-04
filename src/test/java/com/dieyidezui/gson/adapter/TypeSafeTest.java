package com.dieyidezui.gson.adapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class TypeSafeTest {

    private static TypedTester tester = new TypedTester();

    @Test
    public void testObject() {
        tester.assertIt("obj", "\"\"", null);
        tester.assertIt("obj", "{}", new Person());
        tester.assertIt("obj", "[]", null);
        tester.assertIt("obj", "null", null);
        tester.assertIt("obj", "1", null);
        tester.assertIt("obj", 21321321321321213L, null);
    }

    @Test
    public void testByte() {
        tester.assertIt("b", 33, (byte) 33);
        tester.assertIt("b", 21321321321321213L, (byte) 0);
        tester.assertIt("b", "{}", (byte) 0);
        tester.assertIt("b", "true", (byte) 0);
        tester.assertIt("b", "'true'", (byte) 0);
        tester.assertIt("b", 1.232323311421412, (byte) 0);
        tester.assertIt("b", 1 << 8 | 1, (byte) 1);
        tester.assertIt("b", "[]", (byte) 0);
        tester.assertIt("b", "null", (byte) 0);
        tester.assertIt("b", "'null'", (byte) 0);
        tester.assertIt("b", "\"null\"", (byte) 0);
    }

    @Test
    public void testShort() {
        tester.assertIt("s", 33, (short) 33);
        tester.assertIt("s", 21321321321321213L, (short) 0);
        tester.assertIt("s", "{}", (short) 0);
        tester.assertIt("s", "true", (short) 0);
        tester.assertIt("s", "'true'", (short) 0);
        tester.assertIt("s", 1.232323311421412, (short) 0);
        tester.assertIt("s", 1 << 16 | 1, (short) 1);
        tester.assertIt("s", "[]", (short) 0);
        tester.assertIt("s", "null", (short) 0);
        tester.assertIt("s", "'null'", (short) 0);
        tester.assertIt("s", "\"null\"", (short) 0);
    }

    @Test
    public void testInteger() {
        tester.assertIt("i", 33, 33);
        tester.assertIt("i", 21321321321321213L, 0);
        tester.assertIt("i", "{}", 0);
        tester.assertIt("i", "true", 0);
        tester.assertIt("i", "'true'", 0);
        tester.assertIt("i", 1.232323311421412, 0);
        // gson throws exception when int overflow,
        // but doesn't throw when short & byte.
        tester.assertIt("i", 1L << 32 | 1, 0);
        tester.assertIt("i", "[]", 0);
        tester.assertIt("i", "null", 0);
        tester.assertIt("i", "'null'", 0);
        tester.assertIt("i", "\"null\"", 0);
    }

    @Test
    public void testLong() {
        tester.assertIt("l", 33, 33L);
        tester.assertIt("l", 21321321321321213L, 21321321321321213L);
        tester.assertIt("l", "{}", 0L);
        tester.assertIt("l", "true", 0L);
        tester.assertIt("l", "'true'", 0L);
        tester.assertIt("l", 1.232323311421412, 0L);
        tester.assertIt("l", 1L << 32 | 1, 1L << 32 | 1);
        tester.assertIt("l", "[]", 0L);
        tester.assertIt("l", "null", 0L);
        tester.assertIt("l", "'null'", 0L);
        tester.assertIt("l", "\"null\"", 0L);
    }

    @Test
    public void testFloat() {
        tester.assertIt("f", 33, 33f);
        tester.assertIt("f", 21321321321321213L, (float) 21321321321321213L);
        tester.assertIt("f", "{}", 0f);
        tester.assertIt("f", "true", 0f);
        tester.assertIt("f", "'true'", 0f);
        tester.assertIt("f", 1.232323311421412, 1.232323311421412f);
        tester.assertIt("f", 1L << 32 | 1, (float) (1L << 32 | 1));
        tester.assertIt("f", "[]", 0f);
        tester.assertIt("f", "null", 0f);
        tester.assertIt("f", "'null'", 0f);
        tester.assertIt("f", "\"null\"", 0f);
    }

    @Test
    public void testDouble() {
        tester.assertIt("d", 33, 33.0);
        tester.assertIt("d", 21321321321321213L, 21321321321321213.0);
        tester.assertIt("d", "{}", 0.0);
        tester.assertIt("d", "true", 0.0);
        tester.assertIt("d", "'true'", 0.0);
        tester.assertIt("d", 1.232323311421412, 1.232323311421412);
        tester.assertIt("d", 1L << 32 | 1, (double) (1L << 32 | 1));
        tester.assertIt("d", "[]", 0.0);
        tester.assertIt("d", "null", 0.0);
        tester.assertIt("d", "'null'", 0.0);
        tester.assertIt("d", "\"null\"", 0.0);
    }

    @Test
    public void testCharacter() {
        tester.assertIt("c", 33, '3');
        tester.assertIt("c", 21321321321321213L, '2');
        tester.assertIt("c", "{}", (char) 0);
        tester.assertIt("c", "true", 't');
        tester.assertIt("c", "'true'", 't');
        tester.assertIt("c", 1.232323311421412, '1');
        tester.assertIt("c", "[]", (char) 0);
        tester.assertIt("c", "null", (char) 0);
        tester.assertIt("c", "'null'", 'n');
        tester.assertIt("c", "\"null\"", 'n');
    }

    @Test
    public void testBoolean() {
        tester.assertIt("bo", 33, false);
        tester.assertIt("bo", 21321321321321213L, false);
        tester.assertIt("bo", "{}", false);
        tester.assertIt("bo", "true", true);
        tester.assertIt("bo", "'true'", true);
        tester.assertIt("bo", 1.232323311421412, false);
        tester.assertIt("bo", "[]", false);
        tester.assertIt("bo", "null", false);
        tester.assertIt("bo", "'null'", false);
        tester.assertIt("bo", "\"null\"", false);
    }

    @Test
    public void testList() {
        List empty = Collections.emptyList();
        tester.assertIt("list", 33, empty);
        tester.assertIt("list", 21321321321321213L, empty);
        tester.assertIt("list", "{}", empty);
        tester.assertIt("list", "true", empty);
        tester.assertIt("list", "'true'", empty);
        tester.assertIt("list", 1.232323311421412, empty);
        tester.assertIt("list", "[]", empty);
        tester.assertIt("list", "[123, true]", Arrays.asList(123.0, true));
        tester.assertIt("list", "null", empty);
        tester.assertIt("list", "'null'", empty);
        tester.assertIt("list", "\"null\"", empty);
    }

    @Test
    public void testMap() {
        Map empty = Collections.emptyMap();
        tester.assertIt("map", 33, empty);
        tester.assertIt("map", 21321321321321213L, empty);
        tester.assertIt("map", "{}", empty);
        tester.assertIt("map", "{k1:v1}", Collections.singletonMap("k1", "v1"));
        tester.assertIt("map", "{k1:v1, k1:v2}", empty);
        tester.assertIt("map", "{k1:v1, k1:v2, kk:vv}", empty);
        try {
            tester.assertIt("map", "{k1:v1, k1:v2, kk}", empty);
            throw new AssertionError();
        } catch (JsonSyntaxException ignored) {
        }
        tester.assertIt("map", "true", empty);
        tester.assertIt("map", "'true'", empty);
        tester.assertIt("map", 1.232323311421412, empty);
        tester.assertIt("map", "[]", empty);
        tester.assertIt("map", "[[123, true]]", Collections.singletonMap(123.0, true));
        tester.assertIt("map", "[[123, true], [123, true]]", empty);
        try {
            tester.assertIt("map", "[[123, true], [123, true], [], [zz, oo]]", empty);
            throw new AssertionError();
        } catch (JsonSyntaxException ignored) {
        }
        tester.assertIt("map", "null", empty);
        tester.assertIt("map", "'null'", empty);
        tester.assertIt("map", "\"null\"", empty);
    }

    @Test
    public void testArray() {
        Object[] empty1 = new Object[0];
        Object[][] empty2 = new Object[0][0];
        tester.assertArrayEquals("ar", "null", empty2);
        tester.assertArrayEquals("ar", "'null'", empty2);
        tester.assertArrayEquals("ar", "\"null\"", empty2);
        tester.assertArrayEquals("ar", "true", empty2);
        tester.assertArrayEquals("ar", "1", empty2);
        tester.assertArrayEquals("ar", "2.0", empty2);
        tester.assertArrayEquals("ar", "{}", empty2);
        tester.assertArrayEquals("ar", "[]", empty2);
        tester.assertArrayEquals("ar", "[1, 2, 3]", new Object[]{empty1, empty1, empty1});
        tester.assertArrayEquals("ar", "[[1], 2, 3]", new Object[]{new Object[]{1.0}, empty1, empty1});
    }


    private static class TypedTester {
        private final Gson safe = initGson();

        private Gson initGson() {
            GsonBuilder gsonBuilder = new GsonBuilder();
            return gsonBuilder
                    .registerTypeAdapterFactory(TypeSafeAdapterFactory.newInstance())
                    .create();
        }

        void assertIt(String name, Object input, Object expect) {
            assertThat(name, input, expect, new BiConsumer() {
                @Override
                public void accept(Object o, Object o2) {
                    Assert.assertEquals(o, o2);
                }
            });
        }

        void assertArrayEquals(String name, Object input, Object expect) {
            assertThat(name, input, expect, new BiConsumer() {

                @Override
                public void accept(Object o, Object o2) {
                    Assert.assertArrayEquals((Object[]) o, (Object[]) o2);
                }
            });
        }

        void assertThat(String name, Object input, Object expect, BiConsumer consumer) {
            TestBean bean = safe.fromJson("{'" + name + "' : " + input + "}", TestBean.class);
            try {
                consumer.accept(expect, TestBean.class.getDeclaredField(name)
                        .get(bean));
            } catch (Exception e) {
                throw new AssertionError();
            }
        }
    }


    static class TestBean {
        Byte b;
        Short s;
        Integer i;
        Float f;
        Double d;
        Long l;
        Character c;
        Boolean bo;
        String str;
        List list;
        Map map;
        Object[][] ar;
        Person obj;

        @Override
        public String toString() {
            return "TestBean{" +
                    "b=" + b +
                    ", s=" + s +
                    ", i=" + i +
                    ", f=" + f +
                    ", d=" + d +
                    ", l=" + l +
                    ", c=" + c +
                    ", bo=" + bo +
                    ", str='" + str + '\'' +
                    ", list=" + list +
                    ", map=" + map +
                    ", ar=" + Arrays.deepToString(ar) +
                    ", obj=" + ((obj == null) ? "null" : "{}") +
                    '}';
        }
    }

    static class Person {

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Person;
        }
    }
}
