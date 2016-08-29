package com.lizy.retrofit;

import com.google.common.reflect.TypeToken;

import org.junit.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static com.lizy.retrofit.CallAdapter.Factory.getParameterUpperBound;
import static com.lizy.retrofit.CallAdapter.Factory.getRawType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;


/**
 * Created by lizy on 16-8-26.
 */
public class CallAdapterTest {
    @Test
    public void testGetRawType() throws Exception {
    }

    @Test public void paramerizedTypes() {
        ParameterizedType one = (ParameterizedType) new TypeToken<List<String>>() {}.getType();
        assertThat(getParameterUpperBound(0, one)).isSameAs(String.class);

        ParameterizedType two = (ParameterizedType) new TypeToken<Map<String, String>>() {}.getType();
        assertThat(getParameterUpperBound(0, two)).isSameAs(String.class);
        assertThat(getParameterUpperBound(1, two)).isSameAs(String.class);

        ParameterizedType wild = (ParameterizedType) new TypeToken<List<? extends CharSequence>>() {
        }.getType();
        assertThat(getParameterUpperBound(0, wild)).isSameAs(CharSequence.class);
    }

    @Test public void rawTypeThrowsNull() {
        try {
            getRawType(null);
            fail("should get exception");
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("type == null");
        }
    }

    @Test public void rawType() throws NoSuchMethodException {
        assertThat(getRawType(String.class)).isSameAs(String.class);
        Type listOfString = new TypeToken<List<String>>() {}.getType();
        assertThat(getRawType(listOfString)).isSameAs(List.class);

        Type listOfArray = new TypeToken<String[]>() {}.getType();
        assertThat(getRawType(listOfArray)).isSameAs(String[].class);

        Type wild = ((ParameterizedType)new TypeToken<List<? extends CharSequence>>() {}.getType())
                .getActualTypeArguments()[0];
        assertThat(getRawType(wild)).isSameAs(CharSequence.class);

        Type wildParam = ((ParameterizedType) new TypeToken<List<? extends List<String>>>() {
        }.getType()).getActualTypeArguments()[0];
        assertThat(getRawType(wildParam)).isSameAs(List.class);

        Type typeVar = A.class.getDeclaredMethod("method").getGenericReturnType();
        assertThat(getRawType(typeVar)).isSameAs(Object.class);
    }

    static class A<T> {
        T method() {
            return null;
        }
    }
}
