package com.ben.bencustomerserver.adapter

import android.text.TextUtils
import android.util.Log
import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import com.ben.bencustomerserver.delegate.BenMessageAdapterDelegate
import com.ben.bencustomerserver.model.BaseMessageModel
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class BenAdapterDelegatesManager(private val hasConsistItemType: Boolean) {
    private val dataTypeWithTags = SparseArrayCompat<String>()
    private val delegates =
        SparseArrayCompat<BenAdapterDelegate<Any,BenBaseRecyclerViewAdapter.ViewHolder<*>>>()

    @JvmField
    var fallbackDelegate: BenAdapterDelegate<Any, BenBaseRecyclerViewAdapter.ViewHolder<*>>? =
        null

    fun addDelegate(delegate: BenAdapterDelegate<*, *>, tag: String):BenAdapterDelegatesManager {
        val superclass = getParameterizedType(delegate.javaClass)
        if (superclass is ParameterizedType) {
            val clazz = superclass.actualTypeArguments[0] as Class<*>
            val typeWithTag = typeWithTag(clazz, tag)
            val viewType = if (hasConsistItemType) delegate.itemViewType else delegates.size()
            delegates.put(
                viewType,
                delegate as BenAdapterDelegate<Any, BenBaseRecyclerViewAdapter.ViewHolder<*>>
            )
            dataTypeWithTags.put(viewType, typeWithTag)
        } else {
            // Has no generics.
            throw IllegalArgumentException(
                String.format(
                    "Please set the correct generic parameters on %s.", delegate.javaClass.name
                )
            )
        }
        return this
    }

    private fun getParameterizedType(clazz: Class<*>?): Type? {
        if (clazz == null) {
            return null
        }
        val superclass = clazz.genericSuperclass
        return superclass as? ParameterizedType ?: if (clazz.name == "java.lang.Object") {
            null
        } else getParameterizedType(clazz.superclass)
    }

    fun getDelegate(viewType: Int): BenAdapterDelegate<Any, BenBaseRecyclerViewAdapter.ViewHolder<*>>? {
        return delegates[viewType] ?: return fallbackDelegate
    }

    val allDelegates: List<BenAdapterDelegate<Any, BenBaseRecyclerViewAdapter.ViewHolder<*>>>
        get() {
            val list: MutableList<BenAdapterDelegate<Any, BenBaseRecyclerViewAdapter.ViewHolder<*>>> =
                ArrayList()
            if (!delegates.isEmpty) {
                for (i in 0 until delegates.size()) {
                    list.add(delegates.valueAt(i))
                }
            }
            if (fallbackDelegate != null) {
                list.add(fallbackDelegate!!)
            }
            return list
        }

    fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BenBaseRecyclerViewAdapter.ViewHolder<*> {
        val delegate = getDelegate(viewType)
            ?: throw NullPointerException("No BenAdapterDelegate added for ViewType $viewType")

        val tag = getTagByViewType(viewType)
        return delegate.onCreateViewHolder(parent, tag)
    }

    /**
     * 注意：此处获取viewType时，不要直接使用[RecyclerView.ViewHolder.getItemViewType]
     * 使用ConcatAdapter时，返回的viewType不准确。下同
     * @param holder
     * @param position
     * @param item
     */
    fun onBindViewHolder(
        holder: BenBaseRecyclerViewAdapter.ViewHolder<*>, position: Int, item: Any
    ) {
        val viewType = holder.adapter!!.getItemViewType(position)
        val delegate = getDelegate(viewType) ?: throw NullPointerException(
            "No delegate found for item at position = $position for viewType = $viewType"
        )
        delegate.onBindViewHolder(holder, position, targetItem(item))
    }

    fun onBindViewHolder(
        holder: BenBaseRecyclerViewAdapter.ViewHolder<*>,
        position: Int,
        payloads: List<Any?>,
        item: Any
    ) {
        val viewType = holder.adapter!!.getItemViewType(position)
        val delegate = getDelegate(viewType) ?: throw NullPointerException(
            "No delegate found for item at position = $position for viewType = $viewType"
        )
        delegate.onBindViewHolder(holder, position, payloads, targetItem(item))
    }

    fun getItemViewType(item: Any, position: Int): Int {
        val clazz: Class<*> = targetItem(item).javaClass
        val tag = targetTag(item)
        val typeWithTag = typeWithTag(clazz, tag)
        val indexList = indexesOfValue(dataTypeWithTags, typeWithTag)
        for (index in indexList) {
            val delegate = delegates[index]
            if (delegate != null && delegate.tags.contains(tag) && delegate.isForViewType(item, position)) {
                return if (hasConsistItemType) delegate.itemViewType else index
            }
        }
        if (fallbackDelegate != null && fallbackDelegate!!.isForViewType(item, position)) {
            var index = 0
            if (fallbackDelegate!!.tags.contains(tag)) {
                index = fallbackDelegate!!.tags.indexOf(tag)
            }
            return if (hasConsistItemType) fallbackDelegate!!.itemViewType + index else delegates.size() + index
        }
        throw NullPointerException(
            "No BenAdapterDelegate added that matches position = $position item = " + targetItem(
                item
            ) + " in data source."
        )
    }

    fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        val delegate = getDelegate(holder.itemViewType)
        delegate?.onViewRecycled(holder)
    }

    fun onFailedToRecycleView(holder: RecyclerView.ViewHolder): Boolean {
        val delegate = getDelegate(holder.itemViewType)
        return delegate != null && delegate.onFailedToRecycleView(holder)
    }

    fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        val delegate = getDelegate(holder.itemViewType)
        delegate?.onViewAttachedToWindow(holder)
    }

    fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        val delegate = getDelegate(holder.itemViewType)
        delegate?.onViewDetachedFromWindow(holder)
    }

    fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        for (i in 0 until delegates.size()) {
            val delegate = delegates[i]
            delegate?.onAttachedToRecyclerView(recyclerView)
        }
    }

    fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        for (i in 0 until delegates.size()) {
            val delegate = delegates[i]
            delegate?.onDetachedFromRecyclerView(recyclerView)
        }
    }

    fun getDelegateViewType(delegate: BenAdapterDelegate<Any, BenBaseRecyclerViewAdapter.ViewHolder<*>>): Int {
        val index = delegates.indexOfValue(delegate)
        return if (index > 0) delegates.keyAt(index) else -1
    }

    private fun getTagByViewType(viewType: Int): String? {
        //最关键 为什么一直返回 receive symbol-todo 有时候 tag 获取是空
        return if (dataTypeWithTags.containsKey(viewType)) {
            val typeWithTag = dataTypeWithTags[viewType]
            if (TextUtils.isEmpty(typeWithTag)) {
                return typeWithTag
            }
            if (!typeWithTag!!.contains(":")) {
                typeWithTag
            } else typeWithTag.split(":".toRegex())[1]
        } else {
            val index =
                if (hasConsistItemType) viewType - fallbackDelegate!!.itemViewType else viewType - delegates.size()
            if (fallbackDelegate!!.tags.size <= index) {
                null
            } else fallbackDelegate!!.tags[index]
        }
    }

    private fun typeWithTag(clazz: Class<*>, tag: String): String {
        return if (TextUtils.isEmpty(tag)) clazz.name else clazz.name + ":" + tag
    }

    private fun targetItem(item: Any): Any {
        return item
    }

    private fun targetTag(item: Any): String {
        if (item is BaseMessageModel) {
            if (!delegates.isEmpty) {
                var isChat = true
                for (i in 0 until delegates.size()) {
                    val key = delegates.indexOfKey(i)
                    val delegate = delegates[key]!!
                    if (delegate !is BenMessageAdapterDelegate<*, *>) {
                        isChat = false
                        break
                    }
                }
                return if (isChat) item.direct.toString() else BenAdapterDelegate.Companion.DEFAULT_TAG
            }
            return BenAdapterDelegate.Companion.DEFAULT_TAG
        }
        return BenAdapterDelegate.Companion.DEFAULT_TAG
    }

    private fun indexesOfValue(array: SparseArrayCompat<String>, value: String): List<Int> {
        val indexes: MutableList<Int> = ArrayList()
        for (i in 0 until array.size()) {
            if (TextUtils.equals(array.valueAt(i), value)) {
                indexes.add(array.keyAt(i))
            }
        }
        return indexes
    }
}