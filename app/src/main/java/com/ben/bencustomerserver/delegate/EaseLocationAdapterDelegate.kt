package com.ben.bencustomerserver.delegate

import android.view.View
import android.view.ViewGroup
import com.ben.bencustomerserver.listener.MessageListItemClickListener
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.MessageType
import com.ben.bencustomerserver.viewholder.EaseChatRowViewHolder
import com.ben.bencustomerserver.viewholder.EaseLocationViewHolder
import com.ben.bencustomerserver.views.chatrow.EaseChatRow
import com.ben.bencustomerserver.views.chatrow.EaseChatRowLocation

/**
 * 定位代理类
 */
class EaseLocationAdapterDelegate :
    EaseMessageAdapterDelegate<BaseMessageModel?, EaseChatRowViewHolder?> {
    constructor()
    constructor(
        itemClickListener: MessageListItemClickListener?,
    ) : super(itemClickListener)

    override fun isForViewType(item: BaseMessageModel?, position: Int): Boolean {
        return item?.messageType === MessageType.LOCATION
    }

    override fun getEaseChatRow(parent: ViewGroup?, isSender: Boolean): EaseChatRow {
        return EaseChatRowLocation(parent!!.context, isSender)
    }

    override fun createViewHolder(
        view: View?,
        itemClickListener: MessageListItemClickListener?
    ): EaseChatRowViewHolder {
        return EaseLocationViewHolder(view!!, itemClickListener)
    }
}