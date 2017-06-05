package com.papermelody.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
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
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.papermelody.R;
import com.papermelody.util.CanvasUtil;
import com.papermelody.util.ImageUtil;
import com.papermelody.util.ToastUtil;
import com.papermelody.util.ViewUtil;
import com.papermelody.widget.CameraDebugView;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import tapdetect.facade.Tap;

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

    private long lastFrameTime = 0;

    public void processImage(Image image) {
        /**
         * Process image here
         * Called on every frame of video
         */
        long frameTime = System.currentTimeMillis();

        if (Tap.readyForNextFrame()) {
            Mat mat = ImageUtil.imageToBgr(image);

            long t1 = System.currentTimeMillis();
            List<List<Point>> ret = Tap.getAllForDebug(mat);
            long t2 = System.currentTimeMillis();

            CanvasUtil.setScreenHeight(ViewUtil.getScreenHeight(this));
            canvasCameraDebug.updateInfo(
                    ret.get(0), ret.get(1), ret.get(2), ret.get(3), ret.get(4),
                    t2-t1, frameTime - lastFrameTime, Tap.getProcessInterval()
            );
        }

        lastFrameTime = frameTime;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /*View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);*/

        /*Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // 状态栏（以上几行代码必须，参考setStatusBarColor|setNavigationBarColor方法源码）
        window.setStatusBarColor(Color.TRANSPARENT);
        // 虚拟导航键
        window.setNavigationBarColor(Color.TRANSPARENT);*/

        Log.d("TESTC", ViewUtil.getScreenWidth(this)+" "+ViewUtil.getScreenHeight(this));

        initSurfaceView();
    }


    private void initSurfaceSize(double scalar) {
        /* 横屏导致长宽交换 */
        int width = ViewUtil.getScreenWidth(this);
        int height = (int) (width / scalar);
        FrameLayout.LayoutParams lp;
        lp = new FrameLayout.LayoutParams(width, height);
        lp.gravity = Gravity.CENTER;
        if (Build.VERSION.SDK_INT >= 24) {
            // TODO: Android 7.0 上貌似有自动图片适配功能，暂时不太确定，需要更多的测试情况
            height = ViewUtil.getScreenHeight(this);
            lp = new FrameLayout.LayoutParams(width, height);
            lp.gravity = Gravity.CENTER;
            FrameLayout.LayoutParams lpCanvas;
            width = (int)(height * scalar);
            lpCanvas = new FrameLayout.LayoutParams(width, height);
            lpCanvas.gravity = Gravity.CENTER;
            canvasCameraDebug.setLayoutParams(lpCanvas);
        }
        viewCameraDebug.setLayoutParams(lp);
        CanvasUtil.setSurfaceViewSize(width, height);
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

            // 获取照相机可用的符合条件的最小像素图片
            Size relativeMin = ImageUtil.getRelativeMinSize(Arrays.asList(map.getOutputSizes(ImageFormat.YUV_420_888)),
                                                        viewCameraDebug.getWidth(), viewCameraDebug.getHeight());
            initSurfaceSize((double) relativeMin.getWidth()/relativeMin.getHeight());

            Log.d("TESTVL", relativeMin.getWidth()+" "+relativeMin.getHeight());
            imageReader = ImageReader.newInstance(relativeMin.getWidth(), relativeMin.getHeight(), ImageFormat.YUV_420_888, 5);

            CanvasUtil.setPhotoSize(relativeMin.getWidth(), relativeMin.getHeight());

            imageReader.setOnImageAvailableListener(reader -> {
                Image image = null;
                try {
                    image = imageReader.acquireLatestImage();
                    if (image == null) {
                        return;
                    }

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

    @Override
    protected int getContentViewId() {
        return R.layout.activity_camera_debug;
    }
}
