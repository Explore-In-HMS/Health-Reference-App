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

package com.hms.referenceapp.healthylifeapp.ui.login

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.AGConnectAuthCredential
import com.huawei.hmf.tasks.Task
import com.huawei.hms.hihealth.SettingController

class LoginViewModel : ViewModel() {

    private val HEALTH_APP_SETTING_DATA_SHARE_HEALTHKIT_ACTIVITY_SCHEME = "huaweischeme://healthapp/achievement?module=kit"
    private val APP_HEALTH_NOT_INSTALLED = "50033"

    val isLoginSuccessfully = MutableLiveData<Boolean>()
    val isShowProgressDialog = MutableLiveData<Boolean>()
    val showMessage = MutableLiveData<String>()

    fun  signIn(credential: AGConnectAuthCredential, mSettingController: SettingController, packageManager: PackageManager){

        setIsShowProgressDialog(true)

        AGConnectAuth.getInstance().signIn(credential)
            .addOnSuccessListener {   // onSuccess

                setIsShowProgressDialog(false)
                checkOrAuthorizeHealth(mSettingController, packageManager)

            }.addOnFailureListener {
                setIsShowProgressDialog(false)
                checkOrAuthorizeHealth(mSettingController, packageManager)
            }
    }

    private fun checkOrAuthorizeHealth(mSettingController: SettingController, packageManager: PackageManager) {
        val authTask = mSettingController.healthAppAuthorization

        authTask.addOnSuccessListener { result ->
            // If HUAWEI Health is authorized, build success View.
            if (true == result) {
                setIsLoginSuccessfully(true)
            } else {
                // If not, start HUAWEI Health authorization Activity by schema with User-defined requestCode.
                val healthKitSchemaUri: Uri = Uri.parse(HEALTH_APP_SETTING_DATA_SHARE_HEALTHKIT_ACTIVITY_SCHEME)
                val intent = Intent(Intent.ACTION_VIEW, healthKitSchemaUri)
                // Before start, Determine whether the HUAWEI health authorization Activity can be opened.
                if (intent.resolveActivity(packageManager) != null) {
                    setIsLoginSuccessfully(false)
                } else {
                    setShowMessage(APP_HEALTH_NOT_INSTALLED)
                }
            }
        }.addOnFailureListener { exception ->
            if (exception != null) {
                setShowMessage(exception.message.toString())
            }
        }
    }

    fun queryHealthAuthorization(mSettingController: SettingController) {
        // 1. Build a PopupWindow as progress dialog for time-consuming operation
        // 2. Calling SettingController to query HUAWEI Health authorization status.
        // This method is asynchronous, so need to build a listener for result.
        val queryTask: Task<Boolean> = mSettingController.healthAppAuthorization

        queryTask.addOnSuccessListener{
            // Show authorization result in view.
            if (true == it) {
                setIsLoginSuccessfully(true)
            }
        }

        queryTask.addOnFailureListener {
            if (it != null) {
                setShowMessage(it.message.toString())
            }
        }
    }

    private fun setIsLoginSuccessfully(item: Boolean) {
        isLoginSuccessfully.value = item
    }

    private fun setIsShowProgressDialog(item: Boolean) {
        isShowProgressDialog.value = item
    }

    private fun setShowMessage(item: String) {
        showMessage.value = item
    }
}