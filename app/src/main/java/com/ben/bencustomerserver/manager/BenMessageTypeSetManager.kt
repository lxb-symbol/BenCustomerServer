package com.ben.bencustomerserver.manager

import com.ben.bencustomerserver.adapter.BenAdapterDelegate
import com.ben.bencustomerserver.adapter.BenBaseDelegateAdapter
import com.ben.bencustomerserver.adapter.BenBaseRecyclerViewAdapter
import com.ben.bencustomerserver.delegate.BenCustomAdapterDelegate
import com.ben.bencustomerserver.delegate.BenExpressionAdapterDelegate
import com.ben.bencustomerserver.delegate.BenFileAdapterDelegate
import com.ben.bencustomerserver.delegate.BenImageAdapterDelegate
import com.ben.bencustomerserver.delegate.BenLocationAdapterDelegate
import com.ben.bencustomerserver.delegate.BenTextAdapterDelegate
import com.ben.bencustomerserver.delegate.BenVideoAdapterDelegate
import com.ben.bencustomerserver.delegate.BenVoiceAdapterDelegate

class BenMessageTypeSetManager private constructor() {
    private var defaultDelegate: BenAdapterDelegate<*, *>? = BenTextAdapterDelegate()
    private var defaultDelegateCls: Class<out BenAdapterDelegate<*, *>>? = null
    private val delegates: MutableSet<Class<out BenAdapterDelegate<*, *>>> = HashSet()
    private val delegateList: MutableList<Class<out BenAdapterDelegate<*, *>>> = ArrayList()
    private var hasConsistItemType = false

    /**
     * 是否使用自定义的item ViewType
     * @param hasConsistItemType
     * @return
     */
    fun setConsistItemType(hasConsistItemType: Boolean): BenMessageTypeSetManager {
        this.hasConsistItemType = hasConsistItemType
        return this
    }

    fun addMessageType(cls: Class<out BenAdapterDelegate<*, *>>): BenMessageTypeSetManager {
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
    fun setDefaultMessageType(cls: Class<out BenAdapterDelegate<*, *>>?): BenMessageTypeSetManager {
        defaultDelegateCls = cls
        return this
    }

    /**
     * 注册消息类型
     * @param adapter
     */
    @Throws(InstantiationException::class, IllegalAccessException::class)
    fun registerMessageType(adapter: BenBaseDelegateAdapter<Any>?) {
        if (adapter == null) {
            return
        }
        //如果没有注册聊天类型，则使用默认的
        if (delegateList.size == 0) {
            addMessageType(BenExpressionAdapterDelegate::class.java) //自定义表情
                .addMessageType(BenFileAdapterDelegate::class.java) //文件
                .addMessageType(BenImageAdapterDelegate::class.java) //图片
                .addMessageType(BenLocationAdapterDelegate::class.java) //定位
                .addMessageType(BenVideoAdapterDelegate::class.java) //视频
                .addMessageType(BenVoiceAdapterDelegate::class.java) //声音
                .addMessageType(BenCustomAdapterDelegate::class.java) //自定义消息
                .setDefaultMessageType(BenTextAdapterDelegate::class.java) //文本
        }
        for (cls in delegateList) {
            val delegate = cls.newInstance()
            adapter.addDelegate(delegate)
        }
        defaultDelegate = if (defaultDelegateCls == null) {
            BenTextAdapterDelegate()
        } else {
            defaultDelegateCls!!.newInstance()
        }
        adapter.setFallbackDelegate(defaultDelegate as  BenAdapterDelegate<Any,BenBaseRecyclerViewAdapter.ViewHolder<*>>)
    }

    fun hasConsistItemType(): Boolean {
        return hasConsistItemType
    }

    fun release() {
        defaultDelegate = null
    }

    companion object {
        private var mInstance: BenMessageTypeSetManager? = null
        val instance: BenMessageTypeSetManager?
            get() {
                if (mInstance == null) {
                    synchronized(BenMessageTypeSetManager::class.java) {
                        if (mInstance == null) {
                            mInstance = BenMessageTypeSetManager()
                        }
                    }
                }
                return mInstance
            }
    }
}