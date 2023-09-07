package com.ben.bencustomerserver.views.chatrow

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ben.bencustomerserver.model.BaseMessageModel

class EaseChatRowVideo : EaseChatRowFile {
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
    ) : super(context, message, position, adapter)

    override fun onInflateView() {
        inflater.inflate(
            if (!showSenderType) R.layout.ease_row_received_video else R.layout.ease_row_sent_video,
            this
        )
    }

    override fun onFindViewById() {
        imageView = findViewById<View>(R.id.chatting_content_iv) as ImageView
        sizeView = findViewById<View>(R.id.chatting_size_iv) as TextView
        timeLengthView = findViewById<View>(R.id.chatting_length_iv) as TextView
        playView = findViewById<View>(R.id.chatting_status_btn) as ImageView
        percentageView = findViewById<View>(R.id.percentage) as TextView
    }

    override fun onSetUpView() {
        if (bubbleLayout != null) {
            bubbleLayout!!.background = null
        }
        val videoBody: EMVideoMessageBody = message.getBody() as EMVideoMessageBody
        if (videoBody.getDuration() > 0) {
            val time: String = EaseDateUtils.toTime(videoBody.getDuration())
            timeLengthView!!.text = time
        }
        if (message!!.direct() === BaseMessageModel.Direct.RECEIVE) {
            if (videoBody.getVideoFileLength() > 0) {
                val size: String = TextFormater.getDataSize(videoBody.getVideoFileLength())
                sizeView!!.text = size
            }
        } else {
            val videoFileLength: Long = videoBody.getVideoFileLength()
            sizeView.setText(TextFormater.getDataSize(videoFileLength))
        }
        Log.d(TAG, "video thumbnailStatus:" + videoBody.thumbnailDownloadStatus())
        if (message!!.direct() === BaseMessageModel.Direct.RECEIVE) {
            if (videoBody.thumbnailDownloadStatus() === EMFileMessageBody.EMDownloadStatus.DOWNLOADING) {
                imageView!!.setImageResource(R.drawable.ease_default_image)
            } else {
                // System.err.println("!!!! not back receive, show image directly");
                imageView!!.setImageResource(R.drawable.ease_default_image)
                showVideoThumbView(message)
            }
        } else {
            if (videoBody.thumbnailDownloadStatus() === EMFileMessageBody.EMDownloadStatus.DOWNLOADING || videoBody.thumbnailDownloadStatus() === EMFileMessageBody.EMDownloadStatus.PENDING || videoBody.thumbnailDownloadStatus() === EMFileMessageBody.EMDownloadStatus.FAILED) {
                if (progressBar != null) {
                    progressBar!!.visibility = INVISIBLE
                }
                if (percentageView != null) {
                    percentageView!!.visibility = INVISIBLE
                }
                if (videoBody.thumbnailDownloadStatus() === EMFileMessageBody.EMDownloadStatus.PENDING) {
                    showVideoThumbView(message)
                } else {
                    imageView!!.setImageResource(R.drawable.ease_default_image)
                }
            } else {
                if (progressBar != null) {
                    progressBar!!.visibility = GONE
                }
                if (percentageView != null) {
                    percentageView!!.visibility = GONE
                }
                imageView!!.setImageResource(R.drawable.ease_default_image)
                showVideoThumbView(message)
            }
        }
    }

    /**
     * show video thumbnails
     * @param message
     */
    @SuppressLint("StaticFieldLeak")
    private fun showVideoThumbView(message: BaseMessageModel) {
        val params: ViewGroup.LayoutParams =
            EaseImageUtils.showVideoThumb(context, imageView, message)
        setBubbleView(params.width, params.height)
    }

    private fun setBubbleView(width: Int, height: Int) {
        val params = bubbleLayout!!.layoutParams
        params.width = width
        params.height = height
    }

    companion object {
        private val TAG = EaseChatRowVideo::class.java.simpleName
    }
}