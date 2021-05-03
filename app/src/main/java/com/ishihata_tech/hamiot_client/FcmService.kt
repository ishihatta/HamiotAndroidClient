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
import com.ishihata_tech.hamiot_client.repo.UserAccountRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FcmService : FirebaseMessagingService() {
    companion object {
        private const val TAG = "FcmService"

        private const val NOTIFICATION_ID = 1
    }

    @Inject lateinit var userAccountRepository: UserAccountRepository

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "onMessageReceived: data=${remoteMessage.data}")

        remoteMessage.data["type"]?.also { type ->
            val amount = remoteMessage.data["amount"]
            val opponentDisplayName = remoteMessage.data["opponentDisplayName"]
            val yourAccountId = remoteMessage.data["yourAccountId"]
            if (amount != null && opponentDisplayName != null && yourAccountId != null) {
                onHamiotMessageReceived(type, amount, opponentDisplayName, yourAccountId)
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
     * HamIOT関連のメッセージを受信したときに呼ばれる
     */
    private fun onHamiotMessageReceived(
            type: String,
            amount: String,
            opponentDisplayName: String,
            yourAccountId: String
    ) {
        // アカウントIDがログイン中のアカウントと一致するか確認する
        if (yourAccountId != userAccountRepository.accountId) {
            Log.d(TAG, "onHamiotMessageReceived: accountId does not match")
            return
        }

        // 通知を表示する
        val (resIdTitle, resIdDescription) = if (type == "ReceiveAsset") {
            Pair(R.string.notification_title_when_received, R.string.notification_title_when_sent)
        } else {
            Pair(R.string.notification_destination_when_received, R.string.notification_destination_when_sent)
        }
        createNotification(
                getString(resIdTitle),
                getString(resIdDescription).format(
                        amount, opponentDisplayName
                )
        )

        // 残高が変わったことをブロードキャストする
        notifyRefreshBalance()
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
                .setSmallIcon(R.drawable.ic_notification)
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