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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.hms.referenceapp.healthylifeapp.*
import com.hms.referenceapp.healthylifeapp.analytics.Analytics
import com.hms.referenceapp.healthylifeapp.databinding.ActivityWorkoutDetailBinding
import com.hms.referenceapp.healthylifeapp.util.InjectorUtil
import com.hms.referenceapp.healthylifeapp.util.ScreenType
import com.hms.referenceapp.healthylifeapp.util.WorkoutState
import com.hms.referenceapp.healthylifeapp.util.getTimeDifference
import com.huawei.hms.hihealth.ActivityRecordsController
import com.huawei.hms.hihealth.DataController
import com.huawei.hms.hihealth.HiHealthOptions
import com.huawei.hms.hihealth.HuaweiHiHealth
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


class WorkoutDetailActivity : AppCompatActivity() {

    private val workoutDetailViewModel by lazy { ViewModelProvider(
        this, InjectorUtil.getWorkoutDetailModelFactory()
    ).get(WorkoutDetailViewModel::class.java) }

    private val binding by lazy { DataBindingUtil.setContentView<ActivityWorkoutDetailBinding>(
        this,
        R.layout.activity_workout_detail
    ) }

    private lateinit var backBtn: ImageButton
    private lateinit var finishBtn: AppCompatButton
    private lateinit var deleteBtn: AppCompatButton

    private lateinit var workoutNameTv: TextView
    private lateinit var calorieTv: TextView
    private lateinit var durationTv: TextView
    private lateinit var distanceTv: TextView
    private lateinit var speedTv: TextView
    private lateinit var paceTv: TextView

    private var workoutId: String = ""
    private var workoutName: String = ""
    private var workoutType: String = ""
    private var workoutDescription: String = ""
    private var workoutState: String = ""
    private var workoutDuration: Long = 0L
    private var workoutDistance: Int = 0
    private var workoutSpeed: Double = 0.0
    private var workoutPace: Double = 0.0
    private var workoutCalorie: Int = 0
    private var startTime: Long = 0L
    private var endTime: Long = 0L

    // ActivityRecordsController for managing activity records
    private var activityRecordsController: ActivityRecordsController? = null

    // DataController for deleting activity records
    private var dataController: DataController? = null

    private var customHandler: Handler = Handler()
    private var alertDialog: AlertDialog? = null
    private var dialogBuilder: AlertDialog.Builder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Analytics(applicationContext).setScreenForAnalytics(
            ScreenType.ACTIVITY.screenType,
            WorkoutDetailActivity::class.simpleName.toString()
        )

        binding.viewModel = workoutDetailViewModel
        binding.lifecycleOwner = this

        val hiHealthOptions = HiHealthOptions.builder().build()
        val signInHuaweiId = HuaweiIdAuthManager.getExtendedAuthResult(hiHealthOptions)

        dataController = HuaweiHiHealth.getDataController(this, signInHuaweiId)
        activityRecordsController = HuaweiHiHealth.getActivityRecordsController(
            this,
            signInHuaweiId
        )

        workoutId = intent.getStringExtra(WORKOUT_ID)!!
        workoutName = intent.getStringExtra(WORKOUT_NAME)!!
        workoutType = intent.getStringExtra(WORKOUT_TYPE)!!
        workoutDescription = intent.getStringExtra(WORKOUT_DESCRIPTION)!!
        workoutState = intent.getStringExtra(WORKOUT_STATE)!!
        workoutDuration = intent.getLongExtra(WORKOUT_DURATION, 0L)
        workoutDistance = intent.getIntExtra(WORKOUT_DISTANCE, 0)
        workoutSpeed = intent.getDoubleExtra(WORKOUT_SPEED, 0.0)
        workoutPace = intent.getDoubleExtra(WORKOUT_PACE, 0.0)
        workoutCalorie = intent.getIntExtra(WORKOUT_CALORIE, 0)
        startTime = intent.getLongExtra(WORKOUT_START_TIME, 0L)
        endTime = intent.getLongExtra(WORKOUT_END_TIME, 0L)

        workoutDetailViewModel.isAddedSuccessfully.observe(this, {
            if (it) {
                val intent = Intent()
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.stopped_fail),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        workoutDetailViewModel.isProgress.observe(this, {

            if (it){
                showProgressDialog()
            }else{
                hideProgressDialog()
            }

        })

        initView()
    }

    private fun initView(){

        backBtn = findViewById(R.id.backBtn)
        finishBtn = findViewById(R.id.finishBtn)
        deleteBtn = findViewById(R.id.deleteBtn)

        workoutNameTv = findViewById(R.id.workoutNameTv)
        calorieTv = findViewById(R.id.calorieTv)
        durationTv = findViewById(R.id.durationTv)
        distanceTv = findViewById(R.id.distanceTv)
        speedTv = findViewById(R.id.speedTv)
        paceTv = findViewById(R.id.paceTv)

        val formatter: NumberFormat = DecimalFormat("#0.00")

        workoutNameTv.text = workoutType

        distanceTv.text = formatter.format((workoutDistance.toDouble() / 1000)).toString() + " km"
        speedTv.text = formatter.format(workoutSpeed) + " km/h"
        paceTv.text = formatter.format(workoutPace) + " min/km"
        calorieTv.text = "$workoutCalorie kcal"

        if (WorkoutState.FINISHED.state == workoutState){
            finishBtn.visibility = View.GONE
            deleteBtn.visibility = View.VISIBLE
            durationTv.text = getTimeDifference(startTime, endTime)
        }

        if (WorkoutState.STARTED.state == workoutState){
            finishBtn.visibility = View.VISIBLE
            deleteBtn.visibility = View.GONE
            start()
        }

        backBtn.setOnClickListener {
            finish()
        }

        finishBtn.setOnClickListener {

            workoutDetailViewModel.endActivityRecord(applicationContext, workoutId, activityRecordsController!!)
        }

        deleteBtn.setOnClickListener{
            workoutDetailViewModel.deleteWorkoutRecord(applicationContext, workoutId, dataController!!, activityRecordsController!!)
        }

        hideKeyboardWhenFocusGoes()

    }

    override fun onStop() {
        super.onStop()
        stop()
    }

    fun start() {
        customHandler.postDelayed(updateTimerThread, 0)
    }

    fun stop() {
        customHandler.removeCallbacks(updateTimerThread)
    }

    private val updateTimerThread: Runnable = object : Runnable {
        override fun run() {
            val timeInMilliseconds = Date().time
            durationTv.text = getTimeDifference(startTime, timeInMilliseconds)
            customHandler.postDelayed(this, 1000)
        }
    }

    private fun hideKeyboardWhenFocusGoes() {
        workoutNameTv.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showProgressDialog() {
        dialogBuilder = AlertDialog.Builder(this)
        val inflater: LayoutInflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView: View = inflater.inflate(R.layout.progress_loading_dialog_layout, null)
        dialogBuilder!!.setView(dialogView)
        dialogBuilder!!.setCancelable(false)
        alertDialog = dialogBuilder!!.create()
        alertDialog?.show()
    }

    private fun hideProgressDialog() {
        alertDialog?.dismiss()
    }
}
