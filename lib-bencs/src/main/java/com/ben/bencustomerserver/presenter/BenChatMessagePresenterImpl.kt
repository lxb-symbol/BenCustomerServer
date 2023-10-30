package com.ben.bencustomerserver.presenter

import android.text.TextUtils
import android.util.Log
import com.ben.bencustomerserver.connnect.RecieveMessageManager
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.MessageStatus
import com.ben.bencustomerserver.model.SearchDirection
import com.ben.bencustomerserver.vm.ChatViewModel

class BenChatMessagePresenterImpl : BenChatMessagePresenter() {

    var currentPage = 1
    override fun loadLocalMessages(pageSize: Int) {
        Log.i("BenChatMessagePresenterImpl", "loadLocalMessages")
        if (isActive) {
            runOnUI {
                if (mView != null) {
                    mView!!.loadNoMoreLocalMsg()
                }
            }
        }
    }

    override fun loadMoreLocalMessages(msgId: String?, pageSize: Int) {
        Log.i("BenChatMessagePresenterImpl", "loadMoreLocalMessages")
        currentPage++
        (viewModel as ChatViewModel).chatMessages(currentPage)
        if (isActive) {
            runOnUI {
                if (mView != null) {
                    mView!!.loadNoMoreLocalMsg()
                }
            }
        }
    }

    override fun loadMoreLocalHistoryMessages(
        msgId: String?,
        pageSize: Int,
        direction: SearchDirection
    ) {
        currentPage++
        (viewModel as ChatViewModel).chatMessages(currentPage)
        if (isActive) {
            runOnUI {
                mView!!.loadMoreLocalHistoryMsgSuccess(null, SearchDirection.UP)
            }
        }
    }

    override fun loadServerMessages(pageSize: Int) {
        Log.i("BenChatMessagePresenterImpl", "loadServerMessages")

        (viewModel as ChatViewModel).chatMessages(currentPage)
        if (isActive) {
            runOnUI {
                if (mView != null) {
                    mView!!.loadNoMoreLocalMsg()
                }
            }
        }

    }

    override fun loadMoreServerMessages(msgId: String?, pageSize: Int) {
        currentPage++

        (viewModel as ChatViewModel).chatMessages(currentPage)

        Log.i("BenChatMessagePresenterImpl", "loadServerMessages")
        if (isActive) {
            runOnUI {
                if (mView != null) {
                    mView!!.loadNoMoreLocalMsg()
                }
            }
        }
    }

    override fun refreshCurrentConversation() {
        currentPage = 1
        RecieveMessageManager.msgs.clear()
        (viewModel as ChatViewModel).chatMessages(currentPage)

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