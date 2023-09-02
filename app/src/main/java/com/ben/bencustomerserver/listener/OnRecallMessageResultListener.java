package com.ben.bencustomerserver.listener;


import com.ben.bencustomerserver.model.BaseMessageModel;

public interface OnRecallMessageResultListener {
    /**
     * Recall successful
     * @param originalMessage The message was unsent
     * @param notification  The notification message
     */
    void recallSuccess(BaseMessageModel originalMessage, BaseMessageModel notification);

    /**
     * Recall failed
     * @param code
     * @param errorMsg
     */
    void recallFail(int code, String errorMsg);
}
