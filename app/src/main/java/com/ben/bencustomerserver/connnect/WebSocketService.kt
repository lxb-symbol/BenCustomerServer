package com.ben.bencustomerserver.connnect

import android.app.Service
import android.content.Intent
import android.os.IBinder


/**
 * socket 服务链接
 */
class WebSocketService : Service() {


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (wsURL.isNotEmpty()) {
            WsManager.openWs()
        }
        return super.onStartCommand(intent, flags, startId)

    }
}