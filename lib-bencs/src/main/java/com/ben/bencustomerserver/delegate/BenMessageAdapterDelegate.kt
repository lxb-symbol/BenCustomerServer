package com.ben.bencustomerserver.delegate

import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.ben.bencustomerserver.adapter.BenAdapterDelegate
import com.ben.bencustomerserver.listener.MessageListItemClickListener
import com.ben.bencustomerserver.model.Direct
import com.ben.bencustomerserver.viewholder.BenChatRowViewHolder
import com.ben.bencustomerserver.views.chatrow.BenChatRow

/**
 * 本类设计的目的是做为对话消息代理类的基类，添加了对话代理类特有的方法
 * @param <T>
 * @param <VH>
</VH></T> */
abstract class BenMessageAdapterDelegate<T, VH : BenChatRowViewHolder?>() :
    BenAdapterDelegate<T, VH>() {
    private var mItemClickListener: MessageListItemClickListener? = null

    constructor(itemClickListener: MessageListItemClickListener?) : this() {
        mItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup?, tag: String?): VH {
        val view = getBenChatRow(parent, isSender(tag))
        return createViewHolder(view, mItemClickListener)
    }

    private fun isSender(tag: String?): Boolean {


        return !TextUtils.isEmpty(tag) && TextUtils.equals(tag, Direct.SEND.toString())
    }

    protected abstract fun getBenChatRow(parent: ViewGroup?, isSender: Boolean): BenChatRow
    protected abstract fun createViewHolder(
        view: View?,
        itemClickListener: MessageListItemClickListener?
    ): VH

    fun setListItemClickListener(itemClickListener: MessageListItemClickListener?) {
        mItemClickListener = itemClickListener
    }
}