# ScreenTrack-Android
Android App for monitoring app usage. Using MVVM architecture and Hilt for Dependency Injection.

### Download
Get the latest APK from [releases](https://github.com/ashar-7/ScreenTrack-Android/releases)

### Screenshots
<img src="screenshots/home_light.png" width=250/> <img src="screenshots/home_dark.png" width=250/>

<img src="screenshots/app_detail_light.png" width=250/> <img src="screenshots/app_detail_dark.png" width=250/>

<img src="screenshots/timeline_light.png" width=250/> <img src="screenshots/timeline_dark.png" width=250/>

### Libraries used
* [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
* [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
* [Navigation](https://developer.android.com/guide/navigation)
* [Room](https://developer.android.com/training/data-storage/room)
* [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
* [Material Design](https://material.io/)
* [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
* [Material Spinner](https://github.com/jaredrummler/MaterialSpinner)
* [ThreeTenABP](https://github.com/JakeWharton/ThreeTenABP)

#### Data
The data is fetched from [UsageStatsManager](https://developer.android.com/reference/android/app/usage/UsageStatsManager)'s [queryEvents](https://developer.android.com/reference/android/app/usage/UsageStatsManager#queryEvents(long,%20long)) function.

#### Permissions
To use [UsageStatsManager](https://developer.android.com/reference/android/app/usage/UsageStatsManager), the [Manifest.permission.PACKAGE_USAGE_STATS](https://developer.android.com/reference/android/Manifest.permission#PACKAGE_USAGE_STATS) permission is required.

#### Accuracy
The data should probably be accurate on most devices but I did experience a few abnormalities in the
usage events of some devices. For instance, some [ACTIVITY_PAUSE](https://developer.android.com/reference/kotlin/android/app/usage/UsageEvents.Event#ACTIVITY_PAUSED:kotlin.Int) events without their corresponding [ACTIVITY_RESUME](https://developer.android.com/reference/kotlin/android/app/usage/UsageEvents.Event#ACTIVITY_RESUMED:kotlin.Int) events (which the app ignores).

