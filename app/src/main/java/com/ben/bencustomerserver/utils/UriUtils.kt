package com.ben.bencustomerserver.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.InvocationTargetException
import java.util.Locale

object UriUtils {
    private val TAG = UriUtils::class.java.simpleName

    /**
     * 判断文件是否存在
     * @param context
     * @param fileUri
     * @return
     */
    fun isFileExistByUri(context: Context, fileUri: Uri?): Boolean {
        if (fileUri == null) {
            return false
        }
        // 先判断是否是Document uri
        val isDocumentUri: Boolean = DocumentFile.isDocumentUri(context, fileUri)
        // 如果是的话，则直接判断
        if (isDocumentUri) {
            return DocumentFile.fromSingleUri(context, fileUri)?.exists()!!
        }
        // 如果不是的话，有如下情况，1：是绝对路径；2：是file；3：content开头的uri；4：fileProvider开头的路径；5：其他情况不进行判断
        val filePath = getFilePath(context, fileUri)
        if (!TextUtils.isEmpty(filePath)) {
            return File(filePath).exists()
        }
        return if (uriStartWithFile(fileUri)) {
            val path = fileUri.path
            val exists = File(path).exists()
            val length = File(path).length()
            Log.d(TAG, "file uri exist = $exists file length = $length")
            exists
        } else if (!uriStartWithContent(fileUri)) {
            fileUri.toString().startsWith("/") && File(fileUri.toString()).exists()
        } else {
            val documentFile: DocumentFile = DocumentFile.fromSingleUri(context, fileUri)!!
            documentFile != null && documentFile.exists()
        }
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
     * 判断uri是否以content开头
     * @param fileUri
     * @return
     */
    fun uriStartWithContent(fileUri: Uri): Boolean {
        return "content".equals(fileUri.scheme, ignoreCase = true)
    }

    /**
     * 获取文件名
     * @param context
     * @param fileUri
     * @return
     */
    fun getFileNameByUri(context: Context?, fileUri: Uri?): String? {
        if (context == null || fileUri == null) {
            return ""
        }
        val mimeType = context.contentResolver.getType(fileUri)
        var filename: String? = null
        if (mimeType == null) {
            val filePath = getFilePath(context, fileUri)
            filename = if (TextUtils.isEmpty(filePath)) {
                getName(fileUri.toString())
            } else {
                val file = File(filePath)
                file.name
            }
        } else {
            val returnCursor = context.contentResolver.query(
                fileUri, null,
                null, null, null
            )
            if (returnCursor != null) {
                val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                returnCursor.moveToFirst()
                filename = returnCursor.getString(nameIndex)
                returnCursor.close()
            }
        }
        Log.d(TAG, "getFileNameByUri filename: $filename")
        return filename
    }

    private fun getName(filePath: String?): String? {
        if (filePath == null) {
            return null
        }
        val index = filePath.lastIndexOf('/')
        return filePath.substring(index + 1)
    }

    /**
     * get filename from DocumentFile
     * @param context
     * @param uri
     * @return
     */
    @Deprecated("use {@link #getFileNameByUri(Context, Uri)}")
    fun getFilenameByDocument(context: Context, uri: Uri?): String {
        val documentFile: DocumentFile = getDocumentFile(context, uri) ?: return ""
        return documentFile.name!!
    }

    /**
     * 获取文件大小
     * @param context
     * @param fileUri
     * @return
     */
    fun getFileLength(context: Context?, fileUri: Uri?): Long {
        // 先判断是否是Document uri
        val isDocumentUri: Boolean = DocumentFile.isDocumentUri(context!!, fileUri)
        // 如果是的话，则直接判断
        if (isDocumentUri) {
            return DocumentFile.fromSingleUri(context, fileUri!!)!!.length()
        }
        val mimeType = context!!.contentResolver.getType(fileUri!!)
        var fileLength: Long = 0
        if (mimeType == null) {
            val filePath = getFilePath(context, fileUri)
            if (!TextUtils.isEmpty(filePath)) {
                fileLength = File(filePath).length()
            }
        } else {
            val returnCursor = context.contentResolver.query(
                fileUri, null,
                null, null, null
            )
            if (returnCursor != null) {
                val nameIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
                returnCursor.moveToFirst()
                fileLength = returnCursor.getLong(nameIndex)
                returnCursor.close()
            }
        }
        Log.d(TAG, "getFileLength fileSize: $fileLength")
        return fileLength
    }

    /**
     * 获取文件类型
     * @param context
     * @param fileUri
     * @return
     */
    @Deprecated("use {@link #getMimeType(Context, Uri)}")
    fun getFileMimeType(context: Context, fileUri: Uri?): String? {
        if (fileUri == null) {
            return null
        }
        //target 小于Q
        if (!VersionUtils.isTargetQ(context)) {
            val filePath = getFilePath(context, fileUri)
            return if (!TextUtils.isEmpty(filePath)) {
                FileUtils.getMIMEType(File(filePath))
            } else null
        }
        //target 大于Q
        if (uriStartWithFile(fileUri)) {
            return FileUtils.getMIMEType(File(fileUri.path))
        }
        if (!uriStartWithContent(fileUri)) {
            return if (fileUri.toString().startsWith("/") && File(fileUri.toString()).exists()) {
                FileUtils.getMIMEType(File(fileUri.toString()))
            } else {
                null
            }
        }
        val documentFile: DocumentFile = getDocumentFile(context, fileUri) ?: return null
        return documentFile.getType()
    }

    @SuppressLint("Range")
    fun getVideoOrAudioDuration(context: Context, mediaUri: Uri?): Int {
        val projection = arrayOf<String>(MediaStore.Video.Media.DURATION)
        var cursor = context.contentResolver.query(
            mediaUri!!, projection, null,
            null, null
        )
        var duration: Long = 0
        if (cursor != null && cursor.moveToFirst()) {
            duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
            cursor.close()
            cursor = null
        }
        if (duration <= 0) {
            var retriever: MediaMetadataRetriever? = null
            var durationString = ""
            try {
                retriever = MediaMetadataRetriever()
                retriever.setDataSource(context, mediaUri)
                durationString =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toString()
                duration = Integer.valueOf(durationString).toLong()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: SecurityException) {
                e.printStackTrace()
            } finally {
                if (retriever != null) {
                    retriever.release()
                }
            }
        }
        if (duration <= 0) {
            duration = 0
        }
        Log.d(TAG, "duration:$duration")
        return duration.toInt()
    }

    private fun getDocumentFile(context: Context, uri: Uri?): DocumentFile? {
        if (uri == null) {
            Log.e(TAG, "uri is null")
            return null
        }
        val documentFile: DocumentFile = DocumentFile.fromSingleUri(context, uri)!!
        if (documentFile == null) {
            Log.e(TAG, "DocumentFile is null")
            return null
        }
        return documentFile
    }

    fun getUriString(uri: Uri?): String? {
        return uri?.toString()
    }

    @Deprecated("")
    fun getLocalUriFromString(url: String): Uri? {
        if (TextUtils.isEmpty(url)) {
            return null
        }
        if (url.startsWith("content")) {
            return Uri.parse(url)
        } else if (url.startsWith("file") && url.length > 7) {
            return Uri.fromFile(File(Uri.parse(url).path))
        } else if (url.startsWith("/")) {
            return Uri.fromFile(File(url))
        }
        return null
    }

    /**
     * 获取文件类型
     * @param context
     * @param fileUri
     * @return
     */
    fun getMimeType(context: Context, fileUri: Uri?): String? {
        var mimeType = context.contentResolver.getType(fileUri!!)
        if (TextUtils.isEmpty(mimeType)) {
            val filePath = getFilePath(context, fileUri)
            if (!TextUtils.isEmpty(filePath)) {
                mimeType = getMimeType(File(filePath))
            }
        }
        Log.d(TAG, "getMimeType mimeType: $mimeType")
        return mimeType
    }

    fun getMimeType(sourceFile: File?): String {
        return FileUtils.getMIMEType(sourceFile!!)
    }

    fun getMimeType(fileName: String): String {
        if (fileName.endsWith(".3gp") || fileName.endsWith(".amr")) {
            return "audio/3gp"
        }
        if (fileName.endsWith(".jpe") || fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")) {
            return "image/jpeg"
        }
        if (fileName.endsWith(".amr")) {
            return "audio/amr"
        }
        if (fileName.endsWith(".mp4")) {
            return "video/mp4"
        }
        return if (fileName.endsWith(".mp3")) {
            "audio/mpeg"
        } else {
            "application/octet-stream"
        }
    }

    /**
     * get file path, maybe the param of path is the Uri's string type.
     * @param context
     * @param path
     * @return
     */
    fun getFilePath(context: Context, path: String?): String? {
        return if (TextUtils.isEmpty(path)) {
            path
        } else getFilePath(context, Uri.parse(path))
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
    fun getFilePath(context: Context, uri: Uri?): String? {
        if (uri == null) {
            return ""
        }
        //sdk版本在29之前的
        if (!VersionUtils.isTargetQ(context)) {
            val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    val docId: String = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    }
                } else if (isDownloadsDocument(uri)) {
                    val id: String = DocumentsContract.getDocumentId(uri)
                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:".toRegex(), "")
                    }
                    val contentUriPrefixesToTry = arrayOf(
                        "content://downloads/public_downloads",
                        "content://downloads/my_downloads",
                        "content://downloads/all_downloads"
                    )
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        for (contentUriPrefix in contentUriPrefixesToTry) {
                            val contentUri: Uri = ContentUris.withAppendedId(
                                Uri.parse(contentUriPrefix),
                                java.lang.Long.valueOf(id)
                            )
                            var path: String? = null
                            try {
                                path = getDataColumn(context, contentUri, null, null)
                                if (!TextUtils.isEmpty(path)) {
                                    return path
                                }
                            } catch (e: Exception) {
                                //e.printStackTrace();
                            }
                        }
                        // 如果找不到绝对路径，则返回空字符串，让SDK直接处理Uri
                        return ""
                    }
                    var path: String? = null
                    try {
                        path = getDataColumn(context, uri, null, null)
                        if (!TextUtils.isEmpty(path)) {
                            return path
                        }
                    } catch (e: Exception) {
                        //e.printStackTrace();
                    }
                    // 如果还是找不到绝对路径，则返回空字符串，让SDK直接处理Uri
                    return ""
                } else if (isMediaDocument(uri)) {
                    val docId: String = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(
                        split[1]
                    )
                    var path: String? = null
                    try {
                        path = getDataColumn(context, contentUri, selection, selectionArgs)
                        if (!TextUtils.isEmpty(path)) {
                            return path
                        }
                    } catch (e: Exception) {
                        //e.printStackTrace();
                    }
                    // 如果找不到绝对路径，则返回空字符串，让SDK直接处理Uri
                    return ""
                }
            } else if (isFileProvider(context, uri)) {
                return getFPUriToPath(context, uri)
            } else if (isOtherFileProvider(context, uri)) {
                return copyFileProviderUri(context, uri)
            } else if (uriStartWithContent(uri)) {
                var path: String? = null
                try {
                    path = getDataColumn(context, uri, null, null)
                    if (!TextUtils.isEmpty(path)) {
                        return path
                    }
                } catch (e: Exception) {
                    //e.printStackTrace();
                }
                // 如果找不到绝对路径，则返回空字符串，让SDK直接处理Uri
                return ""
            }
        }
        // FileProvider
        if (isFileProvider(context, uri)) {
            return getFPUriToPath(context, uri)
        } else if (isOtherFileProvider(context, uri)) {
            return copyFileProviderUri(context, uri)
        } else if (uriStartWithFile(uri)) {
            return uri.path
        } else if (uri.toString().startsWith("/")) { //如果是路径的话，返回路径
            return uri.toString()
        }
        // sdk29之后的资源路径（content开头的资源路径）及其他情况
        return ""
    }

    /**
     * 从FileProvider获取文件
     * @param context
     * @param uri
     * @return
     */
    public fun copyFileProviderUri(context: Context, uri: Uri): String {
        //如果是分享过来的文件，则将其写入到私有目录下
        val filename = getFileNameByUri(context, uri)
        if (TextUtils.isEmpty(filename)) {
            return ""
        }
        val filePath = PathUtil.instance!!.filePath.toString() + File.separator + filename
        if (File(filePath).exists()) {
            return filePath
        }
        var `in`: InputStream? = null
        var out: OutputStream? = null
        try {
            `in` = context.contentResolver.openInputStream(uri)
            out = FileOutputStream(filePath)
            val tmp = ByteArray(2048)
            var l: Int
            while (`in`!!.read(tmp).also { l = it } != -1) {
                out.write(tmp, 0, l)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `in`?.close()
                out?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return if (File(filePath).exists()) filePath else ""
    }

    /**
     * 从FileProvider获取文件路径
     * @param context
     * @param uri
     * @return
     */
    private fun getFPUriToPath(context: Context, uri: Uri): String? {
        try {
            val packs: List<PackageInfo> =
                context.packageManager.getInstalledPackages(PackageManager.GET_PROVIDERS)
            if (packs != null) {
                val fileProviderClassName: String = FileProvider::class.java.getName()
                for (pack in packs) {
                    val providers = pack.providers
                    if (providers != null) {
                        for (provider in providers) {
                            if (uri.authority == provider.authority) {
                                if (provider.name.equals(
                                        fileProviderClassName,
                                        ignoreCase = true
                                    )
                                ) {
                                    val fileProviderClass: Class<FileProvider> =
                                        FileProvider::class.java
                                    try {
                                        val getPathStrategy = fileProviderClass.getDeclaredMethod(
                                            "getPathStrategy",
                                            Context::class.java,
                                            String::class.java
                                        )
                                        getPathStrategy.isAccessible = true
                                        val invoke =
                                            getPathStrategy.invoke(null, context, uri.authority)
                                        if (invoke != null) {
                                            val PathStrategyStringClass: String =
                                                FileProvider::class.java.getName() + "\$PathStrategy"
                                            val PathStrategy =
                                                Class.forName(PathStrategyStringClass)
                                            val getFileForUri = PathStrategy.getDeclaredMethod(
                                                "getFileForUri",
                                                Uri::class.java
                                            )
                                            getFileForUri.isAccessible = true
                                            val invoke1 = getFileForUri.invoke(invoke, uri)
                                            if (invoke1 is File) {
                                                return invoke1.absolutePath
                                            }
                                        }
                                    } catch (e: NoSuchMethodException) {
                                        e.printStackTrace()
                                    } catch (e: InvocationTargetException) {
                                        e.printStackTrace()
                                    } catch (e: IllegalAccessException) {
                                        e.printStackTrace()
                                    } catch (e: ClassNotFoundException) {
                                        e.printStackTrace()
                                    }
                                    break
                                }
                                break
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            cursor?.close()
        }
        return null
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
     * the uri is from current app's FileProvider
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
     * if the uri is from other app
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
}