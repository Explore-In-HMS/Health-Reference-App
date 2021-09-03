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

import android.app.Activity
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.hms.referenceapp.healthylifeapp.R
import com.hms.referenceapp.healthylifeapp.analytics.Analytics
import com.hms.referenceapp.healthylifeapp.data.model.ActivityBuilder
import com.hms.referenceapp.healthylifeapp.databinding.ActivityAddWorkoutBinding
import com.hms.referenceapp.healthylifeapp.util.*
import com.huawei.hms.hihealth.ActivityRecordsController
import com.huawei.hms.hihealth.HiHealthOptions
import com.huawei.hms.hihealth.HuaweiHiHealth
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import java.util.*


class AddWorkoutActivity : AppCompatActivity() {

    private val addWorkoutViewModel by lazy { ViewModelProvider(
        this, InjectorUtil.getAddWorkoutModelFactory()
    ).get(AddWorkoutViewModel::class.java) }

    private val binding by lazy { DataBindingUtil.setContentView<ActivityAddWorkoutBinding>(
        this,
        R.layout.activity_add_workout
    ) }

    private lateinit var backBtn: ImageButton
    private lateinit var saveBtn: ImageButton

    private lateinit var activityNameEditText: AppCompatEditText
    private lateinit var typeEditText: AppCompatEditText
    private lateinit var startTimeEditText: AppCompatEditText
    private lateinit var endTimeEditText: AppCompatEditText
    private lateinit var descriptionEditText: AppCompatEditText
    private lateinit var distanceEditText: AppCompatEditText

    private var startTime: Long = 0L
    private var endTime: Long = 0L

    // ActivityRecordsController for managing activity records
    private var activityRecordsController: ActivityRecordsController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Analytics(applicationContext).setScreenForAnalytics(
            ScreenType.ACTIVITY.screenType,
            AddWorkoutActivity::class.simpleName.toString()
        )

        binding.viewModel = addWorkoutViewModel
        binding.lifecycleOwner = this

        val hiHealthOptions = HiHealthOptions.builder().build()
        val signInHuaweiId = HuaweiIdAuthManager.getExtendedAuthResult(hiHealthOptions)

        activityRecordsController = HuaweiHiHealth.getActivityRecordsController(
            this,
            signInHuaweiId
        )

        addWorkoutViewModel.isAddedSuccessfully.observe(this, {
            if (it){
                val intent = Intent()
                setResult(Activity.RESULT_OK, intent)
                finish()
            }else{
                Toast.makeText(this, getString(R.string.add_workout_fail), Toast.LENGTH_SHORT).show()
            }
        })

