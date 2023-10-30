package com.ben.bencustomerserver.views.chatrow

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.VoiceMessage
import java.io.IOException

/**
 * Created by zhangsong on 17-10-20.
 */
class BenChatRowVoicePlayer private constructor(cxt: Context) {
    private val baseContext: Context
    private val audioManager: AudioManager
    val player: MediaPlayer

    /**
     * May null, please consider.
     *
     * @return
     */
    var currentPlayingId: String? = null
        private set
    private var onCompletionListener: OnCompletionListener? = null

    init {
        baseContext = cxt.applicationContext
        audioManager = baseContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        player = MediaPlayer()
    }

    val isPlaying: Boolean
        get() = player.isPlaying

    fun play(msg: BaseMessageModel, listener: OnCompletionListener?) {
        val voiceMessage =msg.innerMessage as VoiceMessage
        if (player.isPlaying) {
            stop()
        }
        currentPlayingId = msg.msgId
        onCompletionListener = listener
        try {
            setSpeaker()
            player.setDataSource(baseContext,Uri.parse(voiceMessage.localPath))
            player.prepare()
            player.setOnCompletionListener {
                stop()
                currentPlayingId = null
                onCompletionListener = null
            }
            player.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun stop() {
        player.stop()
        player.reset()
        /**
         * This listener is to stop the voice play animation currently, considered the following 3 conditions:
         *
         * 1.A new voice item is clicked to play, to stop the previous playing voice item animation.
         * 2.The voice is play complete, to stop it's voice play animation.
         * 3.Press the voice record button will stop the voice play and must stop voice play animation.
         *
         */
        if (onCompletionListener != null) {
            onCompletionListener!!.onCompletion(player)
        }
    }

    fun release() {
        onCompletionListener = null
    }

    private fun setSpeaker() {
        val speakerOn = true
        if (speakerOn) {
            audioManager.mode = AudioManager.MODE_NORMAL
            audioManager.isSpeakerphoneOn = true
            player.setAudioStreamType(AudioManager.STREAM_RING)
        } else {
            audioManager.isSpeakerphoneOn = true // 关闭扬声器
            // 把声音设定成Earpiece（听筒）出来，设定为正在通话中
            audioManager.mode = AudioManager.MODE_NORMAL
            player.setAudioStreamType(AudioManager.STREAM_RING)
        }
    }

    companion object {
        private val TAG = BenChatRowVoicePlayer::class.java.simpleName
        private var instance: BenChatRowVoicePlayer? = null
        fun getInstance(context: Context): BenChatRowVoicePlayer? {
            if (instance == null) {
                synchronized(BenChatRowVoicePlayer::class.java) {
                    if (instance == null) {
                        instance = BenChatRowVoicePlayer(context)
                    }
                }
            }
            return instance
        }
    }
}