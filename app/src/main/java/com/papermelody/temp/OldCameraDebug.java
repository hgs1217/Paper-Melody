package com.papermelody.temp;

/**
 * Created by HgS_1217_ on 2017/6/5.
 */

public class OldCameraDebug {


//
//    private void initSurfaceSize(double scalar) {
//        /* 横屏导致长宽交换 */
//        int width = ViewUtil.getScreenWidth(this);
//        int height = (int) (width / scalar);
//        FrameLayout.LayoutParams lp;
//        lp = new FrameLayout.LayoutParams(width, height);
//        lp.gravity = Gravity.CENTER;
//        if (Build.VERSION.SDK_INT >= 24) {
//            // TODO: Android 7.0 上貌似有自动图片适配功能，暂时不太确定，需要更多的测试情况
//            height = ViewUtil.getScreenHeight(this);
//            lp = new FrameLayout.LayoutParams(width, height);
//            lp.gravity = Gravity.CENTER;
//            FrameLayout.LayoutParams lpCanvas;
//            width = (int)(height * scalar);
//            lpCanvas = new FrameLayout.LayoutParams(width, height);
//            lpCanvas.gravity = Gravity.CENTER;
//            canvasCameraDebug.setLayoutParams(lpCanvas);
//        }
//        viewCameraDebug.setLayoutParams(lp);
//        CanvasUtil.setSurfaceViewSize(width, height);
//    }
//
//    private void initSurfaceView() {
//        surfaceHolder = viewCameraDebug.getHolder();
//        surfaceHolder.setKeepScreenOn(true);
//        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//                initCamera();
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//                if (cameraDevice != null) {
//                    cameraDevice.close();
//                    cameraDevice = null;
//                }
//            }
//        });
//    }
//
//
//    private void initCamera() {
//        /**
//         * 不要改这里的代码！
//         * 任何图像处理的代码加到 processImage 里去！  by gigaflw
//         */
//        HandlerThread handlerThread = new HandlerThread("Camera2");
//        handlerThread.start();
//        childHandler = new Handler(handlerThread.getLooper());
//        mainHandler = new Handler(getMainLooper());
//        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
//        String cameraID = String.valueOf(CameraCharacteristics.LENS_FACING_BACK);
//
//        // 设置imageReader的尺寸与采样频率
//        try {
//            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraID);
//            StreamConfigurationMap map = characteristics.get(
//                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
//
//            // 获取照相机可用的符合条件的最小像素图片
//            Size relativeMin = ImageUtil.getRelativeMinSize(Arrays.asList(map.getOutputSizes(ImageFormat.YUV_420_888)),
//                                                        viewCameraDebug.getWidth(), viewCameraDebug.getHeight());
//            initSurfaceSize((double) relativeMin.getWidth()/relativeMin.getHeight());
//
//            Log.d("TESTVL", relativeMin.getWidth()+" "+relativeMin.getHeight());
//            imageReader = ImageReader.newInstance(relativeMin.getWidth(), relativeMin.getHeight(), ImageFormat.YUV_420_888, 5);
//
//            CanvasUtil.setPhotoSize(relativeMin.getWidth(), relativeMin.getHeight());
//
//            imageReader.setOnImageAvailableListener(reader -> {
//                Image image = null;
//                try {
//                    image = imageReader.acquireLatestImage();
//                    if (image == null) {
//                        return;
//                    }
//
//                    processImage(image);
//                } finally {
//                    if (image != null) { image.close(); }
//                }
//            }, mainHandler);
//
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            cameraManager.openCamera(cameraID, deviceStateCallback, mainHandler);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private CameraDevice.StateCallback deviceStateCallback = new CameraDevice.StateCallback() {
//        /* 摄像头创建监听 */
//
//        @Override
//        public void onOpened(@NonNull CameraDevice camera) {
//            ToastUtil.showShort("开启成功");
//            cameraDevice = camera;
//            takePreview();
//        }
//
//        @Override
//        public void onDisconnected(@NonNull CameraDevice camera) {
//            if (null != cameraDevice) {
//                cameraDevice.close();
//                cameraDevice = null;
//            }
//        }
//
//        @Override
//        public void onError(@NonNull CameraDevice camera, int error) {
//            ToastUtil.showShort("摄像头开启失败");
//        }
//    };
//
//    private void takePreview() {
//        /* 开启预览 */
//
//        try {
//            // 创建预览需要的CaptureRequest.Builder
//            final CaptureRequest.Builder previewRequestBuilder =
//                    cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//            // 将SurfaceView的surface作为CaptureRequest.Builder的目标
//            previewRequestBuilder.addTarget(surfaceHolder.getSurface());
//            // 将imageReader的surface作为CaptureRequest.Builder的目标
//            previewRequestBuilder.addTarget(imageReader.getSurface());
//            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
//            cameraDevice.createCaptureSession(Arrays.asList(surfaceHolder.getSurface(),
//                    imageReader.getSurface()), new CameraCaptureSession.StateCallback() {
//                @Override
//                public void onConfigured(@NonNull CameraCaptureSession session) {
//                    if (null == cameraDevice) {
//                        return;
//                    }
//                    // 当摄像头已经准备好时，开始显示预览
//                    cameraCaptureSession = session;
//                    try {
//                        // 自动对焦
//                        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
//                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
//                        // 打开闪光灯
//                        previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
//                                CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
//                        // 显示预览
//                        CaptureRequest previewRequest = previewRequestBuilder.build();
//                        cameraCaptureSession.setRepeatingRequest(previewRequest, null, childHandler);
//                        // 获取手机方向
//                        int rotation = ViewUtil.getWindowRotation(CameraDebugActivity.this);
//                        // 根据设备方向计算设置照片的方向
//                        previewRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
//                    } catch (CameraAccessException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
//                    ToastUtil.showShort("配置失败");
//                }
//            }, childHandler);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }
}
