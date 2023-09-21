package com.ben.bencustomerserver.presenter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.ben.bencustomerserver.connnect.HttpMessageListener
import com.ben.bencustomerserver.connnect.RecieveMessageManager
import com.ben.bencustomerserver.connnect.WebSocketMessageListener
import com.ben.bencustomerserver.listener.INetCallback
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.MessageUtil
import com.ben.bencustomerserver.utils.BenCommonUtils
import com.ben.bencustomerserver.utils.BenFileUtils
import com.ben.bencustomerserver.utils.BenImageUtils
import com.ben.bencustomerserver.utils.ImageUtils
import com.ben.bencustomerserver.utils.PathUtil
import com.ben.bencustomerserver.vm.ChatViewModel
import java.io.File
import java.io.FileOutputStream

/**
 * 需要绑定 ViewModle 处理数据
 */
class BenHandleMessagePresenterImpl : BenHandleMessagePresenter() {


    override fun sendTextMessage(content: String?) {
        sendTextMessage(content, false)
    }

    override fun sendTextMessage(content: String?, isNeedGroupAck: Boolean) {
        content?.let {
            val msg = MessageUtil.generateTextModel(it)
            sendMessage(msg)
        }
    }

    override fun sendAtMessage(content: String?) {

    }

    override fun sendBigExpressionMessage(name: String?, identityCode: String?) {
        val message: BaseMessageModel? =
            name?.let { BenCommonUtils.createExpressionMessage(toChatUsername, it, identityCode) }
        sendMessage(message)
    }

    override fun sendVoiceMessage(filePath: Uri?, length: Int) {
        val message: BaseMessageModel = MessageUtil.generateVoiceModel(filePath, length)
        sendMessage(message)
    }

    override fun sendImageMessage(imageUri: Uri?) {
        sendImageMessage(imageUri, false)
    }

    override fun sendImageMessage(imageUri: Uri?, sendOriginalImage: Boolean) {
        //Compatible with web and does not support heif image terminal
        val message: BaseMessageModel = MessageUtil.generateImgModel(imageUri, sendOriginalImage)
        sendMessage(message)
    }

    override fun sendSwitchHumeMessage() {

        val model = MessageUtil.generateHumeSwitchModel()
        sendMessage(model)

    }

    override fun sendLocationMessage(
        latitude: Double,
        longitude: Double,
        locationAddress: String?,
        buildingName: String?
    ) {
        val message =
            MessageUtil.generateLocationModel(latitude, longitude, locationAddress, buildingName)
        sendMessage(message)
    }

    override fun sendVideoMessage(videoUri: Uri?, videoLength: Int) {
        val message = MessageUtil.generateVideoModel(videoUri, videoLength, "")
        sendMessage(message)
    }

    override fun sendFileMessage(fileUri: Uri?) {
        val message = MessageUtil.generateFileModel(fileUri)
        sendMessage(message)
    }

    override fun addMessageAttributes(message: BaseMessageModel?) {
        //可以添加一些自定义属性
        mView!!.addMsgAttrBeforeSend(message)
    }

    override fun sendMessage(message: BaseMessageModel?) {
        if (message == null) {
            if (isActive) {
                runOnUI { mView!!.sendMessageFail("message is null!") }
            }
            return
        }
        addMessageAttributes(message)
//        message.messageStatusCallback = object : EMCallBack {
//            override fun onSuccess() {
//                if (isActive) {
//                    runOnUI { mView!!.onPresenterMessageSuccess(message) }
//                }
//            }
//
//            override fun onError(code: Int, error: String?) {
//                if (isActive) {
//                    runOnUI { mView!!.onPresenterMessageError(message, code, error) }
//                }
//            }
//
//            override fun onProgress(progress: Int, status: String?) {
//                if (isActive) {
//                    runOnUI { mView!!.onPresenterMessageInProgress(message, progress) }
//                }
//            }
//        }
        //symbol  send message 进行网络请求
        message.let {
            (viewModel as ChatViewModel).sendMessage(it, object : INetCallback<String> {
                override fun onSuccess(data: String) {
                    if (isActive) {
                        runOnUI { mView!!.sendMessageFinish(message) }
                    }
                }

                override fun onError(code: Int, msg: String) {
                    if (isActive) {
                        runOnUI { mView!!.sendMessageFail(msg) }
                    }
                }
            })
        }
        if (isActive) {
            runOnUI { mView!!.sendMessageFinish(message) }
        }

    }

