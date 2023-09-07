package com.ben.bencustomerserver.views.chatrow

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.Direct

class EaseChatRowVoice : EaseChatRowFile {
    private var voiceImageView: ImageView? = null
    private var voiceLengthView: TextView? = null
    private var readStatusView: ImageView? = null
    private var voiceAnimation: AnimationDrawable? = null

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
            if (!showSenderType) R.layout.ease_row_received_voice else R.layout.ease_row_sent_voice,
            this
        )
    }

    override fun onFindViewById() {
        voiceImageView = findViewById<ImageView>(R.id.iv_voice)
        voiceLengthView = findViewById<TextView>(R.id.tv_length)
        readStatusView = findViewById<ImageView>(R.id.iv_unread_voice)
    }

    override fun onSetUpView() {
//        val voiceBody: EMVoiceMessageBody = message.getBody() as EMVoiceMessageBody
//        val len: Int = voiceBody.getLength()
//        var padding = 0
//        if (len > 0) {
//            padding = EaseVoiceLengthUtils.getVoiceLength(getContext(), len)
//            voiceLengthView.setText(voiceBody.getLength() + "\"")
//            voiceLengthView!!.visibility = View.VISIBLE
//        } else {
//            voiceLengthView!!.visibility = View.INVISIBLE
//        }
//        if (!showSenderType) {
//            voiceImageView!!.setImageResource(R.drawable.ease_chatfrom_voice_playing)
//            voiceLengthView!!.setPadding(padding, 0, 0, 0)
//        } else {
//            voiceImageView!!.setImageResource(R.drawable.ease_chatto_voice_playing)
//            voiceLengthView!!.setPadding(0, 0, padding, 0)
//        }
//        if (message?.direct === Direct.RECEIEVE) {
//            if (readStatusView != null) {
//                if (message.isListened()) {
//                    // hide the unread icon
//                    readStatusView!!.visibility = View.INVISIBLE
//                } else {
//                    readStatusView!!.visibility = View.VISIBLE
//                }
//            }
//            Log.d(TAG, "it is receive msg")
//            //TODO symbol
////            if (voiceBody.downloadStatus() === EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
////                voiceBody.downloadStatus() === EMFileMessageBody.EMDownloadStatus.PENDING
////            ) {
////                if (EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {
////                    progressBar.setVisibility(View.VISIBLE)
////                } else {
////                    progressBar.setVisibility(View.INVISIBLE)
////                }
////            } else {
////                progressBar.setVisibility(View.INVISIBLE)
////            }
//        } else {
//            // hide the unread icon
//            readStatusView!!.visibility = View.INVISIBLE
//        }
//
//        // To avoid the item is recycled by listview and slide to this item again but the animation is stopped.
//        val voicePlayer = EaseChatRowVoicePlayer.getInstance(getContext())
//        if (voicePlayer!!.isPlaying && message?.msgId.equals(voicePlayer.currentPlayingId)) {
//            startVoicePlayAnimation()
//        }
    }

    override fun onViewUpdate(msg: BaseMessageModel) {
        super.onViewUpdate(msg)

        // Only the received message has the attachment download status.
        if (message?.direct === Direct.SEND) {
            return
        }
//        val voiceBody: EMVoiceMessageBody = msg.getBody() as EMVoiceMessageBody
//        if (voiceBody.downloadStatus() === EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
//            voiceBody.downloadStatus() === EMFileMessageBody.EMDownloadStatus.PENDING
//        ) {
//            progressBar?.setVisibility(View.VISIBLE)
//        } else {
//            progressBar?.setVisibility(View.INVISIBLE)
//        }
    }

    @SuppressLint("ResourceType")
    fun startVoicePlayAnimation() {
        if (message?.direct === Direct.RECEIEVE) {
            voiceImageView!!.setImageResource(R.anim.voice_from_icon)
        } else {
            voiceImageView!!.setImageResource(R.anim.voice_to_icon)
        }
        voiceAnimation = voiceImageView!!.drawable as AnimationDrawable
        voiceAnimation?.start()

        // Hide the voice item not listened status view.
        if (message?.direct === Direct.RECEIEVE) {
            readStatusView!!.visibility = View.INVISIBLE
        }
    }

    fun stopVoicePlayAnimation() {
        if (voiceAnimation != null) {
            voiceAnimation?.stop()
        }
        if (message?.direct === Direct.RECEIEVE) {
            voiceImageView!!.setImageResource(R.drawable.ease_chatfrom_voice_playing)
        } else {
            voiceImageView!!.setImageResource(R.drawable.ease_chatto_voice_playing)
        }
    }

    companion object {
        private val TAG = EaseChatRowVoice::class.java.simpleName
    }
}