package com.papermelody.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
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
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.papermelody.R;
import com.papermelody.util.CanvasUtil;
import com.papermelody.util.ImageProcessor;
import com.papermelody.util.ImageUtil;
import com.papermelody.util.ToastUtil;
import com.papermelody.util.ViewUtil;
import com.papermelody.widget.AutoFitTextureView;
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
    AutoFitTextureView viewCameraDebug;

    @BindView(R.id.canvas_camera_debug)
    CameraDebugView canvasCameraDebug;

    private static final String TAG = "CameraDebugAct";

    private static final int MAX_PREVIEW_WIDTH = 1920;
    private static final int MAX_PREVIEW_HEIGHT = 1080;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray() {
        {
            append(Surface.ROTATION_0, 90);
            append(Surface.ROTATION_90, 0);
            append(Surface.ROTATION_180, 270);
            append(Surface.ROTATION_270, 180);
        }
    };

    private long lastFrameTime = 0;

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

        Log.d("TESTC", ViewUtil.getScreenWidth(this)+" "+ViewUtil.getScreenHeight(this));

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 开启子线程，绑定TextureView的响应事件
        startBackgroundThread();
        viewCameraDebug.setSurfaceTextureListener(surfaceTextureListener);
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
                    viewCameraDebug.getWidth(), viewCameraDebug.getHeight());

            Log.d(TAG, relativeMin.getWidth()+" "+relativeMin.getHeight());

            imageReader = ImageReader.newInstance(relativeMin.getWidth(), relativeMin.getHeight(),
                    ImageFormat.YUV_420_888, 5);

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

            android.graphics.Point displaySize = new android.graphics.Point();
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
            viewCameraDebug.setAspectRatio(previewSize.getWidth(), previewSize.getHeight());

            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(viewCameraDebug.getWidth(), viewCameraDebug.getHeight());
            lp.gravity = Gravity.CENTER;
            canvasCameraDebug.setLayoutParams(lp);
            CanvasUtil.setSurfaceViewSize(viewCameraDebug.getWidth(), viewCameraDebug.getHeight());

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

        if (viewCameraDebug == null || previewSize == null) {
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
        viewCameraDebug.setTransform(matrix);
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
            SurfaceTexture texture = viewCameraDebug.getSurfaceTexture();
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
                                int rotation = ViewUtil.getWindowRotation(CameraDebugActivity.this);
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
    protected int getContentViewId() {
        return R.layout.activity_camera_debug;
    }
}
