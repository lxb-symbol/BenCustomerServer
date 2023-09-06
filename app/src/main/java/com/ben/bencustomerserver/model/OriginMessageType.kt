package com.ben.bencustomerserver.model

/**
 * byte 的消息的类型
 */
object OriginMessageType {

    const val TYPE_CHAT_MESSAGE = "chatMessage"

    const val TYPE_CUSTOMER_IN = "customerIn"

    const val TYPE_AFTER_SEND = "afterSend"

    const val TYPE_DIRECT_LINK_SERVER ="directLinkKF"

    const val TYPE_MESSAGE_READ="readMessage"
}