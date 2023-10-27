package com.ben.bencustomerserver.listener

import android.view.MotionEvent
import android.view.View

interface OnChatRecordTouchListener {
    /**
     * 语音按压事件
     * @param v
     * @param event
     * @return
     */
    fun onRecordTouch(v: View?, event: MotionEvent?): Boolean
}