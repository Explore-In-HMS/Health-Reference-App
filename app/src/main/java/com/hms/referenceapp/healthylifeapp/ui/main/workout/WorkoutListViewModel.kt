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

package com.hms.referenceapp.healthylifeapp.ui.main.workout

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hms.referenceapp.healthylifeapp.adapter.WorkoutListAdapter
import com.hms.referenceapp.healthylifeapp.data.model.Workout
import com.hms.referenceapp.healthylifeapp.util.FilterType
import com.hms.referenceapp.healthylifeapp.util.WorkoutState
import com.huawei.hms.hihealth.ActivityRecordsController
import com.huawei.hms.hihealth.HiHealthActivities
import com.huawei.hms.hihealth.data.ActivityRecord
import com.huawei.hms.hihealth.data.DataType
import com.huawei.hms.hihealth.data.SamplePoint
import com.huawei.hms.hihealth.data.Value
import com.huawei.hms.hihealth.options.ActivityRecordReadOptions
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class WorkoutListViewModel : ViewModel() {

    private var adapter: WorkoutListAdapter? = null
    val isProgress = MutableLiveData<Boolean>()
    var isProgressLoaded: Boolean = false

    fun init(workoutListAdapter: WorkoutListAdapter) {
        this.adapter = workoutListAdapter
    }

    private fun setWorkoutListAdapter(workouts: MutableList<Workout>){

        val sortedList = workouts.sortedByDescending { it.startTime }

        adapter?.setWorkoutList(sortedList.toMutableList())
        adapter!!.notifyDataSetChanged()

    }

    fun getActivityRecord(activityRecordsController: ActivityRecordsController, filterType: String) {

        if (!isProgressLoaded){
            setIsProgress(true)
            isProgressLoaded = true
        }

        var dayDifference = 0
        when (filterType) {
            FilterType.WEEKLY.filterType -> {
                dayDifference = -7
            }
            FilterType.MONTHLY.filterType -> {
                dayDifference = -30
            }
            FilterType.YEARLY.filterType -> {
                dayDifference = -360
            }
        }

        val workoutList = ArrayList<Workout>()

        // Build the time range of the request object: start time and end time
        val cal = Calendar.getInstance()
        val now = Date()
        cal.time = now
        val endTime = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, dayDifference)
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

                    var state = ""
                    if (activityRecord.getEndTime(TimeUnit.MILLISECONDS) == 0L){
                        state = WorkoutState.STARTED.state
                    }else{
                        state = WorkoutState.FINISHED.state
                    }

                    val workout = Workout(
                        activityId = activityRecord.id,
                        name = activityRecord.name!!,
                        activityType = activityRecord.activityType,
                        description = activityRecord.desc,
                        startTime = activityRecord.getStartTime(TimeUnit.MILLISECONDS),
                        endTime = activityRecord.getEndTime(TimeUnit.MILLISECONDS),
                        workoutState = state,
                        pace = getPaceValue(activityRecord),
                        speed = getSpeedValue(activityRecord, activityRecord.getEndTime(TimeUnit.MILLISECONDS), activityRecord.getStartTime(TimeUnit.MILLISECONDS)),
                        calorie = getCalorieValue(activityRecord),
                        length = getDistanceValue(activityRecord)
                    )

                    if(workout.activityType != HiHealthActivities.SLEEP){
                        workoutList.add(workout)
                    }
                }
            }

            setWorkoutListAdapter(workoutList.toMutableList())

            setIsProgress(false)

        }.addOnFailureListener {
            setIsProgress(false)
        }
    }

    private fun getCalorieValue(activityRecord: ActivityRecord): Int {
        var calorie = 0

        if (activityRecord.activitySummary != null){

            val dataSummary = activityRecord.activitySummary.dataSummary
            for (samplePoint: SamplePoint in dataSummary) {
                for (entry: Map.Entry<String, Value> in samplePoint.fieldValues.entries) {
                    if (entry.key == "calories_total(f)"){
                        calorie = entry.value.asFloatValue().toInt()
                    }
                }
            }
        }

        return calorie
    }

    private fun getDistanceValue(activityRecord: ActivityRecord): Int {
        var distance = 0

        if (activityRecord.activitySummary != null){
            val dataSummary = activityRecord.activitySummary.dataSummary
            for (samplePoint: SamplePoint in dataSummary) {
                for (entry: Map.Entry<String, Value> in samplePoint.fieldValues.entries) {
                    if (entry.key == "distance(f)"){
                        distance = entry.value.asFloatValue().toInt()
                    }
                }
            }
        }

        return distance
    }

    private fun getSpeedValue(activityRecord: ActivityRecord, endTime: Long, startTime: Long): Double {

        var speed = 0.0

        if (activityRecord.activitySummary != null){
            val dataSummary = activityRecord.activitySummary.dataSummary

            for (samplePoint: SamplePoint in dataSummary) {
                for (entry: Map.Entry<String, Value> in samplePoint.fieldValues.entries) {
                    if (entry.key == "speed(f)"){
                        speed = entry.value.asFloatValue().toDouble()
                    }
                }
            }

            if (speed == 0.0 && endTime.toInt() != 0){
                speed = getDistanceValue(activityRecord).toDouble() / ((endTime - startTime) / 1000).toDouble()
            }
        }

        return speed
    }

    private fun getPaceValue(activityRecord: ActivityRecord): Double{

        if (activityRecord.activitySummary != null){
            val paceSummary = activityRecord.activitySummary.paceSummary
            return if (paceSummary != null && paceSummary.avgPace != null){
                paceSummary.avgPace
            }else{
                0.0
            }
        }
        return 0.0
    }

    private fun setIsProgress(item: Boolean) {
        isProgress.value = item
    }

    fun getAdapter(): WorkoutListAdapter? {
        return adapter
    }
}