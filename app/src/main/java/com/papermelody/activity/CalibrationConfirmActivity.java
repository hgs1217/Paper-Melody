package com.papermelody.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.papermelody.R;
import com.papermelody.core.calibration.Calibration;
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
    @BindView(R.id.btn_calibration_cancel)
    Button btnCalibrationCancel;
    @BindView(R.id.btn_calibration_complete)
    Button btnCalibrationComplete;

    private Calibration.CalibrationResult calibrationResult;
    private int height;
    private int width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        calibrationResult = (Calibration.CalibrationResult) intent.getSerializableExtra(CalibrationActivity.EXTRA_RESULT);
        height = intent.getIntExtra(CalibrationActivity.EXTRA_HEIGHT, 960);
        width = intent.getIntExtra(CalibrationActivity.EXTRA_WIDTH, 1280);

        initView();
    }

    private void initView() {
        btnCalibrationComplete.setOnClickListener((View v)->{
            Intent intent = new Intent(this, PlayActivity.class);
            startActivity(intent);
            finish();
        });

        btnCalibrationCancel.setOnClickListener((View v)->{
            Intent intent = new Intent(this, CalibrationActivity.class);
            startActivity(intent);
            finish();
        });

        canvasCalibrationConfirm.updateCalibrationCoordinates(calibrationResult, height, width);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_calibration_confirm;
    }
}
