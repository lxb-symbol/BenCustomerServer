package com.ben.bencustomerserver.model

import java.io.Serializable


/**
 * {
 *     "time": 1694138155,
 *     "token": "7b599763899f9f3622db77fd7443d445",
 *     "socket_url": "wss://kf.saizhuge.com:2021/5c6cbcb7d55ca-1694138155-7b599763899f9f3622db77fd7443d445"
 *   }
 */
data class TokenAndWsEntity(
    val time:String,
    val token: String,
    val socket_url: String,
    val seller_id:String
) : Serializable
