package com.ben.bencustomerserver.views.chatrow

import android.content.Context
import android.view.View
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.views.EaseEmojicon
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

/**
 * big emoji icons
 *
 */
class EaseChatRowBigExpression : EaseChatRowText {
    private var imageView: ImageView? = null

    constructor(context: Context?, isSender: Boolean) : super(context, isSender)
    constructor(
        context: Context?,
        message: BaseMessageModel?,
        position: Int,
        adapter: BaseAdapter?
    ) : super(context, message, position, adapter)

    override fun onInflateView() {
        inflater.inflate(
            if (!isSender) R.layout.ease_row_received_bigexpression else R.layout.ease_row_sent_bigexpression,
            this
        )
    }

    override fun onFindViewById() {
        percentageView = findViewById<View>(R.id.percentage) as TextView
        imageView = findViewById<View>(R.id.image) as ImageView
    }

    override fun onSetUpView() {
        // symbol 无大表情消息
        val messageInner  =message?.innerMessage

    }
}