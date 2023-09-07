package com.ben.bencustomerserver.delegate

import android.view.View
import android.view.ViewGroup
import com.hyphenate.chat.BaseMessageModel

/**
 * 图片代理类
 */
class EaseImageAdapterDelegate :
    EaseMessageAdapterDelegate<BaseMessageModel?, EaseChatRowViewHolder?> {
    constructor()
    constructor(
        itemClickListener: MessageListItemClickListener?,
        itemStyle: EaseMessageListItemStyle?
    ) : super(itemClickListener, itemStyle)

    override fun isForViewType(item: BaseMessageModel, position: Int): Boolean {
        return item.getType() === MessageType.IMAGE
    }

    override fun getEaseChatRow(parent: ViewGroup?, isSender: Boolean): EaseChatRow {
        return EaseChatRowImage(parent!!.context, isSender)
    }

    override fun createViewHolder(
        view: View?,
        itemClickListener: MessageListItemClickListener?
    ): EaseChatRowViewHolder {
        return EaseImageViewHolder(view, itemClickListener)
    }
}