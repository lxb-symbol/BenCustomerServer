package com.ben.bencustomerserver.utils

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import com.ben.bencustomerserver.utils.EMFileHelper.IFilePresenter
import java.io.File

/**
 * \~chinese
 * 文件帮助类，实现了判断文件是否存在，获取文件绝对路径，获取文件名称
 * ，获取文件大小及获取文件类型等方法
 * 可以实现如下自定义的逻辑：
 * （1）判断文件是否存在[IFilePresenter.isFileExist]
 * (2) 获取文件名称[IFilePresenter.getFilename]
 * (3) 获取文件绝对路径[IFilePresenter.getFilePath]
 * (4) 获取文件大小[IFilePresenter.getFileLength]
 * (5) 获取文件类型[IFilePresenter.getFileMimeType]
 * 通过方法[.setFileHelper]添加实现了[IFilePresenter]接口的对象即可
 *
 * 注：需要在[EMClient.init]后调用有标注的相关方法， 否则这些方法中的context会为空
 *
 * \~english
 * File help class, to determine whether the file exists, get the absolute path to the file,
 * get the file name, get the file length and get the file type and other methods
 * The following custom logic can be implemented：
 * （1）Determine whether the file exists [IFilePresenter.isFileExist]
 * (2) Get the file name [IFilePresenter.getFilename]
 * (3) Get the absolute path of file [IFilePresenter.getFilePath]
 * (4) Get file length [IFilePresenter.getFileLength]
 * (5) Get file type [IFilePresenter.getFileMimeType]
 * Implement [IFilePresenter] by the method of [.setFileHelper]
 *
 * Note: Should Calls this after [EMClient.init], otherwise the context may be null
 */
class EMFileHelper private constructor() {
    private object EMFileHelperInstance {
        val instance = EMFileHelper()
    }

    private val mContext: Context? = null
    private var mHelper: IFilePresenter

    init {
        mHelper = FilePresenterImpl()
    }

    /**
     * \~chinese
     * 设置自定义的FilePresenter
     * @param presenter
     *
     * \~english
     * Set custom FilePresenter
     * @param presenter
     */
    fun setFileHelper(presenter: IFilePresenter) {
        mHelper = presenter
    }

    /**
     * \~chinese
     * 判断文件是否存在，需要在[EMClient.init]后调用
     * @param fileUri   文件的资源标识符（路径）
     * @return  文件是否存在
     *
     * \~english
     * Whether the file exists, call it after [EMClient.init]
     * @param fileUri   File's Uri
     * @return  Whether the file exists
     */
    fun isFileExist(fileUri: Uri?): Boolean {
        return mHelper.isFileExist(mContext, fileUri)
    }

    /**
     * \~chinese
     * 判断文件是否存在，需要在[EMClient.init]后调用
     * @param stringUri 文件路径，可能是文件的绝对路径，也可能是资源标识符的字符串形式
     * @return  文件是否存在
     *
     * \~english
     * Whether the file exists, call it after [EMClient.init]
     * @param stringUri File's path, which may be the absolute path of file or the string from of Uri
     * @return  Whether the file exists
     */
    fun isFileExist(stringUri: String?): Boolean {
        return if (TextUtils.isEmpty(stringUri)) {
            false
        } else isFileExist(Uri.parse(stringUri))
    }

    /**
     * \~chinese
     * 判断文件是否存在
     * @param context   上下文
     * @param fileUri   文件资源标识符（路径）
     * @return  文件是否存在
     *
     * \~english
     * Determine whether the file exists
     * @param context
     * @param fileUri   File's Uri
     * @return  Whether the file exists
     */
    fun isFileExist(context: Context?, fileUri: Uri?): Boolean {
        return mHelper.isFileExist(context, fileUri)
    }

    /**
     * \~chinese
     * 判断文件是否存在
     * @param context   上下文
     * @param stringUri   文件路径，可能是文件的绝对路径，也可能是资源标识符的字符串形式
     * @return  文件是否存在
     *
     * \~english
     * Determine whether the file exists
     * @param context
     * @param stringUri   File's path, which may be the absolute path of file or the string from of Uri
     * @return  Whether the file exists
     */
    fun isFileExist(context: Context?, stringUri: String?): Boolean {
        return if (TextUtils.isEmpty(stringUri)) {
            false
        } else isFileExist(context, Uri.parse(stringUri))
    }

