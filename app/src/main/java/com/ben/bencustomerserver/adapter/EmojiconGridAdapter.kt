package com.ben.bencustomerserver.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.utils.BenSmileUtils
import com.ben.bencustomerserver.views.BenEmojicon
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class EmojiconGridAdapter(
    context: Context?,
    textViewResourceId: Int,
    objects: List<BenEmojicon?>?,
    private val emojiconType: BenEmojicon.Type
) : ArrayAdapter<BenEmojicon?>(
    context!!, textViewResourceId, objects!!
) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = if (emojiconType == BenEmojicon.Type.BIG_EXPRESSION) {
                View.inflate(context, R.layout.ben_row_big_expression, null)
            } else {
                View.inflate(context, R.layout.ben_row_expression, null)
            }
        }
        val imageView = convertView!!.findViewById<View>(R.id.iv_expression) as ImageView
        val emojicon = getItem(position)
        if (emojiconType==BenEmojicon.Type.BIG_EXPRESSION){
            val textView = convertView!!.findViewById<View>(R.id.tv_name) as TextView
            if (emojicon!!.name != null) {
                textView.text = emojicon.name
            }
        }

        if (BenSmileUtils.DELETE_KEY == emojicon?.emojiText) {
            imageView.setImageResource(R.drawable.ben_delete_expression)
        } else {
            if (emojicon?.icon != 0) {
                imageView.setImageResource(emojicon?.icon!!)
            } else if (emojicon.iconPath != null) {
                Glide.with(context).load(emojicon.iconPath)
                    .apply(RequestOptions.placeholderOf(R.drawable.ben_default_expression))
                    .into(imageView)
            }
        }
        return convertView
    }
}