package com.papermelody.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.papermelody.R;
import com.papermelody.fragment.MusicHallFragment;
import com.papermelody.model.OnlineMusic;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import com.papermelody.R;
import com.papermelody.fragment.ListenFragment;
import com.papermelody.util.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class OnlineListenActivity extends BaseActivity {
    /**
     * 用例：用户试听
     * 音乐圈中网络作品的试听页面，其中试听部分是用Fragment实现，音乐信息、评论等内容布局在Activity中
     */
    @BindView(R.id.btn_download)
    Button btnDownload;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private String fileName;    // 从intent中得到文件名称，下载到本地然后播放

    private OnlineMusic onlineMusic;
    private Thread downloadThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
//         获取从音乐圈传入的onlineMusic实例
        onlineMusic = (OnlineMusic) intent.getSerializableExtra(MusicHallFragment.SERIAL_ONLINEMUSIC);
//         测试用，先同步server代码
//        fileName = onlineMusic.getFilename();
        fileName = "Kissbye.mid";
        Log.i("nib", onlineMusic.getMusicName());
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_online_listen, ListenFragment.newInstance(fileName));
        transaction.commit();

        downloadThread = new Thread(() -> {
            Log.i("nib", "downloading");
            String dataPath = getApplicationContext().getFilesDir().getAbsolutePath() + "/";
            String sourceURL = getString(R.string.server_ip) + "uploaded/" + fileName;
            Log.i("nib", sourceURL);
            boolean downloadResult = download(sourceURL, dataPath, fileName);
            if (downloadResult) {
                ToastUtil.showShort(R.string.download_success);
            } else {
                ToastUtil.showShort(R.string.download_failed);
            }
        });

        // TODO 不知道为什么点击按钮后就跳到一个空白界面然后退出，尽管文件确实下载下来了
        btnDownload.setOnClickListener((View v) -> {
            downloadThread.start();
        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_online_listen;
    }

    // strurl要下载的文件的url，path保存的路径，filename文件名
    private boolean download(String strurl, String path, String fileName) {
        InputStream is = null;
        OutputStream os = null;
        URL url = null;
        try {
            //创建文件夹
            File f = new File(path);
            if (!f.exists()) {
                f.mkdir();
            }
            //创建文件
            File file = new File(path + fileName);
            //判断是否存在文件
            if (file.exists()) {
                //创建新文件
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }
            //创建并打开连接
            url = new URL(strurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //文件输入流
            is = conn.getInputStream();
            //输出流
            os = new FileOutputStream(file);
            byte buffer[] = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
            Log.i("nib", "download success");
            return true;
        } catch (IOException e) {
            Log.i("nib", e.toString());
            return false;
        } finally {
            try {
                os.close();
                is.close();
            } catch (Exception e) {
                Log.i("nib", e.toString());
            }
        }

    }
}
