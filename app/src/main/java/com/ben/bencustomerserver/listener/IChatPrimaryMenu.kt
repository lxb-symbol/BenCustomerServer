package com.ben.bencustomerserver.listener

import android.graphics.drawable.Drawable
import android.widget.EditText
import com.ben.bencustomerserver.views.BenInputMenuStyle

interface IChatPrimaryMenu {
    /**
     * 菜单展示类型
     * @param style
     */
    fun setMenuShowType(style: BenInputMenuStyle?)

    /**
     * 常规模式
     */
    fun showNormalStatus()

    /**
     * 文本输入模式
     */
    fun showTextStatus()

    /**
     * 语音输入模式
     */
    fun showVoiceStatus()

    /**
     * 表情输入模式
     */
    fun showEmojiconStatus()

    /**
     * 更多模式
     */
    fun showMoreStatus()

    /**
     * 隐藏扩展区模式
     */
    fun hideExtendStatus()

    /**
     * 隐藏软键盘
     */
    fun hideSoftKeyboard()

    /**
     * 输入表情
     * @param emojiContent
     */
    fun onEmojiconInputEvent(emojiContent: CharSequence?)

    /**
     * 删除表情
     */
    fun onEmojiconDeleteEvent()

    /**
     * 输入文本
     * @param text
     */
    fun onTextInsert(text: CharSequence?)

    /**
     * 获取EditText
     * @return
     */
    val editText: EditText?

    /**
     * 设置输入框背景
     * @param bg
     */
    fun setMenuBackground(bg: Drawable?)

    /**
     * 设置发送按钮背景
     * @param bg
     */
    fun setSendButtonBackground(bg: Drawable?)

    /**
     * 设置监听
     * @param listener
     */
    fun setBenChatPrimaryMenuListener(listener: BenChatPrimaryMenuListener?)
}