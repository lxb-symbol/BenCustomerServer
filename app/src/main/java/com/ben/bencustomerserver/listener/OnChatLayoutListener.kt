package com.ben.bencustomerserver.listener

import android.view.View

/**
 * 用于监听[com.hyphenate.easeui.modules.chat.EaseChatLayout]中的变化
 */
interface OnChatLayoutListener {
    /**
     * 点击消息bubble区域
     * @param message
     * @return
     */
//    fun onBubbleClick(message: BaseMessageModel?): Boolean

    /**
     * 长按消息bubble区域
     * @param v
     * @param message
     * @return
     */
//    fun onBubbleLongClick(v: View?, message: BaseMessageModel?): Boolean

    /**
     * 点击头像
     * @param username
     */
    fun onUserAvatarClick(username: String?)

    /**
     * 长按头像
     * @param username
     */
    fun onUserAvatarLongClick(username: String?)

    /**
     * 条目点击
     * @param view
     * @param itemId
     */
    fun onChatExtendMenuItemClick(view: View?, itemId: Int)

    /**
     * EditText文本变化监听
     * @param s
     * @param start
     * @param before
     * @param count
     */
    fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)

    /**
     * 发送消息成功后的回调
     * @param message
     */
//    fun onChatSuccess(message: BaseMessageModel?) {}

    /**
     * 聊天中错误信息
     * @param code
     * @param errorMsg
     */
    fun onChatError(code: Int, errorMsg: String?)

    /**
     * 用于监听其他人正在数据事件
     * @param action 输入事件 TypingBegin为开始 TypingEnd为结束
     */
    fun onOtherTyping(action: String?) {}

    /**
     * Click on thread region
     * @param messageId
     * @param threadId
     * @param parentId
     * @return
     */
    fun onThreadClick(messageId: String?, threadId: String?, parentId: String?): Boolean {
        return false
    }

    /**
     * Long press on thread region
     * @param v
     * @param messageId
     * @param threadId
     * @param parentId
     * @return
     */
    fun onThreadLongClick(
        v: View?,
        messageId: String?,
        threadId: String?,
        parentId: String?
    ): Boolean {
        return false
    }
}