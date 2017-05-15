package com.papermelody.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


/**
 * Created by 潘宇杰 on 2017-5-15 0015.
 */

public class CommentResponse extends HttpResponse {
    @SerializedName("result")
    private ArrayList<String> result;

    public CommentResponse(ArrayList<String> x) {
        result = x;
    }

    public ArrayList<String> getResult() {
        return result;
    }
}
