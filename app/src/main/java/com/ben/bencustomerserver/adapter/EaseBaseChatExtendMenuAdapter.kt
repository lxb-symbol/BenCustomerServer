package com.ben.bencustomerserver.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class EaseBaseChatExtendMenuAdapter<VH : RecyclerView.ViewHolder?, T> :
    RecyclerView.Adapter<VH>() {
    @JvmField
    var mData: List<T>? = null

    /**
     * 设置数据
     * @param data
     */
    fun setData(data: List<T>?) {
        mData = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (mData == null) 0 else mData!!.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH & Any {
        val view = LayoutInflater.from(parent.context).inflate(itemLayoutId, parent, false)
        return easeCreateViewHolder(view)!!
    }

    protected abstract val itemLayoutId: Int

    /**
     * 获取ViewHolder
     * @param view
     * @return
     */
    protected abstract fun easeCreateViewHolder(view: View?): VH
}