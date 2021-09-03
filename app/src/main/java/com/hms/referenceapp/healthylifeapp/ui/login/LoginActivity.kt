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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.hms.referenceapp.healthylifeapp.*
import com.hms.referenceapp.healthylifeapp.analytics.Analytics
import com.hms.referenceapp.healthylifeapp.databinding.ActivityLoginBinding
import com.hms.referenceapp.healthylifeapp.ui.main.MainActivity
import com.hms.referenceapp.healthylifeapp.util.InjectorUtil
import com.hms.referenceapp.healthylifeapp.util.ScreenType
import com.huawei.agconnect.auth.HwIdAuthProvider
import com.huawei.hms.hihealth.HiHealthOptions
import com.huawei.hms.hihealth.HuaweiHiHealth
import com.huawei.hms.hihealth.SettingController
import com.huawei.hms.hihealth.data.Scopes
import com.huawei.hms.support.api.entity.auth.Scope
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService
import com.huawei.hms.support.hwid.ui.HuaweiIdAuthButton

class LoginActivity : AppCompatActivity() {

    private val HUAWEIID_SIGNIN = 1001
    private val REQUEST_HEALTH_AUTH = 1003

    private val loginViewModel by lazy { ViewModelProvider(
        this, InjectorUtil.getLoginModelFactory()
    ).get(LoginViewModel::class.java) }

    private val binding by lazy { DataBindingUtil.setContentView<ActivityLoginBinding>(
        this,
        R.layout.activity_login
    ) }

    private lateinit var huaweiIdAuthParamsHelper: HuaweiIdAuthParamsHelper
    private lateinit var authParams: HuaweiIdAuthParams
    private lateinit var authService : HuaweiIdAuthService
    private var mSettingController: SettingController? = null

    private lateinit var loginWithHuaweiId : HuaweiIdAuthButton

    private var alertDialog: AlertDialog? = null
    private var dialogBuilder: AlertDialog.Builder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Analytics(applicationContext).setScreenForAnalytics(
            ScreenType.ACTIVITY.screenType,
            LoginActivity::class.simpleName.toString()
        )

        binding.viewModel = loginViewModel
        binding.lifecycleOwner = this

        initView()
        initHealthService()

        loginViewModel.isLoginSuccessfully.observe(this, {
            if (it){

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

            }else{
                startActivityForResult(intent, REQUEST_HEALTH_AUTH)
            }

        })

        loginViewModel.isShowProgressDialog.observe(this, {
            if (it){
                showProgressDialog()
            }else{
                hideProgressDialog()
            }

        })

        loginViewModel.showMessage.observe(this, {
            if (!it.isNullOrEmpty()){
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun initView(){
        initToolbar()

        loginWithHuaweiId = findViewById(R.id.loginWithHuaweiId)

        loginWithHuaweiId.setOnClickListener(View.OnClickListener {
            signIn()
        })
    }

    private fun initHealthService(){
        val fitnessOptions = HiHealthOptions.builder().build()
        val signInHuaweiId = HuaweiIdAuthManager.getExtendedAuthResult(fitnessOptions)
        mSettingController = HuaweiHiHealth.getSettingController(this, signInHuaweiId)
    }

    private fun initToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    private fun signIn() {

        huaweiIdAuthParamsHelper = HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
        val scopeList = listOf(
            Scope(Scopes.HEALTHKIT_STEP_READ),
            Scope(Scopes.HEALTHKIT_STEP_WRITE),
            Scope(Scopes.HEALTHKIT_ACTIVITY_READ),
            Scope(Scopes.HEALTHKIT_ACTIVITY_WRITE),
            Scope(Scopes.HEALTHKIT_ACTIVITY_RECORD_READ),
            Scope(Scopes.HEALTHKIT_ACTIVITY_RECORD_WRITE)
        )

        huaweiIdAuthParamsHelper.setScopeList(scopeList)
        authParams = huaweiIdAuthParamsHelper.setIdToken().setAccessToken().createParams()
        authService = HuaweiIdAuthManager.getService(this, authParams)

        val signInIntent = authService.signInIntent

        startActivityForResult(signInIntent, HUAWEIID_SIGNIN)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == HUAWEIID_SIGNIN) {


            val authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data)

            if (authHuaweiIdTask.isSuccessful) {

                val huaweiAccount = authHuaweiIdTask.result
                val accessToken = huaweiAccount.accessToken
                val credential = HwIdAuthProvider.credentialWithToken(accessToken)

                val bundle = Bundle()
                bundle.putString(USER_ID_TOKEN, huaweiAccount.idToken )
                bundle.putString(USER_DISPLAY_NAME, huaweiAccount.displayName )
                Analytics(applicationContext).logInForAnalytics(bundle)

                HealthApplication.preferencesController!!.huaweiAccount = huaweiAccount
                loginViewModel.signIn(credential, mSettingController!!, packageManager)
                return
            }

            Toast.makeText(
                this,
                "HwID signIn failed: " + authHuaweiIdTask.exception.message,
                Toast.LENGTH_LONG
            ).show()
        }

        handleHealthAuthResult(requestCode)
    }

    private fun handleHealthAuthResult(requestCode: Int) {
        if (requestCode != REQUEST_HEALTH_AUTH) {
            return
        }
        loginViewModel.queryHealthAuthorization(mSettingController!!)
    }

    private fun showProgressDialog() {
        dialogBuilder = AlertDialog.Builder(this@LoginActivity)
        val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView: View = inflater.inflate(R.layout.progress_dialog_layout, null)
        dialogBuilder!!.setView(dialogView)
        dialogBuilder!!.setCancelable(false)
        alertDialog = dialogBuilder!!.create()
        alertDialog?.show()
    }

    private fun hideProgressDialog() {
        alertDialog?.dismiss()
    }
}
