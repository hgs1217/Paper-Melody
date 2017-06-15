package com.papermelody.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.papermelody.R;
import com.papermelody.core.calibration.CalibrationResult;
import com.papermelody.core.calibration.TransformResult;
import com.papermelody.util.CanvasUtil;
import com.papermelody.util.ImageProcessor;
import com.papermelody.util.ImageUtil;
import com.papermelody.util.ToastUtil;
import com.papermelody.util.ViewUtil;
import com.papermelody.widget.AutoFitTextureView;
import com.papermelody.widget.CameraDebugView;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import tapdetect.facade.Tap;

/**
 * Created by HgS_1217_ on 2017/3/18.
 */

public class PlayActivity extends BaseActivity {
    /**
     * 用例：演奏乐器
     * 弹奏乐器时的界面，可能存在虚拟乐器或曲谱等内容
     */

    @BindView(R.id.canvas_play)
    CameraDebugView canvasPlay;
    @BindView(R.id.view_play)
    AutoFitTextureView viewPlay;
    @BindView(R.id.text_mode)
    TextView textViewMode;
    @BindView(R.id.text_instrument)
    TextView textViewInstrument;
    @BindView(R.id.text_mode_name)
    TextView textViewModeName;
    @BindView(R.id.text_instrument_name)
    TextView textViewInstrumentName;
    @BindView(R.id.btn_play_over)
    Button btnPlayOver;
    @BindView(R.id.key_effect_c3)
    LinearLayout keyC3;
    @BindView(R.id.key_effect_d3)
    LinearLayout keyD3;
    @BindView(R.id.key_effect_e3)
    LinearLayout keyE3;
    @BindView(R.id.key_effect_f3)
    LinearLayout keyF3;
    @BindView(R.id.key_effect_g3)
    LinearLayout keyG3;
    @BindView(R.id.key_effect_a3)
    LinearLayout keyA3;
    @BindView(R.id.key_effect_b3)
    LinearLayout keyB3;
    @BindView(R.id.key_effect_c4)
    LinearLayout keyC4;
    @BindView(R.id.key_effect_d4)
    LinearLayout keyD4;
    @BindView(R.id.key_effect_e4)
    LinearLayout keyE4;
    @BindView(R.id.key_effect_f4)
    LinearLayout keyF4;
    @BindView(R.id.key_effect_g4)
    LinearLayout keyG4;
    @BindView(R.id.key_effect_a4)
    LinearLayout keyA4;
    @BindView(R.id.key_effect_b4)
    LinearLayout keyB4;
    @BindView(R.id.key_effect_c5)
    LinearLayout keyC5;
    @BindView(R.id.key_effect_d5)
    LinearLayout keyD5;
    @BindView(R.id.key_effect_e5)
    LinearLayout keyE5;
    @BindView(R.id.key_effect_f5)
    LinearLayout keyF5;
    @BindView(R.id.key_effect_g5)
    LinearLayout keyG5;
    @BindView(R.id.key_effect_a5)
    LinearLayout keyA5;
    @BindView(R.id.key_effect_b5)
    LinearLayout keyB5;
    @BindView(R.id.key_effect_c3m)
    LinearLayout keyC3M;
    @BindView(R.id.key_effect_d3m)
    LinearLayout keyD3M;
    @BindView(R.id.key_effect_f3m)
    LinearLayout keyF3M;
    @BindView(R.id.key_effect_g3m)
    LinearLayout keyG3M;
    @BindView(R.id.key_effect_a3m)
    LinearLayout keyA3M;
    @BindView(R.id.key_effect_c4m)
    LinearLayout keyC4M;
    @BindView(R.id.key_effect_d4m)
    LinearLayout keyD4M;
    @BindView(R.id.key_effect_f4m)
    LinearLayout keyF4M;
    @BindView(R.id.key_effect_g4m)
    LinearLayout keyG4M;
    @BindView(R.id.key_effect_a4m)
    LinearLayout keyA4M;
    @BindView(R.id.key_effect_c5m)
    LinearLayout keyC5M;
    @BindView(R.id.key_effect_d5m)
    LinearLayout keyD5M;
    @BindView(R.id.key_effect_f5m)
    LinearLayout keyF5M;
    @BindView(R.id.key_effect_g5m)
    LinearLayout keyG5M;
    @BindView(R.id.key_effect_a5m)
    LinearLayout keyA5M;

