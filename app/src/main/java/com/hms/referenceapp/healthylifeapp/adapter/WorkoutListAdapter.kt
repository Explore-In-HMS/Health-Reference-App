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

package com.hms.referenceapp.healthylifeapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hms.referenceapp.healthylifeapp.R
import com.hms.referenceapp.healthylifeapp.data.model.Workout
import com.hms.referenceapp.healthylifeapp.util.WorkoutState
import com.hms.referenceapp.healthylifeapp.util.getTimeDifference
import com.huawei.hms.hihealth.HiHealthActivities
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat

class WorkoutListAdapter(private val workoutClickListener: WorkoutClickListener) : RecyclerView.Adapter<WorkoutListAdapter.ModelViewHolder>() {

    private lateinit var workoutList: MutableList<Workout>

    class ModelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val workoutType: ImageView = view.findViewById(R.id.workoutType)
        private val activityType: TextView = view.findViewById(R.id.activityType)
        private val date: TextView = view.findViewById(R.id.date)
        private val calorieAmount: TextView = view.findViewById(R.id.calorieAmount)
        private val length: TextView = view.findViewById(R.id.length)
        private val duration: TextView = view.findViewById(R.id.duration)
        private val startedWorkoutInfo: TextView = view.findViewById(R.id.startedWorkoutInfo)

        private val formatter: NumberFormat = DecimalFormat("#0.00")

        fun bindItems(item: Workout) {

            when (item.activityType) {
                HiHealthActivities.RUNNING -> {
                    workoutType.setBackgroundResource(R.drawable.ic_run_grey600_18dp)
                }
                HiHealthActivities.WALKING -> {
                    workoutType.setBackgroundResource(R.drawable.ic_walk_grey600_18dp)
                }
                HiHealthActivities.CYCLING -> {
                    workoutType.setBackgroundResource(R.drawable.ic_bike_grey600_18dp)
                }
                HiHealthActivities.HIKING -> {
                    workoutType.setBackgroundResource(R.drawable.ic_hiking_grey600_18dp)
                }
                HiHealthActivities.BASKETBALL -> {
                    workoutType.setBackgroundResource(R.drawable.ic_basketball_grey600_18dp)
                }
                HiHealthActivities.CROSSFIT -> {
                    workoutType.setBackgroundResource(R.drawable.ic_weight_lifter_grey600_18dp)
                }
                HiHealthActivities.KITESURFING -> {
                    workoutType.setBackgroundResource(R.drawable.ic_weather_windy_grey600_18dp)
                }
            }

            activityType.text = item.activityType
            calorieAmount.text = item.calorie.toString() + " kcal"
            date.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(item.startTime)

            if (item.length == 0){
                length.text =  "0 km"
            }else{
                length.text =  formatter.format((item.length.toDouble() / 1000)).toString() + " km"
            }

            if (WorkoutState.FINISHED.state == item.workoutState || item.endTime == 0L){

                duration.visibility = View.VISIBLE
                startedWorkoutInfo.visibility = View.GONE
                duration.text = getTimeDifference(item.startTime, item.endTime)
            }

            if (WorkoutState.STARTED.state == item.workoutState || item.endTime == 0L){
                duration.visibility = View.GONE
                startedWorkoutInfo.visibility = View.VISIBLE
            }
        }
    }

    fun setWorkoutList(workoutList: MutableList<Workout>) {
        this.workoutList = workoutList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.workout_item, parent, false)
        return ModelViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (this::workoutList.isInitialized){
            return workoutList.size
        }
        return 0
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        if (this::workoutList.isInitialized){
            holder.bindItems(workoutList[position])

            holder.itemView.setOnClickListener {
                workoutClickListener.onWorkoutClickListener(workoutList[position])
            }
        }
    }

    interface WorkoutClickListener {
        fun onWorkoutClickListener(data: Workout)
    }
}