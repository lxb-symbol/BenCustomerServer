package com.ben.bencustomerserver.viewholder

import android.view.View
import android.view.ViewGroup
import com.ben.bencustomerserver.listener.MessageListItemClickListener
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.views.chatrow.BenChatRowImage

class BenImageViewHolder(itemView: View, itemClickListener: MessageListItemClickListener?) :
    BenChatRowViewHolder(itemView, itemClickListener!!) {
    override fun onBubbleClick(message: BaseMessageModel?) {
        super.onBubbleClick(message)
        // 1. 下载封面更新界面         chatRow.updateView()
        // 2. 跳转预览界面

//        val intent = Intent(context, BenShowBigImageActivity::class.java)
//        val imgUri: Uri = imgBody.getLocalUri()
//        //检查Uri读权限
//        BenFileUtils.takePersistableUriPermission(context, imgUri)
//
//        if (BenFileUtils.isFileExistByUri(context, imgUri)) {
//            intent.putExtra("uri", imgUri)
//        } else {
//            // The local full size pic does not exist yet.
//            // ShowBigImage needs to download it from the server
//            // first
//            val msgId: String = message?.msgId!!
//            intent.putExtra("messageId", msgId)
//            intent.putExtra("filename", imgBody.getFileName())
//        }
//
//        context.startActivity(intent)
    }

    override fun handleReceiveMessage(message: BaseMessageModel?) {
        super.handleReceiveMessage(message)
        chatRow?.updateView(message!!)
    }

    companion object {
        fun create(
            parent: ViewGroup,
            isSender: Boolean, itemClickListener: MessageListItemClickListener?
        ): BenChatRowViewHolder {
            return BenImageViewHolder(
                BenChatRowImage(parent.context, isSender),
                itemClickListener
            )
        }
    }
}