    public static final int KEY_C3 = 0;
    public static final int KEY_D3 = 1;
    public static final int KEY_E3 = 2;
    public static final int KEY_F3 = 3;
    public static final int KEY_G3 = 4;
    public static final int KEY_A3 = 5;
    public static final int KEY_B3 = 6;
    public static final int KEY_C4 = 7;
    public static final int KEY_D4 = 8;
    public static final int KEY_E4 = 9;
    public static final int KEY_F4 = 10;
    public static final int KEY_G4 = 11;
    public static final int KEY_A4 = 12;
    public static final int KEY_B4 = 13;
    public static final int KEY_C5 = 14;
    public static final int KEY_D5 = 15;
    public static final int KEY_E5 = 16;
    public static final int KEY_F5 = 17;
    public static final int KEY_G5 = 18;
    public static final int KEY_A5 = 19;
    public static final int KEY_B5 = 20;
    public static final int KEY_C3M = 21;
    public static final int KEY_D3M = 22;
    public static final int KEY_F3M = 23;
    public static final int KEY_G3M = 24;
    public static final int KEY_A3M = 25;
    public static final int KEY_C4M = 26;
    public static final int KEY_D4M = 27;
    public static final int KEY_F4M = 28;
    public static final int KEY_G4M = 29;
    public static final int KEY_A4M = 30;
    public static final int KEY_C5M = 31;
    public static final int KEY_D5M = 32;
    public static final int KEY_F5M = 33;
    public static final int KEY_G5M = 34;
    public static final int KEY_A5M = 35;

    public static final String EXTRA_MODE = "EXTRA_MODE";
    public static final String EXTRA_INSTRUMENT = "EXTRA_INSTRUMENT";
    public static final String EXTRA_CATIGORY = "EXTRA_CATIGORY";
    public static final String EXTRA_OPERN = "EXTRA_OPERN";
    public static final String FILENAME = "FILENAME";

    private static final String TAG = "PlayAct";

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

    private int mode, instrument, category, opern;
    private CalibrationResult calibrationResult;
    private LinearLayout[] keys = new LinearLayout[36];
    private int[] voiceResId = new int[]{R.raw.c3, R.raw.d3, R.raw.e3, R.raw.f3, R.raw.g3, R.raw.a3, R.raw.b3,
            R.raw.c4, R.raw.d4, R.raw.e4, R.raw.f4, R.raw.g4, R.raw.a4, R.raw.b4, R.raw.c5, R.raw.d5, R.raw.e5,
            R.raw.f5, R.raw.g5, R.raw.a5, R.raw.b5, R.raw.c3m, R.raw.d3m, R.raw.f3m, R.raw.g3m, R.raw.a3m,
            R.raw.c4m, R.raw.d4m, R.raw.f4m, R.raw.g4m, R.raw.a4m, R.raw.c5m, R.raw.d5m, R.raw.f5m, R.raw.g5m,
            R.raw.a5m};
    private int[] voiceId = new int[36];
    private SoundPool soundPool;

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

    private MediaRecorder mediaRecorder;

    private String fileName = "";

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

    private ArrayList<Integer> lastKeys = new ArrayList<>();
    // this variable is used to prevent a same key to be played in a row
    // FIXME: it is only a temporary measure because real piano will play a long sound instead of one shot
    // FIXME: this vairable should be put into the class in responsible for playing sound, not here
    //    by gigaflw

