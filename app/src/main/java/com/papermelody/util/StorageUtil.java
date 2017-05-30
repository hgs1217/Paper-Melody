package com.papermelody.util;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by HgS_1217_ on 2017/5/29.
 */

public class StorageUtil {

    public static File saveBitmap(String filepath, Bitmap bmp) {
        File f = new File(filepath);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Log.i(TAG, "已经保存");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }
}
