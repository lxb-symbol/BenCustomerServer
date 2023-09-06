package com.ben.bencustomerserver.model

import java.io.Serializable

data class TokenAndWsEntity(
    val token: String,
    val socket_url: String
) : Serializable
