# GeoLandmarks

GeoLandmarks is an Android app for discovering, adding, and visiting geo-tagged landmarks in Bangladesh. It combines a local cache with a REST API so landmark and visit data can remain available when the device is offline.

## Tech stack

- Kotlin and Android SDK
- MVVM, ViewModel, LiveData, and View Binding
- Room for local storage
- Retrofit, Gson, and OkHttp for REST API requests
- WorkManager for background synchronization and visit processing
- OpenStreetMap via OSMDroid, with Google Maps dependencies also present
- Gradle with Kotlin DSL

## Features

- Map and list views of landmarks, with score-based map markers
- Filter the list by minimum score and sort by score
- Create landmarks with a title, score, GPS coordinates, and uploaded image
- Record visits using the device location and display the returned distance
- Background polling for asynchronous visit jobs
- Local Room cache and queued visit synchronization when connectivity returns
- Visit activity history and server-side soft-delete/restore actions

## API and configuration

The app calls the course REST service configured in `RetrofitClient.kt`. The Android manifest also declares a Google Maps API key resource; provide a valid key in your local Android configuration if using the Google Maps integration. Do not commit private API credentials.

## Run locally

1. Clone the repository:

   ```bash
   git clone https://github.com/nafus08/GeoLandmarks.git
   ```
2. Open the project root in Android Studio and sync Gradle.
3. Configure any required Maps credentials locally.
4. Run the `app` configuration on an Android device or emulator. Grant location and media permissions when prompted to use the corresponding features.

The project supports Android 7.0 (API 24) and later.
