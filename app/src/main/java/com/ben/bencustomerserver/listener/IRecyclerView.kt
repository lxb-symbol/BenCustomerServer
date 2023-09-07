package com.ben.bencustomerserver.listener

import androidx.recyclerview.widget.RecyclerView

interface IRecyclerView {
    /**
     * 添加头部adapter
     *
     * @param adapter
     */
    fun addHeaderAdapter(adapter: RecyclerView.Adapter<*>?)

    /**
     * 添加尾部adapter
     *
     * @param adapter
     */
    fun addFooterAdapter(adapter: RecyclerView.Adapter<*>?)

    /**
     * 移除adapter
     *
     * @param adapter
     */
    fun removeAdapter(adapter: RecyclerView.Adapter<*>?)

    /**
     * 添加装饰类
     *
     * @param decor
     */
    fun addRVItemDecoration(decor: RecyclerView.ItemDecoration)

    /**
     * 移除装饰类
     *
     * @param decor
     */
    fun removeRVItemDecoration(decor: RecyclerView.ItemDecoration)

    /**
     * 设置条目点击事件
     */
    fun setOnItemClickListener(listener: OnItemClickListener?) {}

    /**
     * 设置条目长按事件
     */
    fun setOnItemLongClickListener(listener: OnItemLongClickListener?) {}
}