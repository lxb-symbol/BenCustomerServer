package com.symbol.lib_net.interceptor

import android.util.Log
import com.tencent.mmkv.MMKV
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class CommonInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
        val builder = addHeaders(request.newBuilder())
        val response = chain.proceed(builder)

        return response

    }

    private fun addHeaders(builder: Request.Builder): Request {
        val token: String? = MMKV.defaultMMKV().getString("token", "")
        val time: String? = MMKV.defaultMMKV().getString("time", "")

        Log.i("symbol-6","token: ${token}")
        Log.i("symbol-6","time: ${time}")
        return builder.addHeader("charset", "UTF-8")
            .addHeader("token", token ?: "")
            .addHeader("time", time ?: "")
            .build()
    }
}