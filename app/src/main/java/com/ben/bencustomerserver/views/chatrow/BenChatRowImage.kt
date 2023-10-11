package com.ben.bencustomerserver.views.chatrow

import android.annotation.SuppressLint
import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.Im
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.Direct
import com.ben.bencustomerserver.model.ImageMessage
import com.ben.bencustomerserver.model.MessageStatus
import com.ben.bencustomerserver.utils.BenImageUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.symbol.lib_net.net.RetrofitClient

/**
 * image for row
 */
class BenChatRowImage : BenChatRowFile {
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
            if (!isSender) R.layout.ben_row_received_picture else R.layout.ben_row_sent_picture,
            this
        )
    }

    override fun onFindViewById() {
        percentageView = findViewById<View>(R.id.percentage) as TextView
        imageView = findViewById<View>(R.id.image) as ImageView
    }

    override fun onSetUpView() {
        if (bubbleLayout != null) {
            bubbleLayout!!.background = null
        }
        val imgBody = message?.innerMessage as ImageMessage
        // received messages
        if (message!!.direct === Direct.RECEIEVE) {
            message?.status=MessageStatus.SUCCESS
            onViewUpdate(message!!)
        }
        showImageView(imgBody)
    }

    override fun onViewUpdate(msg: BaseMessageModel) {
        super.onViewUpdate(msg)
        //此方法中省略掉了之前的有关非自动下载缩略图后展示图片的逻辑
        showImageView(msg.innerMessage as ImageMessage)
    }

    override fun onMessageSuccess() {
        super.onMessageSuccess()
        //即使是sender，发送成功后也要在执行，防止出现图片尺寸不对的问题
        message?.let {
            val inner = it.innerMessage as ImageMessage
            showImageView(inner)
        }
    }

    override fun onMessageInProgress() {
        if (message!!.direct === Direct.SEND) {
            super.onMessageInProgress()
        } else {
            val innerMessage = message?.innerMessage as ImageMessage
            if (TextUtils.isEmpty(innerMessage.localPath)) {
                imageView?.setImageResource(R.drawable.ben_default_image)
            } else {
                progressBar!!.visibility = INVISIBLE
                if (percentageView != null) {
                    percentageView!!.visibility = INVISIBLE
                }
            }
        }
    }

    /**
     * load image into image view
     *
     */
    @SuppressLint("StaticFieldLeak")
    private fun showImageView(message: ImageMessage) {
        val url = RetrofitClient.BASE_URL + message.netPath
        Log.i("symbol", "imgage url :$url")
        imageView?.let {
            Glide.with(context)
                .load(url)
                .apply(RequestOptions().centerCrop().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                .into(it)
        }

    }
}