    override fun sendCmdMessage(action: String?) {

    }

    override fun resendMessage(message: BaseMessageModel?) {
        sendMessage(message)
    }

    override fun deleteMessage(message: BaseMessageModel?) {
        if (isActive) {
            runOnUI { mView!!.deleteLocalMessageSuccess(message) }
        }
    }


    /***
     * 撤回消息
     */
    override fun recallMessage(message: BaseMessageModel?) {

    }

    override fun translateMessage(
        message: BaseMessageModel?,
        languageCode: String?,
        isTranslation: Boolean
    ) {

    }

    override fun hideTranslate(message: BaseMessageModel?) {
    }

    /**
     * 获取视频封面
     * @param videoUri
     * @return
     */
    private fun getThumbPath(videoUri: Uri?): String {
        if (!BenFileUtils.isFileExistByUri(mView!!.context(), videoUri)) {
            return ""
        }
        val filePath: String = BenFileUtils.getFilePath(mView!!.context(), videoUri)
        val file: File = File(
            PathUtil.instance?.videoPath,
            "thvideo" + System.currentTimeMillis() + ".jpeg"
        )
        var createSuccess = true
        if (!TextUtils.isEmpty(filePath) && File(filePath).exists()) {
            try {
                val fos = FileOutputStream(file)
                val ThumbBitmap: Bitmap = ThumbnailUtils.createVideoThumbnail(filePath, 3)!!
                ThumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.close()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "" + e.message)
                if (isActive) {
                    runOnUI { mView!!.createThumbFileFail(e.message) }
                }
                createSuccess = false
            }
        } else {
            try {
                val fos = FileOutputStream(file)
                val media = MediaMetadataRetriever()
                media.setDataSource(mView!!.context(), videoUri)
                val frameAtTime: Bitmap = media.frameAtTime!!
                frameAtTime.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.close()
            } catch (e: Exception) {
                e.printStackTrace()
                e.message?.let { Log.e(TAG, it) }
                if (isActive) {
                    runOnUI { mView!!.createThumbFileFail(e.message) }
                }
                createSuccess = false
            }
        }
        return if (createSuccess) file.absolutePath else ""
    }

    /**
     * 图片heif转jpeg
     *
     * @param imageUri 图片Uri
     * @return Uri
     */
    private fun handleImageHeifToJpeg(imageUri: Uri?): Uri? {
        var imageUri = imageUri
        try {
            val options: BitmapFactory.Options?
            val filePath: String = BenFileUtils.getFilePath(mView!!.context(), imageUri)
            options = ImageUtils.getBitmapOptions(filePath)

            if ("image/heif".equals(options?.outMimeType, ignoreCase = true)) {
                imageUri = BenImageUtils.imageToJpeg(
                    mView!!.context(),
                    imageUri,
                    File(PathUtil.instance?.imagePath, "image_message_temp.jpeg")
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return imageUri
    }

    override fun addReaction(message: BaseMessageModel?, reaction: String?) {

    }

    override fun removeReaction(message: BaseMessageModel?, reaction: String?) {

    }

    /**
     * 这里只刷新数据 ，不处理数据
     */
    open fun addHttpListener() {
        RecieveMessageManager.addHttpMessageListener(this.toString(), object : HttpMessageListener {

            override fun receiveBoltMessage(model: BaseMessageModel) {
                if (isActive) {
                    mView?.sendMessageFinish(model)
                }
            }

        })

        RecieveMessageManager.addSocketMessageListener(this.toString(),
            object : WebSocketMessageListener {
                override fun onReceiveMessage(model: BaseMessageModel) {
                    if (isActive) {
                        mView?.sendMessageFinish(model)
                    }
                }
            })
    }

    companion object {
        private val TAG: String = BenHandleMessagePresenterImpl::class.java.simpleName
    }
}


