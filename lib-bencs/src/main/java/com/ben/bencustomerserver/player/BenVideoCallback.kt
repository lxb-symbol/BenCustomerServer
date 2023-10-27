package com.ben.bencustomerserver.player

interface BenVideoCallback {
    fun onStarted(player: com.ben.bencustomerserver.player.BenVideoPlayer?)
    fun onPaused(player: com.ben.bencustomerserver.player.BenVideoPlayer?)
    fun onPreparing(player: com.ben.bencustomerserver.player.BenVideoPlayer?)
    fun onPrepared(player: com.ben.bencustomerserver.player.BenVideoPlayer?)
    fun onBuffering(percent: Int)
    fun onError(player: com.ben.bencustomerserver.player.BenVideoPlayer?, e: Exception?)
    fun onCompletion(player: com.ben.bencustomerserver.player.BenVideoPlayer?)
    fun onClickVideoFrame(player: com.ben.bencustomerserver.player.BenVideoPlayer?)
}