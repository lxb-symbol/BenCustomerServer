package com.ben.bencustomerserver.adapter

import android.content.Context
import android.view.ViewGroup
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.model.BenEmojiEntity
import com.ben.lib_picture_selector.ImageLoaderUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder

class CsEmojiAdapter : BaseQuickAdapter<BenEmojiEntity, QuickViewHolder>() {
    override fun onBindViewHolder(
        holder: QuickViewHolder,
        position: Int,
        item: BenEmojiEntity?
    ) {
        Glide.with(context).asGif().load(item?.id).into(holder.getView(R.id.iv_emoji))
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_ben_chat_cs_emoji, parent)
    }
}