    /**
     * \~chinese
     * 获取文件名称
     * 注：需要在[EMClient.init]后调用
     * @param fileUri   文件的资源标识符（路径）
     * @return  文件名称
     *
     * \~english
     * Get file name
     * Note：Calls after [EMClient.init]
     * @param fileUri   File's Uri
     * @return  File name
     */
    fun getFilename(fileUri: Uri?): String {
        return mHelper.getFilename(mContext, fileUri)
    }

    /**
     * \~chinese
     * 获取文件名称
     * 注：需要在[EMClient.init]后调用
     * @param filePath   文件路径，可能是文件的绝对路径，也可能是资源标识符的字符串形式
     * @return  文件名称
     *
     * \~english
     * Get file name
     * Note：Calls after [EMClient.init]
     * @param filePath   File's path, which may be the absolute path of file or the string from of Uri
     * @return  File name
     */
    fun getFilename(filePath: String?): String {
        return if (TextUtils.isEmpty(filePath)) {
            ""
        } else getFilename(Uri.parse(filePath))
    }

    /**
     * \~chinese
     * 获取文件的绝对路径
     * 注：需要在[EMClient.init]后调用
     * @param fileUri   文件的资源标识符（路径）
     * @return  文件路径
     *
     * \~english
     * Get file's absolute path
     * Note：Calls after [EMClient.init]
     * @param fileUri   File's Uri
     * @return  File's absolute path
     */
    fun getFilePath(fileUri: Uri?): String {
        return mHelper.getFilePath(mContext, fileUri)
    }

    /**
     * \~chinese
     * 获取文件的绝对路径
     * 注：需要在[EMClient.init]后调用
     * @param filePath   文件路径，可能是文件的绝对路径，也可能是资源标识符的字符串形式
     * @return  文件的绝对路径
     *
     * \~english
     * Get file's absolute path
     * Note：Calls after [EMClient.init]
     * @param filePath   File's path, which may be the absolute path of file or the string from of Uri
     * @return  File's absolute path
     */
    fun getFilePath(filePath: String?): String? {
        return if (TextUtils.isEmpty(filePath)) {
            filePath
        } else getFilePath(
            Uri.parse(
                filePath
            )
        )
    }

    /**
     * \~chinese
     * 获取文件的绝对路径
     * @param context   上下文
     * @param fileUri   文件的资源标识符（路径）
     * @return  文件路径
     *
     * \~english
     * Get file's absolute path
     * @param context
     * @param fileUri   File's Uri
     * @return  File's absolute path
     */
    fun getFilePath(context: Context?, fileUri: Uri?): String {
        return mHelper.getFilePath(context, fileUri)
    }

    /**
     * \~chinese
     * 获取文件的绝对路径
     * @param context    上下文
     * @param filePath   文件路径，可能是文件的绝对路径，也可能是资源标识符的字符串形式
     * @return  文件的绝对路径
     *
     * \~english
     * Get file's absolute path
     * @param context
     * @param filePath   File's path, which may be the absolute path of file or the string from of Uri
     * @return  File's absolute path
     */
    fun getFilePath(context: Context?, filePath: String?): String? {
        return if (TextUtils.isEmpty(filePath)) {
            filePath
        } else getFilePath(context, Uri.parse(filePath))
    }

    /**
     * \~chinese
     * 获取文件大小
     * 注：需要在[EMClient.init]后调用
     * @param fileUri   文件的资源标识符（路径）
     * @return  文件大小
     *
     * \~english
     * Get file length
     * Note：Calls after [EMClient.init]
     * @param fileUri   File's Uri
     * @return  File length
     */
    fun getFileLength(fileUri: Uri?): Long {
        return mHelper.getFileLength(mContext, fileUri)
    }

