package com.ben.bencustomerserver.adapter

import android.text.TextUtils
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * （1）作为代理类的基类，通过[BenAdapterDelegatesManager]实现与[RecyclerView.Adapter]的关联。
 * （2）判断是否使用这个代理类的方法为[.isForViewType]，如果它返回true就代表这个代理类进行数据处理。
 * 具体逻辑，可见[BenAdapterDelegatesManager.getItemViewType]。
 * （3）[BenBaseDelegateAdapter.getItemViewType]中得到不同delegate对应的viewType。
 * （4）[BenBaseDelegateAdapter.getViewHolder]通过ViewType得到对应的[.onCreateViewHolder],
 * 这个[.onCreateViewHolder]就是需要具体的实现。
 * 特别说明一下：
 * 本文是参考：https://github.com/xuehuayous/DelegationAdapter实现而来。
 * 针对具体的项目中，如何实现对话类型的方便插入，而且对象类型一般是接收方和发送发两种样式，但是实现类似。
 * 针对这种情况，项目中使用tag进行区分，也就是说一个delegate对应多个tag，从而对应多个布局，所以这里就设计了tags集合用于管理
 * 同一个delegate对应多个tag的情况，并对[BenAdapterDelegatesManager.getItemViewType]中相关逻辑进行修改。
 *
 * @param <T>
 * @param <VH>
</VH></T> */
abstract class BenAdapterDelegate<T, VH : RecyclerView.ViewHolder?> : Cloneable {
    open var tag = DEFAULT_TAG
    open var tags: MutableList<String> = ArrayList()


    constructor(){
        if (!TextUtils.isEmpty(tag)) {
            tags.add(tag)
        }
    }

    constructor(tag: String) {
        if (!TextUtils.isEmpty(tag)) {
            tags.add(tag)
        }
    }


    open fun isForViewType(item: T, position: Int): Boolean {
        return true
    }

    abstract fun onCreateViewHolder(parent: ViewGroup?, tag: String?): VH
    fun onBindViewHolder(holder: VH, position: Int, item: T) {}
    val itemCount: Int
        get() = 0

    /**
     * Called by RecyclerView to display the data at the specified position. This method
     * should update the contents of the [RecyclerView.ViewHolder.itemView] to reflect the item at
     * the given position.
     */
    fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: List<Any?>,
        item: T
    ) {
    }

    fun onViewRecycled(holder: RecyclerView.ViewHolder) {}
    fun onFailedToRecycleView(holder: RecyclerView.ViewHolder): Boolean {
        return false
    }

    fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {}
    fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {}
    fun onAttachedToRecyclerView(recyclerView: RecyclerView) {}
    fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {}
    val itemViewType: Int
        get() = 0


    @Throws(CloneNotSupportedException::class)
    public override fun clone(): Any {
        var obj: Any? = null
        try {
            obj = super.clone()
        } catch (e: CloneNotSupportedException) {
            e.printStackTrace()
        }
        return obj!!
    }

    companion object {
        const val DEFAULT_TAG = ""
    }
}