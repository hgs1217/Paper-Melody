package com.papermelody.util;

import android.support.annotation.Nullable;

import com.papermelody.model.response.CommentResponse;
import com.papermelody.model.response.HttpResponse;
import com.papermelody.model.response.MessageResponse;
import com.papermelody.model.response.OnlineMusicListResponse;
import com.papermelody.model.response.UploadResponse;
import com.papermelody.model.response.UpvoteResponse;
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
import retrofit2.http.Path;
import retrofit2.http.Query;
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

    // 修改昵称
    @FormUrlEncoded
    @POST("user/nickname")
    Observable<UserResponse> updateNickname(@Field("userID") Integer userID,
                                            @Field("nickname") String nickname);

    // 修改密码
    @FormUrlEncoded
    @POST("user/password")
    Observable<UserResponse> updatePassword(@Field("userID") Integer userID,
                                            @Field("oldPassword") String oldPassword,
                                            @Field("newPassword") String newPassword);

    // 修改头像文件名称，每次上传完头像后需调用一次该接口
    @FormUrlEncoded
    @POST("user/avatar")
    Observable<UserResponse> updateAvatar(@Field("userID") Integer userID,
                                          @Field("avatarName") String avatarName);

    // 上传头像
    @Multipart
    @POST("upload/avatar")
    Observable<UploadResponse> uploadAvatar(@Part MultipartBody.Part avatar);

    // 上传作品
    @FormUrlEncoded
    @POST("upload/music")
    Observable<UploadResponse> uploadMusic(@Field("name") String name,
                                           @Field("author") String author,
                                           @Field("authorID") Integer authorID,
                                           @Field("authorAvatarName") String authorAvatarName,
                                           @Field("date") Date date,
                                           @Field("musicName") String musicName,
                                           @Field("imgName") String imgName,
                                           @Field("musicInfo") String musicInfo);

    // 上传作品图片，上传作品前调用
    @Multipart
    @POST("upload/img")
    Observable<UploadResponse> uploadImg(@Part MultipartBody.Part image);

    // 上传音乐，上传作品前调用
    @Multipart
    @POST("upload/musicfile")
    Observable<UploadResponse> uploadMusicFile(@Part MultipartBody.Part file);

    // 音乐圈作品获取
    @GET("onlinemusics")
    Observable<OnlineMusicListResponse> getOnlineMusicList(@Query("order") @Nullable Integer order);

    // 音乐查看人数加一
    @FormUrlEncoded
    @POST("addview")
    Observable<HttpResponse> addView(@Field("musicID") Integer musicID);

    // 音乐点赞
    @FormUrlEncoded
    @POST("upvote/add")
    Observable<HttpResponse> addUpvote(@Field("userID") Integer userID,
                                       @Field("musicID") Integer musicID);

    // 音乐取消点赞
    @FormUrlEncoded
    @POST("upvote/cancel")
    Observable<HttpResponse> cancelUpvote(@Field("userID") Integer userID,
                                          @Field("musicID") Integer musicID);

    // 获取音乐点赞状态
    @GET("upvote/status")
    Observable<UpvoteResponse> getUpvoteStatus(@Query("userID") @Nullable Integer userID,
                                               @Query("musicID") @Nullable Integer musicID);

    // 上传评论
    @FormUrlEncoded
    @POST("upload/comment")
    Observable<HttpResponse> uploadComment(@Field("musicID") Integer musicID,
                                           @Field("user") String user,
                                           @Field("userID") Integer userID,
                                           @Field("userAvatarName") String userAvatarName,
                                           // 若不是评论回复，replyUserID 值取0
                                           @Field("replyUserID") Integer replyUserID,
                                           @Field("comment") String comment,
                                           @Field("time") String time);

    // 获取评论
    @GET("download/comment")
    Observable<CommentResponse> getComment(@Query("musicID") @Nullable String musicID);

    // 获取音乐
    @GET("download/music/{filename}")
    Observable<ResponseBody> downloadMusic(@Path("filename") String fileName);

    // 上传作品获取
    @GET("download/uploadmusics")
    Observable<OnlineMusicListResponse> getUploadMusicList(@Query("userID") @Nullable Integer userID);

    // 收藏作品获取
    @GET("download/favorites")
    Observable<OnlineMusicListResponse> getFavoriteMusicList(@Query("userID") @Nullable Integer userID);

    // 消息获取
    @GET("download/messages")
    Observable<MessageResponse> getMessages(@Query("userID") @Nullable Integer userID,
                                            @Query("hasRead") @Nullable Boolean hasRead);

    // 服务器重启
    @GET("reset")
    Observable<HttpResponse> reset();
}