    /**
     * \~chinese
     * 获取文件大小
     * 注：需要在[EMClient.init]后调用
     * @param filePath   文件路径，可能是文件的绝对路径，也可能是资源标识符的字符串形式
     * @return  文件大小
     *
     * \~english
     * Get file length
     * Note：Calls after [EMClient.init]
     * @param filePath   File's path, which may be the absolute path of file or the string from of Uri
     * @return  File length
     */
    fun getFileLength(filePath: String?): Long {
        return if (TextUtils.isEmpty(filePath)) {
            0
        } else getFileLength(
            Uri.parse(
                filePath
            )
        )
    }

    /**
     * \~chinese
     * 获取文件类型
     * 注：需要在[EMClient.init]后调用
     * @param fileUri   文件的资源标识符（路径）
     * @return  文件类型
     *
     * \~english
     * Get file mime type
     * Note：Calls after [EMClient.init]
     * @param fileUri   File's Uri
     * @return  File mime type
     */
    fun getFileMimeType(fileUri: Uri?): String {
        return mHelper.getFileMimeType(mContext, fileUri)
    }

    /**
     * 删除文件（可以获取到绝对路径）
     * SDk内部调用，不建议外部使用
     * 注：需要在[EMClient.init]后调用
     * @param filePath
     * @return
     */
    fun deletePrivateFile(filePath: String?): Boolean {
        var filePath = filePath
        if (TextUtils.isEmpty(filePath)) {
            return false
        }
        if (!isFileExist(filePath)) {
            return false
        }
        filePath = getFilePath(Uri.parse(filePath))
        if (!TextUtils.isEmpty(filePath)) {
            val file = File(filePath)
            if (file.exists()) {
                return file.delete()
            }
        }
        return false
    }

    /**
     * 格式化输入到SDK内部的文件的Uri，并输出未Uri
     * SDk内部调用，不建议外部使用
     * 注：需要在[EMClient.init]后调用
     * @param fileUri
     * @return
     */
    fun formatInUri(fileUri: Uri?): Uri? {
        var fileUri: Uri? = fileUri ?: return null
        if (mContext?.let { VersionUtils.isTargetQ(it) } == true && UriUtils.uriStartWithContent(fileUri!!)) {
            return fileUri
        }
        val path = getFilePath(fileUri)
        if (!TextUtils.isEmpty(path)) {
            fileUri = Uri.parse(path)
        }
        return fileUri
    }

    /**
     * 格式化输入到SDK的文件，并输出未Uri
     * SDk内部调用，不建议外部使用
     * @param file
     * @return
     */
    fun formatInUri(file: File?): Uri? {
        return if (file == null) {
            null
        } else Uri.parse(file.absolutePath)
    }

    /**
     * 格式化输入到SDK内部的文件的Uri，并输出未Uri
     * SDk内部调用，不建议外部使用
     * 注：需要在[EMClient.init]后调用
     * @param filePath
     * @return
     */
    fun formatInUri(filePath: String?): Uri? {
        return if (TextUtils.isEmpty(filePath)) {
            null
        } else formatInUri(
            Uri.parse(
                filePath
            )
        )
    }

    /**
     * 将格式化后的Uri转为string
     * SDk内部调用，不建议外部使用
     * 注：需要在[EMClient.init]后调用
     * @param uri
     * @return
     */
    fun formatInUriToString(uri: Uri?): String {
        var uri = uri
        uri = formatInUri(uri)
        return uri?.toString() ?: ""
    }

    /**
     * 格式化文件，并最终转为Uri的string样式
     * SDk内部调用，不建议外部使用
     * @param file
     * @return
     */
    fun formatInUriToString(file: File?): String {
        val fileUri = formatInUri(file) ?: return ""
        return fileUri.toString()
    }

    /**
     * 格式化传入的路径，并最终转为Uri的string样式
     * SDk内部调用，不建议外部使用
     * 注：需要在[EMClient.init]后调用
     * @param filePath
     * @return
     */
    fun formatInUriToString(filePath: String?): String {
        return if (TextUtils.isEmpty(filePath)) {
            ""
        } else formatInUriToString(Uri.parse(filePath))
    }

