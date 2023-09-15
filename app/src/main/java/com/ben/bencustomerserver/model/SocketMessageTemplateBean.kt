package com.ben.bencustomerserver.model

import java.io.Serializable

/**
 * 适用于 聊天消息
 */
class SocketMessageTemplateBean(
    cmd: String,
    data: MessageTemplateBean
) : Serializable