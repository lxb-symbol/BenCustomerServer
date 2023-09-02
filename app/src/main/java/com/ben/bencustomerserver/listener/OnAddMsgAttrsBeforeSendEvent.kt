package com.ben.bencustomerserver.listener

import com.ben.bencustomerserver.model.BaseMessageModel


/**
 * 发送消息前，添加属性事件
 */
interface OnAddMsgAttrsBeforeSendEvent {
    /**
     * 发送消息前设置消息属性，比如设置ext
     * @param message
     * @return
     */
    fun addMsgAttrsBeforeSend(message: BaseMessageModel?)
}