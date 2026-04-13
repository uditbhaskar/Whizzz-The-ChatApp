# Whizzz — The Chat App

Realtime **one-to-one chat** on Android: **Firebase** (Auth, Realtime Database, FCM), **Jetpack Compose**, **multi-module** layout, **Clean Architecture**, and **MVI**-style screens.

## Demo

https://github.com/user-attachments/assets/955f8ac9-82ba-45d2-8358-3c069e561abd





## Features

- **Auth** — Sign in, sign up, forgot password; **Lottie** on login / register.
- **Home** — Tabs: **Chats** (paged list), **Users** (search + directory), **Profile** (your account).
- **Chat** — 1:1 threads, composer, errors / retry, read receipts / seen handling.
- **Profiles** — Edit name & bio, **crop** profile photo, **Coil** images, fullscreen viewer (share / save where supported).
- **Peer profile** — Open the other user from chat.
- **Push** — **FCM** for incoming messages; token registration when signed in.
- **Presence** — Online / offline via **process lifecycle** + Realtime Database; **onDisconnect** handling in data layer.
- **App Check** — Debug vs release (Play Integrity) wired for Firebase.

## Architecture

| Module | Role |
|--------|------|
| **`:app`** | `MainActivity`, **Navigation Compose**, Koin, splash, FCM service, process-wide presence. |
| **`:domain`** | Use cases, repository interfaces, models — pure Kotlin, no Android/Firebase. |
| **`:data`** | Firebase Auth, RTDB, Messaging; Koin `DataModule`; Coroutines + Play Services. |
| **`:core:common`** | Shared helpers (routes, errors, flows). |
| **`:core:strings`** | `WhizzzStrings`. |
| **`:core:ui`** | Theme, avatars, profile UI, photo overlay, insets. |
| **`:feature:auth`** | Login / register / forgot. |
| **`:feature:home`** | Shell, chats & users lists (embeds profile tab). |
| **`:feature:chat`** | Conversation + peer profile. |
| **`:feature:profile`** | Profile editing, **image cropper**. |

**UI:** `UiState` / `UiEvent`, `StateFlow`, `collectAsStateWithLifecycle`, `koinViewModel()`.

## Tech stack

**Architecture & structure**

- **Clean Architecture** — domain isolated from Android/Firebase; data implements ports; features consume use cases.
- **Multi-module Gradle** — `app`, `domain`, `data`, `core/*`, `feature/*` for clear boundaries and faster incremental builds.
- **MVI-style UI** — single direction: **UiState** + **UiEvent**, **ViewModels**, predictable screen updates.
- **Unidirectional data** — **Kotlin Flow** / **StateFlow**, **`collectAsStateWithLifecycle`** for lifecycle-safe collection.

**UI**

- **Jetpack Compose** — declarative UI, **Material 3**, animations, previews where it helps.
- **Navigation Compose** — type-safe routes, transitions between auth / home / chat / profile.
- **Single-activity** — Compose-hosted navigation; splash & fullscreen overlays where needed.
- **Coil** — image loading; **Lottie** — auth motion; **image cropper** for profile photos.

**Platform & async**

- **Kotlin** — coroutines, structured concurrency, extension-heavy style.
- **Process lifecycle** — app-wide foreground/background hooks for presence (not fragile per-screen hacks).
- **Firebase** — Authentication, Realtime Database, Cloud Messaging, **App Check** (debug vs release integrity).

**DI & tooling**

- **Koin** — lightweight service locator / DI for Android + Compose.
- **Gradle Kotlin DSL** + version catalog — dependency pins live in **`gradle/libs.versions.toml`** if you need exact versions.

## Setup

1. Clone the repo.
2. In [Firebase Console](https://console.firebase.google.com/), add an Android app (**`com.example.whizzz`** or your `applicationId`), download **`google-services.json`** → **`app/google-services.json`** (local only; not in git).
3. Enable **Authentication** (email/password), **Realtime Database**, **Cloud Messaging**; configure **App Check** as needed.

## Build

```bash
./gradlew :app:assembleDebug
```

Release uses R8 — keep **`proguard-rules.pro`** aligned with Firebase.

## Notes

- Changing RTDB shape or rules may require a fresh install / clear data.
- Tighten Database / Storage rules before production.

## APK

Need a prebuilt APK? Open an issue on the repo.

## License

See **`LICENSE`**.

---

**Local config:** Each machine needs its own `google-services.json` and `local.properties`. Anything secret or IDE-specific belongs in **`.gitignore`** — don’t `git add -f` those files.
