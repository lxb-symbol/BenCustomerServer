/*
 *  * BenMob CONFIDENTIAL
 * __________________
 * Copyright (C) 2017 BenMob Technologies. All rights reserved.
 *
 * NOTICE: All information contained herein is, and remains
 * the property of BenMob Technologies.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from BenMob Technologies.
 */
package com.ben.bencustomerserver.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.File
import java.util.Collections
import java.util.Locale

object FileUtils {
    var fileTypes = arrayOf(
        "apk",
        "avi",
        "bmp",
        "chm",
        "dll",
        "doc",
        "docx",
        "dos",
        "gif",
        "html",
        "jpeg",
        "jpg",
        "movie",
        "mp3",
        "dat",
        "mp4",
        "mpe",
        "mpeg",
        "mpg",
        "pdf",
        "png",
        "ppt",
        "pptx",
        "rar",
        "txt",
        "wav",
        "wma",
        "wmv",
        "xls",
        "xlsx",
        "xml",
        "zip"
    )

    fun loadFiles(directory: File): Array<File?> {
        var listFiles = directory.listFiles()
        if (listFiles == null) listFiles = arrayOf()
        val tempFolder = ArrayList<File>()
        val tempFile = ArrayList<File>()
        for (file in listFiles) {
            if (file.isDirectory) {
                tempFolder.add(file)
            } else if (file.isFile) {
                tempFile.add(file)
            }
        }
        // sort list
        val comparator: Comparator<File> = MyComparator()
        Collections.sort(tempFolder, comparator)
        Collections.sort(tempFile, comparator)
        val datas = arrayOfNulls<File>(tempFolder.size + tempFile.size)
        System.arraycopy(tempFolder.toTypedArray(), 0, datas, 0, tempFolder.size)
        System.arraycopy(tempFile.toTypedArray(), 0, datas, tempFolder.size, tempFile.size)
        return datas
    }

    /**
     * Determine the type of file
     *
     * @param f
     * @return
     */
    fun getMIMEType(f: File): String {
        var type = ""
        val fName = f.name
        val end =
            fName.substring(fName.lastIndexOf(".") + 1, fName.length).lowercase(Locale.getDefault())
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(end)!!
        return type
    }

    fun getMIMEType(fileName: String): String {
        var type = ""
        val end = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length).lowercase(
            Locale.getDefault()
        )
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(end)!!
        return type
    }

    private const val TAG = "FileUtils"
    fun getUriForFile(context: Context, file: File?): Uri {
        // Build.VERSION_CODES.N = 24, 直接用会导致ant打包不成功
        return if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(context, context.packageName + ".fileProvider", file!!)
        } else {
            Uri.fromFile(file)
        }
    }

    // custom comparator
    class MyComparator : Comparator<File> {
        override fun compare(lhs: File, rhs: File): Int {
            return lhs.name.compareTo(rhs.name)
        }
    }
}