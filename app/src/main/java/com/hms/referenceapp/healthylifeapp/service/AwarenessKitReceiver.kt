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

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock

class AwarenessKitReceiver:BroadcastReceiver() {

    private val PERIOD = 900000
    private val INITIAL_DELAY = 0

    override fun onReceive(ctxt: Context, i: Intent) {
        if (i.action == null) {
            AwarenessKitScheduledService.enqueueWork(ctxt)
        } else {
            scheduleAlarms(ctxt)
        }
    }

    fun scheduleAlarms(context: Context) {
        val mgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val i = Intent(context, AwarenessKitReceiver::class.java)
        val pi = PendingIntent.getBroadcast(context, 0, i, 0)
        mgr.setRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + INITIAL_DELAY,
            PERIOD.toLong(), pi
        )
    }
}