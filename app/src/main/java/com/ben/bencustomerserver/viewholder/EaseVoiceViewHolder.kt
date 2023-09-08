package com.ben.bencustomerserver.viewholder

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.view.View
import android.view.ViewGroup
import com.ben.bencustomerserver.listener.MessageListItemClickListener
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.Direct
import com.ben.bencustomerserver.views.chatrow.EaseChatRowVoice
import com.ben.bencustomerserver.views.chatrow.EaseChatRowVoicePlayer

class EaseVoiceViewHolder(itemView: View, itemClickListener: MessageListItemClickListener?) :
    EaseChatRowViewHolder(itemView, itemClickListener!!) {
    private var voicePlayer: EaseChatRowVoicePlayer? = null

    init {
        voicePlayer = EaseChatRowVoicePlayer.getInstance(context)
    }

    override fun onBubbleClick(message: BaseMessageModel?) {
        super.onBubbleClick(message)
        val msgId: String = message?.msgId ?: ""
        // 1. 下载完毕就播放，没下载完毕继续下载

        if (voicePlayer?.isPlaying == true) {
            // Stop the voice play first, no matter the playing voice item is this or others.
            voicePlayer?.stop()
            // Stop the voice play animation.
            (chatRow as EaseChatRowVoice).stopVoicePlayAnimation()

            // If the playing voice item is this item, only need stop play.
            val playingId: String? = voicePlayer?.currentPlayingId
            if (msgId == playingId) {
                return
            }
        }
        if (message?.direct === Direct.SEND) {
            // Play the voice
//            val localPath: String = (message.getBody() as EMVoiceMessageBody).getLocalUrl()
//            val file = File(localPath)
//            if (file.exists() && file.isFile) {
//                playVoice(message)
//                // Start the voice play animation.
//                (chatRow as EaseChatRowVoice).startVoicePlayAnimation()
//            } else {
//                asyncDownloadVoice(message)
//            }
        } else {
//            val st: String =
//                context.getResources().getString(R.string.Is_download_voice_click_later)
//            if (message.status() === BaseMessageModel.Status.SUCCESS) {
//                if (EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {
//                    play(message)
//                } else {
//                    val voiceBody: EMVoiceMessageBody = message.getBody() as EMVoiceMessageBody
//                    Log.i(TAG, "Voice body download status: " + voiceBody.downloadStatus())
//                    when (voiceBody.downloadStatus()) {
//                        PENDING, FAILED -> {
//                            chatRow.updateView(message)
//                            asyncDownloadVoice(message)
//                        }
//
//                        DOWNLOADING -> Toast.makeText(context, st, Toast.LENGTH_SHORT).show()
//                        SUCCESSED -> play(message)
//                    }
//                }
//            } else if (message.status() === BaseMessageModel.Status.INPROGRESS) {
//                Toast.makeText(context, st, Toast.LENGTH_SHORT).show()
//            } else if (message.status() === BaseMessageModel.Status.FAIL) {
//                Toast.makeText(context, st, Toast.LENGTH_SHORT).show()
//                asyncDownloadVoice(message)
//            }
        }
    }

    private fun playVoice(msg: BaseMessageModel) {
        voicePlayer?.play(msg) { // Stop the voice play animation.
            (chatRow as EaseChatRowVoice).stopVoicePlayAnimation()
        }
    }

    @SuppressLint("StaticFieldLeak")
    private fun asyncDownloadVoice(message: BaseMessageModel) {
        object : AsyncTask<Void?, Void?, Void?>() {


            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                chatRow?.updateView(message)
            }

            override fun doInBackground(vararg params: Void?): Void? {
                TODO("Not yet implemented")
            }
        }.execute()
    }

    private fun play(message: BaseMessageModel) {
//        val localPath: String = (message.getBody() as EMVoiceMessageBody).getLocalUrl()
//        val file = File(localPath)
//        if (file.exists() && file.isFile) {
//            ackMessage(message)
//            playVoice(message)
//            // Start the voice play animation.
//            (chatRow as EaseChatRowVoice).startVoicePlayAnimation()
//        } else {
//            Log.e(TAG, "file not exist")
//        }
    }

    private fun ackMessage(message: BaseMessageModel) {
//        1. 更新已读状态 2. 更新已听音乐状态

//        if (!message.isListened()) {
//            EMClient.getInstance().chatManager().setVoiceMessageListened(message)
//        }
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
        ): EaseChatRowViewHolder {
            return EaseVoiceViewHolder(
                EaseChatRowVoice(parent.context, isSender),
                itemClickListener
            )
        }
    }
}