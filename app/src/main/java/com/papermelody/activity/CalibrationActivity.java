package com.papermelody.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
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
import com.papermelody.widget.AutoFitTextureView;
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
     *
     * 预览实现参考Google官方Camera2 API sample
     * link: https://github.com/googlesamples/android-Camera2Basic
     */

    /**
     * viewCalibration：用于放置预览流
     */
    @BindView(R.id.view_calibration)
    AutoFitTextureView viewCalibration;

    /**
     * canvasCalibration：作为画布用来画标定获得的坐标
     */
    @BindView(R.id.canvas_calibration)
    CalibrationView canvasCalibration;

    /**
     * imgCalibration：用于装载最后标定成功的图片
     */
    @BindView(R.id.img_calibration)
    ImageView imgCalibration;

    /**
     * btnCalibrationCancel：标定成功后点击可进行重新标定
     */
    @BindView(R.id.btn_calibration_cancel)
    Button btnCalibrationCancel;

    /**
     * btnCalibrationComplete：标定成功后点击可进入到演奏页面
     */
    @BindView(R.id.btn_calibration_complete)
    Button btnCalibrationComplete;

    /**
     * layoutContainer：标定不合法区域，会被涂上一些黑色显示
     */
    @BindView(R.id.layout_container)
    LinearLayout layoutContainer;

    /**
     * layoutLegal：标定合法区域
     */
    @BindView(R.id.layout_legal)
    LinearLayout layoutLegal;

    private static final String TAG = "CalibrationAct";

    private static final int MAX_PREVIEW_WIDTH = 1920;
    private static final int MAX_PREVIEW_HEIGHT = 1080;

    /**
     * 用于使图片竖直显示所建立的一些方向数据
     */
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private CameraManager cameraManager;
    private CameraDevice cameraDevice;

    /**
     * 获取照片图像数据用到的子线程
     */
    private HandlerThread backgroundThread;
    private Handler backgroundHandler;

    /**
     * Activity主线程
     */
    private Handler mainHandler;

    private String cameraID;
    private CameraCaptureSession cameraCaptureSession;

    /**
     * imageReader：用于装载预览的图片流
     */
    private ImageReader imageReader;

    /**
     * calibrationResult：放置了对图片进行标定处理后的结果，包括了四个边界点和标定状态
     */
    private CalibrationResult calibrationResult;

    /**
     * targetHeightStart和targetHeightEnd：分别是合法区域的纵坐标（单位px）
     */
    private int targetHeightStart = 0;
    private int targetHeightEnd = 1000;

    /**
     * canCalibration: 为true时可以开始标定，为false时标定停止
     */
    private boolean canCalibration = true;

    private int cnt = 0;

    /**
     * 用来传递模式的参数
     */
    private int mode;
    private int instrument;
    private int category;
    private int opern;

    /**
     * previewSize: 预览区域的尺寸
     */
    private Size previewSize;

    private CaptureRequest.Builder previewRequestBuilder;

    private final TextureView.SurfaceTextureListener surfaceTextureListener =
            new TextureView.SurfaceTextureListener() {

                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
                    openCamera(width, height);
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
                    configureTransform(width, height);
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
                    return true;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture texture) { }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mode = intent.getIntExtra(PlayActivity.EXTRA_MODE, 0);
        instrument = intent.getIntExtra(PlayActivity.EXTRA_INSTRUMENT, 0);
        category = intent.getIntExtra(PlayActivity.EXTRA_CATIGORY, 0);
        opern = intent.getIntExtra(PlayActivity.EXTRA_OPERN, 0);

