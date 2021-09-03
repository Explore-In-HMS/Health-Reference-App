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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import com.hms.referenceapp.healthylifeapp.R
import com.hms.referenceapp.healthylifeapp.STEP_PROGRESS_MAX
import com.hms.referenceapp.healthylifeapp.adapter.StepAdapter
import com.hms.referenceapp.healthylifeapp.analytics.Analytics
import com.hms.referenceapp.healthylifeapp.databinding.FragmentStepBinding
import com.hms.referenceapp.healthylifeapp.util.InjectorUtil
import com.hms.referenceapp.healthylifeapp.util.ScreenType
import com.huawei.hms.hihealth.DataController
import com.huawei.hms.hihealth.HiHealthOptions
import com.huawei.hms.hihealth.HuaweiHiHealth
import com.huawei.hms.hihealth.data.DataType
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import java.text.SimpleDateFormat
import java.util.*


class StepFragment : Fragment() {

    private lateinit var stepViewModel: StepViewModel
    private lateinit var progressCircular: CircularProgressBar
    private var dataController: DataController? = null
    internal var status = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentStepBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_step,
            container,
            false
        )

        Analytics(context!!).setScreenForAnalytics(
            ScreenType.FRAGMENT.screenType,
            StepFragment::class.simpleName.toString()
        )

        stepViewModel = ViewModelProvider(this, InjectorUtil.getStepModelFactory()).get(StepViewModel::class.java)
        binding.viewModel = stepViewModel
        binding.lifecycleOwner = this

        stepViewModel.init(StepAdapter())
        initDataController()

        dataController?.let { stepViewModel.setDataController(it) }

        var count  = 0
        var today  = Calendar.getInstance()
        var nextDay = Calendar.getInstance()
        nextDay.add(Calendar.DATE, -20)
        var dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

        println("Today : " + dateFormat.format(Date(today.timeInMillis)))

        stepViewModel.readToday()
        stepViewModel.readDaily(dateFormat.format(Date(nextDay.timeInMillis)).toInt(), dateFormat.format(Date(today.timeInMillis)).toInt()-1)


        binding.stepList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!binding.stepList.canScrollVertically(1) && count<5) {
                    count++
                    var otherDay = Calendar.getInstance()
                    var nextDay = Calendar.getInstance()

                    otherDay.add(Calendar.DATE, -(20*count))
                    nextDay.add(Calendar.DATE, -((20*(count + 1)-1)))

                    stepViewModel.readDaily(dateFormat.format(Date(nextDay.timeInMillis)).toInt(), dateFormat.format(Date(otherDay.timeInMillis)).toInt())
                }
            }
        })

        val view = binding.root
        initView(view)
        return view
    }
    private fun initView(view: View) {
        progressCircular = view.findViewById(R.id.progress_circular)
        progressCircular.progressMax = STEP_PROGRESS_MAX.toFloat()

        stepViewModel.todayStepDataInt.observe(viewLifecycleOwner, {

            if (it != null){
                progressCircular.progress = it.toFloat()
            }
        })
    }

    private fun initDataController() {
        val hiHealthOptions = HiHealthOptions.builder()
            .addDataType(DataType.DT_CONTINUOUS_STEPS_DELTA, HiHealthOptions.ACCESS_READ)
            .build()
        val signInHuaweiId =
            HuaweiIdAuthManager.getExtendedAuthResult(hiHealthOptions)
        dataController = HuaweiHiHealth.getDataController(context!!, signInHuaweiId)
    }
}
