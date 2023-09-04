package com.ben.bencustomerserver.connnect

import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import com.nofish.websocket.NetworkStatusMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.TimeUnit
import kotlin.math.pow


var wsURL: String = ""

object WsManager : CoroutineScope by MainScope() {


    val TAG = "WsManager"
    private val wsHttpClient by lazy {
        OkHttpClient.Builder()
            .pingInterval(10, TimeUnit.SECONDS) // 设置 PING 帧发送间隔
            .build()
    }
    private val requestHttp by lazy {
        Request.Builder()
            .url(wsURL)
            .build()
    }
    var mWebSocket: WebSocket? = null
        private set


    private var reconnectJob: Job? = null
    private var connectionStatus = ConnectionStatus.DISCONNECTED // 初始状态为断开连接
    private const val mMaxRetryCount = 10 // 最大重试次数
    private val mNetWorkCallback by lazy {
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // 联网后开始重连
                Log.e(TAG, "网络恢复")
                reconnect()
            }

            override fun onLost(network: Network) {
                // 断网停止重连
                if (!NetworkStatusMonitor.isNetworkConnected()) {
                    Log.e(TAG, "网络断开")
                    cancelReconnect()
                }
            }
        }
    }

    private fun cancelReconnect() {
        Log.e(TAG, "取消重连")
        connectionStatus = ConnectionStatus.DISCONNECTED
        reconnectJob?.cancel()
    }

    private fun reconnect() {
        if (reconnectJob?.isActive == true) {
            // 避免重复执行重连逻辑
            return
        }
        connectionStatus = ConnectionStatus.RECONNECTING
        reconnectJob = launch(Dispatchers.IO) {
            var retryCount = 0
            while (retryCount <= mMaxRetryCount) {
                if (retryCount == mMaxRetryCount) {
                    Log.e(TAG, "超过最大重试次数，停止重连")
                    break
                }
                if (connectionStatus != ConnectionStatus.CONNECTED) {
                    // 进行重连
                    connect()
                    Log.e(TAG, "尝试重连")
                    retryCount++
                } else {
                    Log.e(TAG, "重连成功")
                    // 连接成功，退出重连循环
                    break
                }
                delay(exponentialBackoffRetry(retryCount))
            }
        }
    }

    private fun exponentialBackoffRetry(retryCount: Int): Long {
        val maxRetryDelay = 10000L // 最大重试延迟时间（毫秒）
        val baseDelay = 200L // 基础延迟时间（毫秒）
        val multiplier = 1.2 // 延迟时间乘数
        val delay = baseDelay * multiplier.pow(retryCount.toDouble()).toLong()
        Log.e(TAG, "重连间隔变为 $delay")
        return minOf(delay, maxRetryDelay)
    }

    fun openWs() {
        if (connectionStatus == ConnectionStatus.RECONNECTING || connectionStatus == ConnectionStatus.CONNECTED) {
            return
        }
        Log.e(TAG, "openWs")
        connect()
        NetworkStatusMonitor.register(mNetWorkCallback)
    }

    private fun connect() {
        Log.e(TAG, "开始连接 WS")
        mWebSocket = wsHttpClient.newWebSocket(requestHttp, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                Log.e(TAG, "WS connection successful")
                connectionStatus = ConnectionStatus.CONNECTED
                // WebSocket 连接建立
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                Log.e(TAG, "openWs onMessage $text")

                // 收到服务端发送来的 String 类型消息
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
                // 收到服务端发送来的 ByteString 类型消息
                Log.e(TAG, "openWs onMessage bytes $bytes")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                Log.e(TAG, "openWs onClosing")
                mWebSocket = null
                // 收到服务端发来的 CLOSE 帧消息，准备关闭连接
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                Log.e(TAG, "openWs onClosed")
                mWebSocket = null
                // WebSocket 连接关闭
                /**
                 *  Defined Status Codes

                Endpoints MAY use the following pre-defined status codes when sending
                a Close frame.

                1000

                1000 indicates a normal closure, meaning that the purpose for
                which the connection was established has been fulfilled.

                1001

                1001 indicates that an endpoint is "going away", such as a server
                going down or a browser having navigated away from a page.

                1002

                1002 indicates that an endpoint is terminating the connection due
                to a protocol error.

                1003

                1003 indicates that an endpoint is terminating the connection
                because it has received a type of data it cannot accept (e.g., an
                endpoint that understands only text data MAY send this if it
                receives a binary message).
                 */

                // Not normally close, need to reconnect
                if (code != 1000) {
                    reconnect()
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                Log.e(TAG, "Ws连接失败")
                mWebSocket = null
                // 出错了
                reconnect()
            }
        })
    }


}

enum class ConnectionStatus {
    CONNECTED,
    DISCONNECTED,
    RECONNECTING
}
