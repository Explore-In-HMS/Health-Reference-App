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

package com.hms.referenceapp.healthylifeapp.ui.main

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hms.referenceapp.healthylifeapp.PROFILE_FRAGMENT
import com.hms.referenceapp.healthylifeapp.R
import com.hms.referenceapp.healthylifeapp.STEP_FRAGMENT
import com.hms.referenceapp.healthylifeapp.WORKOUT_LIST_FRAGMENT
import com.hms.referenceapp.healthylifeapp.analytics.Analytics
import com.hms.referenceapp.healthylifeapp.databinding.ActivityMainBinding
import com.hms.referenceapp.healthylifeapp.service.AwarenessKitReceiver
import com.hms.referenceapp.healthylifeapp.ui.main.profile.ProfileFragment
import com.hms.referenceapp.healthylifeapp.ui.main.step.StepFragment
import com.hms.referenceapp.healthylifeapp.ui.main.workout.WorkoutListFragment
import com.hms.referenceapp.healthylifeapp.util.InjectorUtil
import com.hms.referenceapp.healthylifeapp.util.ScreenType
import java.util.*

class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 940

    @RequiresApi(Build.VERSION_CODES.Q)
    private val mPermissionsOnHigherVersion = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        Manifest.permission.ACTIVITY_RECOGNITION
    )

    private val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 940

    private val mPermissionsOnLowerVersion = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        "com.huawei.hms.permission.ACTIVITY_RECOGNITION"
    )
    private val viewModel by lazy {
        ViewModelProvider(
            this, InjectorUtil.getMainModelFactory()
        ).get(MainViewModel::class.java)
    }

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(
            this,
            R.layout.activity_main
        )
    }

    private val fm: FragmentManager = supportFragmentManager

    private lateinit var workoutListFragment: Fragment
    private lateinit var stepFragment: Fragment
    private lateinit var profileFragment: Fragment

    private lateinit var active: Fragment
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Analytics(applicationContext).setScreenForAnalytics(
            ScreenType.ACTIVITY.screenType,
            MainActivity::class.simpleName.toString()
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        sharedPreferences =
            this.getSharedPreferences("com.hms.referenceapp.healthylifeapp", Context.MODE_PRIVATE)
        var counter = sharedPreferences?.getInt("counter", 0)
        checkAndRequestPermissions()
        if (counter == 0) {
            AwarenessKitReceiver().scheduleAlarms(this)
            sharedPreferences?.edit()!!.putInt("counter", 1)
                .apply()
        }

        val toolbar: Toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        workoutListFragment = WorkoutListFragment()
        stepFragment = StepFragment()
        profileFragment = ProfileFragment()

        active = workoutListFragment

        val navigation = findViewById<View>(R.id.navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        fm.beginTransaction().add(R.id.main_container, profileFragment, PROFILE_FRAGMENT).hide(
            profileFragment
        ).commit()
        fm.beginTransaction().add(R.id.main_container, stepFragment, STEP_FRAGMENT).hide(
            stepFragment
        ).commit()
        fm.beginTransaction().add(R.id.main_container, workoutListFragment, WORKOUT_LIST_FRAGMENT)
            .commit()
    }

    private val mOnNavigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener =
        object : BottomNavigationView.OnNavigationItemSelectedListener {

            override fun onNavigationItemSelected(@NonNull item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.navigation_activities -> {
                        fm.beginTransaction().hide(active).show(workoutListFragment).commit()
                        active = workoutListFragment
                        return true
                    }
                    R.id.navigation_step -> {
                        fm.beginTransaction().hide(active).show(stepFragment).commit()
                        active = stepFragment
                        return true
                    }
                    R.id.navigation_profile -> {
                        fm.beginTransaction().hide(active).show(profileFragment).commit()
                        active = profileFragment
                        return true
                    }
                }
                return false
            }
        }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        applicationContext, getString(R.string.permissions_granted),
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
        }

    }
    private fun checkAndRequestPermissions() {
        val permissionsDoNotGrant: MutableList<String> = ArrayList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            for (permission in mPermissionsOnHigherVersion) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        permission
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionsDoNotGrant.add(permission)
                }
            }
        } else {
            for (permission in mPermissionsOnLowerVersion) {
                if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionsDoNotGrant.add(permission)
                }
            }
        }
        if (permissionsDoNotGrant.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsDoNotGrant.toTypedArray(), PERMISSION_REQUEST_CODE
            )
        }
    }
}