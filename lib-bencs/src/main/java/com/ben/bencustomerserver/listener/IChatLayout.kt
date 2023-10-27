package com.ben.bencustomerserver.listener

import android.net.Uri
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.views.BenChatInputMenu

interface IChatLayout {
    /**
     * 获取输入菜单
     * @return
     */
    fun  chatInputMenu(): BenChatInputMenu?

    /**
     * 获取输入框内容
     * @return
     */
    fun inputContent(): String?

    /**
     * 是否打开正在输入监控
     * @param turnOn
     */
    fun turnOnTypingMonitor(turnOn: Boolean)

    /**
     * 发送文本消息
     * @param content
     */
    fun sendTextMessage(content: String?)

    /**
     * 发送文本消息
     * @param content
     * @param isNeedGroupAck 需要需要群回执
     */
    fun sendTextMessage(content: String?, isNeedGroupAck: Boolean)

    /**
     * 发送@消息
     * @param content
     */
    fun sendAtMessage(content: String?)

    /**
     * 发送大表情消息
     * @param name
     * @param identityCode
     */
    fun sendBigExpressionMessage(name: String?, identityCode: String?)

    /**
     * 发送语音消息
     * @param filePath
     * @param length
     */
    fun sendVoiceMessage(filePath: String?, length: Int)

    /**
     * 发送语音消息
     * @param filePath
     * @param length
     */
    fun sendVoiceMessage(filePath: Uri?, length: Int)

    /**
     * 发送图片消息
     * @param imageUri
     */
    fun sendImageMessage(imageUri: Uri?)

    /**
     * 发送图片消息
     * @param imageUri
     * @param sendOriginalImage
     */
    fun sendImageMessage(imageUri: Uri?, sendOriginalImage: Boolean)

    /**
     * 发送定位消息
     * @param latitude
     * @param longitude
     * @param locationAddress
     */
    fun sendLocationMessage(
        latitude: Double,
        longitude: Double,
        locationAddress: String?,
        buildingName: String?
    )

    /**
     * 发送视频消息
     * @param videoUri
     * @param videoLength
     */
    fun sendVideoMessage(videoUri: Uri?, videoLength: Int)

    /**
     * 发送文件消息
     * @param fileUri
     */
    fun sendFileMessage(fileUri: Uri?)

    /**
     * 为消息添加扩展字段
     * @param message
     */
    fun addMessageAttributes(message: BaseMessageModel?)

    /**
     * 发送消息
     * @param message
     */
    fun sendMessage(message: BaseMessageModel?)

    /**
     * 重新发送消息
     * @param message
     */
    fun resendMessage(message: BaseMessageModel?)

    /**
     * 删除消息
     * @param message
     */
    fun deleteMessage(message: BaseMessageModel?)

    /**
     * 撤回消息
     * @param message
     */
    fun recallMessage(message: BaseMessageModel?)

    /**
     * 翻译消息
     * @param message
     * @param isTranslate
     */
    fun translateMessage(message: BaseMessageModel?, isTranslate: Boolean)

    /**
     * 隐藏翻译
     * @param message
     */
    fun hideTranslate(message: BaseMessageModel?)

    /**
     * 用于监听消息的变化
     * @param listener
     */
    fun setOnChatLayoutListener(listener: OnChatLayoutListener?)

    /**
     * 用于监听发送语音的触摸事件
     * @param voiceTouchListener
     */
    fun setOnChatRecordTouchListener(voiceTouchListener: OnChatRecordTouchListener?)

    /**
     * 消息撤回监听
     * @param listener
     */
    fun setOnRecallMessageResultListener(listener: OnRecallMessageResultListener?)

    /**
     * 设置发送消息前设置属性事件
     * @param sendMsgEvent
     */
    fun setOnAddMsgAttrsBeforeSendEvent(sendMsgEvent: OnAddMsgAttrsBeforeSendEvent?)
}