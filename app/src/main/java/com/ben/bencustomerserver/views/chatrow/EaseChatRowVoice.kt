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
import com.ben.bencustomerserver.model.VoiceMessage

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
            if (!isSender) R.layout.ease_row_received_voice else R.layout.ease_row_sent_voice,
            this
        )
    }

    override fun onFindViewById() {
        voiceImageView = findViewById<ImageView>(R.id.iv_voice)
        voiceLengthView = findViewById<TextView>(R.id.tv_length)
        readStatusView = findViewById<ImageView>(R.id.iv_unread_voice)
    }

    override fun onSetUpView() {
        val voiceBody = message?.innerMessage as VoiceMessage
        val len: Int = voiceBody.duration
        var padding = 0
        if (len > 0) {
            voiceLengthView?.text = len.toString()
            voiceLengthView!!.visibility = View.VISIBLE
        } else {
            voiceLengthView!!.visibility = View.INVISIBLE
        }
        if (!isSender) {
            voiceImageView!!.setImageResource(R.drawable.ease_chatfrom_voice_playing)
            voiceLengthView!!.setPadding(padding, 0, 0, 0)
        } else {
            voiceImageView!!.setImageResource(R.drawable.ease_chatto_voice_playing)
            voiceLengthView!!.setPadding(0, 0, padding, 0)
        }
        if (message?.direct === Direct.RECEIEVE) {
            if (readStatusView != null) {
                readStatusView!!.visibility = View.VISIBLE
            }
        } else {
            // hide the unread icon
            readStatusView!!.visibility = View.INVISIBLE
        }

        val voicePlayer = EaseChatRowVoicePlayer.getInstance(context)
        if (voicePlayer!!.isPlaying && message?.msgId.equals(voicePlayer.currentPlayingId)) {
            startVoicePlayAnimation()
        }
    }

    override fun onViewUpdate(msg: BaseMessageModel) {
        super.onViewUpdate(msg)
        // Only the received message has the attachment download status.
        if (message?.direct === Direct.SEND) {
            return
        }

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