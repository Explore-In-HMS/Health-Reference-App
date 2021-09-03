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
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hms.referenceapp.healthylifeapp.R
import com.hms.referenceapp.healthylifeapp.data.model.Step
import java.text.SimpleDateFormat
import java.util.*

class StepAdapter : RecyclerView.Adapter<StepAdapter.ModelViewHolder>() {

    private lateinit var stepList: MutableList<Step>

    class ModelViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val date : TextView = view.findViewById(R.id.date)
        private val stepCount : TextView = view.findViewById(R.id.stepcount)
        private val calorie : TextView= view.findViewById(R.id.calorie)
        private val dateFormat = SimpleDateFormat("dd MMMM", Locale.getDefault())

        fun bindItems (item: Step) {

            date.text = dateFormat.format(Date(item.addedTime))
            stepCount.text = item.stepCount.toString() + " " + "steps"
            calorie.text = item.calorie.toString() + " " + "cal"
        }
    }

    fun setStepList(steps: MutableList<Step>) {
        if (this::stepList.isInitialized) {
            stepList.addAll(steps)
        }else {
            stepList = steps
        }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.step_item, parent, false)
        return ModelViewHolder(view)
    }

    override fun getItemCount() : Int {
        if (this::stepList.isInitialized){
            return stepList.size
        }
        return 0
    }

    override fun onBindViewHolder(holder:ModelViewHolder, position: Int) {
        if (this::stepList.isInitialized){

            holder.bindItems(stepList[position])
        }
    }
}