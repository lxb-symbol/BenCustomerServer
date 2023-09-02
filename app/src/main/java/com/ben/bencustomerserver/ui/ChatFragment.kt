package com.ben.bencustomerserver.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.databinding.FragmentChatBinding
import com.ben.bencustomerserver.listener.OnChatExtendMenuItemClickListener
import com.ben.bencustomerserver.listener.OnChatLayoutListener
import com.ben.bencustomerserver.model.ChatViewModel
import com.ben.bencustomerserver.repositories.ChatRepository
import com.ben.bencustomerserver.utils.EaseCommonUtils
import com.ben.bencustomerserver.utils.EaseCompat
import com.ben.bencustomerserver.utils.PathUtil
import com.ben.module_base.ui.BaseFragment
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.symbol.lib_net.net.RetrofitClient
import java.io.File


/**
 * 聊天
 */
class ChatFragment : BaseFragment<ChatViewModel, FragmentChatBinding>(), OnChatLayoutListener {

    var onChatExtendMenuItemClickListener: OnChatExtendMenuItemClickListener? = null
    var cameraFile:File?=null
    companion object {
        const val REQUEST_CODE_MAP = 1
        const val REQUEST_CODE_CAMERA = 2
        const val REQUEST_CODE_LOCAL = 3
        const val REQUEST_CODE_DING_MSG = 4
        const val REQUEST_CODE_SELECT_VIDEO = 11
        const val REQUEST_CODE_SELECT_FILE = 12
    }

    //    private lateinit var messageAdapter: BenMessageAdapter
//    private lateinit var myLayoutManager: LinearLayoutManager
    override fun getLayoutResId() = R.layout.fragment_chat


    override fun initView() {
        mViewBinding.cl.chatLayoutListener = this

//        myLayoutManager = LinearLayoutManager(requireContext())
//        messageAdapter = BenMessageAdapter((mViewModel.getDataMessages().value ?: emptyList()))
//        with(mViewBinding.rv) {
//            layoutManager = myLayoutManager
//            adapter = messageAdapter
//        }
//      消息点击事件
//        messageAdapter.setOnItemClickListener { adapter, view, position ->
//            Log.e("symbol", " positon:$position")
//            Log.e("symbol", " positon:$position")
//        }
    }

    override fun initData() {
        //        获取聊天消息
        mViewModel.chatMessages()
        mViewModel.getDataMessages().observe(this) {
            Log.e("symbol result", "${it.size}")
//            messageAdapter.addAll(it)
        }
    }


    override fun initViewModel(): ChatViewModel {
        val repo = ChatRepository(RetrofitClient.instance)
        return ChatViewModel(repo)
    }

    override fun onUserAvatarClick(username: String?) {
        TODO("Not yet implemented")
    }

    override fun onUserAvatarLongClick(username: String?) = Unit

    override fun onChatExtendMenuItemClick(view: View?, itemId: Int) {
        onChatExtendMenuItemClickListener?.onChatExtendMenuItemClick(view, itemId)
        if (itemId == R.id.extend_item_picture) {
            XXPermissions.with(requireContext())
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                        selectPicFromLocal()
                    }

                    override fun onDenied(
                        permissions: MutableList<String>,
                        doNotAskAgain: Boolean
                    ) {
                        super.onDenied(permissions, doNotAskAgain)
                    }

                })
            return
        }


    }


    /**
     * select local image
     */
    fun selectPicFromLocal() {
        EaseCompat.openImage(
            this,
            REQUEST_CODE_LOCAL
        )
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        TODO("Not yet implemented")
    }

    override fun onChatError(code: Int, errorMsg: String?) {
        TODO("Not yet implemented")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            mViewBinding.cl.chatInputMenu.hideExtendContainer()
            when (requestCode) {
                REQUEST_CODE_CAMERA -> { // capture new image
                    onActivityResultForCamera(data)
                }
                REQUEST_CODE_LOCAL -> { // send local image
                    onActivityResultForLocalPhotos(data)
                }
                REQUEST_CODE_MAP -> { // location
                    onActivityResultForMapLocation(data)
                }
                REQUEST_CODE_DING_MSG -> { // To send the ding-type msg.
                    onActivityResultForDingMsg(data)
                }
                REQUEST_CODE_SELECT_FILE -> {
                    onActivityResultForLocalFiles(data)
                }
                REQUEST_CODE_SELECT_VIDEO -> {
                    onActivityResultForLocalVideos(data)
                }
            }
        }
    }

    private fun onActivityResultForMapLocation(data: Intent?) {

    }

    private fun onActivityResultForDingMsg(data: Intent?) {
        TODO("Not yet implemented")
    }

    private fun onActivityResultForLocalFiles(data: Intent?) {
        TODO("Not yet implemented")
    }

    private fun onActivityResultForLocalVideos(data: Intent?) {
        TODO("Not yet implemented")
    }

    private fun onActivityResultForLocalPhotos(data: Intent?) {
        TODO("Not yet implemented")
    }

    private fun onActivityResultForCamera(data: Intent?) {
        if (cameraFile != null && cameraFile!!.exists()) {
            mViewBinding.cl.sendImageMessage(Uri.parse(cameraFile!!.absolutePath))
        }

    }

    /**
     * 检查sd卡是否挂载
     *
     * @return
     */
    protected fun checkSdCardExist(): Boolean {
        return EaseCommonUtils.isSdcardExist
    }

    /**
     * select picture from camera
     */
    protected fun selectPicFromCamera() {
        if (!checkSdCardExist()) {
            return
        }
        cameraFile = File(
            PathUtil.instance?.imagePath, ( System.currentTimeMillis()).toString() + ".jpg"
        )
        cameraFile?.parentFile?.mkdirs()
        startActivityForResult(
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                MediaStore.EXTRA_OUTPUT, EaseCompat.getUriForFile(
                    context, cameraFile!!
                )
            ),
            REQUEST_CODE_CAMERA
        )
    }

}