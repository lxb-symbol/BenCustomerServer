package com.ben.bencustomerserver.connnect

import com.ben.bencustomerserver.model.BaseMessageModel

interface HttpMessageListener {
    fun receiveBoltMessage(model: BaseMessageModel)

     fun receiveHistoryMessageFromNet(model: BaseMessageModel){}
}