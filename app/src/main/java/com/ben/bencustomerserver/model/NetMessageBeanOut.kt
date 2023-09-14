package com.ben.bencustomerserver.model

import java.io.Serializable

data class NetMessageBeanOut(
    val code: Int,
    val data: List<NetMessageBean>,
    val msg: Int,
    val total: Int
) : Serializable

data class NetMessageBean(
    val content: String,
    val create_time: String,
    val from_avatar: String,
    val from_id: String,
    val from_name: String,
    val log_id: Int,
    val read_flag: Int,
    val seller_code: String,
    val to_id: String,
    val to_name: String,
    val type: String,
    val valid: Int
) : Serializable