package com.papermelody.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
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
import android.preference.PreferenceManager;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.papermelody.R;
import com.papermelody.core.calibration.CalibrationResult;
import com.papermelody.core.calibration.TransformResult;
import com.papermelody.model.Opern;
import com.papermelody.model.instrument.FluteWith7Holes;
import com.papermelody.model.instrument.Instrument;
import com.papermelody.model.instrument.PianoWith14KeysC3ToB4;
import com.papermelody.model.instrument.PianoWith14KeysC4ToB5;
import com.papermelody.model.instrument.PianoWith21KeysC3ToB5;
import com.papermelody.model.instrument.PianoWith21KeysC4ToB6;
import com.papermelody.util.CanvasUtil;
import com.papermelody.util.ImageProcessor;
import com.papermelody.util.ImageUtil;
import com.papermelody.util.ToastUtil;
import com.papermelody.util.ViewUtil;
import com.papermelody.widget.AutoFitTextureView;
import com.papermelody.widget.Bean;
import com.papermelody.widget.CameraDebugView;
import com.papermelody.widget.PlayView;

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
import java.util.Timer;
import java.util.TimerTask;

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

    
    @BindView(R.id.text_mode_name)
    TextView textViewModeName;
    @BindView(R.id.text_instrument_name)
    TextView textViewInstrumentName;
    @BindView(R.id.text_opern_name)
    TextView textViewOpernName;
    @BindView(R.id.btn_play_over)
    Button btnPlayOver;
    @BindView(R.id.dot_view)
    PlayView playView;
    @BindView(R.id.new_img_opern)
    ImageView newImgOpern;
    @BindView(R.id.old_img_opern)
    ImageView oldImgOpern;
    @BindView(R.id.screen_cover)
    LinearLayout screencover;
    @BindView(R.id.notice_time)
    TextView noticetime;
    @BindView(R.id.start_notice)
    TextView startnotice;
    @BindView(R.id.notice_layout)
    LinearLayout noticelayout;
    @BindView(R.id.firstBar)
    ProgressBar noticebar;

    @BindView(R.id.text_time)
    TextView textTime;

    @BindView(R.id.text_calibration)
    TextView calibrationtext;

    public static final String EXTRA_MODE = "EXTRA_MODE";
    public static final String EXTRA_INSTRUMENT = "EXTRA_INSTRUMENT";
    public static final String EXTRA_CATIGORY = "EXTRA_CATIGORY";
    public static final String EXTRA_OPERN = "EXTRA_OPERN";
    public static final String FILENAME = "FILENAME";
    public static final String MODE = "MODE";
    public static final String INSTRUMENT = "INSTRUMENT";
    public static final String CATEGORY = "CATEGORY";
    public static final String EXTRA_RESULT = "EXTRA_RESULT";

    public static final int MODE_FREE = 0;
    public static final int MODE_OPERN = 1;

    private static final String TAG = "PlayAct";

    private static final int MAX_PREVIEW_WIDTH = 1920;
    private static final int MAX_PREVIEW_HEIGHT = 1080;

    /**
     * 谱子在进入界面后，延迟播放的时间，即准备时间
     */
    private static final int OPERN_SECOND_DELAYED = 4;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    ///为了使照片竖直显示
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }


    private int mode;
    private int instrument;
    private int category;
    private int opernNum;
    private CalibrationResult calibrationResult;
    private int[] voiceId;
    private SoundPool soundPool;
    private int soundPoolStreamId = 0;

    private CameraManager cameraManager;
    private CameraDevice cameraDevice;
    Paint paint;
    private List<Bean> list = new ArrayList<Bean>();
    ;
    /**
     * 跟谱模式动画
     */
    private int MaxAlpha = 255;///**
    private boolean START = true;// * 获取照片图像数据用到的子线程
    private Path path; // */
    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    /**
     * 手标定完成判定
     */
    private boolean hand_calibration_flag = false;
    private boolean start_flag = false;
    private int number_count = 0;
    Timer pause_timer = new Timer();
    private int pro;

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

    /**
     * 跟谱模式的点、坐标、时间节点等数据
     */
    private ArrayList<Integer> listX;
    private ArrayList<Integer> listY;
    private ArrayList<Integer> listWidth;
    private ArrayList<Integer> listHeight;
    private ArrayList<Integer> listTime;

    /**
     * 每一行谱子的计时器与任务
     */
    private Timer[] timers;
    private TimerTask[] tasks;

    /**
     * sourceBitmap: 源谱子
     */
    private Bitmap sourceBitmap;

    /**
     * drawable读入之后与原来的像素情况会有一定程度的放大，用长宽两个scalar记录放大比例
     */
    private double widthScalar;
    private double heightScalar;

    /**
     * 用于统计当前谱子的行数和当前时间
     */
    private int currentLine = 0;
    private int currentSecond = 0;

    /**
     * 秒钟计时器
     */
    private Timer secondTimer;

    /**
     * 秒钟的计时任务
     */
    private TimerTask nextTask = new TimerTask() {
        @Override
        public void run() {
            number_count++;

        }
    };

    private boolean barfirst = false;
    private double step = 0;
    Handler barhandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //处理消息

            //设置滚动条和text的值
            noticebar.setProgress(pro);
        }
    };

    private void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int max = noticebar.getMax();
                try {
                    //子线程循环间隔消息
                    while (pro < max) {
                        pro = (int) (step + pro);
                        Message msg = new Message();

                        barhandle.sendMessage(msg);
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private TimerTask secondTask = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                // UI thread
                @Override
                public void run() {
                    if (mode == MODE_OPERN) {
                        int remainTime = listTime.get(currentLine) + OPERN_SECOND_DELAYED - currentSecond;
                        if (!barfirst) {
                            barfirst = true;
                            pro = 0;
                            step = (900.0 / remainTime) / 9;
                        }
                        if (remainTime == 0) {
                            currentLine++;

                            if (currentLine >= listTime.size()) {
                                Log.d("TESTT", "finish");
                                playOver();
                            } else {
                                remainTime = listTime.get(currentLine) + OPERN_SECOND_DELAYED - currentSecond;
                                pro = 0;
                                step = (900.0 / (remainTime) / 9);
                            }
                        }
                        Log.d("TESTTttt", remainTime + "");
                        Log.d("TESTTeee", noticebar.getProgress() + "");
                        Log.d("TESTTttt", step + "");
                    }

                    //textTime.setText(String.valueOf(remainTime));
                    currentSecond++;
                }
            });
        }
    };

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
                public void onSurfaceTextureUpdated(SurfaceTexture texture) {
                }
            };

    private ArrayList<Integer> lastKeys = new ArrayList<>();
    // this variable is used to prevent a same key to be played in a row
    // FIXME: it is only a temporary measure because real piano will play a long sound instead of one shot
    // FIXME: this vairable should be put into the class in responsible for playing sound, not here
    //    by gigaflw

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = getIntent();
        mode = intent.getIntExtra(EXTRA_MODE, 0);
        instrument = intent.getIntExtra(EXTRA_INSTRUMENT, 0);
        category = intent.getIntExtra(EXTRA_CATIGORY, 0);
        opernNum = intent.getIntExtra(EXTRA_OPERN, 0);
        opernNum = 1; // FIXME: opern默认
        calibrationResult = (CalibrationResult) intent.getSerializableExtra(EXTRA_RESULT);

        initSoundPool();
        initVoice();

        // 开启子线程，绑定TextureView的响应事件
        startBackgroundThread();
        viewPlay.setSurfaceTextureListener(surfaceTextureListener);

        Tap.reset();
    }

    public void processImage(Image image) {

        /**
         * Process image here
         * Calibration process already ends, now we play keys according to tapping points
         */
        if (!Tap.sampleCompleted()) {
            //canvasPlay.setVisibility(View.INVISIBLE);
            calibrationtext.setVisibility(View.VISIBLE);
            noticelayout.setVisibility(View.VISIBLE);
        }
        if (Tap.sampleCompleted() && !hand_calibration_flag) {
            noticelayout.setVisibility(View.INVISIBLE);
            calibrationtext.setVisibility(View.INVISIBLE);
            canvasPlay.setVisibility(View.INVISIBLE);
            screencover.setVisibility(View.VISIBLE);
            noticetime.setVisibility(View.VISIBLE);
            startnotice.setVisibility(View.VISIBLE);
            noticetime.setText(String.valueOf("3"));
            hand_calibration_flag = true;
            pause_timer.schedule(nextTask, 0, 1000);

            return;
        }
        if (Tap.sampleCompleted() && number_count <= 4 && !start_flag) {
            switch (number_count) {
                case 0:
                    break;
                case 1:
                    break;
                case 2: {
                    noticetime.setText(String.valueOf("2"));
                    break;
                }
                case 3: {
                    noticetime.setText(String.valueOf("1"));
                    break;
                }
                case 4: {
                    screencover.setVisibility(View.INVISIBLE);
                    noticetime.setVisibility(View.INVISIBLE);
                    startnotice.setVisibility(View.INVISIBLE);
                    canvasPlay.setVisibility(View.VISIBLE);
                    initMediaRecorder();

                    initView();
                    start_flag = true;
                    break;
                }
                default:
                    break;
            }
            return;
        }

        if (!Tap.readyForNextFrame()) {
            return;
        }
        Mat mat = ImageUtil.imageToBgr(image);
        TransformResult transformResult = ImageProcessor.getKeyTransform(calibrationResult);

        long t1 = System.currentTimeMillis();
        List<Point> tapping;

        if (instrument == Instrument.INSTRUMENT_FLUTE) {
            tapping = Tap.getFluteAll(mat, canvasPlay.getHandContours(), canvasPlay.getFingerTips());
        } else {
            tapping = Tap.getAll(mat, canvasPlay.getHandContours(), canvasPlay.getFingerTips());
        }


        //List<Point> tapping = Tap.getAll(mat, canvasPlay.getHandContours(), canvasPlay.getFingerTips());
        long t2 = System.currentTimeMillis();

        //playView.addBean(tapping);

        CanvasUtil.setScreenHeight(ViewUtil.getScreenHeight(this));
        canvasPlay.updateInfo(t2 - t1, 0, Tap.getProcessInterval());

        boolean[] judge_in_area = new boolean[tapping.size()];
        for (int i = 0; i < tapping.size(); i++) {
            judge_in_area[i] = false;
        }
        List<Integer> keys;
        switch (instrument) {
            case Instrument.INSTRUMENT_PIANO: {
                keys = ImageProcessor.getPlaySoundKey(mat.clone(), transformResult, tapping, judge_in_area);
                for (int i = 0; i < keys.size(); i++) {
                    playSound(keys.get(i));
                    Log.d("TESTKEY", keys.get(i) + "");
                }
                break;
            }
            case Instrument.INSTRUMENT_FLUTE: {
                List<Integer> flute = ImageProcessor.getPlaySoundKey(mat.clone(), transformResult, tapping, judge_in_area);
                boolean[] temp = new boolean[7];
                for (int i = 0; i < 7; i++) temp[i] = true;
                for (int i = 0; i < flute.size(); i++) {
                    if (flute.get(i) < 7) temp[flute.get(i)] = false;
                }
                keys = new ArrayList<>();
                keys.add(0, FluteWith7Holes.holesToVoice(temp));
                for (int i = 0; i < keys.size(); i++) {
                    playSound(keys.get(i));
                }
                break;
            }
            default:
                keys = new ArrayList<>();
                break;
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if (pref.getBoolean("heart_show", true))
        playView.addBean(tapping, judge_in_area);
        // lastKeys = new ArrayList<>(keys);
    }

    private void initView() {
        /**
         * 初始化界面上的文字标签、按键响应等等
         */

        switch (mode) {
            case MODE_FREE:
                textViewModeName.setText(R.string.mode_free);
                btnPlayOver.setVisibility(View.VISIBLE);

                // calibrationtext.setVisibility(View.VISIBLE);
                //textViewOpern.setText("");
                initSecondTimer();
                break;
            case MODE_OPERN:
                textViewModeName.setText(R.string.mode_opern);
                textViewOpernName.setText(getResources().getStringArray(R.array.spinner_opern)[opernNum]);
                textViewOpernName.setVisibility(View.VISIBLE);
                newImgOpern.setVisibility(View.VISIBLE);
                oldImgOpern.setVisibility(View.VISIBLE);
                btnPlayOver.setVisibility(View.VISIBLE);
                // calibrationtext.setVisibility(View.VISIBLE);
                //calibrationtext.setText(String.valueOf("dfdfdfd"));
                //textTime.setVisibility(View.VISIBLE);
                noticebar.setVisibility(View.VISIBLE);
                initSecondTimer();
                initOpernTimer();
                break;
        }

        btnPlayOver.setOnClickListener((View v) -> {
            if (currentSecond >= 3) {
                playOver();
            } else {
                ToastUtil.showShort(R.string.play_time_too_short);
            }
        });

    }

    private void initVoice() {
        switch (category) {
            case Instrument.INSTRUMENT_PIANO21C3TOB5:
                textViewInstrumentName.setText(R.string.piano_with_21_keys_c3_to_b5);
                voiceId = new int[PianoWith21KeysC3ToB5.KEY_NUM];
                for (int i = 0; i < voiceId.length; ++i) {
                    voiceId[i] = soundPool.load(this, PianoWith21KeysC3ToB5.getVoiceResId(i), 1);
                }
                break;
            case Instrument.INSTRUMENT_PIANO21C4TOB6:
                textViewInstrumentName.setText(R.string.piano_with_21_keys_c4_to_b6);
                voiceId = new int[PianoWith21KeysC4ToB6.KEY_NUM];
                for (int i = 0; i < voiceId.length; ++i) {
                    voiceId[i] = soundPool.load(this, PianoWith21KeysC4ToB6.getVoiceResId(i), 1);
                }
                break;
            case Instrument.INSTRUMENT_PIANO14C3TOB4:
                textViewInstrumentName.setText(R.string.piano_with_14_keys_c3_to_b4);
                voiceId = new int[PianoWith14KeysC3ToB4.KEY_NUM];
                for (int i = 0; i < voiceId.length; ++i) {
                    voiceId[i] = soundPool.load(this, PianoWith14KeysC3ToB4.getVoiceResId(i), 1);
                }
                break;
            case Instrument.INSTRUMENT_PIANO14C4TOB5:
                textViewInstrumentName.setText(R.string.piano_with_14_keys_c4_to_b5);
                voiceId = new int[PianoWith14KeysC4ToB5.KEY_NUM];
                for (int i = 0; i < voiceId.length; ++i) {
                    voiceId[i] = soundPool.load(this, PianoWith14KeysC4ToB5.getVoiceResId(i), 1);
                }
                break;
            case Instrument.INSTRUMENT_FLUTE7:
                textViewInstrumentName.setText(R.string.flute_with_7_holes);
                voiceId = new int[FluteWith7Holes.KEY_NUM];
                for (int i = 0; i < voiceId.length; ++i) {
                    voiceId[i] = soundPool.load(this, FluteWith7Holes.getVoiceResId(i), 1);
                }
                break;
        }
    }

    private void initSoundPool() {
        SoundPool.Builder spb = new SoundPool.Builder();
        spb.setMaxStreams(10);
        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
        attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
        spb.setAudioAttributes(attrBuilder.build());
        soundPool = spb.build();
    }

    private void initSecondTimer() {
        secondTimer = new Timer();
        secondTimer.schedule(secondTask, 0, 1000);
    }

    /**
     * 在跟谱模式中，需要从原始txt格式谱子中设置所有行谱子的截取坐标与跳转时间，因此每行都需要一个计时器来跳转，
     * 同时还需要设置一个全局的秒钟计时器，来统计下一次跳转的剩余时间
     */
    private void initOpernTimer() {

        start();

        Opern opern = new Opern(this, Opern.getOpernRaw(opernNum)); // FIXME: 暂时只有一首谱子

        listX = opern.getListX();
        listY = opern.getListY();
        listWidth = opern.getListWidth();
        listHeight = opern.getListHeight();
        listTime = opern.getListTime();

        timers = new Timer[listTime.size()];
        tasks = new TimerTask[listTime.size()];
        for (int i = 0; i < listTime.size() - 1; ++i) {
            final int fi = i;
            final int ofi = i - 1;

            tasks[i] = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {      // UI thread
                        int image_alpha = 255;
                        boolean isrung = false;
                        Handler mHandler;

                        @Override
                        public void run() {

//                            if (fi!=0){
//                                newImgOpern.setDrawingCacheEnabled(true);
//                               // Bitmap old = ((BitmapDrawable)newImgOpern.getDrawable()).getBitmap();
//
//                                Bitmap old=newImgOpern.getDrawingCache();
//                               newImgOpern.setDrawingCacheEnabled(false);
//                                oldImgOpern.setImageBitmap(old);
//                            }

                            timers[fi].cancel();
                            int x = (int) (listX.get(fi) * widthScalar);
                            int y = (int) (listY.get(fi) * heightScalar);
                            int width = (int) (listWidth.get(fi) * widthScalar);
                            int height = (int) (listHeight.get(fi) * heightScalar);
                            if (fi != 0) {
                                int ox = (int) (listX.get(ofi) * widthScalar);
                                int oy = (int) (listY.get(ofi) * heightScalar);
                                int owidth = (int) (listWidth.get(ofi) * widthScalar);
                                int oheight = (int) (listHeight.get(ofi) * heightScalar);
                                Bitmap osmallBitmap = Bitmap.createBitmap(sourceBitmap, ox, oy, owidth, oheight);
                                oldImgOpern.setImageBitmap(osmallBitmap);
                                oldImgOpern.setImageAlpha(image_alpha);
                            }
                            Bitmap smallBitmap = Bitmap.createBitmap(sourceBitmap, x, y, width, height);

                            newImgOpern.setImageBitmap(smallBitmap);
                            newImgOpern.setImageAlpha(0);

                            isrung = true;

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    while (isrung) {
                                        try {
                                            Thread.sleep(50);
                                            // 更新Alpha值
                                            updateAlpha();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }).start();
                            mHandler = new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    super.handleMessage(msg);
                                    oldImgOpern.setImageAlpha(image_alpha);
                                    newImgOpern.setImageAlpha(255 - image_alpha);
                                    // 设置textview显示当前的Alpha值
                                    //textView.setText("现在的alpha值是:" + Integer.toString(image_alpha));
                                    // 刷新视图
                                    oldImgOpern.invalidate();
                                    newImgOpern.invalidate();
                                }
                            };
                        }

                        public void updateAlpha() {
                            if (image_alpha - 7 >= 0) {
                                image_alpha -= 7;
                            } else {
                                image_alpha = 0;
                                isrung = false;
                            }
                            // 发送需要更新imageview视图的消息-->这里是发给主线程
                            mHandler.sendMessage(mHandler.obtainMessage());
                        }
                    });
                }
            };
            timers[i] = new Timer();
            timers[i].schedule(tasks[i], (listTime.get(i) + OPERN_SECOND_DELAYED) * 1000, 10000);
        }

        Drawable drawable = getDrawable(Opern.getOpernDrawable(opernNum));
        sourceBitmap = ImageUtil.drawableToBitmap(drawable);
