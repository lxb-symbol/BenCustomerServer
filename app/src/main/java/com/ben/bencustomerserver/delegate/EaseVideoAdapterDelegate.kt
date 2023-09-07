package com.ben.bencustomerserver.delegate

import android.view.View
import android.view.ViewGroup
import com.hyphenate.chat.BaseMessageModel

/**
 * 视频代理类
 */
class EaseVideoAdapterDelegate :
    EaseMessageAdapterDelegate<BaseMessageModel?, EaseChatRowViewHolder?> {
    constructor()
    constructor(
        itemClickListener: MessageListItemClickListener?,
        itemStyle: EaseMessageListItemStyle?
    ) : super(itemClickListener, itemStyle)

    override fun isForViewType(item: BaseMessageModel, position: Int): Boolean {
        return item.getType() === MessageType.VIDEO
    }

    override fun getEaseChatRow(parent: ViewGroup?, isSender: Boolean): EaseChatRow {
        return EaseChatRowVideo(parent!!.context, isSender)
    }

    override fun createViewHolder(
        view: View?,
        itemClickListener: MessageListItemClickListener?
    ): EaseChatRowViewHolder {
        return EaseVideoViewHolder(view, itemClickListener)
    }
}