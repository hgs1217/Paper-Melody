package com.papermelody.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.papermelody.R;
import com.papermelody.util.ToastUtils;
import com.papermelody.util.ViewUtil;

import java.nio.ByteBuffer;
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
    @BindView(R.id.img_calibration)
    ImageView imgCalibration;

    private static double STANDARD_SIZE_RATE = 1.33333; // 4: 3
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

    private int cnt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initSurfaceView();
    }

    private void initSurfaceSize() {
        int height = ViewUtil.getScreenHeight(this);
        int width = (int) (height / STANDARD_SIZE_RATE);
        Log.d("TEST", height+"\t"+width);
        viewCalibration.setLayoutParams(new LinearLayout.LayoutParams(width, height));
    }

    private void initSurfaceView() {
        surfaceHolder = viewCalibration.getHolder();
        surfaceHolder.setKeepScreenOn(true);
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                initCamera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                initSurfaceSize();
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
        HandlerThread handlerThread = new HandlerThread("Camera2");
        handlerThread.start();
        childHandler = new Handler(handlerThread.getLooper());
        mainHandler = new Handler(getMainLooper());
        cameraID = "" + CameraCharacteristics.LENS_FACING_FRONT;  //前摄像头
        imageReader = ImageReader.newInstance(1080, 1920, ImageFormat.JPEG, 1);
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            /* 可以在这里处理拍照得到的临时照片 例如，写入本地 */

            @Override
            public void onImageAvailable(ImageReader reader) {
                cameraDevice.close();
                viewCalibration.setVisibility(View.GONE);
                imgCalibration.setVisibility(View.VISIBLE);

                cnt++;
                Log.d("TEST", "img"+cnt);
                Image image = reader.acquireNextImage();
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);  //由缓冲区存入字节数组
                final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bitmap != null) {
                    imgCalibration.setImageBitmap(bitmap);
                }
            }
        }, mainHandler);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            // TODO: 权限请求
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
            ToastUtils.showShort("开启成功");
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
            ToastUtils.showShort("摄像头开启失败");
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
                    ToastUtils.showShort("配置失败");
                }
            }, childHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }



    private void onFinished() {
        Intent intent = new Intent(this, PlayActivity.class);
        startActivity(intent);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_calibration;
    }
}
