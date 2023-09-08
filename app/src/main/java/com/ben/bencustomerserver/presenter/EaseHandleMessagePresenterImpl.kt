package com.ben.bencustomerserver.presenter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.ben.bencustomerserver.listener.EMCallBack
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.utils.EaseCommonUtils
import com.ben.bencustomerserver.utils.EaseFileUtils
import com.ben.bencustomerserver.utils.EaseImageUtils
import com.ben.bencustomerserver.utils.ImageUtils
import com.ben.bencustomerserver.utils.PathUtil
import java.io.File
import java.io.FileOutputStream

class EaseHandleMessagePresenterImpl : EaseHandleMessagePresenter() {
    override fun sendTextMessage(content: String?) {
        sendTextMessage(content, false)
    }

    override fun sendTextMessage(content: String?, isNeedGroupAck: Boolean) {
//        if (EaseAtMessageHelper.get().containsAtUsername(content)) {
//            sendAtMessage(content)
//            return
//        }
//        val message: BaseMessageModel = createTxtSendMessage(content, toChatUsername)
//        message.setIsNeedGroupAck(isNeedGroupAck)
//        sendMessage(message)
    }

    override fun sendAtMessage(content: String?) {

    }

    override fun sendBigExpressionMessage(name: String?, identityCode: String?) {
        val message: BaseMessageModel? =
            name?.let { EaseCommonUtils.createExpressionMessage(toChatUsername, it, identityCode) }
        sendMessage(message)
    }

    override fun sendVoiceMessage(filePath: Uri?, length: Int) {
//        val message: BaseMessageModel = createVoiceSendMessage(filePath, length, toChatUsername)
//        sendMessage(message)
    }

    override fun sendImageMessage(imageUri: Uri?) {
        sendImageMessage(imageUri, false)
    }

    override fun sendImageMessage(imageUri: Uri?, sendOriginalImage: Boolean) {
        //Compatible with web and does not support heif image terminal
        //convert heif format to jpeg general image format
        var imageUri = imageUri
        imageUri = handleImageHeifToJpeg(imageUri)
//        val message: BaseMessageModel =
//            createImageSendMessage(imageUri, sendOriginalImage, toChatUsername)
//        sendMessage(message)
    }

    override fun sendLocationMessage(
        latitude: Double,
        longitude: Double,
        locationAddress: String?,
        buildingName: String?
    ) {
        // TODO:  
//        val message: BaseMessageModel = createLocationSendMessage(
//            latitude,
//            longitude,
//            locationAddress,
//            buildingName,
//            toChatUsername
//        )
//       
//        val body: BaseMessageModelBody = message.getBody()
//        val msgId: String = message.msgId
//        val from: String = message.from
//        Log.i(TAG, "body = $body")
//        Log.i(TAG, "msgId = $msgId from = $from")
//        sendMessage(message)
    }

    override fun sendVideoMessage(videoUri: Uri?, videoLength: Int) {
        val thumbPath = getThumbPath(videoUri)
        // TODO:
//        val message: BaseMessageModel =
//            createVideoSendMessage(
//                videoUri,
//                thumbPath,
//                videoLength,
//                toChatUsername
//            )
//        sendMessage(message)
    }

    override fun sendFileMessage(fileUri: Uri?) {
//        val message: BaseMessageModel =
//            createFileSendMessage(fileUri, toChatUsername)
//        sendMessage(message)
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

        message.messageStatusCallback = object : EMCallBack {
            override fun onSuccess() {
                if (isActive) {
                    runOnUI { mView!!.onPresenterMessageSuccess(message) }
                }
            }

            override fun onError(code: Int, error: String?) {
                if (isActive) {
                    runOnUI { mView!!.onPresenterMessageError(message, code, error) }
                }
            }

            override fun onProgress(progress: Int, status: String?) {
                if (isActive) {
                    runOnUI { mView!!.onPresenterMessageInProgress(message, progress) }
                }
            }
        }
        // send message 进行网络请求
        TODO("message 进行网络请求")
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
        if (!EaseFileUtils.isFileExistByUri(mView!!.context(), videoUri)) {
            return ""
        }
        val filePath: String = EaseFileUtils.getFilePath(mView!!.context(), videoUri)
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
            val options: BitmapFactory.Options
            val filePath: String = EaseFileUtils.getFilePath(mView!!.context(), imageUri)
            options = if (!TextUtils.isEmpty(filePath) && File(filePath).exists()) {
                ImageUtils.getBitmapOptions(filePath)
            } else ({
                mView!!.context()?.let { ImageUtils.getBitmapOptions(it, imageUri) }
            })!!
            if ("image/heif".equals(options.outMimeType, ignoreCase = true)) {
                imageUri = EaseImageUtils.imageToJpeg(
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

    companion object {
        private val TAG: String = EaseHandleMessagePresenterImpl::class.java.simpleName
    }
}