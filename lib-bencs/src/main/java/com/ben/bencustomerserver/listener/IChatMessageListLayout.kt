package com.ben.bencustomerserver.listener

import com.ben.bencustomerserver.adapter.BenMessageAdapter
import com.ben.bencustomerserver.presenter.BenChatMessagePresenter
import com.ben.bencustomerserver.views.BenChatMessageListLayout.OnChatErrorListener
import com.ben.bencustomerserver.views.BenChatMessageListLayout.OnMessageTouchListener

interface IChatMessageListLayout : IRecyclerView {
    /**
     * 设置presenter
     * @param presenter
     */
    fun setPresenter(presenter: BenChatMessagePresenter?)

    /**
     * 获取adapter
     * @return
     */
    val messageAdapter: BenMessageAdapter?

    /**
     * 设置聊天区域的touch监听，判断是否点击在条目消息外，是否正在拖拽列表
     * @param listener
     */
    fun setOnMessageTouchListener(listener: OnMessageTouchListener?)

    /**
     * 设置聊天过程中的错误监听
     * @param listener
     */
    fun setOnChatErrorListener(listener: OnChatErrorListener?)

    /**
     * 设置聊天列表条目中各个控件的点击事件
     * @param listener
     */
    fun setMessageListItemClickListener(listener: MessageListItemClickListener?)
}