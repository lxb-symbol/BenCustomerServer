package com.symbol.lib_net.model

data class BaseModel<out T>(val code: Int, val msg: String, val data: T)
