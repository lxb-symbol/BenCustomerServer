package com.ben.bencustomerserver.viewholder

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.ben.bencustomerserver.adapter.EaseBaseRecyclerViewAdapter
import com.ben.bencustomerserver.listener.MessageListItemClickListener
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.Direct
import com.ben.bencustomerserver.views.chatrow.EaseChatRow
import com.ben.bencustomerserver.views.chatrow.EaseChatRow.EaseChatRowActionCallback

class EaseChatRowViewHolder(itemView: View, itemClickListener: MessageListItemClickListener) :
    EaseBaseRecyclerViewAdapter.ViewHolder<BaseMessageModel?>(itemView), EaseChatRowActionCallback {
    val context: Context
    var chatRow: EaseChatRow? = null
        private set
    private var message: BaseMessageModel? = null
    private val mItemClickListener: MessageListItemClickListener

    init {
        // 解决view宽和高不显示的问题
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        itemView.layoutParams = params
        context = itemView.context
        mItemClickListener = itemClickListener
    }

    override fun initView(itemView: View?) {
        chatRow = itemView as EaseChatRow?
    }



    override fun setData(item: BaseMessageModel?, position: Int) {
        chatRow!!.resetViewState()
        message = item
        chatRow!!.setUpView(item, position, mItemClickListener, this)
        handleMessage()
    }

    override fun setDataList(data: List<BaseMessageModel?>?, position: Int) {
        super.setDataList(data, position)
        chatRow!!.setTimestamp(if (position == 0) null else data!![position - 1])
    }

    override fun onResendClick(message: BaseMessageModel) {}
    override fun onBubbleClick(message: BaseMessageModel) {}
    override fun onDetachedFromWindow() {}
    private fun handleMessage() {
        if (message!!.direct === Direct.SEND) {
            handleSendMessage(message)
        } else if (message!!.direct === Direct.RECEIEVE) {
            handleReceiveMessage(message)
        }
    }

    /**
     * send message
     * @param message
     */
    protected fun handleSendMessage(message: BaseMessageModel?) {
        // Update the view according to the message current status.
        chatRow!!.updateView(message)
    }

    /**
     * receive message
     * @param message
     */
    protected fun handleReceiveMessage(message: BaseMessageModel?) {}

    companion object {
        private val TAG = EaseChatRowViewHolder::class.java.simpleName
    }
}