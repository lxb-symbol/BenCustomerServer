package com.ben.bencustomerserver.ui

import android.net.Uri
import android.util.Log
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.databinding.CsActivityBenShowVideoBinding
import com.ben.bencustomerserver.model.VoiceMessage
import com.ben.bencustomerserver.player.BenVideoCallback
import com.ben.bencustomerserver.player.BenVideoPlayer
import com.ben.bencustomerserver.utils.HttpUtils
import com.ben.bencustomerserver.vm.EmptyViewModel
import com.ben.module_base.ui.BaseActivity
import com.luck.picture.lib.utils.ToastUtils
import com.symbol.lib_net.net.RetrofitClient
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File

open class BenShowVideoActivity : BaseActivity<EmptyViewModel, CsActivityBenShowVideoBinding>(),
    BenVideoCallback {
    override fun getLayoutResId() = R.layout.cs_activity_ben_show_video
    var path: String? = ""

    override fun initData() {
    }

    override fun initView() {
        mViewBinding.evpPlayer.setCallback(this)
        mViewBinding.title.ivBack.setOnClickListener {
            finish()
        }
        path = intent.getStringExtra("path")
        if (path?.startsWith("http://") == true) {
            path?.let {
                downAndPlay(it)
            }
        } else if (path?.startsWith("/upload") == true) {
            path?.let {
                downAndPlay(RetrofitClient.BASE_URL+it)
            }
        } else {
            mViewBinding.evpPlayer.setAutoPlay(true)
            val uri = Uri.parse(path)
            mViewBinding.evpPlayer.setSource(uri)
        }
    }

    private fun downAndPlay(path: String) {
        val filePath =externalCacheDir?.path
        val name = "${path.hashCode()}.mp4"
        MainScope().launch {
            val b =   HttpUtils.downFile(this@BenShowVideoActivity,path?:"",filePath?:"",name)
            Log.i("symbol-5","b-->$b")
            if(b){
                mViewBinding.evpPlayer.setAutoPlay(true)
                mViewBinding.evpPlayer.setSource(Uri.parse("$filePath/$name"))
            }
        }
    }


    override fun initViewModel(): EmptyViewModel {
        return EmptyViewModel()
    }

    override fun onStarted(player: BenVideoPlayer?) {
    }

    override fun onPaused(player: BenVideoPlayer?) {
    }

    override fun onPreparing(player: BenVideoPlayer?) {
    }

    override fun onPrepared(player: BenVideoPlayer?) {
    }

    override fun onBuffering(percent: Int) {
    }

    override fun onError(player: BenVideoPlayer?, e: Exception?) {
    }

    override fun onCompletion(player: BenVideoPlayer?) {
    }

    override fun onClickVideoFrame(player: BenVideoPlayer?) {
    }

    override fun onPause() {
        super.onPause()
        mViewBinding.evpPlayer.pause()

    }

    override fun onDestroy() {
        super.onDestroy()
        mViewBinding.evpPlayer.release()

    }
}