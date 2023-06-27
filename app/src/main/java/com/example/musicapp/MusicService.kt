package com.example.musicapp

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log

class MusicService: Service() {

    private lateinit var mPlayer: MediaPlayer
    private lateinit var songs: ArrayList<Int>
    private var songPos = 0
    private val musicBind: IBinder = MusicBinder()
    private var isPlaying: Boolean = false

    companion object {
        const val ACTION_NEXT = "NEXT"
        const val ACTION_PREVIOUS = "PREVIOUS"
        const val ACTION_PLAY = "PLAY"
        const val ACTION_PAUSE = "PAUSE"
    }

    override fun onCreate() {
        super.onCreate()

        songs = arrayListOf(R.raw.song_one, R.raw.song_two, R.raw.song_three)
        mPlayer = MediaPlayer.create(applicationContext, songs[songPos])
    }

    override fun onBind(intent: Intent?): IBinder {
        return musicBind
    }

    fun previousSong() {
        if (songPos > 0) {
            songPos--
        } else {
            songPos = songs.size - 1
        }

        mPlayer.stop()
        mPlayer = MediaPlayer.create(applicationContext, songs[songPos])
        mPlayer.start()
        isPlaying = true
    }

    fun nextSong() {
        if (songPos < songs.size - 1) {
            songPos++
        } else {
            songPos = 0
        }

        mPlayer.stop()
        mPlayer = MediaPlayer.create(applicationContext, songs[songPos])
        mPlayer.start()
        isPlaying = true
    }

    fun start() {
        mPlayer.start()
        isPlaying = true
    }

    fun pause() {
        mPlayer.pause()
        isPlaying = false
    }

    inner class MusicBinder : Binder() {
            fun getService(): MusicService = this@MusicService
    }

    fun getSongPos() : Int {
        return songPos
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val actionName = intent?.action
        if (actionName != null) {
            when (actionName) {
                ACTION_PLAY -> {
                    start()
                }

                ACTION_PAUSE -> {
                    pause()
                }

                ACTION_NEXT -> {
                    nextSong()
                }

                ACTION_PREVIOUS -> {
                    previousSong()
                }
            }
        }
        return START_STICKY
    }
}