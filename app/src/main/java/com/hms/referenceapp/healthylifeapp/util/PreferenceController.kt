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

package com.hms.referenceapp.healthylifeapp.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.gson.Gson
import com.huawei.hms.support.hwid.result.AuthHuaweiId
import com.hms.referenceapp.healthylifeapp.PREF_NAME

class PreferencesController(context: Context) {

    val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE)
    private val USER_ACCOUNT = "UserAccount"

    var huaweiAccount : AuthHuaweiId?
        get() {
            val gson = Gson()
            val json: String? = sharedPreferences.getString(USER_ACCOUNT, "")
            return gson.fromJson(json, AuthHuaweiId::class.java)
        }
        set(account) {
            val gson = Gson()
            val json: String = gson.toJson(account)
            sharedPreferences.edit().putString(USER_ACCOUNT,json).apply()
        }

}
