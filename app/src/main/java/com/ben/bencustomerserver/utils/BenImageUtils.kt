/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ben.bencustomerserver.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.utils.BenCommonUtils.getScreenInfo
import com.ben.bencustomerserver.utils.BenFileUtils.getFilePath
import com.ben.bencustomerserver.utils.PathUtil.Companion.instance
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Objects

object BenImageUtils : ImageUtils() {
    fun getImagePath(remoteUrl: String): String {
        val imageName = remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1)
        val path = Objects.requireNonNull(instance)?.imagePath.toString() + "/" + imageName
        Log.d("msg", "image path:$path")
        return path
    }

    fun getImagePathByFileName(filename: String): String {
        val path = instance!!.imagePath.toString() + "/" + filename
        Log.d("msg", "image path:$path")
        return path
    }

    fun getThumbnailImagePath(thumbRemoteUrl: String): String {
        val thumbImageName =
            thumbRemoteUrl.substring(thumbRemoteUrl.lastIndexOf("/") + 1, thumbRemoteUrl.length)
        val path = instance!!.imagePath.toString() + "/" + "th" + thumbImageName
        Log.d("msg", "thum image path:$path")
        return path
    }

    fun getThumbnailImagePathByName(filename: String): String {
        val path = instance!!.imagePath.toString() + "/" + "th" + filename
        Log.d("msg", "thum image dgdfg path:$path")
        return path
    }

    /**
     * 获取图片最大的长和宽
     * @param context
     */
    fun getImageMaxSize(context: Context?): IntArray {
        val screenInfo = getScreenInfo(context!!)
        val maxSize = IntArray(2)
        if (screenInfo != null) {
            maxSize[0] = (screenInfo[0] / 3).toInt()
            maxSize[1] = (screenInfo[0] / 2).toInt()
        }
        return maxSize
    }

    /**
     * 展示视频封面
     * @param context
     * @param imageView
     * @param message
     * @return
     */
    fun showVideoThumb(
        context: Context?,
        imageView: ImageView,
        message: BaseMessageModel
    ): ViewGroup.LayoutParams {
//        val body: BaseMessageModelBody =
//            message.getBody() as? EMVideoMessageBody ?: return imageView.layoutParams
//        //获取图片的尺寸
//        val width: Int = (body as EMVideoMessageBody).getThumbnailWidth()
//        val height: Int = (body as EMVideoMessageBody).getThumbnailHeight()
//        //获取视频封面本地资源路径
//        var localThumbUri: Uri? = (body as EMVideoMessageBody).getLocalThumbUri()
//        //检查Uri读权限
//        takePersistableUriPermission(context, localThumbUri)
//        //获取视频封面服务器地址
//        val thumbnailUrl: String = (body as EMVideoMessageBody).getThumbnailUrl()
//        if (!isFileExistByUri(context, localThumbUri)) {
//            localThumbUri = null
//        }
//        return showImage(context, imageView, localThumbUri, thumbnailUrl, width, height)
        // TODO:
        return ViewGroup.LayoutParams(-1,-1)
    }

    fun getImageShowSize(context: Context?, message: BaseMessageModel): ViewGroup.LayoutParams {
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
//        val body: BaseMessageModelBody = message.getBody() as? EMImageMessageBody ?: return params
//        //获取图片的长和宽
//        var width: Int = (body as EMImageMessageBody).getWidth()
//        var height: Int = (body as EMImageMessageBody).getHeight()
//        //获取图片本地资源地址
//        var imageUri: Uri? = (body as EMImageMessageBody).getLocalUri()
//        if (!isFileExistByUri(context, imageUri)) {
//            imageUri = (body as EMImageMessageBody).thumbnailLocalUri()
//            if (!isFileExistByUri(context, imageUri)) {
//                imageUri = null
//            }
//        }
//        //图片附件上传之前从消息体中获取不到图片的长和宽
//        if (width == 0 || height == 0) {
//            var options: BitmapFactory.Options? = null
//            try {
//                options = getBitmapOptions(context!!, imageUri)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            if (options != null) {
//                width = options.outWidth
//                height = options.outHeight
//            }
//        }
//        val maxSize = getImageMaxSize(context)
//        val maxWidth = maxSize[0]
//        val maxHeight = maxSize[1]
//        val mRadio = maxWidth * 1.0f / maxHeight
//        var radio = width * 1.0f / if (height == 0) 1 else height
//        if (radio == 0f) {
//            radio = 1f
//        }
//        //按原图展示的情况
//        if (maxHeight == 0 && maxWidth == 0 /*|| (width <= maxWidth && height <= maxHeight)*/) {
//            return params
//        }
//        //如果宽度方向大于最大值，且宽高比过大,将图片设置为centerCrop类型
//        //宽度方向设置为最大值，高度的话设置为宽度的1/2
//        if (mRadio / radio < 0.1f) {
//            params.width = maxWidth
//            params.height = maxWidth / 2
//        } else if (mRadio / radio > 4) {
//            //如果高度方向大于最大值，且宽高比过大,将图片设置为centerCrop类型
//            //高度方向设置为最大值，宽度的话设置为宽度的1/2
//            params.width = maxHeight / 2
//            params.height = maxHeight
//        } else {
//            //对比图片的宽高比，找到最接近最大值的，其余方向，按比例缩放
//            if (radio < mRadio) {
//                //说明高度方向上更大
//                params.height = maxHeight
//                params.width = (maxHeight * radio).toInt()
//            } else {
//                //宽度方向上更大
//                params.width = maxWidth
//                params.height = (maxWidth / radio).toInt()
//            }
//        }
        return params
    }

    /**
     * 展示图片
     * @param context
     * @param imageView
     * @param message
     * @return
     */
    fun showImage(
        context: Context?,
        imageView: ImageView,
        message: BaseMessageModel
    ): ViewGroup.LayoutParams {
//        val body: BaseMessageModelBody =
//            message.getBody() as? EMImageMessageBody ?: return imageView.layoutParams
//        //获取图片的长和宽
//        var width: Int = (body as EMImageMessageBody).getWidth()
//        var height: Int = (body as EMImageMessageBody).getHeight()
//        //获取图片本地资源地址
//        var imageUri: Uri? = (body as EMImageMessageBody).getLocalUri()
//        // 获取Uri的读权限
//        takePersistableUriPermission(context, imageUri)
//        Log.e(
//            "tag",
//            "current show small view big file: uri:" + imageUri + " exist: " + isFileExistByUri(
//                context,
//                imageUri
//            )
//        )
//        if (!isFileExistByUri(context, imageUri)) {
//            imageUri = (body as EMImageMessageBody).thumbnailLocalUri()
//            takePersistableUriPermission(context, imageUri)
//            Log.e(
//                "tag",
//                "current show small view thumbnail file: uri:" + imageUri + " exist: " + isFileExistByUri(
//                    context,
//                    imageUri
//                )
//            )
//            if (!isFileExistByUri(context, imageUri)) {
//                //context.revokeUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                imageUri = null
//            }
//        }
//        //图片附件上传之前从消息体中获取不到图片的长和宽
//        if (width == 0 || height == 0) {
//            var options: BitmapFactory.Options? = null
//            try {
//                options = getBitmapOptions(context!!, imageUri)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            if (options != null) {
//                width = options.outWidth
//                height = options.outHeight
//            }
//        }
//        //获取图片服务器地址
//        var thumbnailUrl: String? = null
//        // If not auto download thumbnail, do not set remote url
//        if (EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {
//            thumbnailUrl = (body as EMImageMessageBody).getThumbnailUrl()
//            if (TextUtils.isEmpty(thumbnailUrl)) {
//                thumbnailUrl = (body as EMImageMessageBody).getRemoteUrl()
//            }
//        }
//        return showImage(context, imageView, imageUri, thumbnailUrl, width, height)
        return ViewGroup.LayoutParams(-1,-1)
    }

    /**
     * 展示图片的逻辑如下：
     * 1、图片的宽度不超过屏幕宽度的1/3，高度不超过屏幕宽度1/2，这样的话，图片的长宽比位3：2
     * 2、如果图片的长宽比大于3：2，则选择高度方向与规定一致，宽度方向按比例缩放
     * 3、如果图片的长宽比小于3：2，则选择宽度方向与规定一致，高度方向按比例缩放
     * 4、如果图片的长和宽都小的话，就按照图片的大小展示就好
     * 5、如果没有本地资源，则展示服务器地址
     * @param context 上下文
     * @param imageView
     * @param imageUri 图片本地资源
     * @param imageUrl 服务器图片地址
     * @param imgWidth 图片的宽度
     * @param imgHeight 图片的长度
     * @return
     */
    fun showImage(
        context: Context?,
        imageView: ImageView,
        imageUri: Uri?,
        imageUrl: String?,
        imgWidth: Int,
        imgHeight: Int
    ): ViewGroup.LayoutParams {
        val maxSize = getImageMaxSize(context)
        val maxWidth = maxSize[0]
        val maxHeight = maxSize[1]
        val mRadio = maxWidth * 1.0f / maxHeight
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        var radio = imgWidth * 1.0f / if (imgHeight == 0) 1 else imgHeight
        if (radio == 0f) {
            radio = 1f
        }

        //按原图展示的情况
        if (maxHeight == 0 && maxWidth == 0 /*|| (width <= maxWidth && height <= maxHeight)*/) {
            if (context is Activity && (context.isFinishing || context.isDestroyed)) {
                return imageView.layoutParams
            }
            Glide.with(context!!).load(imageUri ?: imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView)
            return imageView.layoutParams
        }
        val params = imageView.layoutParams
        //如果宽度方向大于最大值，且宽高比过大,将图片设置为centerCrop类型
        //宽度方向设置为最大值，高度的话设置为宽度的1/2
        if (mRadio / radio < 0.1f) {
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            params.width = maxWidth
            params.height = maxWidth / 2
        } else if (mRadio / radio > 4) {
            //如果高度方向大于最大值，且宽高比过大,将图片设置为centerCrop类型
            //高度方向设置为最大值，宽度的话设置为宽度的1/2
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            params.width = maxHeight / 2
            params.height = maxHeight
        } else {
            //对比图片的宽高比，找到最接近最大值的，其余方向，按比例缩放
            if (radio < mRadio) {
                //说明高度方向上更大
                params.height = maxHeight
                params.width = (maxHeight * radio).toInt()
            } else {
                //宽度方向上更大
                params.width = maxWidth
                params.height = (maxWidth / radio).toInt()
            }
        }
        if (context is Activity && (context.isFinishing || context.isDestroyed)) {
            return params
        }
        Glide.with(context!!)
            .load(imageUri ?: imageUrl)
            .apply(
                RequestOptions()
                    .error(R.drawable.ben_default_image)
            )
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .override(params.width, params.height)
            .into(imageView)
        return params
    }

    /**
     * image转jpeg图片
     *
     * @param context  上下文
     * @param srcImg   原image uri
     * @param destFile 目标文件
     * @return Uri 图片URI
     */
    @Throws(IOException::class)
    fun imageToJpeg(context: Context?, srcImg: Uri?, destFile: File?): Uri? {
        val bitmap: Bitmap?
        val filePath = getFilePath(context, srcImg)
        bitmap = if (!TextUtils.isEmpty(filePath) && File(filePath).exists()) {
            BitmapFactory.decodeFile(filePath, null)
        } else {
            getBitmapByUri(context!!, srcImg, null)
        }
        if (null != bitmap && null != destFile) {
            if (destFile.exists()) {
                destFile.delete()
            }
            val out = FileOutputStream(destFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            return Uri.fromFile(destFile)
        }
        return srcImg
    }
}