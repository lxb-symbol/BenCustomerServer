package com.ben.bencustomerserver.listener

import com.ben.bencustomerserver.model.BaseMessageModel

interface OnRecallMessageResultListener {
    /**
     * Recall successful
     * @param originalMessage The message was unsent
     * @param notification  The notification message
     */
    fun recallSuccess(originalMessage: BaseMessageModel?, notification: BaseMessageModel?)

    /**
     * Recall failed
     * @param code
     * @param errorMsg
     */
    fun recallFail(code: Int, errorMsg: String?)
}