package com.ben.bencustomerserver.ui

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.adapter.BenMessageAdapter
import com.ben.bencustomerserver.databinding.FragmentChatBinding
import com.ben.bencustomerserver.model.ChatViewModel
import com.ben.bencustomerserver.repositories.ChatRepository
import com.ben.module_base.ui.BaseFragment
import com.symbol.lib_net.net.RetrofitClient


/**
 * 聊天
 */
class ChatFragment : BaseFragment<ChatViewModel, FragmentChatBinding>() {

    private lateinit var messageAdapter: BenMessageAdapter
    private lateinit var myLayoutManager: LinearLayoutManager
    override fun getLayoutResId() = R.layout.fragment_chat


    override fun initView() {
        myLayoutManager = LinearLayoutManager(requireContext())
        messageAdapter = BenMessageAdapter((mViewModel.getDataMessages().value ?: emptyList()))
        with(mViewBinding.rv) {
            layoutManager = myLayoutManager
            adapter = messageAdapter
        }
//      消息点击事件
        messageAdapter.setOnItemClickListener { adapter, view, position ->
            Log.e("symbol", " positon:$position")
            Log.e("symbol", " positon:$position")
        }
    }

    override fun initData() {
        //        获取聊天消息
        mViewModel.chatMessages()
        mViewModel.getDataMessages().observe(this) {
            Log.e("symbol result", "${it.size}")
            messageAdapter.addAll(it)
        }
    }


    override fun initViewModel(): ChatViewModel {
        val repo = ChatRepository(RetrofitClient.instance)
        return ChatViewModel(repo)
    }


}