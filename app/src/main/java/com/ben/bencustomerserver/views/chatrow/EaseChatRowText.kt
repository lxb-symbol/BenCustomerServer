package com.ben.bencustomerserver.views.chatrow

import android.content.Context
import android.text.Spannable
import android.text.Spanned
import android.text.style.URLSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.model.BaseMessageModel

open class EaseChatRowText : EaseChatRow {
    private var contentView: TextView? = null
    private var translationContentView: TextView? = null
    private var translationStatusView: ImageView? = null
    private var translationContainer: View? = null

    constructor(context: Context?, isSender: Boolean) : super(context!!, isSender)
    constructor(
        context: Context?,
        message: BaseMessageModel?,
        position: Int,
        adapter: Any?
    ) : super(
        context!!, message, position, adapter
    )

    override fun onInflateView() {
        inflater.inflate(
            if (!showSenderType) R.layout.ease_row_received_message else R.layout.ease_row_sent_message,
            this
        )
    }

    override fun onFindViewById() {
        contentView = findViewById<View>(R.id.tv_chatcontent) as TextView
        translationContentView = findViewById<View>(R.id.tv_subContent) as TextView
        translationStatusView = findViewById<View>(R.id.translation_status) as ImageView
        translationContainer = findViewById<View>(R.id.subBubble) as View
    }

    public override fun onSetUpView() {
//        val txtBody: EMTextMessageBody? = message.getBody() as EMTextMessageBody?
//        if (txtBody != null) {
//            val span: Spannable = EaseSmileUtils.getSmiledText(context, txtBody.getMessage())
//            // 设置内容
//            contentView.setText(span, TextView.BufferType.SPANNABLE)
//            contentView!!.setOnLongClickListener { v ->
//                contentView!!.setTag(R.id.action_chat_long_click, true)
//                if (itemClickListener != null) {
//                    itemClickListener!!.onBubbleLongClick(v, message)
//                } else false
//            }
//            replaceSpan()
//            val result: EMTranslationResult =
//                EMClient.getInstance().translationManager().getTranslationResult(
//                    message!!.msgId
//                )
//            if (result != null) {
//                if (result.showTranslation()) {
//                    translationContainer!!.visibility = VISIBLE
//                    translationContentView.setText(result.translatedText())
//                    translationContainer!!.setOnLongClickListener { v ->
//                        contentView!!.setTag(R.id.action_chat_long_click, true)
//                        if (itemClickListener != null) {
//                            itemClickListener!!.onBubbleLongClick(v, message)
//                        } else false
//                    }
//                    translationStatusView!!.setImageResource(R.drawable.translation_success)
//                } else {
//                    translationContainer!!.visibility = GONE
//                }
//            } else {
//                translationContainer!!.visibility = GONE
//            }
//        }
    }

    /**
     * 解决长按事件与relink冲突，参考：https://www.jianshu.com/p/d3bef8449960
     */
    private fun replaceSpan() {
        val spannable: Spannable = contentView!!.text as Spannable
        val spans: Array<URLSpan> =
            spannable.getSpans(0, spannable.length, URLSpan::class.java)
        for (i in spans.indices) {
            var url: String = spans[i].url
            var index: Int = spannable.toString().indexOf(url)
            var end = index + url.length
            if (index == -1) {
                if (url.contains("http://")) {
                    url = url.replace("http://", "")
                } else if (url.contains("https://")) {
                    url = url.replace("https://", "")
                } else if (url.contains("rtsp://")) {
                    url = url.replace("rtsp://", "")
                }
                index = spannable.toString().indexOf(url)
                end = index + url.length
            }
            if (index != -1) {
                spannable.removeSpan(spans[i])
                spannable.setSpan(
                    AutolinkSpan(spans[i].url), index, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE
                )
            }
        }
    }

    override fun onMessageCreate() {
        setStatus(VISIBLE, GONE)
    }

    override fun onMessageSuccess() {
        setStatus(GONE, GONE)

        // Show "1 Read" if this msg is a ding-type msg.

//        if (isSender && EaseDingMessageHelper.get().isDingMessage(message) && ackedView != null) {
//            ackedView!!.visibility = VISIBLE
//            val count: Int = message.groupAckCount()
//            ackedView!!.text =
//                String.format(context.getString(R.string.group_ack_read_count), count)
//        }

        // Set ack-user list change listener.
        // Only use the group ack count from message. - 2022.04.27
        //EaseDingMessageHelper.get().setUserUpdateListener(message, userUpdateListener);
    }

    override fun onMessageError() {
        super.onMessageError()
        setStatus(GONE, VISIBLE)
    }

    override fun onMessageInProgress() {
        setStatus(VISIBLE, GONE)
    }

    /**
     * set progress and status view visible or gone
     * @param progressVisible
     * @param statusVisible
     */
    private fun setStatus(progressVisible: Int, statusVisible: Int) {
        if (progressBar != null) {
            progressBar!!.visibility = progressVisible
        }
        if (statusView != null) {
            statusView!!.visibility = statusVisible
        }
    }

//    private val userUpdateListener: EaseDingMessageHelper.IAckUserUpdateListener =
//        EaseDingMessageHelper.IAckUserUpdateListener { list -> onAckUserUpdate(list.size()) }

    fun onAckUserUpdate(count: Int) {
        if (ackedView == null) {
            return
        }
        ackedView!!.post {
            if (isSender) {
                ackedView!!.visibility = VISIBLE
                ackedView!!.text =
                    String.format(context.getString(R.string.group_ack_read_count), count)
            }
        }
    }
}