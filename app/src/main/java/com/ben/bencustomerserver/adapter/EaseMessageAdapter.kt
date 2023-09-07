package com.ben.bencustomerserver.adapter

import android.view.ViewGroup
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.delegate.EaseMessageAdapterDelegate
import com.ben.bencustomerserver.listener.MessageListItemClickListener
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.Direct

/**
 * 做为对话列表的adapter，继承自[EaseBaseDelegateAdapter]
 */
class EaseMessageAdapter : EaseBaseDelegateAdapter<BaseMessageModel>() {
    private var itemClickListener: MessageListItemClickListener? = null
    override fun getEmptyLayoutId(): Int {
        return R.layout.ease_layout_empty_list_invisible
    }

    override fun getViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder<BaseMessageModel> {
        val delegate = getAdapterDelegate(viewType)
        if (delegate is EaseMessageAdapterDelegate<*, *>) {
            delegate.setListItemClickListener(itemClickListener)
        }
        return super.getViewHolder(parent, viewType)
    }

    /**
     * 为每个delegate添加item listener和item style
     * @param delegate
     * @return
     */
    override fun addDelegate(delegate: EaseAdapterDelegate<*, *>): EaseBaseDelegateAdapter<*> {
        var clone: EaseAdapterDelegate<*, *>? = null
        try {
            clone = delegate.clone() as EaseAdapterDelegate<*, *>
            clone!!.setTag(Direct.RECEIEVE.name)
            //设置点击事件
            if (clone is EaseMessageAdapterDelegate<*, *>) {
                clone.setListItemClickListener(itemClickListener)
            }
            super.addDelegate(clone)
        } catch (e: CloneNotSupportedException) {
            e.printStackTrace()
        }
        delegate.setTag(Direct.SEND.name)
        //设置点击事件
        if (delegate is EaseMessageAdapterDelegate<*, *>) {
            delegate.setListItemClickListener(itemClickListener)
        }
        return super.addDelegate(delegate)
    }

    override fun setFallbackDelegate(delegate: EaseAdapterDelegate<Any, ViewHolder<*>>?): EaseBaseDelegateAdapter<*> {
        var clone: EaseAdapterDelegate<Any, ViewHolder<*>>? = null
        try {
            clone = delegate?.clone() as EaseAdapterDelegate<Any, ViewHolder<*>>
            clone.setTag(Direct.RECEIEVE.name)
            //设置点击事件
            if (clone is EaseMessageAdapterDelegate<*, *>) {
                clone.setListItemClickListener(itemClickListener)
            }
            super.setFallbackDelegate(clone)
        } catch (e: CloneNotSupportedException) {
            e.printStackTrace()
        }
        delegate?.setTag(Direct.SEND.name)
        //设置点击事件
        if (delegate is EaseMessageAdapterDelegate<*, *>) {
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
        return if (mData != null && !mData!!.isEmpty()) {
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