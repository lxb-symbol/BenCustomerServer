package com.ben.bencustomerserver.views.chatrow

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import com.ben.bencustomerserver.model.BaseMessageModel
import java.io.IOException

/**
 * Created by zhangsong on 17-10-20.
 */
class EaseChatRowVoicePlayer private constructor(cxt: Context) {
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
        TODO("2023-9-1 留下的注释")
//        if (msg.getBody() !is EMVoiceMessageBody) return

        if (player.isPlaying) {
            stop()
        }
//        currentPlayingId = msg.getMsgId()
        onCompletionListener = listener
        try {
            setSpeaker()
            //            EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) msg.getBody();
//            player.setDataSource(baseContext, voiceBody.getLocalUri())
            player.prepare()
            player.setOnCompletionListener(object : OnCompletionListener {
                override fun onCompletion(mp: MediaPlayer) {
                    stop()
                    currentPlayingId = null
                    onCompletionListener = null
                }
            })
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
            audioManager.isSpeakerphoneOn = false // 关闭扬声器
            // 把声音设定成Earpiece（听筒）出来，设定为正在通话中
            audioManager.mode = AudioManager.MODE_IN_CALL
            player.setAudioStreamType(AudioManager.STREAM_VOICE_CALL)
        }
    }

    companion object {
        private val TAG = EaseChatRowVoicePlayer::class.java.simpleName
        private var instance: EaseChatRowVoicePlayer? = null
        fun getInstance(context: Context): EaseChatRowVoicePlayer? {
            if (instance == null) {
                synchronized(EaseChatRowVoicePlayer::class.java) {
                    if (instance == null) {
                        instance = EaseChatRowVoicePlayer(context)
                    }
                }
            }
            return instance
        }
    }
}