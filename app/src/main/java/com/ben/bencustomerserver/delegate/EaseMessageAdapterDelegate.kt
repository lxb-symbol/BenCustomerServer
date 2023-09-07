package com.ben.bencustomerserver.delegate

import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import com.ben.bencustomerserver.adapter.EaseAdapterDelegate
import com.ben.bencustomerserver.listener.MessageListItemClickListener
import com.ben.bencustomerserver.model.Direct
import com.ben.bencustomerserver.viewholder.EaseChatRowViewHolder
import com.ben.bencustomerserver.views.chatrow.EaseChatRow

/**
 * 本类设计的目的是做为对话消息代理类的基类，添加了对话代理类特有的方法
 * @param <T>
 * @param <VH>
</VH></T> */
abstract class EaseMessageAdapterDelegate<T, VH : EaseChatRowViewHolder?>() :
    EaseAdapterDelegate<T, VH>() {
    private var mItemClickListener: MessageListItemClickListener? = null

    constructor(itemClickListener: MessageListItemClickListener?) : this() {
        mItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup?, tag: String?): VH {
        val view = getEaseChatRow(parent, isSender(tag))
        return createViewHolder(view, mItemClickListener)
    }

    private fun isSender(tag: String?): Boolean {
        return !TextUtils.isEmpty(tag) && TextUtils.equals(tag, Direct.SEND.toString())
    }

    protected abstract fun getEaseChatRow(parent: ViewGroup?, isSender: Boolean): EaseChatRow
    protected abstract fun createViewHolder(
        view: View?,
        itemClickListener: MessageListItemClickListener?
    ): VH

    fun setListItemClickListener(itemClickListener: MessageListItemClickListener?) {
        mItemClickListener = itemClickListener
    }
}