package com.ben.bencustomerserver.connnect


/**
 * 这个是处理过的消息
 */
interface WebSocketMessageListener {

    fun onReceiveMessage(msg: String)


}