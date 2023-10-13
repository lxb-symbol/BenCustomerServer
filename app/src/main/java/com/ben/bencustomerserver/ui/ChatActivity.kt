package com.ben.bencustomerserver.ui

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.util.Log
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.connnect.RecieveMessageManager
import com.ben.bencustomerserver.connnect.WebSocketService
import com.ben.bencustomerserver.connnect.wsURL
import com.ben.bencustomerserver.databinding.CsActivityChatBinding
import com.ben.bencustomerserver.repositories.ChatRepository
import com.ben.bencustomerserver.utils.BenEmojiUtil
import com.ben.bencustomerserver.utils.MMkvTool
import com.ben.bencustomerserver.utils.PathUtil
import com.ben.bencustomerserver.vm.ChatViewModel
import com.ben.module_base.ui.BaseActivity
import com.symbol.lib_net.net.RetrofitClient
import com.tencent.mmkv.MMKV
import java.lang.ref.WeakReference

/**
 * 聊天界面
 */
class ChatActivity : BaseActivity<ChatViewModel, CsActivityChatBinding>() {

    private lateinit var chatFragment: ChatFragment

    override fun initData() {

        mViewModel.saveUserId("symbol-8374782")
        mViewModel.saveUserName("symbol2023")
        mViewModel.saveUserAvatar("https://symbol-file.oss-cn-beijing.aliyuncs.com/b1aa0c85f414485bc77a122592eea150.jpg")
        mViewModel.saveSellerCode("5c6cbcb7d55ca")
        mViewModel.getEmojis(null)
        MMkvTool.putIsHuman(false)

        mViewModel.getHumanTak().postValue(MMkvTool.getIsHuman())
        mViewModel.getTokenAndWsResul().observe(this) {
            Log.e("symbol:", "${it.token}   <---> ${it.socket_url}")
            wsURL = it.socket_url
            val intent = Intent(this, WebSocketService::class.java)
            startService(intent)
        }
        MMkvTool.getSellerCode()?.let {
            mViewModel.getTokenAndWs(it)
        }

        mViewModel.getHumanTak().observe(this) {
            mViewBinding.tvTitle.text = if (it) "人工客服" else "机器人"
            // 切换到人工之后获取，历史消息列表
            chatFragment.mViewBinding.cl.loadData()
        }


    }

    override fun initView() {

        initSome()
        chatFragment = ChatFragment()
        val bundle = Bundle()
        chatFragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(mViewBinding.fcv.id, chatFragment)
            .commit()

    }

    fun initSome() {
        RecieveMessageManager.vm = WeakReference(mViewModel)
        MMKV.initialize(this)
        PathUtil.instance?.initDirs("", MMkvTool.getUserName() ?: "", this)
    }


    override fun getLayoutResId() = R.layout.cs_activity_chat

    override fun initViewModel(): ChatViewModel {
        val repository = ChatRepository(RetrofitClient.instance)
        return ChatViewModel(repository)
    }


}