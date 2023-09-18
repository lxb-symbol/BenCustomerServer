package com.ben.bencustomerserver.views.chatrow

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.utils.EaseImageUtils

class EaseChatRowVideo : EaseChatRowFile {
    private var imageView: ImageView? = null
    private var sizeView: TextView? = null
    private var timeLengthView: TextView? = null
    private var playView: ImageView? = null

    constructor(context: Context?, isSender: Boolean) : super(context, isSender)
    constructor(context: Context?, message: BaseMessageModel?, position: Int, adapter: Any?) : super(
        context,
        message,
        position,
        adapter
    )

    protected override fun onInflateView() {
        inflater.inflate(
            if (!isSender) R.layout.ease_row_received_video else R.layout.ease_row_sent_video,
            this
        )
    }

    protected override fun onFindViewById() {
        imageView = findViewById(R.id.chatting_content_iv) as ImageView?
        sizeView = findViewById(R.id.chatting_size_iv) as TextView?
        timeLengthView = findViewById(R.id.chatting_length_iv) as TextView?
        playView = findViewById(R.id.chatting_status_btn) as ImageView?
        percentageView = findViewById(R.id.percentage) as TextView
    }

    protected override fun onSetUpView() {
        if (bubbleLayout != null) {
            bubbleLayout?.background = null
        }
     // todo 待设置样式
    }

    /**
     * show video thumbnails
     * @param message
     */
    @SuppressLint("StaticFieldLeak")
    private fun showVideoThumbView(message: BaseMessageModel) {
        val params: ViewGroup.LayoutParams =
            EaseImageUtils.showVideoThumb(context, imageView!!, message)
        setBubbleView(params.width, params.height)
    }

    private fun setBubbleView(width: Int, height: Int) {
        val params: ViewGroup.LayoutParams? = bubbleLayout?.layoutParams
        params?.width = width
        params?.height = height
    }

    companion object {
        val TAG = EaseChatRowVideo::class.java.simpleName
    }
}