//        sourceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.opern_pugongyingdeyueding);
        widthScalar = (double) drawable.getIntrinsicWidth() / opern.getWidth();
        heightScalar = (double) drawable.getIntrinsicHeight() / opern.getHeight();
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
            File audioFile = new File(getCacheDir().getAbsolutePath() + "/" + fileName);  // FIXED: 路径待确定
            Log.i("nib", audioFile.getAbsolutePath());
            mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playSound(int keyID) {
        if (keyID < voiceId.length && keyID >= 0) {
            switch (instrument) {
                case Instrument.INSTRUMENT_PIANO:
                    soundPool.play(voiceId[keyID], 1, 1, 0, 0, 1);
                    break;
                case Instrument.INSTRUMENT_FLUTE:
                    soundPool.stop(soundPoolStreamId);
                    soundPoolStreamId = soundPool.play(voiceId[keyID], 1, 1, 0, 0, 1);
                    break;
            }
        }
    }

    private void playOver() {
        mediaRecorder.stop();
        Intent intent = new Intent(this, PlayListenActivity.class);
        intent.putExtra(FILENAME, fileName);
        intent.putExtra(MODE, mode);
        intent.putExtra(INSTRUMENT, instrument);
        intent.putExtra(CATEGORY, category);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mode == MODE_OPERN) {
            try {
                secondTimer.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int i = 0; i < timers.length; ++i) {
                try {
                    timers[i].cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 相机开启
     *
     * @param width  TextureView的宽度
     * @param height TextureView的高度
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
     *
     * @param width  TextureView的宽度
     * @param height TextureView的高度
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

            Log.d(TAG, relativeMin.getWidth() + " " + relativeMin.getHeight());

            imageReader = ImageReader.newInstance(relativeMin.getWidth(), relativeMin.getHeight(),
                    ImageFormat.YUV_420_888, 1);

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

            Log.d(TAG, previewSize.getWidth() + " " + previewSize.getHeight());
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
     *
     * @param viewWidth  TextureView的宽度
     * @param viewHeight TextureView的高度
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

    @Override
    public void onBackPressed() {
        // TODO: @tth 做一个确认提示框，返回到首页
        try {
            mediaRecorder.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }

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