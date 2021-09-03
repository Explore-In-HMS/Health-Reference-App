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

data class ActivityBuilder(
    val activityId: String,
    val activityType: String,
    val activityName: String,
    val activityDescription: String,
    val startTime: Long,
    val endTime: Long,
    val distance: Int
) {

    class Builder {

        private lateinit var activityId: String
        private lateinit var activityType: String
        private lateinit var activityName: String
        private lateinit var activityDescription: String
        private var startTime: Long = 0L
        private var endTime: Long = 0L
        private var distance: Int = 0

        fun setActivityId(activityId: String) = apply {
            this.activityId = activityId
        }

        fun setActivityType(activityType: String) = apply {
            this.activityType = activityType
        }

        fun setActivityName(activityName: String) = apply {
            this.activityName = activityName
        }

        fun setActivityDescription(activityDescription: String) = apply {
            this.activityDescription = activityDescription
        }

        fun setStartTime(startTime: Long) = apply {
            this.startTime = startTime
        }

        fun setEndTime(endTime: Long) = apply {
            this.endTime = endTime
        }

        fun setDistance(distance: Int) = apply {
            this.distance = distance
        }

        fun build() = ActivityBuilder(
            activityId,
            activityType,
            activityName,
            activityDescription,
            startTime,
            endTime,
            distance
        )
    }
}