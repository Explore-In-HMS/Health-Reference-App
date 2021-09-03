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

import com.hms.referenceapp.healthylifeapp.ui.splash.SplashModelFactory
import com.hms.referenceapp.healthylifeapp.ui.addworkout.AddWorkoutModelFactory
import com.hms.referenceapp.healthylifeapp.ui.login.LoginModelFactory
import com.hms.referenceapp.healthylifeapp.ui.main.MainModelFactory
import com.hms.referenceapp.healthylifeapp.ui.main.profile.ProfileModelFactory
import com.hms.referenceapp.healthylifeapp.ui.main.step.StepModelFactory
import com.hms.referenceapp.healthylifeapp.ui.main.workout.WorkoutListModelFactory
import com.hms.referenceapp.healthylifeapp.ui.startworkout.StartWorkoutModelFactory
import com.hms.referenceapp.healthylifeapp.ui.workoutdetail.WorkoutDetailModelFactory

object InjectorUtil {

    fun getWorkoutModelFactory() = WorkoutListModelFactory()

    fun getStepModelFactory() = StepModelFactory()

    fun getProfileModelFactory() = ProfileModelFactory()

    fun getAddWorkoutModelFactory() = AddWorkoutModelFactory()

    fun getWorkoutDetailModelFactory() = WorkoutDetailModelFactory()

    fun getStartWorkoutModelFactory() = StartWorkoutModelFactory()

    fun getSplashModelFactory() = SplashModelFactory()

    fun getMainModelFactory() = MainModelFactory()

    fun getLoginModelFactory() = LoginModelFactory()

}