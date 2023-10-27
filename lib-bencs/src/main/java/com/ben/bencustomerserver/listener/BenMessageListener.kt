
package com.ben.bencustomerserver.listener

import com.ben.bencustomerserver.model.BaseMessageModel

/**
 * \~chinese
 * 消息事件监听器。
 * 用于监听消息接收情况，消息成功发送到对方手机后会有回执（需开启送达回执，
 * 对方阅读了这条消息也会收到回执（需开启允许已读回执，详见
 * 发送消息过程中，消息 ID 会从最初本地生成的 uuid 变更为服务器端生成的全局唯一 ID，该 ID 在使用 SDK 的所有设备上均唯一。
 * 应用需实现此接口监听消息变更状态。
 *
 */
interface BaseMessageModelListener {
    /**
     * \~chinese
     * 收到消息。
     * 在收到文本、图片、视频、语音、地理位置和文件等消息时，通过此回调通知用户。
     *
     * \~english
     * Occurs when a message is received.
     * This callback is triggered to notify the user when a message such as texts or an image, video, voice, location, or file is received.
     */
    fun onMessageReceived(messages: List<BaseMessageModel?>?)

    /**
     * \~chinese
     * 收到命令消息。
     * 与 [.onMessageReceived] 不同, 这个回调只包含命令的消息，命令消息通常不对用户展示。
     *
     * \~english
     * Occurs when a command message is received.
     * Unlike [BaseMessageModelListener.onMessageReceived], this callback only contains a command message body that is usually invisible to users.
     *
     */
    fun onCmdMessageReceived(messages: List<BaseMessageModel?>?) {}

    /**
     * \~chinese
     * 收到消息的已读回执。
     *
     * \~english
     * Occurs when a read receipt is received for a message.
     */
    fun onMessageRead(messages: List<BaseMessageModel?>?) {}

    /**
     * \~chinese
     * 收到消息的送达回执。
     *
     * \~english
     * Occurs when a delivery receipt is received.
     */
    fun onMessageDelivered(messages: List<BaseMessageModel?>?) {}

    /**
     * \~chinese
     * 撤回收到的消息。
     *
     * \~english
     * Occurs when a received message is recalled.
     */
    fun onMessageRecalled(messages: List<BaseMessageModel?>?) {}

    /**
     * \~chinese
     * 收到 Reaction 变更消息。
     * 在收到 Reaction 变更事件时，通过此回调通知用户。
     *
     * \~english
     * Occurs when a message Reaction changed.
     * The SDK triggers the onReactionChanged callback, notifying that the message Reaction changed.
     */
    fun onReactionChanged(messageReactionChangeList: List<String?>?) {}
}