package com.ben.bencustomerserver.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.listener.OnItemClickListener
import com.ben.bencustomerserver.listener.OnItemLongClickListener
import com.ben.bencustomerserver.model.BaseMessageModel

/**
 * 作为RecyclerView Adapter的基类，有默认空白布局
 * 如果要修改默认布局可以采用以下两种方式：1、在app layout中新建ease_layout_default_no_data.xml覆盖。
 * 2、继承EaseBaseRecyclerViewAdapter后，重写getEmptyLayoutId()方法，返回自定义的布局即可。
 * 3、[.VIEW_TYPE_EMPTY]建议设置成负值，以防占用
 * [.addDelegate]
 * 中相应的position
 * @param <T>
</T> */
abstract class EaseBaseRecyclerViewAdapter<T> :
    EaseBaseAdapter<EaseBaseRecyclerViewAdapter.ViewHolder<T>>() {

    open var mOnItemClickListener: OnItemClickListener? = null
    open var mOnItemLongClickListener: OnItemLongClickListener? = null
    open var mItemSubViewListener: OnItemSubViewClickListener? = null
    open var mContext: Context? = null
    open var mData: MutableList<T>? = null
    open var hideEmptyView = false
    open var emptyView: View? = null
    open var emptyViewId = 0
    open var mUserPresenceListener: OnUserPresenceListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
        Log.i("adapter", "onCreateViewHolder()")
        mContext = parent.context
        if (viewType == VIEW_TYPE_EMPTY) {
            return getEmptyViewHolder(parent)
        }
        val holder = getViewHolder(parent, viewType)
        if (isItemClickEnable) {
            holder.itemView.setOnClickListener { v ->
                itemClickAction(v, holder.bindingAdapterPosition)
            }
        }
        if (isItemLongClickEnable) {
            holder.itemView.setOnLongClickListener { v ->
                itemLongClickAction(v, holder.bindingAdapterPosition)
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        holder.setAdapter(this)
        //增加viewType类型的判断
        if (isEmptyViewType(position)) {
            return
        }
        if (mData == null || mData!!.isEmpty()) {
            return
        }
        val item = getItem(position)
        holder.setData(item, position)
        holder.setDataList(mData, position)
    }

    /**
     * 判断是否是空布局类型
     * @param position
     * @return
     */
    fun isEmptyViewType(position: Int): Boolean {
        val viewType = getItemViewType(position)
        return viewType == VIEW_TYPE_EMPTY
    }

    fun itemLongClickAction(v: View?, position: Int): Boolean {
        return if (mOnItemLongClickListener != null) {
            mOnItemLongClickListener!!.onItemLongClick(v, position)
        } else false
    }

    override fun getItemCount(): Int {
        return if (mData == null || mData?.isEmpty()!!) 1 else mData?.size!!
    }

    override fun getItemViewType(position: Int): Int {
        return if (mData == null || mData!!.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_ITEM
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    val isItemClickEnable: Boolean
        /**
         * 条目单击事件是否可用
         * 默认为true，如果需要自己设置请设置为false
         * @return
         */
        get() = true
    val isItemLongClickEnable: Boolean
        /**
         * 条目长按事件是否可用
         * 默认为true
         * @return
         */
        get() = true

    /**
     * 点击事件
     * @param v
     * @param position
     */
    fun itemClickAction(v: View?, position: Int) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener!!.onItemClick(v, position)
        }
    }

    /**
     * 返回数据为空时的布局
     * @param parent
     * @return
     */
    protected fun getEmptyViewHolder(parent: ViewGroup): ViewHolder<T> {
        var emptyView: View? = getEmptyView(parent)
        if (this.emptyView != null) {
            emptyView = this.emptyView
        }
        if (emptyViewId > 0) {
            emptyView = LayoutInflater.from(mContext).inflate(emptyViewId, parent, false)
        }
        if (hideEmptyView) {
            emptyView = LayoutInflater.from(mContext)
                .inflate(R.layout.ease_layout_no_data_show_nothing, parent, false)
        }
        return object : ViewHolder<T>(
            emptyView!!
        ) {
            override fun initView(itemView: View?) {}
            override fun setData(item: T, position: Int) {}
        }
    }

    /**
     * 隐藏空白布局
     * @param hide
     */
    fun hideEmptyView(hide: Boolean) {
        hideEmptyView = hide
        notifyDataSetChanged()
    }

//    /**
//     * 设置空白布局
//     * @param emptyView
//     */
//    fun setEmptyView(emptyView: View?) {
//        this.emptyView = emptyView
//        notifyDataSetChanged()
//    }

    /**
     * 设置空白布局
     * @param emptyViewId
     */
    fun setEmptyView(@LayoutRes emptyViewId: Int) {
        this.emptyViewId = emptyViewId
        notifyDataSetChanged()
    }

    /**
     * 获取空白布局
     * @param parent
     * @return
     */
    private fun getEmptyView(parent: ViewGroup): View {
        return LayoutInflater.from(mContext).inflate(emptyLayoutId, parent, false)
    }

    /**
     * 获取ViewHolder
     * @param parent
     * @param viewType
     * @return
     */
    abstract fun getViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder<T>

    /**
     * 根据position获取相应的data
     * @param position
     * @return
     */
    @Synchronized
    override fun getItem(position: Int): T {
        return mData?.let { it[position] }!!
    }

    /**
     * 添加数据
     * @param data
     */
    @Synchronized
    fun setData(data: MutableList<T>) {
        mData = data
        notifyDataSetChanged()
    }

    /**
     * 添加单个数据
     * @param item
     */
    fun addData(item: T) {
        synchronized(EaseBaseRecyclerViewAdapter::class.java) {
            if (mData == null) {
                mData = ArrayList()
            }
            mData!!.add(item)
        }
        notifyDataSetChanged()
    }

    /**
     * 添加更多数据
     * @param data
     */
    fun addData(data: MutableList<T>?) {
        synchronized(EaseBaseRecyclerViewAdapter::class.java) {
            if (data == null || data.isEmpty()) {
                return
            }
            if (mData == null) {
                mData = data
            } else {
                mData!!.addAll(data)
            }
        }
        notifyDataSetChanged()
    }

    /**
     * 添加更多数据
     * @param position 插入位置
     * @param data
     */
    fun addData(position: Int, data: MutableList<T>?) {
        synchronized(EaseBaseRecyclerViewAdapter::class.java) {
            if (data.isNullOrEmpty()) {
                return
            }
            if (mData == null) {
                mData = data
            } else {
                mData!!.addAll(position, data)
            }
        }
        notifyDataSetChanged()
    }

    /**
     * 添加更多数据
     * @param position
     * @param data
     * @param refresh
     */
    fun addData(position: Int, data: MutableList<T>?, refresh: Boolean) {
        synchronized(EaseBaseRecyclerViewAdapter::class.java) {
            if (data == null || data.isEmpty()) {
                return
            }
            if (mData == null) {
                mData = data
            } else {
                mData!!.addAll(position, data)
            }
        }
        if (refresh) {
            notifyDataSetChanged()
        }
    }

    val data: List<T>?
        /**
         * 获取数据
         * @return
         */
        get() = mData

    /**
     * 清除数据
     */
    fun clearData() {
        if (mData != null) {
            mData!!.clear()
            notifyDataSetChanged()
        }
    }

    /**
     * set item click
     * @param listener
     */
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mOnItemClickListener = listener
    }

    /**
     * set item long click
     * @param longClickListener
     */
    fun setOnItemLongClickListener(longClickListener: OnItemLongClickListener?) {
        mOnItemLongClickListener = longClickListener
    }

    abstract class ViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /**
         * Get adapter
         * @return
         */
        var adapter: EaseBaseAdapter<ViewHolder<T>>? = null
            private set

        init {
            initView(itemView)
        }

        /**
         * Set data when viewType is VIEW_TYPE_EMPTY
         */
        fun setEmptyData() {}

        /**
         * Initialize the views
         * @param itemView
         */
        open fun initView(itemView: View?) {}

        /**
         * Set data
         * @param item
         * @param position
         */
        abstract fun setData(item: T, position: Int)

        /**
         * @param id
         * @param <E>
         * @return
        </E> */
        fun <E : View?> findViewById(@IdRes id: Int): E {
            return itemView.findViewById(id)
        }

        /**
         * Set data to provide a data set
         * @param data
         * @param position
         */
        open fun setDataList(data: List<T>?, position: Int) {}

        /**
         * Set adapter
         * @param adapter
         */
        fun setAdapter(adapter: EaseBaseRecyclerViewAdapter<T>) {
            this.adapter = adapter
        }
    }

    private val emptyLayoutId: Int
        /**
         * 返回空白布局
         * @return
         */
        get() = R.layout.ease_layout_default_no_data

    /**
     * item sub view interface
     */
    interface OnItemSubViewClickListener {
        fun onItemSubViewClick(view: View?, position: Int)
    }

    /**
     * set item sub view click
     * @param mItemSubViewListener
     */
    fun setOnItemSubViewClickListener(mItemSubViewListener: OnItemSubViewClickListener?) {
        this.mItemSubViewListener = mItemSubViewListener
    }

    interface OnUserPresenceListener {
        fun subscribe(username: String?, expireTime: Long)
    }

    fun setOnUserPresenceListener(userPresenceListener: OnUserPresenceListener?) {
        mUserPresenceListener = userPresenceListener
    }

    companion object {
        const val VIEW_TYPE_EMPTY = -1
        const val VIEW_TYPE_ITEM = 0
    }
}