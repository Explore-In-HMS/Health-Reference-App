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

import android.app.Activity
import android.app.AlertDialog
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
import com.hms.referenceapp.healthylifeapp.databinding.ActivityStartWorkoutBinding
import com.hms.referenceapp.healthylifeapp.util.InjectorUtil
import com.hms.referenceapp.healthylifeapp.util.ScreenType
import com.hms.referenceapp.healthylifeapp.util.getRandomString
import com.hms.referenceapp.healthylifeapp.util.listActivityTypes
import com.huawei.hms.hihealth.ActivityRecordsController
import com.huawei.hms.hihealth.HiHealthOptions
import com.huawei.hms.hihealth.HuaweiHiHealth
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import java.util.*

class StartWorkoutActivity : AppCompatActivity() {

    private val startWorkoutViewModel by lazy { ViewModelProvider(
        this, InjectorUtil.getStartWorkoutModelFactory()
    ).get(StartWorkoutViewModel::class.java) }

    private val binding by lazy { DataBindingUtil.setContentView<ActivityStartWorkoutBinding>(
        this,
        R.layout.activity_start_workout
    ) }

    private lateinit var backBtn: ImageButton
    private lateinit var saveBtn: ImageButton

    private lateinit var activityNameEditText: AppCompatEditText
    private lateinit var typeEditText: AppCompatEditText
    private lateinit var descriptionEditText: AppCompatEditText

    // ActivityRecordsController for managing activity records
    private var activityRecordsController: ActivityRecordsController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Analytics(applicationContext).setScreenForAnalytics(
            ScreenType.ACTIVITY.screenType,
            StartWorkoutActivity::class.simpleName.toString()
        )

        binding.viewModel = startWorkoutViewModel
        binding.lifecycleOwner = this

        val hiHealthOptions = HiHealthOptions.builder().build()
        val signInHuaweiId = HuaweiIdAuthManager.getExtendedAuthResult(hiHealthOptions)

        activityRecordsController = HuaweiHiHealth.getActivityRecordsController(this, signInHuaweiId)

        startWorkoutViewModel.isAddedSuccessfully.observe(this, {
            if (it){
                val intent = Intent()
                setResult(Activity.RESULT_OK, intent)
                finish()
            }else{
                Toast.makeText(this, getString(R.string.add_workout_fail_start), Toast.LENGTH_SHORT).show()
            }
        })

        initView()

    }

    private fun initView(){

        backBtn = findViewById(R.id.backBtn)
        saveBtn = findViewById(R.id.saveBtn)

        activityNameEditText = findViewById(R.id.activityNameEditText)
        typeEditText = findViewById(R.id.typeEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)

        typeEditText.setOnClickListener {
            showChooseActivityTypeDialog()
        }

        backBtn.setOnClickListener {
            finish()
        }

        saveBtn.setOnClickListener {

            val activityName = activityNameEditText.text.toString()
            val activityType = typeEditText.text.toString()
            val description = descriptionEditText.text.toString()

            val isValid = validateAddWorkoutInputs(activityName, activityType)

            if (isValid){

                val startTime = Calendar.getInstance().timeInMillis

                val activityBuilder: ActivityBuilder = ActivityBuilder.Builder()
                    .setActivityId(getRandomString(10))
                    .setActivityName(activityName)
                    .setActivityType(activityType)
                    .setActivityDescription(description)
                    .setStartTime(startTime)
                    .build()

                startWorkoutViewModel.beginActivityRecord(applicationContext, activityBuilder, activityRecordsController!!)
            }
        }

        hideKeyboardWhenFocusGoes()
    }

    private fun validateAddWorkoutInputs(activityName: String, activityType: String): Boolean {

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

        return isValid
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

        descriptionEditText.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
