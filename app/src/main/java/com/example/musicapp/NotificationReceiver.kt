package com.example.musicapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class NotificationReceiver: BroadcastReceiver() {

    companion object {
        const val ACTION_NEXT = "NEXT"
        const val ACTION_PREVIOUS = "PREVIOUS"
        const val ACTION_PLAY = "PLAY"
        }

    override fun onReceive(context: Context?, intent: Intent?) {
        val serviceIntent = Intent(context, MusicService::class.java)

        if (intent?.action != null) {
            when (intent.action) {
                ACTION_PLAY -> {
                    serviceIntent.putExtra("action_name", intent.action)
                    context?.startService(serviceIntent)
                }

                ACTION_NEXT -> {
                    serviceIntent.putExtra("action_name", intent.action)
                    context?.startService(serviceIntent)
                }

                ACTION_PREVIOUS -> {
                    serviceIntent.putExtra("action_name", intent.action)
                    context?.startService(serviceIntent)
                }
            }

        }
    }
}