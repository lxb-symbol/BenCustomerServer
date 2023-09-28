package com.ben.bencustomerserver.ui

import android.net.Uri
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.databinding.CsActivityBenShowImgBinding
import com.ben.bencustomerserver.databinding.CsActivityChatBinding
import com.ben.bencustomerserver.vm.EmptyViewModel
import com.ben.lib_picture_selector.ImageLoaderUtils
import com.ben.module_base.ui.BaseActivity
import com.symbol.lib_net.net.RetrofitClient

open class BenShowBigImgActivity : BaseActivity<EmptyViewModel, CsActivityBenShowImgBinding>() {
    override fun getLayoutResId() = R.layout.cs_activity_ben_show_img
    var path: String? = ""

    override fun initData() {
    }

    override fun initView() {

        mViewBinding.title.ivBack.setOnClickListener {
            finish()
        }
        path = intent.getStringExtra("path")
        if (path?.startsWith("http://") == true){
            ImageLoaderUtils.load(BenShowBigImgActivity@ this, mViewBinding.iv, path)
        }else if (path?.startsWith("/upload")==true){
            ImageLoaderUtils.load(BenShowBigImgActivity@ this, mViewBinding.iv, RetrofitClient.BASE_URL+path)
        }else{
            ImageLoaderUtils.load(BenShowBigImgActivity@ this, mViewBinding.iv, Uri.parse(path))
        }
    }

    override fun initViewModel(): EmptyViewModel {
        return EmptyViewModel()
    }
}