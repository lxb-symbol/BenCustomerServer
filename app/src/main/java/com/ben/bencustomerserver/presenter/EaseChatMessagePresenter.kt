package com.ben.bencustomerserver.presenter

import com.ben.bencustomerserver.listener.ILoadDataView

abstract class EaseChatMessagePresenter : EaseBasePresenter() {
    //TODO 先屏蔽掉
//    var mView: IChatMessageListView? = null
//    var conversation: EMConversation? = null
    override fun attachView(view: ILoadDataView?) {
//        mView = view as IChatMessageListView?
    }

    override fun detachView() {
//        mView = null
    }

    override fun onDestroy() {
        super.onDestroy()
        detachView()
    }

    /**
     * 与会话绑定
     * TODO 暂时屏蔽
     * @param conversation
     */
//    fun setupWithConversation(conversation: EMConversation?) {
//        this.conversation = conversation
//    }

    abstract fun joinChatRoom(username: String?)

    /**
     * 加载本地数据
     * @param pageSize
     */
    abstract fun loadLocalMessages(pageSize: Int)

    /**
     * 加载更多本地数据
     * @param pageSize
     */
    abstract fun loadMoreLocalMessages(msgId: String?, pageSize: Int)

    /**
     * 从本地加载更多历史数据
     * @param msgId
     * @param pageSize
     * @param direction
     *
     * TODO(" 暂时屏蔽"）
     */
//    abstract fun loadMoreLocalHistoryMessages(
//        msgId: String?,
//        pageSize: Int,
//        direction: EMConversation.EMSearchDirection?
//    )

    /**
     * 从服务器加载数据
     * @param pageSize
     */
    abstract fun loadServerMessages(pageSize: Int)

    /**
     * TODO("暂时屏蔽"）
     * Load data from the server
     * @param pageSize
     */
//    abstract fun loadServerMessages(pageSize: Int, direction: EMConversation.EMSearchDirection?)

    /**
     * 从服务器加载更多数据
     * @param msgId 消息id
     * @param pageSize
     */
    abstract fun loadMoreServerMessages(msgId: String?, pageSize: Int)

    /**
     * Load more data from the server
     * @param msgId 消息id
     * @param pageSize
     *  暂时屏蔽
     */
//    abstract fun loadMoreServerMessages(
//        msgId: String?,
//        pageSize: Int,
//        direction: EMConversation.EMSearchDirection?
//    )

    /**
     * 刷新当前的会话
     */
    abstract fun refreshCurrentConversation()

    /**
     * 刷新当前会话，并移动到最新
     */
    abstract fun refreshToLatest()
}