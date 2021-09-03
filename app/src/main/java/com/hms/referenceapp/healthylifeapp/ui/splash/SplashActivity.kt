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

package com.hms.referenceapp.healthylifeapp.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.hms.referenceapp.healthylifeapp.ui.login.LoginActivity
import com.hms.referenceapp.healthylifeapp.ui.main.MainActivity
import com.hms.referenceapp.healthylifeapp.util.InjectorUtil

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider(this, InjectorUtil.getSplashModelFactory()).get(
            SplashViewModel::class.java)

        if (viewModel.isLoggedIn()) {

            // go to place list screen

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {

            // go to login screen

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
