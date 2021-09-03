# Healthy Life App

![Latest Version](https://img.shields.io/badge/latestVersion-1.0-yellow) ![Kotlin](https://img.shields.io/badge/language-kotlin-blue) ![Minimum SDK Version](https://img.shields.io/badge/minSDK-26-orange) ![Android Gradle Version](https://img.shields.io/badge/androidGradleVersion-4.0.1-green) ![Gradle Version](https://img.shields.io/badge/gradleVersion-6.5-informational)

<table>
  <tbody>
    <tr>
      <td><img src="https://github.com/Explore-In-HMS/Health-Reference-App/blob/master/art/Login.png"></td>
      <td><img src="https://github.com/Explore-In-HMS/Health-Reference-App/blob/master/art/Add_Workout.png"></td>
      <td><img src="https://github.com/Explore-In-HMS/Health-Reference-App/blob/master/art/Activities.png"></td>
      <td><img src="https://github.com/Explore-In-HMS/Health-Reference-App/blob/master/art/Step_Tracker.png"></td>
    </tr>
  </tbody>
 </table>

# Introduction

Healthy Life is a reference application for Hms Kits to phones running with the android- based Hms Service.
Healthy Life App is provides the facility to create and monitor the physical activities necessary for a healthy life.
Whether you walk or run, hike or bike, play indoor or outdoor sports, you can add and track the various physical exercises
and activities in a single app using the various trackers. It watches your daily steps and keeps your weekly
and monthly records. It is developed with Kotlin.

Project architecture is MVVM.

# HUAWEI Health Kit

HUAWEI Health Kit (Health Kit for short) allows ecosystem apps to access fitness and health data of users based on their HUAWEI ID and authorization. For consumers, Health Kit provides a mechanism for fitness and health data storage and sharing based on flexible authorization. For developers and partners, Health Kit provides a data platform and fitness and health open capabilities, so that they can build related apps and services based on a multitude of data types. Health Kit connects the hardware devices and ecosystem apps to provide consumers with health care, workout guidance, and ultimate service experience.

# Awareness Kit

HUAWEI Awareness Kit provides your app with the ability to obtain contextual information including users' current time, location, behavior, audio device status, ambient light, weather, and nearby beacons. Your app can gain insight into a user's current situation more efficiently, making it possible to deliver a smarter, more considerate user experience.

# Account Kit

Account Kit provides you with simple, secure, and quick sign-in and authorization functions. Instead of entering accounts and passwords and waiting for authentication, users can just tap the Sign in with HUAWEI ID button to quickly and securely sign in to your app with their HUAWEI IDs.

# Auth Service

Auth Service provides SDKs and backend services, supports multiple authentication modes, and provides a powerful management console, enabling you to easily develop and manage user authentication.

# Analytics Kit

HUAWEI Analytics Kit offers a rich array of preset analytics models that help you gain an in-depth insight into your users, products, and content. With this insight, you can then take a data-driven approach to make informed decisions for product and marketing optimizations.

# Crash Service

The AppGallery Connect Crash service provides a powerful yet lightweight solution to app crash problems. With the service, you can quickly detect, locate, and resolve app crashes (unexpected exits of apps), and have access to highly readable crash reports in real time, without the need to write any code.

# What You Will Need

**Hardware Requirements**
- A computer that can run Android Studio.
- A Huawei phone for debugging.

**Software Requirements**
- Android SDK package
- Android Studio 3.X-4.X
- HMS Core (APK) 4.X or later

## Getting Started

Healthy Life App uses HUAWEI services. In order to use them, you have to [create an app](https://developer.huawei.com/consumer/en/doc/distribution/app/agc-create_app) first. Before getting started, please [sign-up](https://id1.cloud.huawei.com/CAS/portal/userRegister/regbyemail.html?service=https%3A%2F%2Foauth-login1.cloud.huawei.com%2Foauth2%2Fv2%2Flogin%3Faccess_type%3Doffline%26client_id%3D6099200%26display%3Dpage%26flowID%3D6d751ab7-28c0-403c-a7a8-6fc07681a45d%26h%3D1603370512.3540%26lang%3Den-us%26redirect_uri%3Dhttps%253A%252F%252Fdeveloper.huawei.com%252Fconsumer%252Fen%252Flogin%252Fhtml%252FhandleLogin.html%26response_type%3Dcode%26scope%3Dopenid%2Bhttps%253A%252F%252Fwww.huawei.com%252Fauth%252Faccount%252Fcountry%2Bhttps%253A%252F%252Fwww.huawei.com%252Fauth%252Faccount%252Fbase.profile%26v%3D9f7b3af3ae56ae58c5cb23a5c1ff5af7d91720cea9a897be58cff23593e8c1ed&loginUrl=https%3A%2F%2Fid1.cloud.huawei.com%3A443%2FCAS%2Fportal%2FloginAuth.html&clientID=6099200&lang=en-us&display=page&loginChannel=89000060&reqClientType=89) for a HUAWEI developer account.

After creating the application, you need to [generate a signing certificate fingerprint](https://developer.huawei.com/consumer/en/codelab/HMSPreparation/index.html#3). Then you have to set this fingerprint to the application you created in AppGallery Connect.
- Go to "My Projects" in AppGallery Connect.
- Find your project from the project list and click the app on the project card.
- On the Project Setting page, set SHA-256 certificate fingerprint to the SHA-256 fingerprint you've generated.
![AGC-Fingerprint](https://communityfile-drcn.op.hicloud.com/FileServer/getFile/cmtyPub/011/111/111/0000000000011111111.20200511174103.08977471998788006824067329965155:50510612082412:2800:6930AD86F3F5AF6B2740EF666A56165E65A37E64FA305A30C5EFB998DA38D409.png?needInitFileName=true?needInitFileName=true?needInitFileName=true?needInitFileName=true)

# Using the application

- Before you run the app, make sure that you have a working internet connection since the application uses Huawei Mobile Services. Otherwise, the app will not be able to perform the security checks only after which you can use the app.

When the user first enter the application, the login screen opens, the login process with the account kit is made, after the health kit permissions are made and the necessary permissions are obtained, the home screen is switched to. There are 3 tabs on the main screen. The first tab can display your activities. You can filter your activities yearly, monthly and weekly in the upper right corner. The user can view activity records on the application and created from official Health app as a list. The user can add, start, end or delete activities through the application. Health Kit used for this use case. In the second tab,the user can see the step data and calorie created by the health application as a list up to the last 3 months . At the top, the user can view your step with your daily goal in the progress bar in card view. The third tab is the profile part. Here your information is displayed. Our application sends you notification according to your inactivity. We can detect the inactivity of the users thanks to the behavior feature of the Awareness Kit. This notification is sent every 30 minutes by default. If you want, you can change the duration of this notification. We have a logout button on the top right. You must click here to log out of the application.

## Screenshots

<img src="https://github.com/Explore-In-HMS/Health-Reference-App/blob/master/art/Login.png"></img>
<img src="https://github.com/Explore-In-HMS/Health-Reference-App/blob/master/art/Add_Workout.png"></img>
<img src="https://github.com/Explore-In-HMS/Health-Reference-App/blob/master/art/Activities.png"></img>
<img src="https://github.com/Explore-In-HMS/Health-Reference-App/blob/master/art/Filter.png"></img>
<img src="https://github.com/Explore-In-HMS/Health-Reference-App/blob/master/art/Step_Tracker.png"></img>
<img src="https://github.com/Explore-In-HMS/Health-Reference-App/blob/master/art/Profile.png"></img>

## Project Structure

Healthy Life App is designed with MVVM design pattern.

## Libaries
 - Health Kit
 - Awareness Kit
 - Account Kit
 - Auth Service
 - Analytics Kit
 - Crash Service
 - Retrofit
 - Gson
 - Material Components
 - Cardview
 - Fab
 - Circular Progress Bar
 - Glide

## Contributors
 - Çağnur Hacımahmutoğlu
 - Tuğrul Altun

