package com.ben.bencustomerserver.viewholder

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import com.ben.bencustomerserver.listener.MessageListItemClickListener
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.views.chatrow.EaseChatRowVideo

class EaseVideoViewHolder(itemView: View, itemClickListener: MessageListItemClickListener?) :
    EaseChatRowViewHolder(itemView, itemClickListener!!) {
    override fun onBubbleClick(message: BaseMessageModel?) {
        super.onBubbleClick(message)

        // 1. 下载视频封面
        // 2. 预览视频

//        val videoBody: EMVideoMessageBody = message.getBody() as EMVideoMessageBody
//        if (EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {
//        } else {
//            if (videoBody.thumbnailDownloadStatus() === EMFileMessageBody.EMDownloadStatus.DOWNLOADING || videoBody.thumbnailDownloadStatus() === EMFileMessageBody.EMDownloadStatus.PENDING || videoBody.thumbnailDownloadStatus() === EMFileMessageBody.EMDownloadStatus.FAILED) {
//                // retry download with click event of user
//                EMClient.getInstance().chatManager().downloadThumbnail(message)
//                return
//            }
//        }
//        val intent = Intent(context, EaseShowVideoActivity::class.java)
//        intent.putExtra("msg", message)
//
//        context.startActivity(intent)
    }

    companion object {
        private val TAG = EaseVideoViewHolder::class.java.simpleName
        fun create(
            parent: ViewGroup,
            isSender: Boolean, itemClickListener: MessageListItemClickListener?
        ): EaseChatRowViewHolder {
            return EaseVideoViewHolder(
                EaseChatRowVideo(parent.context, isSender),
                itemClickListener
            )
        }
    }
}