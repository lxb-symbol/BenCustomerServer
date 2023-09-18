package com.ben.bencustomerserver.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.media.ExifInterface
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

open class ImageUtils {
    companion object {
        fun getRoundedCornerBitmap(bitmap: Bitmap): Bitmap {
            return getRoundedCornerBitmap(bitmap, 6f)
        }

        fun getRoundedCornerBitmap(bitmap: Bitmap, radius: Float): Bitmap {
            val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)
            val color = -0xbdbdbe
            val paint = Paint()
            val rect = Rect(0, 0, bitmap.width, bitmap.height)
            val rectF = RectF(rect)
            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = color
            canvas.drawRoundRect(rectF, radius, radius, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, rect, rect, paint)
            return output
        }

        /**
         * 获取视频的缩略图
         * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
         * 如果想要的缩略图的宽和高都小于MICRO_KIND,则类型要使用MICRO_KIND作为kind的值，这样会节省内存
         * @param videoPath 视频的路径
         * @param width 指定输出视频缩略图的宽度
         * @param height 指定输出视频缩略图的高度
         * @param kind 参照MediaStore。Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND.
         * 其中，MINI_KIND:512*384,MICRO_KIND:96*96
         * @return 指定大小的视频缩略图
         */
        fun getVideoThumbnail(videoPath: String?, width: Int, height: Int, kind: Int): Bitmap? {
            var bitmap: Bitmap? = null
            //获取视频的缩略图
            bitmap = ThumbnailUtils.createVideoThumbnail(videoPath!!, kind)
            Log.d("getVideoThumbnail", "video thumb width:" + bitmap!!.width)
            Log.d("getVideoThumbnail", "video thumb height:" + bitmap.height)
            bitmap = ThumbnailUtils.extractThumbnail(
                bitmap,
                width,
                height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT
            )
            return bitmap
        }

