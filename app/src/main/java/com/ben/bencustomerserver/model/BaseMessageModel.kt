package com.ben.bencustomerserver.model

import com.ben.bencustomerserver.listener.EMCallBack
import java.io.Serializable


/**
 * 基础消息体,也算是处理过的消息体
 *
 *  {
 *     from_name: 用户名称,
 *     from_avatar: 用户头像,
 *     from_id: 用户uid,
 *     to_id: 客服code,
 *     to_name: 客服名称,
 *     content: 发送消息内容,
 *     seller_code: 商家code
 * }
 *
 *
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
    var from_name: String = "",
    var from_id: String = "",
    var from_avatar: String = "",
    var to_id: String = "",
    var to_name: String = "",
    var to_avatar: String = "",
    var content: String = "",
    var seller_code: String = "",
    var msgTime: Long = System.currentTimeMillis(),
    var progress: Int = 0,
    var isBolt: Boolean = true,
    var extString: String = ""// 额外的信息
) : Serializable