    /**
     * 格式化输出的路径，如果可以获取到绝对路径，则优先返回绝对路径
     * SDk内部调用，不建议外部使用
     * 注：需要在[EMClient.init]后调用
     * @param filePath
     * @return
     */
    fun formatOutLocalUrl(filePath: String?): String? {
        if (TextUtils.isEmpty(filePath)) {
            return filePath
        }
        val path = getFilePath(filePath)
        return if (!TextUtils.isEmpty(path)) {
            path
        } else filePath
    }

    /**
     * 格式化输出的Uri
     * SDk内部调用，不建议外部使用
     * 注：需要在[EMClient.init]后调用
     * @param filePath
     * @return
     */
    fun formatOutUri(filePath: String?): Uri? {
        if (TextUtils.isEmpty(filePath)) {
            return null
        }
        var fileUri = Uri.parse(filePath)
        if (VersionUtils.isTargetQ(mContext!!) && UriUtils.uriStartWithContent(fileUri)) {
            return fileUri
        }
        val str = getFilePath(fileUri)
        if (!TextUtils.isEmpty(str)) {
            fileUri = Uri.fromFile(File(str))
        }
        return fileUri
    }

    /**
     * \~chinese
     * 将Uri转成string类型
     * @param fileUri   文件的资源标识符（路径）
     * @return
     *
     * \~english
     * Convert the URI to a String
     * @param fileUri   File's Uri
     * @return
     */
    fun uriToString(fileUri: Uri?): String {
        return fileUri?.toString() ?: ""
    }

    class FilePresenterImpl : IFilePresenter {
        override fun isFileExist(context: Context?, fileUri: Uri?): Boolean {
            return UriUtils.isFileExistByUri(context!!, fileUri)
        }

        override fun getFilename(context: Context?, fileUri: Uri?): String {
            return UriUtils.getFileNameByUri(context, fileUri)!!
        }

        override fun getFilePath(context: Context?, fileUri: Uri?): String {
            return UriUtils.getFilePath(context!!, fileUri)!!
        }

        override fun getFileLength(context: Context?, fileUri: Uri?): Long {
            return UriUtils.getFileLength(context, fileUri)
        }

        override fun getFileMimeType(context: Context?, fileUri: Uri?): String {
            return UriUtils.getMimeType(context!!, fileUri)!!
        }
    }

    /**
     * \~chinese
     * 操作文件接口
     *
     * \~english
     * Operation file interface
     */
    interface IFilePresenter {
        /**
         * \~chinese
         * 用于判断文件是否存在
         * @param context   上下文
         * @param fileUri   文件的资源标识符（路径）
         * @return 文件是否存在
         *
         * \~english
         * Determine whether file exists
         * @param context
         * @param fileUri   File's Uri
         * @return  Whether file exists
         */
        fun isFileExist(context: Context?, fileUri: Uri?): Boolean

        /**
         * \~chinese
         * 获取文件名
         * @param context   上下文
         * @param fileUri   文件的资源标识符（路径）
         * @return 文件名
         *
         * \~english
         * Get file name
         * @param context
         * @param fileUri   File's Uri
         * @return  File name
         */
        fun getFilename(context: Context?, fileUri: Uri?): String

        /**
         * \~chinese
         * 获取文件绝对路径
         * @param context   上下文
         * @param fileUri   文件的资源标识符（路径）
         * @return 文件绝对路径
         *
         * \~english
         * Get file's absolute path
         * @param context
         * @param fileUri   File's Uri
         * @return  File's absolute path
         */
        fun getFilePath(context: Context?, fileUri: Uri?): String

        /**
         * \~chinese
         * 获取文件大小
         * @param context   上下文
         * @param fileUri   文件的资源标识符（路径）
         * @return 文件大小
         *
         * \~english
         * Get file length
         * @param context
         * @param fileUri   File's Uri
         * @return  File length
         */
        fun getFileLength(context: Context?, fileUri: Uri?): Long

        /**
         * \~chinese
         * 获取文件类型
         * @param context   上下文
         * @param fileUri   文件的资源标识符（路径）
         * @return 文件类型
         *
         * \~english
         * Get file mime type
         * @param context
         * @param fileUri   File's Uri
         * @return  File mime type
         */
        fun getFileMimeType(context: Context?, fileUri: Uri?): String
    }

    companion object {
        val instance: EMFileHelper
            get() = EMFileHelperInstance.instance
    }
}