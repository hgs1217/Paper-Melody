package com.papermelody.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.papermelody.R;
import com.papermelody.fragment.UserFragment;
import com.papermelody.model.User;
import com.papermelody.model.response.HttpResponse;
import com.papermelody.model.response.UploadResponse;
import com.papermelody.model.response.UserResponse;
import com.papermelody.util.App;
import com.papermelody.util.NetworkFailureHandler;
import com.papermelody.util.RetrofitClient;
import com.papermelody.util.SocialSystemAPI;
import com.papermelody.util.ToastUtil;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class UploadActivity extends BaseActivity {
    /**
     * 用例：上传作品
     * 上传作品页面
     */
    @BindView(R.id.edit_music_title)
    EditText editMusicTitle;
    @BindView(R.id.edit_music_des)
    EditText editMusicDes;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    private String link = null;
    private SocialSystemAPI api;
    private boolean isSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        api = RetrofitClient.getSocialSystemAPI();
        btnConfirm.setOnClickListener((View v) -> {
            boolean hasUser = true;
            String author = null;
            try {
                author = ((App) getApplication()).getUser().getUsername();
            } catch (NullPointerException e) {
                ToastUtil.showShort("登录了才能上传哦！");
                hasUser = false;
            }
            if (hasUser) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
//                这里上传的是data/data/目录下已经存在的文件
                        isSuccess = uploadMusic("http://59.78.0.200:8080/uploadFile",
                                "Kissbye.mid", new File(getApplicationContext().getFilesDir()
                                        .getAbsolutePath() + "/Kissbye.mid"));
                        Log.i("nib", "isSuccess1==" + isSuccess);
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
//                    ToastUtil.showShort("InterruptedException");
                    Log.i("nib", "InterruptedException");
//                e.printStackTrace();
                }
                Log.i("nib", "isSuccess2==" + isSuccess);
                if (isSuccess) {
                    String name = editMusicTitle.getText().toString();
                    Date date = new Date(System.currentTimeMillis());
                    Log.i("nib", date.toString());
                    addSubscription(api.uploadMusic(name, author, date, link)
                            .flatMap(NetworkFailureHandler.httpFailureFilter)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .map(response -> ((UploadResponse) response).getError())
                            .subscribe(
                                    errorCode -> {
                                        if (errorCode == 0) {
                                            Log.i("nib", "errCode==0");
                                            ToastUtil.showShort(R.string.upload_success);
                                            Intent intent = new Intent();
                                            intent.setClass(this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Log.i("nib", "errCode!=0");
                                            ToastUtil.showShort(R.string.upload_failed);
                                        }
                                    }
                                    ,
                                    NetworkFailureHandler.uploadErrorHandler
                            ));
                } else {
                    Log.i("nib", "isSuccess==false");
                    ToastUtil.showShort(R.string.upload_failed);
                }
            }
        });
    }

    @Override
    protected int getContentViewId() {

        return R.layout.activity_upload;
    }

    //    将文件uploadFile上传到actionUrl，以newName重命名
    private boolean uploadMusic(String actionUrl, String newName, File uploadFile) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        Log.i("nib", "uploading");
        try {
            URL url = new URL(actionUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
        /* 允许Input、Output，不使用Cache */
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
        /* 设置传送的method=POST */
            con.setRequestMethod("POST");
        /* setRequestProperty */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
        /* 设置DataOutputStream */
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; "
                    + "name=\"userfile\";filename=\"" + newName + "\"" + end);
            ds.writeBytes(end);

        /* 取得文件的FileInputStream */
            FileInputStream fStream = new FileInputStream(uploadFile);
        /* 设置每次写入1024bytes */
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int length = -1;
        /* 从文件读取数据至缓冲区 */
            while ((length = fStream.read(buffer)) != -1) {
            /* 将资料写入DataOutputStream中 */
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);

            // -----
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data;name=\"name\"" + end);
            ds.writeBytes(end + URLEncoder.encode("HgS diao", "UTF-8") + end);
            // -----

            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
        /* close streams */
            fStream.close();
            ds.flush();

        /* 取得Response内容 */
            InputStream is = con.getInputStream();
            int ch;
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            link = b.toString();
            Log.i("nib", b.toString());

        /* 关闭DataOutputStream */
            ds.close();

            return !b.toString().equals("error");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("nib", "failed");
            return false;
        }
    }
}
