package com.ben.bencustomerserver.viewholder

import android.net.Uri
import android.view.View
import android.view.ViewGroup
import com.ben.bencustomerserver.listener.MessageListItemClickListener
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.utils.BenCompat
import com.ben.bencustomerserver.utils.BenFileUtils
import com.ben.bencustomerserver.views.chatrow.BenChatRowFile

class BenFileViewHolder(itemView: View, itemClickListener: MessageListItemClickListener?) :
    BenChatRowViewHolder(itemView, itemClickListener!!) {
    override fun onBubbleClick(message: BaseMessageModel?) {
        super.onBubbleClick(message)

        val filePath: Uri = Uri.parse("")
        //检查Uri读权限
        BenFileUtils.takePersistableUriPermission(itemView.context , filePath)
        if (BenFileUtils.isFileExistByUri(itemView.context, filePath)) {
            BenCompat.openFile(itemView.context, filePath)
        } else {
            // download the file
//            context.startActivity(
//                Intent(
//                    context,
//
//                ).putExtra("msg", message)
//            )
        }

    }

    companion object {
        fun create(
            parent: ViewGroup,
            isSender: Boolean, itemClickListener: MessageListItemClickListener?
        ): BenChatRowViewHolder {
            return BenFileViewHolder(BenChatRowFile(parent.context, isSender), itemClickListener)
        }
    }
}