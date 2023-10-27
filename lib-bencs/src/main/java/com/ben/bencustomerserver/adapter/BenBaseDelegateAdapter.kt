package com.ben.bencustomerserver.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BenBaseDelegateAdapter<T : Any> : BenBaseRecyclerViewAdapter<T> {
    private var delegatesManager: _root_ide_package_.com.ben.bencustomerserver.adapter.BenAdapterDelegatesManager

    constructor() {
        delegatesManager =
            _root_ide_package_.com.ben.bencustomerserver.adapter.BenAdapterDelegatesManager(false)
    }

    constructor(delegatesManager: _root_ide_package_.com.ben.bencustomerserver.adapter.BenAdapterDelegatesManager) {
        this.delegatesManager = delegatesManager
    }

    open fun addDelegate(delegate: com.ben.bencustomerserver.adapter.BenAdapterDelegate<*, *>): BenBaseDelegateAdapter<*> {
        delegatesManager.addDelegate(delegate, delegate.tag)
        notifyDataSetChanged()
        return this
    }

    fun addDelegate(delegate: com.ben.bencustomerserver.adapter.BenAdapterDelegate<*, *>, tag: String?): BenBaseDelegateAdapter<*> {
        delegate.tag =(tag!!)
        delegate.tags.add(tag)
        return addDelegate(delegate)
    }

    fun getDelegateViewType(delegate: com.ben.bencustomerserver.adapter.BenAdapterDelegate<Any, BenBaseRecyclerViewAdapter.ViewHolder<*>>): Int {
        return delegatesManager.getDelegateViewType(delegate)
    }

    fun setFallbackDelegate(
        delegate: com.ben.bencustomerserver.adapter.BenAdapterDelegate<Any, BenBaseRecyclerViewAdapter.ViewHolder<*>>,
        tag: String?
    ): BenBaseDelegateAdapter<*> {
        delegate.tag =(tag!!)
        delegate.tags.add(tag)

        return setFallbackDelegate(delegate)
    }

    open fun setFallbackDelegate(delegate: com.ben.bencustomerserver.adapter.BenAdapterDelegate<Any, BenBaseRecyclerViewAdapter.ViewHolder<*>>?): BenBaseDelegateAdapter<*> {
        delegatesManager.fallbackDelegate = delegate
        return this
    }

    fun getAdapterDelegate(viewType: Int): com.ben.bencustomerserver.adapter.BenAdapterDelegate<*, *>? {
        return delegatesManager.getDelegate(viewType)
    }


    val allDelegate: List<com.ben.bencustomerserver.adapter.BenAdapterDelegate<Any, BenBaseRecyclerViewAdapter.ViewHolder<*>>>
        get() = delegatesManager.allDelegates

    override fun getItemViewType(position: Int): Int {
        var viewType = 0
        viewType = try {
            delegatesManager.getItemViewType(getItem(position), position)
        } catch (e: Exception) {
            return super.getItemViewType(position)
        }
        return viewType
    }

    override fun getViewHolder(parent: ViewGroup?, viewType: Int):BenBaseRecyclerViewAdapter.ViewHolder<T> {
        return delegatesManager.onCreateViewHolder(parent, viewType) as ViewHolder<T>
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        super.onBindViewHolder(holder, position)
        if (isEmptyViewType(position)) {
            return
        }
        if (mData == null || mData!!.isEmpty()) {
            return
        }
        if (delegatesManager.allDelegates.isNotEmpty()) {
            delegatesManager.onBindViewHolder(holder, position, getItem(position))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int, payloads: List<Any>) {
        super.onBindViewHolder(holder, position, payloads)
        if (isEmptyViewType(position)) {
            return
        }
        if (mData == null || mData!!.isEmpty()) {
            return
        }
        if (delegatesManager.allDelegates.isNotEmpty()) {
            delegatesManager.onBindViewHolder(holder, position, payloads, getItem(position))
        }
    }

    override fun onViewRecycled(holder: ViewHolder<T>) {
        super.onViewRecycled(holder)
    }

    override fun onFailedToRecycleView(holder: ViewHolder<T>): Boolean {
        return super.onFailedToRecycleView(holder)
    }



    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        delegatesManager.onAttachedToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        delegatesManager.onDetachedFromRecyclerView(recyclerView)
    }

    abstract fun getEmptyLayoutId(): Int

    companion object {
        private const val TAG = "adapter"
    }
}