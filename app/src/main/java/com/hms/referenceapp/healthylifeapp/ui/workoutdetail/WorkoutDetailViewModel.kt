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

package com.hms.referenceapp.healthylifeapp.ui.workoutdetail

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hms.referenceapp.healthylifeapp.*
import com.hms.referenceapp.healthylifeapp.analytics.Analytics
import com.huawei.hms.hihealth.ActivityRecordsController
import com.huawei.hms.hihealth.DataController
import com.huawei.hms.hihealth.data.ActivityRecord
import com.huawei.hms.hihealth.data.DataType
import com.huawei.hms.hihealth.options.ActivityRecordReadOptions
import com.huawei.hms.hihealth.options.DeleteOptions
import java.util.*
import java.util.concurrent.TimeUnit

class WorkoutDetailViewModel : ViewModel() {

    val isAddedSuccessfully = MutableLiveData<Boolean>()
    val isProgress = MutableLiveData<Boolean>()

    fun endActivityRecord(context: Context, workoutId: String, activityRecordsController: ActivityRecordsController) {

        val endTask = activityRecordsController.endActivityRecord(workoutId)

        endTask.addOnSuccessListener {
            // Return the list of activity records that have stopped

            val bundle = Bundle()
            bundle.putString(WORKOUT_ID, workoutId)
            Analytics(context).logEvent(END_WORKOUT, bundle)

            setIsAddedSuccessfully(true)

        }.addOnFailureListener { e ->
            setIsAddedSuccessfully(false)
        }
    }

    fun deleteWorkoutRecord(context: Context, workoutId: String, dataController: DataController, activityRecordsController: ActivityRecordsController) {

        setIsProgress(true)

        var deletedActivityRecord: ActivityRecord? = null

        // Build the time range of the request object: start time and end time
        val cal = Calendar.getInstance()
        val now = Date()
        cal.time = now
        val endTime = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, -100)
        val startTime = cal.timeInMillis

        // Build the request body for reading activity records
        val readRequest = ActivityRecordReadOptions.Builder()
            .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
            .readActivityRecordsFromAllApps()
            .read(DataType.DT_CONTINUOUS_STEPS_DELTA)
            .build()

        // Call the read method of the ActivityRecordsController to obtain activity records
        // from the Health platform based on the conditions in the request body
        val getTask = activityRecordsController.getActivityRecord(readRequest)

        getTask.addOnSuccessListener { activityRecordReply ->
            // Print ActivityRecord and corresponding activity data in the result
            val activityRecordList = activityRecordReply.activityRecords
            for (activityRecord in activityRecordList) {
                if (activityRecord != null) {
                    if (activityRecord.id == workoutId){
                        deletedActivityRecord = activityRecord
                        deleteActivityRecord(context, dataController, deletedActivityRecord!!)
                    }
                }
            }

        }.addOnFailureListener {

        }

    }

    private fun deleteActivityRecord(context: Context, dataController: DataController, deletedActivityRecord: ActivityRecord){
        val deleteOptions = DeleteOptions.Builder().addActivityRecord(deletedActivityRecord)
            .setTimeInterval(deletedActivityRecord.getStartTime(TimeUnit.MILLISECONDS), deletedActivityRecord.getEndTime(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS)
            .build()

        // Delete ActivityRecord
        val deleteTask = dataController.delete(deleteOptions)
        deleteTask.addOnSuccessListener {

            val bundle = Bundle()
            bundle.putString(WORKOUT_ID, deletedActivityRecord.id)
            bundle.putString(WORKOUT_NAME, deletedActivityRecord.name)
            bundle.putString(WORKOUT_TYPE, deletedActivityRecord.activityType)
            Analytics(context).logEvent(DELETE_WORKOUT, bundle)

            setIsAddedSuccessfully(true)
            setIsProgress(false)

        }.addOnFailureListener {
            setIsProgress(false)
        }
    }

    private fun setIsProgress(item: Boolean) {
        isProgress.value = item
    }

    private fun setIsAddedSuccessfully(item: Boolean) {
        isAddedSuccessfully.value = item
    }
}