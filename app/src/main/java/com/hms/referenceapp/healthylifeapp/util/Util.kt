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

import com.huawei.hms.hihealth.HiHealthActivities
import kotlin.random.Random

val RANDOM_STRING : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

fun getRandomString(length: Int) : String {
    return (1..length)
        .map { Random.nextInt(0, RANDOM_STRING.size) }
        .map(RANDOM_STRING::get)
        .joinToString("")
}

fun getTimeDifference(startDate: Long, endDate: Long): String{

    var different: Long = endDate - startDate

    val secondsInMilli: Long = 1000
    val minutesInMilli = secondsInMilli * 60
    val hoursInMilli = minutesInMilli * 60
    val daysInMilli = hoursInMilli * 24

    val elapsedDays = different / daysInMilli
    different %= daysInMilli

    val elapsedHours = different / hoursInMilli
    different %= hoursInMilli

    val elapsedMinutes = different / minutesInMilli
    different %= minutesInMilli

    val elapsedSeconds = different / secondsInMilli

    val builder = StringBuilder()

    if (elapsedDays != 0L){
        builder.append("$elapsedDays d ")
    }

    if (elapsedHours != 0L){
        builder.append("$elapsedHours h ")
    }

    if (elapsedMinutes != 0L){
        builder.append("$elapsedMinutes m ")
    }

    if (elapsedSeconds != 0L){
        builder.append("$elapsedSeconds s ")
    }

    return builder.toString()
}

