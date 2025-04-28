# Android On-Chain Test Wallet

A sample Android application demonstrating a simple crypto wallet dashboard. It displays a list of cryptocurrency balances held by the user, along with their approximate value in USD, based on locally stored data.

## Overview

This project showcases modern Android development practices, including:

*   **Clean Architecture:** Separation of concerns into Data, Domain, and Presentation layers.
*   **MVI (Model-View-Intent):** A unidirectional data flow pattern implemented in the presentation layer for predictable state management.
*   **Dependency Injection:** Using Hilt for managing dependencies throughout the app.
*   **Asynchronous Programming:** Leveraging Kotlin Coroutines and Flow for handling background tasks and data streams.
*   **Local Data:** Reading data (currencies, rates, balances) from JSON files stored in the `assets` folder.
*   **Unit Testing:** Demonstrating unit tests for ViewModels, UseCases, Repositories, and Mappers using JUnit, MockK, and Turbine.

## Architecture

The project follows a layered architecture:

*   **Data Layer:** Responsible for fetching raw data (from local JSON assets in this case) and mapping it to domain models. Includes `DataSource`, `Repository` implementations, and `DTOs`.
*   **Domain Layer:** Contains the core business logic, independent of other layers. Includes `UseCases`, `Repository` interfaces, and `Domain Models`.
*   **Presentation Layer:** Handles the UI and user interactions. Uses MVI pattern with `ViewModel`, `UiState`, `Events`, and `Effects`. Includes `Activities`/`Fragments` and `Adapters`.

## Tech Stack

*   **Language:** [Kotlin](https://kotlinlang.org/)
*   **Architecture:** Clean Architecture, MVI
*   **Asynchronous:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)
*   **Dependency Injection:** [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
*   **Serialization:** [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)
*   **UI:** Android SDK, View Binding, RecyclerView, Material Design Components
*   **Image Loading:** [Coil](https://coil-kt.github.io/coil/) (Added, usage might vary)
*   **Testing:**
    *   [JUnit 4](https://junit.org/junit4/)
    *   [MockK](https://mockk.io/) (Mocking)
    *   [Turbine](https://github.com/cashapp/turbine) (Flow testing)
    *   `kotlinx-coroutines-test`
    *   `androidx.arch.core:core-testing`

## Setup

1.  Clone the repository:
    ```bash
    git clone <repository-url>
    cd android-onchain-test
    ```
2.  Open the project in Android Studio (latest stable version recommended).
3.  Allow Android Studio to sync Gradle dependencies.

## Building and Running

*   **Android Studio:**
    1.  Select the `app` configuration from the run configurations dropdown.
    2.  Choose a target device (emulator or physical device).
    3.  Click the "Run" button (▶️).
*   **Gradle:**
    ```bash
    # Build and install the debug APK
    ./gradlew installDebug
    ```

## Running Tests

*   **Unit Tests:** Run all unit tests located in `app/src/test`.
    ```bash
    ./gradlew testDebugUnitTest
    ```
*   **Instrumented Tests:** Run Android instrumented tests located in `app/src/androidTest` (if any).
    ```bash
    ./gradlew connectedDebugAndroidTest
    ```

## Project Structure

```
android-onchain-test/
├── app/
│   ├── build.gradle.kts         # App-level Gradle configuration
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/cryptocom/wallet/
│   │   │   │   ├── App.kt               # Application class (for Hilt)
│   │   │   │   ├── data/                # Data Layer
│   │   │   │   │   ├── datasource/      # Data sources (local/remote)
│   │   │   │   │   ├── mapper/          # DTO <-> Domain mappers
│   │   │   │   │   ├── model/           # Data Transfer Objects (DTOs)
│   │   │   │   │   └── repository/      # Repository implementations
│   │   │   │   ├── di/                  # Hilt Dependency Injection modules
│   │   │   │   ├── domain/              # Domain Layer
│   │   │   │   │   ├── common/          # Common utilities (e.g., Result wrapper)
│   │   │   │   │   ├── model/           # Domain models
│   │   │   │   │   ├── repository/      # Repository interfaces
│   │   │   │   │   └── usecase/         # Business logic use cases
│   │   │   │   └── presentation/        # Presentation Layer
│   │   │   │       └── dashboard/       # Dashboard feature module (Activity, VM, etc.)
│   │   │   │           ├── model/       # MVI State/Event/Effect
│   │   │   │           └── ...
│   │   │   ├── res/                   # Android resources (layouts, drawables, etc.)
│   │   │   └── assets/                # Local JSON data files
│   │   │       ├── balances.json
│   │   │       ├── currencies.json
│   │   │       └── rates.json
│   │   ├── test/                    # Unit tests
│   │   └── androidTest/             # Instrumented tests
│   └── ...
├── build.gradle.kts             # Project-level Gradle configuration
├── settings.gradle.kts          # Gradle settings
└── README.md                    # This file
```

## TODO / Future Improvements

*   Implement error handling display in the UI (e.g., show a snackbar/message when data loading fails).
*   Add pull-to-refresh functionality.
*   Replace local JSON data source with a remote API call.
*   Implement data caching.
*   Add UI tests (Espresso).
*   Add more sophisticated sorting/filtering options.
