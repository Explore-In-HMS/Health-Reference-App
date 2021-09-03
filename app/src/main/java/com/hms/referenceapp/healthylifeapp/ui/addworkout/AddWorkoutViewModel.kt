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

package com.hms.referenceapp.healthylifeapp.ui.addworkout

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hms.referenceapp.healthylifeapp.*
import com.hms.referenceapp.healthylifeapp.analytics.Analytics
import com.hms.referenceapp.healthylifeapp.data.model.ActivityBuilder
import com.huawei.hms.hihealth.ActivityRecordsController
import com.huawei.hms.hihealth.HiHealthActivities
import com.huawei.hms.hihealth.data.*
import com.huawei.hms.hihealth.options.ActivityRecordInsertOptions
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.pow

class AddWorkoutViewModel : ViewModel() {

    val isAddedSuccessfully = MutableLiveData<Boolean>()

    fun addActivityRecord(
        activityRecordsController: ActivityRecordsController,
        activityBuilder: ActivityBuilder,
        context: Context
    ) {

        val formatter: NumberFormat = DecimalFormat("#0.00")

        val avgSpeed = activityBuilder.distance.toDouble() / ((activityBuilder.endTime - activityBuilder.startTime) / 1000).toDouble()
        val avgPace = (activityBuilder.endTime - activityBuilder.startTime.toDouble()) / 1000 / 60 / (activityBuilder.distance.toDouble() / 1000)

        val calorie = calculateCalories(
            0f,
            activityBuilder.endTime - activityBuilder.startTime,
            72.2,
            formatter.format(avgSpeed).toDouble(),
            activityBuilder.activityType
        )

        val dataCollector: DataCollector =
            DataCollector.Builder().setDataType(DataType.DT_CONTINUOUS_STEPS_DELTA)
                .setDataGenerateType(DataCollector.DATA_TYPE_RAW)
                .setPackageName(context)
                .setDataCollectorName(DATA_COLLECTOR_NAME)
                .build()

        val activitySummary: ActivitySummary = getActivitySummary(avgPace)

        val dataCollectorStep: DataCollector =
            DataCollector.Builder()
                .setDataType(DataType.DT_CONTINUOUS_STEPS_TOTAL)
                .setDataGenerateType(DataCollector.DATA_TYPE_RAW)
                .setPackageName(context)
                .setDataCollectorName(DATA_COLLECTOR_NAME)
                .build()

        val dataCollectorCalorie: DataCollector =

            DataCollector.Builder()
                .setDataType(DataType.DT_CONTINUOUS_CALORIES_BURNT)
                .setDataGenerateType(DataCollector.DATA_TYPE_RAW)
                .setPackageName(context)
                .setDataCollectorName(DATA_COLLECTOR_NAME)
                .build()

        val dataCollectorDistance: DataCollector =
            DataCollector.Builder()
                .setDataType(DataType.DT_CONTINUOUS_DISTANCE_TOTAL)
                .setDataGenerateType(DataCollector.DATA_TYPE_RAW)
                .setPackageName(context)
                .setDataCollectorName(DATA_COLLECTOR_NAME)
                .build()

        val samplePointStep = SamplePoint.Builder(dataCollectorStep).build()
        samplePointStep.getFieldValue(Field.FIELD_STEPS).setIntValue(activityBuilder.distance)

        val samplePointCalorie = SamplePoint.Builder(dataCollectorCalorie).build()
        samplePointCalorie.getFieldValue(Field.FIELD_CALORIES).setDoubleValue(calorie.toDouble())

        val samplePointDistance = SamplePoint.Builder(dataCollectorDistance).build()
        samplePointDistance.getFieldValue(Field.FIELD_DISTANCE).setFloatValue(activityBuilder.distance.toFloat())

        activitySummary.dataSummary = listOf(samplePointStep, samplePointCalorie, samplePointDistance)

        val tz = TimeZone.getDefault()

        // Build the activity record request object
        val activityRecord = ActivityRecord.Builder()
            .setId(activityBuilder.activityId)
            .setName(activityBuilder.activityName)
            .setDesc(activityBuilder.activityDescription)
            .setActivityTypeId(activityBuilder.activityType)
            .setStartTime(activityBuilder.startTime, TimeUnit.MILLISECONDS)
            .setEndTime(activityBuilder.endTime, TimeUnit.MILLISECONDS)
            .setActivitySummary(activitySummary)
            .setTimeZone(tz.displayName)
            .setDurationTime(activityBuilder.endTime - activityBuilder.startTime, TimeUnit.MILLISECONDS)
            .build()

        // Build the sampling sampleSet based on the dataCollector

        val sampleSet = SampleSet.create(dataCollector)

        val samplePointDetail = sampleSet.createSamplePoint().setTimeInterval(
            activityBuilder.startTime,
            activityBuilder.endTime,
            TimeUnit.MILLISECONDS
        )
        samplePointDetail.getFieldValue(Field.FIELD_STEPS_DELTA).setIntValue(activityBuilder.distance)
        sampleSet.addSample(samplePointDetail)

        // Build the activity record addition request object
        val insertRequest = ActivityRecordInsertOptions.Builder()
            .setActivityRecord(activityRecord)
            .build()

        // Call the related method in the ActivityRecordsController to add activity records
        val addTask = activityRecordsController.addActivityRecord(insertRequest)

        addTask.addOnSuccessListener {

            val bundle = Bundle()
            bundle.putString(WORKOUT_ID, activityBuilder.activityId)
            bundle.putString(WORKOUT_NAME, activityBuilder.activityName)
            bundle.putString(WORKOUT_TYPE, activityBuilder.activityType)
            bundle.putLong(WORKOUT_START_TIME, activityBuilder.startTime)
            bundle.putLong(WORKOUT_END_TIME, activityBuilder.endTime)
            Analytics(context).logEvent(ADD_WORKOUT, bundle)

            setIsAddedSuccessfully(true)

        }.addOnFailureListener { e ->
            setIsAddedSuccessfully(false)
        }
    }

