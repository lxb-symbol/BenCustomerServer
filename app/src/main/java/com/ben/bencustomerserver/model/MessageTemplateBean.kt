package com.ben.bencustomerserver.model


data class MessageTemplateBean(
    val content: String,
    val from_avatar: String,
    val from_id: String,
    val from_name: String,
    val seller_code: String,
    val to_id: String,
    val to_name: String
):SocketBean

