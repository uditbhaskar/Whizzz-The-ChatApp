# Whizzz — The Chat App

Android app for **real-time one-to-one chat** with Firebase (Authentication, Realtime Database, Cloud Messaging), **Jetpack Compose** UI, and a **multi-module** layout aligned with **MVI and Clean Architecture** (domain vs data vs presentation).

## Screen recording

Walkthrough of the app (splash, auth, home tabs, chat, profile). **Add your recording** as [`docs/screen-recording.mp4`](docs/screen-recording.mp4) and push it so the clip below plays on GitHub. Prefer **H.264** in an **`.mp4`** container for broad support.

<video src="docs/screen-recording.mp4" controls playsinline width="100%">
  Video not supported in this view — open
  <a href="docs/screen-recording.mp4"><code>docs/screen-recording.mp4</code></a>
  in the repo.
</video>

**Hosted file instead:** If the relative URL does not play in the README, upload the video (e.g. GitHub release asset or `user-images.githubusercontent.com` link from an issue comment) and set `src` on the `<video>` tag to that **direct** `.mp4` URL.

## Features

- **Authentication:** Email/password sign-in, registration, password reset; **Lottie** animations on auth screens.
- **Home shell:** Tabbed **Chats** (conversation list with pagination), **Users** (search + paginated directory), **Profile** (own profile tab).
- **Messaging:** 1:1 thread with compose bar, read receipts / seen handling, stream error retry, offline-aware send errors.
- **Profiles:** Edit display name and bio; profile photo via **image cropper**; avatars loaded with **Coil**; fullscreen photo viewer with share / save (where supported).
- **Peer profile:** View other user from chat; same fullscreen photo viewer with safe-area toolbar.
- **Push:** **Firebase Cloud Messaging** for incoming message notifications; FCM token registration from the signed-in session.
- **Presence:** User `status` in Realtime Database; app syncs **online / offline** with **`ProcessLifecycleOwner`** (foreground vs background), with **`onDisconnect`** fallback in the data layer when marking online.
- **Security:** **Firebase App Check** — debug provider in debug builds; Play Integrity flag in release via `BuildConfig` (configure in Firebase Console).

## Architecture

| Layer / module | Role |
|----------------|------|
| **`:app`** | Application shell, `MainActivity`, **Navigation Compose** (`WhizzzNavHost`), **Koin** startup, **splash**, **FCM** service, **process-wide presence** (`AppProcessPresenceEffect`). |
| **`:domain`** | Pure Kotlin/JVM: **use cases**, repository interfaces, models (e.g. `User`, `ChatMessage`). No Android or Firebase APIs. |
| **`:data`** | Firebase **Auth**, **Realtime Database**, **Messaging**; repository implementations; **Koin** `DataModule`; **Kotlin Coroutines** + Play Services coroutines for Firebase calls. |
| **`:core:common`** | Shared utilities (e.g. navigation routes, coroutine helpers). |
| **`:core:strings`** | Centralized user-facing and default strings (`WhizzzStrings`). |
| **`:core:ui`** | Compose theme, shared widgets (avatars, profile rows, photo overlay, keyboard insets). |
| **`:feature:auth`** | Login, register, forgot password (Compose + **ViewModels** + MVI-style contracts). |
| **`:feature:home`** | Home shell, chats list, users list (depends on **`:feature:profile`** for the profile tab). |
| **`:feature:chat`** | Conversation and peer profile screens. |
| **`:feature:profile`** | Profile editing UI; **Vanniktech Android Image Cropper** for picking/cropping photos. |

**UI pattern:** MVI-style **UiState / UiEvent** per screen, **`StateFlow`**, **`collectAsStateWithLifecycle`**, **`koinViewModel()`**.

## Tech stack (versions from `gradle/libs.versions.toml`)

- **Language:** Kotlin **2.0.21**, JVM **17**
- **Android:** `compileSdk` / `targetSdk` **35**, `minSdk` **26**
- **Gradle:** AGP **8.7.3**, Google Services plugin **4.4.2**
- **UI:** Jetpack Compose (BOM **2025.02.00**), Material 3, Material Icons Extended, Navigation Compose **2.8.4**
- **Lifecycle:** **2.8.7** (`lifecycle-runtime-compose`, `lifecycle-viewmodel-compose`, **`lifecycle-process`** for app-wide lifecycle)
- **DI:** **Koin** **3.5.6** (`koin-android`, `koin-androidx-compose`)
- **Async:** Kotlin Coroutines **1.9.0** (core, Android, Play Services)
- **Firebase:** BOM **33.7.0** — **Auth**, **Realtime Database**, **Cloud Messaging**, **App Check** (Play Integrity + debug)
- **Images:** **Coil** **2.7.0** (Compose), **Lottie Compose** **6.6.2**, **Android Image Cropper** **4.7.0**
- **Other:** AndroidX Core KTX, AppCompat, Activity Compose **1.9.3**, Core Splashscreen **1.0.1**

## Setup

1. Create a Firebase project and enable **Authentication** (email/password), **Realtime Database**, and **Cloud Messaging**.
2. Download **`google-services.json`** and place it in the **`app/`** module root (replace the placeholder if present).
3. Register the **App Check** debug token for local builds in the Firebase Console when using the debug provider.
4. For release, configure **Play Integrity** in Firebase and ensure `USE_PLAY_INTEGRITY_APP_CHECK` / ProGuard rules match your release pipeline.
5. If you change **`applicationId`** or package names, update Firebase, `namespace` in Gradle, and manifest-aligned code accordingly.

## Build

```bash
./gradlew :app:assembleDebug
```

Release builds use R8 minification; keep `proguard-rules.pro` in sync with Firebase and reflection usage.

## Important notes

1. **Database schema:** If you change Realtime Database structure, field names, or security rules, reinstall the app or clear app data where needed to avoid inconsistent local assumptions.
2. **Rules:** Start with restrictive Realtime Database rules; avoid wide-open reads/writes outside development.
3. **Storage:** Profile images in this project are handled via **Realtime Database** fields (e.g. URLs or data URIs), not a separate Firebase Storage dependency in Gradle. If you add Storage later, add the SDK, rules, and ProGuard entries explicitly.

## APK

If you need a prebuilt APK, open an issue on the repository.
