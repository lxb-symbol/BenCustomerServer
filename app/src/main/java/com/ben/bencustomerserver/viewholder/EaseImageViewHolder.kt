package com.ben.bencustomerserver.viewholder

import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import com.ben.bencustomerserver.listener.MessageListItemClickListener
import com.ben.bencustomerserver.viewholder.EaseChatRowViewHolder.chatRow
import com.ben.bencustomerserver.viewholder.EaseChatRowViewHolder.context
import com.ben.bencustomerserver.views.chatrow.EaseChatRow.updateView

class EaseImageViewHolder(itemView: View, itemClickListener: MessageListItemClickListener?) :
    EaseChatRowViewHolder(itemView, itemClickListener!!) {
    override fun onBubbleClick(message: BaseMessageModel?) {
        super.onBubbleClick(message)
        val imgBody: EMImageMessageBody = message.getBody() as EMImageMessageBody
        if (EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {
            if (imgBody.thumbnailDownloadStatus() === EMFileMessageBody.EMDownloadStatus.FAILED) {
                chatRow.updateView(message)
                // retry download with click event of user
                EMClient.getInstance().chatManager().downloadThumbnail(message)
            }
        } else {
            if (imgBody.thumbnailDownloadStatus() === EMFileMessageBody.EMDownloadStatus.DOWNLOADING || imgBody.thumbnailDownloadStatus() === EMFileMessageBody.EMDownloadStatus.PENDING || imgBody.thumbnailDownloadStatus() === EMFileMessageBody.EMDownloadStatus.FAILED) {
                // retry download with click event of user
                EMClient.getInstance().chatManager().downloadThumbnail(message)
                chatRow.updateView(message)
                return
            }
        }
        val intent = Intent(context, EaseShowBigImageActivity::class.java)
        val imgUri: Uri = imgBody.getLocalUri()
        //检查Uri读权限
        EaseFileUtils.takePersistableUriPermission(context, imgUri)
        Log.e(
            "Tag",
            "big image uri: " + imgUri + "  exist: " + EaseFileUtils.isFileExistByUri(
                context,
                imgUri
            )
        )
        if (EaseFileUtils.isFileExistByUri(context, imgUri)) {
            intent.putExtra("uri", imgUri)
        } else {
            // The local full size pic does not exist yet.
            // ShowBigImage needs to download it from the server
            // first
            val msgId: String = message.getMsgId()
            intent.putExtra("messageId", msgId)
            intent.putExtra("filename", imgBody.getFileName())
        }
        if (!EaseIM.getInstance().getConfigsManager().enableSendChannelAck()) {
            //此处不再单独发送read_ack消息，改为进入聊天页面发送channel_ack
            //新消息在聊天页面的onReceiveMessage方法中，排除视频，语音和文件消息外，发送read_ack消息
            if (message != null && message.direct() === BaseMessageModel.Direct.RECEIVE && !message.isAcked() && message.getChatType() === BaseMessageModel.ChatType.Chat) {
                try {
                    EMClient.getInstance().chatManager()
                        .ackMessageRead(message.getFrom(), message.getMsgId())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        context.startActivity(intent)
    }

    override fun handleReceiveMessage(message: BaseMessageModel?) {
        super.handleReceiveMessage(message)
        chatRow.updateView(message)
    }

    companion object {
        fun create(
            parent: ViewGroup,
            isSender: Boolean, itemClickListener: MessageListItemClickListener?
        ): EaseChatRowViewHolder {
            return EaseImageViewHolder(
                EaseChatRowImage(parent.context, isSender),
                itemClickListener
            )
        }
    }
}