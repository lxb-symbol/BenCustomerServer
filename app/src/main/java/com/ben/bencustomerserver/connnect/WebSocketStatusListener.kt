package com.ben.bencustomerserver.connnect

interface WebSocketStatusListener {
     fun onConnecting()

     fun onConnected()

     fun onDisConnected()

     fun onReConnected()
}