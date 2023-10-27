package com.ben.bencustomerserver.delegate

import android.view.View
import android.view.ViewGroup
import com.ben.bencustomerserver.listener.MessageListItemClickListener
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.MessageType
import com.ben.bencustomerserver.viewholder.BenChatRowViewHolder
import com.ben.bencustomerserver.viewholder.BenCustomViewHolder
import com.ben.bencustomerserver.views.chatrow.BenChatRow
import com.ben.bencustomerserver.views.chatrow.BenChatRowCustom

class BenCustomAdapterDelegate :
    BenMessageAdapterDelegate<BaseMessageModel?, BenChatRowViewHolder?>() {
    override fun isForViewType(item: BaseMessageModel?, position: Int): Boolean {
        return item?.messageType === MessageType.CUSTOM
    }

    override fun getBenChatRow(parent: ViewGroup?, isSender: Boolean): BenChatRow {
        return BenChatRowCustom(parent!!.context, isSender)
    }

    override fun createViewHolder(
        view: View?,
        itemClickListener: MessageListItemClickListener?
    ): BenChatRowViewHolder {
        return BenCustomViewHolder(view!!, itemClickListener)
    }
}