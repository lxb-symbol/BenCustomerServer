package com.ben.bencustomerserver.ui

import android.graphics.drawable.GradientDrawable.Orientation
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.adapter.BenMessageAdapter
import com.ben.bencustomerserver.databinding.CsActivityChatBinding
import com.ben.bencustomerserver.model.ChatViewModel
import com.ben.bencustomerserver.repositories.ChatRepository
import com.ben.module_base.ui.BaseActivity
import com.symbol.lib_net.net.RetrofitClient

/**
 * 聊天界面
 */
class ChatActivity : BaseActivity<ChatViewModel, CsActivityChatBinding>() {

    private lateinit var chatFragment: ChatFragment


    override fun initData() {

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