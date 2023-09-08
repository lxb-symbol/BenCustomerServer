package com.ben.bencustomerserver.viewholder

import android.view.View
import com.ben.bencustomerserver.listener.MessageListItemClickListener
import com.ben.bencustomerserver.model.BaseMessageModel

class EaseCustomViewHolder(itemView: View, itemClickListener: MessageListItemClickListener?) :
    EaseChatRowViewHolder(itemView, itemClickListener!!) {
    override fun handleReceiveMessage(message: BaseMessageModel?) {

    }
}