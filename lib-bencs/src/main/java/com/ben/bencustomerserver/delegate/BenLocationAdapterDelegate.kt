package com.ben.bencustomerserver.delegate

import android.view.View
import android.view.ViewGroup
import com.ben.bencustomerserver.listener.MessageListItemClickListener
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.MessageType
import com.ben.bencustomerserver.viewholder.BenChatRowViewHolder
import com.ben.bencustomerserver.viewholder.BenLocationViewHolder
import com.ben.bencustomerserver.views.chatrow.BenChatRow
import com.ben.bencustomerserver.views.chatrow.BenChatRowLocation

/**
 * 定位代理类
 */
class BenLocationAdapterDelegate :
    BenMessageAdapterDelegate<BaseMessageModel?, BenChatRowViewHolder?> {
    constructor()
    constructor(
        itemClickListener: MessageListItemClickListener?,
    ) : super(itemClickListener)

    override fun isForViewType(item: BaseMessageModel?, position: Int): Boolean {
        return item?.messageType === MessageType.LOCATION
    }

    override fun getBenChatRow(parent: ViewGroup?, isSender: Boolean): BenChatRow {
        return BenChatRowLocation(parent!!.context, isSender)
    }

    override fun createViewHolder(
        view: View?,
        itemClickListener: MessageListItemClickListener?
    ): BenChatRowViewHolder {
        return BenLocationViewHolder(view!!, itemClickListener)
    }
}