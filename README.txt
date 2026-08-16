============================================================
                     GEOLANDMARKS APP
============================================================

Project Overview:
-----------------
GeoLandmarks is an Android application designed to help users discover, manage, and visit geo-tagged landmarks across Bangladesh. The app provides a seamless experience by integrating real-time API data with robust offline capabilities, allowing users to interact with landmarks even without an active internet connection.

Features Implemented:
---------------------
1. Map View:
   - Visualizes all landmarks on an OpenStreetMap centered on Bangladesh.
   - Markers are color-coded based on the landmark's score:
     * Red: Score < 20
     * Orange: 20 <= Score < 50
     * Yellow: 50 <= Score < 80
     * Green: Score >= 80
2. Landmarks List:
   - Displays all active landmarks with their title, score, and image.
   - Supports real-time filtering by minimum score.
   - Supports sorting by score (ascending/descending).
3. Visit Feature:
   - Automatically fetches current GPS location.
   - Implements a two-step asynchronous visit process:
     * Sends a visit request to receive a job_id.
     * Polls the server in the background using WorkManager until the job is 'done'.
   - Displays the calculated distance upon completion.
4. Add Landmark:
   - Allows users to create new landmarks with a title, score, and image.
   - Includes an auto-fetch feature for GPS coordinates.
   - Uses Multipart/form-data for reliable image uploads.
5. Activity History:
   - Tracks all visit attempts, showing their status (pending, done, failed) and calculated distance.
6. Soft Delete Handling:
   - Automatically hides landmarks marked as deleted on the server.

API Usage:
----------
The application communicates with a centralized REST API (api.php) using the following actions:
- get_landmarks: Fetches the list of all available landmarks.
- visit_landmarks: Initiates a visit job for a specific landmark.
- get_job_status: Polls for the completion status and distance of a visit job.
- create_landmark: Uploads a new landmark with metadata and image.
- delete_landmark / restore_landmark: Handles soft deletion and restoration.

Offline Strategy:
-----------------
- Single Source of Truth: The app uses a local Room database to cache all fetched landmarks. The UI always observes the local database.
- Background Synchronization: Visit requests made while offline are saved to the database as 'pending'.
- WorkManager: A periodic SyncWorker and an immediate OneTimeWorkRequest ensure that queued visits are processed as soon as internet connectivity is restored.

Architecture Used:
------------------
- MVVM (Model-View-ViewModel): Separates UI logic from business logic.
- Repository Pattern: Centralizes data access from both local (Room) and remote (Retrofit) sources.
- Jetpack Navigation: Manages the 4-tab bottom navigation flow.
- ViewBinding: Provides safe and efficient access to UI components.

Challenges Faced:
-----------------
- Polling Logic: Orchestrating the background polling for visit job status required careful use of CoroutineWorkers to ensure the UI remained responsive and the job survived app restarts.
- Data Integrity: Handling decimal scores from the API required adjusting the local schema and GSON DTOs to use Double types instead of Integers to avoid parsing crashes.
- Map Rendering: Transitioning between Google Maps and OpenStreetMap to find the most stable solution for displaying markers without authorization issues.
