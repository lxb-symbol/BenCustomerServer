package com.ben.bencustomerserver.model

data class WsMessageSendOut(
    val cmd: String = OriginMessageType.TYPE_CHAT_MESSAGE,
    var data: MessageTemplateBean?
)