package com.ben.bencustomerserver.ui

import android.os.Bundle
import android.util.Log
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.databinding.CsActivityChatBinding
import com.ben.bencustomerserver.vm.ChatViewModel
import com.ben.bencustomerserver.repositories.ChatRepository
import com.ben.module_base.ui.BaseActivity
import com.symbol.lib_net.net.RetrofitClient

/**
 * 聊天界面
 */
class ChatActivity : BaseActivity<ChatViewModel, CsActivityChatBinding>() {

    private lateinit var chatFragment: ChatFragment

    override fun initData() {
        mViewModel.getTokenAndWsResul().observe(this) {
            Log.e("symbol:", "${it.token}   <---> ${it.socket_url}")
        }
        var code: String = "测试"
        mViewModel.getTokenAndWs(code)
    }

    override fun initView() {
        chatFragment = ChatFragment()
        val bundle = Bundle()
        chatFragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(mViewBinding.fcv.id, chatFragment)
            .commit()

    }

    override fun getLayoutResId() = R.layout.cs_activity_chat

    override fun initViewModel(): ChatViewModel {
        val repository = ChatRepository(RetrofitClient.instance)
        return ChatViewModel(repository)
    }


}