package com.papermelody.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;

import java.nio.ByteBuffer;

/**
 * Created by HgS_1217_ on 2017/5/8.
 */

public class ImageUtil {
    /**
     * 用于处理图像的工具类
     */

    public static Bitmap imageToByteArray(Image image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);  //由缓冲区存入字节数组
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
