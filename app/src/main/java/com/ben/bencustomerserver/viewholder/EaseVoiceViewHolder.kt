package com.ben.bencustomerserver.viewholder

import android.os.AsyncTask
import android.view.View
import android.view.ViewGroup
import com.ben.bencustomerserver.listener.MessageListItemClickListener
import com.ben.bencustomerserver.views.chatrow.EaseChatRowVoicePlayer
import java.io.File

class EaseVoiceViewHolder(itemView: View, itemClickListener: MessageListItemClickListener?) :
    EaseChatRowViewHolder(itemView, itemClickListener) {
    private val voicePlayer: EaseChatRowVoicePlayer

    init {
        voicePlayer = EaseChatRowVoicePlayer.getInstance(context)
    }

    override fun onBubbleClick(message: BaseMessageModel) {
        super.onBubbleClick(message)
        val msgId: String = message.getMsgId()
        if (voicePlayer.isPlaying()) {
            // Stop the voice play first, no matter the playing voice item is this or others.
            voicePlayer.stop()
            // Stop the voice play animation.
            (chatRow as EaseChatRowVoice).stopVoicePlayAnimation()

            // If the playing voice item is this item, only need stop play.
            val playingId: String = voicePlayer.getCurrentPlayingId()
            if (msgId == playingId) {
                return
            }
        }
        if (message.direct() === BaseMessageModel.Direct.SEND) {
            // Play the voice
            val localPath: String = (message.getBody() as EMVoiceMessageBody).getLocalUrl()
            val file = File(localPath)
            if (file.exists() && file.isFile) {
                playVoice(message)
                // Start the voice play animation.
                (chatRow as EaseChatRowVoice).startVoicePlayAnimation()
            } else {
                asyncDownloadVoice(message)
            }
        } else {
            val st: String =
                context.getResources().getString(R.string.Is_download_voice_click_later)
            if (message.status() === BaseMessageModel.Status.SUCCESS) {
                if (EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {
                    play(message)
                } else {
                    val voiceBody: EMVoiceMessageBody = message.getBody() as EMVoiceMessageBody
                    Log.i(TAG, "Voice body download status: " + voiceBody.downloadStatus())
                    when (voiceBody.downloadStatus()) {
                        PENDING, FAILED -> {
                            chatRow.updateView(message)
                            asyncDownloadVoice(message)
                        }

                        DOWNLOADING -> Toast.makeText(context, st, Toast.LENGTH_SHORT).show()
                        SUCCESSED -> play(message)
                    }
                }
            } else if (message.status() === BaseMessageModel.Status.INPROGRESS) {
                Toast.makeText(context, st, Toast.LENGTH_SHORT).show()
            } else if (message.status() === BaseMessageModel.Status.FAIL) {
                Toast.makeText(context, st, Toast.LENGTH_SHORT).show()
                asyncDownloadVoice(message)
            }
        }
    }

    private fun playVoice(msg: BaseMessageModel) {
        voicePlayer.play(msg, object : OnCompletionListener {
            override fun onCompletion(mp: MediaPlayer) {
                // Stop the voice play animation.
                (chatRow as EaseChatRowVoice).stopVoicePlayAnimation()
            }
        })
    }

    private fun asyncDownloadVoice(message: BaseMessageModel) {
        object : AsyncTask<Void?, Void?, Void?>() {
            protected override fun doInBackground(vararg params: Void): Void? {
                EMClient.getInstance().chatManager().downloadAttachment(message)
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                chatRow.updateView(message)
            }
        }.execute()
    }

    private fun play(message: BaseMessageModel) {
        val localPath: String = (message.getBody() as EMVoiceMessageBody).getLocalUrl()
        val file = File(localPath)
        if (file.exists() && file.isFile) {
            ackMessage(message)
            playVoice(message)
            // Start the voice play animation.
            (chatRow as EaseChatRowVoice).startVoicePlayAnimation()
        } else {
            Log.e(TAG, "file not exist")
        }
    }

    private fun ackMessage(message: BaseMessageModel) {
        val chatType: BaseMessageModel.ChatType = message.getChatType()
        if (!message.isAcked() && chatType === BaseMessageModel.ChatType.Chat) {
            try {
                EMClient.getInstance().chatManager()
                    .ackMessageRead(message.getFrom(), message.getMsgId())
            } catch (e: HyphenateException) {
                e.printStackTrace()
            }
        }
        if (!message.isListened()) {
            EMClient.getInstance().chatManager().setVoiceMessageListened(message)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (voicePlayer.isPlaying()) {
            voicePlayer.stop()
            voicePlayer.release()
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            isSender: Boolean, itemClickListener: MessageListItemClickListener?
        ): EaseChatRowViewHolder {
            return EaseVoiceViewHolder(
                EaseChatRowVoice(parent.context, isSender),
                itemClickListener
            )
        }
    }
}