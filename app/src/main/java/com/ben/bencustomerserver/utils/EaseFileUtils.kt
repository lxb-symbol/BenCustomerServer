package com.ben.bencustomerserver.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Log
import com.tencent.mmkv.MMKV
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

object EaseFileUtils {
    private val TAG = EaseFileUtils::class.java.simpleName
    private val isQ: Boolean
        private get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    fun isFileExistByUri(context: Context?, fileUri: Uri?): Boolean {
        return EMFileHelper.instance.isFileExist(fileUri)
    }

    /**
     * 删除文件
     * @param context
     * @param uri
     */
    fun deleteFile(context: Context, uri: Uri?) {
        if (isFileExistByUri(context, uri)) {
            val filePath = getFilePath(context, uri)
            if (!TextUtils.isEmpty(filePath)) {
                val file = File(filePath)
                if (file.exists() && file.isFile) {
                    file.delete()
                }
            } else {
                try {
                    context.contentResolver.delete(uri!!, null, null)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * 获取文件名
     * @param context
     * @param fileUri
     * @return
     */
    fun getFileNameByUri(context: Context?, fileUri: Uri?): String {
        return EMFileHelper.instance.getFilename(fileUri)
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    fun getFilePath(context: Context?, uri: Uri?): String {
        return EMFileHelper.instance.getFilePath(uri)
    }

    /**
     * 判断uri是否以file开头
     * @param fileUri
     * @return
     */
    fun uriStartWithFile(fileUri: Uri): Boolean {
        return "file".equals(fileUri.scheme, ignoreCase = true) && fileUri.toString().length > 7
    }

    /**
     * 判断是否以content开头的Uri
     * @param fileUri
     * @return
     */
    fun uriStartWithContent(fileUri: Uri): Boolean {
        return "content".equals(fileUri.scheme, ignoreCase = true)
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * 是否是本app的FileProvider
     * @param context
     * @param uri
     * @return
     */
    fun isFileProvider(context: Context, uri: Uri): Boolean {
        return (context.applicationInfo.packageName + ".fileProvider").equals(
            uri.authority,
            ignoreCase = true
        )
    }

    /**
     * 其他app分享过来的FileProvider
     * @param context
     * @param uri
     * @return
     */
    fun isOtherFileProvider(context: Context, uri: Uri): Boolean {
        val scheme = uri.scheme
        val authority = uri.authority
        return if (TextUtils.isEmpty(scheme) || TextUtils.isEmpty(authority)) {
            false
        } else (!(context.applicationInfo.packageName + ".fileProvider").equals(
            uri.authority,
            ignoreCase = true
        )
                && "content".equals(uri.scheme, ignoreCase = true)
                && authority!!.contains(".fileProvider".lowercase(Locale.getDefault())))
    }

    @SuppressLint("WrongConstant")
    fun saveUriPermission(context: Context?, fileUri: Uri?, intent: Intent?): Boolean {
        if (context == null || fileUri == null) {
            return false
        }
        //目前只处理scheme为"content"的Uri
        if (!uriStartWithContent(fileUri)) {
            return false
        }
        var intentFlags = 0
        if (intent != null) {
            intentFlags = intent.flags
        }
        val takeFlags = intentFlags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        var last: String? = null
        try {
            context.contentResolver.takePersistableUriPermission(fileUri, takeFlags)
            last = getLastSubFromUri(fileUri)
            Log.d(TAG, "saveUriPermission last part of Uri: $last")
        } catch (e: SecurityException) {
            Log.e("EaseFileUtils", "saveUriPermission failed e: " + e.message)
        }
        if (!TextUtils.isEmpty(last)) {
            MMKV.defaultMMKV().putString(last,fileUri.toString())
            return true
        }
        return false
    }

    private fun getLastSubFromUri(fileUri: Uri?): String {
        if (fileUri == null) {
            return ""
        }
        val uri = fileUri.toString()
        if (!uri.contains("/")) {
            return ""
        }
        val lastIndex = uri.lastIndexOf("/")
        return uri.substring(lastIndex + 1)
    }

    /**
     * 获取Uri的永久读权限
     * @param context
     * @param uri
     * @return
     */
    fun takePersistableUriPermission(context: Context?, uri: Uri?): Uri? {
        if (context == null || uri == null) {
            return null
        }
        //目前只处理scheme为"content"的Uri
        if (!uriStartWithContent(uri)) {
            return null
        }
        //获取Uri的读权限
        val last = getLastSubFromUri(uri)
        if (!TextUtils.isEmpty(last)) {
            TODO("随后补上 sharepreference 打算采用 MMVM")
            val fileUri = ""
            //            String fileUri = EasePreferenceManager.getInstance().getString(last);
            if (!TextUtils.isEmpty(fileUri)) {
                return try {
                    context.contentResolver.takePersistableUriPermission(
                        Uri.parse(fileUri),
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    Uri.parse(fileUri)
                } catch (e: SecurityException) {
                    Log.e("EaseFileUtils", "takePersistableUriPermission failed e: " + e.message)
                    null
                }
            }
        }
        try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (e: SecurityException) {
            Log.e("EaseFileUtils", "takePersistableUriPermission failed e: " + e.message)
            return null
        }
        return uri
    }

    fun getThumbPath(context: Context?, videoUri: Uri?): String {
        if (!isFileExistByUri(context, videoUri)) {
            return ""
        }
        val filePath = getFilePath(context, videoUri)
        val file =
            File(PathUtil.instance?.videoPath, "thvideo" + System.currentTimeMillis() + ".jpeg")
        var createSuccess = true
        if (!TextUtils.isEmpty(filePath) && File(filePath).exists()) {
            try {
                val fos = FileOutputStream(file)
                val thumbBitmap = ThumbnailUtils.createVideoThumbnail(filePath, 3)
                thumbBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.close()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, e.message!!)
                createSuccess = false
            }
        } else {
            try {
                val fos = FileOutputStream(file)
                val media = MediaMetadataRetriever()
                media.setDataSource(context, videoUri)
                val frameAtTime = media.frameAtTime
                frameAtTime!!.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.close()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, e.message!!)
                createSuccess = false
            }
        }
        return if (createSuccess) file.absolutePath else ""
    }
}