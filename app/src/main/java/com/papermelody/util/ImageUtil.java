package com.papermelody.util;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.media.Image;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;

/**
 * Created by HgS_1217_ on 2017/5/8.
 */

public class ImageUtil {
    /**
     * 用于处理图像的工具类
     */

    public static Bitmap imageToBitmap(Mat bgr) {
        Log.d("TESTB", bgr.rows()+" "+bgr.cols());
        Mat rgbaMat = new Mat (bgr.cols(), bgr.rows(), CvType.CV_8U, new Scalar(4));
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
        Log.d("TESTMAT", mat.rows()+" "+mat.cols());
        mat.put(0, 0, data);

        return mat;
    }



    public static Mat yuvToBgr(Image image, Mat yuvMat) {
        Mat bgrMat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC4);
        Log.d("TESTCALL", bgrMat.rows() + " " + bgrMat.cols());
        Imgproc.cvtColor(yuvMat, bgrMat, Imgproc.COLOR_YUV2BGR_I420);
        Log.d("TESTCALL", yuvMat.rows() + " " + yuvMat.cols());
        return bgrMat;
    }
}
