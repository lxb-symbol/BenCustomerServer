package com.ben.bencustomerserver.model

/**
 * byte 的消息的类型
 */
object OriginMessageType {

    const val TYPE_CHAT_MESSAGE = "chatMessage"

    const val TYPE_CUSTOMER_IN = "customerIn"

    const val TYPE_AFTER_SEND = "afterSend"

    const val TYPE_DIRECT_LINK_SERVER = "directLinkKF"

    const val TYPE_MESSAGE_READ = "readMessage"

    const val TYPE_USER_INIT = "userInit"

    const val TYPE_HELLO = "hellow"

    const val TYPE_IS_CLOSE = "isClose"

    const val TYPE_QUESTION = "comQuestion"

    const val TAG_FACE = "face["
    const val TAG_VIDEO = "video("
    const val TAG_FILE = "file("
    const val TAG_VOICE = "audio["
    const val TAG_LOCATION="location["
    const val TAG_IMG="img["


}