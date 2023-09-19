package com.ben.bencustomerserver.connnect

import com.ben.bencustomerserver.model.BaseMessageModel


/**
 * 这个是处理过的消息
 */
interface WebSocketMessageListener {
    fun onReceiveMessage(model: BaseMessageModel)

}