package com.ben.bencustomerserver.model

data class OutSocketBean(
    var cmd:String =OriginMessageType.TYPE_CHAT_MESSAGE,
    var data:SocketBean?
)
