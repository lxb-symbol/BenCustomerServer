package com.ben.bencustomerserver.viewholder

import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import com.ben.bencustomerserver.listener.MessageListItemClickListener
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.IMessageModel
import com.ben.bencustomerserver.model.ImageMessage
import com.ben.bencustomerserver.ui.BenShowBigImgActivity
import com.ben.bencustomerserver.views.chatrow.BenChatRowImage

class BenImageViewHolder(itemView: View, itemClickListener: MessageListItemClickListener?) :
    BenChatRowViewHolder(itemView, itemClickListener!!) {
    override fun onBubbleClick(message: BaseMessageModel?) {
        super.onBubbleClick(message)
        // 1. 下载封面更新界面         chatRow.updateView()
        // 2. 跳转预览界面

        val imgMessage: ImageMessage = message?.innerMessage as ImageMessage
        val intent = Intent(itemView.context, BenShowBigImgActivity::class.java)
        intent.putExtra("messageId", ""+message.msgId)
        intent.putExtra("path", imgMessage.netPath?:imgMessage.localPath)
        itemView.context.startActivity(intent)
    }

    override fun handleReceiveMessage(message: BaseMessageModel?) {
        super.handleReceiveMessage(message)
        chatRow?.updateView(message!!)
    }

    companion object {
        fun create(
            parent: ViewGroup,
            isSender: Boolean, itemClickListener: MessageListItemClickListener?
        ): BenChatRowViewHolder {
            return BenImageViewHolder(
                BenChatRowImage(parent.context, isSender),
                itemClickListener
            )
        }
    }
}