val listActivityTypes = arrayOf(
    HiHealthActivities.AEROBICS,
    HiHealthActivities.ARCHERY,
    HiHealthActivities.BADMINTON,
    HiHealthActivities.BASEBALL,
    HiHealthActivities.BASKETBALL,
    HiHealthActivities.BIATHLON,
    HiHealthActivities.BOXING,
    HiHealthActivities.CALISTHENICS,
    HiHealthActivities.CIRCUIT_TRAINING,
    HiHealthActivities.CRICKET,
    HiHealthActivities.CROSSFIT,
    HiHealthActivities.CURLING,
    HiHealthActivities.CYCLING,
    HiHealthActivities.CYCLING_INDOOR,
    HiHealthActivities.DANCING,
    HiHealthActivities.DIVING,
    HiHealthActivities.ELLIPTICAL,
    HiHealthActivities.ERGOMETER,
    HiHealthActivities.ESCALATOR,
    HiHealthActivities.FENCING,
    HiHealthActivities.FOOTBALL_AMERICAN,
    HiHealthActivities.FOOTBALL_AUSTRALIAN,
    HiHealthActivities.FOOTBALL_SOCCER,
    HiHealthActivities.FLYING_DISC,
    HiHealthActivities.GARDENING,
    HiHealthActivities.GOLF,
    HiHealthActivities.GYMNASTICS,
    HiHealthActivities.HANDBALL,
    HiHealthActivities.HIIT,
    HiHealthActivities.HIKING,
    HiHealthActivities.HOCKEY,
    HiHealthActivities.HORSE_RIDING,
    HiHealthActivities.HOUSEWORK,
    HiHealthActivities.ICE_SKATING,
    HiHealthActivities.INTERVAL_TRAINING,
    HiHealthActivities.JUMPING_ROPE,
    HiHealthActivities.KAYAKING,
    HiHealthActivities.KETTLEBELL_TRAINING,
    HiHealthActivities.KICKBOXING,
    HiHealthActivities.KITESURFING,
    HiHealthActivities.MARTIAL_ARTS,
    HiHealthActivities.MEDITATION,
    HiHealthActivities.MIXED_MARTIAL_ARTS,
    HiHealthActivities.OTHER,
    HiHealthActivities.P90X,
    HiHealthActivities.PARAGLIDING,
    HiHealthActivities.PILATES,
    HiHealthActivities.POLO,
    HiHealthActivities.RACQUETBALL,
    HiHealthActivities.ROCK_CLIMBING,
    HiHealthActivities.ROWING,
    HiHealthActivities.ROWING_MACHINE,
    HiHealthActivities.RUGBY,
    HiHealthActivities.RUNNING,
    HiHealthActivities.RUNNING_MACHINE,
    HiHealthActivities.SAILING,
    HiHealthActivities.SCUBA_DIVING,
    HiHealthActivities.SCOOTER_RIDING,
    HiHealthActivities.SKATEBOARDING,
    HiHealthActivities.SKATING,
    HiHealthActivities.SKIING,
    HiHealthActivities.SLEDDING,
    HiHealthActivities.SNOWBOARDING,
    HiHealthActivities.SNOWMOBILE,
    HiHealthActivities.SNOWSHOEING,
    HiHealthActivities.SOFTBALL,
    HiHealthActivities.SQUASH,
    HiHealthActivities.STAIR_CLIMBING,
    HiHealthActivities.STAIR_CLIMBING_MACHINE,
    HiHealthActivities.STANDUP_PADDLEBOARDING,
    HiHealthActivities.STILL,
    HiHealthActivities.STRENGTH_TRAINING,
    HiHealthActivities.SURFING,
    HiHealthActivities.SWIMMING,
    HiHealthActivities.SWIMMING_POOL,
    HiHealthActivities.SWIMMING_OPEN_WATER,
    HiHealthActivities.TABLE_TENNIS,
    HiHealthActivities.TEAM_SPORTS,
    HiHealthActivities.TENNIS,
    HiHealthActivities.TILTING,
    HiHealthActivities.VOLLEYBALL,
    HiHealthActivities.WAKEBOARDING,
    HiHealthActivities.WALKING,
    HiHealthActivities.WATER_POLO,
    HiHealthActivities.WEIGHTLIFTING,
    HiHealthActivities.WHEELCHAIR,
    HiHealthActivities.WINDSURFING,
    HiHealthActivities.YOGA,
    HiHealthActivities.ZUMBA,
    HiHealthActivities.DARTS,
    HiHealthActivities.BILLIARDS,
    HiHealthActivities.BOWLING,
    HiHealthActivities.GROUP_CALISTHENICS,
    HiHealthActivities.TUG_OF_WAR,
    HiHealthActivities.BEACH_SOCCER,
    HiHealthActivities.BEACH_VOLLEYBALL,
    HiHealthActivities.GATEBALL,
    HiHealthActivities.SEPAKTAKRAW,
    HiHealthActivities.DODGE_BALL,
    HiHealthActivities.TREADMILL,
    HiHealthActivities.SPINNING,
    HiHealthActivities.STROLL_MACHINE,
    HiHealthActivities.CROSS_FIT,
    HiHealthActivities.FUNCTIONAL_TRAINING,
    HiHealthActivities.PHYSICAL_TRAINING,
    HiHealthActivities.BELLY_DANCE,
    HiHealthActivities.JAZZ,
    HiHealthActivities.LATIN,
    HiHealthActivities.BALLET,
    HiHealthActivities.CORE_TRAINING,
    HiHealthActivities.HORIZONTAL_BAR,
    HiHealthActivities.PARALLEL_BARS,
    HiHealthActivities.HIP_HOP,
    HiHealthActivities.SQUARE_DANCE,
    HiHealthActivities.HU_LA_HOOP,
    HiHealthActivities.BMX,
    HiHealthActivities.ORIENTEERING,
    HiHealthActivities.INDOOR_WALK,
    HiHealthActivities.INDOOR_RUNNING,
    HiHealthActivities.MOUNTIN_CLIMBING,
    HiHealthActivities.CROSS_COUNTRY_RACE,
    HiHealthActivities.ROLLER_SKATING,
    HiHealthActivities.HUNTING,
    HiHealthActivities.FLY_A_KITE,
    HiHealthActivities.SWING,
    HiHealthActivities.OBSTACLE_RACE,
    HiHealthActivities.BUNGEE_JUMPING,
    HiHealthActivities.PARKOUR,
    HiHealthActivities.PARACHUTE,
    HiHealthActivities.RACING_CAR,
    HiHealthActivities.TRIATHLONS,
    HiHealthActivities.ICE_HOCKEY,
    HiHealthActivities.CROSSCOUNTRY_SKIING,
    HiHealthActivities.SLED,
    HiHealthActivities.FISHING,
    HiHealthActivities.DRIFTING,
    HiHealthActivities.DRAGON_BOAT,
    HiHealthActivities.MOTORBOAT,
    HiHealthActivities.SUP,
    HiHealthActivities.FREE_SPARRING,
    HiHealthActivities.KARATE,
    HiHealthActivities.BODY_COMBAT,
    HiHealthActivities.KENDO,
    HiHealthActivities.TAI_CHI
)