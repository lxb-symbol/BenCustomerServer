package com.symbol.lib_net

import com.symbol.lib_net.exception.DealException
import com.symbol.lib_net.exception.ResultException
import com.symbol.lib_net.model.BaseModel
import com.symbol.lib_net.model.NetResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import java.lang.Exception

// TODO 待采用 CoroutineExceptionHandler 来处理
open class BaseRepository {

    suspend fun <T : Any> callRequest(
        call: suspend () -> NetResult<T>
    ): NetResult<T> {
        return try {
            call()
        } catch (e: Exception) {
            e.printStackTrace()
            NetResult.Error(DealException.handlerException(e))
        }
    }


    suspend fun <T : Any> handleResponse(
        response: BaseModel<T>,
        successBlock: (suspend CoroutineScope.() -> Unit)? = null,
        errorBlock: (suspend CoroutineScope.() -> Unit)? = null
    ): NetResult<T> {
        return coroutineScope {
            if (response.code == -1) {
                errorBlock?.let { it() }
                NetResult.Error(ResultException(response.code.toString(), response.msg))
            } else {
                successBlock?.let { it() }
                NetResult.Success(response.data)
            }
        }
    }

    suspend fun <T:Any> handleResponse(
        response: T,
        successBlock: (suspend CoroutineScope.() -> Unit)?=null,
        errorBlock: (suspend CoroutineScope.() -> Unit)?=null
    ):NetResult<T>{
        return coroutineScope {
            if (response is Unit){
                errorBlock?.let { it() }
                NetResult.Error(ResultException("-1","解析出错"))
            }else{
                successBlock?.let { it() }
                NetResult.Success(response)
            }
        }
    }


}