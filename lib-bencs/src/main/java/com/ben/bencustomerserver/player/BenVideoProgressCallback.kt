package com.ben.bencustomerserver.player

interface BenVideoProgressCallback {
    fun onVideoProgressUpdate(position: Int, duration: Int)
}