    private fun getActivitySummary(avgPace: Double): ActivitySummary {
        val activitySummary = ActivitySummary()
        val paceSummary = PaceSummary()
        paceSummary.avgPace = avgPace
        activitySummary.paceSummary = paceSummary
        return activitySummary
    }

    private fun calculateCalories(
        workoutAscent: Float,
        duration: Long,
        weight: Double,
        avgSpeed: Double,
        workoutType: String
    ): Int {
        val mins = (duration / 1000).toDouble() / 60
        val ascent = workoutAscent.toInt() // 1 calorie per meter
        return (mins * (getMET(avgSpeed, workoutType) * 3.5 * weight) / 200).toInt() + ascent
    }

    private fun getMET(avgSpeed: Double, workoutType: String): Double {
        val speedInKmh: Double = avgSpeed * 3.6
        return when (workoutType) {
            HiHealthActivities.RUNNING,
            HiHealthActivities.WALKING,
            HiHealthActivities.HIKING,
            HiHealthActivities.BASKETBALL, -> max(
                3.0,
                speedInKmh * 0.97
            )
            HiHealthActivities.CYCLING -> max(
                3.5,
                0.00818 * speedInKmh.pow(2.0) + 0.1925 * speedInKmh + 1.13
            )
            HiHealthActivities.SKATING -> max(3.0, 0.6747 * speedInKmh - 2.1893)
            HiHealthActivities.SKATEBOARDING -> max(4.0, 0.43 * speedInKmh + 0.89)
            HiHealthActivities.ROWING -> max(
                2.5,
                0.18 * speedInKmh.pow(2.0) - 1.375 * speedInKmh + 5.2
            )
            else -> 0.0
        }
    }

    private fun setIsAddedSuccessfully(item: Boolean) {
        isAddedSuccessfully.value = item
    }
}