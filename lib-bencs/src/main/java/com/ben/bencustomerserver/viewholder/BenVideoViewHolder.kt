package com.ben.bencustomerserver.viewholder

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import com.ben.bencustomerserver.listener.MessageListItemClickListener
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.VideoMessage
import com.ben.bencustomerserver.ui.BenShowVideoActivity
import com.ben.bencustomerserver.views.chatrow.BenChatRowVideo

class BenVideoViewHolder(itemView: View, itemClickListener: MessageListItemClickListener?) :
    BenChatRowViewHolder(itemView, itemClickListener!!) {
    override fun onBubbleClick(message: BaseMessageModel?) {
        super.onBubbleClick(message)

        // 1. 下载视频封面
        // 2. 预览视频
        val videoBody: VideoMessage = message?.innerMessage as VideoMessage
        val intent = Intent(itemView.context, BenShowVideoActivity::class.java)
        intent.putExtra("path", videoBody.localPath ?: videoBody.netPath)
        itemView.context.startActivity(intent)
    }

    companion object {
        private val TAG = BenVideoViewHolder::class.java.simpleName
        fun create(
            parent: ViewGroup,
            isSender: Boolean, itemClickListener: MessageListItemClickListener?
        ): BenChatRowViewHolder {
            return BenVideoViewHolder(
                BenChatRowVideo(parent.context, isSender),
                itemClickListener
            )
        }
    }
}