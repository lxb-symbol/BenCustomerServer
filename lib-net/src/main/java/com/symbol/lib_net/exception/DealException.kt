package com.symbol.lib_net.exception

import android.net.ParseException
import com.google.gson.JsonParseException

import org.json.JSONException
import retrofit2.HttpException

import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.UnknownServiceException
import javax.net.ssl.SSLHandshakeException



object DealException {

    fun handlerException(t: Throwable): ResultException {
        val ex: ResultException
        when (t) {
            is ResultException -> {
                ex = t
            }
            is HttpException -> {
                ex = when (t.code()) {
                    ApiResultCode.UNAUTHORIZED,
                    ApiResultCode.FORBIDDEN,
                        //权限错误，需要实现
                    ApiResultCode.NOT_FOUND -> ResultException(
                        t.code().toString(),
                        "网络错误"
                    )
                    ApiResultCode.REQUEST_TIMEOUT,
                    ApiResultCode.GATEWAY_TIMEOUT -> ResultException(
                        t.code().toString(),
                        "网络连接超时"
                    )
                    ApiResultCode.INTERNAL_SERVER_ERROR,
                    ApiResultCode.BAD_GATEWAY,
                    ApiResultCode.SERVICE_UNAVAILABLE -> ResultException(
                        t.code().toString(),
                        "服务器错误"
                    )
                    else -> ResultException(t.code().toString(), "网络错误")
                }
            }
            is JsonParseException, is JSONException, is ParseException -> {
                ex = ResultException(
                    ApiResultCode.PARSE_ERROR,
                    "解析错误"
                )
            }

            is SocketException -> {
                ex = ResultException(
                    ApiResultCode.REQUEST_TIMEOUT.toString(),
                    "网络连接错误，请重试"
                )
            }

            is SocketTimeoutException -> {
                ex = ResultException(
                    ApiResultCode.REQUEST_TIMEOUT.toString(),
                    "网络连接超时"
                )
            }

            is SSLHandshakeException -> {
                ex = ResultException(
                    ApiResultCode.SSL_ERROR,
                    "证书验证失败"
                )
                return ex
            }

            is UnknownHostException -> {
                ex = ResultException(
                    ApiResultCode.UNKNOW_HOST,
                    "网络错误，请切换网络重试"
                )
                return ex
            }

            is UnknownServiceException -> {
                ex = ResultException(
                    ApiResultCode.UNKNOW_HOST,
                    "网络错误，请切换网络重试"
                )
            }

            is NumberFormatException -> {
                ex = ResultException(
                    ApiResultCode.UNKNOW_HOST,
                    "数字格式化异常"
                )
            }

            else -> {
                ex = ResultException(
                    ApiResultCode.UNKNOWN,
                    "未知错误"
                )
            }
        }
        return ex
    }
}