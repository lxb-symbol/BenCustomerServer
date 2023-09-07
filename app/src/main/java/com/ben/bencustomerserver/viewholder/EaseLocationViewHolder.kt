package com.ben.bencustomerserver.viewholder

import android.view.View
import android.view.ViewGroup
import com.hyphenate.chat.EMClient

class EaseLocationViewHolder(itemView: View, itemClickListener: MessageListItemClickListener?) :
    EaseChatRowViewHolder(itemView, itemClickListener) {
    override fun onBubbleClick(message: BaseMessageModel) {
        super.onBubbleClick(message)
        val locBody: EMLocationMessageBody = message.getBody() as EMLocationMessageBody
        EaseBaiduMapActivity.actionStart(
            context,
            locBody.getLatitude(),
            locBody.getLongitude(),
            locBody.getAddress()
        )
    }

    override fun handleReceiveMessage(message: BaseMessageModel) {
        super.handleReceiveMessage(message)
        if (!EaseIM.getInstance().getConfigsManager().enableSendChannelAck()) {
            //此处不再单独发送read_ack消息，改为进入聊天页面发送channel_ack
            //新消息在聊天页面的onReceiveMessage方法中，排除视频，语音和文件消息外，发送read_ack消息
            if (!message.isAcked() && message.getChatType() === BaseMessageModel.ChatType.Chat) {
                try {
                    EMClient.getInstance().chatManager()
                        .ackMessageRead(message.getFrom(), message.getMsgId())
                } catch (e: HyphenateException) {
                    e.printStackTrace()
                }
            }
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            isSender: Boolean, itemClickListener: MessageListItemClickListener?
        ): EaseChatRowViewHolder {
            return EaseLocationViewHolder(
                EaseChatRowLocation(parent.context, isSender),
                itemClickListener
            )
        }
    }
}