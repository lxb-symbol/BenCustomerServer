package com.ben.bencustomerserver.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.ben.bencustomerserver.adapter.CsEmojiAdapter
import com.ben.bencustomerserver.databinding.BenChatViewCsEmojiLayoutBinding
import com.ben.bencustomerserver.listener.BenEmojiconMenuListener
import com.ben.bencustomerserver.listener.IChatEmojiconMenu
import com.ben.bencustomerserver.listener.OnItemClickListener
import com.ben.bencustomerserver.utils.MMkvTool
import com.yalantis.ucrop.decoration.GridSpacingItemDecoration

class CustomerServerEmojiMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), IChatEmojiconMenu {

    private var emojis: MutableList<String>
    private lateinit var binding: BenChatViewCsEmojiLayoutBinding
    private  lateinit var adapter: CsEmojiAdapter
    private var listener: BenEmojiconMenuListener? = null



    init {
        binding = BenChatViewCsEmojiLayoutBinding.inflate(LayoutInflater.from(context), this, true)
        binding.rv.layoutManager = GridLayoutManager(context, 7)
        binding.rv.addItemDecoration(GridSpacingItemDecoration(7,24,true))
        adapter = CsEmojiAdapter()
        emojis=MMkvTool.getEmojis()
        adapter.addAll(emojis)
        Log.e("symbol","CustomServerEmojiMenu ${emojis.size} ")
        binding.rv.adapter = adapter

        adapter.setOnItemClickListener{ _,_,position->
            listener?.let {
                val emoji = emojis[position]
                it.onExpressionClicked(emoji)
            }
        }
    }




    override fun addEmojiconGroup(groupEntity: BenEmojiconGroupEntity?) {
    }

    override fun addEmojiconGroup(groupEntitieList: List<BenEmojiconGroupEntity?>?) {
    }

    override fun removeEmojiconGroup(position: Int) {
    }

    override fun setTabBarVisibility(isVisible: Boolean) {
    }

    override fun setEmojiconMenuListener(listener: BenEmojiconMenuListener?) {
        this.listener = listener
    }


}