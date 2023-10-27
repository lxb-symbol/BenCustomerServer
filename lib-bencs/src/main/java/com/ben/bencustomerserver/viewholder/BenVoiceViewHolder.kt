package com.ben.bencustomerserver.viewholder

import android.annotation.SuppressLint
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.ben.bencustomerserver.listener.MessageListItemClickListener
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.Direct
import com.ben.bencustomerserver.model.MessageStatus
import com.ben.bencustomerserver.model.VoiceMessage
import com.ben.bencustomerserver.utils.HttpUtils
import com.ben.bencustomerserver.views.chatrow.BenChatRowVoice
import com.ben.bencustomerserver.views.chatrow.BenChatRowVoicePlayer
import com.luck.picture.lib.utils.ToastUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File

class BenVoiceViewHolder(itemView: View, itemClickListener: MessageListItemClickListener?) :
    BenChatRowViewHolder(itemView, itemClickListener!!) {
    private var voicePlayer: BenChatRowVoicePlayer? = null

    init {
        voicePlayer = BenChatRowVoicePlayer.getInstance(itemView.context)
    }

    override fun onBubbleClick(message: BaseMessageModel?) {
        super.onBubbleClick(message)
        val msgId: String = message?.msgId ?: ""
        // 1. 下载完毕就播放，没下载完毕继续下载

        if (voicePlayer?.isPlaying == true) {
            // Stop the voice play first, no matter the playing voice item is this or others.
            voicePlayer?.stop()
            // Stop the voice play animation.
            (chatRow as BenChatRowVoice).stopVoicePlayAnimation()

            // If the playing voice item is this item, only need stop play.
            val playingId: String? = voicePlayer?.currentPlayingId
            if (msgId == playingId) {
                return
            }
        }
        if (message?.direct === Direct.SEND) {
            // Play the voice
            val localPath: String = (message.innerMessage as VoiceMessage).localPath ?: ""
            val file = File(localPath)
            if (file.exists() && file.isFile) {
                playVoice(message)
                // Start the voice play animation.
                (chatRow as BenChatRowVoice).startVoicePlayAnimation()
            } else {
                asyncDownloadVoice(message)
            }
        } else {
            if (message?.status === MessageStatus.SUCCESS) {
                val voiceBody = message.innerMessage as VoiceMessage
                if (TextUtils.isEmpty(voiceBody.localPath)) {
                    chatRow?.updateView(message)
                    asyncDownloadVoice(message)
                } else {
                    play(message)
                }
            } else if (message?.status === MessageStatus.INPROGRESS) {
                ToastUtils.showToast(chatRow?.context, "下载中")
            } else if (message?.status === MessageStatus.FAIL) {
                asyncDownloadVoice(message)
            }
        }
    }

    private fun playVoice(msg: BaseMessageModel) {
        voicePlayer?.play(msg) { // Stop the voice play animation.
            (chatRow as BenChatRowVoice).stopVoicePlayAnimation()
        }
    }

    @SuppressLint("StaticFieldLeak")
    private fun asyncDownloadVoice(message: BaseMessageModel) {
        val inMsg = message.innerMessage as VoiceMessage
        val netPath = inMsg.netPath

        val filePath = itemView.context.externalCacheDir?.path
        val name = "${netPath.hashCode()}.mp4"
        MainScope().launch {
            val b = HttpUtils.downFile(itemView.context, netPath ?: "", filePath ?: "", name)
            Log.i("symbol-5", "voice-->$b")
            if (b) {
                (message.innerMessage as VoiceMessage).localPath = "$filePath/$name"
                message.status = MessageStatus.SUCCESS
                chatRow?.updateView(message)
            }
        }

    }

    private fun play(message: BaseMessageModel) {
        val localPath: String = (message.innerMessage as VoiceMessage).localPath ?: ""
        val file = File(localPath)
        if (file.exists() && file.isFile) {
            ackMessage(message)
            playVoice(message)
            // Start the voice play animation.
            (chatRow as BenChatRowVoice).startVoicePlayAnimation()
        } else {
            Log.e(TAG, "file not exist")
        }
    }

    private fun ackMessage(message: BaseMessageModel) {
//        1. 更新已读状态 2. 更新已听音乐状态

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        voicePlayer?.let {
            if (it.isPlaying) {
                it.stop()
                it.release()
            }
        }

    }

    companion object {
        fun create(
            parent: ViewGroup,
            isSender: Boolean, itemClickListener: MessageListItemClickListener?
        ): BenChatRowViewHolder {
            return BenVoiceViewHolder(
                BenChatRowVoice(parent.context, isSender),
                itemClickListener
            )
        }
    }
}