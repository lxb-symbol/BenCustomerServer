package com.ben.bencustomerserver.model

import com.ben.bencustomerserver.listener.EMCallBack
import java.io.Serializable


/**
 * 基础消息体,也算是处理过的消息体
 */
class BaseMessageModel(
    var id: Long,
    var messageType: MessageType,
    var cmd: String,
    var direct: Direct,
    var msgId: String,
    var status: MessageStatus,
    var messageStatusCallback: EMCallBack,
    var delivered: Boolean = false,
    var acked: Boolean = false,
    var from: String,
    var to: String,
    var msgTime: Long,
    var progress:Int


) : Serializable


