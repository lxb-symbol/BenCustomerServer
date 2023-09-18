package com.ben.bencustomerserver.views.chatrow

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.Direct
import com.bumptech.glide.Glide

/**
 * image for row
 */
class EaseChatRowImage : EaseChatRowFile {
    protected var imageView: ImageView? = null

    constructor(context: Context?, isSender: Boolean) : super(context, isSender)
    constructor(
        context: Context?,
        message: BaseMessageModel?,
        position: Int,
        adapter: Any?
    ) : super(context, message, position, adapter)

    override fun onInflateView() {
        inflater.inflate(
            if (!showSenderType) R.layout.ease_row_received_picture else R.layout.ease_row_sent_picture,
            this
        )
    }

    override fun onFindViewById() {
        percentageView = findViewById<View>(R.id.percentage) as TextView
        imageView = findViewById<View>(R.id.image) as ImageView
    }

    override fun onSetUpView() {
//        if (bubbleLayout != null) {
//            bubbleLayout!!.background = null
//        }
//        imgBody = message.getBody() as EMImageMessageBody?
//        // received messages
//        if (message!!.direct() === BaseMessageModel.Direct.RECEIVE) {
//            val params: ViewGroup.LayoutParams = EaseImageUtils.getImageShowSize(context, message)
//            val layoutParams = imageView!!.layoutParams
//            layoutParams.width = params.width
//            layoutParams.height = params.height
//            return
//        }
//        showImageView(message)
    }

    override fun onViewUpdate(msg: BaseMessageModel) {
//        super.onViewUpdate(msg)
        //此方法中省略掉了之前的有关非自动下载缩略图后展示图片的逻辑
    }

    override fun onMessageSuccess() {
        super.onMessageSuccess()
        //即使是sender，发送成功后也要在执行，防止出现图片尺寸不对的问题
        message?.let { showImageView(it) }
    }

    override fun onMessageInProgress() {
        if (message!!.direct === Direct.SEND) {
            super.onMessageInProgress()
        } else {
//            if (EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {
//                //imageView.setImageResource(R.drawable.ease_default_image);
//            } else {
//                progressBar!!.visibility = INVISIBLE
//                if (percentageView != null) {
//                    percentageView!!.visibility = INVISIBLE
//                }
//            }
        }
    }

    /**
     * load image into image view
     *
     */
    @SuppressLint("StaticFieldLeak")
    private fun showImageView(message: BaseMessageModel) {
//        Glide.with(context)
//            .load()
    }
}