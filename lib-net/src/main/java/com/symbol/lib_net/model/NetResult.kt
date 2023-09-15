package com.symbol.lib_net.model

import java.lang.Exception

sealed class NetResult<out T : Any> {

    data class Success<out T : Any>(val data: T) : NetResult<T>()
    data class Error(val exception: Exception) : NetResult<Nothing>()
}
