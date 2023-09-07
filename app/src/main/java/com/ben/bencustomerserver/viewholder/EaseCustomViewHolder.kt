package com.ben.bencustomerserver.viewholder

import android.view.View
import com.ben.bencustomerserver.listener.MessageListItemClickListener

class EaseCustomViewHolder(itemView: View, itemClickListener: MessageListItemClickListener?) :
    EaseChatRowViewHolder(itemView, itemClickListener) {
    override fun handleReceiveMessage(message: BaseMessageModel) {
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
                return
            }
        }

        // Send the group-ack cmd type msg if this msg is a ding-type msg.
        EaseDingMessageHelper.get().sendAckMessage(message)
    }
}