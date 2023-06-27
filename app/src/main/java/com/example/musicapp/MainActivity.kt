package com.example.musicapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.musicapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var musicService: MusicService
    private var musicBound: Boolean = false
    private var playIntent: Intent? = null
    private lateinit var mediaSession: MediaSessionCompat
    private var isPlaying: Boolean = false

    private var serviceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            musicBound = true
            createNotificationChannel()
            showNotification()


        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicBound = false
        }

    }

    companion object {
        private const val CHANNEL_ID = "music_channel_id"
        private const val NOTIFICATION_ID = 1
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        mediaSession = MediaSessionCompat(this, "AudioPlayer")
        binding.textViewSongName.text = resources.getResourceEntryName(R.raw.song_one)

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!checkPermissionGranted(Manifest.permission.POST_NOTIFICATIONS)) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        if (playIntent == null) {
            playIntent = Intent(this, MusicService::class.java)
            bindService(playIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

        binding.nextPlayerButton.setOnClickListener {
            musicService.nextSong()
            binding.textViewSongName.text = getSongTitle(musicService.getSongPos())
            if (binding.startPlayerButton.visibility == View.VISIBLE) {
                showPauseButton()
            }

        }

        binding.previousPlayerButton.setOnClickListener {
            musicService.previousSong()
            binding.textViewSongName.text = getSongTitle(musicService.getSongPos())
            if (binding.startPlayerButton.visibility == View.VISIBLE) {
                showPlayButton()
            }

        }

        binding.startPlayerButton.setOnClickListener {
            musicService.start()
            showPauseButton()

        }

        binding.pausePlayerButton.setOnClickListener {
            musicService.pause()
            showPlayButton()

        }

        createNotificationChannel()
    }

    private fun getSongTitle(songPosition: Int) : String {
        return when(songPosition) {
            0 -> resources.getResourceEntryName(R.raw.song_one)
            1 -> resources.getResourceEntryName(R.raw.song_two)
            2 -> resources.getResourceEntryName(R.raw.song_three)
            else -> "Unknown name"
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(CHANNEL_ID, "Music Controller", NotificationManager.IMPORTANCE_HIGH)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
    private fun showNotification() {
        val intent = Intent(this, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val previousIntent = Intent(this, MusicService::class.java).setAction("PREVIOUS")
        val previousPendingIntent = PendingIntent.getService(this, 0, previousIntent, PendingIntent.FLAG_IMMUTABLE)

        val playIntent = Intent(this, MusicService::class.java).setAction("PLAY")
        val playPendingIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_IMMUTABLE)

        val pauseIntent = Intent(this, MusicService::class.java).setAction("PAUSE")
        val pausePendingIntent = PendingIntent.getService(this, 0, pauseIntent, PendingIntent.FLAG_IMMUTABLE)

        val nextIntent = Intent(this, MusicService::class.java).setAction("NEXT")
        val nextPendingIntent = PendingIntent.getService(this, 0, nextIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_music_note_24)
            .addAction(R.drawable.back_icon, "Previous", previousPendingIntent)
            .addAction(R.drawable.start_icon, "Play", playPendingIntent)
            .addAction(R.drawable.pause_icon, "Pause", pausePendingIntent)
            .addAction(R.drawable.next_icon, "Next", nextPendingIntent)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle())
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(contentIntent)
            .setOnlyAlertOnce(true)
            .build()
        val notificationManager: NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notification)
    }

    private fun checkPermissionGranted(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }

    private fun showPauseButton() {
        binding.startPlayerButton.visibility = View.GONE
        binding.pausePlayerButton.visibility = View.VISIBLE
    }

    private fun showPlayButton() {
        binding.pausePlayerButton.visibility = View.GONE
        binding.startPlayerButton.visibility = View.VISIBLE
    }

}