//        instrument = Instrument.INSTRUMENT_FLUTE;
//        category = Instrument.INSTRUMENT_FLUTE7;

        // 给ImageProcessor绑定乐器种类
        ImageProcessor.setInstrumentType(category);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // 开启子线程，绑定TextureView的响应事件
        startBackgroundThread();
        viewCalibration.setSurfaceTextureListener(surfaceTextureListener);

        initView();
    }

    /**
     * 初始化界面上的文字标签、按键响应等等
     */
    private void initView() {

        initViewStatus();

        btnCalibrationComplete.setOnClickListener((View v) -> {
            Intent intent = new Intent(this, PlayActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(PlayActivity.EXTRA_RESULT, calibrationResult);
            intent.putExtras(bundle);
            intent.putExtra(PlayActivity.EXTRA_MODE, mode);
            intent.putExtra(PlayActivity.EXTRA_OPERN, opern);
            intent.putExtra(PlayActivity.EXTRA_INSTRUMENT, instrument);
            intent.putExtra(PlayActivity.EXTRA_CATIGORY, category);
            startActivity(intent);
            finish();
        });

        btnCalibrationCancel.setOnClickListener((View v) -> {
            initViewStatus();
        });
    }

    /**
     * 标定状态的界面布局，隐藏一些按钮
     */
    private void initViewStatus() {

        viewCalibration.setVisibility(View.VISIBLE);
        layoutContainer.setVisibility(View.VISIBLE);
        imgCalibration.setVisibility(View.GONE);
        btnCalibrationCancel.setVisibility(View.GONE);
        btnCalibrationComplete.setVisibility(View.GONE);
        canCalibration = true;
    }

    /**
     * 照片处理
     */
    private void processImage(Image image) {

        // FIXME: 标定原帧率卡顿严重，暂时调慢了标定视频帧率，如果标定算法能优化则优化，不能的话就这样吧
        cnt++;
        if (cnt % 2 != 0) {
            return;
        }

        Mat mat = ImageUtil.imageToBgr(image);
        calibrationResult = ImageProcessor.getCalibrationCoordinate(mat, targetHeightStart, targetHeightEnd);

        canvasCalibration.updateCalibrationCoordinates(calibrationResult, CalibrationActivity.this, false);

        if (ImageProcessor.getCalibrationStatus(calibrationResult)) {
            /*Log.d("TESThistres",calibrationResult.getLeftLowX()+"");
            Log.d("TESThistres",calibrationResult.getLeftLowY()+"");
            Log.d("TESThistres",calibrationResult.getLeftUpX()+"");
            Log.d("TESThistres",calibrationResult.getLeftUpY()+"");
            Log.d("TESThistres",calibrationResult.getRightLowX()+"");
            Log.d("TESThistres",calibrationResult.getRightLowY()+"");
            Log.d("TESThistres",calibrationResult.getRightUpX()+"");
            Log.d("TESThistres",calibrationResult.getRightUpY()+"");
            Log.d("TESThistres",calibrationResult.isFlag()+"");*/
            canvasCalibration.updateCalibrationCoordinates(calibrationResult, CalibrationActivity.this, true);

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

    /**
     * 相机开启
     * @param width     TextureView的宽度
     * @param height    TextureView的高度
     */
    private void openCamera(int width, int height) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
//            requestCameraPermission();
            return;
        }
        setUpCameraOutputs(width, height);
        configureTransform(width, height);
        try {
            cameraManager.openCamera(cameraID, deviceStateCallback, mainHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用来设置相机的输出选项，包括图像尺寸，图像获取，图像的标定操作等等
     * @param width     TextureView的宽度
     * @param height    TextureView的高度
     */
    private void setUpCameraOutputs(int width, int height) {

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        cameraID = String.valueOf(CameraCharacteristics.LENS_FACING_BACK);  //前摄像头
        ImageProcessor.initProcessor();

        try {
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraID);
            StreamConfigurationMap map = characteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            // 获取最贴近手机屏幕长宽比的照片，若存在多张，则选取一个大于640*480的最小尺寸, YUV_420_888格式是预览流格式
            Size relativeMin = ImageUtil.getRelativeMinSize(Arrays.asList(map.getOutputSizes(ImageFormat.YUV_420_888)),
                                                            viewCalibration.getWidth(), viewCalibration.getHeight());

            Log.d(TAG, relativeMin.getWidth()+" "+relativeMin.getHeight());

            canvasCalibration.setPhotoSize(relativeMin.getWidth(), relativeMin.getHeight());

            imageReader = ImageReader.newInstance(relativeMin.getWidth(), relativeMin.getHeight(),
                                            ImageFormat.YUV_420_888, 1);
            imageReader.setOnImageAvailableListener((reader) -> {
                /* 当获取到图片后，对图片进行操作 */

                Image image = null;
                try {
                    image = imageReader.acquireLatestImage();
                    if (!canCalibration || image == null) {
                        return;
                    }
                    processImage(image);
                } finally {
                    if (image != null) {
                        image.close();
                    }
                }
            }, mainHandler);

            // 查看当前手机朝向来决定是否要对调TextureView的长宽
            int displayRotation = getWindowManager().getDefaultDisplay().getRotation();
            int sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            boolean swappedDimensions = false;
            switch (displayRotation) {
                case Surface.ROTATION_0:
                case Surface.ROTATION_180:
                    if (sensorOrientation == 90 || sensorOrientation == 270) {
                        swappedDimensions = true;
                    }
                    break;
                case Surface.ROTATION_90:
                case Surface.ROTATION_270:
                    if (sensorOrientation == 0 || sensorOrientation == 180) {
                        swappedDimensions = true;
                    }
                    break;
                default:
                    Log.e(TAG, "Display rotation is invalid: " + displayRotation);
            }

            Point displaySize = new Point();
            getWindowManager().getDefaultDisplay().getSize(displaySize);
            int rotatedPreviewWidth = width;
            int rotatedPreviewHeight = height;
            int maxPreviewWidth = displaySize.x;
            int maxPreviewHeight = displaySize.y;

            if (swappedDimensions) {
                rotatedPreviewWidth = height;
                rotatedPreviewHeight = width;
                maxPreviewWidth = displaySize.y;
                maxPreviewHeight = displaySize.x;
            }

            if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                maxPreviewWidth = MAX_PREVIEW_WIDTH;
            }
            if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                maxPreviewHeight = MAX_PREVIEW_HEIGHT;
            }

            // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
            // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
            // garbage capture data.
            previewSize = ImageUtil.chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                    rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth, maxPreviewHeight, relativeMin);

            Log.d(TAG, previewSize.getWidth()+" "+previewSize.getHeight());
            viewCalibration.setAspectRatio(previewSize.getWidth(), previewSize.getHeight());

            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(viewCalibration.getWidth(), viewCalibration.getHeight());
            lp.gravity = Gravity.CENTER;
            canvasCalibration.setLayoutParams(lp);
            imgCalibration.setLayoutParams(lp);
            canvasCalibration.setSize(viewCalibration.getWidth(), viewCalibration.getHeight());

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 当手机屏幕的朝向改变时，要对获取到的视频流进行方向上的调整
     * @param viewWidth     TextureView的宽度
     * @param viewHeight    TextureView的高度
     */
    private void configureTransform(int viewWidth, int viewHeight) {

        if (viewCalibration == null || previewSize == null) {
            return;
        }
        int rotation = getWindowManager().getDefaultDisplay().getRotation();

        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max((float) viewHeight / previewSize.getHeight(),
                    (float) viewWidth / previewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        viewCalibration.setTransform(matrix);
    }

    private final CameraDevice.StateCallback deviceStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice device) {
            ToastUtil.showShort("开启成功");
            cameraDevice = device;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice device) {
            device.close();
            cameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice device, int error) {
        }
    };

    /**
     * 预览状态的创建
     */
    private void createCameraPreviewSession() {

        try {
            SurfaceTexture texture = viewCalibration.getSurfaceTexture();
            assert texture != null;

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());

            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            // 将TextureView的surface作为CaptureRequest.Builder的目标
            previewRequestBuilder.addTarget(surface);
            // 将imageReader的surface作为CaptureRequest.Builder的目标
            previewRequestBuilder.addTarget(imageReader.getSurface());

            // Here, we create a CameraCaptureSession for camera preview.
            cameraDevice.createCaptureSession(Arrays.asList(surface, imageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            // The camera is already closed
                            if (cameraDevice == null) {
                                return;
                            }

                            // When the session is ready, we start displaying the preview.
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
                                cameraCaptureSession.setRepeatingRequest(previewRequest, null, backgroundHandler);
                                // 获取手机方向
                                int rotation = ViewUtil.getWindowRotation(CalibrationActivity.this);
                                // 根据设备方向计算设置照片的方向
                                previewRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                            ToastUtil.showShort("配置失败");
                        }
                    }, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("Camera2");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
        mainHandler = new Handler(getMainLooper());
    }

    @Override
    public void onBackPressed() {
        // TODO: @tth 做一个确认提示框，询问是否退出标定
        finish();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_calibration;
    }
}
