package com.xuexiang.xhttp2demo;

import com.xuexiang.xhttp2.utils.HttpUtils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);

        String url = "http://192.168.5.11/api/test?token=123412443&timeStamp=111111&sign=xuexiang";

        System.out.println(HttpUtils.updateUrlParams(url, "token", "edse342dsfw522r"));
    }
}