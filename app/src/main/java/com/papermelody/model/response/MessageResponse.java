package com.papermelody.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by HgS_1217_ on 2017/6/15.
 */

public class MessageResponse extends HttpResponse {
    /**
     * 用于处理消息获取的响应
     */

    @SerializedName("result")
    private MessageListInfo result;

    public MessageListInfo getResult() {
        return result;
    }

    public class MessageListInfo {

        @SerializedName("count")
        private Integer count;
        @SerializedName("newMsgNum")
        private Integer newMsgNum;
        @SerializedName("messages")
        private List<MessageInfo> messages;

        public MessageListInfo (Integer count, Integer newMsgNum, List<MessageInfo> messages) {
            this.count = count;
            this.newMsgNum = newMsgNum;
            this.messages = messages;
        }

        public Integer getCount() {
            return count;
        }

        public Integer getNewMsgNum() {
            return newMsgNum;
        }

        public List<MessageInfo> getMessages() {
            return messages;
        }
    }

}
