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
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
     * http://blog.sina.com.cn/s/blog_46e3af5b0101cehh.html
     * http://blog.csdn.net/u013869488/article/details/49853217
     * http://blog.csdn.net/sinat_29384657/article/details/52188723
     *
     * http://blog.csdn.net/yanzi1225627/article/details/38098729
     */

    @BindView(R.id.view_calibration)
    AutoFitTextureView viewCalibration;
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

    private static final String TAG = "CalibrationAct";
    private static final int REQUEST_CAMERA_PERMISSION = 1;

    private static final int MAX_PREVIEW_WIDTH = 1920;
    private static final int MAX_PREVIEW_HEIGHT = 1080;

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
    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    private String cameraID;
    private CameraCaptureSession cameraCaptureSession;
    private ImageReader imageReader;
    private CalibrationResult calibrationResult;

    private int targetHeightStart = 0;
    private int targetHeightEnd = 1000;

    private boolean canCalibration = true;
    private int cnt = 0;

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

        /*Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);*/

        super.onCreate(savedInstanceState);

        Log.d(TAG, ViewUtil.getScreenHeight(this)+" "+ViewUtil.getScreenWidth(this));

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);

        startBackgroundThread();
        viewCalibration.setSurfaceTextureListener(surfaceTextureListener);

        initView();
    }

    private void initView() {
        /**
         * 初始化界面上的文字标签、按键响应等等
         */

        initViewStatus();

        btnCalibrationComplete.setOnClickListener((View v) -> {
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

        // FIXME: 暂时调慢了标定视频帧率
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

    private void openCamera(int width, int height) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
//            requestCameraPermission();
            return;
        }
        setUpCameraOutputs(width, height);
        configureTransform(width, height);
        try {
            cameraManager.openCamera(cameraID, deviceStateCallback, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setUpCameraOutputs(int width, int height) {
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        cameraID = String.valueOf(CameraCharacteristics.LENS_FACING_BACK);  //前摄像头
        ImageProcessor.initProcessor();

        try {
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraID);
            StreamConfigurationMap map = characteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            Size relativeMin = ImageUtil.getRelativeMinSize(Arrays.asList(map.getOutputSizes(ImageFormat.YUV_420_888)),
                                                            viewCalibration.getWidth(), viewCalibration.getHeight());

            Log.d(TAG, relativeMin.getWidth()+" "+relativeMin.getHeight());

            imageReader = ImageReader.newInstance(relativeMin.getWidth(), relativeMin.getHeight(),
                                            ImageFormat.YUV_420_888, 5);
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
                } finally {
                    if (image != null) {
                        image.close();
                    }
                }
            }, backgroundHandler);

            // Find out if we need to swap dimension to get the preview size relative to sensor
            // coordinate.
            int displayRotation = getWindowManager().getDefaultDisplay().getRotation();
            //noinspection ConstantConditions
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

            viewCalibration.setAspectRatio(previewSize.getWidth(), previewSize.getHeight());
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

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
            float scale = Math.max(
                    (float) viewHeight / previewSize.getHeight(),
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

    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = viewCalibration.getSurfaceTexture();
            assert texture != null;

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());

            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            previewRequestBuilder
                    = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewRequestBuilder.addTarget(surface);

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
                        public void onConfigureFailed(
                                @NonNull CameraCaptureSession cameraCaptureSession) {
                            ToastUtil.showShort("Configure Failed");
                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("CameraBackground");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_calibration;
    }

//    private void requestCameraPermission() {
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
//                new ConfirmationDialog().show(getChildFragmentManager(), FRAGMENT_DIALOG);
//            } else {
//                FragmentCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
//                        REQUEST_CAMERA_PERMISSION);
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_CAMERA_PERMISSION) {
//            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                ErrorDialog.newInstance(getString(R.string.request_permission))
//                        .show(getChildFragmentManager(), FRAGMENT_DIALOG);
//            }
//        } else {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }
//
//    /**
//     * Shows an error message dialog.
//     */
//    public static class ErrorDialog extends DialogFragment {
//
//        private static final String ARG_MESSAGE = "message";
//
//        public static ErrorDialog newInstance(String message) {
//            ErrorDialog dialog = new ErrorDialog();
//            Bundle args = new Bundle();
//            args.putString(ARG_MESSAGE, message);
//            dialog.setArguments(args);
//            return dialog;
//        }
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            final Activity activity = getActivity();
//            return new AlertDialog.Builder(activity)
//                    .setMessage(getArguments().getString(ARG_MESSAGE))
//                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            activity.finish();
//                        }
//                    })
//                    .create();
//        }
//
//    }
//
//    /**
//     * Shows OK/Cancel confirmation dialog about camera permission.
//     */
//    public static class ConfirmationDialog extends DialogFragment {
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            final Fragment parent = getParentFragment();
//            return new AlertDialog.Builder(getActivity())
//                    .setMessage(R.string.request_permission)
//                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            FragmentCompat.requestPermissions(parent,
//                                    new String[]{Manifest.permission.CAMERA},
//                                    REQUEST_CAMERA_PERMISSION);
//                        }
//                    })
//                    .setNegativeButton(android.R.string.cancel,
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Activity activity = parent.getActivity();
//                                    if (activity != null) {
//                                        activity.finish();
//                                    }
//                                }
//                            })
//                    .create();
//        }
//    }
}
