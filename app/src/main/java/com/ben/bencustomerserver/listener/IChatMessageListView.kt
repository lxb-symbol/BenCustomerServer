package com.ben.bencustomerserver.listener

import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.SearchDirection

interface IChatMessageListView : ILoadDataView {
    /**
     * 获取当前会话
     * @return
     */
    /**
     * 加入聊天室成功
     * @param value
     */
    /**
     * 加入聊天室失败
     * @param error
     * @param errorMsg
     */
    /**
     * 加载消息失败
     * @param error
     * @param message
     */
    fun loadMsgFail(error: Int, message: String?)

    /**
     * 加载本地数据成功
     * @param data
     */
    fun loadLocalMsgSuccess(data: List<BaseMessageModel>?)

    /**
     * 没有加载到本地数据
     */
    fun loadNoLocalMsg()

    /**
     * 加载本地更多数据成功
     * @param data
     */
    fun loadMoreLocalMsgSuccess(data: List<BaseMessageModel>?)

    /**
     * 没有加载到更多数据
     */
    fun loadNoMoreLocalMsg()

    /**
     * 加载更多本地的历史数据
     * @param data
     */
    fun loadMoreLocalHistoryMsgSuccess(data: List<BaseMessageModel>?, direction: SearchDirection?)

    /**
     * 没有更多的本地历史数据
     */
    fun loadNoMoreLocalHistoryMsg()

    /**
     * 加载漫游数据
     * @param data
     */
    fun loadServerMsgSuccess(data: List<BaseMessageModel>?, cursor: String?)

    /**
     * 加载更多漫游数据
     * @param data
     */
    fun loadMoreServerMsgSuccess(data: List<BaseMessageModel>?, cursor: String?)

    /**
     * 刷新当前会话
     * @param data
     */
    fun refreshCurrentConSuccess(data: List<BaseMessageModel>?, toLatest: Boolean)

    /**
     * Insert the message to the last of message list
     * @param message
     */
    fun insertMessageToLast(message: BaseMessageModel?)

    /**
     * Whether thread message list has reached the lasted message
     */
    fun reachedLatestThreadMessage()
}