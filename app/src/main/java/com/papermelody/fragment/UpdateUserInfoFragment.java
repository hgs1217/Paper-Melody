package com.papermelody.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import de.hdodenhof.circleimageview.CircleImageView;
import me.shaohui.bottomdialog.BottomDialog;
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


    @BindView(R.id.card_user_avatar)
    CardView cardUserAvatar;
    @BindView(R.id.card_user_nickname)
    CardView cardUserNickname;
    @BindView(R.id.card_user_password)
    CardView cardUserPassword;
    @BindView(R.id.card_user_username)
    CardView cardUserUsername;
    @BindView(R.id.img_user_avatar)
    CircleImageView imgUserAvatar;
    @BindView(R.id.text_user_nickname)
    TextView textUserNickname;
    @BindView(R.id.text_user_username)
    TextView textUserUsername;

    public static final int LOAD_PIC = 0;

    private SocialSystemAPI api;
    private Context context;
    private String filePath = null;
    private CircleImageView imgSelectAvatar;
    private Button btnDialogCancel, btnDialogConfirm;
    private BottomDialog bottomDialog;
    private EditText editText, editText2;

    private static UpdateUserInfoFragment fragment;

    public static UpdateUserInfoFragment newInstance() {
        if (fragment != null) {
            return fragment;
        }
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
        textUserUsername.setText(App.getUser().getUsername());
        textUserNickname.setText(App.getUser().getNickname());
        String avatarUrl = App.getUser().getAvatarUrl();
        if (avatarUrl != null && avatarUrl.length() > 0) {
            Picasso.with(context).load(avatarUrl).into(imgUserAvatar);
        } else {
            imgUserAvatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_tag_faces_black_48dp));
        }
        cardUserAvatar.setOnClickListener((view) -> {
            chooseImg();
            bottomDialog = BottomDialog.create(getFragmentManager())
                    .setLayoutRes(R.layout.dialog_avatar)
                    .setCancelOutside(true);    // 点击外部是否可以关闭对话框
            bottomDialog.setViewListener((v) -> {
                imgSelectAvatar = (CircleImageView) v.findViewById(R.id.img_select_avatar);
                btnDialogConfirm = (Button) v.findViewById(R.id.btn_avatar_confirm);
                btnDialogCancel = (Button) v.findViewById(R.id.btn_avatar_cancel);
                btnDialogCancel.setOnClickListener((view1) ->
                        bottomDialog.dismiss()
                );
                btnDialogConfirm.setOnClickListener((view2) ->
                        uploadAvatar()
                );
            });
            bottomDialog.show();

        });
        cardUserNickname.setOnClickListener((view) -> {
            bottomDialog = BottomDialog.create(getFragmentManager())
                    .setLayoutRes(R.layout.dialog_nickname)
                    .setCancelOutside(true);
            bottomDialog.setViewListener((v) -> {
                editText = (EditText) v.findViewById(R.id.edit_user_nickname);
                btnDialogConfirm = (Button) v.findViewById(R.id.btn_nickname_confirm);
                btnDialogCancel = (Button) v.findViewById(R.id.btn_nickname_cancel);
                btnDialogCancel.setOnClickListener((view1) -> {
                    hideKeyBoard(editText);
                    bottomDialog.dismiss();
                });
                btnDialogConfirm.setOnClickListener((view2) -> {
                    String newNickname = editText.getText().toString();
                    Log.i("nib", newNickname);
//                        if (newNickname.length() <= 6) {
//                            ToastUtil.showShort(R.string.less_than_6_chars);
//                        } else
                    if (newNickname.length() > 20) {
                        ToastUtil.showShort(R.string.more_than_20_chars);
                    } else {
                        updateNickname(newNickname);
                    }
                });
                showKeyBoard(editText);
            });
            bottomDialog.show();
        });
        cardUserPassword.setOnClickListener((view) -> {
            bottomDialog = BottomDialog.create(getFragmentManager())
                    .setLayoutRes(R.layout.dialog_password)
                    .setCancelOutside(true);
            bottomDialog.setViewListener((v) -> {
                editText = (EditText) v.findViewById(R.id.edit_old_password);
                editText2 = (EditText) v.findViewById(R.id.edit_new_password);
                btnDialogConfirm = (Button) v.findViewById(R.id.btn_password_confirm);
                btnDialogCancel = (Button) v.findViewById(R.id.btn_password_cancel);
                btnDialogCancel.setOnClickListener((view1) -> {
                    hideKeyBoard(editText);
                    bottomDialog.dismiss();
                });
                btnDialogConfirm.setOnClickListener((view2) -> {
                    String oldPassword = editText.getText().toString();
                    if (!oldPassword.equals(App.getUser().getPassword())) {
                        ToastUtil.showShort(R.string.old_password_wrong);
                    } else {
                        updatePassword(editText.getText().toString(), editText2.getText().toString());
                    }
                });
                showKeyBoard(editText);
            });
            bottomDialog.show();
        });
        cardUserUsername.setOnClickListener((v) ->
                ToastUtil.showShort("你的名字是——" + App.getUser().getUsername())
        );
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
                            textUserNickname.setText(App.getUser().getNickname());
                            hideKeyBoard(editText);
                            bottomDialog.dismiss();
                            ToastUtil.showShort(R.string.edit_success);
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
                            hideKeyBoard(editText);
                            ToastUtil.showShort("密码修改为" + App.getUser().getPassword());
                            bottomDialog.dismiss();
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
                            bottomDialog.dismiss();
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
                    Picasso.with(context).load(selectedImage).into(imgSelectAvatar);
                    filePath = imgPath;
                }
                break;
        }
    }

    private void showKeyBoard(EditText et) {
        // TODO: 弹出输入框不起作用
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    private void hideKeyBoard(EditText et) {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(et.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
