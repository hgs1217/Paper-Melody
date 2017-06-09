package com.papermelody.util;

import android.support.annotation.Nullable;

import com.papermelody.model.response.CommentResponse;
import com.papermelody.model.response.HttpResponse;
import com.papermelody.model.response.OnlineMusicListResponse;
import com.papermelody.model.response.UploadImgResponse;
import com.papermelody.model.response.UploadResponse;
import com.papermelody.model.response.UserResponse;

import java.util.Date;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public interface SocialSystemAPI {
    /**
     * 社交系统API，提供与服务器互相交互的接口
     */

    // 登录
    @FormUrlEncoded
    @POST("login")
    Observable<UserResponse> login(@Field("name") String username,
                                   @Field("pw") String password);

    // 注册
    @FormUrlEncoded
    @POST("register")
    Observable<UserResponse> register(@Field("name") String username,
                                      @Field("pw") String password);

    // 上传作品
    @FormUrlEncoded
    @POST("uploadmusic")
    Observable<UploadResponse> uploadMusic(@Field("name") String name,
                                           @Field("author") String author,
                                           @Field("date") Date date,
                                           @Field("link") String link,
                                           @Field("imgName") String imgName);

    // 上传作品图片
    @Multipart
    @POST("uploadimg")
    Observable<UploadImgResponse> uploadImg(@Part MultipartBody.Part image);

    // 音乐圈作品获取
    @GET("onlinemusics")
    Observable<OnlineMusicListResponse> getOnlineMusicList(@Query("order") @Nullable Integer order);

    // 音乐查看人数加一
    @FormUrlEncoded
    @POST("addview")
    Observable<HttpResponse> addView(@Field("musicID") Integer musicID);

    // 音乐点赞人数加一
    @FormUrlEncoded
    @POST("addupvote")
    Observable<HttpResponse> addUpvote(@Field("musicID") Integer musicID);

    //upload comment
    @FormUrlEncoded
    @POST("uploadcomment")
    Observable<HttpResponse> uploadComment(@Field("musicID") String musicID,
                                           @Field("user") String user,
                                           @Field("comment") String comment,
                                           @Field("time") String time);

    // 获取评论
    @GET("getcomment")
    Observable<CommentResponse> getComment(@Query("musicID") @Nullable String musicID);

    // 获取音乐
    @Streaming
    @GET
    Observable<ResponseBody> downloadMusic(@Url String fileUrl);
}
