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

package com.hms.referenceapp.healthylifeapp.data.model

data class Workout(

    var id: Long = 0L,
    var activityId: String = "",
    var name: String = "",
    var description: String = "",
    var activityType: String = "",
    var startTime: Long = 0L,
    var endTime: Long = 0L,
    var duration: Long = 0L,
    var length: Int = 0,
    var speed: Double = 0.0,
    var pace: Double = 0.0,
    var calorie: Int = 0,
    var workoutState: String = ""
)