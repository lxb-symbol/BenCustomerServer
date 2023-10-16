package com.ben.bencustomerserver.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.databinding.FragmentChatBinding
import com.ben.bencustomerserver.listener.OnChatExtendMenuItemClickListener
import com.ben.bencustomerserver.listener.OnChatLayoutListener
import com.ben.bencustomerserver.listener.OnChatRecordTouchListener
import com.ben.bencustomerserver.repositories.ChatRepository
import com.ben.bencustomerserver.utils.BenFileUtils
import com.ben.bencustomerserver.utils.UriUtils
import com.ben.bencustomerserver.utils.VersionUtils
import com.ben.bencustomerserver.views.CustomerServerEmojiMenu
import com.ben.bencustomerserver.vm.ChatViewModel
import com.ben.lib_picture_selector.PictureSelectUtil
import com.ben.lib_picture_selector.ResultListener
import com.ben.module_base.ui.BaseFragment
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.luck.picture.lib.entity.LocalMedia
import com.symbol.lib_net.net.RetrofitClient
import java.io.File
import java.io.IOException


/**
 * 聊天
 */
open class ChatFragment : BaseFragment<ChatViewModel, FragmentChatBinding>(), OnChatLayoutListener,
    OnChatRecordTouchListener {

    private var onChatExtendMenuItemClickListener: OnChatExtendMenuItemClickListener? = null
    private var cameraFile: File? = null

    companion object {
        const val REQUEST_CODE_SELECT_FILE = 12
    }

    override fun getLayoutResId() = R.layout.fragment_chat


    override fun initView() {
        mViewBinding.cl.chatLayoutListener = this
        mViewBinding.cl.setOnChatRecordTouchListener(this)
        mViewBinding.cl.setUpViewModel(mViewModel)
        mViewBinding.cl.chatInputMenu()
            .setCustomEmojiconMenu(CustomerServerEmojiMenu(requireContext()))

    }

    override fun initData() {
        //        获取聊天消息
        mViewModel.chatMessages(1)

        mViewModel.getFinalResultMessages().observe(this) {
            Log.e("symbol-3:", "聊天消息数目：${it.size}")
            // TODO:  待完善
            mViewBinding.cl.loadData()
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
            selectPicFromCamera()
            return
        }

        if (itemId == R.id.extend_item_picture) {
            selectPicFromLocal()
            return
        }

        if (itemId == R.id.extend_item_location) {

            return
        }

        if (itemId == R.id.extend_item_video) {

            selectVideoFromLocal()
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            mViewBinding.cl.chatInputMenu().hideExtendContainer()
            if (requestCode == REQUEST_CODE_SELECT_FILE) {
                onActivityResultForLocalFiles(data)
            }
        }
    }


    protected open fun selectVideoFromLocal() {

        PictureSelectUtil.get().selectVideos(requireContext(), 1, object : ResultListener {
            override fun onResult(medias: MutableList<LocalMedia>?) {
                medias?.let {
                    if (it.size == 0 || it.isEmpty()) return
                    val uri = Uri.parse(it[0].availablePath)
                    val mediaPlayer = MediaPlayer()
                    try {
                        mediaPlayer.setDataSource(requireContext(), uri!!)
                        mediaPlayer.prepare()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    val duration = mediaPlayer.duration
                    Log.d("ChatFragment", "vide opath = " + uri!!.path + ",duration=" + duration)
                    mViewBinding.cl.sendVideoMessage(uri, duration)
                }
            }

            override fun onCancel() {
            }

        })

    }


    /**
     * select local image
     */
    private fun selectPicFromLocal() {
        PictureSelectUtil.get().selectImages(requireContext(), 1, object : ResultListener {
            override fun onResult(medias: MutableList<LocalMedia>?) {
                medias?.let {
                    mViewBinding.cl.sendImageMessage(Uri.parse(it[0].availablePath))
                }
            }

            override fun onCancel() {

            }

        })
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun onChatError(code: Int, errorMsg: String?) {
    }





    private fun onActivityResultForLocalFiles(data: Intent?) {
        val uri = data?.data
        Log.i("symbol onActivityResultForLocalFiles-->", "---$uri")

        uri?.let {
            val path = UriUtils.copyFileProviderUri(requireContext(), uri)
            mViewBinding.cl.sendFileMessage(Uri.parse(path))
        }


    }


    /**
     * select picture from camera
     */
    protected fun selectPicFromCamera() {

        PictureSelectUtil.get().takePicture(requireContext(), object : ResultListener {
            override fun onResult(medias: MutableList<LocalMedia>?) {
                medias?.let {
                    mViewBinding.cl.sendImageMessage(Uri.parse(it[0].path))
                }
            }

            override fun onCancel() {
            }
        })

    }

    override fun onRecordTouch(v: View?, event: MotionEvent?): Boolean {

        if (!XXPermissions.isGranted(requireContext(), Manifest.permission.RECORD_AUDIO)) {
            XXPermissions.with(requireContext())
                .permission(Manifest.permission.RECORD_AUDIO)
                .request { _, allGranted ->
                    if (!allGranted) {

                    }
                }
            return false
        }

        return true
    }

}