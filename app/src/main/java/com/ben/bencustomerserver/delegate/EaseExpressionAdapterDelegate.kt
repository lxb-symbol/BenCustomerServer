package com.ben.bencustomerserver.delegate

import android.view.View
import android.view.ViewGroup
import com.hyphenate.chat.BaseMessageModel

/**
 * 表情代理类
 */
class EaseExpressionAdapterDelegate :
    EaseMessageAdapterDelegate<BaseMessageModel?, EaseChatRowViewHolder?> {
    constructor() : super()
    constructor(
        itemClickListener: MessageListItemClickListener?,
        itemStyle: EaseMessageListItemStyle?
    ) : super(itemClickListener, itemStyle)

    override fun isForViewType(item: BaseMessageModel, position: Int): Boolean {
        return item.getType() === MessageType.TXT && item.getBooleanAttribute(
            EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION,
            false
        )
    }

    override fun getEaseChatRow(parent: ViewGroup?, isSender: Boolean): EaseChatRow {
        return EaseChatRowBigExpression(parent!!.context, isSender)
    }

    override fun createViewHolder(
        view: View?,
        itemClickListener: MessageListItemClickListener?
    ): EaseChatRowViewHolder {
        return EaseExpressionViewHolder(view, itemClickListener)
    }
}