        initView()

    }

    private fun initView(){

        backBtn = findViewById(R.id.backBtn)
        saveBtn = findViewById(R.id.saveBtn)

        activityNameEditText = findViewById(R.id.activityNameEditText)
        typeEditText = findViewById(R.id.typeEditText)
        startTimeEditText = findViewById(R.id.startTimeEditText)
        endTimeEditText = findViewById(R.id.endTimeEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        distanceEditText = findViewById(R.id.distanceEditText)

        typeEditText.setOnClickListener {
            showChooseActivityTypeDialog()
        }

        startTimeEditText.setOnClickListener{
            showChooseTimeDialog(TimeType.START_TIME)
        }

        endTimeEditText.setOnClickListener{
            showChooseTimeDialog(TimeType.END_TIME)
        }

        backBtn.setOnClickListener {
            finish()
        }

        saveBtn.setOnClickListener {

            val activityName = activityNameEditText.text.toString()
            val activityType = typeEditText.text.toString()
            val description = descriptionEditText.text.toString()

            val distance: Int = if (distanceEditText.text.toString().isEmpty()){ 0 }
            else{
                distanceEditText.text.toString().toInt()
            }

            val isValid = validateAddWorkoutInputs(activityName, activityType, distance)

            if (isValid){

                val activityBuilder: ActivityBuilder = ActivityBuilder.Builder()
                    .setActivityId(getRandomString(10))
                    .setActivityName(activityName)
                    .setActivityType(activityType)
                    .setActivityDescription(description)
                    .setStartTime(startTime)
                    .setEndTime(endTime)
                    .setDistance(distance)
                    .build()

                addWorkoutViewModel.addActivityRecord(activityRecordsController!!, activityBuilder, this)
            }
        }

        hideKeyboardWhenFocusGoes()
    }

    private fun validateAddWorkoutInputs(activityName: String, activityType: String, distance: Int): Boolean {

        var isValid = true

        if (activityName.isEmpty()) {
            Toast.makeText(
                this,
                getString(R.string.activity_name_cannot_be_blank),
                Toast.LENGTH_SHORT
            ).show()

            isValid = false
        }

        if (activityType.isEmpty()) {
            Toast.makeText(
                this,
                getString(R.string.activity_type_cannot_be_blank),
                Toast.LENGTH_SHORT
            ).show()

            isValid = false
        }

        if (startTime == 0L){
            Toast.makeText(
                this,
                getString(R.string.start_time_cannot_be_blank),
                Toast.LENGTH_SHORT
            ).show()

            isValid = false
        }

        if (endTime == 0L){
            Toast.makeText(
                this,
                getString(R.string.end_time_cannot_be_blank),
                Toast.LENGTH_SHORT
            ).show()

            isValid = false
        }

        val dateStartDate = Date(startTime)
        val dateEndTime = Date(endTime)

        if (dateStartDate.after(dateEndTime)){
            Toast.makeText(
                this,
                getString(R.string.start_date_cannot_be_after_end_date),
                Toast.LENGTH_SHORT
            ).show()

            isValid = false
        }

        if (distance < 20){
            Toast.makeText(
                this,
                getString(R.string.distance_cannot_be_blank),
                Toast.LENGTH_SHORT
            ).show()

            isValid = false
        }

        return isValid
    }

    private fun showChooseTimeDialog(timeType: TimeType) {
        val currentTime = Calendar.getInstance()

        val hour = currentTime[Calendar.HOUR_OF_DAY]
        val minute = currentTime[Calendar.MINUTE]
        val mTimePicker: TimePickerDialog
        mTimePicker = TimePickerDialog(
            this@AddWorkoutActivity,
            OnTimeSetListener { _, selectedHour, selectedMinute ->

                onTimeSelect(selectedHour, selectedMinute, timeType)

            }, hour, minute, true
        )

        mTimePicker.setTitle(getString(R.string.select_time))
        mTimePicker.show()
    }

    private fun showChooseActivityTypeDialog() {

        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle(getString(R.string.choose_activity_type))
        mBuilder.setSingleChoiceItems(listActivityTypes, -1) { dialogInterface, i ->

            val s = listActivityTypes[i]
            typeEditText.setText(s)

            dialogInterface.dismiss()
        }
        // Set the neutral/cancel button click listener
        mBuilder.setNeutralButton(getString(R.string.cancel)) { dialog, which ->
            // Do something when click the neutral button
            dialog.cancel()
        }

        val mDialog = mBuilder.create()
        mDialog.show()
    }

    private fun hideKeyboardWhenFocusGoes() {
        activityNameEditText.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        typeEditText.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        startTimeEditText.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        endTimeEditText.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        descriptionEditText.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        distanceEditText.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun onTimeSelect(hour: Int, minute: Int, timeType: TimeType) {

        val calendarLast = Calendar.getInstance()
        calendarLast[Calendar.HOUR_OF_DAY] = hour
        calendarLast[Calendar.MINUTE] = minute
        calendarLast[Calendar.SECOND] = 0
        calendarLast[Calendar.MILLISECOND] = 0

        val millis = calendarLast.timeInMillis

        when(timeType){
            TimeType.START_TIME -> startTime = millis
            TimeType.END_TIME -> endTime = millis
        }

        val hourString: String = if (hour < 10){
            "0$hour"
        }else{
            "$hour"
        }

        val minuteString: String = if (minute < 10){
            "0$minute"
        }else{
            "$minute"
        }

        val time = "$hourString : $minuteString"

        when(timeType){
            TimeType.START_TIME -> startTimeEditText.setText(time)
            TimeType.END_TIME -> endTimeEditText.setText(time)
        }
    }
}
