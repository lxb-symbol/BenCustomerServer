package com.ben.bencustomerserver.presenter

import android.text.TextUtils
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.MessageStatus

class EaseChatMessagePresenterImpl : EaseChatMessagePresenter() {
    override fun loadLocalMessages(pageSize: Int) {

    }
    override fun loadMoreLocalMessages(msgId: String?, pageSize: Int) {

    }
    override fun loadServerMessages(pageSize: Int) {}
    override fun loadMoreServerMessages(msgId: String?, pageSize: Int) {}
    override fun refreshCurrentConversation() {

        // TODO 对
        val allMessages: List<BaseMessageModel?> = ArrayList()
        if (isActive) {
            runOnUI {
                mView?.refreshCurrentConSuccess(allMessages, false)
            }
        }
    }

    override fun refreshToLatest() {

//        conversation.getAllMessages();
        val allMessages: List<BaseMessageModel?> = ArrayList()
        if (isActive) {
            runOnUI {
                if (mView != null) {
                    mView!!.refreshCurrentConSuccess(allMessages, true)
                }
            }
        }
    }

    /**
     * 判断是否是消息id
     *
     * @param msgId
     * @return
     */
    fun isMessageId(msgId: String?): Boolean {
        //可以允许消息id为空
        return TextUtils.isEmpty(msgId)
        // TODO: 2023/9/7
    }

    /**
     * @param messages
     */
    private fun checkMessageStatus(messages: List<BaseMessageModel>?) {
        if (messages.isNullOrEmpty()) {
            return
        }
        for (message in messages) {
            if (message.status !== MessageStatus.SUCCESS && message.status !== MessageStatus.FAIL) {
                message.status = MessageStatus.FAIL
            }
        }
    }
}