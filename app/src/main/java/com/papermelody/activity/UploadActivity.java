package com.papermelody.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.papermelody.R;
import com.papermelody.model.response.HttpResponse;
import com.papermelody.model.response.UploadImgResponse;
import com.papermelody.util.App;
import com.papermelody.util.NetworkFailureHandler;
import com.papermelody.util.RetrofitClient;
import com.papermelody.util.SocialSystemAPI;
import com.papermelody.util.StorageUtil;
import com.papermelody.util.ToastUtil;
import com.squareup.picasso.Picasso;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
    @BindView(R.id.btn_upload_img)
    Button btnUploadImg;
    @BindView(R.id.img_upload)
    ImageView imgUpload;
    @BindView(R.id.btn_upload_confirm)
    Button btnConfirm;

    public static final int LOAD_PIC = 0;

    private String link = null;
    private SocialSystemAPI api;
    private boolean isSuccess = false;
    private String cacheName = null;  // 缓存的文件的名称
    private String name = null;
    private String author = null;
    private Date date = null;
    private Bitmap bmp = null;
    private String filePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = RetrofitClient.getSocialSystemAPI();

        // 默认图片
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.pyj);

        initView();
    }

    private void initView() {
        Picasso.with(this).load(R.drawable.pyj).into(imgUpload);
        btnUploadImg.setOnClickListener((View v) -> {
            chooseImg();
        });
        btnConfirm.setOnClickListener((View v) -> {
            uploadConfirm();
        });
    }

    private void chooseImg() {
        try {
            Intent intent= new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            intent.putExtra("crop", true);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, LOAD_PIC);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LOAD_PIC:
                if (data != null) {
                    Uri selectedImage = data.getData();
                    String imgPath = StorageUtil.imageGetPath(this, selectedImage);
                    Log.d("TESTPATH", String.valueOf(imgPath));
                    Picasso.with(this).load(selectedImage).into(imgUpload);
                    filePath = imgPath;
                }
                break;
        }
    }

    private void uploadConfirm() {
        boolean hasUser = true;
        try {
            author = App.getUser().getUsername();
        } catch (NullPointerException e) {
            ToastUtil.showShort("登录了才能上传哦！");
            hasUser = false;
        }
        if (hasUser) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
//                      这里上传的是data/data/~/cache/目录下已经存在的文件
                    File file = new File(getApplicationContext().getCacheDir()
                            .getAbsolutePath() + cacheName);
                    isSuccess = uploadMusic(R.string.server_ip + "uploadFile",
                            cacheName, file);
                    Log.i("nib", "isSuccess1=" + isSuccess);
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                Log.i("nib", "InterruptedException");
//                e.printStackTrace();
            }
            Log.i("nib", "isSuccess2=" + isSuccess);

            isSuccess = true; // FIXME: 仅供暂时的跟谱模式入口测试，到时候要删掉
            if (isSuccess) {
                name = editMusicTitle.getText().toString();
                date = new Date(System.currentTimeMillis());
                Log.i("nib", date.toString());

                uploadImg();

            } else {
                Log.i("nib", "isSuccess==false");
                ToastUtil.showShort(R.string.upload_failed);
            }
        }
    }

    private void uploadImg() {
        File file;
        if (filePath != null) {
            file = new File(filePath);
        } else {
            String filename = "test.png";
            filePath = Environment.getExternalStorageDirectory() + "/" + filename;
            file = StorageUtil.saveBitmap(filePath, bmp);
        }
        Log.d("TESTPATH", filePath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestBody);

        addSubscription(api.uploadImg(body)
                .flatMap(NetworkFailureHandler.httpFailureFilter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> ((UploadImgResponse) response).getImgName())
                .subscribe(
                        imgName -> {
                            uploadMusicInfo(imgName);

                            /*if (errorCode == 0) {
                                Log.i("nib", "errCode==0");
                                ToastUtil.showShort(R.string.upload_success);
                                Intent intent = new Intent();
                                intent.setClass(this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.i("nib", "errCode!=0");
                                ToastUtil.showShort(R.string.upload_failed);
                            }*/
                        }, NetworkFailureHandler.uploadErrorHandler
                ));
    }

    private void uploadMusicInfo(String imgName) {
        addSubscription(api.uploadMusic(name, author, date, "", imgName)  // FIXME: link 需要修改
                .flatMap(NetworkFailureHandler.httpFailureFilter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> ((HttpResponse) response).getError())
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
                        }, NetworkFailureHandler.uploadErrorHandler
                ));
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
