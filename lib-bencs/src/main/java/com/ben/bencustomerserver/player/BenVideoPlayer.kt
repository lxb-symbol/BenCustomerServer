package com.ben.bencustomerserver.player

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.PorterDuff
import android.graphics.SurfaceTexture
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnBufferingUpdateListener
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnPreparedListener
import android.media.MediaPlayer.OnVideoSizeChangedListener
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import com.ben.bencustomerserver.R
import java.io.IOException
import java.util.Locale
import java.util.Objects

/**
 * 简易视频播放器，参考：https://github.com/mmbadimunei/easyVideoPlayer
 * 可以实现播放进度控制，播放暂停与继续播放等功能
 */
class BenVideoPlayer : FrameLayout, BenIUserMethods, SurfaceTextureListener, OnPreparedListener,
    OnBufferingUpdateListener, OnCompletionListener, OnVideoSizeChangedListener,
    MediaPlayer.OnErrorListener, View.OnClickListener, OnSeekBarChangeListener {
    private var mTextureView: TextureView? = null
    private var mSurface: Surface? = null
    private var mControlsFrame: View? = null
    private var mClickFrame: View? = null
    private var mSeeker: SeekBar? = null
    private var mLabelPosition: TextView? = null
    private var mLabelDuration: TextView? = null
    private var mBtnPlayPause: ImageButton? = null
    private var mPlayer: MediaPlayer? = null
    private var mSurfaceAvailable = false
    private var mIsPrepared = false
    private var mWasPlaying = false
    private var mInitialTextureWidth = 0
    private var mInitialTextureHeight = 0
    private var mHandler: Handler? = null
    private var mSource: Uri? = null
    private var mCallback: BenVideoCallback? = null
    private var mProgressCallback: BenVideoProgressCallback? = null
    private var mPlayDrawable: Drawable? = null
    private var mPauseDrawable: Drawable? = null
    private var mHideControlsOnPlay = true
    private var mAutoPlay = false
    private var mInitialPosition = -1
    private var mControlsDisabled = false
    private var mThemeColor = 0
    private var mAutoFullscreen = false
    private var mLoop = false
    private var currentPos = 0

    // Runnable used to run code on an interval to update counters and seeker
    private val mUpdateCounters: Runnable = object : Runnable {
        override fun run() {
            if (mHandler == null || !mIsPrepared || mSeeker == null || mPlayer == null) return
            var pos = mPlayer!!.currentPosition
            val dur = mPlayer!!.duration
            if ("oppo" == Build.BRAND.lowercase(Locale.getDefault()) && "OPPO R9sk" == Build.MODEL && pos <= currentPos) {
                pos = currentPos
            }
            if (pos > dur) pos = dur
            mLabelPosition!!.text = Util.getDurationString(pos.toLong(), false)
            mLabelDuration!!.text = Util.getDurationString(dur.toLong(), false)
            mSeeker!!.progress = pos
            mSeeker!!.max = dur
            if (mProgressCallback != null) mProgressCallback!!.onVideoProgressUpdate(pos, dur)
            if (mHandler != null) mHandler!!.postDelayed(this, UPDATE_INTERVAL.toLong())
        }
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        setBackgroundColor(Color.BLACK)
        if (attrs != null) {
            val a = context.theme.obtainStyledAttributes(attrs, R.styleable.BenVideoPlayer, 0, 0)
            try {
                val source = a.getString(R.styleable.BenVideoPlayer_ben_source)
                if (source != null && !source.trim { it <= ' ' }.isEmpty()) mSource =
                    Uri.parse(source)
                val playDrawableResId =
                    a.getResourceId(R.styleable.BenVideoPlayer_ben_playDrawable, -1)
                val pauseDrawableResId =
                    a.getResourceId(R.styleable.BenVideoPlayer_ben_pauseDrawable, -1)
                if (playDrawableResId != -1) {
                    mPlayDrawable = AppCompatResources.getDrawable(context, playDrawableResId)
                }
                if (pauseDrawableResId != -1) {
                    mPauseDrawable = AppCompatResources.getDrawable(context, pauseDrawableResId)
                }
                mHideControlsOnPlay =
                    a.getBoolean(R.styleable.BenVideoPlayer_ben_hideControlsOnPlay, true)
                mAutoPlay = a.getBoolean(R.styleable.BenVideoPlayer_ben_autoPlay, false)
                mControlsDisabled =
                    a.getBoolean(R.styleable.BenVideoPlayer_ben_disableControls, false)
                mThemeColor = a.getColor(
                    R.styleable.BenVideoPlayer_ben_themeColor,
                    Util.resolveColor(context, androidx.appcompat.R.attr.colorPrimary)
                )
                mAutoFullscreen = a.getBoolean(R.styleable.BenVideoPlayer_ben_autoFullscreen, false)
                mLoop = a.getBoolean(R.styleable.BenVideoPlayer_ben_loop, false)
            } finally {
                a.recycle()
            }
        } else {
            mHideControlsOnPlay = true
            mAutoPlay = false
            mControlsDisabled = false
            mThemeColor = Util.resolveColor(context, androidx.appcompat.R.attr.colorPrimary)
            mAutoFullscreen = false
            mLoop = false
        }
        if (mPlayDrawable == null) mPlayDrawable =
            AppCompatResources.getDrawable(context, R.drawable.ben_action_play)
        if (mPauseDrawable == null) mPauseDrawable =
            AppCompatResources.getDrawable(context, R.drawable.ben_action_pause)
    }

    override fun setSource(source: Uri) {
        val hadSource = mSource != null
        if (hadSource) stop()
        mSource = source
        if (mPlayer != null) {
            if (hadSource) {
                sourceChanged()
            } else {
                prepare()
            }
        }
    }

    override fun setCallback(callback: BenVideoCallback) {
        mCallback = callback
    }

    override fun setProgressCallback(callback: BenVideoProgressCallback) {
        mProgressCallback = callback
    }

    override fun setPlayDrawable(drawable: Drawable) {
        mPlayDrawable = drawable
        if (!isPlaying) mBtnPlayPause!!.setImageDrawable(drawable)
    }

    override fun setPlayDrawableRes(@DrawableRes res: Int) {
        AppCompatResources.getDrawable(context, res)?.let {
            setPlayDrawable(it)
        }

    }

    override fun setPauseDrawable(drawable: Drawable) {
        mPauseDrawable = drawable
        if (isPlaying) mBtnPlayPause!!.setImageDrawable(drawable)
    }

    override fun setPauseDrawableRes(@DrawableRes res: Int) {
        AppCompatResources.getDrawable(context, res)?.let {
            setPauseDrawable(it)
        }

    }

    override fun setThemeColor(@ColorInt color: Int) {
        mThemeColor = color
        invalidateThemeColors()
    }

    override fun setThemeColorRes(@ColorRes colorRes: Int) {
        setThemeColor(ContextCompat.getColor(context, colorRes))
    }

    override fun setHideControlsOnPlay(hide: Boolean) {
        mHideControlsOnPlay = hide
    }

    override fun setAutoPlay(autoPlay: Boolean) {
        mAutoPlay = autoPlay
    }

    override fun setInitialPosition(@IntRange(from = 0, to = Int.MAX_VALUE.toLong()) pos: Int) {
        mInitialPosition = pos
    }

    private fun sourceChanged() {
        setControlsEnabled(false)
        mSeeker!!.progress = 0
        mSeeker!!.isEnabled = false
        mPlayer!!.reset()
        if (mCallback != null) mCallback!!.onPreparing(this)
        try {
            setSourceInternal()
        } catch (e: IOException) {
            throwError(e)
        }
    }

    @Throws(IOException::class)
    private fun setSourceInternal() {
        if (mSource!!.scheme != null
            && (mSource!!.scheme == "http" || mSource!!.scheme == "https")
        ) {
            LOG("Loading web URI: " + mSource.toString())
            mPlayer!!.setDataSource(mSource.toString())
        } else if (mSource!!.scheme != null && mSource!!.scheme == "file" && mSource!!.path!!.contains(
                "/android_assets/"
            )
        ) {
            LOG("Loading assets URI: " + mSource.toString())
            val afd: AssetFileDescriptor
            afd = context
                .assets
                .openFd(mSource.toString().replace("file:///android_assets/", ""))
            mPlayer!!.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
        } else if (mSource!!.scheme != null && mSource!!.scheme == "asset") {
            LOG("Loading assets URI: " + mSource.toString())
            val afd: AssetFileDescriptor
            afd = context.assets.openFd(mSource.toString().replace("asset://", ""))
            mPlayer!!.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
        } else {
            LOG("Loading local URI: " + mSource.toString())
            mPlayer!!.setDataSource(context, mSource!!)
        }
        mPlayer!!.prepareAsync()
    }

    private fun prepare() {
        if (!mSurfaceAvailable || mSource == null || mPlayer == null || mIsPrepared) return
        if (mCallback != null) mCallback!!.onPreparing(this)
        try {
            mPlayer!!.setSurface(mSurface)
            setSourceInternal()
        } catch (e: IOException) {
            throwError(e)
        }
    }

    private fun setControlsEnabled(enabled: Boolean) {
        if (mSeeker == null) return
        mSeeker!!.isEnabled = enabled
        mBtnPlayPause!!.isEnabled = enabled
        val disabledAlpha = .4f
        mBtnPlayPause!!.alpha = if (enabled) 1f else disabledAlpha
        mClickFrame!!.isEnabled = enabled
    }

    override fun showControls() {
        if (mControlsDisabled || isControlsShown || mSeeker == null) return
        mControlsFrame!!.animate().cancel()
        mControlsFrame!!.alpha = 0f
        mControlsFrame!!.visibility = VISIBLE
        mBtnPlayPause!!.visibility = VISIBLE
        mControlsFrame!!
            .animate()
            .alpha(1f)
            .setInterpolator(DecelerateInterpolator())
            .setListener(
                object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        if (mAutoFullscreen) {
                            setFullscreen(false)
                        }
                    }
                })
            .start()
    }

    override fun hideControls() {
        if (mControlsDisabled || !isControlsShown || mSeeker == null) return
        mControlsFrame!!.animate().cancel()
        mControlsFrame!!.alpha = 1f
        mControlsFrame!!.visibility = VISIBLE
        mControlsFrame!!
            .animate()
            .alpha(0f)
            .setInterpolator(DecelerateInterpolator())
            .setListener(
                object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        setFullscreen(true)
                        if (mControlsFrame != null) {
                            mControlsFrame!!.visibility = INVISIBLE
                            mBtnPlayPause!!.visibility = INVISIBLE
                        }
                    }
                })
            .start()
    }

    @get:CheckResult
    override val isControlsShown: Boolean
        get() = !mControlsDisabled && mControlsFrame != null && mControlsFrame!!.alpha > .5f

    override fun toggleControls() {
        if (mControlsDisabled) return
        if (isControlsShown) {
            hideControls()
        } else {
            showControls()
        }
    }

    override fun enableControls(andShow: Boolean) {
        mControlsDisabled = false
        if (andShow) showControls()
        mClickFrame!!.setOnClickListener { toggleControls() }
        mClickFrame!!.isClickable = true
    }

    override fun disableControls() {
        mControlsDisabled = true
        mControlsFrame!!.visibility = GONE
        mClickFrame!!.setOnClickListener(null)
        mClickFrame!!.isClickable = false
    }

    @get:CheckResult
    override val isPrepared: Boolean
        get() = mPlayer != null && mIsPrepared

    @get:CheckResult
    override val isPlaying: Boolean
        get() = mPlayer != null && mPlayer!!.isPlaying

    @get:CheckResult
    override val currentPosition: Int
        get() = if (mPlayer == null) -1 else mPlayer!!.currentPosition

    @get:CheckResult
    override val duration: Int
        get() = if (mPlayer == null) -1 else mPlayer!!.duration

    override fun start() {
        if (mPlayer == null) return
        mPlayer!!.start()
        if (mCallback != null) mCallback!!.onStarted(this)
        if (mHandler == null) mHandler = Handler()
        mHandler!!.post(mUpdateCounters)
        mBtnPlayPause!!.setImageDrawable(mPauseDrawable)
    }

    override fun seekTo(@IntRange(from = 0, to = Int.MAX_VALUE.toLong()) pos: Int) {
        if (mPlayer == null) return
        mPlayer!!.seekTo(pos)
    }

    override fun setVolume(
        @FloatRange(from = 0.0, to = 1.0) leftVolume: Float,
        @FloatRange(from = 0.0, to = 1.0) rightVolume: Float
    ) {
        check(!(mPlayer == null || !mIsPrepared)) { "You cannot use setVolume(float, float) until the player is prepared." }
        mPlayer!!.setVolume(leftVolume, rightVolume)
    }

    override fun pause() {
        if (mPlayer == null || !isPlaying) return
        mPlayer!!.pause()
        if (mCallback != null) mCallback!!.onPaused(this)
        if (mHandler == null) return
        mHandler!!.removeCallbacks(mUpdateCounters)
        mBtnPlayPause!!.setImageDrawable(mPlayDrawable)
    }

    override fun stop() {
        if (mPlayer == null) return
        try {
            mPlayer!!.stop()
        } catch (ignored: Throwable) {
        }
        if (mHandler == null) return
        mHandler!!.removeCallbacks(mUpdateCounters)
        mBtnPlayPause!!.setImageDrawable(mPlayDrawable)
    }

    override fun reset() {
        if (mPlayer == null) return
        mIsPrepared = false
        mPlayer!!.reset()
        mIsPrepared = false
    }

    override fun release() {
        mIsPrepared = false
        if (mPlayer != null) {
            try {
                mPlayer!!.release()
            } catch (ignored: Throwable) {
            }
            mPlayer = null
        }
        if (mHandler != null) {
            mHandler!!.removeCallbacks(mUpdateCounters)
            mHandler = null
        }
        LOG("Released player and Handler")
    }

    override fun setAutoFullscreen(autoFullscreen: Boolean) {
        mAutoFullscreen = autoFullscreen
    }

    override fun setLoop(loop: Boolean) {
        mLoop = loop
        if (mPlayer != null) mPlayer!!.isLooping = loop
    }

    // Surface listeners
    override fun onSurfaceTextureAvailable(
        surfaceTexture: SurfaceTexture,
        width: Int,
        height: Int
    ) {
        LOG("Surface texture available: %dx%d", width, height)
        mInitialTextureWidth = width
        mInitialTextureHeight = height
        mSurfaceAvailable = true
        mSurface = Surface(surfaceTexture)
        if (mIsPrepared) {
            mPlayer!!.setSurface(mSurface)
            if ("oppo" == Build.BRAND.lowercase(Locale.getDefault()) && "OPPO R9sk" == Build.MODEL) {
                mPlayer!!.seekTo(currentPosition)
            }
        } else {
            prepare()
        }
    }

    override fun onSurfaceTextureSizeChanged(
        surfaceTexture: SurfaceTexture,
        width: Int,
        height: Int
    ) {
        LOG("Surface texture changed: %dx%d", width, height)
        adjustAspectRatio(width, height, mPlayer!!.videoWidth, mPlayer!!.videoHeight)
    }

    override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
        LOG("Surface texture destroyed")
        if ("oppo" == Build.BRAND.lowercase(Locale.getDefault()) && "OPPO R9sk" == Build.MODEL) {
            currentPos = currentPosition
        }
        mSurfaceAvailable = false
        mSurface = null
        return false
    }

    override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {}
    override fun onPrepared(mediaPlayer: MediaPlayer) {
        LOG("onPrepared()")
        mIsPrepared = true
        if (mCallback != null) mCallback!!.onPrepared(this)
        mLabelPosition!!.text = Util.getDurationString(0, false)
        mLabelDuration!!.text = Util.getDurationString(mediaPlayer.duration.toLong(), false)
        mSeeker!!.progress = 0
        mSeeker!!.max = mediaPlayer.duration
        setControlsEnabled(true)
        if (mAutoPlay) {
            if (!mControlsDisabled && mHideControlsOnPlay) hideControls()
            start()
            if (mInitialPosition > 0) {
                seekTo(mInitialPosition)
                mInitialPosition = -1
            }
        } else {
            // Hack to show first frame, is there another way?
            mPlayer!!.start()
            mPlayer!!.pause()
            mBtnPlayPause!!.setImageDrawable(mPlayDrawable)
            mBtnPlayPause!!.visibility = VISIBLE
        }
    }

    override fun onBufferingUpdate(mediaPlayer: MediaPlayer, percent: Int) {
        LOG("Buffering: %d%%", percent)
        if (mCallback != null) mCallback!!.onBuffering(percent)
        if (mSeeker != null) {
            if (percent == 100) mSeeker!!.secondaryProgress = 0 else mSeeker!!.secondaryProgress =
                mSeeker!!.max * (percent / 100)
        }
    }

    override fun onCompletion(mediaPlayer: MediaPlayer) {
        LOG("onCompletion()")
        if ("oppo" == Build.BRAND.lowercase(Locale.getDefault()) && "OPPO R9sk" == Build.MODEL) {
            currentPos = 0
        }
        if (mLoop) {
            mBtnPlayPause!!.setImageDrawable(mPlayDrawable)
            if (mHandler != null) mHandler!!.removeCallbacks(mUpdateCounters)
            mSeeker!!.progress = mSeeker!!.max
            showControls()
        } else {
            seekTo(0)
            mSeeker!!.progress = 0
            mLabelPosition!!.text = Util.getDurationString(0, false)
            mBtnPlayPause!!.setImageDrawable(mPlayDrawable)
            showControls()
        }
        if (mCallback != null) {
            mCallback!!.onCompletion(this)
            if (mLoop) mCallback!!.onStarted(this)
        }
    }

    override fun onVideoSizeChanged(mediaPlayer: MediaPlayer, width: Int, height: Int) {
        LOG("Video size changed: %dx%d", width, height)
        adjustAspectRatio(mInitialTextureWidth, mInitialTextureHeight, width, height)
    }

    override fun onError(mediaPlayer: MediaPlayer, what: Int, extra: Int): Boolean {
        if (what == -38) {
            // Error code -38 happens on some Samsung devices
            // Just ignore it
            return false
        }
        var errorMsg = "Preparation/playback error ($what): "
        errorMsg += when (what) {
            MediaPlayer.MEDIA_ERROR_IO -> "I/O error"
            MediaPlayer.MEDIA_ERROR_MALFORMED -> "Malformed"
            MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK -> "Not valid for progressive playback"
            MediaPlayer.MEDIA_ERROR_SERVER_DIED -> "Server died"
            MediaPlayer.MEDIA_ERROR_TIMED_OUT -> "Timed out"
            MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> "Unsupported"
            else -> "Unknown error"
        }
        throwError(Exception(errorMsg))
        return false
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (isInEditMode) {
            return
        }
        keepScreenOn = true
        mHandler = Handler()
        mPlayer = MediaPlayer()
        mPlayer!!.setOnPreparedListener(this)
        mPlayer!!.setOnBufferingUpdateListener(this)
        mPlayer!!.setOnCompletionListener(this)
        mPlayer!!.setOnVideoSizeChangedListener(this)
        mPlayer!!.setOnErrorListener(this)
        mPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mPlayer!!.isLooping = mLoop
        initTextureView()
        initClickFrame()
        initPlayButton()
        initControlsFrame()
        if (mControlsDisabled) {
            mClickFrame!!.setOnClickListener(null)
            mControlsFrame!!.visibility = GONE
        } else {
            mClickFrame!!.setOnClickListener {
                toggleControls()
                if (mCallback != null) {
                    mCallback!!.onClickVideoFrame(this@BenVideoPlayer)
                }
            }
        }
        invalidateThemeColors()
        setControlsEnabled(false)
        prepare()
    }

    private fun initTextureView() {
        // Instantiate and add TextureView for rendering
        val textureLp =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        mTextureView = TextureView(context)
        addView(mTextureView, textureLp)
        mTextureView!!.surfaceTextureListener = this
    }

    private fun initPlayButton() {
        mBtnPlayPause = ImageButton(context)
        val params =
            LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER
        mBtnPlayPause!!.setImageDrawable(mPauseDrawable)
        mBtnPlayPause!!.setOnClickListener(this)
        addView(mBtnPlayPause, params)
        mBtnPlayPause!!.id = R.id.btnPlayPause
        mBtnPlayPause!!.visibility = VISIBLE
    }

    private fun initControlsFrame() {
        val li = LayoutInflater.from(context)
        // Inflate controls
        mControlsFrame = li.inflate(R.layout.ben_include_controls, this, false)
        val controlsLp =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        controlsLp.gravity = Gravity.BOTTOM
        addView(mControlsFrame, controlsLp)

        // Retrieve controls
        mSeeker = mControlsFrame!!.findViewById<View>(R.id.seeker) as SeekBar
        mSeeker!!.setOnSeekBarChangeListener(this)
        mLabelPosition = mControlsFrame!!.findViewById<View>(R.id.position) as TextView
        mLabelPosition!!.text = Util.getDurationString(0, false)
        mLabelDuration = mControlsFrame!!.findViewById<View>(R.id.duration) as TextView
        mLabelDuration!!.text = Util.getDurationString(0, false)
    }

    private fun initClickFrame() {
        // Instantiate and add click frame (used to toggle controls)
        mClickFrame = FrameLayout(context)
        addView(
            mClickFrame, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    override fun onClick(view: View) {
        if (view.id == R.id.btnPlayPause) {
            if (mPlayer!!.isPlaying) {
                pause()
            } else {
                if (mHideControlsOnPlay && !mControlsDisabled) {
                    hideControls()
                }
                start()
            }
        }
    }

    override fun onProgressChanged(seekBar: SeekBar, value: Int, fromUser: Boolean) {
        if (fromUser) seekTo(value)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        mWasPlaying = isPlaying
        if (mWasPlaying) mPlayer!!.pause() // keeps the time updater running, unlike pause()
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        if (mWasPlaying) mPlayer!!.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        LOG("Detached from window")
        release()
        mSeeker = null
        mLabelPosition = null
        mLabelDuration = null
        mBtnPlayPause = null
        mControlsFrame = null
        mClickFrame = null
        if (mHandler != null) {
            mHandler!!.removeCallbacks(mUpdateCounters)
            mHandler = null
        }
    }

    private fun adjustAspectRatio(
        viewWidth: Int,
        viewHeight: Int,
        videoWidth: Int,
        videoHeight: Int
    ) {
        val aspectRatio = videoHeight.toDouble() / videoWidth
        val newWidth: Int
        val newHeight: Int
        if (viewHeight > (viewWidth * aspectRatio).toInt()) {
            // limited by narrow width; restrict height
            newWidth = viewWidth
            newHeight = (viewWidth * aspectRatio).toInt()
        } else {
            // limited by short height; restrict width
            newWidth = (viewHeight / aspectRatio).toInt()
            newHeight = viewHeight
        }
        val xoff = (viewWidth - newWidth) / 2
        val yoff = (viewHeight - newHeight) / 2
        val txform = Matrix()
        mTextureView!!.getTransform(txform)
        txform.setScale(newWidth.toFloat() / viewWidth, newHeight.toFloat() / viewHeight)
        txform.postTranslate(xoff.toFloat(), yoff.toFloat())
        mTextureView!!.setTransform(txform)
    }

    private fun throwError(e: Exception) {
        if (mCallback != null) mCallback!!.onError(this, e) else throw RuntimeException(e)
    }

    private fun tintDrawable(d: Drawable, @ColorInt color: Int): Drawable {
        var d = d
        d = DrawableCompat.wrap(d.mutate())
        DrawableCompat.setTint(d, color)
        return d
    }

    private fun tintSelector(view: View, @ColorInt color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
            && view.background is RippleDrawable
        ) {
            val rd = view.background as RippleDrawable
            rd.setColor(ColorStateList.valueOf(Util.adjustAlpha(color, 0.3f)))
        }
    }

    private fun invalidateThemeColors() {
        val labelColor = if (Util.isColorDark(mThemeColor)) Color.WHITE else Color.BLACK
        mControlsFrame!!.setBackgroundColor(Util.adjustAlpha(mThemeColor, 0.8f))
        tintSelector(mBtnPlayPause!!, labelColor)
        mLabelDuration!!.setTextColor(labelColor)
        mLabelPosition!!.setTextColor(labelColor)
        setTint(mSeeker!!, labelColor)
        mPlayDrawable = tintDrawable(mPlayDrawable!!.mutate(), labelColor)
        mPauseDrawable = tintDrawable(mPauseDrawable!!.mutate(), labelColor)
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private fun setFullscreen(fullscreen: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (mAutoFullscreen) {
                var flags = if (!fullscreen) 0 else SYSTEM_UI_FLAG_LOW_PROFILE
                ViewCompat.setFitsSystemWindows(mControlsFrame, !fullscreen)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    flags = flags or (SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or SYSTEM_UI_FLAG_LAYOUT_STABLE)
                    if (fullscreen) {
                        flags = flags or (SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                or SYSTEM_UI_FLAG_FULLSCREEN
                                or SYSTEM_UI_FLAG_IMMERSIVE)
                    }
                }
                mClickFrame!!.systemUiVisibility = flags
            }
        }
    }

    companion object {
        private const val UPDATE_INTERVAL = 100
        private fun LOG(message: String, vararg args: Any) {
            var message: String? = message
            try {
                if (args != null) message = String.format(message!!, *args)
                Log.d("BenVideoPlayer", message!!)
            } catch (ignored: Exception) {
            }
        }

        private fun setTint(seekBar: SeekBar, @ColorInt color: Int) {
            val s1 = ColorStateList.valueOf(color)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                seekBar.thumbTintList = s1
                seekBar.progressTintList = s1
                seekBar.secondaryProgressTintList = s1
            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                val progressDrawable = DrawableCompat.wrap(seekBar.progressDrawable)
                seekBar.progressDrawable = progressDrawable
                DrawableCompat.setTintList(progressDrawable, s1)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    val thumbDrawable = DrawableCompat.wrap(seekBar.thumb)
                    DrawableCompat.setTintList(thumbDrawable, s1)
                    seekBar.thumb = thumbDrawable
                }
            } else {
                var mode = PorterDuff.Mode.SRC_IN
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
                    mode = PorterDuff.Mode.MULTIPLY
                }
                if (seekBar.indeterminateDrawable != null) seekBar.indeterminateDrawable.setColorFilter(
                    color,
                    mode
                )
                if (seekBar.progressDrawable != null) seekBar.progressDrawable.setColorFilter(
                    color,
                    mode
                )
            }
        }
    }
}