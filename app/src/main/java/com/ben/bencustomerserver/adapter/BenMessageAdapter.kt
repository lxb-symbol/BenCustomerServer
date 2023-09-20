package com.ben.bencustomerserver.adapter

import android.util.Log
import android.view.ViewGroup
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.delegate.BenMessageAdapterDelegate
import com.ben.bencustomerserver.listener.MessageListItemClickListener
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.Direct

/**
 * 做为对话列表的adapter，继承自[BenBaseDelegateAdapter]
 */
class BenMessageAdapter : BenBaseDelegateAdapter<BaseMessageModel>() {
    private var itemClickListener: MessageListItemClickListener? = null
    override fun getEmptyLayoutId(): Int {
        return R.layout.ben_layout_empty_list_invisible
    }

    override fun getViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder<BaseMessageModel> {
        val delegate = getAdapterDelegate(viewType)
        if (delegate is BenMessageAdapterDelegate<*, *>) {
            delegate.setListItemClickListener(itemClickListener)
        }
        return super.getViewHolder(parent, viewType)
    }

    /**
     * 为每个delegate添加item listener和item style
     * @param delegate
     * @return
     */
    override fun addDelegate(delegate: BenAdapterDelegate<*, *>): BenBaseDelegateAdapter<*> {
        var clone: BenAdapterDelegate<*, *>? = null
        try {
            clone = delegate.clone() as BenAdapterDelegate<*, *>
            clone.tag = (Direct.RECEIEVE.name)
            clone.tags.add(Direct.RECEIEVE.name)
            //设置点击事件
            if (clone is BenMessageAdapterDelegate<*, *>) {
                clone.setListItemClickListener(itemClickListener)
            }
            super.addDelegate(clone)
        } catch (e: CloneNotSupportedException) {
            e.printStackTrace()
        }
        delegate.tag = (Direct.SEND.name)
        delegate.tags.add(Direct.SEND.name)
        //设置点击事件
        if (delegate is BenMessageAdapterDelegate<*, *>) {
            delegate.setListItemClickListener(itemClickListener)
        }
        return super.addDelegate(delegate)
    }

    override fun setFallbackDelegate(delegate: BenAdapterDelegate<Any, ViewHolder<*>>?): BenBaseDelegateAdapter<*> {
        var clone: BenAdapterDelegate<Any, ViewHolder<*>>? = null
        try {
            clone = delegate?.clone() as BenAdapterDelegate<Any, ViewHolder<*>>
            clone.tag = (Direct.RECEIEVE.name)
            clone.tags.add(Direct.RECEIEVE.name)

            //设置点击事件
            if (clone is BenMessageAdapterDelegate<*, *>) {
                clone.setListItemClickListener(itemClickListener)
            }
            super.setFallbackDelegate(clone)
        } catch (e: CloneNotSupportedException) {
            e.printStackTrace()
        }
        delegate?.tag = (Direct.SEND.name)
        delegate?.tags?.add(Direct.SEND.name)
        //设置点击事件
        if (delegate is BenMessageAdapterDelegate<*, *>) {
            delegate.setListItemClickListener(itemClickListener)
        }
        return super.setFallbackDelegate(delegate)
    }

    /**
     * get item message
     * @param position
     * @return
     */
    private fun getItemMessage(position: Int): BaseMessageModel? {
        return if (mData != null && mData!!.isNotEmpty()) {
            mData!![position]
        } else null
    }

    /**
     * set item click listener
     * @param itemClickListener
     */
    fun setListItemClickListener(itemClickListener: MessageListItemClickListener?) {
        this.itemClickListener = itemClickListener
    }
}