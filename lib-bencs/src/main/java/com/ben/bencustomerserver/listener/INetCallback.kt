package com.ben.bencustomerserver.listener

interface INetCallback<T> {
    fun  onSuccess(data: T)

    fun onError(code: Int, msg: String)
}