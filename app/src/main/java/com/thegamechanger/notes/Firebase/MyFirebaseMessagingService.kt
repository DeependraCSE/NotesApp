package com.thegamechanger.notes.Firebase

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.thegamechanger.notes.Activity.MainActivity
import com.thegamechanger.notes.Helper.SharedData
import com.thegamechanger.notes.R

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        var sharedData = SharedData(this)
        sharedData.setToken(token!!)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        createNotification(p0!!)
    }

    private fun createNotification(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        for (e in data.keys) {
            intent.putExtra(e, data[e])
        }
        val resultIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val mNotificationBuilder = NotificationCompat.Builder(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mNotificationBuilder.setSmallIcon(R.drawable.ic_notepad)
            mNotificationBuilder.color = resources.getColor(R.color.colorPrimary)
        } else {
            mNotificationBuilder.setSmallIcon(R.drawable.ic_notepad)
        }
        mNotificationBuilder.setContentTitle(remoteMessage.data["title"])
            .setContentText(remoteMessage.data["text"])
            .setSound(defaultSoundUri)
            .setVibrate(longArrayOf(500, 500))
            .setAutoCancel(true)
            .setContentIntent(resultIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, mNotificationBuilder.build())
    }
}