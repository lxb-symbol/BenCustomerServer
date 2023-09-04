package com.ben.bencustomerserver.connnect

import android.app.Service
import android.content.Intent
import android.os.IBinder


class WebSocketService:Service() {
    override fun onCreate() {
        super.onCreate()
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (wsURL.isNotEmpty()){
            WsManager.openWs()
        }
        return super.onStartCommand(intent, flags, startId)

    }
}