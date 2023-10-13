package com.ben.bencustomerserver.presenter

import android.text.TextUtils
import android.util.Log
import com.ben.bencustomerserver.connnect.RecieveMessageManager
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.MessageStatus
import com.ben.bencustomerserver.vm.ChatViewModel

class BenChatMessagePresenterImpl : BenChatMessagePresenter() {
    override fun loadLocalMessages(pageSize: Int) {
        Log.i("BenChatMessagePresenterImpl","loadLocalMessages")
        if (isActive) {
            runOnUI {
                if (mView != null) {
                    mView!!.loadNoMoreLocalMsg()
                }
            }
        }
    }
    override fun loadMoreLocalMessages(msgId: String?, pageSize: Int) {
        Log.i("BenChatMessagePresenterImpl","loadMoreLocalMessages")
        if (isActive) {
            runOnUI {
                if (mView != null) {
                    mView!!.loadNoMoreLocalMsg()
                }
            }
        }
    }
    override fun loadServerMessages(pageSize: Int) {
        Log.i("BenChatMessagePresenterImpl","loadServerMessages")

        (viewModel as ChatViewModel).chatMessages(1)
        if (isActive) {
            runOnUI {
                if (mView != null) {
                    mView!!.loadNoMoreLocalMsg()
                }
            }
        }

    }
    override fun loadMoreServerMessages(msgId: String?, pageSize: Int) {


        Log.i("BenChatMessagePresenterImpl","loadServerMessages")
        if (isActive) {
            runOnUI {
                if (mView != null) {
                    mView!!.loadNoMoreLocalMsg()
                }
            }
        }
    }
    override fun refreshCurrentConversation() {
        (viewModel as ChatViewModel).chatMessages(0)

        if (isActive) {
            runOnUI {
                if (mView != null) {
                    mView!!.loadNoMoreLocalMsg()
                }
            }
        }
    }

    override fun refreshToLatest() {


        val allMessages: List<BaseMessageModel> = RecieveMessageManager.msgs
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