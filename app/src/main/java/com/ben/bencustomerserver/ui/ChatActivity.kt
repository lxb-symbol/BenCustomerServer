package com.ben.bencustomerserver.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.databinding.CsActivityChatBinding
import com.ben.bencustomerserver.model.ChatViewModel
import com.ben.bencustomerserver.repositories.ChatRepository
import com.ben.module_base.ui.BaseActivity
import com.symbol.lib_net.net.RetrofitClient

class ChatActivity : BaseActivity<ChatViewModel, CsActivityChatBinding>() {


    override fun initData() {
        mViewModel.getDataMessages().observe(this) {
            Log.e("symbol result", "${it.size}")
        }
    }

    override fun initView() {

    }

    override fun getLayoutResId() = R.layout.cs_activity_chat

    override fun initViewModel(): ChatViewModel {
        val repository = ChatRepository(RetrofitClient.instance)
        return ChatViewModel(repository)
    }

}