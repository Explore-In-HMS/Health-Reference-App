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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.hms.referenceapp.healthylifeapp.*
import com.hms.referenceapp.healthylifeapp.adapter.WorkoutListAdapter
import com.hms.referenceapp.healthylifeapp.analytics.Analytics
import com.hms.referenceapp.healthylifeapp.data.model.Workout
import com.hms.referenceapp.healthylifeapp.databinding.FragmentWorkoutListBinding
import com.hms.referenceapp.healthylifeapp.ui.addworkout.AddWorkoutActivity
import com.hms.referenceapp.healthylifeapp.ui.startworkout.StartWorkoutActivity
import com.hms.referenceapp.healthylifeapp.ui.workoutdetail.WorkoutDetailActivity
import com.hms.referenceapp.healthylifeapp.util.FilterType
import com.hms.referenceapp.healthylifeapp.util.InjectorUtil
import com.hms.referenceapp.healthylifeapp.util.ScreenType
import com.huawei.hms.hihealth.ActivityRecordsController
import com.huawei.hms.hihealth.HiHealthOptions
import com.huawei.hms.hihealth.HuaweiHiHealth
import com.huawei.hms.support.hwid.HuaweiIdAuthManager


class WorkoutListFragment : Fragment(), WorkoutListAdapter.WorkoutClickListener {

    private lateinit var workoutListViewModel: WorkoutListViewModel

    private lateinit var menu: FloatingActionMenu
    private lateinit var rootView: ConstraintLayout
    private lateinit var workoutList: RecyclerView
    private lateinit var recordWorkoutFab: FloatingActionButton
    private lateinit var enterWorkoutRecordFab: FloatingActionButton

    private var activityRecordsController: ActivityRecordsController? = null

    private var alertDialog: AlertDialog? = null
    private var dialogBuilder: AlertDialog.Builder? = null

    private var checkedItemIndex = 1

    companion object {
        const val WORKOUT_DETAIL_ACTIVITY = 123
        const val WORKOUT_START_ACTIVITY = 124
        const val WORKOUT_ADD_ACTIVITY = 125
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Analytics(context!!).setScreenForAnalytics(
            ScreenType.FRAGMENT.screenType,
            WorkoutListFragment::class.simpleName.toString()
        )

        val hiHealthOptions = HiHealthOptions.builder().build()
        val signInHuaweiId = HuaweiIdAuthManager.getExtendedAuthResult(hiHealthOptions)
        activityRecordsController = HuaweiHiHealth.getActivityRecordsController(
            context!!,
            signInHuaweiId
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentWorkoutListBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_workout_list,
            container,
            false
        )

        workoutListViewModel = ViewModelProvider(
            this,
            InjectorUtil.getWorkoutModelFactory()
        ).get(WorkoutListViewModel::class.java)

        binding.viewModel = workoutListViewModel
        binding.lifecycleOwner = this

        workoutListViewModel.init(WorkoutListAdapter(this))
        workoutListViewModel.getActivityRecord(
            activityRecordsController!!,
            FilterType.MONTHLY.filterType
        )

        workoutListViewModel.isProgress.observe(viewLifecycleOwner, Observer {

            if (it) {
                showProgressDialog()
            } else {
                hideProgressDialog()
            }

        })

        setHasOptionsMenu(true)

        val view = binding.root
        initView(view)
        return view
    }

    private fun initView(view: View){

        menu = view.findViewById(R.id.workoutListMenu)
        rootView = view.findViewById(R.id.rootView)
        workoutList = view.findViewById(R.id.workoutList)
        recordWorkoutFab = view.findViewById(R.id.recordWorkoutFab)
        enterWorkoutRecordFab = view.findViewById(R.id.enterWorkoutRecordFab)

        recordWorkoutFab.setOnClickListener {
            val intent = Intent(activity, StartWorkoutActivity::class.java)
            startActivityForResult(intent, WORKOUT_START_ACTIVITY)

            menu.close(true)
        }

        enterWorkoutRecordFab.setOnClickListener {
            val intent = Intent(activity, AddWorkoutActivity::class.java)
            startActivityForResult(intent, WORKOUT_ADD_ACTIVITY)

            menu.close(true)
        }

        rootView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                menu.close(true)
                return true
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_filter, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id =item.itemId
        if (id == R.id.filterMenu) {
            menu.close(true)
            showWorkoutFilterDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showWorkoutFilterDialog() {
        val listItems = arrayOf(
            FilterType.WEEKLY.filterType,
            FilterType.MONTHLY.filterType,
            FilterType.YEARLY.filterType
        )
        val mBuilder = android.app.AlertDialog.Builder(activity)
        mBuilder.setTitle(getString(R.string.choose_filter))
        mBuilder.setSingleChoiceItems(listItems, checkedItemIndex) { dialogInterface, index ->

            workoutListViewModel.isProgressLoaded = false

            val filterType = listItems[index]
            workoutListViewModel.getActivityRecord(activityRecordsController!!, filterType)

            checkedItemIndex = index

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

    override fun onWorkoutClickListener(data: Workout) {

        menu.close(true)

        val intent = Intent(this.requireContext(), WorkoutDetailActivity::class.java)
        intent.putExtra(WORKOUT_ID, data.activityId)
        intent.putExtra(WORKOUT_NAME, data.name)
        intent.putExtra(WORKOUT_TYPE, data.activityType)
        intent.putExtra(WORKOUT_DESCRIPTION, data.description)
        intent.putExtra(WORKOUT_STATE, data.workoutState)
        intent.putExtra(WORKOUT_DURATION, data.duration)
        intent.putExtra(WORKOUT_DISTANCE, data.length)
        intent.putExtra(WORKOUT_SPEED, data.speed)
        intent.putExtra(WORKOUT_PACE, data.pace)
        intent.putExtra(WORKOUT_CALORIE, data.calorie)
        intent.putExtra(WORKOUT_START_TIME, data.startTime)
        intent.putExtra(WORKOUT_END_TIME, data.endTime)
        startActivityForResult(intent, WORKOUT_DETAIL_ACTIVITY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Check which request we're responding to
        if (requestCode == WORKOUT_DETAIL_ACTIVITY) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {

                workoutListViewModel.isProgressLoaded = false
                workoutListViewModel.getActivityRecord(
                    activityRecordsController!!,
                    FilterType.MONTHLY.filterType
                )
            }
        }

        if (requestCode == WORKOUT_START_ACTIVITY) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {

                workoutListViewModel.isProgressLoaded = false
                workoutListViewModel.getActivityRecord(
                    activityRecordsController!!,
                    FilterType.MONTHLY.filterType
                )
            }
        }

        if (requestCode == WORKOUT_ADD_ACTIVITY) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {

                workoutListViewModel.isProgressLoaded = false
                workoutListViewModel.getActivityRecord(
                    activityRecordsController!!,
                    FilterType.MONTHLY.filterType
                )
            }
        }

    }

    private fun showProgressDialog() {
        dialogBuilder = AlertDialog.Builder(this.requireContext())
        val inflater: LayoutInflater = this.requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
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