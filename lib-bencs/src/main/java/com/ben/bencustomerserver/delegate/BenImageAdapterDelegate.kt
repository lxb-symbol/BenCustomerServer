package com.ben.bencustomerserver.delegate

import android.view.View
import android.view.ViewGroup
import com.ben.bencustomerserver.listener.MessageListItemClickListener
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.MessageType
import com.ben.bencustomerserver.viewholder.BenChatRowViewHolder
import com.ben.bencustomerserver.viewholder.BenImageViewHolder
import com.ben.bencustomerserver.views.chatrow.BenChatRow
import com.ben.bencustomerserver.views.chatrow.BenChatRowImage

/**
 * 图片代理类
 */
class BenImageAdapterDelegate :
    BenMessageAdapterDelegate<BaseMessageModel?, BenChatRowViewHolder?> {
    constructor()
    constructor(
        itemClickListener: MessageListItemClickListener?,
    ) : super(itemClickListener)

    override fun isForViewType(item: BaseMessageModel?, position: Int): Boolean {
        return item?.messageType === MessageType.IMAGE
    }

    override fun getBenChatRow(parent: ViewGroup?, isSender: Boolean): BenChatRow {
        return BenChatRowImage(parent!!.context, isSender)
    }

    override fun createViewHolder(
        view: View?,
        itemClickListener: MessageListItemClickListener?
    ): BenChatRowViewHolder {
        return BenImageViewHolder(view!!, itemClickListener)
    }
}