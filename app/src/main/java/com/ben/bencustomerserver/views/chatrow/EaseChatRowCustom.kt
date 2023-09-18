package com.ben.bencustomerserver.views.chatrow

import android.content.Context
import android.view.View
import android.widget.BaseAdapter
import android.widget.TextView
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.model.BaseMessageModel

class EaseChatRowCustom : EaseChatRow {
    private var contentView: TextView? = null

    constructor(context: Context?, isSender: Boolean) : super(context!!, isSender)
    constructor(
        context: Context?,
        message: BaseMessageModel?,
        position: Int,
        adapter: BaseAdapter?
    ) : super(
        context!!, message!!, position, adapter
    )

    override fun onInflateView() {
        inflater.inflate(
            if (!isSender) R.layout.ease_row_received_message else R.layout.ease_row_sent_message,
            this
        )
    }

    override fun onFindViewById() {
        contentView = findViewById<View>(R.id.tv_chatcontent) as TextView
    }

    public override fun onSetUpView() {
//        EMCustomMessageBody
        val msg ="自定义消息"
        contentView!!.text = msg
    }

    fun onAckUserUpdate(count: Int) {
        if (ackedView != null && isSender) {
            ackedView!!.post {
                ackedView!!.visibility = VISIBLE
                ackedView!!.text =
                    String.format(context.getString(R.string.group_ack_read_count), count)
            }
        }
    }

    override fun onMessageCreate() {
        if (progressBar != null) {
            progressBar!!.visibility = VISIBLE
        }
        if (statusView != null) {
            statusView!!.visibility = GONE
        }
    }

    override fun onMessageSuccess() {
        if (progressBar != null) {
            progressBar!!.visibility = GONE
        }
        if (statusView != null) {
            statusView!!.visibility = GONE
        }

        // Show "1 Read" if this msg is a ding-type msg.
//        if (isSender && EaseDingMessageHelper.get().isDingMessage(message) && ackedView != null) {
//            ackedView!!.visibility = VISIBLE
//            val count: Int = message.groupAckCount()
//            ackedView!!.text =
//                String.format(context.getString(R.string.group_ack_read_count), count)
//        }

        // Set ack-user list change listener.
//        EaseDingMessageHelper.get().setUserUpdateListener(message, userUpdateListener)
    }

    override fun onMessageError() {
        super.onMessageError()
        if (progressBar != null) {
            progressBar!!.visibility = GONE
        }
        if (statusView != null) {
            statusView!!.visibility = VISIBLE
        }
    }

    override fun onMessageInProgress() {
        if (progressBar != null) {
            progressBar!!.visibility = VISIBLE
        }
        if (statusView != null) {
            statusView!!.visibility = GONE
        }
    }


}