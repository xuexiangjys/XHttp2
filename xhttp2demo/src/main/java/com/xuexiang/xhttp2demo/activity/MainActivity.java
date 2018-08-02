package com.xuexiang.xhttp2demo.activity;

import android.os.Bundle;

import com.xuexiang.xhttp2demo.fragment.MainFragment;
import com.xuexiang.xpage.base.XPageActivity;

/**
 * ================================================
 * <p>
 * 基础的壳Activity，Fragment的载体 <br>
 * Created by XAndroidTemplate <br>
 * <a href="mailto:xuexiangjys@gmail.com">Contact me</a>
 * <a href="https://github.com/xuexiangjys">Follow me</a>
 * </p>
 * ================================================
 */
public class MainActivity extends XPageActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openPage(MainFragment.class);
    }
}

