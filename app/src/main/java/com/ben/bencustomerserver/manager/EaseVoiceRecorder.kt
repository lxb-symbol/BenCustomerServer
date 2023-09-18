package com.ben.bencustomerserver.manager

import android.content.Context
import android.media.MediaRecorder
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.text.format.Time
import android.util.Log
import com.ben.bencustomerserver.utils.MMkvTool
import com.ben.bencustomerserver.utils.PathUtil.Companion.instance
import java.io.File
import java.io.IOException
import java.util.Date
import java.util.Objects

/***
 * 录音器
 */
class EaseVoiceRecorder(private val handler: Handler) {
    var recorder: MediaRecorder? = null
    var isRecording = false
        private set
    private var startTime: Long = 0
    var voiceFilePath: String? = null
        private set
    var voiceFileName: String? = null
        private set
    private var file: File? = null

    /**
     * start recording to the file
     */
    fun startRecording(appContext: Context?): String? {
        file = null
        try {
            // need to create recorder every time, otherwise, will got exception
            // from setOutputFile when try to reuse
            if (recorder != null) {
                recorder!!.release()
                recorder = null
            }
            recorder = MediaRecorder()
            recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder!!.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
            recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            recorder!!.setAudioChannels(1) // MONO
            recorder!!.setAudioSamplingRate(8000) // 8000Hz
            recorder!!.setAudioEncodingBitRate(64) // seems if change this to
            // 128, still got same file
            // size.
            val uid = MMkvTool.getUserId()
            voiceFileName = getVoiceFileName(uid?:"symbolId")
            voiceFilePath =
                Objects.requireNonNull(instance)?.voicePath.toString() + "/" + voiceFileName
            file = File(voiceFilePath)
            recorder!!.setOutputFile(file!!.absolutePath)
            recorder!!.prepare()
            isRecording = true
            recorder!!.start()
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("voice", "prepare() failed")
        }
        Thread {
            try {
                while (isRecording) {
                    val msg = Message()
                    msg.what = recorder!!.maxAmplitude * 13 / 0x7FFF
                    handler.sendMessage(msg)
                    SystemClock.sleep(100)
                }
            } catch (e: Exception) {
                // from the crash report website, found one NPE crash from
                // one android 4.0.4 htc phone
                // maybe handler is null for some reason
                Log.e("voice", e.toString())
            }
        }.start()
        startTime = Date().time
        Log.d("voice", "start voice recording to file:" + file!!.absolutePath)
        return if (file == null) null else file?.absolutePath
    }

    /**
     * stop the recoding
     *
     * @return seconds of the voice recorded
     */
    fun discardRecording() {
        if (recorder != null) {
            try {
                recorder!!.stop()
                recorder!!.release()
                recorder = null
                if (file != null && file!!.exists() && !file!!.isDirectory) {
                    file!!.delete()
                }
            } catch (ignored: RuntimeException) {
            }
            isRecording = false
        }
    }

    fun stopRecoding(): Int {
        if (recorder != null) {
            isRecording = false
            recorder!!.stop()
            recorder!!.release()
            recorder = null
            if (file == null || !file!!.exists() || !file!!.isFile) {
                return 401
            }
            if (file!!.length() == 0L) {
                file!!.delete()
                return 401
            }
            val seconds = (Date().time - startTime).toInt() / 1000
            Log.d(
                "voice",
                "voice recording finished. seconds:" + seconds + " file length:" + file!!.length()
            )
            return seconds
        }
        return 0
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        if (recorder != null) {
            recorder!!.release()
        }
    }

    private fun getVoiceFileName(uid: String): String {
        val now = Time()
        now.setToNow()
        return uid + now.toString().substring(0, 15) + EXTENSION
    }

    companion object {
        const val PREFIX = "voice"
        const val EXTENSION = ".amr"
    }
}