        /**
         * 保存video的缩略图
         * @param videoFile 视频文件
         * @param width 指定输出视频缩略图的宽度
         * @param height 指定输出视频缩略图的高度
         * @param kind 参照MediaStore。Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND.
         * 其中，MINI_KIND:512*384,MICRO_KIND:96*96
         * @return 缩略图绝对路径
         */
        fun saveVideoThumb(videoFile: File, width: Int, height: Int, kind: Int): String {
            val bitmap = getVideoThumbnail(videoFile.absolutePath, width, height, kind)
            val file = File(PathUtil.instance!!.videoPath, "th" + videoFile.name)
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            var fOut: FileOutputStream? = null
            try {
                fOut = FileOutputStream(file)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
            try {
                fOut?.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                fOut?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return file.absolutePath
        }

        fun decodeScaleImage(imagePath: String?, reqWidth: Int, reqHeight: Int): Bitmap? {
            // First decode with inJustDecodeBounds=true to check dimensions
            val options = getBitmapOptions(imagePath)

            // Calculate inSampleSize
            val sampleSize = calculateInSampleSize(options!!, reqWidth, reqHeight)
            Log.d(
                "img",
                "original wid" + options.outWidth + " original height:" + options.outHeight + " sample:"
                        + sampleSize
            )
            options.inSampleSize = sampleSize

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            var bm = BitmapFactory.decodeFile(imagePath, options)
            //图片旋转角度
            val degree = readPictureDegree(imagePath)
            var rotateBm: Bitmap? = null
            return if (bm != null && degree != 0) {
                rotateBm = rotateImageView(degree, bm)
                bm.recycle()
                bm = null
                rotateBm
            } else {
                bm
            }
            // return BitmapFactory.decodeFile(imagePath, options);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Throws(IOException::class)
        fun decodeScaleImage(
            context: Context,
            imageUri: Uri?,
            reqWidth: Int,
            reqHeight: Int
        ): Bitmap? {
            // First decode with inJustDecodeBounds=true to check dimensions
            val options = getBitmapOptions(context, imageUri)

            // Calculate inSampleSize
            val sampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
            Log.d(
                "img",
                "original wid" + options.outWidth + " original height:" + options.outHeight + " sample:"
                        + sampleSize
            )
            options.inSampleSize = sampleSize

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            var bm: Bitmap? = getBitmapByUri(context, imageUri, options)
            //图片旋转角度
            val degree = readPictureDegree(context, imageUri)
            var rotateBm: Bitmap? = null
            return if (bm != null && degree != 0) {
                rotateBm = rotateImageView(degree, bm)
                bm.recycle()
                bm = null
                rotateBm
            } else {
                bm
            }
            // return BitmapFactory.decodeFile(imagePath, options);
        }

        fun decodeScaleImage(
            context: Context,
            drawableId: Int,
            reqWidth: Int,
            reqHeight: Int
        ): Bitmap {
            val bitmap: Bitmap
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeResource(context.resources, drawableId, options)
            val sampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
            options.inSampleSize = sampleSize
            options.inJustDecodeBounds = false
            bitmap = BitmapFactory.decodeResource(context.resources, drawableId, options)
            return bitmap
        }

        fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int,
            reqHeight: Int
        ): Int {
            // Raw height and width of image
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1
            if (height > reqHeight || width > reqWidth) {

                // Calculate ratios of height and width to requested height and
                // width
                val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
                val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())

                // Choose the smallest ratio as inSampleSize value, this will
                // guarantee
                // a final image with both dimensions larger than or equal to the
                // requested height and width.
                inSampleSize = if (heightRatio > widthRatio) heightRatio else widthRatio
            }
            return inSampleSize
        }

        fun getThumbnailImage(imagePath: String?, thumbnailSize: Int): String? {

            /*
	    BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        if (imageWidth <= thumbnailSize && imageHeight <= thumbnailSize) {
	        //it is already small image within thumbnail required size. directly using it
	        return imagePath;
	    }*/
            val image = decodeScaleImage(imagePath, thumbnailSize, thumbnailSize)
            return try {
                val tempFile = File.createTempFile("image", ".jpg")
                val stream = FileOutputStream(tempFile)
                image?.compress(Bitmap.CompressFormat.JPEG, 60, stream)
                stream.close()
                Log.d(
                    "img",
                    "generate thumbnail image at:" + tempFile.absolutePath + " size:" + tempFile.length()
                )
                tempFile.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
                //if any error, return original file
                imagePath
            }
        }

        /**
         * 获取文件名
         * @param context
         * @param fileUri
         * @return
         */
        fun getFilename(context: Context?, fileUri: String?): String {
            if (TextUtils.isEmpty(fileUri)) {
                return ""
            }
            return if (!BenFileHelper.instance.isFileExist(context, Uri.parse(fileUri))) {
                ""
            } else BenFileHelper.instance.getFilename(fileUri)
        }

        /**
         * 获取文件长度
         * @param context
         * @param fileUri
         * @return
         */
        fun getFileLength(context: Context?, fileUri: String?): Long {
            return if (TextUtils.isEmpty(fileUri)) {
                0
            } else BenFileHelper.instance.getFileLength(fileUri)
        }

        /**
         * 获取图片缩略图
         * @param context
         * @param localPath
         * @return
         */
        fun getScaledImageByUri(context: Context, localPath: String): String {
            if (TextUtils.isEmpty(localPath)) {
                return localPath
            }
            Log.d("img", "original localPath: $localPath")
            val localUri = Uri.parse(localPath)
            if (!BenFileHelper.instance.isFileExist(context, localPath)) {
                return localPath
            }
            val filePath: String = BenFileHelper.instance.getFilePath(context, localUri)
            return if (!TextUtils.isEmpty(filePath)) {
                getScaledImage(context, filePath)
            } else {
                var scaledImage: Uri? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    scaledImage = getScaledImage(context, localUri)
                }
                scaledImage?.toString() ?: localPath
            }
        }

