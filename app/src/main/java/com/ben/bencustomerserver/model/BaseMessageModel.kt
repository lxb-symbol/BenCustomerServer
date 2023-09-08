package com.ben.bencustomerserver.model

import com.ben.bencustomerserver.listener.EMCallBack
import java.io.Serializable


/**
 * 基础消息体,也算是处理过的消息体
 */
data class BaseMessageModel(
    var id: Long = 0,
    var messageType: MessageType,
    var cmd: String = "",
    var direct: Direct = Direct.SEND,
    var msgId: String = "0",
    var status: MessageStatus = MessageStatus.CREATE,
    var messageStatusCallback: EMCallBack? = null,
    var delivered: Boolean = false,
    var acked: Boolean = false,
    var from: String,
    var to: String,
    var msgTime: Long = System.currentTimeMillis(),
    var progress: Int = 0,
    var isBolt: Boolean = true
) : Serializable


