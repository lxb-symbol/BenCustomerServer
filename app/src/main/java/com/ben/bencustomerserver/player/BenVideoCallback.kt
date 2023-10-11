package com.ben.bencustomerserver.player

interface BenVideoCallback {
    fun onStarted(player: BenVideoPlayer?)
    fun onPaused(player: BenVideoPlayer?)
    fun onPreparing(player: BenVideoPlayer?)
    fun onPrepared(player: BenVideoPlayer?)
    fun onBuffering(percent: Int)
    fun onError(player: BenVideoPlayer?, e: Exception?)
    fun onCompletion(player: BenVideoPlayer?)
    fun onClickVideoFrame(player: BenVideoPlayer?)
}