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

package com.hms.referenceapp.healthylifeapp.ui.main.step

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hms.referenceapp.healthylifeapp.adapter.StepAdapter
import com.hms.referenceapp.healthylifeapp.data.model.Step
import com.huawei.hms.common.ApiException
import com.huawei.hms.hihealth.DataController
import com.huawei.hms.hihealth.HiHealthStatusCodes
import com.huawei.hms.hihealth.data.DataType
import com.huawei.hms.hihealth.data.SampleSet
import com.huawei.hms.hihealth.options.ReadOptions
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern


class StepViewModel : ViewModel() {

    private var dataController :DataController? =null
    private var adapter: StepAdapter? = null

    private val todayStepData = MutableLiveData<String>()
    val todayStepDataInt = MutableLiveData<Int>()
    private val todayCalorieData = MutableLiveData<String>()

    fun init(StepAdapter: StepAdapter) {
        this.adapter = StepAdapter
    }

    private fun setStepAdapter(steps: MutableList<Step>) {
        adapter?.setStepList(steps)
    }

    fun readToday() {
          val todaySummaryTask = dataController!!.readTodaySummation(DataType.DT_CONTINUOUS_STEPS_DELTA)

          todaySummaryTask.addOnSuccessListener { sampleSet ->

              if (sampleSet != null) {
                  getTodayData(sampleSet)
              }
          }
    }
    fun readDaily(startTime: Int, endTime: Int) {

        val daliySummationTask =
            dataController!!.readDailySummation(
                DataType.DT_CONTINUOUS_STEPS_DELTA,
                startTime,
                endTime
            )
        daliySummationTask.addOnSuccessListener { sampleSet ->
            sampleSet?.let { showSampleSet(it) }
        }
        daliySummationTask.addOnFailureListener { e ->
            val errorCode: String? = e.message
            val pattern: Pattern = Pattern.compile("[0-9]*")
            val isNum: Matcher = pattern.matcher(errorCode)
            when {
                e is ApiException -> {
                    val eCode = e.statusCode
                    val errorMsg = HiHealthStatusCodes.getStatusCodeMessage(eCode)
                }
                isNum.matches() -> {
                    val errorMsg =
                        errorCode?.toInt()?.let { HiHealthStatusCodes.getStatusCodeMessage(it) }
                }
                else -> {
                }
            }
        }
    }
    fun getActivityRecord() {

        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
        val startDate: Date = dateFormat.parse("2020-09-01 00:00:00")!!
        val endDate: Date = dateFormat.parse("2020-11-20 23:59:59")!!

        val readOptions = ReadOptions.Builder()
            .read(DataType.DT_CONTINUOUS_STEPS_DELTA)
            .setTimeRange(
                startDate.time,
                endDate.time,
                TimeUnit.MILLISECONDS
            )
            .build()

        val readReplyTask = dataController!!.read(readOptions)
        readReplyTask.addOnSuccessListener { readReply ->
            Log.i("TAG", "Success read an SampleSets from HMS core")
            for (sampleSet in readReply.getSampleSets()) {
                showSampleSet(sampleSet)

            }

        }.addOnFailureListener { e ->
            val errorCode: String? = e.message
            val pattern: Pattern = Pattern.compile("[0-9]*")
            val isNum: Matcher = pattern.matcher(errorCode)
            when {
                e is ApiException -> {
                    val eCode = e.statusCode
                    val errorMsg = HiHealthStatusCodes.getStatusCodeMessage(eCode)

                }
                isNum.matches() -> {
                    val errorMsg =
                        errorCode?.toInt()?.let { HiHealthStatusCodes.getStatusCodeMessage(it) }

                }
                else -> {

                }
            }
        }
    }
    fun getAdapter(): StepAdapter? {
        return adapter
    }

    fun getTodayStepData(): LiveData<String?>? {
        return todayStepData
    }

    fun getTodayCalorieData(): LiveData<String?>? {
        return todayCalorieData
    }

    private fun showSampleSet(sampleSet: SampleSet) {
        val stepList = ArrayList<Step>()
        for (samplePoint in sampleSet.samplePoints) {
            for (field in samplePoint.dataType.fields) {


                val step = Step(
                    stepCount = samplePoint.getFieldValue(field).asIntValue(),
                    addedTime = samplePoint.getStartTime(TimeUnit.MILLISECONDS),
                    calorie = (samplePoint.getFieldValue(field).asIntValue() * 0.05).toInt()
                )
                stepList.add(step)
            }
        }

        Collections.reverse(stepList)
        setStepAdapter(stepList)
    }

    private fun getTodayData(sampleSet: SampleSet) {
        val stepList = ArrayList<Step>()
        for (samplePoint in sampleSet.samplePoints) {
            for (field in samplePoint.dataType.fields) {


                val step = Step(
                    stepCount = samplePoint.getFieldValue(field).asIntValue(),
                    addedTime = samplePoint.getStartTime(TimeUnit.MILLISECONDS),
                    calorie = (samplePoint.getFieldValue(field).asIntValue() * 0.05).toInt()
                )
                stepList.add(step)
            }
        }

        if (stepList.size == 0){
            return
        }

        todayStepData.value = stepList[0].stepCount.toString() + "/10000"
        todayStepDataInt.value = stepList[0].stepCount
        todayCalorieData.value = "Calorie : " + stepList[0].calorie.toString() + " cal"
    }

    fun setDataController(dataController: DataController){
        this.dataController = dataController
    }

}
