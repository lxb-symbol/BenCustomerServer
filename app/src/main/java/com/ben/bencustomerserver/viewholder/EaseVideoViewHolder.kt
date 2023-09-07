package com.ben.bencustomerserver.viewholder

import android.view.View
import android.view.ViewGroup
import com.hyphenate.chat.EMClient

class EaseVideoViewHolder(itemView: View, itemClickListener: MessageListItemClickListener?) :
    EaseChatRowViewHolder(itemView, itemClickListener) {
    override fun onBubbleClick(message: BaseMessageModel?) {
        super.onBubbleClick(message)
        val videoBody: EMVideoMessageBody = message.getBody() as EMVideoMessageBody
        Log.d(TAG, "video view is on click")
        if (EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {
        } else {
            if (videoBody.thumbnailDownloadStatus() === EMFileMessageBody.EMDownloadStatus.DOWNLOADING || videoBody.thumbnailDownloadStatus() === EMFileMessageBody.EMDownloadStatus.PENDING || videoBody.thumbnailDownloadStatus() === EMFileMessageBody.EMDownloadStatus.FAILED) {
                // retry download with click event of user
                EMClient.getInstance().chatManager().downloadThumbnail(message)
                return
            }
        }
        val intent = Intent(context, EaseShowVideoActivity::class.java)
        intent.putExtra("msg", message)
        if (message != null && message.direct() === BaseMessageModel.Direct.RECEIVE && !message.isAcked() && message.getChatType() === BaseMessageModel.ChatType.Chat) {
            try {
                EMClient.getInstance().chatManager()
                    .ackMessageRead(message.getFrom(), message.getMsgId())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        context.startActivity(intent)
    }

    companion object {
        private val TAG = EaseVideoViewHolder::class.java.simpleName
        fun create(
            parent: ViewGroup,
            isSender: Boolean, itemClickListener: MessageListItemClickListener?
        ): EaseChatRowViewHolder {
            return EaseVideoViewHolder(
                EaseChatRowVideo(parent.context, isSender),
                itemClickListener
            )
        }
    }
}