        /**
         * deu to the network bandwidth limitation, we will scale image to smaller
         * size before send out
         *
         * @param, appContext, the application context to get file dirs for creating temp image file
         * @param imagePath
         * @return
         */
        fun getScaledImage(appContext: Context, imagePath: String): String {
            // if file size is less than 100k, no need to scale, directly return
            // that file path. imagePath
            val originalFile = File(imagePath)
            if (!originalFile.exists()) {
                // wrong input
                return imagePath
            }
            val fileSize = originalFile.length()
            Log.d("img", "original img size:$fileSize")
            if (fileSize <= 100 * 1024) {
                Log.d("img", "use original small image")
                return imagePath
            }

            // scale image to required size
            val image = decodeScaleImage(imagePath, SCALE_IMAGE_WIDTH, SCALE_IMAGE_HEIGHT)
            // save image to a temp file
            try {
                /*
			 * String extension =
			 * imagePath.substring(imagePath.lastIndexOf(".")+1); String imgExt
			 * = null; if (extension.equalsIgnoreCase("jpg")) { imgExt = "jpg";
			 * } else { imgExt = "png"; }
			 */
                val tempFile = File.createTempFile("image", ".jpg", appContext.filesDir)
                val stream = FileOutputStream(tempFile)
                image?.compress(Bitmap.CompressFormat.JPEG, 70, stream)
                /*
			 * if (extension.equalsIgnoreCase("jpg") ||
			 * extension.equalsIgnoreCase("jpeg")) {
			 * image.compress(Bitmap.CompressFormat.JPEG, 60, stream); } else {
			 * image.compress(Bitmap.CompressFormat.PNG, 60, stream); }
			 */stream.close()
                Log.d(
                    "img",
                    "compared to small fle" + tempFile.absolutePath + " size:" + tempFile.length()
                )
                return tempFile.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return imagePath
        }

        /**
         * deu to the network bandwidth limitation, we will scale image to smaller
         * size before send out
         *
         * @param, appContext, the application context to get file dirs for creating temp image file
         * @param imageUri
         * @return
         */
        @RequiresApi(api = Build.VERSION_CODES.N)
        fun getScaledImage(appContext: Context, imageUri: Uri?): Uri? {
            // if file size is less than 100k, no need to scale, directly return
            // that file path. imagePath
            if (imageUri == null) {
                // wrong input
                return imageUri
            }
            try {
                val fileSize: Long = BenFileHelper.instance.getFileLength(imageUri)
                //			Log.d("img", "original img size:" + fileSize);
                if (fileSize <= 100 * 1024) {
                    Log.d("img", "use original small image")
                    return imageUri
                }

                // scale image to required size
                val image =
                    decodeScaleImage(appContext, imageUri, SCALE_IMAGE_WIDTH, SCALE_IMAGE_HEIGHT)
                // save image to a temp file

                /*
			 * String extension =
			 * imagePath.substring(imagePath.lastIndexOf(".")+1); String imgExt
			 * = null; if (extension.equalsIgnoreCase("jpg")) { imgExt = "jpg";
			 * } else { imgExt = "png"; }
			 */
                val tempFile = File.createTempFile("image", ".jpg", appContext.filesDir)
                val stream = FileOutputStream(tempFile)
                image?.compress(Bitmap.CompressFormat.JPEG, 70, stream)
                /*
			 * if (extension.equalsIgnoreCase("jpg") ||
			 * extension.equalsIgnoreCase("jpeg")) {
			 * image.compress(Bitmap.CompressFormat.JPEG, 60, stream); } else {
			 * image.compress(Bitmap.CompressFormat.PNG, 60, stream); }
			 */stream.close()
                Log.d(
                    "img",
                    "compared to small fle" + tempFile.absolutePath + " size:" + tempFile.length()
                )
                return Uri.fromFile(tempFile)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        /**
         * 检查图片是否被旋转并调整回来
         * @param context 上下文
         * @param imageUri 原始图片uri路径
         * @return 调整后的图片uri路径
         */
        fun checkDegreeAndRestoreImage(context: Context, imageUri: Uri?): Uri? {
            try {
                val filePath: String = BenFileHelper.instance.getFilePath(context, imageUri)
                var bm: Bitmap? = null
                var degree = 0
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = false
                if (!TextUtils.isEmpty(filePath)) {
                    bm = BitmapFactory.decodeFile(filePath, options)
                    degree = readPictureDegree(filePath)
                } else {
                    val parcelFileDescriptor = context.contentResolver.openFileDescriptor(
                        imageUri!!, "r"
                    )
                    val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
                    BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)
                    parcelFileDescriptor.close()
                    bm = getBitmapByUri(context, imageUri, options)
                    //图片旋转角度
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        degree = readPictureDegree(context, imageUri)
                    }
                }
                var rotateBm: Bitmap? = null
                if (bm != null && degree != 0) {
                    rotateBm = rotateImageView(degree, bm)
                    bm.recycle()
                    val tempFile = File.createTempFile(
                        "image-" + System.currentTimeMillis(),
                        ".jpg",
                        context.filesDir
                    )
                    val stream = FileOutputStream(tempFile)
                    if (rotateBm != null) {
                        rotateBm.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    }
                    stream.close()
                    return Uri.fromFile(tempFile)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return imageUri
        }

        /**
         * 得到"eaemobTemp"+i.jpg为文件名的临时图片
         * @param imagePath
         * @param i
         * @return
         */
        fun getScaledImage(appContext: Context, imagePath: String?, i: Int): String? {
//		List<String> temPaths = new ArrayList<String>();
//		for (int i = 0; i < imageLocalPaths.size(); i++) {
            val originalFile = File(imagePath)
            if (originalFile.exists()) {
                val fileSize = originalFile.length()
                Log.d("img", "original img size:$fileSize")
                if (fileSize > 100 * 1024) {
                    // scale image to required size
                    val image = decodeScaleImage(imagePath, SCALE_IMAGE_WIDTH, SCALE_IMAGE_HEIGHT)
                    // save image to a temp file
                    try {
                        val tempFile = File(appContext.externalCacheDir, "eaemobTemp$i.jpg")
                        val stream = FileOutputStream(tempFile)
                        image?.compress(Bitmap.CompressFormat.JPEG, 60, stream)
                        stream.close()
                        Log.d(
                            "img",
                            "compared to small fle" + tempFile.absolutePath + " size:" + tempFile.length()
                        )
                        return tempFile.absolutePath
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

//			}

//			temPaths.add(imagePath);
            }
            return imagePath
        }

        /**
         * merge multiple images into one the result will be 2*2 images or 3*3
         * images
         *
         * @param targetWidth
         * @param targetHeight
         * @param images
         * @return
         */
        fun mergeImages(targetWidth: Int, targetHeight: Int, images: List<Bitmap?>): Bitmap {
            val mergeBitmap =
                Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(mergeBitmap)
            canvas.drawColor(Color.LTGRAY)
            Log.d(
                "img",
                "merge images to size:" + targetWidth + "*" + targetHeight + " with images:" + images.size
            )
            val size: Int
            size = if (images.size <= 4) {
                2 // 2*2 images
            } else {
                3 // 3*3 images
            }
            // draw 2*2 images
            // expect targeWidth == targetHeight
            var imgIdx = 0
            val smallImageSize = (targetWidth - 4) / size
            for (row in 0 until size) {
                for (column in 0 until size) {
                    // load image into small size
                    val originalImage = images[imgIdx]
                    val smallImage = Bitmap.createScaledBitmap(
                        originalImage!!, smallImageSize, smallImageSize, true
                    )
                    val smallRoundedImage = getRoundedCornerBitmap(smallImage, 2f)
                    smallImage.recycle()
                    // draw on merged canvas
                    canvas.drawBitmap(
                        smallRoundedImage,
                        (column * smallImageSize + (column + 2)).toFloat(),
                        (row * smallImageSize
                                + (row + 2)).toFloat(),
                        null
                    )
                    smallRoundedImage.recycle()
                    imgIdx++
                    if (imgIdx == images.size) {
                        return mergeBitmap
                    }
                }
            }
            return mergeBitmap
        }

        /**
         * 读取图片属性：旋转的角度
         *
         * @param path
         * 图片绝对路径
         * @return degree旋转的角度
         */
        fun readPictureDegree(path: String?): Int {
            var degree = 0
            try {
                val exifInterface = ExifInterface(path!!)
                val orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return degree
        }

        /**
         * 读取图片属性：旋转的角度
         *
         * @param imageUri
         * 图片绝对路径
         * @return degree旋转的角度
         */
        @RequiresApi(api = Build.VERSION_CODES.N)
        fun readPictureDegree(context: Context, imageUri: Uri?): Int {
            var degree = 0
            try {
                val parcelFileDescriptor = context.contentResolver.openFileDescriptor(
                    imageUri!!, "r"
                )
                val exifInterface = ExifInterface(
                    parcelFileDescriptor!!.fileDescriptor
                )
                val orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
                }
                parcelFileDescriptor.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return degree
        }

        /*
	 * 旋转图片
	 *
	 * @param angle
	 *
	 * @param bitmap
	 *
	 * @return Bitmap
	 */
        fun rotateImageView(
            angle: Int,
            bitmap: Bitmap
        ): Bitmap {
            // 旋转图片 动作
            val matrix = Matrix()
            matrix.postRotate(angle.toFloat())
            // 创建新的图片
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }

        /**
         * 获取图片的Options
         * @param context
         * @param fileUri
         * @return
         */
        fun getBitmapOptions(context: Context, fileUri: String?): BitmapFactory.Options? {
            if (TextUtils.isEmpty(fileUri)) {
                return null
            }
            if (!BenFileHelper.instance.isFileExist(context, fileUri)) {
                return null
            }
            val filePath: String? = BenFileHelper.instance.getFilePath(context, fileUri)
            if (!TextUtils.isEmpty(filePath)) {
                return getBitmapOptions(filePath)
            } else {
                try {
                    return getBitmapOptions(context, Uri.parse(fileUri))
                } catch (e: IOException) {
                    Log.e("img", "get bitmap options fail by " + e.message)
                }
            }
            return null
        }

        /**
         * get bitmap options
         * @param imagePath
         * @return
         */
        fun getBitmapOptions(imagePath: String?): BitmapFactory.Options? {
            if (TextUtils.isEmpty(imagePath)) return null
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(imagePath, options)
            checkBitmapOrientation(readPictureDegree(imagePath), options)
            return options
        }

        /**
         * get bitmap options
         * @param context
         * @param uri
         * @return
         */
        @JvmStatic
        @Throws(IOException::class)
        fun getBitmapOptions(context: Context, uri: Uri?): BitmapFactory.Options {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(
                uri!!, "r"
            )
            val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)
            parcelFileDescriptor.close()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                checkBitmapOrientation(readPictureDegree(context, uri), options)
            }
            return options
        }

        private fun checkBitmapOrientation(degree: Int, options: BitmapFactory.Options) {
            // 旋转角度为90或者270时，宽高交换
            if (degree == 90 || degree == 270) {
                val originHeight = options.outHeight
                options.outHeight = options.outWidth
                options.outWidth = originHeight
            }
        }

        /**
         * get bitmap by uri
         * @param context
         * @param uri
         * @param options
         * @return
         * @throws IOException
         */
        @JvmStatic
        @Throws(IOException::class)
        fun getBitmapByUri(context: Context, uri: Uri?, options: BitmapFactory.Options?): Bitmap {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(
                uri!!, "r"
            )
            val bitmap = BitmapFactory.decodeFileDescriptor(
                parcelFileDescriptor!!.fileDescriptor, null, options
            )
            parcelFileDescriptor.close()
            return bitmap
        }

        const val SCALE_IMAGE_WIDTH = 640
        const val SCALE_IMAGE_HEIGHT = 960
    }
}