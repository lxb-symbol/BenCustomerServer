package com.ben.bencustomerserver.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class EaseBaseDelegateAdapter<T : Any> : EaseBaseRecyclerViewAdapter<T> {
    private var delegatesManager: EaseAdapterDelegatesManager

    constructor() {
        delegatesManager = EaseAdapterDelegatesManager(false)
    }

    constructor(delegatesManager: EaseAdapterDelegatesManager) {
        this.delegatesManager = delegatesManager
    }

    open fun addDelegate(delegate: EaseAdapterDelegate<*, *>): EaseBaseDelegateAdapter<*> {
        delegatesManager.addDelegate(delegate, delegate.getTag())
        notifyDataSetChanged()
        return this
    }

    fun addDelegate(delegate: EaseAdapterDelegate<*, *>, tag: String?): EaseBaseDelegateAdapter<*> {
        delegate.setTag(tag!!)
        return addDelegate(delegate)
    }

    fun getDelegateViewType(delegate: EaseAdapterDelegate<Any, EaseBaseRecyclerViewAdapter.ViewHolder<*>>): Int {
        return delegatesManager.getDelegateViewType(delegate)
    }

    fun setFallbackDelegate(
        delegate: EaseAdapterDelegate<Any, EaseBaseRecyclerViewAdapter.ViewHolder<*>>,
        tag: String?
    ): EaseBaseDelegateAdapter<*> {
        delegate.setTag(tag!!)
        return setFallbackDelegate(delegate)
    }

    open fun setFallbackDelegate(delegate: EaseAdapterDelegate<Any, EaseBaseRecyclerViewAdapter.ViewHolder<*>>?): EaseBaseDelegateAdapter<*> {
        delegatesManager.fallbackDelegate = delegate
        return this
    }

    fun getAdapterDelegate(viewType: Int): EaseAdapterDelegate<*, *>? {
        return delegatesManager.getDelegate(viewType)
    }


    val allDelegate: List<EaseAdapterDelegate<Any, EaseBaseRecyclerViewAdapter.ViewHolder<*>>>
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

    override fun getViewHolder(parent: ViewGroup?, viewType: Int):EaseBaseRecyclerViewAdapter.ViewHolder<T> {
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
        if (!delegatesManager.allDelegates.isEmpty()) {
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
        if (!delegatesManager.allDelegates.isEmpty()) {
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