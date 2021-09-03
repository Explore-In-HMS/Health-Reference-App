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

package com.hms.referenceapp.healthylifeapp.ui.startworkout

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hms.referenceapp.healthylifeapp.*
import com.hms.referenceapp.healthylifeapp.analytics.Analytics
import com.hms.referenceapp.healthylifeapp.data.model.ActivityBuilder
import com.huawei.hms.hihealth.ActivityRecordsController
import com.huawei.hms.hihealth.data.ActivityRecord
import java.util.concurrent.TimeUnit

class StartWorkoutViewModel : ViewModel() {

    val isAddedSuccessfully = MutableLiveData<Boolean>()

    fun beginActivityRecord(context: Context, activityBuilder: ActivityBuilder, activityRecordsController: ActivityRecordsController) {

        // Build the activity record request object
        val activityRecord = ActivityRecord.Builder()
            .setId(activityBuilder.activityId)
            .setName(activityBuilder.activityName)
            .setDesc(activityBuilder.activityDescription)
            .setActivityTypeId(activityBuilder.activityType)
            .setStartTime(activityBuilder.startTime, TimeUnit.MILLISECONDS)
            .build()

        // Add a listener for the ActivityRecord start success
        val beginTask = activityRecordsController.beginActivityRecord(activityRecord)

        // Add a listener for the ActivityRecord start failure
        beginTask.addOnSuccessListener {

            val bundle = Bundle()
            bundle.putString(WORKOUT_ID, activityBuilder.activityId)
            bundle.putString(WORKOUT_NAME, activityBuilder.activityName)
            bundle.putString(WORKOUT_TYPE, activityBuilder.activityType)
            Analytics(context).logEvent(START_WORKOUT, bundle)

            setIsAddedSuccessfully(true)

        }.addOnFailureListener { e ->
            setIsAddedSuccessfully(false)
        }
    }

    private fun setIsAddedSuccessfully(item: Boolean) {
        isAddedSuccessfully.value = item
    }
}