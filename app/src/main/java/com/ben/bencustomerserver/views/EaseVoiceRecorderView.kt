package com.ben.bencustomerserver.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Message
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.manager.EaseVoiceRecorder
import com.ben.bencustomerserver.utils.EaseCommonUtils.isSdcardExist
import com.ben.bencustomerserver.views.chatrow.EaseChatRowVoicePlayer

/**
 * Voice recorder view
 *
 */
class EaseVoiceRecorderView : RelativeLayout {
    protected lateinit var micImages: Array<Drawable>
    protected var voiceRecorder: EaseVoiceRecorder? = null
    protected var wakeLock: WakeLock? = null
    protected var ivIcon: ImageView? = null
    protected var micImage: ImageView? = null
    protected var recordingHint: TextView? = null
    protected var micImageHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            // change image
            val index = msg.what
            if (index < 0 || index > micImages.size - 1) {
                return
            }
            micImage!!.setImageDrawable(micImages[index])
        }
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context)
    }

    @SuppressLint("InvalidWakeLockTag")
    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.ease_widget_voice_recorder, this)
        ivIcon = findViewById(R.id.iv_icon)
        micImage = findViewById<View>(R.id.mic_image) as ImageView
        recordingHint = findViewById<View>(R.id.recording_hint) as TextView
        voiceRecorder = EaseVoiceRecorder(micImageHandler)

        // animation resources, used for recording
        micImages = arrayOf(
            resources.getDrawable(R.drawable.ease_record_animate_01),
            resources.getDrawable(R.drawable.ease_record_animate_02),
            resources.getDrawable(R.drawable.ease_record_animate_03),
            resources.getDrawable(R.drawable.ease_record_animate_04)
        )
        wakeLock = (context.getSystemService(Context.POWER_SERVICE) as PowerManager).newWakeLock(
            PowerManager.SCREEN_DIM_WAKE_LOCK, "demo"
        )
    }

    /**
     * on speak button touched
     *
     * @param v
     * @param event
     */
    fun onPressToSpeakBtnTouch(
        v: View,
        event: MotionEvent,
        recorderCallback: EaseVoiceRecorderCallback?
    ): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                try {
                    val voicePlayer = EaseChatRowVoicePlayer.getInstance(context!!)
                    if (voicePlayer?.isPlaying==true) voicePlayer?.stop()
                    v.isPressed = true
                    setTextContent(v, true)
                    startRecording()
                } catch (e: Exception) {
                    v.isPressed = false
                }
                true
            }

            MotionEvent.ACTION_MOVE -> {
                if (event.y < dip2px(getContext(), 10f)) {
                    setTextContent(v, false)
                    showReleaseToCancelHint()
                } else {
                    setTextContent(v, true)
                    showMoveUpToCancelHint()
                }
                true
            }

            MotionEvent.ACTION_UP -> {
                v.isPressed = false
                setTextContent(v, false)
                if (event.y < 0) {
                    // discard the recorded audio.
                    discardRecording()
                } else {
                    // stop recording and send voice file
                    try {
                        val length = stopRecoding()
                        if (length > 0) {
                            recorderCallback?.onVoiceRecordComplete(voiceFilePath, length)
                        } else if (length == 401) {
                            Toast.makeText(
                                context,
                                R.string.recording_without_permission,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                R.string.the_recording_time_is_too_short,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, R.string.send_failure_please, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                true
            }

            else -> {
                discardRecording()
                false
            }
        }
    }

    private fun setTextContent(view: View, pressed: Boolean) {
        if (view is ViewGroup && view.childCount > 0) {
            val child = view.getChildAt(0)
            if (child is TextView) {
                child.text =
                    getContext().getString(if (pressed) R.string.button_pushtotalk_pressed else R.string.button_pushtotalk)
            }
        }
    }

    interface EaseVoiceRecorderCallback {
        /**
         * on voice record complete
         *
         * @param voiceFilePath
         * 录音完毕后的文件路径
         * @param voiceTimeLength
         * 录音时长
         */
        fun onVoiceRecordComplete(voiceFilePath: String?, voiceTimeLength: Int)
    }

    fun startRecording() {
        if (!isSdcardExist) {
            Toast.makeText(context, R.string.Send_voice_need_sdcard_support, Toast.LENGTH_SHORT)
                .show()
            return
        }
        try {
            wakeLock!!.acquire()
            this.visibility = VISIBLE
            recordingHint!!.text = context!!.getString(R.string.move_up_to_cancel)
            recordingHint!!.setBackgroundColor(Color.TRANSPARENT)
            ivIcon!!.setImageResource(R.drawable.ease_record_icon)
            micImage!!.visibility = VISIBLE
            voiceRecorder!!.startRecording(context)
        } catch (e: Exception) {
            e.printStackTrace()
            if (wakeLock!!.isHeld) wakeLock!!.release()
            if (voiceRecorder != null) voiceRecorder!!.discardRecording()
            this.visibility = INVISIBLE
            Toast.makeText(context, R.string.recoding_fail, Toast.LENGTH_SHORT).show()
            return
        }
    }

    fun showReleaseToCancelHint() {
        recordingHint!!.text = context!!.getString(R.string.release_to_cancel)
        //recordingHint.setBackgroundResource(R.drawable.ease_recording_text_hint_bg);
        ivIcon!!.setImageResource(R.drawable.ease_record_cancel)
        micImage!!.visibility = GONE
    }

    fun showMoveUpToCancelHint() {
        recordingHint!!.text = context!!.getString(R.string.move_up_to_cancel)
        recordingHint!!.setBackgroundColor(Color.TRANSPARENT)
        ivIcon!!.setImageResource(R.drawable.ease_record_icon)
        micImage!!.visibility = VISIBLE
    }

    fun discardRecording() {
        if (wakeLock!!.isHeld) wakeLock!!.release()
        try {
            // stop recording
            if (voiceRecorder!!.isRecording) {
                voiceRecorder!!.discardRecording()
                this.visibility = INVISIBLE
            }
        } catch (e: Exception) {
        }
    }

    fun stopRecoding(): Int {
        this.visibility = INVISIBLE
        if (wakeLock!!.isHeld) wakeLock!!.release()
        return voiceRecorder!!.stopRecoding()
    }

    var voiceFilePath: String = ""
        get() = voiceRecorder!!.voiceFilePath
    var voiceFileName: String = ""
        get() = voiceRecorder!!.voiceFileName
    var isRecording: Boolean = false
        get() = voiceRecorder!!.isRecording

    companion object {
        /**
         * dip to px
         * @param context
         * @param value
         * @return
         */
        fun dip2px(context: Context, value: Float): Float {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                context.resources.displayMetrics
            )
        }
    }
}