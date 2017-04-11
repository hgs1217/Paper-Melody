package com.papermelody.activity;

import android.os.Bundle;

import com.papermelody.R;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class CalibrationActivity extends BaseActivity {
    /**
     * 用例：演奏乐器（流程四）
     * 标定界面，用于标定演奏纸的演奏合法位置
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_calibration;
    }
}
