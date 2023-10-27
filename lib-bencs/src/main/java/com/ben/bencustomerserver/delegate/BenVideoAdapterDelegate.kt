package com.ben.bencustomerserver.delegate

import android.view.View
import android.view.ViewGroup
import com.ben.bencustomerserver.listener.MessageListItemClickListener
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.MessageType
import com.ben.bencustomerserver.viewholder.BenChatRowViewHolder
import com.ben.bencustomerserver.viewholder.BenVideoViewHolder
import com.ben.bencustomerserver.views.chatrow.BenChatRow
import com.ben.bencustomerserver.views.chatrow.BenChatRowVideo

/**
 * 视频代理类
 */
class BenVideoAdapterDelegate :
    BenMessageAdapterDelegate<BaseMessageModel?, BenChatRowViewHolder?> {
    constructor()
    constructor(
        itemClickListener: MessageListItemClickListener?,
    ) : super(itemClickListener)

    override fun isForViewType(item: BaseMessageModel?, position: Int): Boolean {
        return item?.messageType === MessageType.VIDEO
    }

    override fun getBenChatRow(parent: ViewGroup?, isSender: Boolean): BenChatRow {
        return BenChatRowVideo(parent!!.context, isSender)
    }

    override fun createViewHolder(
        view: View?,
        itemClickListener: MessageListItemClickListener?
    ): BenChatRowViewHolder {
        return BenVideoViewHolder(view!!, itemClickListener)
    }
}