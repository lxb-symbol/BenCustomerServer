package com.ben.bencustomerserver.connnect

interface WebSocketMessageListener {

    fun onReceiveMessage(msg: String)


}