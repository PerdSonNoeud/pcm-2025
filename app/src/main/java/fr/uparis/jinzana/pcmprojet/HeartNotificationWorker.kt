package fr.uparis.jinzana.pcmprojet

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import fr.uparis.jinzana.pcmprojet.HeartRelaod.HEART_RECHARGE_TIME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HeartNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        val db = SettingsDB.getInstance(applicationContext)
        val dao = db.settingsDao()

        var settings = dao.getSettings() ?: return@withContext Result.success()

        Log.d("HeartWorker", "Worker started with hearts=${settings.currentHearts}")


        settings = rechargeHeartsIfNeeded(settings, dao)

        if (!settings.premium &&
            ( settings.currentHearts == settings.maxHearts) && settings.notifIsActive

        ) {
            sendNotification("Vos cœurs sont rechargés !")
            val updated = settings.copy(notifIsActive = false)
            dao.insertSettings(updated)
            Log.d("HeartWorker", "Notification envoyée")

        }

        return@withContext Result.success()
    }

    private suspend fun rechargeHeartsIfNeeded(
        settings: SettingsEntity,
        dao: SettingsDao
    ): SettingsEntity {

        if (settings.currentHearts >= settings.maxHearts) {
            return settings.copy(lastPlayTime = System.currentTimeMillis())
                .also { dao.insertSettings(it) }
        }

        val now = System.currentTimeMillis()
        val elapsed = now - settings.lastPlayTime
        val heartsToAdd = (elapsed / HEART_RECHARGE_TIME).toInt()

        if (heartsToAdd <= 0) return settings

        val newHearts = minOf(settings.currentHearts + heartsToAdd, settings.maxHearts)
        val newLastTime = settings.lastPlayTime + heartsToAdd * HEART_RECHARGE_TIME

        val updated = settings.copy(
            currentHearts = newHearts,
            lastPlayTime = newLastTime
        )

        dao.insertSettings(updated)
        return updated
    }

    private fun sendNotification(message: String) {
        val channelId = "heart_channel"
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Notifications Coeurs",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Coeurs rechargés")
            .setContentText(message)
            .setSmallIcon(R.drawable.dracaufeu)
            .build()

        manager.notify(1, notification)
    }
}
