package com.papermelody.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.papermelody.R;
import com.papermelody.core.calibration.CalibrationResult;
import com.papermelody.util.ImageProcessor;
import com.papermelody.util.ImageUtil;
import com.papermelody.util.ToastUtil;
import com.papermelody.util.ViewUtil;
import com.papermelody.widget.CalibrationView;

import org.opencv.core.Mat;

import java.util.Arrays;

import butterknife.BindView;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class CalibrationActivity extends BaseActivity {

    /**
     * 用例：演奏乐器（流程四）
     * 标定界面，用于标定演奏纸的演奏合法位置
     * http://blog.sina.com.cn/s/blog_46e3af5b0101cehh.html
     * http://blog.csdn.net/u013869488/article/details/49853217
     * http://blog.csdn.net/sinat_29384657/article/details/52188723
     *
     * http://blog.csdn.net/yanzi1225627/article/details/38098729
     */

    @BindView(R.id.view_calibration)
    SurfaceView viewCalibration;
    @BindView(R.id.canvas_calibration)
    CalibrationView canvasCalibration;
    @BindView(R.id.img_calibration)
    ImageView imgCalibration;
    @BindView(R.id.btn_calibration_cancel)
    Button btnCalibrationCancel;
    @BindView(R.id.btn_calibration_complete)
    Button btnCalibrationComplete;
    @BindView(R.id.layout_container)
    LinearLayout layoutContainer;
    @BindView(R.id.layout_legal)
    LinearLayout layoutLegal;

    public static final String EXTRA_RESULT = "EXTRA_RESULT";

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    ///为了使照片竖直显示
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private CameraManager cameraManager;
    private CameraDevice cameraDevice;
    private SurfaceHolder surfaceHolder;
    private Handler childHandler, mainHandler;
    private String cameraID;
    private CameraCaptureSession cameraCaptureSession;
    private ImageReader imageReader;
    private CalibrationResult calibrationResult;

    private int targetHeightStart = 0;
    private int targetHeightEnd = 1000;

    private boolean canCalibration = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initView();
    }

    private void initSurfaceSize(double scalar) {
        /* 横屏导致长宽交换 */
        int width = ViewUtil.getScreenWidth(this);
        int height = (int) (width / scalar);;
        FrameLayout.LayoutParams lp;
        if (Build.VERSION.SDK_INT >= 24) {
            // TODO: Android 7.0 上貌似有自动图片适配功能，暂时不太确定，需要更多的测试情况
            height = ViewUtil.getScreenHeight(this);
        }
        lp = new FrameLayout.LayoutParams(width, height);
        lp.gravity = Gravity.CENTER;
        viewCalibration.setLayoutParams(lp);
        imgCalibration.setLayoutParams(lp);
        canvasCalibration.setSize(width, height);
    }

    private void initView() {
        /**
         * 初始化界面上的文字标签、按键响应等等
         */

        initViewStatus();

        surfaceHolder = viewCalibration.getHolder();
        surfaceHolder.setKeepScreenOn(true);
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                initCamera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (cameraDevice != null) {
                    cameraDevice.close();
                    cameraDevice = null;
                }
            }
        });

        btnCalibrationComplete.setOnClickListener((View v) -> {
            //Intent intent = new Intent(this, PlayActivity.class);
            Intent intent = new Intent(this, PlayActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(EXTRA_RESULT, calibrationResult);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        });

        btnCalibrationCancel.setOnClickListener((View v) -> {
            initViewStatus();
        });
    }

    private void initViewStatus() {
        /**
         * 标定状态的界面布局，隐藏一些按钮
         */

        viewCalibration.setVisibility(View.VISIBLE);
        layoutContainer.setVisibility(View.VISIBLE);
        imgCalibration.setVisibility(View.GONE);
        btnCalibrationCancel.setVisibility(View.GONE);
        btnCalibrationComplete.setVisibility(View.GONE);
        canCalibration = true;
    }

    private void processImage(Image image) {
        /**
         * 照片处理
         */

        Mat mat = ImageUtil.imageToBgr(image);
        calibrationResult = ImageProcessor.getCalibrationCoordinate(mat, targetHeightStart, targetHeightEnd);

        canvasCalibration.updateCalibrationCoordinates(calibrationResult, CalibrationActivity.this);

        if (ImageProcessor.getCalibrationStatus(calibrationResult)) {
            Bitmap bitmap = ImageUtil.imageToBitmap(mat);

            viewCalibration.setVisibility(View.GONE);
            layoutContainer.setVisibility(View.GONE);
            imgCalibration.setVisibility(View.VISIBLE);
            btnCalibrationCancel.setVisibility(View.VISIBLE);
            btnCalibrationComplete.setVisibility(View.VISIBLE);

            imgCalibration.setImageBitmap(bitmap);
            canCalibration = false;
        }
    }

    private void initCamera() {
        /**
         * 初始化相机，在这里设置相机的预览获取，尺寸等等
         */

        HandlerThread handlerThread = new HandlerThread("Camera2");
        handlerThread.start();
        childHandler = new Handler(handlerThread.getLooper());
        mainHandler = new Handler(getMainLooper());
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        cameraID = String.valueOf(CameraCharacteristics.LENS_FACING_BACK);  //前摄像头
        ImageProcessor.initProcessor();

        // 设置imageReader的尺寸与采样频率
        try {
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraID);
            StreamConfigurationMap map = characteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            // 获取照相机可用的符合条件的最小像素图片
            Size relativeMin = ImageUtil.getRelativeMinSize(Arrays.asList(map.getOutputSizes(ImageFormat.YUV_420_888)));
            initSurfaceSize((double) relativeMin.getWidth()/relativeMin.getHeight());
            imageReader = ImageReader.newInstance(relativeMin.getWidth(), relativeMin.getHeight(), ImageFormat.YUV_420_888, 5);
            canvasCalibration.setPhotoSize(relativeMin.getWidth(), relativeMin.getHeight());

            // 计算合法区域范围
            int targetHeightStart = getHeightRelativeCoordinate(ViewUtil.getScreenHeight
                    (CalibrationActivity.this) - layoutLegal.getHeight(), relativeMin.getHeight());
            int targetHeightEnd = getHeightRelativeCoordinate(ViewUtil.getScreenHeight
                    (CalibrationActivity.this), relativeMin.getHeight());

            Log.d("TESTTAR", targetHeightStart+" "+targetHeightEnd);
            imageReader.setOnImageAvailableListener((reader) -> {
                    if (!canCalibration) {
                        return;
                    }
                    Image image = null;
                    try {
                        image = imageReader.acquireLatestImage();
                        if (image == null) {
                            return;
                        }

                        processImage(image);

//                        cnt++;
//                        Log.d("CALIBRATION", "imgReader" + cnt);
                    } finally {
                        if (image != null) {
                            image.close();
                        }
                    }
            }, mainHandler);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            cameraManager.openCamera(cameraID, deviceStateCallback, mainHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CameraDevice.StateCallback deviceStateCallback = new CameraDevice.StateCallback() {
        /* 摄像头创建监听 */

        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            ToastUtil.showShort("开启成功");
            cameraDevice = camera;
            takePreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            if (null != cameraDevice) {
                cameraDevice.close();
                cameraDevice = null;
            }
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            ToastUtil.showShort("摄像头开启失败");
        }
    };

    private void takePreview() {
        /* 开启预览 */

        try {
            // 创建预览需要的CaptureRequest.Builder
            final CaptureRequest.Builder previewRequestBuilder =
                    cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            // 将SurfaceView的surface作为CaptureRequest.Builder的目标
            previewRequestBuilder.addTarget(surfaceHolder.getSurface());
            // 将imageReader的surface作为CaptureRequest.Builder的目标
            previewRequestBuilder.addTarget(imageReader.getSurface());
            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
            cameraDevice.createCaptureSession(Arrays.asList(surfaceHolder.getSurface(),
                    imageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    if (null == cameraDevice) {
                        return;
                    }
                    // 当摄像头已经准备好时，开始显示预览
                    cameraCaptureSession = session;
                    try {
                        // 自动对焦
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        // 打开闪光灯
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                                CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                        // 显示预览
                        CaptureRequest previewRequest = previewRequestBuilder.build();
                        cameraCaptureSession.setRepeatingRequest(previewRequest, null, childHandler);
                        // 获取手机方向
                        int rotation = ViewUtil.getWindowRotation(CalibrationActivity.this);
                        // 根据设备方向计算设置照片的方向
                        previewRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    ToastUtil.showShort("配置失败");
                }
            }, childHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private int getHeightRelativeCoordinate(int screenY, int photoHeight) {
        double offset = (imgCalibration.getHeight() - ViewUtil.getScreenHeight(CalibrationActivity.this)) / 2.0;
        double targetHeightStart = (screenY + offset) / (double) imgCalibration.getHeight() * photoHeight;
        return (int) targetHeightStart;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_calibration;
    }
}
