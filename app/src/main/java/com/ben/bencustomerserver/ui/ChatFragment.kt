package com.ben.bencustomerserver.ui

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.databinding.FragmentChatBinding
import com.ben.bencustomerserver.listener.OnChatExtendMenuItemClickListener
import com.ben.bencustomerserver.listener.OnChatLayoutListener
import com.ben.bencustomerserver.listener.OnChatRecordTouchListener
import com.ben.bencustomerserver.model.ChatViewModel
import com.ben.bencustomerserver.repositories.ChatRepository
import com.ben.bencustomerserver.utils.EaseCommonUtils
import com.ben.bencustomerserver.utils.EaseCompat
import com.ben.bencustomerserver.utils.EaseFileUtils
import com.ben.bencustomerserver.utils.PathUtil
import com.ben.bencustomerserver.utils.VersionUtils
import com.ben.module_base.ui.BaseFragment
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.symbol.lib_net.net.RetrofitClient
import java.io.File
import java.io.IOException


/**
 * 聊天
 */
open class ChatFragment : BaseFragment<ChatViewModel, FragmentChatBinding>(), OnChatLayoutListener,
    OnChatRecordTouchListener {

    var onChatExtendMenuItemClickListener: OnChatExtendMenuItemClickListener? = null
    var cameraFile: File? = null

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
        mViewBinding.cl.setOnChatRecordTouchListener(this)

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


        if (itemId == R.id.extend_item_take_picture) {
            XXPermissions.with(requireContext())
                .permission(
                    Permission.CAMERA,
                    Permission.WRITE_EXTERNAL_STORAGE,
                    Permission.WRITE_EXTERNAL_STORAGE
                )
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                        if (allGranted) {
                            selectPicFromCamera()
                        }
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

        if (itemId == R.id.extend_item_picture) {
            XXPermissions.with(requireContext())
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
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

        if (itemId == R.id.extend_item_location) {
            TODO(" 待完善其他按钮 ")

            return
        }

        if (itemId == R.id.extend_item_video) {

            XXPermissions.with(requireContext())
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                        selectVideoFromLocal()
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

        if (itemId == R.id.extend_item_file) {
            XXPermissions.with(requireContext())
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                        selectFileFromLocal()
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
     * select local file
     */
    protected open fun selectFileFromLocal() {
        val intent = Intent()
        if (VersionUtils.isTargetQ(requireContext())) {
            intent.action = Intent.ACTION_OPEN_DOCUMENT
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                intent.action = Intent.ACTION_GET_CONTENT
            } else {
                intent.action = Intent.ACTION_OPEN_DOCUMENT
            }
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        startActivityForResult(
            intent,
            REQUEST_CODE_SELECT_FILE
        )
    }


    protected open fun selectVideoFromLocal() {
        val intent = Intent()
        if (VersionUtils.isTargetQ(requireContext())) {
            intent.action = Intent.ACTION_OPEN_DOCUMENT
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                intent.action = Intent.ACTION_GET_CONTENT
            } else {
                intent.action = Intent.ACTION_OPEN_DOCUMENT
            }
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "video/*"
        startActivityForResult(
            intent,
            REQUEST_CODE_SELECT_VIDEO
        )
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

                else -> {
                    Log.e("symbol onActivityResult", " nothing to do ")
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
        val uri = data?.data
        val filePath = EaseFileUtils.getFilePath(context, uri)
        if (!TextUtils.isEmpty(filePath) && File(filePath).exists()) {
            mViewBinding.cl.sendFileMessage(Uri.parse(filePath))
        } else {
            EaseFileUtils.saveUriPermission(context, uri, data)
            mViewBinding.cl.sendFileMessage(uri)
        }
    }

    private fun onActivityResultForLocalVideos(data: Intent?) {
        val uri = data?.data
        val mediaPlayer = MediaPlayer()
        try {
            mediaPlayer.setDataSource(requireContext(), uri!!)
            mediaPlayer.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val duration = mediaPlayer.duration
        Log.d(
            "ChatFragment",
            "path = " + uri!!.path + ",duration=" + duration
        )
        mViewBinding.cl.sendVideoMessage(uri, duration)
    }

    private fun onActivityResultForLocalPhotos(data: Intent?) {
        val selectedImage = data?.data
        selectedImage?.let {
            val filePath: String = EaseFileUtils.getFilePath(context, it)
            if (!TextUtils.isEmpty(filePath) && File(filePath).exists()) {
                mViewBinding.cl.sendImageMessage(Uri.parse(filePath))

            } else {
                EaseFileUtils.saveUriPermission(context, it, data)
                mViewBinding.cl.sendImageMessage(it)

            }
        }

    }


    private fun onActivityResultForCamera(data: Intent?) {
        cameraFile?.let {
            mViewBinding.cl.sendImageMessage(Uri.parse(it.absolutePath))
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
            PathUtil.instance?.imagePath, (System.currentTimeMillis()).toString() + ".jpg"
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

    override fun onRecordTouch(v: View?, event: MotionEvent?): Boolean =true

}