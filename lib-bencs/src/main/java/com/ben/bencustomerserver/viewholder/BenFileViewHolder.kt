package com.ben.bencustomerserver.viewholder

import android.net.Uri
import android.view.View
import android.view.ViewGroup
import com.ben.bencustomerserver.listener.MessageListItemClickListener
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.utils.BenCompat
import com.ben.bencustomerserver.utils.BenFileUtils
import com.ben.bencustomerserver.views.chatrow.BenChatRowFile

class BenFileViewHolder(itemView: View, itemClickListener: MessageListItemClickListener?) :
    BenChatRowViewHolder(itemView, itemClickListener!!) {
    override fun onBubbleClick(message: BaseMessageModel?) {
        super.onBubbleClick(message)

        //检查Uri读权限


    }

    companion object {
        fun create(
            parent: ViewGroup,
            isSender: Boolean, itemClickListener: MessageListItemClickListener?
        ): BenChatRowViewHolder {
            return BenFileViewHolder(BenChatRowFile(parent.context, isSender), itemClickListener)
        }
    }
}