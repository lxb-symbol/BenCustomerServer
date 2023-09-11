package com.ben.bencustomerserver.manager

import com.ben.bencustomerserver.adapter.EaseAdapterDelegate
import com.ben.bencustomerserver.adapter.EaseBaseDelegateAdapter
import com.ben.bencustomerserver.adapter.EaseBaseRecyclerViewAdapter
import com.ben.bencustomerserver.delegate.EaseCustomAdapterDelegate
import com.ben.bencustomerserver.delegate.EaseExpressionAdapterDelegate
import com.ben.bencustomerserver.delegate.EaseFileAdapterDelegate
import com.ben.bencustomerserver.delegate.EaseImageAdapterDelegate
import com.ben.bencustomerserver.delegate.EaseLocationAdapterDelegate
import com.ben.bencustomerserver.delegate.EaseTextAdapterDelegate
import com.ben.bencustomerserver.delegate.EaseVideoAdapterDelegate
import com.ben.bencustomerserver.delegate.EaseVoiceAdapterDelegate

class EaseMessageTypeSetManager private constructor() {
    private var defaultDelegate: EaseAdapterDelegate<*, *>? = EaseTextAdapterDelegate()
    private var defaultDelegateCls: Class<out EaseAdapterDelegate<*, *>>? = null
    private val delegates: MutableSet<Class<out EaseAdapterDelegate<*, *>>> = HashSet()
    private val delegateList: MutableList<Class<out EaseAdapterDelegate<*, *>>> = ArrayList()
    private var hasConsistItemType = false

    /**
     * 是否使用自定义的item ViewType
     * @param hasConsistItemType
     * @return
     */
    fun setConsistItemType(hasConsistItemType: Boolean): EaseMessageTypeSetManager {
        this.hasConsistItemType = hasConsistItemType
        return this
    }

    fun addMessageType(cls: Class<out EaseAdapterDelegate<*, *>>): EaseMessageTypeSetManager {
        val size = delegates.size
        delegates.add(cls)
        if (delegates.size > size) {
            delegateList.add(cls)
        }
        return this
    }

    /**
     * 设置默认的对话类型
     * @param cls
     * @return
     */
    fun setDefaultMessageType(cls: Class<out EaseAdapterDelegate<*, *>>?): EaseMessageTypeSetManager {
        defaultDelegateCls = cls
        return this
    }

    /**
     * 注册消息类型
     * @param adapter
     */
    @Throws(InstantiationException::class, IllegalAccessException::class)
    fun registerMessageType(adapter: EaseBaseDelegateAdapter<Any>?) {
        if (adapter == null) {
            return
        }
        //如果没有注册聊天类型，则使用默认的
        if (delegateList.size == 0) {
            addMessageType(EaseExpressionAdapterDelegate::class.java) //自定义表情
                .addMessageType(EaseFileAdapterDelegate::class.java) //文件
                .addMessageType(EaseImageAdapterDelegate::class.java) //图片
                .addMessageType(EaseLocationAdapterDelegate::class.java) //定位
                .addMessageType(EaseVideoAdapterDelegate::class.java) //视频
                .addMessageType(EaseVoiceAdapterDelegate::class.java) //声音
                .addMessageType(EaseCustomAdapterDelegate::class.java) //自定义消息
                .setDefaultMessageType(EaseTextAdapterDelegate::class.java) //文本
        }
        for (cls in delegateList) {
            val delegate = cls.newInstance()
            adapter.addDelegate(delegate)
        }
        defaultDelegate = if (defaultDelegateCls == null) {
            EaseTextAdapterDelegate()
        } else {
            defaultDelegateCls!!.newInstance()
        }
        adapter.setFallbackDelegate(defaultDelegate as  EaseAdapterDelegate<Any,EaseBaseRecyclerViewAdapter.ViewHolder<*>>)
    }

    fun hasConsistItemType(): Boolean {
        return hasConsistItemType
    }

    fun release() {
        defaultDelegate = null
    }

    companion object {
        private var mInstance: EaseMessageTypeSetManager? = null
        val instance: EaseMessageTypeSetManager?
            get() {
                if (mInstance == null) {
                    synchronized(EaseMessageTypeSetManager::class.java) {
                        if (mInstance == null) {
                            mInstance = EaseMessageTypeSetManager()
                        }
                    }
                }
                return mInstance
            }
    }
}