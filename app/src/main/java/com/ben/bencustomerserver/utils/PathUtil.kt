package com.ben.bencustomerserver.utils

import android.content.Context
import android.os.Environment
import java.io.File

/**
 * this is class define all pathes
 * the actuall user class is defined in application domain
 * but in hyphenate sdk, we need to access basic user data, including id, nick, sipnumber
 * so we can send notification, make voip call
 */
class PathUtil private constructor() {
    var voicePath: File? = null
        private set
    var imagePath: File? = null
        private set
    var historyPath: File? = null
        private set
    var videoPath: File? = null
        private set
    var filePath: File? = null
        private set

    //initialize directions used by user data
    fun initDirs(appKey: String?, userName: String, applicationContext: Context) {
        val appPackageName = applicationContext.packageName
//        pathPrefix = "/Android/data/$appPackageName/"
        voicePath = generateVoicePath(appKey, userName, applicationContext)
        if (!voicePath!!.exists()) {
            voicePath!!.mkdirs()
        }
        imagePath = generateImagePath(appKey, userName, applicationContext)
        //System.err.println("image path:" + imagePath.getAbsolutePath());
        if (!imagePath!!.exists()) {
            imagePath!!.mkdirs()
        }
        historyPath = generateHistoryPath(appKey, userName, applicationContext)
        if (!historyPath!!.exists()) {
            historyPath!!.mkdirs()
        }
        videoPath = generateVideoPath(appKey, userName, applicationContext)
        if (!videoPath!!.exists()) {
            videoPath!!.mkdirs()
        }
        filePath = generateFiePath(appKey, userName, applicationContext)
        if (!filePath!!.exists()) filePath!!.mkdirs()
    }

    companion object {
        var pathPrefix: String? = ""
        const val historyPathName = "/chat/"
        const val imagePathName = "/image/"
        const val voicePathName = "/voice/"
        const val filePathName = "/file/"
        const val videoPathName = "/video/"
        const val netdiskDownloadPathName = "/netdisk/"
        const val meetingPathName = "/meeting/"

        //protected final static String netdiskHost = BenMob.EASEMOB_STORAGE_URL + "/share/";
        private var storageDir: File? = null
        @JvmStatic
        var instance: PathUtil? = null
            get() {
                if (field == null) {
                    field = PathUtil()
                }
                return field
            }
            private set

        private fun getStorageDir(applicationContext: Context): File? {
            if (storageDir == null) {
                //try to use sd card if possible
                val sdPath = applicationContext.getExternalFilesDir(Environment.DIRECTORY_DCIM)
                if (sdPath!!.exists()) {
                    return sdPath
                }
                //use application internal storage instead
                storageDir = applicationContext.filesDir
            }
            return storageDir
        }

        private fun generateImagePath(
            appKey: String?,
            userName: String,
            applicationContext: Context
        ): File {
            var path: String? = null
            path =
                if (appKey == null) pathPrefix + userName + imagePathName else pathPrefix + appKey + "/" + userName + imagePathName
            return File(getStorageDir(applicationContext), path)
        }

        private fun generateVoicePath(
            appKey: String?,
            userName: String,
            applicationContext: Context
        ): File {
            var path: String? = null
            path =
                if (appKey == null) pathPrefix + userName + voicePathName else pathPrefix + appKey + "/" + userName + voicePathName
            return File(getStorageDir(applicationContext), path)
        }

        private fun generateFiePath(
            appKey: String?,
            userName: String,
            applicationContext: Context
        ): File {
            var path: String? = null
            path =
                if (appKey == null) pathPrefix + userName + filePathName else pathPrefix + appKey + "/" + userName + filePathName
            return File(getStorageDir(applicationContext), path)
        }

        private fun generateVideoPath(
            appKey: String?,
            userName: String,
            applicationContext: Context
        ): File {
            var path: String? = null
            path =
                if (appKey == null) pathPrefix + userName + videoPathName else pathPrefix + appKey + "/" + userName + videoPathName
            return File(getStorageDir(applicationContext), path)
        }

        private fun generateHistoryPath(
            appKey: String?,
            userName: String,
            applicationContext: Context
        ): File {
            var path: String? = null
            path =
                if (appKey == null) pathPrefix + userName + historyPathName else pathPrefix + appKey + "/" + userName + historyPathName
            return File(getStorageDir(applicationContext), path)
        }

        /*
    public static File getPushMessagePath(String appKey, String userId, Context applicationContext) {
        File filepath = new File(getHistoryPath(appKey, userId, applicationContext), userId + File.separator + "PushMsg.db"); 
        return filepath;
    }*/
        //Create a temp file relative to the file specified. Make sure the temp file is deleted afterward
        fun getTempPath(file: File): File {
            return File(file.absoluteFile.toString() + ".tmp")
        }
    }
}