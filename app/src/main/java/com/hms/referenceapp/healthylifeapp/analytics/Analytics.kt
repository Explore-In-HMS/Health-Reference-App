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

package com.hms.referenceapp.healthylifeapp.analytics

import android.content.Context
import android.os.Bundle
import com.hms.referenceapp.healthylifeapp.SCREEN_NAME
import com.huawei.hms.analytics.HiAnalytics
import com.huawei.hms.analytics.HiAnalyticsInstance
import com.huawei.hms.analytics.HiAnalyticsTools
import com.huawei.hms.analytics.type.HAEventType

class Analytics(context: Context){

    private var hiAnalyticsInstance: HiAnalyticsInstance? = null

    init {
        HiAnalyticsTools.enableLog()
        hiAnalyticsInstance = HiAnalytics.getInstance(context)
        hiAnalyticsInstance?.setAnalyticsEnabled(true)
    }

    fun setScreenForAnalytics(type: String, screenName: String){
        val bundle = Bundle()
        bundle.putString(type, screenName)
        logEvent(SCREEN_NAME, bundle)
    }

    fun logInForAnalytics(bundle: Bundle?){
        logEvent(HAEventType.SIGNIN, bundle)
    }

    fun logEvent(eventName : String, eventKeyName : String, eventKeyValue: String){
        val args = Bundle()
        args.putString(eventKeyName, eventKeyValue)
        hiAnalyticsInstance?.onEvent(eventName, args)
    }

    fun logEvent(eventName: String, bundle: Bundle?){
        hiAnalyticsInstance?.onEvent(eventName, bundle)
    }
}
