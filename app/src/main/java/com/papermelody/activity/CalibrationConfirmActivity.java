package com.papermelody.activity;

import android.os.Bundle;
import android.widget.ImageView;

import com.papermelody.R;
import com.papermelody.widget.CalibrationView;

import butterknife.BindView;

/**
 * Created by HgS_1217_ on 2017/5/11.
 */

public class CalibrationConfirmActivity extends BaseActivity {
    /**
     * 用例：演奏乐器（流程四）
     * 标定确认界面，通知演奏纸演奏的判定位置，用户可以选择确认标定成功，也可以选择重新标定
     */

    @BindView(R.id.canvas_calibration_confirm)
    CalibrationView canvasCalibrationConfirm;
    @BindView(R.id.img_calibration)
    ImageView imgCalibration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initNextButton();
    }

    /*private void initNextButton() {
        btnCalibration.setOnClickListener((View v)->{
            if (canNext) {
                Intent intent = new Intent(this, PlayActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }*/

    @Override
    protected int getContentViewId() {
        return R.layout.activity_calibration_confirm;
    }
}
