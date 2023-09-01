package com.ben.bencustomerserver.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.listener.OnItemClickListener
import com.ben.bencustomerserver.views.EaseChatExtendMenu.ChatMenuItemModel

class EaseChatExtendMenuAdapter :
    EaseBaseChatExtendMenuAdapter<EaseChatExtendMenuAdapter.ViewHolder?, ChatMenuItemModel?>() {
    private var itemListener: OnItemClickListener? = null
    override val itemLayoutId: Int
        get() = R.layout.ease_chat_menu_item

    override fun easeCreateViewHolder(view: View?): ViewHolder {
        return ViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mData!![position]!!
        holder.imageView.setBackgroundResource(item.image)
        holder.textView.text = item.name
        holder.itemView.setOnClickListener { v ->
            item.clickListener?.onChatExtendMenuItemClick(item.id, v)
            if (itemListener != null) {
                itemListener!!.onItemClick(v, position)
            }
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        itemListener = listener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView
        val textView: TextView

        init {
            imageView = itemView.findViewById<View>(R.id.image) as ImageView
            textView = itemView.findViewById<View>(R.id.text) as TextView
        }
    }
}