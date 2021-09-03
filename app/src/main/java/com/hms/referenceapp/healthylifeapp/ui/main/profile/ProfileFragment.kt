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

package com.hms.referenceapp.healthylifeapp.ui.main.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.hms.referenceapp.healthylifeapp.HealthApplication
import com.hms.referenceapp.healthylifeapp.*

import com.hms.referenceapp.healthylifeapp.R
import com.hms.referenceapp.healthylifeapp.analytics.Analytics
import com.hms.referenceapp.healthylifeapp.databinding.FragmentProfileBinding
import com.hms.referenceapp.healthylifeapp.service.AwarenessKitReceiver
import com.hms.referenceapp.healthylifeapp.service.AwarenessKitScheduledService
import com.hms.referenceapp.healthylifeapp.ui.login.LoginActivity
import com.hms.referenceapp.healthylifeapp.util.InjectorUtil
import com.hms.referenceapp.healthylifeapp.util.ScreenType
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService


class ProfileFragment  : Fragment()  {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var thirtyminute : CheckBox
    private lateinit var onehour : CheckBox
    private lateinit var secondhour : CheckBox
    private lateinit var service: AwarenessKitScheduledService
    private lateinit var name : TextView
    private lateinit var profile :ImageView
    private var mAuthManager: HuaweiIdAuthService? = null
    private var mSharedPreferences: SharedPreferences? = null
    private var mEditor: SharedPreferences.Editor? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val binding: FragmentProfileBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_profile,
            container,
            false
        )

        Analytics(context!!).setScreenForAnalytics(
            ScreenType.FRAGMENT.screenType,
            ProfileFragment::class.simpleName.toString()
        )

        service = AwarenessKitScheduledService()
        setHasOptionsMenu(true)
        profileViewModel = ViewModelProvider(this, InjectorUtil.getProfileModelFactory()).get(ProfileViewModel::class.java)

        binding.viewModel = profileViewModel
        binding.lifecycleOwner = this

        val view = binding.root
        initView(view)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id =item.itemId
        Log.i("TAG", "OK1")
        if (id == R.id.logout)
        {
            Log.i("TAG", "OK")
            val signOutTask = mAuthManager?.signOut()

            AGConnectAuth.getInstance().signOut()

            Log.i("TAG", "signOut Success")
            HealthApplication.preferencesController?.sharedPreferences!!.edit().clear().apply()
            this.requireContext().startActivity(Intent(this.requireContext(), LoginActivity::class.java))
            this.requireActivity().finish()

            signOutTask?.let {

                it.addOnSuccessListener {

                    Log.i("TAG", "signOut fail")
                }.addOnFailureListener {
                    Log.i("TAG", "signOut fail")
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initView(view: View){

        profile = view?.findViewById(R.id.profile_image)
        name = view?.findViewById(R.id.name)
        thirtyminute = view?.findViewById(R.id.ch_30min)!!
        onehour = view?.findViewById(R.id.ch_1hr)!!
        secondhour= view?.findViewById(R.id.ch_2hr)!!

        thirtyminute.isChecked =true

        var selectedCheckBox = "selected"


        mSharedPreferences = requireActivity().getSharedPreferences(PREFERENCES_CHECKBOX, Context.MODE_PRIVATE)
        mSharedPreferences?.apply {
            selectedCheckBox = getString(PREFERENCES_SELECTED_CHECKBOX, EMPTY).toString()
        }

        when(selectedCheckBox) {

            INACTIVITY_30_MIN -> {
                thirtyminute.isChecked = true
                onehour.isChecked = false
                secondhour.isChecked = false
            }
            INACTIVITY_60_MIN -> {
                onehour.isChecked = true
                thirtyminute.isChecked = false
                secondhour.isChecked = false
            }
            INACTIVITY_120_MIN -> {
                secondhour.isChecked = true
                onehour.isChecked = false
                thirtyminute.isChecked = false
            }

        }

        val sharedPreferences =
            this.requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)


          if (HealthApplication.preferencesController!!.huaweiAccount !=null ) {
              name.text = HealthApplication.preferencesController!!.huaweiAccount!!.displayName
              Glide.with(this.requireContext())
                  .load(HealthApplication.preferencesController!!.huaweiAccount!!.avatarUriString)
                  .into(profile)

          }

        thirtyminute.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                sharedPreferences?.edit()!!.putInt(NOTIFICATION_COUNTER, 0).apply()
                sharedPreferences?.edit()!!.putInt(PERIOD_COUNTER, 3)
                mSharedPreferences = requireActivity().getSharedPreferences(PREFERENCES_CHECKBOX, Context.MODE_PRIVATE)
                mEditor = mSharedPreferences?.edit()
                mEditor?.putString(PREFERENCES_SELECTED_CHECKBOX, INACTIVITY_30_MIN)
                mEditor?.apply()
                AwarenessKitReceiver().scheduleAlarms(this.requireContext())
                onehour.isChecked = false
                secondhour.isChecked =false

            } else {
               // Toast.makeText(this.requireContext(),"Unchecked", Toast.LENGTH_SHORT).show()
            }
        }
        onehour.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                sharedPreferences?.edit()!!.putInt(NOTIFICATION_COUNTER, 0).apply()
                sharedPreferences?.edit()!!.putInt(PERIOD_COUNTER, 5)
                mSharedPreferences = requireActivity().getSharedPreferences(PREFERENCES_CHECKBOX, Context.MODE_PRIVATE)
                mEditor = mSharedPreferences?.edit()
                mEditor?.putString(PREFERENCES_SELECTED_CHECKBOX, INACTIVITY_60_MIN)
                mEditor?.apply()
                AwarenessKitReceiver().scheduleAlarms(this.requireContext())
                thirtyminute.isChecked =false
                secondhour.isChecked =false
            } else {
              //  Toast.makeText(this.requireContext(),"Unchecked", Toast.LENGTH_SHORT).show()
            }
        }
        secondhour.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                sharedPreferences?.edit()!!.putInt(NOTIFICATION_COUNTER, 0).apply()
                sharedPreferences?.edit()!!.putInt(PERIOD_COUNTER, 7)
                mSharedPreferences = requireActivity().getSharedPreferences(PREFERENCES_CHECKBOX, Context.MODE_PRIVATE)
                mEditor = mSharedPreferences?.edit()
                mEditor?.putString(PREFERENCES_SELECTED_CHECKBOX, INACTIVITY_120_MIN)
                mEditor?.apply()
                AwarenessKitReceiver().scheduleAlarms(this.requireContext())
                thirtyminute.isChecked =false
                onehour.isChecked =false
            } else {
               // Toast.makeText(this.requireContext(),"Unchecked", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


