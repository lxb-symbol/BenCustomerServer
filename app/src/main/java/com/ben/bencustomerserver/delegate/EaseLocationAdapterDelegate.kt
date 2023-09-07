package com.ben.bencustomerserver.delegate

import android.view.View
import android.view.ViewGroup
import com.hyphenate.chat.BaseMessageModel

/**
 * 定位代理类
 */
class EaseLocationAdapterDelegate :
    EaseMessageAdapterDelegate<BaseMessageModel?, EaseChatRowViewHolder?> {
    constructor()
    constructor(
        itemClickListener: MessageListItemClickListener?,
        itemStyle: EaseMessageListItemStyle?
    ) : super(itemClickListener, itemStyle)

    override fun isForViewType(item: BaseMessageModel, position: Int): Boolean {
        return item.getType() === MessageType.LOCATION
    }

    override fun getEaseChatRow(parent: ViewGroup?, isSender: Boolean): EaseChatRow {
        return EaseChatRowLocation(parent!!.context, isSender)
    }

    override fun createViewHolder(
        view: View?,
        itemClickListener: MessageListItemClickListener?
    ): EaseChatRowViewHolder {
        return EaseLocationViewHolder(view, itemClickListener)
    }
}