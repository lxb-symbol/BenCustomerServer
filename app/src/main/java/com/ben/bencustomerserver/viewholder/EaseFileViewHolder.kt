package com.ben.bencustomerserver.viewholder

import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import com.ben.bencustomerserver.listener.MessageListItemClickListener
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.Direct
import com.ben.bencustomerserver.utils.EaseCompat
import com.ben.bencustomerserver.utils.EaseFileUtils
import com.ben.bencustomerserver.views.chatrow.EaseChatRowFile

class EaseFileViewHolder(itemView: View, itemClickListener: MessageListItemClickListener?) :
    EaseChatRowViewHolder(itemView, itemClickListener!!) {
    override fun onBubbleClick(message: BaseMessageModel?) {
        super.onBubbleClick(message)

        val filePath: Uri = Uri.parse("")
        //检查Uri读权限
        EaseFileUtils.takePersistableUriPermission(itemView.context , filePath)
        if (EaseFileUtils.isFileExistByUri(itemView.context, filePath)) {
            EaseCompat.openFile(itemView.context, filePath)
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
        ): EaseChatRowViewHolder {
            return EaseFileViewHolder(EaseChatRowFile(parent.context, isSender), itemClickListener)
        }
    }
}