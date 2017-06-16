package com.papermelody.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.papermelody.R;
import com.papermelody.model.User;
import com.papermelody.model.response.UploadResponse;
import com.papermelody.model.response.UserResponse;
import com.papermelody.util.App;
import com.papermelody.util.NetworkFailureHandler;
import com.papermelody.util.RetrofitClient;
import com.papermelody.util.SocialSystemAPI;
import com.papermelody.util.StorageUtil;
import com.papermelody.util.ToastUtil;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by HgS_1217_ on 2017/6/16.
 */

public class UpdateUserInfoFragment extends BaseFragment {
    /**
     * 修改用户信息
     */

    // TODO: 暂时把修改密码，修改用户名和上传头像放一起了，到时候你们决定把它放在一个Frag还是多个Frag

    @BindView(R.id.text_nickname)
    TextView textNickname;
    @BindView(R.id.edit_nickname)
    EditText editNickname;
    @BindView(R.id.btn_update_nickname)
    Button btnNickname;
    @BindView(R.id.edit_old_password)
    EditText editOldPassword;
    @BindView(R.id.edit_new_password)
    EditText editNewPassword;
    @BindView(R.id.btn_update_password)
    Button btnPassword;
    @BindView(R.id.img_user_avatar)
    ImageView imgUserAvatar;
    @BindView(R.id.img_avatar_chosen)
    ImageView imgAvatarChosen;
    @BindView(R.id.btn_browse_avatar)
    Button btnBrowseAvatar;
    @BindView(R.id.btn_update_avatar)
    Button btnUpdateAvatar;

    public static final int LOAD_PIC = 0;

    private SocialSystemAPI api;
    private Context context;
    private String filePath = null;

    public static UpdateUserInfoFragment newInstance() {
        UpdateUserInfoFragment fragment = new UpdateUserInfoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_user_info, container, false);
        ButterKnife.bind(this, view);

        initView();

        return view;
    }

    private void initView() {
        textNickname.setText(App.getUser().getNickname());
        btnNickname.setOnClickListener((view) -> {
            updateNickname(editNickname.getText().toString());
        });
        btnBrowseAvatar.setOnClickListener((view) -> {
            chooseImg();
        });
        btnPassword.setOnClickListener((view) -> {
            updatePassword(editOldPassword.getText().toString(), editNewPassword.getText().toString());
        });
        btnUpdateAvatar.setOnClickListener((view) -> {
            uploadAvatar();
        });
    }

    private void updateNickname(String nickname) {
        api = RetrofitClient.getSocialSystemAPI();
        addSubscription(api.updateNickname(App.getUser().getUserID(), nickname)
                .flatMap(NetworkFailureHandler.httpFailureFilter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> ((UserResponse) response).getResult())
                .subscribe(
                        result -> {
                            User user = new User(result, context);
                            App.setUser(user);
                            textNickname.setText(App.getUser().getNickname());
                        }, NetworkFailureHandler.basicErrorHandler
                ));
    }

    private void updatePassword(String oldPw, String newPw) {
        api = RetrofitClient.getSocialSystemAPI();
        addSubscription(api.updatePassword(App.getUser().getUserID(), oldPw, newPw)
                .flatMap(NetworkFailureHandler.httpFailureFilter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> ((UserResponse) response).getResult())
                .subscribe(
                        result -> {
                            User user = new User(result, context);
                            App.setUser(user);
                            ToastUtil.showShort("密码修改为"+App.getUser().getPassword());
                        }, NetworkFailureHandler.loginErrorHandler
                ));
    }

    private void updateAvatar(String avatarName) {
        api = RetrofitClient.getSocialSystemAPI();
        addSubscription(api.updateAvatar(App.getUser().getUserID(), avatarName)
                .flatMap(NetworkFailureHandler.httpFailureFilter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> ((UserResponse) response).getResult())
                .subscribe(
                        result -> {
                            User user = new User(result, context);
                            App.setUser(user);
                            Picasso.with(context).load(App.getUser().getAvatarUrl()).into(imgUserAvatar);
                            ToastUtil.showShort("上传成功");
                        }, NetworkFailureHandler.loginErrorHandler
                ));
    }

    private void uploadAvatar() {
        File file;
        if (filePath != null) {
            file = new File(filePath);
        } else {
            ToastUtil.showShort("文件路径为空");
            return;
        }
        Log.d("TESTPATH", filePath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", file.getName(), requestBody);

        ToastUtil.showLong("上传中");
        api = RetrofitClient.getSocialSystemAPI();
        addSubscription(api.uploadAvatar(body)
                .flatMap(NetworkFailureHandler.httpFailureFilter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> ((UploadResponse) response).getFileName())
                .subscribe(
                        avatarName -> {
                            updateAvatar(avatarName);
                        }, NetworkFailureHandler.uploadErrorHandler
                ));
    }

    private void chooseImg() {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LOAD_PIC:
                if (data != null) {
                    Uri selectedImage = data.getData();
                    String imgPath = StorageUtil.imageGetPath(getActivity(), selectedImage);
                    Log.d("TESTPATH", String.valueOf(imgPath));
                    Picasso.with(context).load(selectedImage).into(imgAvatarChosen);
                    imgAvatarChosen.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    filePath = imgPath;
                }
                break;
        }
    }
}
