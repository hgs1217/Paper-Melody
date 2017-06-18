package com.papermelody.util;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.media.Image;
import android.util.Log;
import android.util.Size;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by HgS_1217_ on 2017/5/8.
 */

public class ImageUtil {
    /**
     * 用于处理图像的工具类
     */

    /**
     * 预览照片所需求的最低像素要求，防止像素太低识别有误
     */
    public static final int MIN_HEIGHT = 480;
    public static final int MIN_WIDTH = 640;

    /**
     * 预设的图片长宽比
     */
    public static final double HEIGHT_WIDTH_RATIO = 1.7777777;

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            return bitmap;
        } else {
            return null;
        }
    }

    public static Bitmap imageToBitmap(Mat bgr) {
        Mat rgbaMat = new Mat(bgr.cols(), bgr.rows(), CvType.CV_8U, new Scalar(4));
        Imgproc.cvtColor(bgr, rgbaMat, Imgproc.COLOR_BGR2RGBA, 0);
        Bitmap bmp = Bitmap.createBitmap(rgbaMat.cols(), rgbaMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(bgr, bmp);
        return bmp;
    }

    public static Mat imageToBgr(Image image) {
        Mat yuvMat = imageToMat(image);
        return yuvToBgr(image, yuvMat);
    }

    public static Mat imageToMat(Image image) {
        /* 将image YUV_420_888格式转化为Mat */

        ByteBuffer buffer;
        int rowStride;
        int pixelStride;
        int width = image.getWidth();
        int height = image.getHeight();
        int offset = 0;

        // image格式图片保存在3个planes里，分别为Y, UV, VU
        Image.Plane[] planes = image.getPlanes();
        byte[] data = new byte[image.getWidth() * image.getHeight() * ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8];
        byte[] rowData = new byte[planes[0].getRowStride()];


        for (int i = 0; i < planes.length; i++) {
            buffer = planes[i].getBuffer();
            rowStride = planes[i].getRowStride();
            pixelStride = planes[i].getPixelStride();
            int w = (i == 0) ? width : width / 2;
            int h = (i == 0) ? height : height / 2;
            for (int row = 0; row < h; row++) {
                int bytesPerPixel = ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8;
                if (pixelStride == bytesPerPixel) {
                    int length = w * bytesPerPixel;
                    buffer.get(data, offset, length);

                    if (h - row != 1) {
                        buffer.position(buffer.position() + rowStride - length);
                    }
                    offset += length;
                } else {
                    if (h - row == 1) {
                        buffer.get(rowData, 0, width - pixelStride + 1);
                    } else {
                        buffer.get(rowData, 0, rowStride);
                    }

                    for (int col = 0; col < w; col++) {
                        data[offset++] = rowData[col * pixelStride];
                    }
                }
            }
        }

        Mat mat = new Mat(height + height / 2, width, CvType.CV_8UC1);
        Log.d("TESTMAT", mat.rows() + " " + mat.cols());
        mat.put(0, 0, data);

        return mat;
    }


    public static Mat yuvToBgr(Image image, Mat yuvMat) {
        Mat bgrMat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC4);
        //Log.d("TESTCALL", bgrMat.rows() + " " + bgrMat.cols());
        Imgproc.cvtColor(yuvMat, bgrMat, Imgproc.COLOR_YUV2BGR_I420);
        //Log.d("TESTCALL", yuvMat.rows() + " " + yuvMat.cols());
        return bgrMat;
    }

    /**
     * 获取最贴近手机屏幕长宽比的照片，若存在多张，则选取一个大于预设最低尺寸的最小尺寸
     * @param sizes         能获取的照片尺寸list
     * @param screenWidth   屏幕宽度
     * @param screenHeight  屏幕高度
     * @return              返回的最佳尺寸
     */
    public static Size getRelativeMinSize(List<Size> sizes, int screenWidth, int screenHeight) {
        List<Size> comparedSizes = new ArrayList<>();
        Log.d("TESTCP", screenWidth+" "+screenHeight);
        for (Size size : sizes) {
            if (size.getHeight() >= MIN_HEIGHT && size.getWidth() >= MIN_WIDTH) {
                comparedSizes.add(size);
            }
        }
        for (Size size : comparedSizes) {
            Log.d("ComparedSizes", size.getHeight()+""+size.getWidth());
        }
        double ratio = (double) screenWidth / screenHeight;
        if (comparedSizes.size() > 0) {
            return Collections.min(comparedSizes, new CompareSizesByRatioArea(ratio));
        }
        return Collections.max(sizes, new CompareSizesByArea());
    }

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, choose the smallest one that
     * is at least as large as the respective texture view size, and that is at most as large as the
     * respective max size, and whose aspect ratio matches with the specified value. If such size
     * doesn't exist, choose the largest one that is at most as large as the respective max size,
     * and whose aspect ratio matches with the specified value.
     *
     * @param choices           The list of sizes that the camera supports for the intended output
     *                          class
     * @param textureViewWidth  The width of the texture view relative to sensor coordinate
     * @param textureViewHeight The height of the texture view relative to sensor coordinate
     * @param maxWidth          The maximum width that can be chosen
     * @param maxHeight         The maximum height that can be chosen
     * @param aspectRatio       The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    public static Size chooseOptimalSize(Size[] choices, int textureViewWidth,
                                          int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {

        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth &&
                        option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            return choices[0];
        }
    }

    private static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    private static class CompareSizesByRatioArea implements Comparator<Size> {

        private double widthHeightRatio = HEIGHT_WIDTH_RATIO;

        public CompareSizesByRatioArea(double ratio) {
            widthHeightRatio = ratio;
        }

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            double leftRatio = (double) lhs.getWidth() / lhs.getHeight();
            double rightRatio = (double) rhs.getWidth() / rhs.getHeight();
            double difRatio = Math.abs(leftRatio - widthHeightRatio) - Math.abs(rightRatio - widthHeightRatio);
            if (Math.abs(difRatio) < 0.01) {
                return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                        (long) rhs.getWidth() * rhs.getHeight());
            } else {
                return (int) Math.signum(difRatio);
            }
        }
    }
}
