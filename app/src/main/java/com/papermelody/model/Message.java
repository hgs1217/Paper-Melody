package com.papermelody.model;

import com.papermelody.model.response.MessageInfo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by HgS_1217_ on 2017/6/15.
 */

public class Message implements Serializable {
    /**
     * 消息类
     */

    public static final String SERIAL_MESSAGE = "SERIAL_MESSAGE";

    private String author;
    private Date createTime;
    private String message;

    public Message() { }

    public Message(MessageInfo info) {
        author = info.getAuthor();
        createTime = info.getCreateTime();
        message = info.getMessage();
    }

    public String getAuthor() {
        return author;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public String getMessage() {
        return message;
    }
}
