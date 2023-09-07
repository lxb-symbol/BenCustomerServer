package com.ben.bencustomerserver.viewholder

import android.net.Uri
import android.view.View
import android.view.ViewGroup
import com.hyphenate.chat.EMClient

class EaseFileViewHolder(itemView: View, itemClickListener: MessageListItemClickListener?) :
    EaseChatRowViewHolder(itemView, itemClickListener) {
    override fun onBubbleClick(message: BaseMessageModel) {
        super.onBubbleClick(message)
        val fileMessageBody: EMNormalFileMessageBody = message.getBody() as EMNormalFileMessageBody
        val filePath: Uri = fileMessageBody.getLocalUri()
        //检查Uri读权限
        EaseFileUtils.takePersistableUriPermission(context, filePath)
        if (EaseFileUtils.isFileExistByUri(context, filePath)) {
            EaseCompat.openFile(context, filePath)
        } else {
            // download the file
            context.startActivity(
                Intent(
                    context,
                    EaseShowNormalFileActivity::class.java
                ).putExtra("msg", message)
            )
        }
        if (message.direct() === BaseMessageModel.Direct.RECEIVE && !message.isAcked() && message.getChatType() === BaseMessageModel.ChatType.Chat) {
            try {
                EMClient.getInstance().chatManager()
                    .ackMessageRead(message.getFrom(), message.getMsgId())
            } catch (e: HyphenateException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            isSender: Boolean, itemClickListener: MessageListItemClickListener?
        ): EaseChatRowViewHolder {
            return EaseFileViewHolder(EaseChatRowFile(parent.context, isSender), itemClickListener)
        }
    }
}