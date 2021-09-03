/*
 * *
 *  *Copyright 2020. Explore in HMS. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 *
 *
 */

package com.hms.referenceapp.healthylifeapp.service


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.hms.referenceapp.healthylifeapp.*

import com.hms.referenceapp.healthylifeapp.R
import com.hms.referenceapp.healthylifeapp.data.model.BehaviorDataValue

import com.hms.referenceapp.healthylifeapp.ui.main.MainActivity
import com.huawei.hms.kit.awareness.Awareness
import com.huawei.hms.kit.awareness.status.BehaviorStatus

class AwarenessKitScheduledService : JobIntentService() {

    private var sharedPreferences: SharedPreferences? = null

    public override fun onHandleWork(i: Intent) {
        callBehaviorAwareness(this)
    }

    private fun callBehaviorAwareness(context: Context?) {
        sharedPreferences = context!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        var notificationCounter = sharedPreferences?.getInt(NOTIFICATION_COUNTER, 0)
        val periodCounter = sharedPreferences?.getInt(PERIOD_COUNTER, 2)

        val task = Awareness.getCaptureClient(context).behavior
        task.addOnSuccessListener { response ->
            val behaviorStatus: BehaviorStatus = response.behaviorStatus
            if (behaviorStatus.mostLikelyBehavior.type == BehaviorDataValue.BEHAVIOR_STILL.value) {
                notificationCounter = notificationCounter!! + 1

                sharedPreferences?.edit()!!.putInt(NOTIFICATION_COUNTER, notificationCounter!!)
                    .apply()
                if (notificationCounter!! > periodCounter!!) {

                    showNotification(this, getString(R.string.inactivity_title), getString(R.string.inactivity_notification))
                    sharedPreferences?.edit()!!.putInt(NOTIFICATION_COUNTER, 0)
                        .apply()
                }
            } else {
                sharedPreferences?.edit()!!.putInt(NOTIFICATION_COUNTER, 0).apply()
            }
        }.addOnFailureListener {
            Log.e("uyarı", it.message)
        } }

    private fun showNotification(
        context: Context?,
        title: String?,
        notificationDescription: String?
    ) {
        val channelId = "channelId"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channelName"
            val descriptionText = "channelDescp"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val builder = NotificationCompat.Builder(context!!, channelId)
            .setSmallIcon(R.drawable.icon)
            .setContentTitle(title)
            .setContentText(notificationDescription)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique
            notify(1, builder.build())
        }
    }

    companion object {
        private const val UNIQUE_JOB_ID = 1337
        var periodCounter = 2

        @JvmStatic
        fun enqueueWork(ctxt: Context) {
            enqueueWork(
                ctxt, AwarenessKitScheduledService::class.java, UNIQUE_JOB_ID,
                Intent(ctxt, AwarenessKitScheduledService::class.java)
            )
        }

    }
}


