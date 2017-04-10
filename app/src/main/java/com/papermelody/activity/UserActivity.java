package com.papermelody.activity;

import android.os.Bundle;

import com.papermelody.R;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class UserActivity extends BaseActivity {
    /**
     * 用例：无
     * 用户页面，包括个人信息，已上传作品，收藏作品等等
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_user;
    }
}
