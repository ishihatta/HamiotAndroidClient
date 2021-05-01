package com.ishihata_tech.hamiot_client

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ishihata_tech.hamiot_client.notification.MyNotificationChannel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FcmService : FirebaseMessagingService() {
    companion object {
        private const val TAG = "FcmService"

        private const val NOTIFICATION_ID = 1
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "onMessageReceived: data=${remoteMessage.data}")

        remoteMessage.data["type"]?.also { type ->
            when (type) {
                "ReceiveAsset" -> { // 送金を受け取った
                    val amount = remoteMessage.data["amount"]
                    val opponentDisplayName = remoteMessage.data["opponentDisplayName"]
                    if (amount != null && opponentDisplayName != null) {
                        createNotification(
                                getString(R.string.notification_title_when_received),
                                getString(R.string.notification_destination_when_received).format(
                                        amount, opponentDisplayName
                                )
                        )
                    }
                    notifyRefreshBalance()
                }
                "SentAsset" -> {    // 送金に成功した
                    val amount = remoteMessage.data["amount"]
                    val opponentDisplayName = remoteMessage.data["opponentDisplayName"]
                    if (amount != null && opponentDisplayName != null) {
                        createNotification(
                                getString(R.string.notification_title_when_sent),
                                getString(R.string.notification_destination_when_sent).format(
                                        amount, opponentDisplayName
                                )
                        )
                    }
                    notifyRefreshBalance()
                }
            }
        }
    }

    /**
     * 残高が変わったことをブロードキャストする
     */
    private fun notifyRefreshBalance() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(
                Intent(Constants.ACTION_REFRESH_BALANCE)
        )
    }

    /**
     * 通知を表示する
     *
     * @param title 通知のタイトル
     * @param description 通知に表示するテキスト
     */
    private fun createNotification(title: String, description: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, MyNotificationChannel.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(description)
                .setStyle(NotificationCompat.BigTextStyle().bigText(description))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }
}