    private final Handler viewStartHandler = new Handler() {
        public void handleMessage(Message msg) {
            int i = msg.what;
            keys[i].clearAnimation();
            Animation animation = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.alpha_key_show);
            keys[i].setAlpha(1);
            keys[i].startAnimation(animation);
        }
    };

    private final Handler viewGoneHandler = new Handler() {
        public void handleMessage(Message msg) {
            int i = msg.what;
            keys[i].clearAnimation();
            Animation animation = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.alpha_key_gone);
            keys[i].startAnimation(animation);
        }
    };

    private final Handler viewEndHandler = new Handler() {
        public void handleMessage(Message msg) {
            int i = msg.what;
            keys[i].setAlpha(0);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = getIntent();
        mode = intent.getIntExtra(EXTRA_MODE, 0);
        instrument = intent.getIntExtra(EXTRA_INSTRUMENT, 0);
        category = intent.getIntExtra(EXTRA_CATIGORY, 0);
        //opern = intent.getIntExtra(EXTRA_OPERN, 0);
        calibrationResult = (CalibrationResult) intent.getSerializableExtra(CalibrationActivity.EXTRA_RESULT);

        // 开启子线程，绑定TextureView的响应事件
        startBackgroundThread();
        viewPlay.setSurfaceTextureListener(surfaceTextureListener);
        
        initSoundPool();
        initView();
        initMediaRecorder();
        Tap.reset();
    }

    public void processImage(Image image) {
        /**
         * Process image here
         */
        if (!Tap.readyForNextFrame()) { return; }

        TransformResult transformResult = ImageProcessor.getKeyTransform(calibrationResult);
        Mat mat = ImageUtil.imageToBgr(image);

        long t1 = System.currentTimeMillis();
        List<Point> tapping = Tap.getAll(mat, canvasPlay.getHandContours(), canvasPlay.getFingerTips());
        long t2 = System.currentTimeMillis();

        CanvasUtil.setScreenHeight(ViewUtil.getScreenHeight(this));
        canvasPlay.updateInfo(t2 - t1, 0, Tap.getProcessInterval());

        List<Integer> keys = ImageProcessor.getPlaySoundKey(mat.clone(), transformResult, tapping);
        for (Integer key : keys) {
            if (!lastKeys.contains(key)){
                playSound(key);
            }
        }
        lastKeys = new ArrayList<>(keys);
    }

    private void initSoundPool() {
        SoundPool.Builder spb = new SoundPool.Builder();
        spb.setMaxStreams(10);
        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
        attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
        spb.setAudioAttributes(attrBuilder.build());
        soundPool = spb.build();
    }

    private void initMediaRecorder() {
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mediaRecorder.setAudioSamplingRate(44100);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mediaRecorder.setAudioEncodingBitRate(96000);
            Date currentTime = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
            fileName = dateFormat.format(currentTime) + ".m4a";
            File audioFile = new File(getCacheDir().getAbsolutePath() + "/Download/" + fileName);  // FIXME: 路径待确定
            Log.i("nib", audioFile.getAbsolutePath());
            mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        /**
         * 初始化界面上的文字标签、按键响应等等
         */

        switch (mode) {
            case 0:
                textViewModeName.setText(R.string.mode_free);
                //textViewOpern.setText("");
            case 1:
                textViewModeName.setText(R.string.mode_opern);
                //textViewOpern.setText("曲谱：" + getResources().getStringArray(R.array.spinner_opern)[opern]);
        }

        if (instrument == 0 && category == 0) {
            textViewInstrumentName.setText(R.string.piano_with_21_keys);
        } else if (instrument == 0 && category == 1) {
            textViewInstrumentName.setText("乐器：15键钢琴");
        } else {
            textViewInstrumentName.setText("乐器：7孔笛");
        }

        btnPlayOver.setOnClickListener((View v) -> {
            mediaRecorder.stop();
            Intent intent = new Intent(this, PlayListenActivity.class);
            intent.putExtra(FILENAME, fileName);
            startActivity(intent);
            finish();
        });

        keys = new LinearLayout[] {keyC3, keyD3, keyE3, keyF3, keyG3, keyA3, keyB3, keyC4, keyD4,
                keyE4, keyF4, keyG4, keyA4, keyB4, keyC5, keyD5, keyE5, keyF5, keyG5, keyA5, keyB5,
                keyC3M, keyD3M, keyF3M, keyG3M, keyA3M, keyC4M, keyD4M, keyF4M, keyG4M, keyA4M,
                keyC5M, keyD5M, keyF5M, keyG5M, keyA5M};

        for (int i = 0; i < keys.length; ++i) {
            voiceId[i] = soundPool.load(this, voiceResId[i], 1);

            // FIXME: 动画暂时被关闭
//            final int fi = i;
//            keys[i].setOnClickListener((View v) -> {
//                playSound(fi);
//            });
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
                    viewPlay.getWidth(), viewPlay.getHeight());

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
            viewPlay.setAspectRatio(previewSize.getWidth(), previewSize.getHeight());

            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(viewPlay.getWidth(), viewPlay.getHeight());
            lp.gravity = Gravity.CENTER;
            CanvasUtil.setSurfaceViewSize(viewPlay.getWidth(), viewPlay.getHeight());

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

        if (viewPlay == null || previewSize == null) {
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
        viewPlay.setTransform(matrix);
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
            SurfaceTexture texture = viewPlay.getSurfaceTexture();
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
                                int rotation = ViewUtil.getWindowRotation(PlayActivity.this);
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

    public void playSound(int keyID) {
        soundPool.play(voiceId[keyID], 1, 1, 0, 0, 1);

        // FIXME: 动画暂时被关闭
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Message msg1 = new Message(), msg2 = new Message(), msg3 = new Message();
//                    msg1.what = keyID;
//                    msg2.what = keyID;
//                    msg3.what = keyID;
//                    viewStartHandler.sendMessage(msg1);
//                    Log.d("TEST", "THREAD1");
//                    Thread.sleep(100);
//                    viewGoneHandler.sendMessage(msg2);
//                    Log.d("TEST", "THREAD2");
//                    Thread.sleep(100);
//                    viewEndHandler.sendMessage(msg3);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }

    @Override
    public void onBackPressed() {
        // TODO: @tth 做一个确认提示框，返回到首页
        mediaRecorder.stop();
        finish();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_play;
    }

    private void copyMusicToData() {
        InputStream in = null;
        FileOutputStream out = null;
        String path = getApplicationContext().getFilesDir()
                .getAbsolutePath() + "/Kissbye.mid"; // data/data目录
        File file = new File(path);
        if (!file.exists()) {
            try {
                in = getAssets().open("Kissbye.mid"); // 从assets目录下复制
                out = new FileOutputStream(file);
                int length = -1;
                byte[] buf = new byte[1024];
                while ((length = in.read(buf)) != -1) {
                    out.write(buf, 0, length);
                }
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
        }
        ToastUtil.showShort("文件已保存至data/data/com.papermelody/下");
    }
}
