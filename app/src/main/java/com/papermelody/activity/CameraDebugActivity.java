package com.papermelody.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.papermelody.R;
import com.papermelody.core.calibration.Calibration;
import com.papermelody.util.ImageProcessor;
import com.papermelody.util.ImageUtil;
import com.papermelody.util.TapDetectorAPI;
import com.papermelody.util.ToastUtil;
import com.papermelody.util.ViewUtil;
import com.papermelody.widget.CameraDebugView;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;

/**
 * Created by gigaflw on 2017/4/10.
 */

public class CameraDebugActivity extends BaseActivity {

    /**
     * 用于处理 Camera 获得的图片
     *
     * 图片处理放在 processImage 函数中
     */

    @BindView(R.id.view_camera_debug)
    SurfaceView viewCameraDebug;

    @BindView(R.id.canvas_camera_debug)
    CameraDebugView canvasCameraDebug;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray() {
        {
            append(Surface.ROTATION_0, 90);
            append(Surface.ROTATION_90, 0);
            append(Surface.ROTATION_180, 270);
            append(Surface.ROTATION_270, 180);
        }
    };


    ///为了使照片竖直显示

    private CameraDevice cameraDevice;
    private SurfaceHolder surfaceHolder;
    private Handler childHandler, mainHandler;
    private CameraCaptureSession cameraCaptureSession;
    private ImageReader imageReader;

    private Calibration.CalibrationResult calibrationResult;
    private int[] voiceResId = new int[]{R.raw.c3, R.raw.d3, R.raw.e3, R.raw.f3, R.raw.g3, R.raw.a3, R.raw.b3,
            R.raw.c4, R.raw.d4, R.raw.e4, R.raw.f4, R.raw.g4, R.raw.a4, R.raw.b4, R.raw.c5, R.raw.d5, R.raw.e5,
            R.raw.f5, R.raw.g5, R.raw.a5, R.raw.b5, R.raw.c3m, R.raw.d3m, R.raw.f3m, R.raw.g3m, R.raw.a3m,
            R.raw.c4m, R.raw.d4m, R.raw.f4m, R.raw.g4m, R.raw.a4m, R.raw.c5m, R.raw.d5m, R.raw.f5m, R.raw.g5m,
            R.raw.a5m};
    private int[] voiceId = new int[36];
    private SoundPool soundPool;

    private ArrayList<Integer> lastKeys = new ArrayList<>();
    // this variable is used to prevent a same key to be played in a row
    // FIXME: it is only a temporary measure because real piano will play a long sound instead of one shot
    // FIXME: this vairable should be put into the class in responsible for playing sound, not here
    //    by gigaflw

    public void processImage(Image image) {
        /**
         * Process image here
         */
//        Log.w("test", "hello?" + image.getWidth());
        // FIXME: calibrationResult is null and will cause a crash when click `DEBUG` button
        // @tang tong hui
        Calibration.TransformResult transformResult = ImageProcessor.getKeyTransform(calibrationResult);
        Mat mat = ImageUtil.imageToBgr(image);
        List<Integer> keys = ImageProcessor.getPlaySoundKey(mat.clone(), transformResult);

        List<List<Point>> ret = TapDetectorAPI.getAllForDebug(mat);
        canvasCameraDebug.updatePoints(ret.get(0), ret.get(1), ret.get(2), image.getHeight(),
                image.getWidth(), this, viewCameraDebug.getHeight());
//        Log.w("TESTK", "" + keys);
//        Log.w("LAST_TESTK", "" + lastKeys);
        for (Integer key : keys) {
            if (!lastKeys.contains(key)){
                playSound(key);
            }
        }
        lastKeys = new ArrayList<>(keys);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        calibrationResult = (Calibration.CalibrationResult) intent.getSerializableExtra(CalibrationActivity.EXTRA_RESULT);

        initSoundPool();
        initSurfaceView();
    }

    private void initSoundPool() {
        SoundPool.Builder spb = new SoundPool.Builder();
        spb.setMaxStreams(10);
        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
        attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
        spb.setAudioAttributes(attrBuilder.build());
        soundPool = spb.build();
    }

    private void initSurfaceSize(double scalar) {
        /* 横屏导致长宽交换 */
        int width = ViewUtil.getScreenWidth(this);
        int height = (int) (width / scalar);
        viewCameraDebug.setLayoutParams(new FrameLayout.LayoutParams(width, height));
        Log.d("TESTV", width+" "+height);
    }

    private void initSurfaceView() {
        surfaceHolder = viewCameraDebug.getHolder();
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

        for (int i = 0; i < voiceId.length; ++i) {
            voiceId[i] = soundPool.load(this, voiceResId[i], 1);
        }
    }


    private void initCamera() {
        /**
         * 不要改这里的代码！
         * 任何图像处理的代码加到 processImage 里去！  by gigaflw
         */
        HandlerThread handlerThread = new HandlerThread("Camera2");
        handlerThread.start();
        childHandler = new Handler(handlerThread.getLooper());
        mainHandler = new Handler(getMainLooper());
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String cameraID = String.valueOf(CameraCharacteristics.LENS_FACING_BACK);

        // 设置imageReader的尺寸与采样频率
        try {
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraID);
            StreamConfigurationMap map = characteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.YUV_420_888)),
                    new CompareSizesByArea());
            initSurfaceSize((double) largest.getWidth()/largest.getHeight());

            Log.d("TESTVL", largest.getWidth()+" "+largest.getHeight());
            imageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(), ImageFormat.YUV_420_888, 5);

            imageReader.setOnImageAvailableListener(reader -> {
                Image image = null;
                try {
                    image = imageReader.acquireLatestImage();
                    if (image == null) { return; }

                    processImage(image);

                } finally {
                    if (image != null) { image.close(); }
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
                        int rotation = ViewUtil.getWindowRotation(CameraDebugActivity.this);
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

    public void playSound(int keyID) {
        soundPool.play(voiceId[keyID], 1, 1, 0, 0, 1);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_camera_debug;
    }

    private class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }
}
