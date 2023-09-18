package com.ben.bencustomerserver.views.chatrow

import android.content.Context
import android.view.View
import android.widget.TextView
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.model.BaseMessageModel

/**
 * location row
 */
class EaseChatRowLocation : EaseChatRow {
    private var locationView: TextView? = null
    private var tvLocationName: TextView? = null
//    private var locBody: EMLocationMessageBody? = null

    constructor(context: Context?, isSender: Boolean) : super(context!!, isSender)
    constructor(
        context: Context?,
        message: BaseMessageModel?,
        position: Int,
        adapter: Any?
    ) : super(
        context!!, message, position, adapter
    )

    override fun onInflateView() {
        inflater.inflate(
            if (!isSender) R.layout.ease_row_received_location else R.layout.ease_row_sent_location,
            this
        )
    }

    override fun onFindViewById() {
        locationView = findViewById<View>(R.id.tv_location) as TextView
        tvLocationName = findViewById<TextView>(R.id.tv_location_name)
    }

    override fun onSetUpView() {
//        locBody = message.getBody() as EMLocationMessageBody?
//        locationView.setText(locBody.getAddress())
    }

    override fun onMessageCreate() {
        if (progressBar != null) {
            progressBar!!.visibility = VISIBLE
        }
        if (statusView != null) {
            statusView!!.visibility = GONE
        }
    }

    override fun onMessageSuccess() {
        if (progressBar != null) {
            progressBar!!.visibility = GONE
        }
        if (statusView != null) {
            statusView!!.visibility = GONE
        }
    }

    override fun onMessageError() {
        super.onMessageError()
        if (progressBar != null) {
            progressBar!!.visibility = GONE
        }
        if (statusView != null) {
            statusView!!.visibility = VISIBLE
        }
    }

    override fun onMessageInProgress() {
        if (progressBar != null) {
            progressBar!!.visibility = VISIBLE
        }
        if (statusView != null) {
            statusView!!.visibility = GONE
        }
    }
}