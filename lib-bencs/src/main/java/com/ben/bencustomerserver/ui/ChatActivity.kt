package com.ben.bencustomerserver.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.connnect.RecieveMessageManager
import com.ben.bencustomerserver.connnect.WebSocketService
import com.ben.bencustomerserver.connnect.wsURL
import com.ben.bencustomerserver.databinding.CsActivityChatBinding
import com.ben.bencustomerserver.model.Constants
import com.ben.bencustomerserver.repositories.ChatRepository
import com.ben.bencustomerserver.utils.MMkvTool
import com.ben.bencustomerserver.utils.PathUtil
import com.ben.bencustomerserver.vm.ChatViewModel
import com.ben.module_base.ui.BaseActivity
import com.blankj.utilcode.util.LogUtils
import com.luck.picture.lib.utils.ToastUtils
import com.symbol.lib_net.net.RetrofitClient
import com.tencent.mmkv.MMKV
import java.lang.ref.WeakReference

/**
 * 聊天界面
 */
class ChatActivity : BaseActivity<ChatViewModel, CsActivityChatBinding>() {

    private lateinit var chatFragment: ChatFragment
    private var userId: String? = ""
    private var userName: String? = ""
    private var userAvatar: String? = ""
    private var sellerCode: String? = ""

    override fun initData() {
        userId = intent.getStringExtra(Constants.KEY_USER_ID)
        userName = intent.getStringExtra(Constants.KEY_USER_NAME)
        userAvatar = intent.getStringExtra(Constants.KEY_USER_AVATAR)
        sellerCode = intent.getStringExtra(Constants.KEY_SELLER_CODE)

        userId?.let { mViewModel.saveUserId(it) }
        userName?.let { mViewModel.saveUserName(it) }
        userAvatar?.let { mViewModel.saveUserAvatar(it) }
        sellerCode?.let { mViewModel.saveSellerCode(it) }

        MMkvTool.putIsHuman(false)
        mViewModel.getHumanTak().postValue(false)
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
            chatFragment.mViewBinding.cl.loadData()
            LogUtils.i("symbol-->",it)
            if (!it) {
                mViewBinding.tvRight.visibility = View.VISIBLE
            } else {
                mViewBinding.tvRight.visibility = View.INVISIBLE
            }
        }
        mViewBinding.tvRight.setOnClickListener { _ ->
            chatFragment.mViewBinding.cl.switch(true)
        }

        mViewModel.getNetErrorMsg().observe(this) {
            ToastUtils.showToast(this@ChatActivity, it)
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
        mViewBinding.ivBack.setOnClickListener { finish() }

    }

    private fun initSome() {
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