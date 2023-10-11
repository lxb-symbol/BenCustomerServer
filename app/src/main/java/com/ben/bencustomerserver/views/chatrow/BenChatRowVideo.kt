package com.ben.bencustomerserver.views.chatrow

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.Direct
import com.ben.bencustomerserver.model.MessageStatus
import com.ben.bencustomerserver.model.VideoMessage
import com.ben.bencustomerserver.model.VoiceMessage
import com.ben.bencustomerserver.utils.BenImageUtils
import com.ben.bencustomerserver.utils.HttpUtils
import com.ben.lib_picture_selector.ImageLoaderUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.luck.picture.lib.utils.ToastUtils
import com.symbol.lib_net.net.RetrofitClient
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch

class BenChatRowVideo : BenChatRowFile {
    private var imageView: ImageView? = null
    private var sizeView: TextView? = null
    private var timeLengthView: TextView? = null
    private var playView: ImageView? = null

    constructor(context: Context?, isSender: Boolean) : super(context, isSender)
    constructor(
        context: Context?,
        message: BaseMessageModel?,
        position: Int,
        adapter: Any?
    ) : super(
        context,
        message,
        position,
        adapter
    )

    override fun onInflateView() {
        inflater.inflate(
            if (!isSender) R.layout.ben_row_received_video else R.layout.ben_row_sent_video,
            this
        )
    }

    override fun onFindViewById() {
        imageView = findViewById<ImageView>(R.id.chatting_content_iv)
        sizeView = findViewById<TextView>(R.id.chatting_size_iv)
        timeLengthView = findViewById<TextView>(R.id.chatting_length_iv)
        playView = findViewById<ImageView>(R.id.chatting_status_btn)
        percentageView = findViewById<TextView>(R.id.percentage)
    }

    override fun onSetUpView() {
        if (bubbleLayout != null) {
            bubbleLayout?.background = null
        }
        val innerMessage = message?.innerMessage as VideoMessage
        val localPath = innerMessage.localPath
        if (!TextUtils.isEmpty(localPath)) {
            imageView?.let {
                Glide.with(context).load(localPath)
                    .apply(RequestOptions.centerCropTransform().frame(1000))
                    .into(it)
            }
        } else {
            val netPath = RetrofitClient.BASE_URL + innerMessage.netPath
            showVideoThumbView(netPath)
            MainScope().launch {
                val filePath = context.externalCacheDir?.path
                val name = "${netPath.hashCode()}.mp4"
                val b = HttpUtils.downFile(context, netPath, filePath ?: "", name)
                Log.i("symbol-5", "video-->$b")
                if (b) {
                    showVideoThumbView(str = "$filePath/$name")
                    (message?.innerMessage as VideoMessage).localPath = "$filePath/$name"
                }
            }

        }


    }

    /**
     * show video thumbnails
     * @param message
     */
    @SuppressLint("StaticFieldLeak")
    private fun showVideoThumbView(str: String) {
        imageView?.let {
            Glide.with(context).load(str)
                .apply(RequestOptions.centerCropTransform().frame(1000))
                .into(it)
        }

    }

    private fun setBubbleView(width: Int, height: Int) {
        val params: ViewGroup.LayoutParams? = bubbleLayout?.layoutParams
        params?.width = width
        params?.height = height
    }

    companion object {
        val TAG = BenChatRowVideo::class.java.simpleName
    }
}