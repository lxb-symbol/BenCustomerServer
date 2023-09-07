package com.ben.bencustomerserver.viewholder

import android.view.View
import android.view.ViewGroup
import com.hyphenate.chat.EMClient

class EaseTextViewHolder(itemView: View, itemClickListener: MessageListItemClickListener?) :
    EaseChatRowViewHolder(itemView, itemClickListener) {
    override fun onBubbleClick(message: BaseMessageModel) {
        super.onBubbleClick(message)
        if (!EaseDingMessageHelper.get()
                .isDingMessage(message) || message.getChatType() !== BaseMessageModel.ChatType.GroupChat || message.direct() !== BaseMessageModel.Direct.SEND
        ) {
            return
        }

        // If this msg is a ding-type msg, click to show a list who has already read this message.
        val i = Intent(context, EaseDingAckUserListActivity::class.java)
        i.putExtra("msg", message)
        context.startActivity(i)
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
                return
            }
        }

        // Send the group-ack cmd type msg if this msg is a ding-type msg.
        EaseDingMessageHelper.get().sendAckMessage(message)
    }

    companion object {
        fun create(
            parent: ViewGroup,
            isSender: Boolean, itemClickListener: MessageListItemClickListener?
        ): EaseChatRowViewHolder {
            return EaseTextViewHolder(EaseChatRowText(parent.context, isSender), itemClickListener)
        }
    }
}