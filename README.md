# ⚡ GalleryOn App

GalleryOn is a native Android application built in **Kotlin**, designed to showcase a clean, scalable, and modern approach to building a media gallery. The app uses best practices like modular clean architecture, Jetpack Compose for UI, and robust testing strategies.

![Android](https://img.shields.io/badge/Platform-Android-brightgreen?logo=android)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple?logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue?logo=jetpackcompose)
![MVVM](https://img.shields.io/badge/Architecture-MVVM-orange)
---

## 📝 App Overview

1.  **Albums View**
    * Displays albums grouped by folders.
    * Supports switching between **Grid View** and **List View**.
    * Each album shows preview and media count.

2.  **Media Listing**
    * After selecting an album, displays media items (images/videos) within it.
    * Supports basic viewing of media metadata like name, size, and date.

3.  **Media Details**
    * Displays basic metadata of media items:
      * 🖼️ Media
      * 📄 Name
      * 📏 Size
      * 🗓️ Date Taken

4.  **Permission Handling**
    * Requests appropriate storage permissions at runtime based on Android version.

---

## 📸 Screenshots

| Launcher Screen (Day) | Launcher Screen (Night) | Albums Screen (Day) | Albums Screen (Night) | Media List Screen (Day) | Media List Screen (Night) | Media Detail Screen (Day) | Media Detail Screen (Night) |
|:---------------------:|:-----------------------:|:-------------------:|:---------------------:|:-----------------------:|:-------------------------:|:-------------------------:|:---------------------------:|
| <img src="screenshots/launcher_day.png" width="200" height="400"/> | <img src="screenshots/launcher_night.png" width="200" height="400"/> | <img src="screenshots/albums_day.png" width="200" height="400"/> | <img src="screenshots/albums_night.png" width="200" height="400"/> | <img src="screenshots/media_list_day.png" width="200" height="400"/> | <img src="screenshots/media_list_night.png" width="200" height="400"/> | <img src="screenshots/media_detail_day.png" width="200" height="400"/> | <img src="screenshots/media_detail_night.png" width="200" height="400"/> |

> (🖼️ Replace with your actual day/night mode screenshots inside the `/screenshots` folder.)

---


## 🧠 Architecture

🧱 **Clean MVI + Modularized Architecture**  
Maintains strict separation of concerns between layers.

### 📁 Module Structure

| Module       | Description                                              |
|-------------------------|----------------------------------------------------------|
| `app/`                  | Application entry point and DI setup.                   |
| `core/`                 | Common utilities like permissions management.           |
| `domain/`               | Business models and use cases (pure Kotlin).            |
| `data/`                 | Repository implementations, media management.           |
| `feature/albums/`       | Albums screen, view models, UI components.               |
| `feature/media/`        | Media listing screen, view models, UI components.        |

---

## 🚀 Libraries & Tech Stack

### 🧰 Core Technologies

- **Kotlin** — Modern Android language.
- **Jetpack Compose** — Declarative UI toolkit.
- **Coroutines & Flow** — Async & reactive data streams.
- **Navigation (Compose)** — Type-safe screen navigation.
- **Hilt** — Dependency Injection framework.

### 🧪 Testing Libraries

- **JUnit4** — Unit testing framework.
- **MockK** — Mocks/stubs for testing.
- **Turbine** — Test Kotlin Flow emissions.

##### 📊 Code Coverage Example

<img src="screenshots/code_coverage.png" width="800" height="400" />

---

## ✅ Features Implemented

* 📁 Display albums grouped by folder.
* 🔄 Switch between **Grid** and **List** layout.
* Display media items & media item detail.
* 🔐 Handles permission requests dynamically.
* 🧪 Full unit test coverage for ViewModels and Repositories.
* 🧹 Optimized clean code following SOLID principles.

---

## 🧪 Testing Strategy

### 🧷 Unit Tests

- ✅ *ViewModel
- ✅ *Repository
- ✅ *UseCases

All tests utilize `MockK`, `Turbine`, and `Compose Test APIs`.

```bash
./gradlew clean testDebugUnitTest jacocoFullReport
```

## 💡 Thought Process

* Adopted a **Clean MVI** architecture to ensure clear separation of concerns and scalable code structure.
* Used **Jetpack Compose** for modern, declarative, and efficient UI development.
* Applied **Dagger-Hilt** for lightweight and easy-to-manage dependency injection.
* Focused heavily on **testability**, **modularization**, and **best Kotlin practices** for long-term maintainability.

---

## 👨‍💻 Author

**Mirza Arslan**<br>
📧 mirzaarslan450@gmail.com<br>
🔗 [LinkedIn](https://www.linkedin.com/in/mirza-arslan/)

---

## 🧭 Final Thoughts

**GalleryOn** showcases a modern Android media application built with a focus on clean architecture, performance, and best development practices.  
The project emphasizes modularity, test coverage, scalability, and a smooth user experience.  
Feel free to explore it, and suggestions for improvements are always welcome! 🚀



